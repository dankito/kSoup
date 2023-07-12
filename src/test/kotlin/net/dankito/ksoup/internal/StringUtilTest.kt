package net.dankito.ksoup.internal

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.internal.StringUtil.resolve
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
        Assertions.assertEquals("", StringUtil.padding(0))
        Assertions.assertEquals(" ", StringUtil.padding(1))
        Assertions.assertEquals("  ", StringUtil.padding(2))
        Assertions.assertEquals("               ", StringUtil.padding(15))
        Assertions.assertEquals("                              ", StringUtil.padding(45)) // we default to tap out at 30

        // memoization is up to 21 blocks (0 to 20 spaces) and exits early before min checks making maxPaddingWidth unused
        Assertions.assertEquals("", StringUtil.padding(0, -1))
        Assertions.assertEquals("                    ", StringUtil.padding(20, -1))

        // this test escapes memoization and continues through
        Assertions.assertEquals("                     ", StringUtil.padding(21, -1))

        // this test escapes memoization and using unlimited length (-1) will allow requested spaces
        Assertions.assertEquals("                              ", StringUtil.padding(30, -1))
        Assertions.assertEquals("                                             ", StringUtil.padding(45, -1))

        // we tap out at 0 for this test
        Assertions.assertEquals("", StringUtil.padding(0, 0))

        // as memoization is escaped, setting zero for max padding will not allow any requested width
        Assertions.assertEquals("", StringUtil.padding(21, 0))

        // we tap out at 30 for these tests making > 30 use 30
        Assertions.assertEquals("", StringUtil.padding(0, 30))
        Assertions.assertEquals(" ", StringUtil.padding(1, 30))
        Assertions.assertEquals("  ", StringUtil.padding(2, 30))
        Assertions.assertEquals("               ", StringUtil.padding(15, 30))
        Assertions.assertEquals("                              ", StringUtil.padding(45, 30))

        // max applies regardless of memoized
        Assertions.assertEquals(5, StringUtil.padding(20, 5).length)
    }

    @Test
    fun paddingInACan() {
        val padding = StringUtil.padding
        Assertions.assertEquals(21, padding.size)
        for (i in padding.indices) {
            Assertions.assertEquals(i, padding[i].length)
        }
    }

    @Test
    fun isBlank() {
        Assertions.assertTrue(StringUtil.isBlank(null))
        Assertions.assertTrue(StringUtil.isBlank(""))
        Assertions.assertTrue(StringUtil.isBlank("      "))
        Assertions.assertTrue(StringUtil.isBlank("   \r\n  "))
        Assertions.assertFalse(StringUtil.isBlank("hello"))
        Assertions.assertFalse(StringUtil.isBlank("   hello   "))
    }

    @Test
    fun isNumeric() {
        Assertions.assertFalse(StringUtil.isNumeric(null))
        Assertions.assertFalse(StringUtil.isNumeric(" "))
        Assertions.assertFalse(StringUtil.isNumeric("123 546"))
        Assertions.assertFalse(StringUtil.isNumeric("hello"))
        Assertions.assertFalse(StringUtil.isNumeric("123.334"))
        Assertions.assertTrue(StringUtil.isNumeric("1"))
        Assertions.assertTrue(StringUtil.isNumeric("1234"))
    }

    @Test
    fun isWhitespace() {
        Assertions.assertTrue(StringUtil.isWhitespace('\t'.code))
        Assertions.assertTrue(StringUtil.isWhitespace('\n'.code))
        Assertions.assertTrue(StringUtil.isWhitespace('\r'.code))
        Assertions.assertTrue(StringUtil.isWhitespace('\u000C'.code))
        Assertions.assertTrue(StringUtil.isWhitespace(' '.code))
        Assertions.assertFalse(StringUtil.isWhitespace('\u00a0'.code))
        Assertions.assertFalse(StringUtil.isWhitespace('\u2000'.code))
        Assertions.assertFalse(StringUtil.isWhitespace('\u3000'.code))
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

    @Test
    fun isAscii() {
        Assertions.assertTrue(StringUtil.isAscii(""))
        Assertions.assertTrue(StringUtil.isAscii("example.com"))
        Assertions.assertTrue(StringUtil.isAscii("One Two"))
        Assertions.assertFalse(StringUtil.isAscii("🧔"))
        Assertions.assertFalse(StringUtil.isAscii("测试"))
        Assertions.assertFalse(StringUtil.isAscii("测试.com"))
    }
}
