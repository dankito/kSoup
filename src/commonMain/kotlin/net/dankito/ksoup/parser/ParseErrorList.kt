package net.dankito.ksoup.parser

import net.dankito.ksoup.jvm.OpenArrayList
import kotlin.jvm.JvmStatic

/**
 * A container for ParseErrors.
 *
 * @author Jonathan Hedley
 */
class ParseErrorList internal constructor(private val initialCapacity: Int, val maxSize: Int)
    : OpenArrayList<ParseError>(initialCapacity) {

    /**
     * Create a new ParseErrorList with the same settings, but no errors in the list
     * @param copy initial and max size details to copy
     */
    internal constructor(copy: ParseErrorList) : this(copy.initialCapacity, copy.maxSize)

    fun canAddError(): Boolean {
        return size < maxSize
    }

    companion object {
        private val INITIAL_CAPACITY: Int = 16

        @JvmStatic
        fun noTracking(): ParseErrorList {
            return ParseErrorList(0, 0)
        }

        @JvmStatic
        fun tracking(maxSize: Int): ParseErrorList {
            return ParseErrorList(INITIAL_CAPACITY, maxSize)
        }
    }
}
