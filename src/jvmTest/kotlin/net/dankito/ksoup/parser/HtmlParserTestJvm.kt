package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.integration.ParseTest
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.parse
import net.dankito.ksoup.parser.*
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.io.ByteArrayInputStream

/**
 * Tests for the Parser
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class HtmlParserTestJvm {

    @ParameterizedTest
    @MethodSource("dupeAttributeData")
    fun dropsDuplicateAttributes(html: String?, expected: String?) {
        val parser = htmlParser().setTrackErrors(10)
        val doc = parser.parseInput(html!!, "")
        val el = doc.expectFirst("body > *")
        Assertions.assertEquals(expected, el.outerHtml()) // normalized names due to lower casing
        val tag = el.normalName()
        Assertions.assertEquals(1, parser.errors.size)
        Assertions.assertEquals("Dropped duplicate attribute(s) in tag [$tag]", parser.errors[0].errorMessage)
    }

    @Test
    fun handlesManyChildren() {
        // Arrange
        val longBody = StringBuilder(500000)
        for (i in 0..24999) {
            longBody.append(i).append("<br>")
        }

        // Act
        val start = System.currentTimeMillis()
        val doc = Parser.parseBodyFragment(longBody.toString(), "")

        // Assert
        Assertions.assertEquals(50000, doc.body().childNodeSize())
        Assertions.assertTrue(System.currentTimeMillis() - start < 1000)
    }

    @Test
    fun testInvalidTableContents() {
        val `in`: File = ParseTest.getFile("/htmltests/table-invalid-elements.html")
        val doc = Jsoup.parse(`in`, "UTF-8")
        doc.outputSettings().prettyPrint(true)
        val rendered = doc.toString()
        val endOfEmail = rendered.indexOf("Comment")
        val guarantee = rendered.indexOf("Why am I here?")
        Assertions.assertTrue(endOfEmail > -1, "Comment not found")
        Assertions.assertTrue(guarantee > -1, "Search text not found")
        Assertions.assertTrue(guarantee > endOfEmail, "Search text did not come after comment")
    }

    @Test
    fun testTemplateInsideTable() {
        val `in`: File = ParseTest.getFile("/htmltests/table-polymer-template.html")
        val doc = Jsoup.parse(`in`, "UTF-8")
        doc.outputSettings().prettyPrint(true)
        val templates = doc.body().getElementsByTag("template")
        for (template in templates) {
            Assertions.assertTrue(template.childNodes().size > 1)
        }
    }

    @Test
    fun handlesXmlDeclAndCommentsBeforeDoctype() {
        val `in`: File = ParseTest.getFile("/htmltests/comments.html")
        val doc = Jsoup.parse(`in`, "UTF-8")
        Assertions.assertEquals(
            "<!--?xml version=\"1.0\" encoding=\"utf-8\"?--><!-- so --> <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><!-- what --> <html xml:lang=\"en\" lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"> <!-- now --> <head> <!-- then --> <meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\"> <title>A Certain Kind of Test</title> </head> <body> <h1>Hello</h1>h1&gt; (There is a UTF8 hidden BOM at the top of this file.) </body> </html>",
            StringUtil.normaliseWhitespace(doc.html())
        )
        Assertions.assertEquals("A Certain Kind of Test", doc.head().select("title").text())
    }

    @Test
    fun fallbackToUtfIfCantEncode() {
        // that charset can't be encoded, so make sure we flip to utf
        val `in` = "<html><meta charset=\"ISO-2022-CN\"/>One</html>"
        val doc = Jsoup.parse(ByteArrayInputStream(`in`.toByteArray()), null, "")
        Assertions.assertEquals("UTF-8", doc.charset()!!.name)
        Assertions.assertEquals("One", doc.text())
        val html = doc.outerHtml()
        Assertions.assertEquals(
            "<html><head><meta charset=\"UTF-8\"></head><body>One</body></html>",
            TextUtil.stripNewlines(html)
        )
    }

    @Test
    fun characterReaderBuffer() {
        val `in`: File = ParseTest.getFile("/htmltests/character-reader-buffer.html.gz")
        val doc = Jsoup.parse(`in`, "UTF-8")
        val expectedHref = "http://www.domain.com/path?param_one=value&param_two=value"
        val links = doc.select("a")
        Assertions.assertEquals(2, links.size)
        Assertions.assertEquals(expectedHref, links[0].attr("href")) // passes
        Assertions.assertEquals(
            expectedHref,
            links[1].attr("href")
        ) // fails, "but was:<...ath?param_one=value&[]_two-value>"
    }


    companion object {
        @JvmStatic
        private fun dupeAttributeData(): List<Arguments> {
            return listOf(
                Arguments.of(
                    "<p One=One ONE=Two Two=two one=Three One=Four two=Five>Text</p>",
                    "<p one=\"One\" two=\"two\">Text</p>"
                ),
                Arguments.of(
                    "<img One=One ONE=Two Two=two one=Three One=Four two=Five>",
                    "<img one=\"One\" two=\"two\">"
                ),
                Arguments.of(
                    "<form One=One ONE=Two Two=two one=Three One=Four two=Five></form>",
                    "<form one=\"One\" two=\"two\"></form>"
                )
            )
        }
    }
}
