package net.dankito.ksoup.parser

import net.dankito.ksoup.MultiLocaleTest
import net.dankito.ksoup.nodes.Attributes
import java.util.Locale
import kotlin.test.assertEquals

class ParserSettingsTest {

    @MultiLocaleTest
    fun caseSupport(locale: Locale?) {
        Locale.setDefault(locale)
        val bothOn = ParseSettings(true, true)
        val bothOff = ParseSettings(false, false)
        val tagOn = ParseSettings(true, false)
        val attrOn = ParseSettings(false, true)
        assertEquals("IMG", bothOn.normalizeTag("IMG"))
        assertEquals("ID", bothOn.normalizeAttribute("ID"))
        assertEquals("img", bothOff.normalizeTag("IMG"))
        assertEquals("id", bothOff.normalizeAttribute("ID"))
        assertEquals("IMG", tagOn.normalizeTag("IMG"))
        assertEquals("id", tagOn.normalizeAttribute("ID"))
        assertEquals("img", attrOn.normalizeTag("IMG"))
        assertEquals("ID", attrOn.normalizeAttribute("ID"))
    }

    @MultiLocaleTest
    fun attributeCaseNormalization(locale: Locale?) {
        Locale.setDefault(locale)
        val parseSettings = ParseSettings(false, false)
        val normalizedAttribute = parseSettings.normalizeAttribute("HIDDEN")
        assertEquals("hidden", normalizedAttribute)
    }

    @MultiLocaleTest
    fun attributesCaseNormalization(locale: Locale?) {
        Locale.setDefault(locale)
        val parseSettings = ParseSettings(false, false)
        val attributes = Attributes()
        attributes.put("ITEM", "1")
        val normalizedAttributes = parseSettings.normalizeAttributes(attributes)
        assertEquals("item", normalizedAttributes!!.asList()[0].key)
    }
}
