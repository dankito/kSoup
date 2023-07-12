package org.jsoup.nodes

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.Attribute.Companion.isBooleanAttribute
import org.jsoup.parser.ParseSettings
import org.jsoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AttributeTest {
    @Test
    fun html() {
        val attr = Attribute("key", "value &")
        Assertions.assertEquals("key=\"value &amp;\"", attr.html())
        Assertions.assertEquals(attr.html(), attr.toString())
    }

    @Test
    fun testWithSupplementaryCharacterInAttributeKeyAndValue() {
        val s = String(Character.toChars(135361))
        val attr = Attribute(s, "A" + s + "B")
        Assertions.assertEquals(s + "=\"A" + s + "B\"", attr.html())
        Assertions.assertEquals(attr.html(), attr.toString())
    }

    @Test
    fun validatesKeysNotEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Attribute(" ", "Check") }
    }

    @Test
    fun validatesKeysNotEmptyViaSet() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val attr = Attribute("One", "Check")
            attr.key = " "
        }
    }

    @Test
    fun booleanAttributesAreEmptyStringValues() {
        val doc = Jsoup.parse("<div hidden>")
        val attributes = doc.body().child(0).attributes()
        Assertions.assertEquals("", attributes["hidden"])
        val first = attributes.iterator().next()
        Assertions.assertEquals("hidden", first.key)
        Assertions.assertEquals("", first.value)
        Assertions.assertFalse(first.hasDeclaredValue())
        Assertions.assertTrue(isBooleanAttribute(first.key))
    }

    @Test
    fun settersOnOrphanAttribute() {
        val attr = Attribute("one", "two")
        attr.key = "three"
        val oldVal = attr.setValue("four")
        Assertions.assertEquals("two", oldVal)
        Assertions.assertEquals("three", attr.key)
        Assertions.assertEquals("four", attr.value)
        Assertions.assertNull(attr.parent)
    }

    @Test
    fun hasValue() {
        val a1 = Attribute("one", "")
        val a2 = Attribute("two", null)
        val a3 = Attribute("thr", "thr")
        Assertions.assertTrue(a1.hasDeclaredValue())
        Assertions.assertFalse(a2.hasDeclaredValue())
        Assertions.assertTrue(a3.hasDeclaredValue())
    }

    @Test
    fun canSetValueToNull() {
        val attr = Attribute("one", "val")
        var oldVal = attr.setValue(null)
        Assertions.assertEquals("one", attr.html())
        Assertions.assertEquals("val", oldVal)
        oldVal = attr.setValue("foo")
        Assertions.assertEquals("", oldVal) // string, not null
    }

    @Test
    fun booleanAttributesAreNotCaseSensitive() {
        // https://github.com/jhy/jsoup/issues/1656
        Assertions.assertTrue(isBooleanAttribute("required"))
        Assertions.assertTrue(isBooleanAttribute("REQUIRED"))
        Assertions.assertTrue(isBooleanAttribute("rEQUIREd"))
        Assertions.assertFalse(isBooleanAttribute("random string"))
        val html = "<a href=autofocus REQUIRED>One</a>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<a href=\"autofocus\" required>One</a>", doc.selectFirst("a")!!.outerHtml())
        val doc2 = parse(html, htmlParser().settings(ParseSettings.preserveCase))
        Assertions.assertEquals("<a href=\"autofocus\" REQUIRED>One</a>", doc2.selectFirst("a")!!.outerHtml())
    }
}
