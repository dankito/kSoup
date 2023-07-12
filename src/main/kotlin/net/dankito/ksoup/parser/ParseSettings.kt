package net.dankito.ksoup.parser

import net.dankito.ksoup.internal.Normalizer
import net.dankito.ksoup.nodes.Attributes

/**
 * Controls parser case settings, to optionally preserve tag and/or attribute name case.
 */
class ParseSettings
/**
 * Define parse settings.
 * @param tag preserve tag case?
 * @param attribute preserve attribute name case?
 */(private val preserveTagCase: Boolean, private val preserveAttributeCase: Boolean) {
    /**
     * Returns true if preserving tag name case.
     */
    fun preserveTagCase(): Boolean {
        return preserveTagCase
    }

    /**
     * Returns true if preserving attribute case.
     */
    fun preserveAttributeCase(): Boolean {
        return preserveAttributeCase
    }

    internal constructor(copy: ParseSettings) : this(copy.preserveTagCase, copy.preserveAttributeCase)

    /**
     * Normalizes a tag name according to the case preservation setting.
     */
    fun normalizeTag(name: String): String {
        var name = name.trim()
        if (!preserveTagCase) {
            name = Normalizer.lowerCase(name)
        }
        return name
    }

    /**
     * Normalizes an attribute according to the case preservation setting.
     */
    fun normalizeAttribute(name: String): String {
        var name = name.trim()
        if (!preserveAttributeCase) {
            name = Normalizer.lowerCase(name)
        }
        return name
    }

    fun normalizeAttributes(attributes: Attributes?): Attributes? {
        if (attributes != null && !preserveAttributeCase) {
            attributes.normalize()
        }
        return attributes
    }

    companion object {
        /**
         * HTML default settings: both tag and attribute names are lower-cased during parsing.
         */
        @JvmField
        val htmlDefault = ParseSettings(false, false)

        /**
         * Preserve both tag and attribute case.
         */
        @JvmField
        val preserveCase = ParseSettings(true, true)

        /** Returns the normal name that a Tag will have (trimmed and lower-cased)  */
        fun normalName(name: String): String {
            return Normalizer.lowerCase(name.trim())
        }
    }
}
