package net.dankito.ksoup.helper

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.helper.W3CDom.Companion.OutputHtml
import net.dankito.ksoup.helper.W3CDom.Companion.OutputXml
import net.dankito.ksoup.helper.W3CDom.Companion.asString
import net.dankito.ksoup.helper.W3CDom.Companion.convert
import net.dankito.ksoup.integration.ParseTest
import net.dankito.ksoup.nodes.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.*
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class W3CDomTest {
    @Test
    fun simpleConversion() {
        val html =
            "<html><head><title>W3c</title></head><body><p class='one' id=12>Text</p><!-- comment --><invalid>What<script>alert('!')"
        val doc = Jsoup.parse(html)
        val w3c = W3CDom()
        val wDoc = w3c.fromJsoup(doc)
        val meta = wDoc.getElementsByTagName("META")
        Assertions.assertEquals(0, meta.length)
        val out = asString(wDoc, OutputXml())
        val expected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>W3c</title></head><body><p class=\"one\" id=\"12\">Text</p><!-- comment --><invalid>What<script>alert('!')</script></invalid></body></html>"
        Assertions.assertEquals(expected, TextUtil.stripNewlines(out))
        val roundTrip = parseXml(out, true)
        Assertions.assertEquals("Text", roundTrip.getElementsByTagName("p").item(0).textContent)

        // check we can set properties
        val properties = OutputXml()
        properties[OutputKeys.INDENT] = "yes"
        val furtherOut = asString(wDoc, properties)
        Assertions.assertTrue(furtherOut.length > out.length) // wanted to assert formatting, but actual indentation is platform specific so breaks in CI
        val furtherExpected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>W3c</title></head><body><p class=\"one\" id=\"12\">Text</p><!-- comment --><invalid>What<script>alert('!')</script></invalid></body></html>"
        Assertions.assertEquals(
            furtherExpected,
            TextUtil.stripNewlines(furtherOut)
        ) // on windows, DOM will write newlines as \r\n
    }

    @Test
    fun namespacePreservation() {
        val `in`: File = ParseTest.getFile("/htmltests/namespaces.xhtml")
        val jsoupDoc = Jsoup.parse(`in`, "UTF-8")
        val doc: Document
        val jDom = W3CDom()
        doc = jDom.fromJsoup(jsoupDoc)
        val htmlEl = doc.childNodes.item(0)
        Assertions.assertEquals("http://www.w3.org/1999/xhtml", htmlEl.namespaceURI)
        Assertions.assertEquals("html", htmlEl.localName)
        Assertions.assertEquals("html", htmlEl.nodeName)

        // inherits default namespace
        val head = htmlEl.firstChild.nextSibling
        Assertions.assertEquals("http://www.w3.org/1999/xhtml", head.namespaceURI)
        Assertions.assertEquals("head", head.localName)
        Assertions.assertEquals("head", head.nodeName)
        val epubTitle = htmlEl.childNodes.item(3).childNodes.item(3)
        Assertions.assertEquals("Check", epubTitle.textContent)
        Assertions.assertEquals("http://www.idpf.org/2007/ops", epubTitle.namespaceURI)
        Assertions.assertEquals("title", epubTitle.localName)
        Assertions.assertEquals("epub:title", epubTitle.nodeName)
        val xSection = epubTitle.nextSibling.nextSibling
        Assertions.assertEquals("urn:test", xSection.namespaceURI)
        Assertions.assertEquals("section", xSection.localName)
        Assertions.assertEquals("x:section", xSection.nodeName)

        // https://github.com/jhy/jsoup/issues/977
        // does not keep last set namespace
        val svg = xSection.nextSibling.nextSibling
        Assertions.assertEquals("http://www.w3.org/2000/svg", svg.namespaceURI)
        Assertions.assertEquals("svg", svg.localName)
        Assertions.assertEquals("svg", svg.nodeName)
        val path = svg.childNodes.item(1)
        Assertions.assertEquals("http://www.w3.org/2000/svg", path.namespaceURI)
        Assertions.assertEquals("path", path.localName)
        Assertions.assertEquals("path", path.nodeName)
        val clip = path.childNodes.item(1)
        Assertions.assertEquals("http://example.com/clip", clip.namespaceURI)
        Assertions.assertEquals("clip", clip.localName)
        Assertions.assertEquals("clip", clip.nodeName)
        Assertions.assertEquals("456", clip.textContent)
        val picture = svg.nextSibling.nextSibling
        Assertions.assertEquals("http://www.w3.org/1999/xhtml", picture.namespaceURI)
        Assertions.assertEquals("picture", picture.localName)
        Assertions.assertEquals("picture", picture.nodeName)
        val img = picture.firstChild
        Assertions.assertEquals("http://www.w3.org/1999/xhtml", img.namespaceURI)
        Assertions.assertEquals("img", img.localName)
        Assertions.assertEquals("img", img.nodeName)
    }

    @Test
    fun handlesInvalidAttributeNames() {
        val html = "<html><head></head><body style=\"color: red\" \" name\"></body></html>"
        val jsoupDoc = Jsoup.parse(html)
        val body = jsoupDoc.select("body").first()
        Assertions.assertTrue(body!!.hasAttr("\"")) // actually an attribute with key '"'. Correct per HTML5 spec, but w3c xml dom doesn't dig it
        Assertions.assertTrue(body.hasAttr("name\""))
        val w3Doc = convert(jsoupDoc)
        val xml = asString(w3Doc, OutputXml())
        Assertions.assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head/><body name=\"\" style=\"color: red\"/></html>",
            xml
        )
    }

    @Test
    fun htmlInputDocMaintainsHtmlAttributeNames() {
        val html =
            "<!DOCTYPE html><html><head></head><body><p hành=\"1\" hình=\"2\">unicode attr names</p></body></html>"
        val jsoupDoc = Jsoup.parse(html)
        val w3Doc = convert(jsoupDoc)
        val out = asString(w3Doc, OutputHtml())
        val expected =
            "<!DOCTYPE html SYSTEM \"about:legacy-compat\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body><p hành=\"1\" hình=\"2\">unicode attr names</p></body></html>"
        Assertions.assertEquals(expected, TextUtil.stripNewlines(out))
    }

    @Test
    fun xmlInputDocMaintainsHtmlAttributeNames() {
        val html =
            "<!DOCTYPE html><html><head></head><body><p hành=\"1\" hình=\"2\">unicode attr names coerced</p></body></html>"
        val jsoupDoc = Jsoup.parse(html)
        jsoupDoc.outputSettings().syntax(net.dankito.ksoup.nodes.Document.OutputSettings.Syntax.xml)
        val w3Doc = convert(jsoupDoc)
        val out = asString(w3Doc, OutputHtml())
        val expected =
            "<!DOCTYPE html SYSTEM \"about:legacy-compat\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body><p hnh=\"2\">unicode attr names coerced</p></body></html>"
        Assertions.assertEquals(expected, TextUtil.stripNewlines(out))
    }

    @Test
    fun handlesInvalidTagAsText() {
        val jsoup = Jsoup.parse("<インセンティブで高収入！>Text <p>More</p>")
        val w3Doc = convert(jsoup)
        val xml = asString(w3Doc, OutputXml())
        Assertions.assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head/><body>&lt;インセンティブで高収入！&gt;Text <p>More</p></body></html>",
            xml
        )
    }

    @Test
    fun treatsUndeclaredNamespaceAsLocalName() {
        val html = "<fb:like>One</fb:like>"
        val doc = Jsoup.parse(html)
        val w3Doc = W3CDom().fromJsoup(doc)
        val htmlEl = w3Doc.firstChild
        Assertions.assertEquals("http://www.w3.org/1999/xhtml", htmlEl.namespaceURI)
        Assertions.assertEquals("html", htmlEl.localName)
        Assertions.assertEquals("html", htmlEl.nodeName)
        val fb = htmlEl.firstChild.nextSibling.firstChild
        Assertions.assertNull(fb.namespaceURI)
        Assertions.assertEquals("like", fb.localName)
        Assertions.assertEquals("fb:like", fb.nodeName)
    }

    @Test
    fun xmlnsXpathTest() {
        val w3c = W3CDom()
        var html = "<html><body><div>hello</div></body></html>"
        var dom = w3c.fromJsoup(Jsoup.parse(html))
        var nodeList = xpath(dom, "//*[local-name()=\"body\"]") // namespace aware; HTML namespace is default
        Assertions.assertEquals("div", nodeList?.item(0)?.localName)

        // default output is namespace aware, so query needs to be as well
        html = "<html xmlns='http://www.w3.org/1999/xhtml'><body id='One'><div>hello</div></body></html>"
        dom = w3c.fromJsoup(Jsoup.parse(html))
        nodeList = xpath(dom, "//body")
        Assertions.assertNull(nodeList) // no matches
        dom = w3c.fromJsoup(Jsoup.parse(html))
        nodeList = xpath(dom, "//*[local-name()=\"body\"]")
        Assertions.assertNotNull(nodeList)
        Assertions.assertEquals(1, nodeList?.length)
        Assertions.assertEquals("div", nodeList?.item(0)?.localName)
        Assertions.assertEquals("http://www.w3.org/1999/xhtml", nodeList?.item(0)?.namespaceURI)
        Assertions.assertNull(nodeList?.item(0)?.prefix)

        // get rid of the name space awareness
        val xml = asString(dom)
        dom = parseXml(xml, false)
        val item = xpath(dom, "//body") as Node
        Assertions.assertEquals("body", item.nodeName)
        Assertions.assertNull(item.namespaceURI)
        Assertions.assertNull(item.prefix)

        // put back, will get zero
        dom = parseXml(xml, true)
        nodeList = xpath(dom, "//body")
        Assertions.assertNull(nodeList)
    }

    @Test
    fun xhtmlNoNamespace() {
        val w3c = W3CDom()
        val html = "<html><body><div>hello</div></body></html>"
        w3c.namespaceAware(false)
        val dom = w3c.fromJsoup(Jsoup.parse(html))
        val nodeList = xpath(dom, "//body") // no namespace
        Assertions.assertEquals(1, nodeList?.length)
        Assertions.assertEquals("div", nodeList?.item(0)?.localName)
    }

    @Test
    fun canDisableNamespaces() {
        val w3c = W3CDom()
        Assertions.assertTrue(w3c.namespaceAware())
        w3c.namespaceAware(false)
        Assertions.assertFalse(w3c.namespaceAware())
        val html = "<html xmlns='http://www.w3.org/1999/xhtml'><body id='One'><div>hello</div></body></html>"
        val dom = w3c.fromJsoup(Jsoup.parse(html))
        val nodeList = xpath(dom, "//body") // no ns, so needs no prefix
        Assertions.assertEquals("div", nodeList?.item(0)?.localName)
    }

    private fun xpath(w3cDoc: Document, query: String): NodeList? {
        val xpath = XPathFactory.newInstance().newXPath().compile(query)
        return xpath.evaluate(w3cDoc, XPathConstants.NODE) as? NodeList
    }

    @Test
    fun testRoundTripDoctype() {
        // TODO - not super happy with this output - but plain DOM doesn't let it out, and don't want to rebuild the writer
        // because we have Saxon on the test classpath, the transformer will change to that, and so case may change (e.g. Java base in META, Saxon is meta for HTML)
        val base = "<!DOCTYPE html><p>One</p>"
        assertEqualsIgnoreCase(
            "<!DOCTYPE html SYSTEM \"about:legacy-compat\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body><p>One</p></body></html>",
            output(base, true)
        )
        assertEqualsIgnoreCase(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html SYSTEM \"about:legacy-compat\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head/><body><p>One</p></body></html>",
            output(base, false)
        )
        val publicDoc =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
        assertEqualsIgnoreCase(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body></body></html>",
            output(publicDoc, true)
        )
        // different impls will have different XML formatting. OpenJDK 13 default gives this: <body /> but others have <body/>, so just check start
        Assertions.assertTrue(
            output(
                publicDoc,
                false
            ).startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC")
        )
        val systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">"
        assertEqualsIgnoreCase(
            "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body></body></html>",
            output(systemDoc, true)
        )
        assertEqualsIgnoreCase(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html SYSTEM \"exampledtdfile.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head/><body/></html>",
            output(systemDoc, false)
        )
        val legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">"
        assertEqualsIgnoreCase(
            "<!DOCTYPE html SYSTEM \"about:legacy-compat\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body></body></html>",
            output(legacyDoc, true)
        )
        assertEqualsIgnoreCase(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html SYSTEM \"about:legacy-compat\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head/><body/></html>",
            output(legacyDoc, false)
        )
        val noDoctype = "<p>One</p>"
        assertEqualsIgnoreCase(
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body><p>One</p></body></html>",
            output(noDoctype, true)
        )
        assertEqualsIgnoreCase(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head/><body><p>One</p></body></html>",
            output(noDoctype, false)
        )
    }

    private fun output(`in`: String, modeHtml: Boolean): String {
        val jdoc = Jsoup.parse(`in`)
        val w3c = convert(jdoc)
        val properties = if (modeHtml) OutputHtml() else OutputXml()
        return TextUtil.normalizeSpaces(asString(w3c, properties))
    }

    private fun assertEqualsIgnoreCase(want: String, have: String) {
        Assertions.assertEquals(want.lowercase(), have.lowercase())
    }

    @Test
    fun canOutputHtmlWithoutNamespace() {
        val html = "<p>One</p>"
        val jdoc = Jsoup.parse(html)
        val w3c = W3CDom()
        w3c.namespaceAware(false)
        val asHtml = asString(w3c.fromJsoup(jdoc), OutputHtml())
        val asXtml = asString(w3c.fromJsoup(jdoc), OutputXml())
        assertEqualsIgnoreCase(
            "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head><body><p>one</p></body></html>",
            asHtml
        )
        assertEqualsIgnoreCase(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html><head/><body><p>One</p></body></html>",
            asXtml
        )
    }

    @Test
    fun convertsElementsAndMaintainsSource() {
        val jdoc = Jsoup.parse("<body><div><p>One</div><div><p>Two")
        val w3CDom = W3CDom()
        val jDiv = jdoc.selectFirst("div")
        Assertions.assertNotNull(jDiv)
        val doc = w3CDom.fromJsoup(jDiv!!)
        val div = w3CDom.contextNode(doc)
        Assertions.assertEquals("div", div.localName)
        Assertions.assertEquals(jDiv, div.getUserData(W3CDom.SourceProperty))
        val textNode = div.firstChild.firstChild
        Assertions.assertEquals("One", textNode.textContent)
        Assertions.assertEquals(Node.TEXT_NODE, textNode.nodeType)
        val jText = jDiv.childNode(0).childNode(0) as TextNode
        Assertions.assertEquals(jText, textNode.getUserData(W3CDom.SourceProperty))
    }

    companion object {
        private fun parseXml(xml: String, nameSpaceAware: Boolean): Document {
            return try {
                val factory = DocumentBuilderFactory.newInstance()
                factory.isNamespaceAware = nameSpaceAware
                val builder = factory.newDocumentBuilder()
                builder.setEntityResolver { publicId: String?, systemId: String ->
                    if (systemId.contains("about:legacy-compat")) { // <!doctype html>
                        return@setEntityResolver InputSource(StringReader(""))
                    } else {
                        return@setEntityResolver null
                    }
                }
                val dom = builder.parse(ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8)))
                dom.normalizeDocument()
                dom
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }
    }
}
