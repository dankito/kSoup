package net.dankito.ksoup.safety

import net.dankito.ksoup.nodes.Attribute
import net.dankito.ksoup.nodes.Attributes
import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.parser.Tag
import net.dankito.ksoup.safety.Safelist.Companion.none
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class SafelistTest {

    @Test
    fun testCopyConstructor_noSideEffectOnTags() {
        val safelist1 = none().addTags(SafelistTest.Companion.TEST_TAG)
        val safelist2 = Safelist(safelist1)
        safelist1.addTags("invalidTag")
        assertFalse(safelist2.isSafeTag("invalidTag"))
    }

    @Test
    fun testCopyConstructor_noSideEffectOnAttributes() {
        val safelist1 = none().addAttributes(SafelistTest.Companion.TEST_TAG, SafelistTest.Companion.TEST_ATTRIBUTE)
        val safelist2 = Safelist(safelist1)
        safelist1.addAttributes(SafelistTest.TEST_TAG, "invalidAttribute")
        assertFalse(
            safelist2.isSafeAttribute(
                SafelistTest.Companion.TEST_TAG,
                null,
                Attribute("invalidAttribute", SafelistTest.Companion.TEST_VALUE)
            )
        )
    }

    @Test
    fun testCopyConstructor_noSideEffectOnEnforcedAttributes() {
        val safelist1 = none().addEnforcedAttribute(
            SafelistTest.Companion.TEST_TAG,
            SafelistTest.Companion.TEST_ATTRIBUTE,
            SafelistTest.Companion.TEST_VALUE
        )
        val safelist2 = Safelist(safelist1)
        safelist1.addEnforcedAttribute(
            SafelistTest.Companion.TEST_TAG,
            SafelistTest.Companion.TEST_ATTRIBUTE,
            "invalidValue"
        )
        for ((_, value) in safelist2.getEnforcedAttributes(SafelistTest.Companion.TEST_TAG)) {
            assertNotEquals("invalidValue", value)
        }
    }

    @Test
    fun testCopyConstructor_noSideEffectOnProtocols() {
        val invalidScheme = "invalid-scheme"
        val safelist1 = none()
            .addAttributes(SafelistTest.Companion.TEST_TAG, SafelistTest.Companion.TEST_ATTRIBUTE)
            .addProtocols(
                SafelistTest.Companion.TEST_TAG,
                SafelistTest.Companion.TEST_ATTRIBUTE,
                SafelistTest.Companion.TEST_SCHEME
            )
        val safelist2 = Safelist(safelist1)
        safelist1.addProtocols(SafelistTest.Companion.TEST_TAG, SafelistTest.Companion.TEST_ATTRIBUTE, invalidScheme)
        val attributes = Attributes()
        val invalidAttribute = Attribute(SafelistTest.Companion.TEST_ATTRIBUTE, "$invalidScheme://someValue")
        attributes.put(invalidAttribute)
        val invalidElement = Element(Tag.valueOf(SafelistTest.Companion.TEST_TAG), "", attributes)
        assertFalse(
            safelist2.isSafeAttribute(
                SafelistTest.Companion.TEST_TAG,
                invalidElement,
                invalidAttribute
            )
        )
    }

    companion object {
        private const val TEST_TAG = "testTag"
        private const val TEST_ATTRIBUTE = "testAttribute"
        private const val TEST_SCHEME = "valid-scheme"
        private val TEST_VALUE: String = SafelistTest.Companion.TEST_SCHEME + "://testValue"
    }
}
