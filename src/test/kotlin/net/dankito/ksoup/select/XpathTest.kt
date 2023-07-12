package net.dankito.ksoup.select

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.helper.W3CDom
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import javax.xml.xpath.*

class XpathTest {
    @Test
    fun supportsXpath() {
        val html = "<body><div><p>One</div><div><p>Two</div><div>Three</div>"
        val doc = Jsoup.parse(html)
        val els = doc.selectXpath("//div/p")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("One", els[0].text())
        Assertions.assertEquals("Two", els[1].text())
    }

    @Test
    fun supportsXpathFromElement() {
        val html = "<body><div><p>One</div><div><p>Two</div><div>Three</div>"
        val doc = Jsoup.parse(html)
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val w3cDiv = div!!.selectXpath(".").first() // self
        Assertions.assertSame(div, w3cDiv)
        val els = div.selectXpath("p")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("One", els[0].text())
        Assertions.assertEquals("p", els[0].tagName())
        Assertions.assertEquals(1, div.selectXpath("//body").size) // the whole document is visible on the div context
        Assertions.assertEquals(1, doc.selectXpath("//body").size)
    }

    @Test
    fun emptyElementsIfNoResults() {
        val doc = Jsoup.parse("<p>One<p>Two")
        Assertions.assertEquals(0, doc.selectXpath("//div").size)
    }

