package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.integration.ParseTest
import net.dankito.ksoup.parser.ParseSettings
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * Tests for Document.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class DocumentTest {
    @Test
    fun setTextPreservesDocumentStructure() {
        val doc = Jsoup.parse("<p>Hello</p>")
        doc.text("Replaced")
        Assertions.assertEquals("Replaced", doc.text())
        Assertions.assertEquals("Replaced", doc.body().text())
        Assertions.assertEquals(1, doc.select("head").size)
    }

    @Test
    fun testTitles() {
        val noTitle = Jsoup.parse("<p>Hello</p>")
        val withTitle = Jsoup.parse("<title>First</title><title>Ignore</title><p>Hello</p>")
        Assertions.assertEquals("", noTitle.title())
        noTitle.title("Hello")
        Assertions.assertEquals("Hello", noTitle.title())
        Assertions.assertEquals("Hello", noTitle.select("title").first()!!.text())
        Assertions.assertEquals("First", withTitle.title())
        withTitle.title("Hello")
        Assertions.assertEquals("Hello", withTitle.title())
        Assertions.assertEquals("Hello", withTitle.select("title").first()!!.text())
        val normaliseTitle = Jsoup.parse("<title>   Hello\nthere   \n   now   \n")
        Assertions.assertEquals("Hello there now", normaliseTitle.title())
    }

    @Test
    fun testOutputEncoding() {
        val doc = Jsoup.parse("<p title=π>π & < > </p>")
        // default is utf-8
        Assertions.assertEquals("<p title=\"π\">π &amp; &lt; &gt;</p>", doc.body().html())
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.name())
        doc.outputSettings().charset("ascii")
        Assertions.assertEquals(Entities.EscapeMode.base, doc.outputSettings().escapeMode())
        Assertions.assertEquals("<p title=\"&#x3c0;\">&#x3c0; &amp; &lt; &gt;</p>", doc.body().html())
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended)
        Assertions.assertEquals("<p title=\"&pi;\">&pi; &amp; &lt; &gt;</p>", doc.body().html())
    }

    @Test
    fun testXhtmlReferences() {
        val doc = Jsoup.parse("&lt; &gt; &amp; &quot; &apos; &times;")
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        Assertions.assertEquals("&lt; &gt; &amp; \" ' ×", doc.body().html())
    }

    @Test
    fun testNormalisesStructure() {
        val doc =
            Jsoup.parse("<html><head><script>one</script><noscript><p>two</p></noscript></head><body><p>three</p></body><p>four</p></html>")
        Assertions.assertEquals(
            "<html><head><script>one</script><noscript>&lt;p&gt;two</noscript></head><body><p>three</p><p>four</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun accessorsWillNormalizeStructure() {
        val doc = Document("")
        Assertions.assertEquals("", doc.html())
        val body = doc.body()
        Assertions.assertEquals("body", body.tagName())
        val head = doc.head()
        Assertions.assertEquals("head", head.tagName())
        Assertions.assertEquals("<html><head></head><body></body></html>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun accessorsAreCaseInsensitive() {
        val parser = htmlParser().settings(ParseSettings.preserveCase)
        val doc =
            parser.parseInput("<!DOCTYPE html><HTML><HEAD><TITLE>SHOUTY</TITLE></HEAD><BODY>HELLO</BODY></HTML>", "")
        val body = doc.body()
        Assertions.assertEquals("BODY", body.tagName())
        Assertions.assertEquals("body", body.normalName())
        val head = doc.head()
        Assertions.assertEquals("HEAD", head.tagName())
        Assertions.assertEquals("body", body.normalName())
        val root = doc.selectFirst("html")
        Assertions.assertEquals("HTML", root!!.tagName())
        Assertions.assertEquals("html", root.normalName())
        Assertions.assertEquals("SHOUTY", doc.title())
    }

    @Test
    fun testClone() {
        val doc = Jsoup.parse("<title>Hello</title> <p>One<p>Two")
        val clone = doc.clone()
        Assertions.assertEquals(
            "<html><head><title>Hello</title></head><body><p>One</p><p>Two</p></body></html>",
            TextUtil.stripNewlines(clone.html())
        )
        clone.title("Hello there")
        clone.expectFirst("p").text("One more").attr("id", "1")
        Assertions.assertEquals(
            "<html><head><title>Hello there</title></head><body><p id=\"1\">One more</p><p>Two</p></body></html>",
            TextUtil.stripNewlines(clone.html())
        )
        Assertions.assertEquals(
            "<html><head><title>Hello</title></head><body><p>One</p><p>Two</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testBasicIndent() {
        val doc = Jsoup.parse("<title>Hello</title> <p>One<p>Two")
        val expect =
            "<html>\n <head>\n  <title>Hello</title>\n </head>\n <body>\n  <p>One</p>\n  <p>Two</p>\n </body>\n</html>"
        Assertions.assertEquals(expect, doc.html())
    }

    @Test
    fun testClonesDeclarations() {
        val doc = Jsoup.parse("<!DOCTYPE html><html><head><title>Doctype test")
        val clone = doc.clone()
        Assertions.assertEquals(doc.html(), clone.html())
        Assertions.assertEquals(
            "<!doctype html><html><head><title>Doctype test</title></head><body></body></html>",
            TextUtil.stripNewlines(clone.html())
        )
    }

    @Test
    fun testLocation() {
        // tests location vs base href
        val `in` = ParseTest.getFile("/htmltests/basehref.html")
        val doc = Jsoup.parse(`in`, "UTF-8", "http://example.com/")
        val location = doc.location()
        val baseUri = doc.baseUri()
        Assertions.assertEquals("http://example.com/", location)
        Assertions.assertEquals("https://example.com/path/file.html?query", baseUri)
        Assertions.assertEquals("./anotherfile.html", doc.expectFirst("a").attr("href"))
        Assertions.assertEquals("https://example.com/path/anotherfile.html", doc.expectFirst("a").attr("abs:href"))
    }

    @Test
    fun testLocationFromString() {
        val doc = Jsoup.parse("<p>Hello")
        Assertions.assertEquals("", doc.location())
    }

    @Test
    fun testHtmlAndXmlSyntax() {
        val h = "<!DOCTYPE html><body><img async checked='checked' src='&<>\"'>&lt;&gt;&amp;&quot;<foo />bar"
        val doc = Jsoup.parse(h)
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.html)
        Assertions.assertEquals(
            """<!doctype html>
<html>
 <head></head>
 <body>
  <img async checked src="&amp;<>&quot;">&lt;&gt;&amp;"<foo />bar
 </body>
</html>""", doc.html()
        )
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        Assertions.assertEquals(
            """<!DOCTYPE html>
<html>
 <head></head>
 <body>
  <img async="" checked="checked" src="&amp;&lt;>&quot;" />&lt;&gt;&amp;"<foo />bar
 </body>
</html>""", doc.html()
        )
    }

    @Test
    fun htmlParseDefaultsToHtmlOutputSyntax() {
        val doc = Jsoup.parse("x")
        Assertions.assertEquals(Document.OutputSettings.Syntax.html, doc.outputSettings().syntax())
    }

    @Test
    fun testHtmlAppendable() {
        val htmlContent = "<html><head><title>Hello</title></head><body><p>One</p><p>Two</p></body></html>"
        val document = Jsoup.parse(htmlContent)
        val outputSettings = Document.OutputSettings()
        outputSettings.prettyPrint(false)
        document.outputSettings(outputSettings)
        Assertions.assertEquals(htmlContent, document.html(StringWriter()).toString())
    }

    @Test
    fun testOverflowClone() {
        val sb = StringBuilder()
        sb.append("<head><base href='https://jsoup.org/'>")
        for (i in 0..99999) {
            sb.append("<div>")
        }
        sb.append("<p>Hello <a href='/example.html'>there</a>")
        val doc = Jsoup.parse(sb.toString())
        val expectedLink = "https://jsoup.org/example.html"
        Assertions.assertEquals(expectedLink, doc.selectFirst("a")!!.attr("abs:href"))
        val clone = doc.clone()
        doc.hasSameValue(clone)
        Assertions.assertEquals(expectedLink, clone.selectFirst("a")!!.attr("abs:href"))
    }

    @Test
    fun DocumentsWithSameContentAreEqual() {
        val docA = Jsoup.parse("<div/>One")
        val docB = Jsoup.parse("<div/>One")
        val docC = Jsoup.parse("<div/>Two")
        Assertions.assertNotEquals(docA, docB)
        Assertions.assertEquals(docA, docA)
        Assertions.assertEquals(docA.hashCode(), docA.hashCode())
        Assertions.assertNotEquals(docA.hashCode(), docC.hashCode())
    }

    @Test
    fun DocumentsWithSameContentAreVerifiable() {
        val docA = Jsoup.parse("<div/>One")
        val docB = Jsoup.parse("<div/>One")
        val docC = Jsoup.parse("<div/>Two")
        Assertions.assertTrue(docA.hasSameValue(docB))
        Assertions.assertFalse(docA.hasSameValue(docC))
    }

    @Test
    fun testMetaCharsetUpdateUtf8() {
        val doc = createHtmlDocument("changeThis")
        doc.updateMetaCharsetElement(true)
        doc.charset(Charset.forName(charsetUtf8))
        val htmlCharsetUTF8 = """<html>
 <head>
  <meta charset="$charsetUtf8">
 </head>
 <body></body>
</html>"""
        Assertions.assertEquals(htmlCharsetUTF8, doc.toString())
        val selectedElement = doc.select("meta[charset]").first()
        Assertions.assertEquals(charsetUtf8, doc.charset()!!.name())
        Assertions.assertEquals(charsetUtf8, selectedElement!!.attr("charset"))
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset())
    }

    @Test
    fun testMetaCharsetUpdateIso8859() {
        val doc = createHtmlDocument("changeThis")
        doc.updateMetaCharsetElement(true)
        doc.charset(Charset.forName(charsetIso8859))
        val htmlCharsetISO = """<html>
 <head>
  <meta charset="$charsetIso8859">
 </head>
 <body></body>
</html>"""
        Assertions.assertEquals(htmlCharsetISO, doc.toString())
        val selectedElement = doc.select("meta[charset]").first()
        Assertions.assertEquals(charsetIso8859, doc.charset()!!.name())
        Assertions.assertEquals(charsetIso8859, selectedElement!!.attr("charset"))
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset())
    }

    @Test
    fun testMetaCharsetUpdateNoCharset() {
        val docNoCharset = Document.createShell("")
        docNoCharset.updateMetaCharsetElement(true)
        docNoCharset.charset(Charset.forName(charsetUtf8))
        Assertions.assertEquals(
            charsetUtf8, docNoCharset.select("meta[charset]").first()!!
                .attr("charset")
        )
        val htmlCharsetUTF8 = """<html>
 <head>
  <meta charset="$charsetUtf8">
 </head>
 <body></body>
</html>"""
        Assertions.assertEquals(htmlCharsetUTF8, docNoCharset.toString())
    }

    @Test
    fun testMetaCharsetUpdateDisabled() {
        val docDisabled = Document.createShell("")
        val htmlNoCharset = """<html>
 <head></head>
 <body></body>
</html>"""
        Assertions.assertEquals(htmlNoCharset, docDisabled.toString())
        Assertions.assertNull(docDisabled.select("meta[charset]").first())
    }

    @Test
    fun testMetaCharsetUpdateDisabledNoChanges() {
        val doc = createHtmlDocument("dontTouch")
        val htmlCharset = """<html>
 <head>
  <meta charset="dontTouch">
  <meta name="charset" content="dontTouch">
 </head>
 <body></body>
</html>"""
        Assertions.assertEquals(htmlCharset, doc.toString())
        var selectedElement = doc.select("meta[charset]").first()
        Assertions.assertNotNull(selectedElement)
        Assertions.assertEquals("dontTouch", selectedElement!!.attr("charset"))
        selectedElement = doc.select("meta[name=charset]").first()
        Assertions.assertNotNull(selectedElement)
        Assertions.assertEquals("dontTouch", selectedElement!!.attr("content"))
    }

    @Test
    fun testMetaCharsetUpdateEnabledAfterCharsetChange() {
        val doc = createHtmlDocument("dontTouch")
        doc.charset(Charset.forName(charsetUtf8))
        val selectedElement = doc.select("meta[charset]").first()
        Assertions.assertEquals(charsetUtf8, selectedElement!!.attr("charset"))
        Assertions.assertTrue(doc.select("meta[name=charset]").isEmpty())
    }

    @Test
    fun testMetaCharsetUpdateCleanup() {
        val doc = createHtmlDocument("dontTouch")
        doc.updateMetaCharsetElement(true)
        doc.charset(Charset.forName(charsetUtf8))
        val htmlCharsetUTF8 = """<html>
 <head>
  <meta charset="$charsetUtf8">
 </head>
 <body></body>
</html>"""
        Assertions.assertEquals(htmlCharsetUTF8, doc.toString())
    }

    @Test
    fun testMetaCharsetUpdateXmlUtf8() {
        val doc = createXmlDocument("1.0", "changeThis", true)
        doc.updateMetaCharsetElement(true)
        doc.charset(Charset.forName(charsetUtf8))
        val xmlCharsetUTF8 = """<?xml version="1.0" encoding="$charsetUtf8"?>
<root>
 node
</root>"""
        Assertions.assertEquals(xmlCharsetUTF8, doc.toString())
        val selectedNode = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals(charsetUtf8, doc.charset()!!.name())
        Assertions.assertEquals(charsetUtf8, selectedNode.attr("encoding"))
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset())
    }

    @Test
    fun testMetaCharsetUpdateXmlIso8859() {
        val doc = createXmlDocument("1.0", "changeThis", true)
        doc.updateMetaCharsetElement(true)
        doc.charset(Charset.forName(charsetIso8859))
        val xmlCharsetISO = """<?xml version="1.0" encoding="$charsetIso8859"?>
<root>
 node
</root>"""
        Assertions.assertEquals(xmlCharsetISO, doc.toString())
        val selectedNode = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals(charsetIso8859, doc.charset()!!.name())
        Assertions.assertEquals(charsetIso8859, selectedNode.attr("encoding"))
        Assertions.assertEquals(doc.charset(), doc.outputSettings().charset())
    }

    @Test
    fun testMetaCharsetUpdateXmlNoCharset() {
        val doc = createXmlDocument("1.0", "none", false)
        doc.updateMetaCharsetElement(true)
        doc.charset(Charset.forName(charsetUtf8))
        val xmlCharsetUTF8 = """<?xml version="1.0" encoding="$charsetUtf8"?>
<root>
 node
</root>"""
        Assertions.assertEquals(xmlCharsetUTF8, doc.toString())
        val selectedNode = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals(charsetUtf8, selectedNode.attr("encoding"))
    }

    @Test
    fun testMetaCharsetUpdateXmlDisabled() {
        val doc = createXmlDocument("none", "none", false)
        val xmlNoCharset = """<root>
 node
</root>"""
        Assertions.assertEquals(xmlNoCharset, doc.toString())
    }

    @Test
    fun testMetaCharsetUpdateXmlDisabledNoChanges() {
        val doc = createXmlDocument("dontTouch", "dontTouch", true)
        val xmlCharset = """<?xml version="dontTouch" encoding="dontTouch"?>
<root>
 node
</root>"""
        Assertions.assertEquals(xmlCharset, doc.toString())
        val selectedNode = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals("dontTouch", selectedNode.attr("encoding"))
        Assertions.assertEquals("dontTouch", selectedNode.attr("version"))
    }

    @Test
    fun testMetaCharsetUpdatedDisabledPerDefault() {
        val doc = createHtmlDocument("none")
        Assertions.assertFalse(doc.updateMetaCharsetElement())
    }

    private fun createHtmlDocument(charset: String): Document {
        val doc = Document.createShell("")
        doc.head().appendElement("meta").attr("charset", charset)
        doc.head().appendElement("meta").attr("name", "charset").attr("content", charset)
        return doc
    }

    private fun createXmlDocument(version: String, charset: String, addDecl: Boolean): Document {
        val doc = Document("")
        doc.appendElement("root").text("node")
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        if (addDecl) {
            val decl = XmlDeclaration("xml", false)
            decl.attr("version", version)
            decl.attr("encoding", charset)
            doc.prependChild(decl)
        }
        return doc
    }

    @Test
    @Throws(Exception::class)
    fun testShiftJisRoundtrip() {
        val input = ("<html>"
                + "<head>"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=Shift_JIS\" />"
                + "</head>"
                + "<body>"
                + "before&nbsp;after"
                + "</body>"
                + "</html>")
        val `is` = ByteArrayInputStream(input.toByteArray(StandardCharsets.US_ASCII))
        val doc = Jsoup.parse(`is`, null, "http://example.com")
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        val output = String(doc.html().toByteArray(doc.outputSettings().charset()!!), doc.outputSettings().charset()!!)
        Assertions.assertFalse(output.contains("?"), "Should not have contained a '?'.")
        Assertions.assertTrue(
            output.contains("&#xa0;") || output.contains("&nbsp;"),
            "Should have contained a '&#xa0;' or a '&nbsp;'."
        )
    }

    @Test
    @Throws(InterruptedException::class)
    fun parseAndHtmlOnDifferentThreads() {
        val html = "<p>Alrighty then it's not \uD83D\uDCA9. <span>Next</span></p>" // 💩
        val asci = "<p>Alrighty then it's not &#x1f4a9;. <span>Next</span></p>"
        val doc = Jsoup.parse(html)
        val out = arrayOfNulls<String>(1)
        val p = doc.select("p")
        Assertions.assertEquals(html, p.outerHtml())
        val thread = Thread {
            out[0] = p.outerHtml()
            doc.outputSettings().charset(StandardCharsets.US_ASCII)
        }
        thread.start()
        thread.join()
        Assertions.assertEquals(html, out[0])
        Assertions.assertEquals(StandardCharsets.US_ASCII, doc.outputSettings().charset())
        Assertions.assertEquals(asci, p.outerHtml())
    }

    @Test
    fun testDocumentTypeGet() {
        val html = "\n\n<!-- comment -->  <!doctype html><p>One</p>"
        val doc = Jsoup.parse(html)
        val documentType = doc.documentType()
        Assertions.assertNotNull(documentType)
        Assertions.assertEquals("html", documentType!!.name())
    }

    @Test
    fun framesetSupportsBodyMethod() {
        val html = "<html><head><title>Frame Test</title></head><frameset id=id><frame src=foo.html></frameset>"
        val doc = Jsoup.parse(html)
        val head = doc.head()
        Assertions.assertNotNull(head)
        Assertions.assertEquals("Frame Test", doc.title())

        // Frameset docs per html5 spec have no body element - but instead a frameset elelemt
        Assertions.assertNull(doc.selectFirst("body"))
        val frameset = doc.selectFirst("frameset")
        Assertions.assertNotNull(frameset)

        // the body() method returns body or frameset and does not otherwise modify the document
        // doing it in body() vs parse keeps the html close to original for round-trip option
        val body = doc.body()
        Assertions.assertNotNull(body)
        Assertions.assertSame(frameset, body)
        Assertions.assertEquals("frame", body.child(0).tagName())
        Assertions.assertNull(doc.selectFirst("body")) // did not vivify a body element
        val expected = """<html>
 <head>
  <title>Frame Test</title>
 </head>
 <frameset id="id">
  <frame src="foo.html">
 </frameset>
</html>"""
        Assertions.assertEquals(expected, doc.html())
    }

    @Test
    fun forms() {
        val html = "<body><form id=1><input name=foo></form><form id=2><input name=bar>"
        val doc = Jsoup.parse(html)
        val forms = doc.forms()
        Assertions.assertEquals(2, forms.size)
        val form = forms[1]
        Assertions.assertEquals(1, form.elements().size)
        Assertions.assertEquals("bar", form.elements().first()!!.attr("name"))
        val emptyHtml = "<body>"
        val emptyDoc = Jsoup.parse(emptyHtml)
        Assertions.assertEquals(0, emptyDoc.forms().size)
    }

    @Test
    fun expectForm() {
        val html = "<body><div name=form></div><form id=1 name=form><input name=foo></form><form id=2><input name=bar>"
        val doc = Jsoup.parse(html)

        // test finds first <form>
        val formEl1 = doc.expectForm("[name=form]")
        Assertions.assertEquals("1", formEl1!!.id()) // and not the div
        val formEl2 = doc.expectForm("form")
        Assertions.assertEquals("1", formEl2!!.id())
        val formEl3 = doc.expectForm("form:has([name=bar])")
        Assertions.assertEquals("2", formEl3!!.id())
        var threw = false
        try {
            val nix = doc.expectForm("div")
        } catch (e: IllegalArgumentException) {
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    companion object {
        private const val charsetUtf8 = "UTF-8"
        private const val charsetIso8859 = "ISO-8859-1"
    }
}
