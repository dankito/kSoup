package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.parse
import net.dankito.ksoup.platform.Charsets
import net.dankito.ksoup.platform.toByteArray
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTestJvm {

    @Test
    fun testUtf8() {
        // testcase for https://github.com/jhy/jsoup/issues/1557. no repro.
        val parsed = Jsoup.parse(ByteArrayInputStream("<p>H\u00E9llo, w\u00F6rld!".toByteArray(Charsets.UTF_8)), null, "")
        val text = parsed.selectFirst("p")!!.wholeText()
        assertEquals(text, "H\u00E9llo, w\u00F6rld!")
    }

}
