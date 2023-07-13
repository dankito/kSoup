package net.dankito.ksoup.helper

import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.helper.DataUtil.getCharsetFromContentType
import net.dankito.ksoup.helper.DataUtil.mimeBoundary
import net.dankito.ksoup.helper.DataUtil.parseInputStream
import net.dankito.ksoup.helper.DataUtil.readToByteBuffer
import net.dankito.ksoup.integration.ParseTest
import net.dankito.ksoup.jvm.Charset
import net.dankito.ksoup.jvm.Charsets
import net.dankito.ksoup.jvm.toByteArray
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*
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
        return ByteArrayInputStream(data.toByteArray(Charsets.UTF_8))
    }

    private fun stream(data: String, charset: String): InputStream {
        return ByteArrayInputStream(data.toByteArray(Charset.forName(charset)))
    }

    @Test
    fun discardsSpuriousByteOrderMark() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val doc = parseInputStream(stream(html), "UTF-8", "http://foo.com/", htmlParser())
        Assertions.assertEquals("One", doc.head().text())
    }

    @Test
    fun discardsSpuriousByteOrderMarkWhenNoCharsetSet() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val doc = parseInputStream(stream(html), null, "http://foo.com/", htmlParser())
        Assertions.assertEquals("One", doc.head().text())
        Assertions.assertEquals("UTF-8", doc.outputSettings().charset()!!.displayName)
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
    fun secondMetaElementWithContentTypeContainsCharsetParameter() {
        val html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" +
                "</head><body>한국어</body></html>"
        val doc = parseInputStream(stream(html, "euc-kr"), null, "http://example.com", htmlParser())
        Assertions.assertEquals("한국어", doc.body().text())
    }

    @Test
    fun firstMetaElementWithCharsetShouldBeUsedForDecoding() {
        val html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=koi8-u\">" +
                "</head><body>Übergrößenträger</body></html>"
        val doc = parseInputStream(stream(html, "iso-8859-1"), null, "http://example.com", htmlParser())
        Assertions.assertEquals("Übergrößenträger", doc.body().text())
    }

    @Test
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
    fun supportsUTF8BOM() {
        val `in`: File = ParseTest.Companion.getFile("/bomtests/bom_utf8.html")
        val doc = parse(`in`, null, "http://example.com")
        Assertions.assertEquals("OK", doc.head().select("title").text())
    }

    @Test
    fun noExtraNULLBytes() {
        val b = "<html><head><meta charset=\"UTF-8\"></head><body><div><u>ü</u>ü</div></body></html>".toByteArray(Charsets.UTF_8)
        val doc = parse(ByteArrayInputStream(b), null, "")
        Assertions.assertFalse(doc.outerHtml().contains("\u0000"))
    }

    @Test
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
    fun supportsXmlCharsetDeclaration() {
        val encoding = "iso-8859-1"
        val soup: InputStream = ByteArrayInputStream(
            ("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
                    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">Hellö Wörld!</html>")
                .toByteArray(Charset.forName(encoding))
        )
        val doc = parse(soup, null, "")
        Assertions.assertEquals("Hellö Wörld!", doc.body().text())
    }

    @Test
    fun lLoadsGzipFile() {
        val `in`: File = ParseTest.getFile("/htmltests/gzip.html.gz")
        val doc = parse(`in`, null)
        Assertions.assertEquals("Gzip test", doc.title())
        Assertions.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p")!!.text())
    }

    @Test
    fun loadsZGzipFile() {
        // compressed on win, with z suffix
        val `in`: File = ParseTest.getFile("/htmltests/gzip.html.z")
        val doc = parse(`in`, null)
        Assertions.assertEquals("Gzip test", doc.title())
        Assertions.assertEquals("This is a gzipped HTML file.", doc.selectFirst("p")!!.text())
    }

    @Test
    fun handlesFakeGzipFile() {
        val `in`: File = ParseTest.getFile("/htmltests/fake-gzip.html.gz")
        val doc = parse(`in`, null)
        Assertions.assertEquals("This is not gzipped", doc.title())
        Assertions.assertEquals("And should still be readable.", doc.selectFirst("p")!!.text())
    }

    // an input stream to give a range of output sizes, that changes on each read
    internal class VaryingReadInputStream(val `in`: InputStream) : InputStream() {
        var stride = 0

        override fun read(): Int {
            return `in`.read()
        }

        override fun read(b: ByteArray): Int {
            return `in`.read(b, 0, Math.min(b.size, ++stride))
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            return `in`.read(b, off, Math.min(len, ++stride))
        }
    }

    @Test
    fun handlesChunkedInputStream() {
        val inputFile: File = ParseTest.getFile("/htmltests/large.html")
        val input: String = ParseTest.getFileAsString(inputFile)
        val stream = VaryingReadInputStream(ParseTest.inputStreamFrom(input))
        val expected = parse(input, "https://example.com")
        val doc = parse(stream, null, "https://example.com")
        Assertions.assertTrue(doc.hasSameValue(expected))
    }

    @Test
    fun handlesUnlimitedRead() {
        val inputFile: File = ParseTest.getFile("/htmltests/large.html")
        val input: String = ParseTest.getFileAsString(inputFile)
        val stream = VaryingReadInputStream(ParseTest.inputStreamFrom(input))
        val byteBuffer = readToByteBuffer(stream, 0)
        val read = String(byteBuffer!!.array())
        Assertions.assertEquals(input, read)
    }
}
