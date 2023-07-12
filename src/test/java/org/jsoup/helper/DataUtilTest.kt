package org.jsoup.helper

import org.jsoup.Jsoup.parse
import org.jsoup.helper.DataUtil.getCharsetFromContentType
import org.jsoup.helper.DataUtil.mimeBoundary
import org.jsoup.helper.DataUtil.parseInputStream
import org.jsoup.helper.DataUtil.readToByteBuffer
import org.jsoup.integration.ParseTest
import org.jsoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class DataUtilTest {
    @Test
    fun testCharset() {
        Assertions.assertEquals("utf-8", getCharsetFromContentType("text/html;charset=utf-8 "))
        Assertions.assertEquals("UTF-8", getCharsetFromContentType("text/html; charset=UTF-8"))
        Assertions.assertEquals("ISO-8859-1", getCharsetFromContentType("text/html; charset=ISO-8859-1"))
        Assertions.assertNull(getCharsetFromContentType("text/html"))
        Assertions.assertNull(getCharsetFromContentType(null))
        Assertions.assertNull(getCharsetFromContentType("text/html;charset=Unknown"))
    }

    @Test
    fun testQuotedCharset() {
        Assertions.assertEquals("utf-8", getCharsetFromContentType("text/html; charset=\"utf-8\""))
        Assertions.assertEquals("UTF-8", getCharsetFromContentType("text/html;charset=\"UTF-8\""))
        Assertions.assertEquals("ISO-8859-1", getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""))
        Assertions.assertNull(getCharsetFromContentType("text/html; charset=\"Unsupported\""))
        Assertions.assertEquals("UTF-8", getCharsetFromContentType("text/html; charset='UTF-8'"))
    }

    private fun stream(data: String): InputStream {
        return ByteArrayInputStream(data.toByteArray(StandardCharsets.UTF_8))
    }

    private fun stream(data: String, charset: String): InputStream {
        return ByteArrayInputStream(data.toByteArray(Charset.forName(charset)))
    }

    @Test
    @Throws(IOException::class)
    fun discardsSpuriousByteOrderMark() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val doc = parseInputStream(stream(html), "UTF-8", "http://foo.com/", htmlParser())
        Assertions.assertEquals("One", doc.head().text())
    }

    @Test
    @Throws(IOException::class)
    fun discardsSpuriousByteOrderMarkWhenNoCharsetSet() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val doc = parseInputStream(stream(html), null, "http://foo.com/", htmlParser())
        Assertions.assertEquals("One", doc.head().text())
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.displayName())
    }

    @Test
    fun shouldNotThrowExceptionOnEmptyCharset() {
        Assertions.assertNull(getCharsetFromContentType("text/html; charset="))
        Assertions.assertNull(getCharsetFromContentType("text/html; charset=;"))
    }

    @Test
    fun shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        Assertions.assertEquals("ISO-8859-1", getCharsetFromContentType("text/html; charset=ISO-8859-1, charset=1251"))
    }

    @Test
    fun shouldCorrectCharsetForDuplicateCharsetString() {
        Assertions.assertEquals("iso-8859-1", getCharsetFromContentType("text/html; charset=charset=iso-8859-1"))
    }

    @Test
    fun shouldReturnNullForIllegalCharsetNames() {
        Assertions.assertNull(getCharsetFromContentType("text/html; charset=\$HJKDF§$/("))
    }

    @Test
    fun generatesMimeBoundaries() {
        val m1 = mimeBoundary()
        val m2 = mimeBoundary()
        Assertions.assertEquals(DataUtil.boundaryLength, m1.length)
        Assertions.assertEquals(DataUtil.boundaryLength, m2.length)
        Assertions.assertNotSame(m1, m2)
    }

    @Test
    @Throws(IOException::class)
    fun wrongMetaCharsetFallback() {
        val html = "<html><head><meta charset=iso-8></head><body></body></html>"
        val doc = parseInputStream(stream(html), null, "http://example.com", htmlParser())
        val expected = """<html>
 <head>
  <meta charset="iso-8">
 </head>
 <body></body>
</html>"""
        Assertions.assertEquals(expected, doc.toString())
    }

    @Test
    @Throws(Exception::class)
    fun secondMetaElementWithContentTypeContainsCharsetParameter() {
        val html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" +
                "</head><body>한국어</body></html>"
        val doc = parseInputStream(stream(html, "euc-kr"), null, "http://example.com", htmlParser())
        Assertions.assertEquals("한국어", doc.body().text())
    }

    @Test
    @Throws(Exception::class)
    fun firstMetaElementWithCharsetShouldBeUsedForDecoding() {
        val html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=koi8-u\">" +
                "</head><body>Übergrößenträger</body></html>"
        val doc = parseInputStream(stream(html, "iso-8859-1"), null, "http://example.com", htmlParser())
        Assertions.assertEquals("Übergrößenträger", doc.body().text())
    }

    @Test
    @Throws(IOException::class)
    fun parseSequenceInputStream() {
        // https://github.com/jhy/jsoup/pull/1671
        val `in`: File = ParseTest.Companion.getFile("/htmltests/medium.html")
        val fileContent = String(Files.readAllBytes(`in`.toPath()))
        val halfLength = fileContent.length / 2
        val firstPart = fileContent.substring(0, halfLength)
        val secondPart = fileContent.substring(halfLength)
        val sequenceStream = SequenceInputStream(
            stream(firstPart),
            stream(secondPart)
        )
        val doc = parseInputStream(sequenceStream, null, "", htmlParser())
        Assertions.assertEquals(fileContent, doc.outerHtml())
    }

    @Test
    @Throws(IOException::class)
    fun supportsBOMinFiles() {
        // test files from http://www.i18nl10n.com/korean/utftest/
        var `in`: File = ParseTest.Companion.getFile("/bomtests/bom_utf16be.html")
        var doc = parse(`in`, null, "http://example.com")
        Assertions.assertTrue(doc.title().contains("UTF-16BE"))
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"))
        `in` = ParseTest.Companion.getFile("/bomtests/bom_utf16le.html")
        doc = parse(`in`, null, "http://example.com")
        Assertions.assertTrue(doc.title().contains("UTF-16LE"))
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"))
        `in` = ParseTest.Companion.getFile("/bomtests/bom_utf32be.html")
        doc = parse(`in`, null, "http://example.com")
        Assertions.assertTrue(doc.title().contains("UTF-32BE"))
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"))
        `in` = ParseTest.Companion.getFile("/bomtests/bom_utf32le.html")
        doc = parse(`in`, null, "http://example.com")
        Assertions.assertTrue(doc.title().contains("UTF-32LE"))
        Assertions.assertTrue(doc.text().contains("가각갂갃간갅"))
    }

    @Test
    @Throws(IOException::class)
    fun supportsUTF8BOM() {
        val `in`: File = ParseTest.Companion.getFile("/bomtests/bom_utf8.html")
        val doc = parse(`in`, null, "http://example.com")
        Assertions.assertEquals("OK", doc.head().select("title").text())
    }

    @Test
    @Throws(IOException::class)
    fun noExtraNULLBytes() {
        val b = "<html><head><meta charset=\"UTF-8\"></head><body><div><u>ü</u>ü</div></body></html>".toByteArray(
            StandardCharsets.UTF_8
        )
        val doc = parse(ByteArrayInputStream(b), null, "")
        Assertions.assertFalse(doc.outerHtml().contains("\u0000"))
    }

    @Test
    @Throws(IOException::class)
    fun supportsZippedUTF8BOM() {
        val `in`: File = ParseTest.Companion.getFile("/bomtests/bom_utf8.html.gz")
        val doc = parse(`in`, null, "http://example.com")
        Assertions.assertEquals("OK", doc.head().select("title").text())
        Assertions.assertEquals(
            "There is a UTF8 BOM at the top (before the XML decl). If not read correctly, will look like a non-joining space.",
            doc.body().text()
        )
    }

    @Test
    @Throws(IOException::class)
    fun supportsXmlCharsetDeclaration() {
        val encoding = "iso-8859-1"
        val soup: InputStream = ByteArrayInputStream(
            ("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
                    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">Hellö Wörld!</html>").toByteArray(
                Charset.forName(encoding)
            )
        )
        val doc = parse(soup, null, "")
        Assertions.assertEquals("Hellö Wörld!", doc.body().text())
    }

    @Test
    @Throws(IOException::class)
    fun lLoadsGzipFile() {
        val `in`: File = ParseTest.Companion.getFile("/htmltests/gzip.html.gz")
        val doc = parse(`in`, null)
        Assertions.assertEquals("Gzip test", doc.title())
        Assertions.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p")!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun loadsZGzipFile() {
        // compressed on win, with z suffix
        val `in`: File = ParseTest.Companion.getFile("/htmltests/gzip.html.z")
        val doc = parse(`in`, null)
        Assertions.assertEquals("Gzip test", doc.title())
        Assertions.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p")!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun handlesFakeGzipFile() {
        val `in`: File = ParseTest.Companion.getFile("/htmltests/fake-gzip.html.gz")
        val doc = parse(`in`, null)
        Assertions.assertEquals("This is not gzipped", doc.title())
        Assertions.assertEquals("And should still be readable.", doc.selectFirst("p")!!.text())
    }

    // an input stream to give a range of output sizes, that changes on each read
    internal class VaryingReadInputStream(val `in`: InputStream) : InputStream() {
        var stride = 0
        @Throws(IOException::class)
        override fun read(): Int {
            return `in`.read()
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            return `in`.read(b, 0, Math.min(b.size, ++stride))
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            return `in`.read(b, off, Math.min(len, ++stride))
        }
    }

    @Test
    @Throws(IOException::class)
    fun handlesChunkedInputStream() {
        val inputFile: File = ParseTest.Companion.getFile("/htmltests/large.html")
        val input: String = ParseTest.Companion.getFileAsString(inputFile)
        val stream = VaryingReadInputStream(ParseTest.Companion.inputStreamFrom(input))
        val expected = parse(input, "https://example.com")
        val doc = parse(stream, null, "https://example.com")
        Assertions.assertTrue(doc.hasSameValue(expected))
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnlimitedRead() {
        val inputFile: File = ParseTest.Companion.getFile("/htmltests/large.html")
        val input: String = ParseTest.Companion.getFileAsString(inputFile)
        val stream = VaryingReadInputStream(ParseTest.Companion.inputStreamFrom(input))
        val byteBuffer = readToByteBuffer(stream, 0)
        val read = String(byteBuffer!!.array())
        Assertions.assertEquals(input, read)
    }
}
