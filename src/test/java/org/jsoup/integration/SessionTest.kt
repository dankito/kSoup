package org.jsoup.integration

import org.jsoup.Jsoup.newSession
import org.jsoup.integration.servlets.CookieServlet
import org.jsoup.integration.servlets.EchoServlet
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.nodes.*
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.select.Elements
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException

class SessionTest {
    @Test
    @Throws(IOException::class)
    fun testPathScopedCookies() {
        val session = newSession()
        val userAgent = "Jsoup Testalot v0.1"
        session.userAgent(userAgent)
        session.url(CookieServlet.Companion.Url)

        // should have no cookies:
        val con1 = session.newRequest()
        val doc1 = con1.get()
        Assertions.assertEquals(0, doc1.select("table tr").size) // none sent to servlet

        // set the cookies
        val con2 = session.newRequest().data(CookieServlet.Companion.SetCookiesParam, "1")
        val doc2 = con2.get()
        Assertions.assertEquals(0, doc2.select("table tr").size) // none sent to servlet - we just got them!
        val cookies = con2.response().cookies() // simple cookie response, all named "One", so should be first sent
        Assertions.assertEquals(1, cookies.size)
        Assertions.assertEquals("Root", cookies["One"])

        // todo - interrogate cookie-store

        // check that they are sent and filtered to the right path
        val con3 = session.newRequest()
        val doc3 = con3.get()
        assertCookieServlet(doc3)
        val echo: Document = session.newRequest().url(EchoServlet.Companion.Url).get()
        assertEchoServlet(echo)
        Assertions.assertEquals(
            userAgent,
            SessionTest.Companion.keyText("User-Agent", echo)
        ) // check that customer user agent sent on session arrived

        // check that cookies aren't set out of the session
        val doc4: Document = newSession().url(CookieServlet.Companion.Url).get()
        Assertions.assertEquals(0, doc4.select("table tr").size) // none sent to servlet

        // check can add local ones also
        val doc5 = session.newRequest().cookie("Bar", "Qux").get()
        val doc5Bar: Elements = SessionTest.Companion.keyEls("Bar", doc5)
        Assertions.assertEquals("Qux", doc5Bar.first()!!.text())
    }

    // validate that only cookies set by cookie servlet get to the cookie servlet path
    private fun assertCookieServlet(doc: Document) {
        Assertions.assertEquals(2, doc.select("table tr").size) // two of three sent to servlet (/ and /CookieServlet)
        val doc3Els: Elements = SessionTest.Companion.keyEls("One", doc)
        Assertions.assertEquals(2, doc3Els.size)
        Assertions.assertEquals("CookieServlet", doc3Els[0].text()) // ordered by most specific path
        Assertions.assertEquals("Root", doc3Els[1].text()) // ordered by most specific path
    }

    // validate that only for echo servlet
    private fun assertEchoServlet(doc: Document) {
        val echoEls: Elements =
            SessionTest.Companion.keyEls("Cookie: One", doc) // two of three sent to servlet (/ and /EchoServlet)
        Assertions.assertEquals(2, echoEls.size)
        Assertions.assertEquals("EchoServlet", echoEls[0].text()) // ordered by most specific path - /Echo
        Assertions.assertEquals("Root", echoEls[1].text()) // ordered by most specific path - /
    }

    @Test
    @Throws(IOException::class)
    fun testPathScopedCookiesOnRedirect() {
        val session = newSession()
        val doc1: Document = session.newRequest()
            .url(CookieServlet.Companion.Url)
            .data(CookieServlet.Companion.LocationParam, EchoServlet.Companion.Url)
            .data(CookieServlet.Companion.SetCookiesParam, "1")
            .get()

        // we should be redirected to the echo servlet with cookies
        Assertions.assertEquals(EchoServlet.Companion.Url, doc1.location())
        assertEchoServlet(doc1) // checks we only have /echo cookies
        val doc2: Document = session.newRequest()
            .url(EchoServlet.Companion.Url)
            .get()
        assertEchoServlet(doc2) // test retained in session
        val doc3: Document = session.newRequest()
            .url(CookieServlet.Companion.Url)
            .get()
        assertCookieServlet(doc3) // and so were the /cookie cookies
    }

    @Test
    @Throws(IOException::class)
    fun testCanChangeParsers() {
        val session = newSession().parser(xmlParser())
        val xmlUrl: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val xmlVal = "<doc><val>One<val>Two</val>Three</val></doc>\n"
        val doc1 = session.newRequest().url(xmlUrl).get()
        Assertions.assertEquals(xmlVal, doc1.html()) // not HTML normed, used XML parser
        val doc2 = session.newRequest().parser(htmlParser()).url(xmlUrl).get()
        Assertions.assertTrue(doc2.html().startsWith("<html>"))
        val doc3 = session.newRequest().url(xmlUrl).get()
        Assertions.assertEquals(xmlVal, doc3.html()) // did not blow away xml default
    }

    companion object {
        @BeforeAll
        fun setUp() {
            TestServer.start()
        }

        private fun keyEls(key: String, doc: Document): Elements {
            return doc.select("th:contains($key) + td")
        }

        private fun keyText(key: String, doc: Document): String {
            return doc.selectFirst("th:contains($key) + td")!!.text()
        }
    }
}
