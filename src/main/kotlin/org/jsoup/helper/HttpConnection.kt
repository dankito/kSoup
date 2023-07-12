package org.jsoup.helper

import org.jsoup.Connection
import org.jsoup.HttpStatusException
import org.jsoup.UncheckedIOException
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.internal.ConstrainableInputStream
import org.jsoup.internal.Normalizer
import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.jsoup.parser.TokenQueue
import java.io.*
import java.net.*
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.IllegalCharsetNameException
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

/**
 * Implementation of [Connection].
 * @see org.jsoup.Jsoup.connect
 */
class HttpConnection : Connection {
    /**
     * Create a new, empty HttpConnection.
     */
    constructor() {
        req = Request()
    }

    /**
     * Create a new Request by deep-copying an existing Request. Note that the data and body of the original are not
     * copied. All other settings (proxy, parser, cookies, etc) are copied.
     * @param copy the request to copy
     */
    internal constructor(copy: Request) {
        req = Request(copy)
    }

    private var req: Request
    private var res: Connection.Response? = null
    
    override fun newRequest(): Connection {
        // copy the prototype request for the different settings, cookie manager, etc
        return HttpConnection(req)
    }

    /** Create a new Connection that just wraps the provided Request and Response  */
    private constructor(req: Request, res: Response) {
        this.req = req
        this.res = res
    }

    override fun url(url: URL): Connection {
        req.url(url)
        return this
    }

    override fun url(url: String): Connection {
        Validate.notEmptyParam(url, "url")
        try {
            req.url(URL(url))
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException(
                String.format(
                    "The supplied URL, '%s', is malformed. Make sure it is an absolute URL, and starts with 'http://' or 'https://'. See https://jsoup.org/cookbook/extracting-data/working-with-urls",
                    url
                ), e
            )
        }
        return this
    }

    override fun proxy(proxy: Proxy?): Connection {
        req.proxy(proxy)
        return this
    }

    override fun proxy(host: String, port: Int): Connection {
        req.proxy(host, port)
        return this
    }

    override fun userAgent(userAgent: String): Connection {
        Validate.notNullParam(userAgent, "userAgent")
        req.header(USER_AGENT, userAgent)
        return this
    }

    override fun timeout(millis: Int): Connection {
        req.timeout(millis)
        return this
    }

    override fun maxBodySize(bytes: Int): Connection {
        req.maxBodySize(bytes)
        return this
    }

    override fun followRedirects(followRedirects: Boolean): Connection {
        req.followRedirects(followRedirects)
        return this
    }

    override fun referrer(referrer: String): Connection {
        Validate.notNullParam(referrer, "referrer")
        req.header("Referer", referrer)
        return this
    }

    override fun method(method: Connection.Method): Connection {
        req.method((method)!!)
        return this
    }

    override fun ignoreHttpErrors(ignoreHttpErrors: Boolean): Connection {
        req.ignoreHttpErrors(ignoreHttpErrors)
        return this
    }

    override fun ignoreContentType(ignoreContentType: Boolean): Connection {
        req.ignoreContentType(ignoreContentType)
        return this
    }

    override fun data(key: String, value: String): Connection {
        req.data(KeyVal.create(key, value))
        return this
    }

    override fun sslSocketFactory(sslSocketFactory: SSLSocketFactory): Connection {
        req.sslSocketFactory(sslSocketFactory)
        return this
    }

    override fun data(key: String, filename: String, inputStream: InputStream): Connection {
        req.data(KeyVal.create(key, filename, inputStream))
        return this
    }

    override fun data(
        key: String,
        filename: String,
        inputStream: InputStream,
        contentType: String
    ): Connection {
        req.data(KeyVal.create(key, filename, inputStream).contentType(contentType))
        return this
    }

    override fun data(data: Map<String, String>): Connection {
        Validate.notNullParam(data, "data")
        for (entry in data.entries) {
            req.data(KeyVal.create(entry.key, entry.value))
        }
        return this
    }

    override fun data(vararg keyvals: String): Connection {
        Validate.notNullParam(keyvals, "keyvals")
        Validate.isTrue(keyvals.size % 2 == 0, "Must supply an even number of key value pairs")
        var i: Int = 0
        while (i < keyvals.size) {
            val key: String? = keyvals.get(i)
            val value: String? = keyvals.get(i + 1)
            Validate.notEmpty(key, "Data key must not be empty")
            Validate.notNull(value, "Data value must not be null")
            req.data(KeyVal.create(key, value))
            i += 2
        }
        return this
    }

    override fun data(data: Collection<Connection.KeyVal>): Connection {
        Validate.notNullParam(data, "data")
        for (entry in data) {
            req.data(entry)
        }
        return this
    }

