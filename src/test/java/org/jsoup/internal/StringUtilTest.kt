package org.jsoup.internal

import org.jsoup.Jsoup
import org.jsoup.internal.StringUtil.isAscii
import org.jsoup.internal.StringUtil.isBlank
import org.jsoup.internal.StringUtil.isNumeric
import org.jsoup.internal.StringUtil.isWhitespace
import org.jsoup.internal.StringUtil.padding
import org.jsoup.internal.StringUtil.resolve
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StringUtilTest {
    @Test
    fun join() {
        Assertions.assertEquals("", StringUtil.join(listOf(""), " "))
        Assertions.assertEquals("one", StringUtil.join(listOf("one"), " "))
        Assertions.assertEquals("one two three", StringUtil.join(mutableListOf<String?>("one", "two", "three"), " "))
    }

    @Test
    fun padding() {
        Assertions.assertEquals("", padding(0))
        Assertions.assertEquals(" ", padding(1))
        Assertions.assertEquals("  ", padding(2))
        Assertions.assertEquals("               ", padding(15))
        Assertions.assertEquals("                              ", padding(45)) // we default to tap out at 30

        // memoization is up to 21 blocks (0 to 20 spaces) and exits early before min checks making maxPaddingWidth unused
        Assertions.assertEquals("", padding(0, -1))
        Assertions.assertEquals("                    ", padding(20, -1))

        // this test escapes memoization and continues through
        Assertions.assertEquals("                     ", padding(21, -1))

        // this test escapes memoization and using unlimited length (-1) will allow requested spaces
        Assertions.assertEquals("                              ", padding(30, -1))
        Assertions.assertEquals("                                             ", padding(45, -1))

        // we tap out at 0 for this test
        Assertions.assertEquals("", padding(0, 0))

        // as memoization is escaped, setting zero for max padding will not allow any requested width
        Assertions.assertEquals("", padding(21, 0))

        // we tap out at 30 for these tests making > 30 use 30
        Assertions.assertEquals("", padding(0, 30))
        Assertions.assertEquals(" ", padding(1, 30))
        Assertions.assertEquals("  ", padding(2, 30))
        Assertions.assertEquals("               ", padding(15, 30))
        Assertions.assertEquals("                              ", padding(45, 30))

        // max applies regardless of memoized
        Assertions.assertEquals(5, padding(20, 5).length)
    }

    @Test
    fun paddingInACan() {
        val padding = padding
        Assertions.assertEquals(21, padding.size)
        for (i in padding.indices) {
            Assertions.assertEquals(i, padding[i].length)
        }
    }

    @get:Test
    val isBlank: Unit
        get() {
            Assertions.assertTrue(isBlank(null))
            Assertions.assertTrue(isBlank(""))
            Assertions.assertTrue(isBlank("      "))
            Assertions.assertTrue(isBlank("   \r\n  "))
            Assertions.assertFalse(isBlank("hello"))
            Assertions.assertFalse(isBlank("   hello   "))
        }

    @get:Test
    val isNumeric: Unit
        get() {
            Assertions.assertFalse(isNumeric(null))
            Assertions.assertFalse(isNumeric(" "))
            Assertions.assertFalse(isNumeric("123 546"))
            Assertions.assertFalse(isNumeric("hello"))
            Assertions.assertFalse(isNumeric("123.334"))
            Assertions.assertTrue(isNumeric("1"))
            Assertions.assertTrue(isNumeric("1234"))
        }

    @get:Test
    val isWhitespace: Unit
        get() {
            Assertions.assertTrue(isWhitespace('\t'.code))
            Assertions.assertTrue(isWhitespace('\n'.code))
            Assertions.assertTrue(isWhitespace('\r'.code))
            Assertions.assertTrue(isWhitespace('\u000C'.code))
            Assertions.assertTrue(isWhitespace(' '.code))
            Assertions.assertFalse(isWhitespace('\u00a0'.code))
            Assertions.assertFalse(isWhitespace('\u2000'.code))
            Assertions.assertFalse(isWhitespace('\u3000'.code))
        }

    @Test
    fun normaliseWhiteSpace() {
        Assertions.assertEquals(" ", StringUtil.normaliseWhitespace("    \r \n \r\n"))
        Assertions.assertEquals(" hello there ", StringUtil.normaliseWhitespace("   hello   \r \n  there    \n"))
        Assertions.assertEquals("hello", StringUtil.normaliseWhitespace("hello"))
        Assertions.assertEquals("hello there", StringUtil.normaliseWhitespace("hello\nthere"))
    }

    @Test
    fun normaliseWhiteSpaceHandlesHighSurrogates() {
        val test71540chars = "\ud869\udeb2\u304b\u309a  1"
        val test71540charsExpectedSingleWhitespace = "\ud869\udeb2\u304b\u309a 1"
        Assertions.assertEquals(test71540charsExpectedSingleWhitespace, StringUtil.normaliseWhitespace(test71540chars))
        val extractedText = Jsoup.parse(test71540chars).text()
        Assertions.assertEquals(test71540charsExpectedSingleWhitespace, extractedText)
    }

    @Test
    fun resolvesRelativeUrls() {
        Assertions.assertEquals("http://example.com/one/two?three", resolve("http://example.com", "./one/two?three"))
        Assertions.assertEquals(
            "http://example.com/one/two?three",
            resolve("http://example.com?one", "./one/two?three")
        )
        Assertions.assertEquals(
            "http://example.com/one/two?three#four",
            resolve("http://example.com", "./one/two?three#four")
        )
        Assertions.assertEquals("https://example.com/one", resolve("http://example.com/", "https://example.com/one"))
        Assertions.assertEquals(
            "http://example.com/one/two.html",
            resolve("http://example.com/two/", "../one/two.html")
        )
        Assertions.assertEquals("https://example2.com/one", resolve("https://example.com/", "//example2.com/one"))
        Assertions.assertEquals("https://example.com:8080/one", resolve("https://example.com:8080", "./one"))
        Assertions.assertEquals("https://example2.com/one", resolve("http://example.com/", "https://example2.com/one"))
        Assertions.assertEquals("https://example.com/one", resolve("wrong", "https://example.com/one"))
        Assertions.assertEquals("https://example.com/one", resolve("https://example.com/one", ""))
        Assertions.assertEquals("", resolve("wrong", "also wrong"))
        Assertions.assertEquals("ftp://example.com/one", resolve("ftp://example.com/two/", "../one"))
        Assertions.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "./two.c"))
        Assertions.assertEquals("ftp://example.com/one/two.c", resolve("ftp://example.com/one/", "two.c"))
        // examples taken from rfc3986 section 5.4.2
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "../../../g"))
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "../../../../g"))
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "/./g"))
        Assertions.assertEquals("http://example.com/g", resolve("http://example.com/b/c/d;p?q", "/../g"))
        Assertions.assertEquals("http://example.com/b/c/g.", resolve("http://example.com/b/c/d;p?q", "g."))
        Assertions.assertEquals("http://example.com/b/c/.g", resolve("http://example.com/b/c/d;p?q", ".g"))
        Assertions.assertEquals("http://example.com/b/c/g..", resolve("http://example.com/b/c/d;p?q", "g.."))
        Assertions.assertEquals("http://example.com/b/c/..g", resolve("http://example.com/b/c/d;p?q", "..g"))
        Assertions.assertEquals("http://example.com/b/g", resolve("http://example.com/b/c/d;p?q", "./../g"))
        Assertions.assertEquals("http://example.com/b/c/g/", resolve("http://example.com/b/c/d;p?q", "./g/."))
        Assertions.assertEquals("http://example.com/b/c/g/h", resolve("http://example.com/b/c/d;p?q", "g/./h"))
        Assertions.assertEquals("http://example.com/b/c/h", resolve("http://example.com/b/c/d;p?q", "g/../h"))
        Assertions.assertEquals("http://example.com/b/c/g;x=1/y", resolve("http://example.com/b/c/d;p?q", "g;x=1/./y"))
        Assertions.assertEquals("http://example.com/b/c/y", resolve("http://example.com/b/c/d;p?q", "g;x=1/../y"))
        Assertions.assertEquals("http://example.com/b/c/g?y/./x", resolve("http://example.com/b/c/d;p?q", "g?y/./x"))
        Assertions.assertEquals("http://example.com/b/c/g?y/../x", resolve("http://example.com/b/c/d;p?q", "g?y/../x"))
        Assertions.assertEquals("http://example.com/b/c/g#s/./x", resolve("http://example.com/b/c/d;p?q", "g#s/./x"))
        Assertions.assertEquals("http://example.com/b/c/g#s/../x", resolve("http://example.com/b/c/d;p?q", "g#s/../x"))
    }

    @Test
    fun stripsControlCharsFromUrls() {
        // should resovle to an absolute url:
        Assertions.assertEquals("foo:bar", resolve("\nhttps://\texample.com/", "\r\nfo\to:ba\br"))
    }

    @Test
    fun allowsSpaceInUrl() {
        Assertions.assertEquals("https://example.com/foo bar/", resolve("HTTPS://example.com/example/", "../foo bar/"))
    }

    @get:Test
    val isAscii: Unit
        get() {
            Assertions.assertTrue(isAscii(""))
            Assertions.assertTrue(isAscii("example.com"))
            Assertions.assertTrue(isAscii("One Two"))
            Assertions.assertFalse(isAscii("🧔"))
            Assertions.assertFalse(isAscii("测试"))
            Assertions.assertFalse(isAscii("测试.com"))
        }
}
