package net.dankito.ksoup

import net.dankito.ksoup.helper.DataUtil
//import net.dankito.ksoup.helper.HttpConnection
import net.dankito.ksoup.jvm.IOException
import net.dankito.ksoup.nodes.Document
import net.dankito.ksoup.parser.Parser
import java.io.File
import java.io.InputStream
import java.net.URL


/**
 * Creates a new [Connection] (session), with the defined request URL. Use to fetch and parse a HTML page.
 *
 *
 * Use examples:
 *
 *  * `Document doc = Jsoup.connect("http://example.com").userAgent("Mozilla").data("name", "jsoup").get();`
 *  * `Document doc = Jsoup.connect("http://example.com").cookie("auth", "token").post();`
 *
 * @param url URL to connect to. The protocol must be `http` or `https`.
 * @return the connection. You can add data, cookies, and headers; set the user-agent, referrer, method; and then execute.
 * @see .newSession
 * @see Connection.newRequest
 */
////@JvmStatic
//fun Jsoup.connect(url: String): Connection {
//    return HttpConnection.connect(url)
//}

/**
 * Creates a new [Connection] to use as a session. Connection settings (user-agent, timeouts, URL, etc), and
 * cookies will be maintained for the session. Use examples:
 * <pre>`
 * Connection session = Jsoup.newSession()
 * .timeout(20 * 1000)
 * .userAgent("FooBar 2000");
 *
 * Document doc1 = session.newRequest()
 * .url("https://jsoup.org/").data("ref", "example")
 * .get();
 * Document doc2 = session.newRequest()
 * .url("https://en.wikipedia.org/wiki/Main_Page")
 * .get();
 * Connection con3 = session.newRequest();
`</pre> *
 *
 *
 * For multi-threaded requests, it is safe to use this session between threads, but take care to call [Connection.newRequest] per request and not share that instance between threads when executing or parsing.
 *
 * @return a connection
 * @since 1.14.1
 */
//@JvmStatic
//fun Jsoup.newSession(): Connection {
//    return HttpConnection()
//}

/**
 * Parse the contents of a file as HTML.
 *
 * @param file          file to load HTML from. Supports gzipped files (ending in .z or .gz).
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
 * @return sane HTML
 * @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
 */
//@JvmStatic
fun Jsoup.parse(file: File, charsetName: String?, baseUri: String): Document {
    return DataUtil.load(file, charsetName, baseUri)
}

/**
 * Parse the contents of a file as HTML. The location of the file is used as the base URI to qualify relative URLs.
 *
 * @param file        file to load HTML from. Supports gzipped files (ending in .z or .gz).
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @return sane HTML
 * @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
 * @see .parse
 */
//@JvmStatic
fun Jsoup.parse(file: File, charsetName: String?): Document {
    return DataUtil.load(file, charsetName, file.absolutePath)
}

/**
 * Parse the contents of a file as HTML. The location of the file is used as the base URI to qualify relative URLs.
 * The charset used to read the file will be determined by the byte-order-mark (BOM), or a `<meta charset>` tag,
 * or if neither is present, will be `UTF-8`.
 *
 *
 * This is the equivalent of calling [parse(file, null)][.parse]
 *
 * @param file the file to load HTML from. Supports gzipped files (ending in .z or .gz).
 * @return sane HTML
 * @throws IOException if the file could not be found or read.
 * @see .parse
 * @since 1.15.1
 */
//@JvmStatic
fun Jsoup.parse(file: File): Document {
    return DataUtil.load(file, null, file.absolutePath)
}

/**
 * Parse the contents of a file as HTML.
 *
 * @param file          file to load HTML from. Supports gzipped files (ending in .z or .gz).
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
 * @param parser alternate [parser][Parser.xmlParser] to use.
 * @return sane HTML
 * @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
 * @since 1.14.2
 */
//@JvmStatic
fun Jsoup.parse(file: File, charsetName: String?, baseUri: String, parser: Parser): Document {
    return DataUtil.load(file, charsetName, baseUri, parser)
}

/**
 * Read an input stream, and parse it to a Document.
 *
 * @param in          input stream to read. The stream will be closed after reading.
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
 * @return sane HTML
 * @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
 */
//@JvmStatic
fun Jsoup.parse(`in`: InputStream, charsetName: String?, baseUri: String): Document {
    return DataUtil.load(`in`, charsetName, baseUri)
}

/**
 * Read an input stream, and parse it to a Document. You can provide an alternate parser, such as a simple XML
 * (non-HTML) parser.
 *
 * @param in          input stream to read. Make sure to close it after parsing.
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
 * @param parser alternate [parser][Parser.xmlParser] to use.
 * @return sane HTML
 * @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
 */
//@JvmStatic
fun Jsoup.parse(`in`: InputStream, charsetName: String?, baseUri: String, parser: Parser): Document {
    return DataUtil.load(`in`, charsetName, baseUri, parser)
}

/**
 * Fetch a URL, and parse it as HTML. Provided for compatibility; in most cases use [.connect] instead.
 *
 *
 * The encoding character set is determined by the content-type header or http-equiv meta tag, or falls back to `UTF-8`.
 *
 * @param url           URL to fetch (with a GET). The protocol must be `http` or `https`.
 * @param timeoutMillis Connection and read timeout, in milliseconds. If exceeded, IOException is thrown.
 * @return The parsed HTML.
 * @throws java.net.MalformedURLException if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
 * @throws HttpStatusException if the response is not OK and HTTP response errors are not ignored
 * @throws UnsupportedMimeTypeException if the response mime type is not supported and those errors are not ignored
 * @throws java.net.SocketTimeoutException if the connection times out
 * @throws IOException if a connection or read error occurs
 * @see .connect
 */
//@JvmStatic
//fun Jsoup.parse(url: URL, timeoutMillis: Int): Document {
//    val con: Connection = HttpConnection.connect(url)
//    con.timeout(timeoutMillis)
//    return con.get()
//}