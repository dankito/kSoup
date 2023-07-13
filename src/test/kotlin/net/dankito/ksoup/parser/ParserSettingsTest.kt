package net.dankito.ksoup.parser

import net.dankito.ksoup.MultiLocaleExtension.MultiLocaleTest
import net.dankito.ksoup.nodes.Attributes
import org.junit.jupiter.api.Assertions
import java.util.Locale

class ParserSettingsTest {
    @MultiLocaleTest
    fun caseSupport(locale: Locale?) {
        Locale.setDefault(locale)
        val bothOn = ParseSettings(true, true)
        val bothOff = ParseSettings(false, false)
        val tagOn = ParseSettings(true, false)
        val attrOn = ParseSettings(false, true)
        Assertions.assertEquals("IMG", bothOn.normalizeTag("IMG"))
        Assertions.assertEquals("ID", bothOn.normalizeAttribute("ID"))
        Assertions.assertEquals("img", bothOff.normalizeTag("IMG"))
        Assertions.assertEquals("id", bothOff.normalizeAttribute("ID"))
        Assertions.assertEquals("IMG", tagOn.normalizeTag("IMG"))
        Assertions.assertEquals("id", tagOn.normalizeAttribute("ID"))
        Assertions.assertEquals("img", attrOn.normalizeTag("IMG"))
        Assertions.assertEquals("ID", attrOn.normalizeAttribute("ID"))
    }

    @MultiLocaleTest
    fun attributeCaseNormalization(locale: Locale?) {
        Locale.setDefault(locale)
        val parseSettings = ParseSettings(false, false)
        val normalizedAttribute = parseSettings.normalizeAttribute("HIDDEN")
        Assertions.assertEquals("hidden", normalizedAttribute)
    }

    @MultiLocaleTest
    fun attributesCaseNormalization(locale: Locale?) {
        Locale.setDefault(locale)
        val parseSettings = ParseSettings(false, false)
        val attributes = Attributes()
        attributes.put("ITEM", "1")
        val normalizedAttributes = parseSettings.normalizeAttributes(attributes)
        Assertions.assertEquals("item", normalizedAttributes!!.asList()[0].key)
    }
}
