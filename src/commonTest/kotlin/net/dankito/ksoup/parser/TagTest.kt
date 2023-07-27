package net.dankito.ksoup.parser

import net.dankito.ksoup.parser.Tag.Companion.isKnownTag
import net.dankito.ksoup.test.NullPointerExceptionOfPlatform
import kotlin.test.*

/**
 * Tag tests.
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class TagTest {

    @Test
    fun isCaseSensitive() {
        val p1 = Tag.valueOf("P")
        val p2 = Tag.valueOf("p")
        assertNotEquals(p1, p2)
    }

    @Test
    fun trims() {
        val p1 = Tag.valueOf("p")
        val p2 = Tag.valueOf(" p ")
        assertEquals(p1, p2)
    }

    @Test
    fun equality() {
        val p1 = Tag.valueOf("p")
        val p2 = Tag.valueOf("p")
        assertEquals(p1, p2)
        assertSame(p1, p2)
    }

    @Test
    fun divSemantics() {
        val div = Tag.valueOf("div")
        assertTrue(div.isBlock)
        assertTrue(div.formatAsBlock)
    }

    @Test
    fun pSemantics() {
        val p = Tag.valueOf("p")
        assertTrue(p.isBlock)
        assertFalse(p.formatAsBlock)
    }

    @Test
    fun imgSemantics() {
        val img = Tag.valueOf("img")
        assertTrue(img.isInline)
        assertTrue(img.isSelfClosing)
        assertFalse(img.isBlock)
    }

    @Test
    fun defaultSemantics() {
        val foo = Tag.valueOf("FOO") // not defined
        val foo2 = Tag.valueOf("FOO")
        assertEquals(foo, foo2)
        assertTrue(foo.isInline)
        assertTrue(foo.formatAsBlock)
    }

    @Test
    fun valueOfChecksNotNull() {
        assertFailsWith(NullPointerExceptionOfPlatform) {
            Tag.valueOf(null as String)
        }
    }

    @Test
    fun valueOfChecksNotEmpty() {
        assertFailsWith(IllegalArgumentException::class) { Tag.valueOf(" ") }
    }

    @Test
    fun knownTags() {
        assertTrue(isKnownTag("div"))
        assertFalse(isKnownTag("explain"))
    }
}