    override fun data(key: String): Connection.KeyVal? {
        Validate.notEmptyParam(key, "key")
        for (keyVal: Connection.KeyVal? in request().data()) {
            if ((keyVal!!.key() == key)) return keyVal
        }
        return null
    }

    override fun requestBody(body: String): Connection {
        req.requestBody(body)
        return this
    }

    override fun header(name: String, value: String): Connection {
        req.header((name), value)
        return this
    }

    override fun headers(headers: Map<String, String>): Connection {
        Validate.notNullParam(headers, "headers")
        for (entry in headers.entries) {
            req.header((entry.key), entry.value)
        }
        return this
    }

    override fun cookie(name: String, value: String): Connection {
        req.cookie((name), (value))
        return this
    }

    override fun cookies(cookies: Map<String, String>): Connection {
        Validate.notNullParam(cookies, "cookies")
        for (entry: Map.Entry<String, String> in cookies.entries) {
            req.cookie((entry.key), (entry.value))
        }
        return this
    }

    override fun cookieStore(cookieStore: CookieStore): Connection {
        // create a new cookie manager using the new store
        req.cookieManager = CookieManager(cookieStore, null)
        return this
    }

    override fun cookieStore(): CookieStore {
        return req.cookieManager.cookieStore
    }

    override fun parser(parser: Parser): Connection {
        req.parser(parser)
        return this
    }

    @Throws(IOException::class)
    override fun get(): Document {
        req.method(Connection.Method.GET)
        execute()
        Validate.notNull(res)
        return res!!.parse()
    }

    @Throws(IOException::class)
    override fun post(): Document {
        req.method(Connection.Method.POST)
        execute()

        val response = res
        Validate.notNull(response)
        return response.parse()
    }

    @Throws(IOException::class)
    override fun execute(): Connection.Response {
        return Response.execute(req).also {
            res = it
        }
    }

    override fun request(): Connection.Request {
        return req
    }

    override fun request(request: Connection.Request): Connection {
        req =
            request as Request // will throw a class-cast exception if the user has extended some but not all of Connection; that's desired
        return this
    }

    override fun response(): Connection.Response {
        return res ?: throw IllegalArgumentException("You must execute the request before getting a response.")
    }

    override fun response(response: Connection.Response): Connection {
        res = response
        return this
    }

    override fun postDataCharset(charset: String): Connection {
        req.postDataCharset(charset)
        return this
    }

    abstract class Base<T : Connection.Base<T>> : Connection.Base<T> {
        var url = UnsetUrl
        var method: Connection.Method = Connection.Method.GET
        var headers: MutableMap<String, MutableList<String>>
        var cookies: MutableMap<String, String>

        protected constructor() {
            headers = LinkedHashMap()
            cookies = LinkedHashMap()
        }

        protected constructor(copy: Base<T>) {
            url = copy.url // unmodifiable object
            method = copy.method
            headers = LinkedHashMap()
            for (entry: Map.Entry<String, List<String>> in copy.headers.entries) {
                headers.put(entry.key, ArrayList(entry.value))
            }
            cookies = LinkedHashMap()
            cookies.putAll(copy.cookies) // just holds strings
        }

        override fun url(): URL {
            return if (url === UnsetUrl) {
                throw IllegalArgumentException("URL not set. Make sure to call #url(...) before executing the request.")
            } else {
                url
            }
        }

        override fun url(url: URL): T {
            Validate.notNullParam(url, "url")
            this.url = UrlBuilder(url).build()
            return this as T
        }

        override fun method(): Connection.Method {
            return method
        }

        override fun method(method: Connection.Method): T {
            Validate.notNullParam(method, "method")
            this.method = method
            return this as T
        }

        override fun header(name: String): String? {
            Validate.notNullParam(name, "name")
            val vals: List<String> = getHeadersCaseInsensitive(name)
            if (vals.size > 0) {
                // https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
                return StringUtil.join(vals, ", ")
            }
            return null
        }

        override fun addHeader(name: String, value: String?): T {
            Validate.notEmptyParam(name, "name")
            val value = value ?: ""

            var values = headers(name)
            if (values.isEmpty()) {
                values = ArrayList()
                headers.put(name, values)
            }
            values.add(fixHeaderEncoding(value))

            return this as T
        }

        override fun headers(name: String): MutableList<String> {
            Validate.notEmptyParam(name, "name")
            return getHeadersCaseInsensitive(name)
        }

        override fun header(name: String, value: String): T {
            Validate.notEmptyParam(name, "name")
            removeHeader(name) // ensures we don't get an "accept-encoding" and a "Accept-Encoding"
            addHeader(name, value)
            return this as T
        }

        override fun hasHeader(name: String): Boolean {
            Validate.notEmptyParam(name, "name")
            return !getHeadersCaseInsensitive(name).isEmpty()
        }

