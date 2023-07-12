package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.helper.ValidationException
import net.dankito.ksoup.internal.StringUtil.isBlank
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Test TextNodes
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class TextNodeTest {
    @Test
    fun testBlank() {
        val one = TextNode("")
        val two = TextNode("     ")
        val three = TextNode("  \n\n   ")
        val four = TextNode("Hello")
        val five = TextNode("  \nHello ")
        Assertions.assertTrue(one.isBlank)
        Assertions.assertTrue(two.isBlank)
        Assertions.assertTrue(three.isBlank)
        Assertions.assertFalse(four.isBlank)
        Assertions.assertFalse(five.isBlank)
    }

    @Test
    fun testTextBean() {
        val doc = Jsoup.parse("<p>One <span>two &amp;</span> three &amp;</p>")
        val p = doc.select("p").first()
        val span = doc.select("span").first()
        Assertions.assertEquals("two &", span!!.text())
        val spanText = span.childNode(0) as TextNode
        Assertions.assertEquals("two &", spanText.text())
        val tn = p!!.childNode(2) as TextNode
        Assertions.assertEquals(" three &", tn.text())
        tn.text(" POW!")
        Assertions.assertEquals(
            "One <span>two &amp;</span> POW!", TextUtil.stripNewlines(
                p.html()
            )
        )
        tn.attr(tn.nodeName(), "kablam &")
        Assertions.assertEquals("kablam &", tn.text())
        Assertions.assertEquals(
            "One <span>two &amp;</span>kablam &amp;", TextUtil.stripNewlines(
                p.html()
            )
        )
    }

    @Test
    fun testSplitText() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.select("div").first()
        val tn = div!!.childNode(0) as TextNode
        val tail = tn.splitText(6)
        Assertions.assertEquals("Hello ", tn.wholeText)
        Assertions.assertEquals("there", tail.wholeText)
        tail.text("there!")
        Assertions.assertEquals("Hello there!", div.text())
        Assertions.assertSame(tn.parent(), tail.parent())
    }

    @Test
    fun testSplitAnEmbolden() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.select("div").first()
        val tn = div!!.childNode(0) as TextNode
        val tail = tn.splitText(6)
        tail.wrap("<b></b>")
        Assertions.assertEquals(
            "Hello <b>there</b>",
            TextUtil.stripNewlines(div.html())
        ) // not great that we get \n<b>there there... must correct
    }

    @Test
    fun testSplitTextValidation() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.expectFirst("div")
        val tn = div.childNode(0) as TextNode
        var ex: Throwable = Assertions.assertThrows(
            ValidationException::class.java
        ) { tn.splitText(-5) }
        Assertions.assertEquals("Split offset must be not be negative", ex.message)
        ex = Assertions.assertThrows(
            ValidationException::class.java
        ) { tn.splitText(500) }
        Assertions.assertEquals("Split offset must not be greater than current text length", ex.message)
    }

    @Test
    fun testWithSupplementaryCharacter() {
        val doc = Jsoup.parse(String(Character.toChars(135361)))
        val t = doc.body().textNodes()[0]
        Assertions.assertEquals(String(Character.toChars(135361)), t.outerHtml().trim { it <= ' ' })
    }

    @Test
    fun testLeadNodesHaveNoChildren() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.select("div").first()
        val tn = div!!.childNode(0) as TextNode
        val nodes = tn.childNodes()
        Assertions.assertEquals(0, nodes.size)
    }

    @Test
    fun testSpaceNormalise() {
        // https://github.com/jhy/jsoup/issues/1309
        val whole = "Two  spaces"
        val norm = "Two spaces"
        val tn = TextNode(whole) // there are 2 spaces between the words
        Assertions.assertEquals(whole, tn.wholeText)
        Assertions.assertEquals(norm, tn.text())
        Assertions.assertEquals(norm, tn.outerHtml())
        Assertions.assertEquals(norm, tn.toString())
        val el = Element("p")
        el.appendChild(tn) // this used to change the context
        //tn.setParentNode(el); // set any parent
        Assertions.assertEquals(whole, tn.wholeText)
        Assertions.assertEquals(norm, tn.text())
        Assertions.assertEquals(norm, tn.outerHtml())
        Assertions.assertEquals(norm, tn.toString())
        Assertions.assertEquals("<p>$norm</p>", el.outerHtml())
        Assertions.assertEquals(norm, el.html())
        Assertions.assertEquals(whole, el.wholeText())
    }

    @Test
    fun testClone() {
        // https://github.com/jhy/jsoup/issues/1176
        val x = TextNode("zzz")
        val y = x.clone()
        Assertions.assertNotSame(x, y)
        Assertions.assertEquals(x.outerHtml(), y.outerHtml())
        y.text("yyy")
        Assertions.assertNotEquals(x.outerHtml(), y.outerHtml())
        Assertions.assertEquals("zzz", x.text())
        x.attributes() // already cloned so no impact
        y.text("xxx")
        Assertions.assertEquals("zzz", x.text())
        Assertions.assertEquals("xxx", y.text())
    }

    @Test
    fun testCloneAfterAttributesHit() {
        // https://github.com/jhy/jsoup/issues/1176
        val x = TextNode("zzz")
        x.attributes() // moves content from leafnode value to attributes, which were missed in clone
        val y = x.clone()
        y.text("xxx")
        Assertions.assertEquals("zzz", x.text())
        Assertions.assertEquals("xxx", y.text())
    }

    @Test
    fun testHasTextWhenIterating() {
        // https://github.com/jhy/jsoup/issues/1170
        val doc = Jsoup.parse("<div>One <p>Two <p>Three")
        var foundFirst = false
        for (el in doc.allElements) {
            for (node in el.childNodes()) {
                if (node is TextNode) {
                    val textNode = node
                    Assertions.assertFalse(isBlank(textNode.text()))
                    if (!foundFirst) {
                        foundFirst = true
                        Assertions.assertEquals("One ", textNode.text())
                        Assertions.assertEquals("One ", textNode.wholeText)
                    }
                }
            }
        }
        Assertions.assertTrue(foundFirst)
    }

    @Test
    fun createFromEncoded() {
        val tn = TextNode.createFromEncoded("&lt;One&gt;")
        Assertions.assertEquals("<One>", tn.text())
    }

    @Test
    fun normaliseWhitespace() {
        Assertions.assertEquals(" One Two ", TextNode.normaliseWhitespace("  One \n Two\n"))
    }

    @Test
    fun stripLeadingWhitespace() {
        Assertions.assertEquals("One Two  ", TextNode.stripLeadingWhitespace("\n One Two  "))
    }

    // Lead Node tests
    @Test
    fun leafNodeAttributes() {
        val t = TextNode("First")

        // will hit the !hasAttributes flow
        t.attr(t.nodeName(), "One")
        Assertions.assertEquals("One", t.attr(t.nodeName()))
        Assertions.assertFalse(t.hasAttributes())
        val attr = t.attributes()
        Assertions.assertEquals(1, attr.asList().size) // vivifies 'One' as an attribute
        Assertions.assertEquals("One", attr[t.nodeName()])
        t.coreValue("Two")
        Assertions.assertEquals("Two", t.text())

        // arbitrary attributes
        Assertions.assertFalse(t.hasAttr("foo"))
        t.attr("foo", "bar")
        Assertions.assertTrue(t.hasAttr("foo"))
        t.removeAttr("foo")
        Assertions.assertFalse(t.hasAttr("foo"))
        Assertions.assertEquals("", t.baseUri())
        t.attr("href", "/foo.html")
        Assertions.assertEquals("", t.absUrl("href")) // cannot abs
        val p = Element("p")
        p.doSetBaseUri("https://example.com/")
        p.appendChild(t)
        Assertions.assertEquals("https://example.com/foo.html", t.absUrl("href"))
        Assertions.assertEquals(0, t.childNodeSize())
        Assertions.assertSame(t, t.empty())
        Assertions.assertEquals(0, t.ensureChildNodes().size)
        val clone = t.clone()
        Assertions.assertTrue(t.hasSameValue(clone))
        Assertions.assertEquals("/foo.html", clone.attr("href"))
        Assertions.assertEquals("Two", clone.text())
    }
}
