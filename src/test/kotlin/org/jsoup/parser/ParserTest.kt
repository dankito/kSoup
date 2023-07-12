package org.jsoup.parser

import org.jsoup.Jsoup.parse
import org.jsoup.parser.Parser.Companion.unescapeEntities
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

class ParserTest {
    @Test
    fun unescapeEntities() {
        val s = unescapeEntities("One &amp; Two", false)
        Assertions.assertEquals("One & Two", s)
    }

    @Test
    fun unescapeEntitiesHandlesLargeInput() {
        val longBody = StringBuilder(500000)
        do {
            longBody.append("SomeNonEncodedInput")
        } while (longBody.length < 64 * 1024)
        val body = longBody.toString()
        Assertions.assertEquals(body, unescapeEntities(body, false))
    }

    @Test
    @Throws(IOException::class)
    fun testUtf8() {
        // testcase for https://github.com/jhy/jsoup/issues/1557. no repro.
        val parsed =
            parse(ByteArrayInputStream("<p>H\u00E9llo, w\u00F6rld!".toByteArray(StandardCharsets.UTF_8)), null, "")
        val text = parsed.selectFirst("p")!!.wholeText()
        Assertions.assertEquals(text, "H\u00E9llo, w\u00F6rld!")
    }
}