        /**
         * Test if the request has a header with this value (case insensitive).
         */
        override fun hasHeaderWithValue(name: String, value: String): Boolean {
            Validate.notEmpty(name)
            Validate.notEmpty(value)
            val values: List<String> = headers(name)
            for (candidate: String? in values) {
                if (value.equals(candidate, ignoreCase = true)) return true
            }
            return false
        }

        override fun removeHeader(name: String): T {
            Validate.notEmptyParam(name, "name")
            val entry: Map.Entry<String, List<String>>? = scanHeaders(name) // remove is case-insensitive too
            if (entry != null) headers.remove(entry.key) // ensures correct case
            return this as T
        }

        override fun headers(): Map<String, String> {
            val map: LinkedHashMap<String, String> = LinkedHashMap(headers.size)
            for (entry: Map.Entry<String, List<String>> in headers.entries) {
                val header: String = entry.key
                val values: List<String> = entry.value
                if (values.size > 0) map.put(header, values.get(0))
            }
            return map
        }

        override fun multiHeaders(): Map<String, List<String>> {
            return headers
        }

        private fun getHeadersCaseInsensitive(name: String): MutableList<String> {
            Validate.notNull(name)

            for (entry in headers.entries) {
                if (name.equals(entry.key, ignoreCase = true)) {
                    return entry.value
                }
            }

            return mutableListOf() // emptyList() // TODO: make immutable
        }

        private fun scanHeaders(name: String): Map.Entry<String, List<String>>? {
            val lc = Normalizer.lowerCase(name)
            for (entry: Map.Entry<String, List<String>> in headers.entries) {
                if ((Normalizer.lowerCase(entry.key) == lc)) return entry
            }
            return null
        }

        override fun cookie(name: String): String? {
            Validate.notEmptyParam(name, "name")
            return cookies.get(name)
        }

        override fun cookie(name: String, value: String): T {
            Validate.notEmptyParam(name, "name")
            Validate.notNullParam(value, "value")
            cookies.put(name, value)
            return this as T
        }

        override fun hasCookie(name: String): Boolean {
            Validate.notEmptyParam(name, "name")
            return cookies.containsKey(name)
        }

        override fun removeCookie(name: String): T {
            Validate.notEmptyParam(name, "name")
            cookies.remove(name)
            return this as T
        }

        override fun cookies(): Map<String, String> {
            return cookies
        }

        companion object {

            private val UnsetUrl = URL("http://undefined/") // only used if you created a new Request()

            private fun fixHeaderEncoding(`val`: String): String {
                val bytes: ByteArray = `val`.toByteArray(ISO_8859_1)
                if (!looksLikeUtf8(bytes)) return `val`
                return String(bytes, DataUtil.UTF_8)
            }

            private fun looksLikeUtf8(input: ByteArray): Boolean {
                var i: Int = 0
                // BOM:
                if ((input.size >= 3
                            ) && ((input.get(0).toInt() and 0xFF) == 0xEF
                            ) && ((input.get(1).toInt() and 0xFF) == 0xBB
                            ) && ((input.get(2).toInt() and 0xFF) == 0xBF)
                ) {
                    i = 3
                }
                var end: Int
                val j: Int = input.size
                while (i < j) {
                    var o: Int = input.get(i).toInt()
                    if ((o and 0x80) == 0) {
                        ++i
                        continue  // ASCII
                    }

                    // UTF-8 leading:
                    if ((o and 0xE0) == 0xC0) {
                        end = i + 1
                    } else if ((o and 0xF0) == 0xE0) {
                        end = i + 2
                    } else if ((o and 0xF8) == 0xF0) {
                        end = i + 3
                    } else {
                        return false
                    }
                    if (end >= input.size) return false
                    while (i < end) {
                        i++
                        o = input.get(i).toInt()
                        if ((o and 0xC0) != 0x80) {
                            return false
                        }
                    }
                    ++i
                }
                return true
            }
        }
    }

    class Request : Base<Connection.Request>, Connection.Request {
        private var proxy: Proxy? = null
        private var timeoutMilliseconds: Int
        private var maxBodySizeBytes: Int
        private var followRedirects: Boolean
        private val data: MutableCollection<Connection.KeyVal>
        private var body: String? = null
        private var ignoreHttpErrors: Boolean = false
        private var ignoreContentType: Boolean = false
        private var parser: Parser
        var parserDefined: Boolean = false // called parser(...) vs initialized in ctor
        private var postDataCharset = DataUtil.defaultCharsetName
        private var sslSocketFactory: SSLSocketFactory? = null
        var cookieManager: CookieManager

        @Volatile
        var executing: Boolean = false

