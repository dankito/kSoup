package org.jsoup.parser

/**
 * A container for ParseErrors.
 *
 * @author Jonathan Hedley
 */
class ParseErrorList internal constructor(private val initialCapacity: Int, val maxSize: Int)
    : ArrayList<ParseError>(initialCapacity) {

    /**
     * Create a new ParseErrorList with the same settings, but no errors in the list
     * @param copy initial and max size details to copy
     */
    internal constructor(copy: ParseErrorList) : this(copy.initialCapacity, copy.maxSize)

    fun canAddError(): Boolean {
        return size < maxSize
    }

    override fun clone(): Any {
        // all class fields are primitive, so native clone is enough.
        return super.clone()
    }

    companion object {
        private val INITIAL_CAPACITY: Int = 16
        fun noTracking(): ParseErrorList {
            return ParseErrorList(0, 0)
        }

        @JvmStatic
        fun tracking(maxSize: Int): ParseErrorList {
            return ParseErrorList(INITIAL_CAPACITY, maxSize)
        }
    }
}
