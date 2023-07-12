package net.dankito.ksoup.integration

import net.dankito.ksoup.Connection
import net.dankito.ksoup.HttpStatusException
import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.newSession
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.helper.*
import net.dankito.ksoup.helper.W3CDom.Companion.asString
import net.dankito.ksoup.integration.servlets.*
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.parser.*
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.*
import java.net.*

/**
 * Tests Jsoup.connect against a local server.
 */
class ConnectTest {
    @Test
    @Throws(IOException::class)
    fun canConnectToLocalServer() {
        val url: String = HelloServlet.Url
        val doc = Jsoup.connect(url).get()
        val p = doc.selectFirst("p")
        Assertions.assertEquals("Hello, World!", p!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun fetchURl() {
        val doc = parse(URL(echoUrl), 10 * 1000)
        Assertions.assertTrue(doc.title().contains("Environment Variables"))
    }

    @Test
    @Throws(IOException::class)
    fun fetchURIWithWhitespace() {
        val con = Jsoup.connect(echoUrl + "#with whitespaces")
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("Environment Variables"))
    }

    @Test
    fun exceptOnUnsupportedProtocol() {
        val url = "file://etc/passwd"
        var threw = false
        try {
            val doc = Jsoup.connect(url).get()
        } catch (e: MalformedURLException) {
            threw = true
            Assertions.assertEquals(
                "java.net.MalformedURLException: Only http & https protocols supported",
                e.toString()
            )
        } catch (e: IOException) {
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun throwsExceptionOn404() {
        val url: String = EchoServlet.Url
        val con = Jsoup.connect(url).header(EchoServlet.CodeParam, "404")
        var threw = false
        try {
            val doc = con.get()
        } catch (e: HttpStatusException) {
            threw = true
            Assertions.assertEquals(
                "net.dankito.ksoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=[" + e.url + "]",
                e.toString()
            )
            Assertions.assertTrue(e.url.startsWith(url))
            Assertions.assertEquals(404, e.statusCode)
        } catch (e: IOException) {
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun ignoresExceptionIfSoConfigured() {
        val url: String = EchoServlet.Companion.Url
        val con = Jsoup.connect(url)
            .header(EchoServlet.Companion.CodeParam, "404")
            .ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(404, res.statusCode())
        Assertions.assertEquals("Webserver Environment Variables", doc.title())
    }

    @Test
    @Throws(IOException::class)
    fun doesPost() {
        val doc = Jsoup.connect(echoUrl)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .cookie("auth", "token")
            .post()
        Assertions.assertEquals("POST", ihVal("Method", doc))
        Assertions.assertEquals("gzip", ihVal("Accept-Encoding", doc))
        Assertions.assertEquals("auth=token", ihVal("Cookie", doc))
        Assertions.assertEquals("度一下", ihVal("百", doc))
        Assertions.assertEquals("Jsoup, Jonathan", ihVal("uname", doc))
        Assertions.assertEquals(
            "application/x-www-form-urlencoded; charset=UTF-8",
            ihVal("Content-Type", doc)
        )
    }

    @Test
    @Throws(IOException::class)
    fun doesPostMultipartWithoutInputstream() {
        val doc = Jsoup.connect(echoUrl)
            .header(HttpConnection.CONTENT_TYPE, HttpConnection.MULTIPART_FORM_DATA)
            .userAgent(UrlConnectTest.browserUa)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .post()
        Assertions.assertTrue(ihVal("Content-Type", doc)?.contains(HttpConnection.MULTIPART_FORM_DATA) == true)
        Assertions.assertTrue(ihVal("Content-Type", doc)?.contains("boundary") == true) // should be automatically set
        Assertions.assertEquals("Jsoup, Jonathan", ihVal("uname", doc))
        Assertions.assertEquals("度一下", ihVal("百", doc))
    }

    @Test
    @Throws(IOException::class)
    fun canSendSecFetchHeaders() {
        // https://github.com/jhy/jsoup/issues/1461
        val doc = Jsoup.connect(echoUrl)
            .header("Random-Header-name", "hello")
            .header("Sec-Fetch-Site", "cross-site")
            .header("Sec-Fetch-Mode", "cors")
            .get()
        Assertions.assertEquals("hello", ihVal("Random-Header-name", doc))
        Assertions.assertEquals("cross-site", ihVal("Sec-Fetch-Site", doc))
        Assertions.assertEquals("cors", ihVal("Sec-Fetch-Mode", doc))
    }

    @Test
    @Throws(IOException::class)
    fun secFetchHeadersSurviveRedirect() {
        val doc = Jsoup
            .connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.LocationParam, echoUrl)
            .header("Random-Header-name", "hello")
            .header("Sec-Fetch-Site", "cross-site")
            .header("Sec-Fetch-Mode", "cors")
            .get()
        Assertions.assertEquals("hello", ihVal("Random-Header-name", doc))
        Assertions.assertEquals("cross-site", ihVal("Sec-Fetch-Site", doc))
        Assertions.assertEquals("cors", ihVal("Sec-Fetch-Mode", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBodyJsonWithData() {
        val body = "{key:value}"
        val doc = Jsoup.connect(echoUrl)
            .requestBody(body)
            .header("Content-Type", "application/json")
            .userAgent(UrlConnectTest.browserUa)
            .data("foo", "true")
            .post()
        Assertions.assertEquals("POST", ihVal("Method", doc))
        Assertions.assertEquals("application/json", ihVal("Content-Type", doc))
        Assertions.assertEquals("foo=true", ihVal("Query String", doc))
        Assertions.assertEquals(body, ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBodyJsonWithoutData() {
        val body = "{key:value}"
        val doc = Jsoup.connect(echoUrl)
            .requestBody(body)
            .header("Content-Type", "application/json")
            .userAgent(UrlConnectTest.browserUa)
            .post()
        Assertions.assertEquals("POST", ihVal("Method", doc))
        Assertions.assertEquals("application/json", ihVal("Content-Type", doc))
        Assertions.assertEquals(body, ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBody() {
        val body = "{key:value}"
        val doc = Jsoup.connect(echoUrl)
            .requestBody(body)
            .header("Content-Type", "text/plain")
            .userAgent(UrlConnectTest.browserUa)
            .post()
        Assertions.assertEquals("POST", ihVal("Method", doc))
        Assertions.assertEquals("text/plain", ihVal("Content-Type", doc))
        Assertions.assertEquals(body, ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBodyWithUrlParams() {
        val body = "{key:value}"
        val doc = Jsoup.connect(echoUrl)
            .requestBody(body)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .header("Content-Type", "text/plain") // todo - if user sets content-type, we should append postcharset
            .userAgent(UrlConnectTest.browserUa)
            .post()
        Assertions.assertEquals("POST", ihVal("Method", doc))
        Assertions.assertEquals(
            "uname=Jsoup&uname=Jonathan&%E7%99%BE=%E5%BA%A6%E4%B8%80%E4%B8%8B",
            ihVal("Query String", doc)
        )
        Assertions.assertEquals(body, ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun doesGet() {
        val con = Jsoup.connect(echoUrl + "?what=the")
            .userAgent("Mozilla")
            .referrer("http://example.com")
            .data("what", "about & me?")
        val doc = con.get()
        Assertions.assertEquals("what=the&what=about+%26+me%3F", ihVal("Query String", doc))
        Assertions.assertEquals("the, about & me?", ihVal("what", doc))
        Assertions.assertEquals("Mozilla", ihVal("User-Agent", doc))
        Assertions.assertEquals("http://example.com", ihVal("Referer", doc))
    }

    @Test
    @Throws(IOException::class)
    fun doesPut() {
        val res = Jsoup.connect(echoUrl)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .cookie("auth", "token")
            .method(Connection.Method.PUT)
            .execute()
        val doc = res.parse()
        Assertions.assertEquals("PUT", ihVal("Method", doc))
        Assertions.assertEquals("gzip", ihVal("Accept-Encoding", doc))
        Assertions.assertEquals("auth=token", ihVal("Cookie", doc))
    }

    /**
     * Tests upload of content to a remote service.
     */
    @Test
    @Throws(IOException::class)
    fun postFiles() {
        val thumb: File = ParseTest.getFile("/htmltests/thumb.jpg")
        val html: File = ParseTest.getFile("/htmltests/large.html")
        val res = Jsoup
            .connect(EchoServlet.Companion.Url)
            .data("firstname", "Jay")
            .data("firstPart", thumb.name, FileInputStream(thumb), "image/jpeg")
            .data("secondPart", html.name, FileInputStream(html)) // defaults to "application-octetstream";
            .data("surname", "Soup")
            .post()
        Assertions.assertEquals("4", ihVal("Parts", res))
        Assertions.assertEquals(
            "application/octet-stream",
            ihVal("Part secondPart ContentType", res)
        )
        Assertions.assertEquals("secondPart", ihVal("Part secondPart Name", res))
        Assertions.assertEquals("large.html", ihVal("Part secondPart Filename", res))
        Assertions.assertEquals("280735", ihVal("Part secondPart Size", res))
        Assertions.assertEquals("image/jpeg", ihVal("Part firstPart ContentType", res))
        Assertions.assertEquals("firstPart", ihVal("Part firstPart Name", res))
        Assertions.assertEquals("thumb.jpg", ihVal("Part firstPart Filename", res))
        Assertions.assertEquals("1052", ihVal("Part firstPart Size", res))
        Assertions.assertEquals("Jay", ihVal("firstname", res))
        Assertions.assertEquals("Soup", ihVal("surname", res))

        /*
        <tr><th>Part secondPart ContentType</th><td>application/octet-stream</td></tr>
        <tr><th>Part secondPart Name</th><td>secondPart</td></tr>
        <tr><th>Part secondPart Filename</th><td>google-ipod.html</td></tr>
        <tr><th>Part secondPart Size</th><td>43972</td></tr>
        <tr><th>Part firstPart ContentType</th><td>image/jpeg</td></tr>
        <tr><th>Part firstPart Name</th><td>firstPart</td></tr>
        <tr><th>Part firstPart Filename</th><td>thumb.jpg</td></tr>
        <tr><th>Part firstPart Size</th><td>1052</td></tr>
         */
    }

    @Test
    @Throws(IOException::class)
    fun multipleParsesOkAfterBufferUp() {
        val res = Jsoup.connect(echoUrl).execute().bufferUp()
        val doc = res.parse()
        Assertions.assertTrue(doc.title().contains("Environment"))
        val doc2 = res.parse()
        Assertions.assertTrue(doc2.title().contains("Environment"))
    }

    @Test
    fun bodyAfterParseThrowsValidationError() {
        Assertions.assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
            val res = Jsoup.connect(echoUrl).execute()
            val doc = res.parse()
            val body = res.body()
        }
    }

    @Test
    @Throws(IOException::class)
    fun bodyAndBytesAvailableBeforeParse() {
        val res = Jsoup.connect(echoUrl).execute()
        val body = res.body()
        Assertions.assertTrue(body.contains("Environment"))
        val bytes = res.bodyAsBytes()
        Assertions.assertTrue(bytes.size > 100)
        val doc = res.parse()
        Assertions.assertTrue(doc.title().contains("Environment"))
    }

    @Test
    fun parseParseThrowsValidates() {
        Assertions.assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
            val res = Jsoup.connect(echoUrl).execute()
            val doc = res.parse()
            Assertions.assertTrue(doc.title().contains("Environment"))
            val doc2 = res.parse() // should blow up because the response input stream has been drained
        }
    }

    @Test
    @Throws(IOException::class)
    fun multiCookieSet() {
        val con = Jsoup
            .connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.CodeParam, "302")
            .data(RedirectServlet.Companion.SetCookiesParam, "true")
            .data(RedirectServlet.Companion.LocationParam, echoUrl)
        val res = con.execute()

        // test cookies set by redirect:
        val cookies = res.cookies()
        Assertions.assertEquals("asdfg123", cookies["token"])
        Assertions.assertEquals("jhy", cookies["uid"])

        // send those cookies into the echo URL by map:
        val doc = Jsoup.connect(echoUrl).cookies(cookies).get()
        Assertions.assertEquals("token=asdfg123; uid=jhy", ihVal("Cookie", doc))
    }

    @Test
    @Throws(IOException::class)
    fun requestCookiesSurviveRedirect() {
        // this test makes sure that Request keyval cookies (not in the cookie store) are sent on subsequent redirections,
        // when not using the session method
        val con = Jsoup.connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.LocationParam, echoUrl)
            .cookie("LetMeIn", "True")
            .cookie("DoesItWork", "Yes")
        val res = con.execute()
        Assertions.assertEquals(0, res.cookies().size) // were not set by Redir or Echo servlet
        val doc = res.parse()
        Assertions.assertEquals(echoUrl, doc.location())
        Assertions.assertEquals("True", ihVal("Cookie: LetMeIn", doc))
        Assertions.assertEquals("Yes", ihVal("Cookie: DoesItWork", doc))
    }

    @Test
    @Throws(IOException::class)
    fun supportsDeflate() {
        val res = Jsoup.connect(Deflateservlet.Companion.Url).execute()
        Assertions.assertEquals("deflate", res.header("Content-Encoding"))
        val doc = res.parse()
        Assertions.assertEquals("Hello, World!", doc.selectFirst("p")!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun handlesLargerContentLengthParseRead() {
        // this handles situations where the remote server sets a content length greater than it actually writes
        val res = Jsoup.connect(InterruptedServlet.Companion.Url)
            .data(InterruptedServlet.Companion.Magnitude, InterruptedServlet.Companion.Larger)
            .timeout(400)
            .execute()
        val document = res.parse()
        Assertions.assertEquals("Something", document.title())
        Assertions.assertEquals(0, document.select("p").size)
        // current impl, jetty won't write past content length
        // todo - find way to trick jetty into writing larger than set header. Take over the stream?
    }

    @Test
    @Throws(IOException::class)
    fun handlesWrongContentLengthDuringBufferedRead() {
        val res = Jsoup.connect(InterruptedServlet.Companion.Url)
            .timeout(400)
            .execute()
        // this servlet writes max_buffer data, but sets content length to max_buffer/2. So will read up to that.
        // previous versions of jetty would allow to write less, and would throw except here
        res.bufferUp()
        val doc = res.parse()
        Assertions.assertEquals(0, doc.select("p").size)
    }

    @Test
    @Throws(IOException::class)
    fun handlesRedirect() {
        val doc = Jsoup.connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.LocationParam, HelloServlet.Companion.Url)
            .get()
        val p = doc.selectFirst("p")
        Assertions.assertEquals("Hello, World!", p!!.text())
        Assertions.assertEquals(HelloServlet.Companion.Url, doc.location())
    }

    @Test
    fun handlesEmptyRedirect() {
        var threw = false
        try {
            val res = Jsoup.connect(RedirectServlet.Companion.Url)
                .execute()
        } catch (e: IOException) {
            Assertions.assertTrue(e.message!!.contains("Too many redirects"))
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun doesNotPostFor302() {
        val doc = Jsoup.connect(RedirectServlet.Companion.Url)
            .data("Hello", "there")
            .data(RedirectServlet.Companion.LocationParam, EchoServlet.Companion.Url)
            .post()
        Assertions.assertEquals(EchoServlet.Companion.Url, doc.location())
        Assertions.assertEquals("GET", ihVal("Method", doc))
        Assertions.assertNull(ihVal("Hello", doc)) // data not sent
    }

    @Test
    @Throws(IOException::class)
    fun doesPostFor307() {
        val doc = Jsoup.connect(RedirectServlet.Companion.Url)
            .data("Hello", "there")
            .data(RedirectServlet.Companion.LocationParam, EchoServlet.Companion.Url)
            .data(RedirectServlet.Companion.CodeParam, "307")
            .post()
        Assertions.assertEquals(EchoServlet.Companion.Url, doc.location())
        Assertions.assertEquals("POST", ihVal("Method", doc))
        Assertions.assertEquals("there", ihVal("Hello", doc))
    }

    @Throws(IOException::class)
    @Test
    fun utf8Bom() {
        val con = Jsoup.connect(FileServlet.Companion.urlTo("/bomtests/bom_utf8.html"))
        val doc = con.get()
        Assertions.assertEquals("UTF-8", con.response().charset())
        Assertions.assertEquals("OK", doc.title())
    }

    @Test
    fun testBinaryContentTypeThrowsException() {
        val con = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/thumb.jpg"))
        con.data(FileServlet.Companion.ContentTypeParam, "image/jpeg")
        var threw = false
        try {
            con.execute()
            val doc = con.response().parse()
        } catch (e: IOException) {
            threw = true
            Assertions.assertEquals(
                "Unhandled content type. Must be text/*, application/xml, or application/*+xml",
                e.message
            )
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun testParseRss() {
        // test that we switch automatically to xml, and we support application/rss+xml
        val con = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/test-rss.xml"))
        con.data(FileServlet.Companion.ContentTypeParam, "application/rss+xml")
        val doc = con.get()
        val title = doc.selectFirst("title")
        Assertions.assertNotNull(title)
        Assertions.assertEquals("jsoup RSS news", title!!.text())
        Assertions.assertEquals("channel", title.parent()!!.nodeName())
        Assertions.assertEquals(
            "",
            doc.title()
        ) // the document title is unset, this tag is channel>title, not html>head>title
        Assertions.assertEquals(3, doc.select("link").size)
        Assertions.assertEquals("application/rss+xml", con.response().contentType())
        Assertions.assertTrue(doc.parser().treeBuilder is XmlTreeBuilder)
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
    }

    @Test
    @Throws(IOException::class)
    fun canFetchBinaryAsBytes() {
        val res = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/thumb.jpg"))
            .data(FileServlet.Companion.ContentTypeParam, "image/jpeg")
            .ignoreContentType(true)
            .execute()
        val bytes = res.bodyAsBytes()
        Assertions.assertEquals(1052, bytes.size)
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnknownEscapesAcrossBuffer() {
        val localPath = "/htmltests/escapes-across-buffer.html"
        val localUrl: String = FileServlet.Companion.urlTo(localPath)
        val docFromLocalServer = Jsoup.connect(localUrl).get()
        val docFromFileRead: Document = parse(ParseTest.getFile(localPath), "UTF-8")
        val text = docFromLocalServer.body().text()
        Assertions.assertEquals(14766, text.length)
        Assertions.assertEquals(text, docFromLocalServer.body().text())
        Assertions.assertEquals(text, docFromFileRead.body().text())
    }

    /**
     * Test fetching a form, and submitting it with a file attached.
     */
    @Test
    @Throws(IOException::class)
    fun postHtmlFile() {
        val index = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/upload-form.html")).get()
        val forms = index.select("[name=tidy]").forms()
        Assertions.assertEquals(1, forms.size)
        val form = forms[0]
        val post = form.submit()
        val uploadFile: File = ParseTest.getFile("/htmltests/large.html")
        val stream = FileInputStream(uploadFile)
        val fileData = post.data("_file")
        Assertions.assertNotNull(fileData)
        fileData!!.value("check.html")
        fileData.inputStream(stream)
        val res: Connection.Response
        res = try {
            post.execute()
        } finally {
            stream.close()
        }
        val doc = res.parse()
        Assertions.assertEquals(ihVal("Method", doc), "POST") // from form action
        Assertions.assertEquals(ihVal("Part _file Filename", doc), "check.html")
        Assertions.assertEquals(ihVal("Part _file Name", doc), "_file")
        Assertions.assertEquals(ihVal("_function", doc), "tidy")
    }

    @Test
    @Throws(IOException::class)
    fun fetchHandlesXml() {
        val types = arrayOf("text/xml", "application/xml", "application/rss+xml", "application/xhtml+xml")
        for (type in types) {
            fetchHandlesXml(type)
        }
    }

    @Throws(IOException::class)
    fun fetchHandlesXml(contentType: String?) {
        // should auto-detect xml and use XML parser, unless explicitly requested the html parser
        val xmlUrl: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val con = Jsoup.connect(xmlUrl)
        con.data(FileServlet.Companion.ContentTypeParam, contentType!!)
        val doc = con.get()
        val req = con.request()
        Assertions.assertTrue(req.parser().treeBuilder is XmlTreeBuilder)
        Assertions.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>\n", doc.outerHtml())
        Assertions.assertEquals(con.response().contentType(), contentType)
    }

    @Test
    @Throws(IOException::class)
    fun fetchHandlesXmlAsHtmlWhenParserSet() {
        // should auto-detect xml and use XML parser, unless explicitly requested the html parser
        val xmlUrl: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val con = Jsoup.connect(xmlUrl).parser(htmlParser())
        con.data(FileServlet.Companion.ContentTypeParam, "application/xml")
        val doc = con.get()
        val req = con.request()
        Assertions.assertTrue(req.parser().treeBuilder is HtmlTreeBuilder)
        Assertions.assertEquals(
            "<html> <head></head> <body> <doc> <val> One <val> Two </val>Three </val> </doc> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
    }

    @Test
    @Throws(IOException::class)
    fun combinesSameHeadersWithComma() {
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
        val con = Jsoup.connect(echoUrl)
        con.get()
        val res = con.response()
        Assertions.assertEquals("text/html;charset=utf-8", res.header("Content-Type"))
        Assertions.assertEquals("no-cache, no-store", res.header("Cache-Control"))
        val header = res.headers("Cache-Control")
        Assertions.assertEquals(2, header.size)
        Assertions.assertEquals("no-cache", header[0])
        Assertions.assertEquals("no-store", header[1])
    }

    @Test
    @Throws(IOException::class)
    fun sendHeadRequest() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val con = Jsoup.connect(url)
            .method(Connection.Method.HEAD)
            .data(FileServlet.Companion.ContentTypeParam, "text/xml")
        val response = con.execute()
        Assertions.assertEquals("text/xml", response.header("Content-Type"))
        Assertions.assertEquals("", response.body()) // head ought to have no body
        val doc = response.parse()
        Assertions.assertEquals("", doc.text())
    }

    @Test
    @Throws(IOException::class)
    fun fetchToW3c() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/upload-form.html")
        val doc = Jsoup.connect(url).get()
        val dom = W3CDom()
        val wDoc = dom.fromJsoup(doc)
        Assertions.assertEquals(url, wDoc.documentURI)
        val html = asString(wDoc)
        Assertions.assertTrue(html.contains("Upload"))
    }

    @Test
    @Throws(IOException::class)
    fun baseHrefCorrectAfterHttpEquiv() {
        // https://github.com/jhy/jsoup/issues/440
        val res = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/charset-base.html")).execute()
        val doc = res.parse()
        Assertions.assertEquals("http://example.com/foo.jpg", doc.select("img").first()!!.absUrl("src"))
    }

    @Test
    @Throws(IOException::class)
    fun maxBodySize() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val defaultRes = Jsoup.connect(url).execute()
        val smallRes = Jsoup.connect(url).maxBodySize(50 * 1024).execute() // crops
        val mediumRes = Jsoup.connect(url).maxBodySize(200 * 1024).execute() // crops
        val largeRes = Jsoup.connect(url).maxBodySize(300 * 1024).execute() // does not crop
        val unlimitedRes = Jsoup.connect(url).maxBodySize(0).execute()
        val actualDocText = 269535
        Assertions.assertEquals(actualDocText, defaultRes.parse().text().length)
        Assertions.assertEquals(49165, smallRes.parse().text().length)
        Assertions.assertEquals(196577, mediumRes.parse().text().length)
        Assertions.assertEquals(actualDocText, largeRes.parse().text().length)
        Assertions.assertEquals(actualDocText, unlimitedRes.parse().text().length)
    }

    @Test
    @Throws(IOException::class)
    fun repeatable() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val con = Jsoup.connect(url).parser(xmlParser())
        val doc1 = con.get()
        val doc2 = con.get()
        Assertions.assertEquals("Large HTML", doc1.title())
        Assertions.assertEquals("Large HTML", doc2.title())
    }

    @Test
    @Throws(IOException::class)
    fun maxBodySizeInReadToByteBuffer() {
        // https://github.com/jhy/jsoup/issues/1774
        // when calling readToByteBuffer, contents were not buffered up
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val defaultRes = Jsoup.connect(url).execute()
        val smallRes = Jsoup.connect(url).maxBodySize(50 * 1024).execute() // crops
        val mediumRes = Jsoup.connect(url).maxBodySize(200 * 1024).execute() // crops
        val largeRes = Jsoup.connect(url).maxBodySize(300 * 1024).execute() // does not crop
        val unlimitedRes = Jsoup.connect(url).maxBodySize(0).execute()
        val actualDocText = 280735
        Assertions.assertEquals(actualDocText, defaultRes.body().length)
        Assertions.assertEquals(50 * 1024, smallRes.body().length)
        Assertions.assertEquals(200 * 1024, mediumRes.body().length)
        Assertions.assertEquals(actualDocText, largeRes.body().length)
        Assertions.assertEquals(actualDocText, unlimitedRes.body().length)
    }

    @Test
    @Throws(IOException::class)
    fun formLoginFlow() {
        val echoUrl: String = EchoServlet.Companion.Url
        val cookieUrl: String = CookieServlet.Companion.Url
        val startUrl: String = FileServlet.Companion.urlTo("/htmltests/form-tests.html")
        val loginDoc = Jsoup.connect(startUrl).get()
        val form = loginDoc.expectForm("#login")
        Assertions.assertNotNull(form)
        form!!.expectFirst("[name=username]").value("admin")
        form.expectFirst("[name=password]").value("Netscape engineers are weenies!")

        // post it- should go to Cookie then bounce to Echo
        val submit = form.submit()
        Assertions.assertEquals(Connection.Method.POST, submit.request().method())
        val postRes = submit.execute()
        Assertions.assertEquals(echoUrl, postRes.url().toExternalForm())
        Assertions.assertEquals(Connection.Method.GET, postRes.method())
        val resultDoc = postRes.parse()
        Assertions.assertEquals("One=EchoServlet; One=Root", ihVal("Cookie", resultDoc))
        // should be no form data sent to the echo redirect
        Assertions.assertEquals("", ihVal("Query String", resultDoc))

        // new request to echo, should not have form data, but should have cookies from implicit session
        val newEcho = submit.newRequest().url(echoUrl).get()
        Assertions.assertEquals("One=EchoServlet; One=Root", ihVal("Cookie", newEcho))
        Assertions.assertEquals("", ihVal("Query String", newEcho))
        val cookieDoc = submit.newRequest().url(cookieUrl).get()
        Assertions.assertEquals("CookieServlet", ihVal("One", cookieDoc)) // different cookie path
    }

    @Test
    @Throws(IOException::class)
    fun formLoginFlow2() {
        val echoUrl: String = EchoServlet.Companion.Url
        val cookieUrl: String = CookieServlet.Companion.Url
        val startUrl: String = FileServlet.Companion.urlTo("/htmltests/form-tests.html")
        val session = newSession()
        val loginDoc = session.newRequest().url(startUrl).get()
        val form = loginDoc.expectForm("#login2")
        Assertions.assertNotNull(form)
        val username = "admin"
        form!!.expectFirst("[name=username]").value(username)
        val password = "Netscape engineers are weenies!"
        form.expectFirst("[name=password]").value(password)
        val submit = form.submit()
        Assertions.assertEquals(username, submit.data("username")!!.value())
        Assertions.assertEquals(password, submit.data("password")!!.value())
        val postRes = submit.execute()
        Assertions.assertEquals(cookieUrl, postRes.url().toExternalForm())
        Assertions.assertEquals(Connection.Method.POST, postRes.method())
        val resultDoc = postRes.parse()
        val echo2 = resultDoc.connection()!!.newRequest().url(echoUrl).get()
        Assertions.assertEquals("", ihVal("Query String", echo2)) // should not re-send the data
        Assertions.assertEquals("One=EchoServlet; One=Root", ihVal("Cookie", echo2))
    }

    @Test
    @Throws(IOException::class)
    fun preservesUrlFragment() {
        // confirms https://github.com/jhy/jsoup/issues/1686
        val url: String = EchoServlet.Companion.Url + "#fragment"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals(url, doc.location())
    }

    @Test
    @Throws(IOException::class)
    fun fetchUnicodeUrl() {
        val url: String = EchoServlet.Companion.Url + "/✔/?鍵=値"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals("/✔/", ihVal("Path Info", doc))
        Assertions.assertEquals("%E9%8D%B5=%E5%80%A4", ihVal("Query String", doc))
        Assertions.assertEquals(
            "鍵=値",
            URLDecoder.decode(ihVal("Query String", doc), DataUtil.UTF_8.name())
        )
    }

    companion object {
        private val echoUrl = EchoServlet.Companion.Url

        @BeforeAll
        fun setUp() {
            TestServer.start()
        }

        private fun ihVal(key: String, doc: Document): String? {
            val first = doc.select("th:contains($key) + td").first()
            return first?.text()
        }
    }
}
