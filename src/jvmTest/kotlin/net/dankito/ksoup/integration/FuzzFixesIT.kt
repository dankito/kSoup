package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.parse
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

/**
 * Tests fixes for issues raised by the [OSS Fuzz project](https://oss-fuzz.com/testcases?project=jsoup). As
 * some of these are timeout tests - run each file 100 times and ensure under time.
 */
class FuzzFixesIT {

    @ParameterizedTest
    @MethodSource("testFiles")
    fun testHtmlParse(file: File) {
        val startTime = System.currentTimeMillis()
        val completeBy: Long = startTime + timeout * 1000L
        for (i in 0 until numIters) {
            val doc = Jsoup.parse(file, "UTF-8", "https://example.com/")
            Assertions.assertNotNull(doc)
            if (System.currentTimeMillis() > completeBy) {
                Assertions.fail<Any>("Timeout: only completed $i iters of [${file.name}] in $timeout seconds")
            }
        }
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    fun testXmlParse(file: File) {
        val startTime = System.currentTimeMillis()
        val completeBy: Long = startTime + timeout * 1000L
        for (i in 0 until numIters) {
            val doc = Jsoup.parse(file, "UTF-8", "https://example.com/", xmlParser())
            Assertions.assertNotNull(doc)
            if (System.currentTimeMillis() > completeBy) {
                Assertions.fail<Any>("Timeout: only completed $i iters of [${file.name}] in $timeout seconds")
            }
        }
    }

    companion object {
        var numIters = 50
        var timeout = 30 // external fuzzer is set to 60 for 100 runs
        var testDir: File = ParseTest.getFile("/fuzztests/")

        @JvmStatic
        private fun testFiles(): List<File> {
            val files = testDir.listFiles()
            Assertions.assertNotNull(files)
            Validate.notNull(files)
            Assertions.assertTrue(files.size > 10)

            return files.toList()
        }
    }
}
