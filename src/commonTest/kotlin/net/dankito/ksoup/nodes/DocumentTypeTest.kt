package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the DocumentType node
 *
 * @author Jonathan Hedley, http://jonathanhedley.com/
 */
class DocumentTypeTest {

    @Test
    fun constructorValidationOkWithBlankName() {
        DocumentType("", "", "")
    }

    @Test
    fun constructorValidationThrowsExceptionOnNulls() {
        assertFailsWith(IllegalArgumentException::class) {
            DocumentType("html", null, null)
        }
    }

    @Test
    fun constructorValidationOkWithBlankPublicAndSystemIds() {
        DocumentType("html", "", "")
    }

    @Test
    fun outerHtmlGeneration() {
        val html5 = DocumentType("html", "", "")
        assertEquals("<!doctype html>", html5.outerHtml())
        val publicDocType = DocumentType("html", "-//IETF//DTD HTML//", "")
        assertEquals("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML//\">", publicDocType.outerHtml())
        val systemDocType = DocumentType("html", "", "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd")
        assertEquals(
            "<!DOCTYPE html SYSTEM \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">",
            systemDocType.outerHtml()
        )
        val combo = DocumentType("notHtml", "--public", "--system")
        assertEquals("<!DOCTYPE notHtml PUBLIC \"--public\" \"--system\">", combo.outerHtml())
        assertEquals("notHtml", combo.name())
        assertEquals("--public", combo.publicId())
        assertEquals("--system", combo.systemId())
    }

    @Test
    fun testRoundTrip() {
        val base = "<!DOCTYPE html>"
        assertEquals("<!doctype html>", htmlOutput(base))
        assertEquals(base, xmlOutput(base))
        val publicDoc =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
        assertEquals(publicDoc, htmlOutput(publicDoc))
        assertEquals(publicDoc, xmlOutput(publicDoc))
        val systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">"
        assertEquals(systemDoc, htmlOutput(systemDoc))
        assertEquals(systemDoc, xmlOutput(systemDoc))
        val legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">"
        assertEquals(legacyDoc, htmlOutput(legacyDoc))
        assertEquals(legacyDoc, xmlOutput(legacyDoc))
    }

    private fun htmlOutput(`in`: String): String {
        val type = Jsoup.parse(`in`).childNode(0) as DocumentType
        return type.outerHtml()
    }

    private fun xmlOutput(`in`: String): String {
        return Jsoup.parse(`in`, "", xmlParser()).childNode(0).outerHtml()
    }
}
