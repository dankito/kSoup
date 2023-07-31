package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.helper.ValidationException
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.internal.StringUtil.isBlank
import kotlin.test.*

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
        assertTrue(one.isBlank)
        assertTrue(two.isBlank)
        assertTrue(three.isBlank)
        assertFalse(four.isBlank)
        assertFalse(five.isBlank)
    }

    @Test
    fun testTextBean() {
        val doc = Jsoup.parse("<p>One <span>two &amp;</span> three &amp;</p>")
        val p = doc.select("p").first()
        val span = doc.select("span").first()
        assertEquals("two &", span!!.text())
        val spanText = span.childNode(0) as TextNode
        assertEquals("two &", spanText.text())
        val tn = p!!.childNode(2) as TextNode
        assertEquals(" three &", tn.text())
        tn.text(" POW!")
        assertEquals(
            "One <span>two &amp;</span> POW!", TextUtil.stripNewlines(
                p.html()
            )
        )
        tn.attr(tn.nodeName(), "kablam &")
        assertEquals("kablam &", tn.text())
        assertEquals(
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
        assertEquals("Hello ", tn.wholeText)
        assertEquals("there", tail.wholeText)
        tail.text("there!")
        assertEquals("Hello there!", div.text())
        assertSame(tn.parent(), tail.parent())
    }

    @Test
    fun testSplitAnEmbolden() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.select("div").first()
        val tn = div!!.childNode(0) as TextNode
        val tail = tn.splitText(6)
        tail.wrap("<b></b>")
        assertEquals(
            "Hello <b>there</b>",
            TextUtil.stripNewlines(div.html())
        ) // not great that we get \n<b>there there... must correct
    }

    @Test
    fun testSplitTextValidation() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.expectFirst("div")
        val tn = div.childNode(0) as TextNode
        var ex: Throwable = assertFailsWith(ValidationException::class) {
            tn.splitText(-5)
        }
        assertEquals("Split offset must be not be negative", ex.message)
        ex = assertFailsWith(ValidationException::class) {
            tn.splitText(500)
        }
        assertEquals("Split offset must not be greater than current text length", ex.message)
    }

    @Test
    fun testWithSupplementaryCharacter() {
        val doc = Jsoup.parse(StringUtil.codePointToString(135361))
        val t = doc.body().textNodes()[0]
        assertEquals(StringUtil.codePointToString(135361), t.outerHtml().trim { it <= ' ' })
    }

    @Test
    fun testLeadNodesHaveNoChildren() {
        val doc = Jsoup.parse("<div>Hello there</div>")
        val div = doc.select("div").first()
        val tn = div!!.childNode(0) as TextNode
        val nodes = tn.childNodes()
        assertEquals(0, nodes.size)
    }

    @Test
    fun testSpaceNormalise() {
        // https://github.com/jhy/jsoup/issues/1309
        val whole = "Two  spaces"
        val norm = "Two spaces"
        val tn = TextNode(whole) // there are 2 spaces between the words
        assertEquals(whole, tn.wholeText)
        assertEquals(norm, tn.text())
        assertEquals(norm, tn.outerHtml())
        assertEquals(norm, tn.toString())
        val el = Element("p")
        el.appendChild(tn) // this used to change the context
        //tn.setParentNode(el); // set any parent
        assertEquals(whole, tn.wholeText)
        assertEquals(norm, tn.text())
        assertEquals(norm, tn.outerHtml())
        assertEquals(norm, tn.toString())
        assertEquals("<p>$norm</p>", el.outerHtml())
        assertEquals(norm, el.html())
        assertEquals(whole, el.wholeText())
    }

    @Test
    fun testClone() {
        // https://github.com/jhy/jsoup/issues/1176
        val x = TextNode("zzz")
        val y = x.clone()
        assertNotSame(x, y)
        assertEquals(x.outerHtml(), y.outerHtml())
        y.text("yyy")
        assertNotEquals(x.outerHtml(), y.outerHtml())
        assertEquals("zzz", x.text())
        x.attributes() // already cloned so no impact
        y.text("xxx")
        assertEquals("zzz", x.text())
        assertEquals("xxx", y.text())
    }

    @Test
    fun testCloneAfterAttributesHit() {
        // https://github.com/jhy/jsoup/issues/1176
        val x = TextNode("zzz")
        x.attributes() // moves content from leafnode value to attributes, which were missed in clone
        val y = x.clone()
        y.text("xxx")
        assertEquals("zzz", x.text())
        assertEquals("xxx", y.text())
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
                    assertFalse(isBlank(textNode.text()))
                    if (!foundFirst) {
                        foundFirst = true
                        assertEquals("One ", textNode.text())
                        assertEquals("One ", textNode.wholeText)
                    }
                }
            }
        }
        assertTrue(foundFirst)
    }

    @Test
    fun createFromEncoded() {
        val tn = TextNode.createFromEncoded("&lt;One&gt;")
        assertEquals("<One>", tn.text())
    }

    @Test
    fun normaliseWhitespace() {
        assertEquals(" One Two ", TextNode.normaliseWhitespace("  One \n Two\n"))
    }

    @Test
    fun stripLeadingWhitespace() {
        assertEquals("One Two  ", TextNode.stripLeadingWhitespace("\n One Two  "))
    }

    // Lead Node tests
    @Test
    fun leafNodeAttributes() {
        val t = TextNode("First")

        // will hit the !hasAttributes flow
        t.attr(t.nodeName(), "One")
        assertEquals("One", t.attr(t.nodeName()))
        assertFalse(t.hasAttributes())
        val attr = t.attributes()
        assertEquals(1, attr.asList().size) // vivifies 'One' as an attribute
        assertEquals("One", attr[t.nodeName()])
        t.coreValue("Two")
        assertEquals("Two", t.text())

        // arbitrary attributes
        assertFalse(t.hasAttr("foo"))
        t.attr("foo", "bar")
        assertTrue(t.hasAttr("foo"))
        t.removeAttr("foo")
        assertFalse(t.hasAttr("foo"))
        assertEquals("", t.baseUri())
        t.attr("href", "/foo.html")
        assertEquals("", t.absUrl("href")) // cannot abs
        val p = Element("p")
        p.doSetBaseUri("https://example.com/")
        p.appendChild(t)
        assertEquals("https://example.com/foo.html", t.absUrl("href"))
        assertEquals(0, t.childNodeSize())
        assertSame(t, t.empty())
        assertEquals(0, t.ensureChildNodes().size)
        val clone = t.clone()
        assertTrue(t.hasSameValue(clone))
        assertEquals("/foo.html", clone.attr("href"))
        assertEquals("Two", clone.text())
    }
}
