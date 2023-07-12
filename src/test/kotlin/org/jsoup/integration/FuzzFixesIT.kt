package org.jsoup.integration

import org.jsoup.Jsoup
import org.jsoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.*
import java.util.stream.Stream

/**
 * Tests fixes for issues raised by the [OSS Fuzz project](https://oss-fuzz.com/testcases?project=jsoup). As
 * some of these are timeout tests - run each file 100 times and ensure under time.
 */
class FuzzFixesIT {
    @ParameterizedTest
    @MethodSource("testFiles")
    @Throws(IOException::class)
    fun testHtmlParse(file: File) {
        val startTime = System.currentTimeMillis()
        val completeBy: Long = startTime + FuzzFixesIT.Companion.timeout * 1000L
        for (i in 0 until FuzzFixesIT.Companion.numIters) {
            val doc = Jsoup.parse(file, "UTF-8", "https://example.com/")
            Assertions.assertNotNull(doc)
            if (System.currentTimeMillis() > completeBy) Assertions.fail<Any>(
                String.format(
                    "Timeout: only completed %d iters of [%s] in %d seconds",
                    i,
                    file.name,
                    FuzzFixesIT.Companion.timeout
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    @Throws(IOException::class)
    fun testXmlParse(file: File) {
        val startTime = System.currentTimeMillis()
        val completeBy: Long = startTime + FuzzFixesIT.Companion.timeout * 1000L
        for (i in 0 until FuzzFixesIT.Companion.numIters) {
            val doc = Jsoup.parse(file, "UTF-8", "https://example.com/", xmlParser())
            Assertions.assertNotNull(doc)
            if (System.currentTimeMillis() > completeBy) Assertions.fail<Any>(
                String.format(
                    "Timeout: only completed %d iters of [%s] in %d seconds",
                    i,
                    file.name,
                    FuzzFixesIT.Companion.timeout
                )
            )
        }
    }

    companion object {
        var numIters = 50
        var timeout = 30 // external fuzzer is set to 60 for 100 runs
        var testDir: File = ParseTest.Companion.getFile("/fuzztests/")

        @JvmStatic
        private fun testFiles(): Stream<File> {
            val files: Array<File> = FuzzFixesIT.Companion.testDir.listFiles()
            Assertions.assertNotNull(files)
            Assertions.assertTrue(files.size > 10)
            return Stream.of(*files)
        }
    }
}
