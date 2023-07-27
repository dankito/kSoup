//package net.dankito.ksoup.helper
//
//import net.dankito.ksoup.Connection
//import net.dankito.ksoup.Jsoup
//import net.dankito.ksoup.MultiLocaleExtension.MultiLocaleTest
//import net.dankito.ksoup.helper.HttpConnection.Companion.connect
//import net.dankito.ksoup.helper.HttpConnection.KeyVal.Companion.create
//import net.dankito.ksoup.integration.ParseTest
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//import java.net.URL
//import java.util.Locale
//
//class HttpConnectionTest {
//    /* most actual network http connection tests are in integration */
//    @Test
//    fun canCreateEmptyConnection() {
//        val con = HttpConnection()
//        assertEquals(Connection.Method.GET, con.request().method())
//        assertFailsWith(IllegalArgumentException::class.java) { val url = con.request().url() }
//    }
//
//    @Test
//    fun throwsExceptionOnResponseWithoutExecute() {
//        assertFailsWith(IllegalArgumentException::class.java) {
//            val con = HttpConnection.connect("http://example.com")
//            con.response()
//        }
//    }
//
//    @Test
//    fun throwsExceptionOnParseWithoutExecute() {
//        assertFailsWith(IllegalArgumentException::class.java) {
//            val con = HttpConnection.connect("http://example.com")
//            con.response().parse()
//        }
//    }
//
//    @Test
//    fun throwsExceptionOnBodyWithoutExecute() {
//        assertFailsWith(IllegalArgumentException::class.java) {
//            val con = HttpConnection.connect("http://example.com")
//            con.response().body()
//        }
//    }
//
//    @Test
//    fun throwsExceptionOnBodyAsBytesWithoutExecute() {
//        assertFailsWith(IllegalArgumentException::class.java) {
//            val con = HttpConnection.connect("http://example.com")
//            con.response().bodyAsBytes()
//        }
//    }
//
//    @MultiLocaleTest
//    fun caseInsensitiveHeaders(locale: Locale?) {
//        Locale.setDefault(locale)
//        val res: Connection.Response = HttpConnection.Response()
//        res.header("Accept-Encoding", "gzip")
//        res.header("content-type", "text/html")
//        res.header("refErrer", "http://example.com")
//        assertTrue(res.hasHeader("Accept-Encoding"))
//        assertTrue(res.hasHeader("accept-encoding"))
//        assertTrue(res.hasHeader("accept-Encoding"))
//        assertTrue(res.hasHeader("ACCEPT-ENCODING"))
//        assertEquals("gzip", res.header("accept-Encoding"))
//        assertEquals("gzip", res.header("ACCEPT-ENCODING"))
//        assertEquals("text/html", res.header("Content-Type"))
//        assertEquals("http://example.com", res.header("Referrer"))
//        res.removeHeader("Content-Type")
//        assertFalse(res.hasHeader("content-type"))
//        res.removeHeader("ACCEPT-ENCODING")
//        assertFalse(res.hasHeader("Accept-Encoding"))
//        res.header("ACCEPT-ENCODING", "deflate")
//        assertEquals("deflate", res.header("Accept-Encoding"))
//        assertEquals("deflate", res.header("accept-Encoding"))
//    }
//
//    @Test
//    fun headers() {
//        val con = HttpConnection.connect("http://example.com")
//        val headers: MutableMap<String, String> = HashMap()
//        headers["content-type"] = "text/html"
//        headers["Connection"] = "keep-alive"
//        headers["Host"] = "http://example.com"
//        con.headers(headers)
//        assertEquals("text/html", con.request().header("content-type"))
//        assertEquals("keep-alive", con.request().header("Connection"))
//        assertEquals("http://example.com", con.request().header("Host"))
//    }
//
//    @Test
//    fun sameHeadersCombineWithComma() {
//        val headers: MutableMap<String, MutableList<String?>> = HashMap()
//        val values: MutableList<String?> = ArrayList()
//        values.add("no-cache")
//        values.add("no-store")
//        headers["Cache-Control"] = values
//        val res = HttpConnection.Response()
//        res.processResponseHeaders(headers)
//        assertEquals("no-cache, no-store", res.header("Cache-Control"))
//    }
//
//    @Test
//    fun multipleHeaders() {
//        val req: Connection.Request = HttpConnection.Request()
//        req.addHeader("Accept", "Something")
//        req.addHeader("Accept", "Everything")
//        req.addHeader("Foo", "Bar")
//        assertTrue(req.hasHeader("Accept"))
//        assertTrue(req.hasHeader("ACCEpt"))
//        assertEquals("Something, Everything", req.header("accept"))
//        assertTrue(req.hasHeader("fOO"))
//        assertEquals("Bar", req.header("foo"))
//        val accept = req.headers("accept")
//        assertEquals(2, accept.size)
//        assertEquals("Something", accept[0])
//        assertEquals("Everything", accept[1])
//        var headers = req.multiHeaders()
//        assertEquals(accept, headers["Accept"])
//        assertEquals("Bar", headers["Foo"]!![0])
//        assertTrue(req.hasHeader("Accept"))
//        assertTrue(req.hasHeaderWithValue("accept", "Something"))
//        assertTrue(req.hasHeaderWithValue("accept", "Everything"))
//        assertFalse(req.hasHeaderWithValue("accept", "Something for nothing"))
//        req.removeHeader("accept")
//        headers = req.multiHeaders()
//        assertEquals("Bar", headers["Foo"]!![0])
//        assertFalse(req.hasHeader("Accept"))
//        assertNull(headers["Accept"])
//    }
//
//    @Test
//    fun ignoresEmptySetCookies() {
//        // prep http response header map
//        val headers: MutableMap<String, MutableList<String?>> = HashMap()
//        headers["Set-Cookie"] = mutableListOf()
//        val res = HttpConnection.Response()
//        res.processResponseHeaders(headers)
//        assertEquals(0, res.cookies().size)
//    }
//
//    @Test
//    fun ignoresEmptyCookieNameAndVals() {
//        // prep http response header map
//        val headers: MutableMap<String, MutableList<String?>> = HashMap()
//        val cookieStrings: MutableList<String?> = ArrayList()
//        cookieStrings.add(null)
//        cookieStrings.add("")
//        cookieStrings.add("one")
//        cookieStrings.add("two=")
//        cookieStrings.add("three=;")
//        cookieStrings.add("four=data; Domain=.example.com; Path=/")
//        headers["Set-Cookie"] = cookieStrings
//        val res = HttpConnection.Response()
//        res.processResponseHeaders(headers)
//        assertEquals(4, res.cookies().size)
//        assertEquals("", res.cookie("one"))
//        assertEquals("", res.cookie("two"))
//        assertEquals("", res.cookie("three"))
//        assertEquals("data", res.cookie("four"))
//    }
//
//    @Test
//    fun connectWithUrl() {
//        val con = connect(URL("http://example.com"))
//        assertEquals("http://example.com", con.request().url().toExternalForm())
//    }
//
//    @Test
//    fun throwsOnMalformedUrl() {
//        assertFailsWith(IllegalArgumentException::class.java) { HttpConnection.connect("bzzt") }
//    }
//
//    @Test
//    fun userAgent() {
//        val con = HttpConnection.connect("http://example.com/")
//        assertEquals(HttpConnection.DEFAULT_UA, con.request().header("User-Agent"))
//        con.userAgent("Mozilla")
//        assertEquals("Mozilla", con.request().header("User-Agent"))
//    }
//
//    @Test
//    fun timeout() {
//        val con = HttpConnection.connect("http://example.com/")
//        assertEquals(30 * 1000, con.request().timeout())
//        con.timeout(1000)
//        assertEquals(1000, con.request().timeout())
//    }
//
//    @Test
//    fun referrer() {
//        val con = HttpConnection.connect("http://example.com/")
//        con.referrer("http://foo.com")
//        assertEquals("http://foo.com", con.request().header("Referer"))
//    }
//
//    @Test
//    fun method() {
//        val con = HttpConnection.connect("http://example.com/")
//        assertEquals(Connection.Method.GET, con.request().method())
//        con.method(Connection.Method.POST)
//        assertEquals(Connection.Method.POST, con.request().method())
//    }
//
//    @Test
//    fun throwsOnOddData() {
//        assertFailsWith(IllegalArgumentException::class.java) {
//            val con = HttpConnection.connect("http://example.com/")
//            con.data("Name", "val", "what")
//        }
//    }
//
//    @Test
//    fun data() {
//        val con = HttpConnection.connect("http://example.com/")
//        con.data("Name", "Val", "Foo", "bar")
//        val values: Collection<Connection.KeyVal> = con.request().data()
//        val data: Array<Any> = values.toTypedArray()
//        val one = data[0] as Connection.KeyVal
//        val two = data[1] as Connection.KeyVal
//        assertEquals("Name", one.key())
//        assertEquals("Val", one.value())
//        assertEquals("Foo", two.key())
//        assertEquals("bar", two.value())
//    }
//
//    @Test
//    fun cookie() {
//        val con = HttpConnection.connect("http://example.com/")
//        con.cookie("Name", "Val")
//        assertEquals("Val", con.request().cookie("Name"))
//    }
//
//    @Test
//    fun inputStream() {
//        var kv: Connection.KeyVal = create("file", "thumb.jpg", ParseTest.inputStreamFrom("Check"))
//        assertEquals("file", kv.key())
//        assertEquals("thumb.jpg", kv.value())
//        assertTrue(kv.hasInputStream())
//        kv = create("one", "two")
//        assertEquals("one", kv.key())
//        assertEquals("two", kv.value())
//        assertFalse(kv.hasInputStream())
//    }
//
//    @Test
//    fun requestBody() {
//        val con = HttpConnection.connect("http://example.com/")
//        con.requestBody("foo")
//        assertEquals("foo", con.request().requestBody())
//    }
//
//    @Test
//    fun encodeUrl() {
//        val url1 = URL("https://test.com/foo bar/[One]?q=white space#frag")
//        val url2 = UrlBuilder(url1).build()
//        assertEquals("https://test.com/foo%20bar/%5BOne%5D?q=white+space#frag", url2.toExternalForm())
//    }
//
//    @Test
//    fun encodedUrlDoesntDoubleEncode() {
//        val url1 = URL("https://test.com/foo bar/[One]?q=white space#frag ment")
//        val url2 = UrlBuilder(url1).build()
//        val url3 = UrlBuilder(url2).build()
//        assertEquals("https://test.com/foo%20bar/%5BOne%5D?q=white+space#frag%20ment", url2.toExternalForm())
//        assertEquals("https://test.com/foo%20bar/%5BOne%5D?q=white+space#frag%20ment", url3.toExternalForm())
//    }
//
//    @Test
//    fun connectToEncodedUrl() {
//        val connect = Jsoup.connect("https://example.com/a%20b%20c?query+string")
//        val url = connect.request().url()
//        assertEquals("https://example.com/a%20b%20c?query+string", url.toExternalForm())
//    }
//
//    @Test
//    fun noUrlThrowsValidationError() {
//        val con = HttpConnection()
//        var threw = false
//        try {
//            con.execute()
//        } catch (e: IllegalArgumentException) {
//            threw = true
//            assertEquals("URL not set. Make sure to call #url(...) before executing the request.", e.message)
//        }
//        assertTrue(threw)
//    }
//
//    @Test
//    fun handlesHeaderEncodingOnRequest() {
//        val req: Connection.Request = HttpConnection.Request()
//        req.addHeader("xxx", "é")
//    }
//
//    @Test
//    fun supportsInternationalDomainNames() {
//        val idn = "https://www.测试.测试/foo.html?bar"
//        val puny = "https://www.xn--0zwm56d.xn--0zwm56d/foo.html?bar"
//        val con = Jsoup.connect(idn)
//        assertEquals(puny, con.request().url().toExternalForm())
//        val req = HttpConnection.Request()
//        req.url(URL(idn))
//        assertEquals(puny, req.url().toExternalForm())
//    }
//
//    @Test
//    fun supportsIdnWithPort() {
//        val idn = "https://www.测试.测试:9001/foo.html?bar"
//        val puny = "https://www.xn--0zwm56d.xn--0zwm56d:9001/foo.html?bar"
//        val con = Jsoup.connect(idn)
//        assertEquals(puny, con.request().url().toExternalForm())
//        val req = HttpConnection.Request()
//        req.url(URL(idn))
//        assertEquals(puny, req.url().toExternalForm())
//    }
//
//    @Test
//    fun validationErrorsOnExecute() {
//        val con: Connection = HttpConnection()
//        var urlThrew = false
//        try {
//            con.execute()
//        } catch (e: IllegalArgumentException) {
//            urlThrew = e.message!!.contains("URL")
//        }
//        assertTrue(urlThrew)
//    }
//
//    @Test
//    fun testMalformedException() {
//        var threw = false
//        try {
//            Jsoup.connect("jsoup.org/test")
//        } catch (e: IllegalArgumentException) {
//            threw = true
//            assertEquals(
//                "The supplied URL, 'jsoup.org/test', is malformed. Make sure it is an absolute URL, and starts with 'http://' or 'https://'. See https://jsoup.org/cookbook/extracting-data/working-with-urls",
//                e.message
//            )
//        }
//        assertTrue(threw)
//    }
//}
