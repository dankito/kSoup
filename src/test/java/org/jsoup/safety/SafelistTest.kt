package org.jsoup.safety

import org.jsoup.nodes.Attribute
import org.jsoup.nodes.Attributes
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import org.jsoup.safety.Safelist.Companion.none
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SafelistTest {
    @Test
    fun testCopyConstructor_noSideEffectOnTags() {
        val safelist1 = none().addTags(SafelistTest.Companion.TEST_TAG)
        val safelist2 = Safelist(safelist1)
        safelist1.addTags("invalidTag")
        Assertions.assertFalse(safelist2.isSafeTag("invalidTag"))
    }

    @Test
    fun testCopyConstructor_noSideEffectOnAttributes() {
        val safelist1 = none().addAttributes(SafelistTest.Companion.TEST_TAG, SafelistTest.Companion.TEST_ATTRIBUTE)
        val safelist2 = Safelist(safelist1)
        safelist1.addAttributes(SafelistTest.Companion.TEST_TAG, "invalidAttribute")
        Assertions.assertFalse(
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
            Assertions.assertNotEquals("invalidValue", value)
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
        Assertions.assertFalse(
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