        internal constructor() : super() {
            timeoutMilliseconds = 30000 // 30 seconds
            maxBodySizeBytes = 1024 * 1024 * 2 // 2MB
            followRedirects = true
            data = ArrayList()
            method = Connection.Method.GET
            addHeader("Accept-Encoding", "gzip")
            addHeader(USER_AGENT, DEFAULT_UA)
            parser = Parser.htmlParser()
            cookieManager = CookieManager() // creates a default InMemoryCookieStore
        }

        internal constructor(copy: Request) : super(copy) {
            proxy = copy.proxy
            postDataCharset = copy.postDataCharset
            timeoutMilliseconds = copy.timeoutMilliseconds
            maxBodySizeBytes = copy.maxBodySizeBytes
            followRedirects = copy.followRedirects
            data = ArrayList() // data not copied
            //body not copied
            ignoreHttpErrors = copy.ignoreHttpErrors
            ignoreContentType = copy.ignoreContentType
            parser = copy.parser.newInstance() // parsers and their tree-builders maintain state, so need a fresh copy
            parserDefined = copy.parserDefined
            sslSocketFactory = copy.sslSocketFactory // these are all synchronized so safe to share
            cookieManager = copy.cookieManager
            executing = false
        }

        override fun proxy(): Proxy? {
            return proxy
        }

        override fun proxy(proxy: Proxy?): Request {
            this.proxy = proxy
            return this
        }

