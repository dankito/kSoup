package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.helper.DataUtil.readToByteBuffer
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.zip.GZIPInputStream

/**
 * Integration test: parses from real-world example HTML.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class ParseTest {

    @Test
    fun testHtml5Charset() {
        // test that <meta charset="gb2312"> works
        var `in`: File = getFile("/htmltests/meta-charset-1.html")
        var doc = parse(`in`, null, "http://example.com/") //gb2312, has html5 <meta charset>
        Assertions.assertEquals("新", doc.text())
        Assertions.assertEquals("GB2312", doc.outputSettings().charset()!!.displayName())

        // double check, no charset, falls back to utf8 which is incorrect
        `in` = getFile("/htmltests/meta-charset-2.html") //
        doc = parse(`in`, null, "http://example.com") // gb2312, no charset
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.displayName())
        Assertions.assertNotEquals("新", doc.text())

        // confirm fallback to utf8
        `in` = getFile("/htmltests/meta-charset-3.html")
        doc = parse(`in`, null, "http://example.com/") // utf8, no charset
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.displayName())
        Assertions.assertEquals("新", doc.text())
    }

    @Test
    fun testBrokenHtml5CharsetWithASingleDoubleQuote() {
        val `in`: InputStream = inputStreamFrom(
            """
    <html>
    <head><meta charset=UTF-8"></head>
    <body></body>
    </html>
    """.trimIndent()
        )
        val doc = parse(`in`, null, "http://example.com/")
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.displayName())
    }

    @Test
    fun testLowercaseUtf8Charset() {
        val `in`: File = getFile("/htmltests/lowercase-charset-test.html")
        val doc = parse(`in`, null)
        val form = doc.select("#form").first()
        Assertions.assertEquals(2, form!!.children().size)
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.name())
    }

    @Test
    fun testXwiki() {
        // https://github.com/jhy/jsoup/issues/1324
        // this tests that when in CharacterReader we hit a buffer while marked, we preserve the mark when buffered up and can rewind
        val `in`: File = getFile("/htmltests/xwiki-1324.html.gz")
        val doc = parse(`in`, null, "https://localhost/")
        Assertions.assertEquals("XWiki Jetty HSQLDB 12.1-SNAPSHOT", doc.select("#xwikiplatformversion").text())

        // was getting busted at =userdirectory, because it hit the bufferup point but the mark was then lost. so
        // updated to preserve the mark.
        val wantHtml =
            "<a class=\"list-group-item\" data-id=\"userdirectory\" href=\"/xwiki/bin/admin/XWiki/XWikiPreferences?editor=globaladmin&amp;section=userdirectory\" title=\"Customize the user directory live table.\">User Directory</a>"
        Assertions.assertEquals(wantHtml, doc.select("[data-id=userdirectory]").outerHtml())
    }

    @Test
    fun testXwikiExpanded() {
        // https://github.com/jhy/jsoup/issues/1324
        // this tests that if there is a huge illegal character reference, we can get through a buffer and rewind, and still catch that it's an invalid refence,
        // and the parse tree is correct.
        val `in`: File = getFile("/htmltests/xwiki-edit.html.gz")
        val parser = htmlParser()
        val doc = parse(GZIPInputStream(FileInputStream(`in`)), "UTF-8", "https://localhost/", parser.setTrackErrors(100))
        val errors = parser.errors
        Assertions.assertEquals("XWiki Jetty HSQLDB 12.1-SNAPSHOT", doc.select("#xwikiplatformversion").text())
        Assertions.assertEquals(0, errors.size) // not an invalid reference because did not look legit

        // was getting busted at =userdirectory, because it hit the bufferup point but the mark was then lost. so
        // updated to preserve the mark.
        val wantHtml = "<a class=\"list-group-item\" data-id=\"userdirectory\" href=\"/xwiki/bin/admin/XWiki/XWikiPreferences?editor=globaladmin&amp;RIGHTHERERIGHTHERERIGHTHERERIGHTHERE"
        Assertions.assertTrue(doc.select("[data-id=userdirectory]").outerHtml().startsWith(wantHtml))
    }

    @Test
    fun testWikiExpandedFromString() {
        val `in`: File = getFile("/htmltests/xwiki-edit.html.gz")
        val html: String = getFileAsString(`in`)
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("XWiki Jetty HSQLDB 12.1-SNAPSHOT", doc.select("#xwikiplatformversion").text())
        val wantHtml =
            "<a class=\"list-group-item\" data-id=\"userdirectory\" href=\"/xwiki/bin/admin/XWiki/XWikiPreferences?editor=globaladmin&amp;RIGHTHERERIGHTHERERIGHTHERERIGHTHERE"
        Assertions.assertTrue(doc.select("[data-id=userdirectory]").outerHtml().startsWith(wantHtml))
    }

    @Test
    fun testWikiFromString() {
        val `in`: File = getFile("/htmltests/xwiki-1324.html.gz")
        val html: String = getFileAsString(`in`)
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("XWiki Jetty HSQLDB 12.1-SNAPSHOT", doc.select("#xwikiplatformversion").text())
        val wantHtml =
            "<a class=\"list-group-item\" data-id=\"userdirectory\" href=\"/xwiki/bin/admin/XWiki/XWikiPreferences?editor=globaladmin&amp;section=userdirectory\" title=\"Customize the user directory live table.\">User Directory</a>"
        Assertions.assertEquals(wantHtml, doc.select("[data-id=userdirectory]").outerHtml())
    }

    @Test
    fun testFileParseNoCharsetMethod() {
        val `in`: File = getFile("/htmltests/xwiki-1324.html.gz")
        val doc = parse(`in`)
        Assertions.assertEquals("XWiki Jetty HSQLDB 12.1-SNAPSHOT", doc.select("#xwikiplatformversion").text())
    }

    companion object {
        fun getFile(resourceName: String?): File {
            return try {
                val resource = ParseTest::class.java.getResource(resourceName)
                if (resource != null) File(resource.toURI()) else File("/404")
            } catch (e: URISyntaxException) {
                throw IllegalStateException(e)
            }
        }

        fun inputStreamFrom(s: String): InputStream {
            return ByteArrayInputStream(s.toByteArray(StandardCharsets.UTF_8))
        }

        fun getFileAsString(file: File): String {
            val bytes: ByteArray
            bytes = if (file.name.endsWith(".gz")) {
                val stream: InputStream = GZIPInputStream(FileInputStream(file))
                val byteBuffer = readToByteBuffer(stream, 0)
                byteBuffer!!.array()
            } else {
                Files.readAllBytes(file.toPath())
            }
            return String(bytes)
        }
    }
}
