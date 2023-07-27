package net.dankito.ksoup.safety

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.MultiLocaleTest
import net.dankito.ksoup.TextUtil
import java.util.*
import kotlin.test.assertEquals

class CleanerTestJvm {

    @MultiLocaleTest
    fun safeListedProtocolShouldBeRetained(locale: Locale?) {
        Locale.setDefault(locale)
        val safelist = Safelist.none()
            .addTags("a")
            .addAttributes("a", "href")
            .addProtocols("a", "href", "something")
        val cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist)
        assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml))
    }

}