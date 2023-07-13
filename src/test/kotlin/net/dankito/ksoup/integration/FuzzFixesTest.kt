package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

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
    fun testHtmlParse(file: File?) {
        val doc = Jsoup.parse(file!!, "UTF-8", "https://example.com/")
        Assertions.assertNotNull(doc)
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    fun testXmlParse(file: File?) {
        val doc = Jsoup.parse(file!!, "UTF-8", "https://example.com/", xmlParser())
        Assertions.assertNotNull(doc)
    }

    companion object {
        @JvmStatic
        private fun testFiles(): List<File> {
            val files = FuzzFixesIT.testDir.listFiles()
            Validate.notNull(files)
            Assertions.assertTrue(files.size > 10)

            return files.toList()
        }
    }
}
