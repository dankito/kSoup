package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.Attribute.Companion.isBooleanAttribute
import net.dankito.ksoup.parser.ParseSettings
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import kotlin.test.*

class AttributeTest {

    @Test
    fun html() {
        val attr = Attribute("key", "value &")
        assertEquals("key=\"value &amp;\"", attr.html())
        assertEquals(attr.html(), attr.toString())
    }

    @Test
    fun testWithSupplementaryCharacterInAttributeKeyAndValue() {
        val s = StringUtil.codePointToString(135361)
        val attr = Attribute(s, "A" + s + "B")
        assertEquals(s + "=\"A" + s + "B\"", attr.html())
        assertEquals(attr.html(), attr.toString())
    }

    @Test
    fun validatesKeysNotEmpty() {
        assertFailsWith(IllegalArgumentException::class) {
            Attribute(" ", "Check")
        }
    }

    @Test
    fun validatesKeysNotEmptyViaSet() {
        assertFailsWith(IllegalArgumentException::class) {
            val attr = Attribute("One", "Check")
            attr.key = " "
        }
    }

    @Test
    fun booleanAttributesAreEmptyStringValues() {
        val doc = Jsoup.parse("<div hidden>")
        val attributes = doc.body().child(0).attributes()
        assertEquals("", attributes["hidden"])
        val first = attributes.iterator().next()
        assertEquals("hidden", first.key)
        assertEquals("", first.value)
        assertFalse(first.hasDeclaredValue())
        assertTrue(isBooleanAttribute(first.key))
    }

    @Test
    fun settersOnOrphanAttribute() {
        val attr = Attribute("one", "two")
        attr.key = "three"
        val oldVal = attr.setValue("four")
        assertEquals("two", oldVal)
        assertEquals("three", attr.key)
        assertEquals("four", attr.value)
        assertNull(attr.parent)
    }

    @Test
    fun hasValue() {
        val a1 = Attribute("one", "")
        val a2 = Attribute("two", null)
        val a3 = Attribute("thr", "thr")
        assertTrue(a1.hasDeclaredValue())
        assertFalse(a2.hasDeclaredValue())
        assertTrue(a3.hasDeclaredValue())
    }

    @Test
    fun canSetValueToNull() {
        val attr = Attribute("one", "val")
        var oldVal = attr.setValue(null)
        assertEquals("one", attr.html())
        assertEquals("val", oldVal)
        oldVal = attr.setValue("foo")
        assertEquals("", oldVal) // string, not null
    }

    @Test
    fun booleanAttributesAreNotCaseSensitive() {
        // https://github.com/jhy/jsoup/issues/1656
        assertTrue(isBooleanAttribute("required"))
        assertTrue(isBooleanAttribute("REQUIRED"))
        assertTrue(isBooleanAttribute("rEQUIREd"))
        assertFalse(isBooleanAttribute("random string"))
        val html = "<a href=autofocus REQUIRED>One</a>"
        val doc = Jsoup.parse(html)
        assertEquals("<a href=\"autofocus\" required>One</a>", doc.selectFirst("a")!!.outerHtml())
        val doc2 = parse(html, htmlParser().settings(ParseSettings.preserveCase))
        assertEquals("<a href=\"autofocus\" REQUIRED>One</a>", doc2.selectFirst("a")!!.outerHtml())
    }
}
