package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.*
import java.util.stream.Stream

/**
 * Tests fixes for issues raised by the OSS Fuzz project @ https://oss-fuzz.com/testcases?project=jsoup. Contains inline
 * string cases causing exceptions. Timeout tests are in FuzzFixesIT.
 */
class FuzzFixesTest {
    @Test
    fun blankAbsAttr() {
        // https://github.com/jhy/jsoup/issues/1541
        val html = "b<bodY abs: abs:abs: abs:abs:abs>"
        val doc = Jsoup.parse(html)
        Assertions.assertNotNull(doc)
    }

    @Test
    fun bookmark() {
        // https://github.com/jhy/jsoup/issues/1576
        val html = "<?a<U<P<A "
        val doc = Jsoup.parse(html)
        Assertions.assertNotNull(doc)
        val xmlDoc = xmlParser().parseInput(html, "")
        Assertions.assertNotNull(xmlDoc)
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    @Throws(IOException::class)
    fun testHtmlParse(file: File?) {
        val doc = parse(file!!, "UTF-8", "https://example.com/")
        Assertions.assertNotNull(doc)
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    @Throws(IOException::class)
    fun testXmlParse(file: File?) {
        val doc = parse(file!!, "UTF-8", "https://example.com/", xmlParser())
        Assertions.assertNotNull(doc)
    }

    companion object {
        @JvmStatic
        private fun testFiles(): Stream<File> {
            val files: Array<File> = FuzzFixesIT.testDir.listFiles()
            Assertions.assertNotNull(files)
            Assertions.assertTrue(files.size > 10)
            return Stream.of(*files)
        }
    }
}
