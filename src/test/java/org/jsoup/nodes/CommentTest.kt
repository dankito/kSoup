package org.jsoup.nodes

import org.jsoup.Jsoupimport

org.junit.jupiter.api.Assertionsimport org.junit.jupiter.api.Test
class CommentTest {
    private val comment = Comment(" This is one heck of a comment! ")
    private val decl = Comment("?xml encoding='ISO-8859-1'?")
    @Test
    fun nodeName() {
        Assertions.assertEquals("#comment", comment.nodeName())
    }

    @get:Test
    val data: Unit
        get() {
            Assertions.assertEquals(" This is one heck of a comment! ", comment.data)
        }

    @Test
    fun testToString() {
        Assertions.assertEquals("<!-- This is one heck of a comment! -->", comment.toString())
        var doc = Jsoup.parse("<div><!-- comment--></div>")
        Assertions.assertEquals("<div>\n <!-- comment-->\n</div>", doc.body().html())
        doc = Jsoup.parse("<p>One<!-- comment -->Two</p>")
        Assertions.assertEquals("<p>One<!-- comment -->Two</p>", doc.body().html())
        Assertions.assertEquals("OneTwo", doc.text())
    }

    @Test
    fun testHtmlNoPretty() {
        val doc = Jsoup.parse("<!-- a simple comment -->")
        doc.outputSettings().prettyPrint(false)
        Assertions.assertEquals("<!-- a simple comment --><html><head></head><body></body></html>", doc.html())
        val node = doc.childNode(0)
        val c1 = node as Comment
        Assertions.assertEquals("<!-- a simple comment -->", c1.outerHtml())
    }

    @Test
    fun stableIndentInBlock() {
        val html = "<div><!-- comment --> Text</div><p><!-- comment --> Text</p>"
        val doc = Jsoup.parse(html)
        val out = doc.body().html()
        Assertions.assertEquals(
            """<div>
 <!-- comment --> Text
</div>
<p><!-- comment --> Text</p>""", out
        )
        val doc2 = Jsoup.parse(out)
        val out2 = doc2.body().html()
        Assertions.assertEquals(out, out2)
    }

    @Test
    fun testClone() {
        val c1 = comment.clone()
        Assertions.assertNotSame(comment, c1)
        Assertions.assertEquals(comment.data, comment.data)
        c1.setData("New")
        Assertions.assertEquals("New", c1.data)
        Assertions.assertNotEquals(c1.data, comment.data)
    }

    @get:Test
    val isXmlDeclaration: Unit
        get() {
            Assertions.assertFalse(comment.isXmlDeclaration)
            Assertions.assertTrue(decl.isXmlDeclaration)
        }

    @Test
    fun asXmlDeclaration() {
        val xmlDeclaration = decl.asXmlDeclaration()
        Assertions.assertNotNull(xmlDeclaration)
    }
}
