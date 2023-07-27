package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.platform.Charsets
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.nodes.Document.Companion.createShell
import net.dankito.ksoup.parser.Parser.Companion.parseXmlFragment
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import kotlin.test.*

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
        assertEquals(
            "<doc id=\"2\" href=\"/bar\">Foo <br /><link>One</link><link>Two</link></doc>",
            TextUtil.stripNewlines(doc.html())
        )
        assertEquals(doc.getElementById("2")!!.absUrl("href"), "http://foo.com/bar")
    }

    @Test
    fun testPopToClose() {
        // test: </val> closes Two, </bar> ignored
        val xml = "<doc><val>One<val>Two</val></bar>Three</doc>"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testCommentAndDocType() {
        val xml = "<!DOCTYPE HTML><!-- a comment -->One <qux />Two"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        assertEquals(
            "<!DOCTYPE HTML><!-- a comment -->One <qux />Two",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testSupplyParserToJsoupClass() {
        val xml = "<doc><val>One<val>Two</val></bar>Three</doc>"
        val doc = parse(xml, "http://foo.com/", xmlParser())
        assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testDoesNotForceSelfClosingKnownTags() {
        // html will force "<br>one</br>" to logically "<br />One<br />". XML should be stay "<br>one</br> -- don't recognise tag.
        val htmlDoc = Jsoup.parse("<br>one</br>")
        assertEquals("<br>\none\n<br>", htmlDoc.body().html())
        val xmlDoc = parse("<br>one</br>", "", xmlParser())
        assertEquals("<br>one</br>", xmlDoc.html())
    }

    @Test
    fun handlesXmlDeclarationAsDeclaration() {
        val html = "<?xml encoding='UTF-8' ?><body>One</body><!-- comment -->"
        val doc = parse(html, "", xmlParser())
        assertEquals("<?xml encoding=\"UTF-8\"?><body>One</body><!-- comment -->", doc.outerHtml())
        assertEquals("#declaration", doc.childNode(0).nodeName())
        assertEquals("#comment", doc.childNode(2).nodeName())
    }

    @Test
    fun xmlFragment() {
        val xml = "<one src='/foo/' />Two<three><four /></three>"
        val nodes = parseXmlFragment(xml, "http://example.com/")
        assertEquals(3, nodes.size)
        assertEquals("http://example.com/foo/", nodes[0].absUrl("src"))
        assertEquals("one", nodes[0].nodeName())
        assertEquals("Two", (nodes[1] as TextNode).text())
    }

    @Test
    fun xmlParseDefaultsToHtmlOutputSyntax() {
        val doc = parse("x", "", xmlParser())
        assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
    }

    @Test
    fun testDoesHandleEOFInTag() {
        val html = "<img src=asdf onerror=\"alert(1)\" x="
        val xmlDoc = parse(html, "", xmlParser())
        assertEquals("<img src=\"asdf\" onerror=\"alert(1)\" x=\"\" />", xmlDoc.html())
    }

    @Test
    fun testParseDeclarationAttributes() {
        val xml = "<?xml version='1' encoding='UTF-8' something='else'?><val>One</val>"
        val doc = parse(xml, "", xmlParser())
        val decl = doc.childNode(0) as XmlDeclaration
        assertEquals("1", decl.attr("version"))
        assertEquals("UTF-8", decl.attr("encoding"))
        assertEquals("else", decl.attr("something"))
        assertEquals("version=\"1\" encoding=\"UTF-8\" something=\"else\"", decl.wholeDeclaration)
        assertEquals("<?xml version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", decl.outerHtml())
    }

    @Test
    fun testParseDeclarationWithoutAttributes() {
        val xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<?myProcessingInstruction My Processing instruction.?>"
        val doc = parse(xml, "", xmlParser())
        val decl = doc.childNode(2) as XmlDeclaration
        assertEquals("myProcessingInstruction", decl.name())
        assertTrue(decl.hasAttr("My"))
        assertEquals("<?myProcessingInstruction My Processing instruction.?>", decl.outerHtml())
    }

    @Test
    fun caseSensitiveDeclaration() {
        val xml = "<?XML version='1' encoding='UTF-8' something='else'?>"
        val doc = parse(xml, "", xmlParser())
        assertEquals("<?XML version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", doc.outerHtml())
    }

    @Test
    fun testCreatesValidProlog() {
        val document = createShell("")
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        document.charset(Charsets.UTF_8)
        assertEquals(
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
        assertEquals("<CHECK>One</CHECK><TEST ID=\"1\">Check</TEST>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun appendPreservesCaseByDefault() {
        val xml = "<One>One</One>"
        val doc = parse(xml, "", xmlParser())
        val one = doc.select("One")
        one.append("<Two ID=2>Two</Two>")
        assertEquals("<One>One<Two ID=\"2\">Two</Two></One>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun disablesPrettyPrintingByDefault() {
        val xml = "\n\n<div><one>One</one><one>\n Two</one>\n</div>\n "
        val doc = parse(xml, "", xmlParser())
        assertEquals(xml, doc.html())
    }

    @Test
    fun canNormalizeCase() {
        val xml = "<TEST ID=1>Check</TEST>"
        val doc = parse(xml, "", xmlParser().settings(ParseSettings.htmlDefault))
        assertEquals("<test id=\"1\">Check</test>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun normalizesDiscordantTags() {
        val parser = xmlParser().settings(ParseSettings.htmlDefault)
        val document = parse("<div>test</DIV><p></p>", "", parser)
        assertEquals("<div>test</div><p></p>", document.html())
        // was failing -> toString() = "<div>\n test\n <p></p>\n</div>"
    }

    @Test
    fun roundTripsCdata() {
        val xml = "<div id=1><![CDATA[\n<html>\n <foo><&amp;]]></div>"
        val doc = parse(xml, "", xmlParser())
        val div = doc.getElementById("1")
        assertEquals("<html>\n <foo><&amp;", div!!.text())
        assertEquals(0, div.children().size)
        assertEquals(1, div.childNodeSize()) // no elements, one text node
        assertEquals("<div id=\"1\"><![CDATA[\n<html>\n <foo><&amp;]]></div>", div.outerHtml())
        val cdata = div.textNodes()[0] as CDataNode
        assertEquals("\n<html>\n <foo><&amp;", cdata.text())
    }

    @Test
    fun cdataPreservesWhiteSpace() {
        val xml = "<script type=\"text/javascript\">//<![CDATA[\n\n  foo();\n//]]></script>"
        val doc = parse(xml, "", xmlParser())
        assertEquals(xml, doc.outerHtml())
        assertEquals("//\n\n  foo();\n//", doc.selectFirst("script")!!.text())
    }

    @Test
    fun handlesDodgyXmlDecl() {
        val xml = "<?xml version='1.0'><val>One</val>"
        val doc = parse(xml, "", xmlParser())
        assertEquals("One", doc.select("val").text())
    }

    @Test
    fun handlesLTinScript() {
        // https://github.com/jhy/jsoup/issues/1139
        val html = "<script> var a=\"<?\"; var b=\"?>\"; </script>"
        val doc = parse(html, "", xmlParser())
        assertEquals(
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
        assertEquals(
            "<p One=\"One\" ONE=\"Two\" one=\"Three\" two=\"Six\" Two=\"Eight\">Text</p>", doc.selectFirst("p")!!
                .outerHtml()
        )
    }

    @Test
    fun readerClosedAfterParse() {
        val doc = parse("Hello", "", xmlParser())
        val treeBuilder = doc.parser().treeBuilder
        assertNull(treeBuilder.reader)
        assertNull(treeBuilder.tokeniser)
    }

    @Test
    fun xmlParserEnablesXmlOutputAndEscapes() {
        // Test that when using the XML parser, the output mode and escape mode default to XHTML entities
        // https://github.com/jhy/jsoup/issues/1420
        val doc = parse("<p one='&lt;two&gt;&copy'>Three</p>", "", xmlParser())
        assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml)
        assertEquals(doc.outputSettings().escapeMode(), Entities.EscapeMode.xhtml)
        assertEquals("<p one=\"&lt;two>©\">Three</p>", doc.html()) // only the < should be escaped
    }

    @Test
    fun xmlSyntaxEscapesLtInAttributes() {
        // Regardless of the entity escape mode, make sure < is escaped in attributes when in XML
        val doc = parse("<p one='&lt;two&gt;&copy'>Three</p>", "", xmlParser())
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended)
        doc.outputSettings().charset("ascii") // to make sure &copy; is output
        assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml)
        assertEquals("<p one=\"&lt;two>&copy;\">Three</p>", doc.html())
    }

    @Test
    fun xmlOutputCorrectsInvalidAttributeNames() {
        val xml = "<body style=\"color: red\" \" name\"><div =\"\"></div></body>"
        val doc = parse(xml, xmlParser())
        assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
        val out = doc.html()
        assertEquals("<body style=\"color: red\" name=\"\"><div></div></body>", out)
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
        assertEquals("foo", t1.name)
        assertEquals("FOO", t3.name)
        assertSame(t1, t2)
        assertSame(t3, t4)
    }
}
