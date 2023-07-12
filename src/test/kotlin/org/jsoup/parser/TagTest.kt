package org.jsoup.parser

import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import org.jsoup.parser.Tag.Companion.isKnownTag
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Tag tests.
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class TagTest {
    @Test
    fun isCaseSensitive() {
        val p1 = Tag.valueOf("P")
        val p2 = Tag.valueOf("p")
        Assertions.assertNotEquals(p1, p2)
    }

    @MultiLocaleTest
    fun canBeInsensitive(locale: Locale?) {
        Locale.setDefault(locale)
        val script1 = Tag.valueOf("script", ParseSettings.htmlDefault)
        val script2 = Tag.valueOf("SCRIPT", ParseSettings.htmlDefault)
        Assertions.assertSame(script1, script2)
    }

    @Test
    fun trims() {
        val p1 = Tag.valueOf("p")
        val p2 = Tag.valueOf(" p ")
        Assertions.assertEquals(p1, p2)
    }

    @Test
    fun equality() {
        val p1 = Tag.valueOf("p")
        val p2 = Tag.valueOf("p")
        Assertions.assertEquals(p1, p2)
        Assertions.assertSame(p1, p2)
    }

    @Test
    fun divSemantics() {
        val div = Tag.valueOf("div")
        Assertions.assertTrue(div.isBlock)
        Assertions.assertTrue(div.formatAsBlock)
    }

    @Test
    fun pSemantics() {
        val p = Tag.valueOf("p")
        Assertions.assertTrue(p.isBlock)
        Assertions.assertFalse(p.formatAsBlock)
    }

    @Test
    fun imgSemantics() {
        val img = Tag.valueOf("img")
        Assertions.assertTrue(img.isInline)
        Assertions.assertTrue(img.isSelfClosing)
        Assertions.assertFalse(img.isBlock)
    }

    @Test
    fun defaultSemantics() {
        val foo = Tag.valueOf("FOO") // not defined
        val foo2 = Tag.valueOf("FOO")
        Assertions.assertEquals(foo, foo2)
        Assertions.assertTrue(foo.isInline)
        Assertions.assertTrue(foo.formatAsBlock)
    }

    @Test
    fun valueOfChecksNotNull() {
        Assertions.assertThrows(NullPointerException::class.java) { Tag.valueOf(null as String) }
    }

    @Test
    fun valueOfChecksNotEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Tag.valueOf(" ") }
    }

    @Test
    fun knownTags() {
        Assertions.assertTrue(isKnownTag("div"))
        Assertions.assertFalse(isKnownTag("explain"))
    }
}
