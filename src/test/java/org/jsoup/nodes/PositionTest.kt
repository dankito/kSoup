package org.jsoup.nodes

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.select.GenericNodeVisitor.Companion.jvmNodeVisitor
import org.jsoup.select.NodeTraversor.traverse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException

/**
 * Functional tests for the Position tracking behavior (across nodes, treebuilder, etc.)
 */
internal class PositionTest {
    @Test
    fun parserTrackDefaults() {
        val htmlParser = htmlParser()
        Assertions.assertFalse(htmlParser.isTrackPosition)
        htmlParser.setTrackPosition(true)
        Assertions.assertTrue(htmlParser.isTrackPosition)
        val xmlParser = htmlParser()
        Assertions.assertFalse(xmlParser.isTrackPosition)
        xmlParser.setTrackPosition(true)
        Assertions.assertTrue(xmlParser.isTrackPosition)
    }

    @Test
    fun tracksPosition() {
        val html = "<p id=1\n class=foo>\n<span>Hello\n &reg;\n there &copy.</span> now.\n <!-- comment --> "
        val doc = parse(html, TrackingParser)
        val body = doc.expectFirst("body")
        val p = doc.expectFirst("p")
        val span = doc.expectFirst("span")
        val text = span.firstChild() as TextNode?
        Assertions.assertNotNull(text)
        val now = span.nextSibling() as TextNode?
        Assertions.assertNotNull(now)
        val comment = now!!.nextSibling() as Comment?
        Assertions.assertNotNull(comment)
        Assertions.assertFalse(body.sourceRange().isTracked)
        val pRange = p.sourceRange()
        Assertions.assertEquals("1,1:0-2,12:19", pRange.toString())

        // no explicit P closer
        val pEndRange = p.endSourceRange()
        Assertions.assertFalse(pEndRange.isTracked)
        val pStart = pRange.start()
        Assertions.assertTrue(pStart.isTracked)
        Assertions.assertEquals(0, pStart.pos())
        Assertions.assertEquals(1, pStart.columnNumber())
        Assertions.assertEquals(1, pStart.lineNumber())
        Assertions.assertEquals("1,1:0", pStart.toString())
        val pEnd = pRange.end()
        Assertions.assertTrue(pStart.isTracked)
        Assertions.assertEquals(19, pEnd.pos())
        Assertions.assertEquals(12, pEnd.columnNumber())
        Assertions.assertEquals(2, pEnd.lineNumber())
        Assertions.assertEquals("2,12:19", pEnd.toString())
        Assertions.assertEquals("3,1:20", span.sourceRange().start().toString())
        Assertions.assertEquals("3,7:26", span.sourceRange().end().toString())

        // span end tag
        val spanEnd = span.endSourceRange()
        Assertions.assertTrue(spanEnd.isTracked)
        Assertions.assertEquals("5,14:52-5,21:59", spanEnd.toString())
        val wholeText = text!!.wholeText
        Assertions.assertEquals("Hello\n ®\n there ©.", wholeText)
        val textOrig = "Hello\n &reg;\n there &copy."
        val textRange = text.sourceRange()
        Assertions.assertEquals(textRange.end().pos() - textRange.start().pos(), textOrig.length)
        Assertions.assertEquals("3,7:26", textRange.start().toString())
        Assertions.assertEquals("5,14:52", textRange.end().toString())
        Assertions.assertEquals("6,2:66", comment!!.sourceRange().start().toString())
        Assertions.assertEquals("6,18:82", comment.sourceRange().end().toString())
    }

    @Test
    fun tracksMarkup() {
        val html = "<!doctype\nhtml>\n<title>jsoup &copy;\n2022</title><body>\n<![CDATA[\n<jsoup>\n]]>"
        val doc = parse(html, TrackingParser)
        val doctype = doc.documentType()
        Assertions.assertNotNull(doctype)
        Assertions.assertEquals("html", doctype!!.name())
        Assertions.assertEquals("1,1:0-2,6:15", doctype.sourceRange().toString())
        val title = doc.expectFirst("title")
        val titleText = title.firstChild() as TextNode?
        Assertions.assertNotNull(titleText)
        Assertions.assertEquals("jsoup ©\n2022", title.text())
        Assertions.assertEquals(titleText!!.wholeText, title.text())
        Assertions.assertEquals("3,1:16-3,8:23", title.sourceRange().toString())
        Assertions.assertEquals("3,8:23-4,5:40", titleText.sourceRange().toString())
        val cdata = doc.body().childNode(1) as CDataNode
        Assertions.assertEquals("\n<jsoup>\n", cdata.text())
        Assertions.assertEquals("5,1:55-7,4:76", cdata.sourceRange().toString())
    }

