package net.dankito.ksoup.integration

import net.dankito.ksoup.Connection
import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.UnsupportedMimeTypeException
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.*
import java.io.IOException
import java.net.*

/**
 * Tests the URL connection. Not enabled by default, so tests don't require network connection.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
@Disabled // ignored by default so tests don't require network access. comment out to enable.
// todo: rebuild these into a local Jetty test server, so not reliant on the vagaries of the internet.

class UrlConnectTest {
    @Test
    @Throws(IOException::class)
    fun fetchBaidu() {
        val res = Jsoup.connect("http://www.baidu.com/").timeout(10 * 1000).execute()
        val doc = res.parse()
        Assertions.assertEquals("GBK", doc.outputSettings().charset()!!.displayName())
        Assertions.assertEquals("GBK", res.charset())
        assert(res.hasCookie("BAIDUID"))
        Assertions.assertEquals("text/html;charset=gbk", res.contentType())
    }

    @Test
    fun exceptOnUnknownContentType() {
        val url = "http://direct.jsoup.org/rez/osi_logo.png" // not text/* but image/png, should throw
        var threw = false
        try {
            val doc = parse(URL(url), 3000)
        } catch (e: UnsupportedMimeTypeException) {
            threw = true
            Assertions.assertEquals(
                "net.dankito.ksoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml. Mimetype=image/png, URL=http://direct.jsoup.org/rez/osi_logo.png",
                e.toString()
            )
            Assertions.assertEquals(url, e.url)
            Assertions.assertEquals("image/png", e.mimeType)
        } catch (e: IOException) {
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun ignoresContentTypeIfSoConfigured() {
        val doc = Jsoup.connect("https://jsoup.org/rez/osi_logo.png").ignoreContentType(true).get()
        Assertions.assertEquals("", doc.title()) // this will cause an ugly parse tree
    }

    @Test
    @Throws(IOException::class)
    fun followsTempRedirect() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302.pl") // http://jsoup.org
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
    }

    @Test
    @Throws(IOException::class)
    fun followsNewTempRedirect() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/307.pl") // http://jsoup.org
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
        Assertions.assertEquals("https://jsoup.org/", con.response().url().toString())
    }

    @Test
    @Throws(IOException::class)
    fun postRedirectsFetchWithGet() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302.pl")
            .data("Argument", "Riposte")
            .method(Connection.Method.POST)
        val res = con.execute()
        Assertions.assertEquals("https://jsoup.org/", res.url().toExternalForm())
        Assertions.assertEquals(Connection.Method.GET, res.method())
    }

    @Test
    @Throws(IOException::class)
    fun followsRedirectToHttps() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-secure.pl") // https://www.google.com
        con.data("id", "5")
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("Google"))
    }

    @Test
    @Throws(IOException::class)
    fun followsRelativeRedirect() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-rel.pl") // to /tidy/
        val doc = con.post()
        Assertions.assertTrue(doc.title().contains("HTML Tidy Online"))
    }

    @Test
    @Throws(IOException::class)
    fun followsRelativeDotRedirect() {
        // redirects to "./ok.html", should resolve to http://direct.infohound.net/tools/ok.html
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-rel-dot.pl") // to ./ok.html
        val doc = con.post()
        Assertions.assertTrue(doc.title().contains("OK"))
        Assertions.assertEquals(doc.location(), "http://direct.infohound.net/tools/ok.html")
    }

    @Test
    @Throws(IOException::class)
    fun followsRelativeDotRedirect2() {
        //redirects to "esportspenedes.cat/./ep/index.php", should resolve to "esportspenedes.cat/ep/index.php"
        val con =
            Jsoup.connect("http://esportspenedes.cat") // note lack of trailing / - server should redir to / first, then to ./ep/...; but doesn't'
                .timeout(10000)
        val doc = con.post()
        Assertions.assertEquals(doc.location(), "http://esportspenedes.cat/ep/index.php")
    }

    @Test
    @Throws(IOException::class)
    fun followsRedirectsWithWithespaces() {
        val con = Jsoup.connect("http://tinyurl.com/kgofxl8") // to http://www.google.com/?q=white spaces
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("Google"))
    }

    @Test
    @Throws(IOException::class)
    fun gracefullyHandleBrokenLocationRedirect() {
        val con = Jsoup.connect("http://aag-ye.com") // has Location: http:/temp/AAG_New/en/index.php
        con.get() // would throw exception on error
        Assertions.assertTrue(true)
    }

    @Test
    @Throws(IOException::class)
    fun ignores500tExceptionIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/500.pl").ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(500, res.statusCode())
        Assertions.assertEquals("Application Error", res.statusMessage())
        Assertions.assertEquals("Woops", doc.select("h1").first()!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun ignores500WithNoContentExceptionIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/500-no-content.pl").ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(500, res.statusCode())
        Assertions.assertEquals("Application Error", res.statusMessage())
    }

    @Test
    @Throws(IOException::class)
    fun ignores200WithNoContentExceptionIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/200-no-content.pl").ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
        Assertions.assertEquals("All Good", res.statusMessage())
    }

    @Test
    @Throws(IOException::class)
    fun handles200WithNoContent() {
        var con = Jsoup
            .connect("http://direct.infohound.net/tools/200-no-content.pl")
            .userAgent(browserUa)
        var res = con.execute()
        var doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
        con = Jsoup
            .connect("http://direct.infohound.net/tools/200-no-content.pl")
            .parser(xmlParser())
            .userAgent(browserUa)
        res = con.execute()
        doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
    }

    @Test
    @Throws(IOException::class)
    fun doesntRedirectIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302.pl").followRedirects(false)
        val res = con.execute()
        Assertions.assertEquals(302, res.statusCode())
        Assertions.assertEquals("http://jsoup.org", res.header("Location"))
    }

    @Test
    @Throws(IOException::class)
    fun redirectsResponseCookieToNextResponse() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl")
        val res = con.execute()
        Assertions.assertEquals(
            "asdfg123",
            res.cookie("token")
        ) // confirms that cookies set on 1st hit are presented in final result
        val doc = res.parse()
        Assertions.assertEquals(
            "token=asdfg123; uid=jhy",
            ihVal("HTTP_COOKIE", doc)
        ) // confirms that redirected hit saw cookie
    }

    @Test
    fun maximumRedirects() {
        var threw = false
        try {
            val doc = Jsoup.connect("http://direct.infohound.net/tools/loop.pl").get()
        } catch (e: IOException) {
            Assertions.assertTrue(e.message!!.contains("Too many redirects"))
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun handlesDodgyCharset() {
        // tests that when we get back "UFT8", that it is recognised as unsupported, and falls back to default instead
        val url = "http://direct.infohound.net/tools/bad-charset.pl"
        val res = Jsoup.connect(url).execute()
        Assertions.assertEquals("text/html; charset=UFT8", res.header("Content-Type")) // from the header
        Assertions.assertNull(res.charset()) // tried to get from header, not supported, so returns null
        val doc = res.parse() // would throw an error if charset unsupported
        Assertions.assertTrue(doc.text().contains("Hello!"))
        Assertions.assertEquals("UTF-8", res.charset()) // set from default on parse
    }

    /**
     * Verify that security disabling feature works properly.
     *
     *
     * 1. try to hit url with invalid certificate and evaluate that exception is thrown
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testUnsafeFail() {
        val url: String = WEBSITE_WITH_INVALID_CERTIFICATE
        Assertions.assertThrows(IOException::class.java) { Jsoup.connect(url).execute() }
    }

    /**
     * Verify that requests to websites with SNI fail on jdk 1.6
     *
     *
     * read for more details:
     * http://en.wikipedia.org/wiki/Server_Name_Indication
     *
     * Test is ignored independent from others as it requires JDK 1.6
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testSNIFail() {
        Assertions.assertThrows<IOException>(IOException::class.java) {
            Jsoup.connect(WEBSITE_WITH_SNI).execute()
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldWorkForCharsetInExtraAttribute() {
        val res = Jsoup.connect("https://www.creditmutuel.com/groupe/fr/").execute()
        val doc = res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("ISO-8859-1", res.charset())
    }

    // The following tests were added to test specific domains if they work. All code paths
    // which make the following test green are tested in other unit or integration tests, so the following lines
    // could be deleted
    @Test
    @Throws(IOException::class)
    fun shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        val res = Jsoup.connect("http://aamo.info/").execute()
        res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("ISO-8859-1", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun shouldParseBrokenHtml5MetaCharsetTagCorrectly() {
        val res = Jsoup.connect("http://9kuhkep.net").execute()
        res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("UTF-8", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun shouldEmptyMetaCharsetCorrectly() {
        val res = Jsoup.connect("http://aastmultimedia.com").execute()
        res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("UTF-8", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun shouldWorkForDuplicateCharsetInTag() {
        val res = Jsoup.connect("http://aaptsdassn.org").execute()
        val doc = res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("ISO-8859-1", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun handles201Created() {
        val doc = Jsoup.connect("http://direct.infohound.net/tools/201.pl").get() // 201, location=jsoup
        Assertions.assertEquals("https://jsoup.org/", doc.location())
    }

    /*
     Proxy tests. Assumes local proxy running on 8888, without system propery set (so that specifying it is required).
     */
    @Test
    @Throws(IOException::class)
    fun fetchViaHttpProxy() {
        val url = "https://jsoup.org"
        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("localhost", 8888))
        val doc = Jsoup.connect(url).proxy(proxy).get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
    }

    @Test
    @Throws(IOException::class)
    fun fetchViaHttpProxySetByArgument() {
        val url = "https://jsoup.org"
        val doc = Jsoup.connect(url).proxy("localhost", 8888).get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
    }

    @Test
    fun invalidProxyFails() {
        var caught = false
        val url = "https://jsoup.org"
        try {
            val doc = Jsoup.connect(url).proxy("localhost", 8889).get()
        } catch (e: IOException) {
            caught = e is ConnectException
        }
        Assertions.assertTrue(caught)
    }

    @Test
    @Throws(IOException::class)
    fun proxyGetAndSet() {
        val url = "https://jsoup.org"
        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("localhost", 8889)) // invalid
        val con = Jsoup.connect(url).proxy(proxy)
        assert(con.request().proxy() === proxy)
        con.request().proxy(null) // disable
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("jsoup")) // would fail if actually went via proxy
    }

    @Test
    @Throws(IOException::class)
    fun throwsIfRequestBodyForGet() {
        var caught = false
        val url = "https://jsoup.org"
        try {
            val doc = Jsoup.connect(url).requestBody("fail").get()
        } catch (e: IllegalArgumentException) {
            caught = true
        }
        Assertions.assertTrue(caught)
    }

    @Test
    @Throws(IOException::class)
    fun canSpecifyResponseCharset() {
        // both these docs have <80> in there as euro/control char depending on charset
        val noCharsetUrl = "http://direct.infohound.net/tools/Windows-1252-nocharset.html"
        val charsetUrl = "http://direct.infohound.net/tools/Windows-1252-charset.html"

        // included in meta
        val res1 = Jsoup.connect(charsetUrl).execute()
        Assertions.assertNull(res1.charset()) // not set in headers
        val doc1 = res1.parse()
        Assertions.assertEquals("windows-1252", doc1.charset()!!.displayName()) // but determined at parse time
        Assertions.assertEquals("Cost is €100", doc1.select("p").text())
        Assertions.assertTrue(doc1.text().contains("€"))

        // no meta, no override
        val res2 = Jsoup.connect(noCharsetUrl).execute()
        Assertions.assertNull(res2.charset()) // not set in headers
        val doc2 = res2.parse()
        Assertions.assertEquals("UTF-8", doc2.charset()!!.displayName()) // so defaults to utf-8
        Assertions.assertEquals("Cost is �100", doc2.select("p").text())
        Assertions.assertTrue(doc2.text().contains("�"))

        // no meta, let's override
        val res3 = Jsoup.connect(noCharsetUrl).execute()
        Assertions.assertNull(res3.charset()) // not set in headers
        res3.charset("windows-1252")
        Assertions.assertEquals("windows-1252", res3.charset()) // read back
        val doc3 = res3.parse()
        Assertions.assertEquals("windows-1252", doc3.charset()!!.displayName()) // from override
        Assertions.assertEquals("Cost is €100", doc3.select("p").text())
        Assertions.assertTrue(doc3.text().contains("€"))
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnescapedRedirects() {
        // URL locations should be url safe (ascii) but are often not, so we should try to guess
        // in this case the location header is utf-8, but defined in spec as iso8859, so detect, convert, encode
        val url = "http://direct.infohound.net/tools/302-utf.pl"
        val urlEscaped = "http://direct.infohound.net/tools/test%F0%9F%92%A9.html"
        val res = Jsoup.connect(url).execute()
        val doc = res.parse()
        Assertions.assertEquals(doc.body().text(), "\uD83D\uDCA9!")
        Assertions.assertEquals(doc.location(), urlEscaped)
        val res2 = Jsoup.connect(url).followRedirects(false).execute()
        Assertions.assertEquals("/tools/test\uD83D\uDCA9.html", res2.header("Location"))
        // if we didn't notice it was utf8, would look like: Location: /tools/testð©.html
    }

    @Test
    @Throws(IOException::class)
    fun handlesEscapesInRedirecct() {
        var doc = Jsoup.connect("http://infohound.net/tools/302-escaped.pl").get()
        Assertions.assertEquals("http://infohound.net/tools/q.pl?q=one%20two", doc.location())
        doc = Jsoup.connect("http://infohound.net/tools/302-white.pl").get()
        Assertions.assertEquals("http://infohound.net/tools/q.pl?q=one%20two", doc.location())
    }

    @Test
    @Throws(IOException::class)
    fun handlesUt8fInUrl() {
        val url = "http://direct.infohound.net/tools/test\uD83D\uDCA9.html"
        val urlEscaped = "http://direct.infohound.net/tools/test%F0%9F%92%A9.html"
        val res = Jsoup.connect(url).execute()
        val doc = res.parse()
        Assertions.assertEquals("\uD83D\uDCA9!", doc.body().text())
        Assertions.assertEquals(urlEscaped, doc.location())
    }

    @Test
    @Throws(IOException::class)
    fun inWildUtfRedirect() {
        val res = Jsoup.connect("http://brabantn.ws/Q4F").execute()
        val doc = res.parse()
        Assertions.assertEquals(
            "http://www.omroepbrabant.nl/?news/2474781303/Gestrande+ree+in+Oss+niet+verdoofd,+maar+doodgeschoten+%E2%80%98Dit+kan+gewoon+niet,+bizar%E2%80%99+[VIDEO].aspx",
            doc.location()
        )
    }

    @Test
    @Throws(IOException::class)
    fun inWildUtfRedirect2() {
        val res = Jsoup.connect("https://ssl.souq.com/sa-en/2724288604627/s").execute()
        val doc = res.parse()
        Assertions.assertEquals(
            "https://saudi.souq.com/sa-en/%D8%AE%D8%B2%D9%86%D8%A9-%D8%A2%D9%85%D9%86%D8%A9-3-%D8%B7%D8%A8%D9%82%D8%A7%D8%AA-%D8%A8%D9%86%D8%B8%D8%A7%D9%85-%D9%82%D9%81%D9%84-%D8%A5%D9%84%D9%83%D8%AA%D8%B1%D9%88%D9%86%D9%8A-bsd11523-6831477/i/?ctype=dsrch",
            doc.location()
        )
    }

    @Test
    @Throws(IOException::class)
    fun handlesEscapedRedirectUrls() {
        val url =
            "http://www.altalex.com/documents/news/2016/12/06/questioni-civilistiche-conseguenti-alla-depenalizzazione"
        // sends: Location:http://shop.wki.it/shared/sso/sso.aspx?sso=&url=http%3a%2f%2fwww.altalex.com%2fsession%2fset%2f%3freturnurl%3dhttp%253a%252f%252fwww.altalex.com%253a80%252fdocuments%252fnews%252f2016%252f12%252f06%252fquestioni-civilistiche-conseguenti-alla-depenalizzazione
        // then to: http://www.altalex.com/session/set/?returnurl=http%3a%2f%2fwww.altalex.com%3a80%2fdocuments%2fnews%2f2016%2f12%2f06%2fquestioni-civilistiche-conseguenti-alla-depenalizzazione&sso=RDRG6T684G4AK2E7U591UGR923
        // then : http://www.altalex.com:80/documents/news/2016/12/06/questioni-civilistiche-conseguenti-alla-depenalizzazione

        // bug is that jsoup goes to
        // 	GET /shared/sso/sso.aspx?sso=&url=http%253a%252f%252fwww.altalex.com%252fsession%252fset%252f%253freturnurl%253dhttp%25253a%25252f%25252fwww.altalex.com%25253a80%25252fdocuments%25252fnews%25252f2016%25252f12%25252f06%25252fquestioni-civilistiche-conseguenti-alla-depenalizzazione HTTP/1.1
        // i.e. double escaped
        val res = Jsoup.connect(url)
            .proxy("localhost", 8888)
            .execute()
        val doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnicodeInQuery() {
        var doc = Jsoup.connect("https://www.google.pl/search?q=gąska").get()
        Assertions.assertEquals("gąska - Szukaj w Google", doc.title())
        doc = Jsoup.connect("http://mov-world.net/archiv/TV/A/%23No.Title/").get()
        Assertions.assertEquals("Index of /archiv/TV/A/%23No.Title", doc.title())
    }

    @Test
    @Throws(IOException::class)
    fun handlesSuperDeepPage() {
        // https://github.com/jhy/jsoup/issues/955
        val start = System.currentTimeMillis()
        val url = "http://sv.stargate.wikia.com/wiki/M2J"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals("M2J | Sv.stargate Wiki | FANDOM powered by Wikia", doc.title())
        Assertions.assertEquals(110160, doc.select("dd").size)
        // those are all <dl><dd> stacked in each other. wonder how that got generated?
        Assertions.assertTrue(System.currentTimeMillis() - start < 1000)
    }

    @Test
    @Throws(IOException::class)
    fun handles966() {
        // http://szshb.nxszs.gov.cn/
        // https://github.com/jhy/jsoup/issues/966
        val doc = Jsoup.connect("http://szshb.nxszs.gov.cn/").get()
        Assertions.assertEquals("石嘴山市环境保护局", doc.title())
    }

    @Test
    @Throws(IOException::class)
    fun canRequestIdn() {
        val url = "https://räksmörgås.josefsson.org/"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals("https://xn--rksmrgs-5wao1o.josefsson.org/", doc.location())
        Assertions.assertTrue(doc.title().contains("Räksmörgås.josefßon.org"))
    }

    companion object {
        private const val WEBSITE_WITH_INVALID_CERTIFICATE = "https://certs.cac.washington.edu/CAtest/"
        private const val WEBSITE_WITH_SNI = "https://jsoup.org/"
        var browserUa =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36"

        private fun ihVal(key: String, doc: Document): String {
            return doc.select("th:contains($key) + td").first()!!.text()
        }
    }
}
