package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup.clean
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.nodes.Attribute
import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.safety.Safelist
import net.dankito.ksoup.safety.Safelist.Companion.relaxed
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Check that we can extend Safelist methods
 */
class SafelistExtensionTest {
    @Test
    fun canCustomizeSafeTests() {
        val openSafelist = OpenSafelist(relaxed())
        val safelist = relaxed()
        val html = "<p><opentag openattr>Hello</opentag></p>"
        val openClean = clean(html, openSafelist)
        val clean = clean(html, safelist)
        Assertions.assertEquals("<p><opentag openattr=\"\">Hello</opentag></p>", TextUtil.stripNewlines(openClean))
        Assertions.assertEquals("<p>Hello</p>", clean)
    }

    // passes tags and attributes starting with "open"
    private class OpenSafelist(safelist: Safelist?) : Safelist(safelist!!) {
        override fun isSafeAttribute(tagName: String, el: Element?, attr: Attribute): Boolean {
            return if (attr.key.startsWith("open")) true else super.isSafeAttribute(tagName, el, attr)
        }

        override fun isSafeTag(tag: String): Boolean {
            return if (tag.startsWith("open")) true else super.isSafeTag(tag)
        }
    }
}
