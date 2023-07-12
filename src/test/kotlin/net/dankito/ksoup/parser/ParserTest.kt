package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class ParserTest {
    @Test
    fun unescapeEntities() {
        val s = Parser.unescapeEntities("One &amp; Two", false)
        Assertions.assertEquals("One & Two", s)
    }

    @Test
    fun unescapeEntitiesHandlesLargeInput() {
        val longBody = StringBuilder(500000)
        do {
            longBody.append("SomeNonEncodedInput")
        } while (longBody.length < 64 * 1024)
        val body = longBody.toString()
        Assertions.assertEquals(body, Parser.unescapeEntities(body, false))
    }

    @Test
    fun testUtf8() {
        // testcase for https://github.com/jhy/jsoup/issues/1557. no repro.
        val parsed = Jsoup.parse(ByteArrayInputStream("<p>H\u00E9llo, w\u00F6rld!".toByteArray(StandardCharsets.UTF_8)), null, "")
        val text = parsed.selectFirst("p")!!.wholeText()
        Assertions.assertEquals(text, "H\u00E9llo, w\u00F6rld!")
    }
}