        override fun proxy(host: String, port: Int): Request {
            proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port))
            return this
        }

        override fun timeout(): Int {
            return timeoutMilliseconds
        }

        override fun timeout(millis: Int): Request {
            Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater")
            timeoutMilliseconds = millis
            return this
        }

        override fun maxBodySize(): Int {
            return maxBodySizeBytes
        }

        override fun maxBodySize(bytes: Int): Connection.Request {
            Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger")
            maxBodySizeBytes = bytes
            return this
        }

        override fun followRedirects(): Boolean {
            return followRedirects
        }

        override fun followRedirects(followRedirects: Boolean): Connection.Request {
            this.followRedirects = followRedirects
            return this
        }

        override fun ignoreHttpErrors(): Boolean {
            return ignoreHttpErrors
        }

        override fun sslSocketFactory(): SSLSocketFactory? {
            return sslSocketFactory
        }

        override fun sslSocketFactory(sslSocketFactory: SSLSocketFactory) {
            this.sslSocketFactory = sslSocketFactory
        }

        override fun ignoreHttpErrors(ignoreHttpErrors: Boolean): Connection.Request {
            this.ignoreHttpErrors = ignoreHttpErrors
            return this
        }

        override fun ignoreContentType(): Boolean {
            return ignoreContentType
        }

        override fun ignoreContentType(ignoreContentType: Boolean): Connection.Request {
            this.ignoreContentType = ignoreContentType
            return this
        }

        override fun data(keyval: Connection.KeyVal): Request {
            Validate.notNullParam(keyval, "keyval")
            data.add(keyval)
            return this
        }

        override fun data(): MutableCollection<Connection.KeyVal> {
            return data
        }

        override fun requestBody(body: String?): Connection.Request {
            this.body = body
            return this
        }

        override fun requestBody(): String? {
            return body
        }

        override fun parser(parser: Parser): Request {
            this.parser = parser
            parserDefined = true
            return this
        }

        override fun parser(): Parser {
            return parser
        }

        override fun postDataCharset(charset: String): Connection.Request {
            Validate.notNullParam(charset, "charset")
            if (!Charset.isSupported(charset)) throw IllegalCharsetNameException(charset)
            postDataCharset = charset
            return this
        }

        override fun postDataCharset(): String {
            return postDataCharset
        }

        fun cookieManager(): CookieManager {
            return cookieManager
        }

        companion object {
            init {
                System.setProperty("sun.net.http.allowRestrictedHeaders", "true")
                // make sure that we can send Sec-Fetch-Site headers etc.
            }
        }
    }

    class Response : Base<Connection.Response>, Connection.Response {
        private val statusCode: Int
        private val statusMessage: String
        private var byteData: ByteBuffer? = null
        private var bodyStream: InputStream? = null
        private var conn: HttpURLConnection? = null
        private var charset: String? = null
        private val contentType: String?
        private var executed: Boolean = false
        private var inputStreamRead: Boolean = false
        private var numRedirects: Int = 0
        private val req: Request

        /**
         * **Internal only! **Creates a dummy HttpConnection.Response, useful for testing. All actual responses
         * are created from the HttpURLConnection and fields defined.
         */
        internal constructor() : super() {
            statusCode = 400
            statusMessage = "Request not made"
            req = Request()
            contentType = null
        }

        override fun statusCode(): Int {
            return statusCode
        }

        override fun statusMessage(): String {
            return statusMessage
        }

        override fun charset(): String? {
            return charset
        }

        override fun charset(charset: String): Response {
            this.charset = charset
            return this
        }

        override fun contentType(): String? {
            return contentType
        }

        @Throws(IOException::class)
        override fun parse(): Document {
            Validate.isTrue(
                executed,
                "Request must be executed (with .execute(), .get(), or .post() before parsing response"
            )
            if (byteData != null) { // bytes have been read in to the buffer, parse that
                bodyStream = ByteArrayInputStream(byteData!!.array())
                inputStreamRead = false // ok to reparse if in bytes
            }
            Validate.isFalse(inputStreamRead, "Input stream already read and parsed, cannot re-read.")
            val doc = DataUtil.parseInputStream(bodyStream, charset, url!!.toExternalForm(), req.parser())
            doc.connection(HttpConnection(req, this)) // because we're static, don't have the connection obj. // todo - maybe hold in the req?
            charset = doc.outputSettings().charset()!!.name() // update charset from meta-equiv, possibly
            inputStreamRead = true
            safeClose()
            return doc
        }

        private fun prepareByteData() {
            Validate.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body")
            if (bodyStream != null && byteData == null) {
                Validate.isFalse(inputStreamRead, "Request has already been read (with .parse())")
                try {
                    byteData = DataUtil.readToByteBuffer(bodyStream, req.maxBodySize())
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                } finally {
                    inputStreamRead = true
                    safeClose()
                }
            }
        }

        override fun body(): String {
            prepareByteData()
            Validate.notNull(byteData)
            // charset gets set from header on execute, and from meta-equiv on parse. parse may not have happened yet
            val body: String = (if (charset == null) DataUtil.UTF_8 else Charset.forName(charset))
                .decode(byteData).toString()
            (byteData as Buffer?)!!.rewind() // cast to avoid covariant return type change in jdk9
            return body
        }

        override fun bodyAsBytes(): ByteArray {
            prepareByteData()
            Validate.notNull(byteData)
            return byteData!!.array()
        }

        override fun bufferUp(): Connection.Response {
            prepareByteData()
            return this
        }

        override fun bodyStream(): BufferedInputStream {
            Validate.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body")
            Validate.isFalse(inputStreamRead, "Request has already been read")
            inputStreamRead = true
            return ConstrainableInputStream.Companion.wrap(bodyStream, DataUtil.bufferSize, req.maxBodySize())
        }

        /**
         * Call on completion of stream read, to close the body (or error) stream. The connection.disconnect allows
         * keep-alives to work (as the underlying connection is actually held open, despite the name).
         */
        private fun safeClose() {
            if (bodyStream != null) {
                try {
                    bodyStream!!.close()
                } catch (e: IOException) {
                    // no-op
                } finally {
                    bodyStream = null
                }
            }
            if (conn != null) {
                conn!!.disconnect()
                conn = null
            }
        }

        // set up url, method, header, cookies
        private constructor(conn: HttpURLConnection, request: Request, previousResponse: Response?) {
            this.conn = conn
            req = request
            method = Connection.Method.valueOf(conn.getRequestMethod())
            url = conn.getURL()
            statusCode = conn.getResponseCode()
            statusMessage = conn.getResponseMessage()
            contentType = conn.getContentType()
            val resHeaders = createHeaderMap(conn)
            processResponseHeaders(resHeaders) // includes cookie key/val read during header scan
            CookieUtil.storeCookies(req, url, resHeaders) // add set cookies to cookie store
            if (previousResponse != null) { // was redirected
                // map previous response cookies into this response cookies() object
                for (prevCookie: Map.Entry<String, String> in previousResponse.cookies().entries) {
                    if (!hasCookie(prevCookie.key)) cookie(prevCookie.key, prevCookie.value)
                }
                previousResponse.safeClose()

                // enforce too many redirects:
                numRedirects = previousResponse.numRedirects + 1
                if (numRedirects >= MAX_REDIRECTS) throw IOException(
                    String.format(
                        "Too many redirects occurred trying to load URL %s",
                        previousResponse.url()
                    )
                )
            }
        }

        fun processResponseHeaders(resHeaders: Map<String, MutableList<String?>>) {
            for (entry in resHeaders.entries) {
                val name = entry.key
                if (name == null) continue  // http/1.1 line
                val values: List<String?> = entry.value
                if (name.equals("Set-Cookie", ignoreCase = true)) {
                    for (value: String? in values) {
                        if (value == null) continue
                        val cd: TokenQueue = TokenQueue(value)
                        val cookieName: String = cd.chompTo("=").trim({ it <= ' ' })
                        val cookieVal: String = cd.consumeTo(";").trim({ it <= ' ' })
                        // ignores path, date, domain, validateTLSCertificates et al. full details will be available in cookiestore if required
                        // name not blank, value not null
                        if (cookieName.length > 0 && !cookies.containsKey(cookieName)) // if duplicates, only keep the first
                            cookie(cookieName, cookieVal)
                    }
                }
                for (value in values) {
                    addHeader(name, value)
                }
            }
        }

        companion object {
            private val MAX_REDIRECTS: Int = 20
            private val LOCATION: String = "Location"

            /*
         * Matches XML content types (like text/xml, application/xhtml+xml;charset=UTF8, etc)
         */
            private val xmlContentTypeRxp: Pattern = Pattern.compile("(application|text)/\\w*\\+?xml.*")
            @JvmOverloads
            @Throws(IOException::class)
            fun execute(req: Request, previousResponse: Response? = null): Response {
                synchronized(req, {
                    Validate.isFalse(
                        req.executing,
                        "Multiple threads were detected trying to execute the same request concurrently. Make sure to use Connection#newRequest() and do not share an executing request between threads."
                    )
                    req.executing = true
                })
                Validate.notNullParam(req, "req")
                val url: URL = (req.url())!!
                Validate.notNull(url, "URL must be specified to connect")
                val protocol = url.getProtocol()
                if (!(protocol == "http") && !(protocol == "https")) throw MalformedURLException("Only http & https protocols supported")
                val methodHasBody: Boolean = req.method().hasBody()
                val hasRequestBody: Boolean = req.requestBody() != null
                if (!methodHasBody) Validate.isFalse(
                    hasRequestBody,
                    "Cannot set a request body for HTTP method " + req.method()
                )

                // set up the request for execution
                var mimeBoundary: String? = null
                if (req.data().size > 0 && (!methodHasBody || hasRequestBody)) serialiseRequestUrl(req) else if (methodHasBody) mimeBoundary =
                    setOutputContentType(req)
                val startTime: Long = System.nanoTime()
                val conn: HttpURLConnection = createConnection(req)
                var res: Response? = null
                try {
                    conn.connect()
                    if (conn.getDoOutput()) {
                        val out: OutputStream = conn.getOutputStream()
                        try {
                            writePost(req, out, mimeBoundary)
                        } catch (e: IOException) {
                            conn.disconnect()
                            throw e
                        } finally {
                            out.close()
                        }
                    }
                    val status: Int = conn.getResponseCode()
                    res = Response(conn, req, previousResponse)

                    // redirect if there's a location header (from 3xx, or 201 etc)
                    if (res.hasHeader(LOCATION) && req.followRedirects()) {
                        if (status != HTTP_TEMP_REDIR) {
                            req.method(Connection.Method.GET) // always redirect with a get. any data param from original req are dropped.
                            req.data().clear()
                            req.requestBody(null)
                            req.removeHeader(CONTENT_TYPE)
                        }
                        var location: String = (res.header(LOCATION))!!
                        Validate.notNull(location)
                        if (location.startsWith("http:/") && location.get(6) != '/') // fix broken Location: http:/temp/AAG_New/en/index.php
                            location = location.substring(6)
                        val redir = StringUtil.resolve((req.url()), location)
                        req.url(redir)
                        req.executing = false
                        return execute(req, res)
                    }
                    if ((status < 200 || status >= 400) && !req.ignoreHttpErrors()) throw HttpStatusException(
                        "HTTP error fetching URL",
                        status,
                        req.url().toString()
                    )

                    // check that we can handle the returned content type; if not, abort before fetching it
                    val contentType: String? = res.contentType()
                    if (((contentType != null
                                ) && !req.ignoreContentType()
                                && !contentType.startsWith("text/")
                                && !xmlContentTypeRxp.matcher(contentType).matches())
                    ) throw UnsupportedMimeTypeException(
                        "Unhandled content type. Must be text/*, application/xml, or application/*+xml",
                        contentType, req.url().toString()
                    )

                    // switch to the XML parser if content type is xml and not parser not explicitly set
                    if (contentType != null && xmlContentTypeRxp.matcher(contentType).matches()) {
                        if (!req.parserDefined) req.parser(Parser.Companion.xmlParser())
                    }
                    res.charset =
                        DataUtil.getCharsetFromContentType(res.contentType) // may be null, readInputStream deals with it
                    if (conn.getContentLength() != 0 && req.method() != Connection.Method.HEAD) { // -1 means unknown, chunked. sun throws an IO exception on 500 response with no content when trying to read body
                        res.bodyStream =
                            if (conn.getErrorStream() != null) conn.getErrorStream() else conn.getInputStream()
                        Validate.notNull(res.bodyStream)
                        if (res.hasHeaderWithValue(CONTENT_ENCODING, "gzip")) {
                            res.bodyStream = GZIPInputStream(res.bodyStream)
                        } else if (res.hasHeaderWithValue(CONTENT_ENCODING, "deflate")) {
                            res.bodyStream = InflaterInputStream(res.bodyStream, Inflater(true))
                        }
                        res.bodyStream = ConstrainableInputStream.Companion.wrap(
                            res.bodyStream,
                            DataUtil.bufferSize,
                            req.maxBodySize()
                        )
                            .timeout(startTime, req.timeout().toLong())
                    } else {
                        res.byteData = DataUtil.emptyByteBuffer()
                    }
                } catch (e: IOException) {
                    if (res != null) res.safeClose() // will be non-null if got to conn
                    throw e
                } finally {
                    req.executing = false
                }
                res!!.executed = true
                return res
            }

            // set up connection defaults, and details from request
            @Throws(IOException::class)
            private fun createConnection(req: Request): HttpURLConnection {
                val proxy: Proxy? = req.proxy()
                val conn: HttpURLConnection = (if (proxy == null) req.url()!!
                    .openConnection() else req.url()!!.openConnection(proxy)) as HttpURLConnection
                conn.setRequestMethod(req.method().name)
                conn.setInstanceFollowRedirects(false) // don't rely on native redirection support
                conn.setConnectTimeout(req.timeout())
                conn.setReadTimeout(req.timeout() / 2) // gets reduced after connection is made and status is read
                if (req.sslSocketFactory() != null && conn is HttpsURLConnection) conn.setSSLSocketFactory(req.sslSocketFactory())
                if (req.method().hasBody()) conn.setDoOutput(true)
                CookieUtil.applyCookiesToRequest(req, conn) // from the Request key/val cookies and the Cookie Store
                for (header: Map.Entry<String, List<String>> in req.multiHeaders().entries) {
                    for (value: String in header.value) {
                        conn.addRequestProperty(header.key, value)
                    }
                }
                return conn
            }

            private fun createHeaderMap(conn: HttpURLConnection): LinkedHashMap<String, MutableList<String?>> {
                // the default sun impl of conn.getHeaderFields() returns header values out of order
                val headers = LinkedHashMap<String, MutableList<String?>>()
                var i: Int = 0
                while (true) {
                    val key: String? = conn.getHeaderFieldKey(i)
                    val `val`: String? = conn.getHeaderField(i)
                    if (key == null && `val` == null) break
                    i++
                    if (key == null || `val` == null) continue  // skip http1.1 line
                    if (headers.containsKey(key)) headers.get(key)!!.add(`val`) else {
                        val vals: ArrayList<String?> = ArrayList()
                        vals.add(`val`)
                        headers.put(key, vals)
                    }
                }
                return headers
            }

            private fun setOutputContentType(req: Connection.Request): String? {
                val contentType: String? = req.header(CONTENT_TYPE)
                var bound: String? = null
                if (contentType != null) {
                    // no-op; don't add content type as already set (e.g. for requestBody())
                    // todo - if content type already set, we could add charset

                    // if user has set content type to multipart/form-data, auto add boundary.
                    if (contentType.contains(MULTIPART_FORM_DATA) && !contentType.contains("boundary")) {
                        bound = DataUtil.mimeBoundary()
                        req.header(CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + bound)
                    }
                } else if (needsMultipart(req)) {
                    bound = DataUtil.mimeBoundary()
                    req.header(CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + bound)
                } else {
                    req.header(CONTENT_TYPE, FORM_URL_ENCODED + "; charset=" + req.postDataCharset())
                }
                return bound
            }

            @Throws(IOException::class)
            private fun writePost(req: Connection.Request, outputStream: OutputStream, boundary: String?) {
                val data: Collection<Connection.KeyVal?>? = req.data()
                val w: BufferedWriter =
                    BufferedWriter(OutputStreamWriter(outputStream, Charset.forName(req.postDataCharset())))
                if (boundary != null) {
                    // boundary will be set if we're in multipart mode
                    for (keyVal: Connection.KeyVal? in data!!) {
                        w.write("--")
                        w.write(boundary)
                        w.write("\r\n")
                        w.write("Content-Disposition: form-data; name=\"")
                        w.write(encodeMimeName(keyVal!!.key())) // encodes " to %22
                        w.write("\"")
                        val input: InputStream? = keyVal.inputStream()
                        if (input != null) {
                            w.write("; filename=\"")
                            w.write(encodeMimeName(keyVal.value()))
                            w.write("\"\r\nContent-Type: ")
                            val contentType: String? = keyVal.contentType()
                            w.write(if (contentType != null) contentType else DefaultUploadType)
                            w.write("\r\n\r\n")
                            w.flush() // flush
                            DataUtil.crossStreams(input, outputStream)
                            outputStream.flush()
                        } else {
                            w.write("\r\n\r\n")
                            w.write(keyVal.value())
                        }
                        w.write("\r\n")
                    }
                    w.write("--")
                    w.write(boundary)
                    w.write("--")
                } else {
                    val body: String? = req.requestBody()
                    if (body != null) {
                        // data will be in query string, we're sending a plaintext body
                        w.write(body)
                    } else {
                        // regular form data (application/x-www-form-urlencoded)
                        var first: Boolean = true
                        for (keyVal: Connection.KeyVal? in data!!) {
                            if (!first) w.append('&') else first = false
                            w.write(URLEncoder.encode(keyVal!!.key(), req.postDataCharset()))
                            w.write('='.code)
                            w.write(URLEncoder.encode(keyVal.value(), req.postDataCharset()))
                        }
                    }
                }
                w.close()
            }

            // for get url reqs, serialise the data map into the url
            @Throws(IOException::class)
            private fun serialiseRequestUrl(req: Connection.Request) {
                val `in` = UrlBuilder(req.url())
                for (keyVal: Connection.KeyVal? in req.data()) {
                    Validate.isFalse(keyVal!!.hasInputStream(), "InputStream data not supported in URL query string.")
                    `in`.appendKeyVal(keyVal)
                }
                req.url(`in`.build())
                req.data().clear() // moved into url as get params
            }
        }
    }

    class KeyVal private constructor(private var key: String, private var value: String) : Connection.KeyVal {

        private var stream: InputStream? = null
        private var contentType: String? = null

        init {
            Validate.notEmptyParam(key, "key")
            Validate.notNullParam(value, "value")
        }

        override fun key(key: String): KeyVal {
            Validate.notEmptyParam(key, "key")
            this.key = key
            return this
        }

        override fun key(): String {
            return key
        }

        override fun value(value: String): KeyVal {
            Validate.notNullParam(value, "value")
            this.value = value
            return this
        }

        override fun value(): String {
            return value
        }

        override fun inputStream(inputStream: InputStream): KeyVal {
            Validate.notNullParam(value, "inputStream")
            stream = inputStream
            return this
        }

        override fun inputStream(): InputStream? {
            return stream
        }

        override fun hasInputStream(): Boolean {
            return stream != null
        }

        override fun contentType(contentType: String): Connection.KeyVal {
            Validate.notEmpty(contentType)
            this.contentType = contentType
            return this
        }

        override fun contentType(): String? {
            return contentType
        }

        override fun toString(): String {
            return key + "=" + value
        }

        companion object {
            @JvmStatic
            fun create(key: String, value: String): KeyVal {
                return KeyVal(key, value)
            }

            @JvmStatic
            fun create(key: String, filename: String, stream: InputStream): KeyVal {
                return KeyVal(key, filename)
                    .inputStream(stream)
            }
        }
    }

    companion object {
        val CONTENT_ENCODING: String = "Content-Encoding"

        /**
         * Many users would get caught by not setting a user-agent and therefore getting different responses on their desktop
         * vs in jsoup, which would otherwise default to `Java`. So by default, use a desktop UA.
         */
        @JvmField
        val DEFAULT_UA: String =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36"
        private val USER_AGENT: String = "User-Agent"
        @JvmField
        val CONTENT_TYPE: String = "Content-Type"
        @JvmField
        val MULTIPART_FORM_DATA: String = "multipart/form-data"
        val FORM_URL_ENCODED: String = "application/x-www-form-urlencoded"
        private val HTTP_TEMP_REDIR: Int = 307 // http/1.1 temporary redirect, not in Java's set.
        private val DefaultUploadType: String = "application/octet-stream"
        private val ISO_8859_1: Charset = Charset.forName("ISO-8859-1")

        /**
         * Create a new Connection, with the request URL specified.
         * @param url the URL to fetch from
         * @return a new Connection object
         */
        @JvmStatic
        fun connect(url: String): Connection {
            val con: Connection = HttpConnection()
            con.url(url)
            return con
        }

        /**
         * Create a new Connection, with the request URL specified.
         * @param url the URL to fetch from
         * @return a new Connection object
         */
        @JvmStatic
        fun connect(url: URL): Connection {
            val con: Connection = HttpConnection()
            con.url(url)
            return con
        }

        private fun encodeMimeName(`val`: String?): String {
            return `val`!!.replace("\"", "%22")
        }

        private fun needsMultipart(req: Connection.Request): Boolean {
            // multipart mode, for files. add the header if we see something with an inputstream, and return a non-null boundary
            for (keyVal: Connection.KeyVal? in req.data()) {
                if (keyVal!!.hasInputStream()) return true
            }
            return false
        }
    }
}
