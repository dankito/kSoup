package net.dankito.ksoup.parser

import net.dankito.ksoup.MultiLocaleTest
import java.util.*
import kotlin.test.assertSame

class TagTestJvm {

    @MultiLocaleTest
    fun canBeInsensitive(locale: Locale?) {
        Locale.setDefault(locale)
        val script1 = Tag.valueOf("script", ParseSettings.htmlDefault)
        val script2 = Tag.valueOf("SCRIPT", ParseSettings.htmlDefault)
        assertSame(script1, script2)
    }

}