    @Test
    fun tracksDataNodes() {
        val html = "<head>\n<script>foo;\nbar()\n5 <= 4;</script>"
        val doc = parse(html, TrackingParser)
        val script = doc.expectFirst("script")
        Assertions.assertNotNull(script)
        Assertions.assertEquals("2,1:7-2,9:15", script.sourceRange().toString())
        val data = script.firstChild() as DataNode?
        Assertions.assertNotNull(data)
        Assertions.assertEquals("2,9:15-4,8:33", data!!.sourceRange().toString())
    }

    @Test
    fun tracksXml() {
        val xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!doctype html>\n<rss url=foo>\nXML\n</rss>\n<!-- comment -->"
        val doc = parse(xml, xmlParser().setTrackPosition(true))
        val decl = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals("1,1:0-1,39:38", decl.sourceRange().toString())
        val doctype = doc.childNode(2) as DocumentType
        Assertions.assertEquals("2,1:39-2,16:54", doctype.sourceRange().toString())
        val rss = doc.firstElementChild()
        Assertions.assertNotNull(rss)
        Assertions.assertEquals("3,1:55-3,14:68", rss!!.sourceRange().toString())
        Assertions.assertEquals("5,1:73-5,7:79", rss.endSourceRange().toString())
        val text = rss.firstChild() as TextNode?
        Assertions.assertNotNull(text)
        Assertions.assertEquals("3,14:68-5,1:73", text!!.sourceRange().toString())
        val comment = rss.nextSibling()!!.nextSibling() as Comment?
        Assertions.assertEquals("6,1:80-6,17:96", comment!!.sourceRange().toString())
    }

    @Test
    @Throws(IOException::class)
    fun tracksFromFetch() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val doc = Jsoup.connect(url).parser(TrackingParser).get()
        val firstP = doc.expectFirst("p")
        Assertions.assertNotNull(firstP)
        Assertions.assertEquals("4,1:53-4,4:56", firstP.sourceRange().toString())
        val p = doc.expectFirst("#xy")
        Assertions.assertNotNull(p)
        Assertions.assertEquals("1000,1:279646-1000,10:279655", p.sourceRange().toString())
        Assertions.assertEquals("1000,567:280212-1000,571:280216", p.endSourceRange().toString())
        val text = p.firstChild() as TextNode?
        Assertions.assertNotNull(text)
        Assertions.assertEquals("1000,10:279655-1000,357:280002", text!!.sourceRange().toString())
    }

    @Test
    @Throws(IOException::class)
    fun tracksFromXmlFetch() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/test-rss.xml")
        val doc = Jsoup.connect(url).parser(xmlParser().setTrackPosition(true)).get()
        val item = doc.expectFirst("item + item")
        Assertions.assertNotNull(item)
        Assertions.assertEquals("13,5:496-13,11:502", item.sourceRange().toString())
        Assertions.assertEquals("17,5:779-17,12:786", item.endSourceRange().toString())
    }

    @Test
    fun tracksTableMovedText() {
        val html = "<table>foo<tr>bar<td>baz</td>qux</tr>coo</table>"
        val doc = parse(html, TrackingParser)
        val textNodes: MutableList<TextNode> = ArrayList()
        traverse(jvmNodeVisitor { node: Node? ->
            if (node is TextNode) {
                textNodes.add(node)
            }
        }, doc)
        Assertions.assertEquals(5, textNodes.size)
        Assertions.assertEquals("1,8:7-1,11:10", textNodes[0].sourceRange().toString())
        Assertions.assertEquals("1,15:14-1,18:17", textNodes[1].sourceRange().toString())
        Assertions.assertEquals("1,22:21-1,25:24", textNodes[2].sourceRange().toString())
        Assertions.assertEquals("1,30:29-1,33:32", textNodes[3].sourceRange().toString())
        Assertions.assertEquals("1,38:37-1,41:40", textNodes[4].sourceRange().toString())
    }

    @Test
    fun tracksClosingHtmlTagsInXml() {
        // verifies https://github.com/jhy/jsoup/issues/1935
        val xml = "<p>One</p><title>Two</title><data>Three</data>"
        val doc = parse(xml, xmlParser().setTrackPosition(true))
        val els = doc.children()
        for (el in els) {
            Assertions.assertTrue(el.sourceRange().isTracked)
            Assertions.assertTrue(el.endSourceRange().isTracked)
        }
    }

    companion object {
        var TrackingParser = htmlParser().setTrackPosition(true)
    }
}