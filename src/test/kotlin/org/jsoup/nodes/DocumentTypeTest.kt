package org.jsoup.nodes

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
        Assertions.assertThrows(IllegalArgumentException::class.java) { DocumentType("html", null, null) }
    }

    @Test
    fun constructorValidationOkWithBlankPublicAndSystemIds() {
        DocumentType("html", "", "")
    }

    @Test
    fun outerHtmlGeneration() {
        val html5 = DocumentType("html", "", "")
        Assertions.assertEquals("<!doctype html>", html5.outerHtml())
        val publicDocType = DocumentType("html", "-//IETF//DTD HTML//", "")
        Assertions.assertEquals("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML//\">", publicDocType.outerHtml())
        val systemDocType = DocumentType("html", "", "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd")
        Assertions.assertEquals(
            "<!DOCTYPE html SYSTEM \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">",
            systemDocType.outerHtml()
        )
        val combo = DocumentType("notHtml", "--public", "--system")
        Assertions.assertEquals("<!DOCTYPE notHtml PUBLIC \"--public\" \"--system\">", combo.outerHtml())
        Assertions.assertEquals("notHtml", combo.name())
        Assertions.assertEquals("--public", combo.publicId())
        Assertions.assertEquals("--system", combo.systemId())
    }

    @Test
    fun testRoundTrip() {
        val base = "<!DOCTYPE html>"
        Assertions.assertEquals("<!doctype html>", htmlOutput(base))
        Assertions.assertEquals(base, xmlOutput(base))
        val publicDoc =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
        Assertions.assertEquals(publicDoc, htmlOutput(publicDoc))
        Assertions.assertEquals(publicDoc, xmlOutput(publicDoc))
        val systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">"
        Assertions.assertEquals(systemDoc, htmlOutput(systemDoc))
        Assertions.assertEquals(systemDoc, xmlOutput(systemDoc))
        val legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">"
        Assertions.assertEquals(legacyDoc, htmlOutput(legacyDoc))
        Assertions.assertEquals(legacyDoc, xmlOutput(legacyDoc))
    }

    private fun htmlOutput(`in`: String): String {
        val type = Jsoup.parse(`in`).childNode(0) as DocumentType
        return type.outerHtml()
    }

    private fun xmlOutput(`in`: String): String {
        return parse(`in`, "", xmlParser()).childNode(0).outerHtml()
    }
}
