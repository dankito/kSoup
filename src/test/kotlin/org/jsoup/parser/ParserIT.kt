package org.jsoup.parser

import org.jsoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * Longer running Parser tests.
 */
class ParserIT {
    @Test
    @Disabled // disabled by default now, as there more specific unconsume tests
    fun testIssue1251() {
        // https://github.com/jhy/jsoup/issues/1251
        val str = StringBuilder("<a href=\"\"ca")
        for (countSpaces in 0..99999) {
            try {
                htmlParser().setTrackErrors(1).parseInput(str.toString(), "")
            } catch (e: Exception) {
                throw AssertionError("failed at length " + str.length, e)
            }
            str.insert(countSpaces, ' ')
        }
    }

    @Test
    fun handlesDeepStack() {
        // inspired by http://sv.stargate.wikia.com/wiki/M2J and https://github.com/jhy/jsoup/issues/955
        // I didn't put it in the integration tests, because explorer and intellij kept dieing trying to preview/index it

        // Arrange
        val longBody = StringBuilder(500000)
        for (i in 0..24999) {
            longBody.append(i).append("<dl><dd>")
        }
        for (i in 0..24999) {
            longBody.append(i).append("</dd></dl>")
        }

        // Act
        val start = System.currentTimeMillis()
        val doc = Parser.parseBodyFragment(longBody.toString(), "")

        // Assert
        Assertions.assertEquals(2, doc.body().childNodeSize())
        Assertions.assertEquals(25000, doc.select("dd").size)
        Assertions.assertTrue(System.currentTimeMillis() - start < 20000) // I get ~ 1.5 seconds, but others have reported slower
        // was originally much longer, or stack overflow.
    }
}
