package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ElementTestJvm {

    @ParameterizedTest
    @MethodSource("testOutputSettings")
    fun prettySerializationRoundTrips(settings: Document.OutputSettings?) {
        // https://github.com/jhy/jsoup/issues/1688
        // tests that repeated html() and Jsoup.parse() does not accumulate errant spaces / newlines
        val doc = Jsoup.parse("<div>\nFoo\n<p>\nBar\nqux</p></div>\n<script>\n alert('Hello!');\n</script>")
        doc.outputSettings(settings!!)
        val html = doc.html()
        val doc2 = Jsoup.parse(html)
        doc2.outputSettings(settings)
        val html2 = doc2.html()
        assertEquals(html, html2)
    }

    companion object {

        @JvmStatic
        private fun testOutputSettings(): List<Document.OutputSettings> {
            return listOf(
                Document.OutputSettings().prettyPrint(true).indentAmount(4),
                Document.OutputSettings().prettyPrint(true).indentAmount(1),
                Document.OutputSettings().prettyPrint(true).indentAmount(4).outline(true),
                Document.OutputSettings().prettyPrint(false)
            )
        }

    }
}