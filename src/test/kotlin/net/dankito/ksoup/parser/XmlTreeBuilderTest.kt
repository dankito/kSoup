package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.nodes.Document.Companion.createShell
import net.dankito.ksoup.parser.*
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import net.dankito.ksoup.parser.Parser.Companion.parseXmlFragment
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.*
import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

/**
 * Tests XmlTreeBuilder.
 *
 * @author Jonathan Hedley
 */
class XmlTreeBuilderTest {
    @Test
    fun testSimpleXmlParse() {
        val xml = "<doc id=2 href='/bar'>Foo <br /><link>One</link><link>Two</link></doc>"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        Assertions.assertEquals(
            "<doc id=\"2\" href=\"/bar\">Foo <br /><link>One</link><link>Two</link></doc>",
            TextUtil.stripNewlines(doc.html())
        )
        Assertions.assertEquals(doc.getElementById("2")!!.absUrl("href"), "http://foo.com/bar")
    }

    @Test
    fun testPopToClose() {
        // test: </val> closes Two, </bar> ignored
        val xml = "<doc><val>One<val>Two</val></bar>Three</doc>"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testCommentAndDocType() {
        val xml = "<!DOCTYPE HTML><!-- a comment -->One <qux />Two"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        Assertions.assertEquals(
            "<!DOCTYPE HTML><!-- a comment -->One <qux />Two",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testSupplyParserToJsoupClass() {
        val xml = "<doc><val>One<val>Two</val></bar>Three</doc>"
        val doc = parse(xml, "http://foo.com/", xmlParser())
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Disabled
    @Test
    fun testSupplyParserToConnection() {
        val xmlUrl = "http://direct.infohound.net/tools/jsoup-xml-test.xml"

        // parse with both xml and html parser, ensure different
        val xmlDoc = Jsoup.connect(xmlUrl).parser(xmlParser()).get()
        val htmlDoc = Jsoup.connect(xmlUrl).parser(htmlParser()).get()
        val autoXmlDoc = Jsoup.connect(xmlUrl).get() // check connection auto detects xml, uses xml parser
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(xmlDoc.html())
        )
        Assertions.assertNotEquals(htmlDoc, xmlDoc)
        Assertions.assertEquals(xmlDoc, autoXmlDoc)
        Assertions.assertEquals(1, htmlDoc.select("head").size) // html parser normalises
        Assertions.assertEquals(0, xmlDoc.select("head").size) // xml parser does not
        Assertions.assertEquals(0, autoXmlDoc.select("head").size) // xml parser does not
    }

    @Test
    fun testSupplyParserToDataStream() {
        val xmlFile = File(XmlTreeBuilder::class.java.getResource("/htmltests/xml-test.xml").toURI())
        val inStream: InputStream = FileInputStream(xmlFile)
        val doc = parse(inStream, null, "http://foo.com", xmlParser())
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testDoesNotForceSelfClosingKnownTags() {
        // html will force "<br>one</br>" to logically "<br />One<br />". XML should be stay "<br>one</br> -- don't recognise tag.
        val htmlDoc = Jsoup.parse("<br>one</br>")
        Assertions.assertEquals("<br>\none\n<br>", htmlDoc.body().html())
        val xmlDoc = parse("<br>one</br>", "", xmlParser())
        Assertions.assertEquals("<br>one</br>", xmlDoc.html())
    }

    @Test
    fun handlesXmlDeclarationAsDeclaration() {
        val html = "<?xml encoding='UTF-8' ?><body>One</body><!-- comment -->"
        val doc = parse(html, "", xmlParser())
        Assertions.assertEquals("<?xml encoding=\"UTF-8\"?><body>One</body><!-- comment -->", doc.outerHtml())
        Assertions.assertEquals("#declaration", doc.childNode(0).nodeName())
        Assertions.assertEquals("#comment", doc.childNode(2).nodeName())
    }

    @Test
    fun xmlFragment() {
        val xml = "<one src='/foo/' />Two<three><four /></three>"
        val nodes = parseXmlFragment(xml, "http://example.com/")
        Assertions.assertEquals(3, nodes.size)
        Assertions.assertEquals("http://example.com/foo/", nodes[0].absUrl("src"))
        Assertions.assertEquals("one", nodes[0].nodeName())
        Assertions.assertEquals("Two", (nodes[1] as TextNode).text())
    }

    @Test
    fun xmlParseDefaultsToHtmlOutputSyntax() {
        val doc = parse("x", "", xmlParser())
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
    }

    @Test
    fun testDoesHandleEOFInTag() {
        val html = "<img src=asdf onerror=\"alert(1)\" x="
        val xmlDoc = parse(html, "", xmlParser())
        Assertions.assertEquals("<img src=\"asdf\" onerror=\"alert(1)\" x=\"\" />", xmlDoc.html())
    }

    @Test
    fun testDetectCharsetEncodingDeclaration() {
        val xmlFile = File(XmlTreeBuilder::class.java.getResource("/htmltests/xml-charset.xml").toURI())
        val inStream: InputStream = FileInputStream(xmlFile)
        val doc = parse(inStream, null, "http://example.com/", xmlParser())
        Assertions.assertEquals("ISO-8859-1", doc.charset()!!.name())
        Assertions.assertEquals(
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><data>äöåéü</data>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testParseDeclarationAttributes() {
        val xml = "<?xml version='1' encoding='UTF-8' something='else'?><val>One</val>"
        val doc = parse(xml, "", xmlParser())
        val decl = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals("1", decl.attr("version"))
        Assertions.assertEquals("UTF-8", decl.attr("encoding"))
        Assertions.assertEquals("else", decl.attr("something"))
        Assertions.assertEquals("version=\"1\" encoding=\"UTF-8\" something=\"else\"", decl.wholeDeclaration)
        Assertions.assertEquals("<?xml version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", decl.outerHtml())
    }

    @Test
    fun testParseDeclarationWithoutAttributes() {
        val xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<?myProcessingInstruction My Processing instruction.?>"
        val doc = parse(xml, "", xmlParser())
        val decl = doc.childNode(2) as XmlDeclaration
        Assertions.assertEquals("myProcessingInstruction", decl.name())
        Assertions.assertTrue(decl.hasAttr("My"))
        Assertions.assertEquals("<?myProcessingInstruction My Processing instruction.?>", decl.outerHtml())
    }

    @Test
    fun caseSensitiveDeclaration() {
        val xml = "<?XML version='1' encoding='UTF-8' something='else'?>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals("<?XML version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", doc.outerHtml())
    }

    @Test
    fun testCreatesValidProlog() {
        val document = createShell("")
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        document.charset(StandardCharsets.UTF_8)
        Assertions.assertEquals(
            """<?xml version="1.0" encoding="UTF-8"?>
<html>
 <head></head>
 <body></body>
</html>""", document.outerHtml()
        )
    }

    @Test
    fun preservesCaseByDefault() {
        val xml = "<CHECK>One</CHECK><TEST ID=1>Check</TEST>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals("<CHECK>One</CHECK><TEST ID=\"1\">Check</TEST>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun appendPreservesCaseByDefault() {
        val xml = "<One>One</One>"
        val doc = parse(xml, "", xmlParser())
        val one = doc.select("One")
        one.append("<Two ID=2>Two</Two>")
        Assertions.assertEquals("<One>One<Two ID=\"2\">Two</Two></One>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun disablesPrettyPrintingByDefault() {
        val xml = "\n\n<div><one>One</one><one>\n Two</one>\n</div>\n "
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals(xml, doc.html())
    }

    @Test
    fun canNormalizeCase() {
        val xml = "<TEST ID=1>Check</TEST>"
        val doc = parse(xml, "", xmlParser().settings(ParseSettings.htmlDefault))
        Assertions.assertEquals("<test id=\"1\">Check</test>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun normalizesDiscordantTags() {
        val parser = xmlParser().settings(ParseSettings.htmlDefault)
        val document = parse("<div>test</DIV><p></p>", "", parser)
        Assertions.assertEquals("<div>test</div><p></p>", document.html())
        // was failing -> toString() = "<div>\n test\n <p></p>\n</div>"
    }

    @Test
    fun roundTripsCdata() {
        val xml = "<div id=1><![CDATA[\n<html>\n <foo><&amp;]]></div>"
        val doc = parse(xml, "", xmlParser())
        val div = doc.getElementById("1")
        Assertions.assertEquals("<html>\n <foo><&amp;", div!!.text())
        Assertions.assertEquals(0, div.children().size)
        Assertions.assertEquals(1, div.childNodeSize()) // no elements, one text node
        Assertions.assertEquals("<div id=\"1\"><![CDATA[\n<html>\n <foo><&amp;]]></div>", div.outerHtml())
        val cdata = div.textNodes()[0] as CDataNode
        Assertions.assertEquals("\n<html>\n <foo><&amp;", cdata.text())
    }

    @Test
    fun cdataPreservesWhiteSpace() {
        val xml = "<script type=\"text/javascript\">//<![CDATA[\n\n  foo();\n//]]></script>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals(xml, doc.outerHtml())
        Assertions.assertEquals("//\n\n  foo();\n//", doc.selectFirst("script")!!.text())
    }

    @Test
    fun handlesDodgyXmlDecl() {
        val xml = "<?xml version='1.0'><val>One</val>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals("One", doc.select("val").text())
    }

    @Test
    fun handlesLTinScript() {
        // https://github.com/jhy/jsoup/issues/1139
        val html = "<script> var a=\"<?\"; var b=\"?>\"; </script>"
        val doc = parse(html, "", xmlParser())
        Assertions.assertEquals(
            "<script> var a=\"<!--?\"; var b=\"?-->\"; </script>",
            doc.html()
        ) // converted from pseudo xmldecl to comment
    }

    @Test
    fun dropsDuplicateAttributes() {
        // case sensitive, so should drop Four and Five
        val html = "<p One=One ONE=Two one=Three One=Four ONE=Five two=Six two=Seven Two=Eight>Text</p>"
        val parser = xmlParser().setTrackErrors(10)
        val doc = parser.parseInput(html, "")
        Assertions.assertEquals(
            "<p One=\"One\" ONE=\"Two\" one=\"Three\" two=\"Six\" Two=\"Eight\">Text</p>", doc.selectFirst("p")!!
                .outerHtml()
        )
    }

    @Test
    fun readerClosedAfterParse() {
        val doc = parse("Hello", "", xmlParser())
        val treeBuilder = doc.parser().treeBuilder
        Assertions.assertNull(treeBuilder.reader)
        //        assertNull(treeBuilder.tokeniser); // TODO
    }

    @Test
    fun xmlParserEnablesXmlOutputAndEscapes() {
        // Test that when using the XML parser, the output mode and escape mode default to XHTML entities
        // https://github.com/jhy/jsoup/issues/1420
        val doc = parse("<p one='&lt;two&gt;&copy'>Three</p>", "", xmlParser())
        Assertions.assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml)
        Assertions.assertEquals(doc.outputSettings().escapeMode(), Entities.EscapeMode.xhtml)
        Assertions.assertEquals("<p one=\"&lt;two>©\">Three</p>", doc.html()) // only the < should be escaped
    }

    @Test
    fun xmlSyntaxEscapesLtInAttributes() {
        // Regardless of the entity escape mode, make sure < is escaped in attributes when in XML
        val doc = parse("<p one='&lt;two&gt;&copy'>Three</p>", "", xmlParser())
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended)
        doc.outputSettings().charset("ascii") // to make sure &copy; is output
        Assertions.assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml)
        Assertions.assertEquals("<p one=\"&lt;two>&copy;\">Three</p>", doc.html())
    }

    @Test
    fun xmlOutputCorrectsInvalidAttributeNames() {
        val xml = "<body style=\"color: red\" \" name\"><div =\"\"></div></body>"
        val doc = parse(xml, xmlParser())
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
        val out = doc.html()
        Assertions.assertEquals("<body style=\"color: red\" name=\"\"><div></div></body>", out)
    }

    @Test
    fun customTagsAreFlyweights() {
        val xml = "<foo>Foo</foo><foo>Foo</foo><FOO>FOO</FOO><FOO>FOO</FOO>"
        val doc = parse(xml, xmlParser())
        val els = doc.children()
        val t1 = els[0].tag()
        val t2 = els[1].tag()
        val t3 = els[2].tag()
        val t4 = els[3].tag()
        Assertions.assertEquals("foo", t1.name)
        Assertions.assertEquals("FOO", t3.name)
        Assertions.assertSame(t1, t2)
        Assertions.assertSame(t3, t4)
    }
}