    @Test
    fun throwsSelectException() {
        val doc = Jsoup.parse("<p>One<p>Two")
        var threw = false
        try {
            doc.selectXpath("//???")
        } catch (e: Selector.SelectorParseException) {
            threw = true
            // checks exception message within jsoup's control, rest may be JDK impl specific
            // was - Could not evaluate XPath query [//???]: javax.xml.transform.TransformerException: A location step was expected following the '/' or '//' token.
            Assertions.assertTrue(e.message!!.startsWith("Could not evaluate XPath query [//???]:"))

            // check we have a root cause
            val cause = e.cause
            Assertions.assertNotNull(cause)
            Assertions.assertNotSame(cause, e)
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun supportsLocalname() {
        val xhtml = "<html xmlns='http://www.w3.org/1999/xhtml'><body id='One'><div>hello</div></body></html>"
        val doc = Jsoup.parse(xhtml, xmlParser())
        val elements = doc.selectXpath("//*[local-name()='body']")
        Assertions.assertEquals(1, elements.size)
        Assertions.assertEquals("One", elements.first()!!.id())
    }

    @Test
    fun canDitchNamespaces() {
        val xhtml = "<html xmlns='http://www.w3.org/1999/xhtml'><body id='One'><div>hello</div></body></html>"
        val doc = Jsoup.parse(xhtml, xmlParser())
        doc.select("[xmlns]").removeAttr("xmlns")
        var elements = doc.selectXpath("//*[local-name()='body']")
        Assertions.assertEquals(1, elements.size)
        elements = doc.selectXpath("//body")
        Assertions.assertEquals(1, elements.size)
        Assertions.assertEquals("One", elements.first()!!.id())
    }

    @ParameterizedTest
    @MethodSource("provideEvaluators")
    fun cssAndXpathEquivalents(doc: Document, css: String?, xpath: String?) {
        val fromCss = doc.select(css!!)
        val fromXpath = doc.selectXpath(xpath!!)
        Assertions.assertTrue(fromCss.size >= 1)
        Assertions.assertTrue(fromXpath.size >= 1)
        // tests same size, order, and contents
        Assertions.assertEquals(fromCss, fromXpath)
    }

    @Test
    fun canSelectTextNodes() {
        val html = "<div><p>One<p><a>Two</a><p>Three and some more"
        val doc = Jsoup.parse(html)

        //  as text nodes:
        val text = doc.selectXpath("//body//p//text()", TextNode::class.java)
        Assertions.assertEquals(3, text.size)
        Assertions.assertEquals("One", text[0].text())
        Assertions.assertEquals("Two", text[1].text())
        Assertions.assertEquals("Three and some more", text[2].text())

        //  as just nodes:
        val nodes = doc.selectXpath("//body//p//text()", Node::class.java)
        Assertions.assertEquals(3, nodes.size)
        Assertions.assertEquals("One", nodes[0].outerHtml())
        Assertions.assertEquals("Two", nodes[1].outerHtml())
        Assertions.assertEquals("Three and some more", nodes[2].outerHtml())
    }

    @Test
    fun selectByAttribute() {
        val doc = Jsoup.parse("<p><a href='/foo'>Foo</a><a href='/bar'>Bar</a><a>None</a>")
        val hrefs = doc.selectXpath("//a[@href]").eachAttr("href")
        Assertions.assertEquals(2, hrefs.size)
        Assertions.assertEquals("/foo", hrefs[0])
        Assertions.assertEquals("/bar", hrefs[1])
    }

    @Test
    fun selectOutsideOfElementTree() {
        val doc = Jsoup.parse("<p>One<p>Two<p>Three")
        val ps = doc.selectXpath("//p")
        Assertions.assertEquals(3, ps.size)
        val p1 = ps[0]
        Assertions.assertEquals("One", p1.text())
        val sibs = p1.selectXpath("following-sibling::p")
        Assertions.assertEquals(2, sibs.size)
        Assertions.assertEquals("Two", sibs[0].text())
        Assertions.assertEquals("Three", sibs[1].text())
    }

    @Test
    fun selectAncestorsOnContextElement() {
        // https://github.com/jhy/jsoup/issues/1652
        val doc = Jsoup.parse("<div><p>Hello")
        val p = doc.selectFirst("p")
        Assertions.assertNotNull(p)
        val chain = p!!.selectXpath("ancestor-or-self::*")
        Assertions.assertEquals(4, chain.size)
        Assertions.assertEquals("html", chain[0].tagName())
        Assertions.assertEquals("p", chain[3].tagName())
    }

    @Test
    fun canSupplyAlternateFactoryImpl() {
        // previously we had a test to load Saxon and do an XPath 2.0 query. But we know Saxon works and so that's
        // redundant - really just need to test that an alternate XPath factory can be used
        System.setProperty(W3CDom.XPathFactoryProperty, AlternateXpathFactory::class.java.name)
        val xhtml = "<html xmlns='http://www.w3.org/1999/xhtml'><body id='One'><div>hello</div></body></html>"
        var threw = false
        try {
            val doc = Jsoup.parse(xhtml, xmlParser())
            val elements = doc.selectXpath("//*:body")
        } catch (e: IllegalArgumentException) {
            Assertions.assertTrue(e.message!!.contains("Sorry, no can do!"))
            threw = true
        }
        Assertions.assertTrue(threw)
        System.clearProperty(W3CDom.XPathFactoryProperty)
    }

    @Test
    fun notNamespaceAware() {
        val xhtml = "<html xmlns='http://www.w3.org/1999/xhtml'><body id='One'><div>hello</div></body></html>"
        val doc = Jsoup.parse(xhtml, xmlParser())
        val elements = doc.selectXpath("//body")
        Assertions.assertEquals(1, elements.size)
        Assertions.assertEquals("One", elements.first()!!.id())
    }

    @Test
    fun supportsPrefixes() {
        // example from https://www.w3.org/TR/xml-names/
        val xml = """<?xml version="1.0"?>
<bk:book xmlns:bk='urn:loc.gov:books'
         xmlns:isbn='urn:ISBN:0-395-36341-6'>
    <bk:title>Cheaper by the Dozen</bk:title>
    <isbn:number>1568491379</isbn:number>
</bk:book>"""
        val doc = Jsoup.parse(xml, xmlParser())

        //Elements elements = doc.selectXpath("//bk:book/bk:title");
        val elements = doc.selectXpath("//book/title")
        Assertions.assertEquals(1, elements.size)
        Assertions.assertEquals("Cheaper by the Dozen", elements.first()!!.text())

        // with prefix
        val byPrefix = doc.selectXpath("//*[name()='bk:book']/*[name()='bk:title']")
        Assertions.assertEquals(1, byPrefix.size)
        Assertions.assertEquals("Cheaper by the Dozen", byPrefix.first()!!.text())
        val byLocalName = doc.selectXpath("//*[local-name()='book']/*[local-name()='title']")
        Assertions.assertEquals(1, byLocalName.size)
        Assertions.assertEquals("Cheaper by the Dozen", byLocalName.first()!!.text())
        val isbn = doc.selectXpath("//book/number")
        Assertions.assertEquals(1, isbn.size)
        Assertions.assertEquals("1568491379", isbn.first()!!.text())
    }

    // minimal, no-op implementation class to verify users can load a factory to support XPath 2.0 etc
    class AlternateXpathFactory : XPathFactory() {
        override fun isObjectModelSupported(objectModel: String): Boolean {
            return true
        }

        @Throws(XPathFactoryConfigurationException::class)
        override fun setFeature(name: String, value: Boolean) {
        }

        @Throws(XPathFactoryConfigurationException::class)
        override fun getFeature(name: String): Boolean {
            return true
        }

        override fun setXPathVariableResolver(resolver: XPathVariableResolver) {}
        override fun setXPathFunctionResolver(resolver: XPathFunctionResolver) {}
        override fun newXPath(): XPath {
            throw IllegalArgumentException("Sorry, no can do!")
        }
    }

    companion object {
        @JvmStatic
        private fun provideEvaluators(): Stream<Arguments> {
            val html = "<div id=1><div id=2><p class=foo>Hello</p></div></div><DIV id=3>"
            val doc = Jsoup.parse(html)
            return Stream.of(
                Arguments.of(doc, "DIV", "//div"),
                Arguments.of(doc, "div > p.foo", "//div/p[@class]"),
                Arguments.of(doc, "div + div", "//div/following-sibling::div[1]"),
                Arguments.of(doc, "p:containsOwn(Hello)", "//p[contains(text(),\"Hello\")]")
            )
        }
    }
}
