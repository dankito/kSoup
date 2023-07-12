package org.jsoup.internal

import org.jsoup.helper.Validate
import java.net.*
import java.util.*
import java.util.regex.Pattern
import kotlin.math.min

/**
 * A minimal String utility class. Designed for **internal** jsoup use only - the API and outcome may change without
 * notice.
 */
object StringUtil {
    // memoised padding up to 21 (blocks 0 to 20 spaces)
    @JvmField
    val padding = arrayOf(
        "", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ",
        "         ", "          ", "           ", "            ", "             ", "              ", "               ",
        "                ", "                 ", "                  ", "                   ", "                    "
    )

    /**
     * Join a collection of strings by a separator
     * @param strings collection of string objects
     * @param sep string to place between strings
     * @return joined string
     */
    @JvmStatic
    fun join(strings: Collection<*>, sep: String?): String {
        return join(strings.iterator(), sep)
    }

    /**
     * Join a collection of strings by a separator
     * @param strings iterator of string objects
     * @param sep string to place between strings
     * @return joined string
     */
    fun join(strings: Iterator<*>, sep: String?): String {
        if (!strings.hasNext()) return ""
        val start = strings.next().toString()
        if (!strings.hasNext()) // only one, avoid builder
            return start
        val j = StringJoiner(sep)
        j.add(start)
        while (strings.hasNext()) {
            j.add(strings.next())
        }
        return j.complete()
    }

    /**
     * Join an array of strings by a separator
     * @param strings collection of string objects
     * @param sep string to place between strings
     * @return joined string
     */
    @JvmStatic
    fun join(strings: Array<String?>, sep: String?): String {
        return join(strings.toList(), sep)
    }
    /**
     * Returns space padding, up to a max of maxPaddingWidth.
     * @param width amount of padding desired
     * @param maxPaddingWidth maximum padding to apply. Set to `-1` for unlimited.
     * @return string of spaces * width
     */
    /**
     * Returns space padding (up to the default max of 30). Use [.padding] to specify a different limit.
     * @param width amount of padding desired
     * @return string of spaces * width
     * @see .padding
     */
    @JvmStatic
    @JvmOverloads
    fun padding(width: Int, maxPaddingWidth: Int = 30): String {
        var width = width
        Validate.isTrue(width >= 0, "width must be >= 0")
        Validate.isTrue(maxPaddingWidth >= -1)
        if (maxPaddingWidth != -1) width = min(width, maxPaddingWidth)
        if (width < padding.size) return padding[width]
        val out = CharArray(width)
        for (i in 0 until width) out[i] = ' '
        return out.concatToString()
    }

    /**
     * Tests if a string is blank: null, empty, or only whitespace (" ", \r\n, \t, etc)
     * @param string string to test
     * @return if string is blank
     */
    @JvmStatic
    fun isBlank(string: String?): Boolean {
        if (string == null || string.length == 0) return true
        val l = string.length
        for (i in 0 until l) {
            if (!string[i].isWhitespace()) return false
        }
        return true
    }

    /**
     * Tests if a string starts with a newline character
     * @param string string to test
     * @return if its first character is a newline
     */
    fun startsWithNewline(string: String?): Boolean {
        return if (string == null || string.length == 0) false else string[0] == '\n'
    }

    /**
     * Tests if a string is numeric, i.e. contains only digit characters
     * @param string string to test
     * @return true if only digit chars, false if empty or null or contains non-digit chars
     */
    @JvmStatic
    fun isNumeric(string: String?): Boolean {
        if (string == null || string.length == 0) return false
        val l = string.length
        for (i in 0 until l) {
            if (!string[i].isDigit()) return false
        }
        return true
    }

    /**
     * Tests if a code point is "whitespace" as defined in the HTML spec. Used for output HTML.
     * @param c code point to test
     * @return true if code point is whitespace, false otherwise
     * @see .isActuallyWhitespace
     */
    @JvmStatic
    fun isWhitespace(c: Int): Boolean {
        return c == ' '.code || c == '\t'.code || c == '\n'.code || c == '\u000C'.code || c == '\r'.code
    }

    /**
     * Tests if a code point is "whitespace" as defined by what it looks like. Used for Element.text etc.
     * @param c code point to test
     * @return true if code point is whitespace, false otherwise
     */
    fun isActuallyWhitespace(c: Int): Boolean {
        return c == ' '.code || c == '\t'.code || c == '\n'.code || c == '\u000C'.code || c == '\r'.code || c == 160
        // 160 is &nbsp; (non-breaking space). Not in the spec but expected.
    }

    fun isInvisibleChar(c: Int): Boolean {
        return c == 8203 || c == 173 // zero width sp, soft hyphen
        // previously also included zw non join, zw join - but removing those breaks semantic meaning of text
    }

    /**
     * Normalise the whitespace within this string; multiple spaces collapse to a single, and all whitespace characters
     * (e.g. newline, tab) convert to a simple space.
     * @param string content to normalise
     * @return normalised string
     */
    @JvmStatic
    fun normaliseWhitespace(string: String): String {
        val sb = borrowBuilder()
        appendNormalisedWhitespace(sb, string, false)
        return releaseBuilder(sb)
    }

    /**
     * After normalizing the whitespace within a string, appends it to a string builder.
     * @param accum builder to append to
     * @param string string to normalize whitespace within
     * @param stripLeading set to true if you wish to remove any leading whitespace
     */
    fun appendNormalisedWhitespace(accum: StringBuilder, string: String, stripLeading: Boolean) {
        var lastWasWhite = false
        var reachedNonWhite = false
        val len = string.length
        var c: Int
        var i = 0
        while (i < len) {
            c = string.codePointAt(i)
            if (isActuallyWhitespace(c)) {
                if (stripLeading && !reachedNonWhite || lastWasWhite) {
                    i += Character.charCount(c)
                    continue
                }
                accum.append(' ')
                lastWasWhite = true
            } else if (!isInvisibleChar(c)) {
                accum.appendCodePoint(c)
                lastWasWhite = false
                reachedNonWhite = true
            }
            i += Character.charCount(c)
        }
    }

    fun `in`(needle: String, vararg haystack: String): Boolean {
        val len = haystack.size
        for (i in 0 until len) {
            if (haystack[i] == needle) return true
        }
        return false
    }

    @JvmStatic
    fun inSorted(needle: String?, haystack: Array<out String>): Boolean {
        return haystack.asList().binarySearch(needle) >= 0
    }

    /**
     * Tests that a String contains only ASCII characters.
     * @param string scanned string
     * @return true if all characters are in range 0 - 127
     */
    @JvmStatic
    fun isAscii(string: String): Boolean {
        Validate.notNull(string)
        for (i in 0 until string.length) {
            val c = string[i].code
            if (c > 127) { // ascii range
                return false
            }
        }
        return true
    }

    fun codepointToChar(codepoint: Int): Char =
        Char(codepoint)

    fun getCodePointAt(string: String, index: Int): Int {
//        return string[index].code

        val chars = string.toCharArray()
        val char = chars[index]
        return if (char.isHighSurrogate()) {
            val lowSurrogate = chars[index + 1]
            if (lowSurrogate.isLowSurrogate()) {
                (char.code shl 10) + lowSurrogate.code + -56613888
            } else {
                char.code
            }
        } else {
            char.code
        }

//        val usedCharset = charset ?: Charset.defaultCharset()
//        val value = string.toByteArray(usedCharset)
//
//        // TODO: this is JDK's codePointAt() implementation:
////        return if (string.isLatin1()) {
////            java.lang.String.checkIndex(index, string.value.size)
////            string.value.get(index).toInt() and 255
////        } else {
////            val length: Int = string.value.size shr 1
////            java.lang.String.checkIndex(index, length)
////            StringUTF16.codePointAt(string.value, index, length)
////        }
//
//        return value.get(index).toInt() and 255
    }

    const val MIN_SUPPLEMENTARY_CODE_POINT = 65536

    /**
     * Determines the number of char values needed to represent the specified character (Unicode code point). If the specified character is equal to or greater than 0x10000, then the method returns 2. Otherwise, the method returns 1.
     * This method doesn't validate the specified character to be a valid Unicode code point. The caller must validate the character value using isValidCodePoint if necessary.
     *
     * @param codePoint - the character (Unicode code point) to be tested.
     * @return 2 if the character is a valid supplementary character; 1 otherwise.
     */
    fun charCount(codePoint: Int): Int {
        return if (codePoint >= MIN_SUPPLEMENTARY_CODE_POINT) 2 else 1
    }

    fun toChars(codePoint: Int): CharArray {
        return if (isBmpCodePoint(codePoint)) {
            charArrayOf(codePoint.toChar())
        } else if (isValidCodePoint(codePoint)) {
            charArrayOf(highSurrogate(codePoint), lowSurrogate(codePoint))
        } else {
            throw IllegalArgumentException(String.format("Not a valid Unicode code point: 0x%X", codePoint))
        }
    }

    fun highSurrogate(codePoint: Int): Char {
        return ((codePoint ushr 10) + 'ퟀ'.code).toChar()
    }

    fun lowSurrogate(codePoint: Int): Char {
        return ((codePoint and 1023) + '\udc00'.code).toChar()
    }

    fun isBmpCodePoint(codePoint: Int): Boolean {
        return codePoint ushr 16 == 0
    }

    fun isValidCodePoint(codePoint: Int): Boolean {
        val plane = codePoint ushr 16
        return plane < 17
    }

    fun format(format: String, vararg args: Any): String {
        return String.format(format, *args)
    }

    private val extraDotSegmentsPattern = Regex("^/((\\.{1,2}/)+)")

    /**
     * Create a new absolute URL, from a provided existing absolute URL and a relative URL component.
     * @param base the existing absolute base URL
     * @param relUrl the relative URL to resolve. (If it's already absolute, it will be returned)
     * @return the resolved absolute URL
     * @throws MalformedURLException if an error occurred generating the URL
     */
  @Throws(MalformedURLException::class)
  fun resolve(base: URL, relUrl: String): URL {
    var relUrl = stripControlChars(relUrl)
        // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
    if (relUrl.startsWith("?")) {
        relUrl = base.path + relUrl
    }
    // workaround: //example.com + ./foo = //example.com/./foo, not //example.com/foo
    val url = URL(base, relUrl)
    var fixedFile = extraDotSegmentsPattern.replaceFirst(url.file, "/")
    if (url.ref != null) {
      fixedFile = fixedFile + "#" + url.ref
    }
    return URL(url.protocol, url.host, url.port, fixedFile)
  }

    /**
     * Create a new absolute URL, from a provided existing absolute URL and a relative URL component.
     * @param baseUrl the existing absolute base URL
     * @param relUrl the relative URL to resolve. (If it's already absolute, it will be returned)
     * @return an absolute URL if one was able to be generated, or the empty string if not
     */
    @JvmStatic
    fun resolve(baseUrl: String, relUrl: String): String {
        // workaround: java will allow control chars in a path URL and may treat as relative, but Chrome / Firefox will strip and may see as a scheme. Normalize to browser's view.
        val baseUrl = StringUtil.stripControlChars(baseUrl)
        val relUrl = StringUtil.stripControlChars(relUrl)

        return try {
            val base: URL
            base = try {
                URL(baseUrl)
            } catch (e: MalformedURLException) {
                // the base is unsuitable, but the attribute/rel may be abs on its own, so try that
                val abs = URL(relUrl)
                return abs.toExternalForm()
            }
            resolve(base, relUrl).toExternalForm()
        } catch (e: MalformedURLException) {
            // it may still be valid, just that Java doesn't have a registered stream handler for it, e.g. tel
            // we test here vs at start to normalize supported URLs (e.g. HTTP -> http)
            if (validUriScheme.containsMatchIn(relUrl)) {
                relUrl
            } else {
                ""
            }
        }
    }

    private val validUriScheme = Regex("^[a-zA-Z][a-zA-Z0-9+-.]*:")


    private val controlChars = Pattern.compile("[\\x00-\\x1f]*") // matches ascii 0 - 31, to strip from url

    private fun stripControlChars(input: String?): String {
        return controlChars.matcher(input).replaceAll("")
    }

    private val threadLocalBuilders: ThreadLocal<Stack<StringBuilder>> = object : ThreadLocal<Stack<StringBuilder>>() {
        override fun initialValue(): Stack<StringBuilder> {
            return Stack()
        }
    }

    /**
     * Maintains cached StringBuilders in a flyweight pattern, to minimize new StringBuilder GCs. The StringBuilder is
     * prevented from growing too large.
     *
     *
     * Care must be taken to release the builder once its work has been completed, with [.releaseBuilder]
     * @return an empty StringBuilder
     */
    fun borrowBuilder(): StringBuilder {
        val builders = threadLocalBuilders.get()
        return if (builders.empty()) StringBuilder(MaxCachedBuilderSize) else builders.pop()
    }

    /**
     * Release a borrowed builder. Care must be taken not to use the builder after it has been returned, as its
     * contents may be changed by this method, or by a concurrent thread.
     * @param sb the StringBuilder to release.
     * @return the string value of the released String Builder (as an incentive to release it!).
     */
    fun releaseBuilder(sb: StringBuilder): String {
        var sb = sb
        Validate.notNull(sb)
        val string = sb.toString()
        if (sb.length > MaxCachedBuilderSize) sb = StringBuilder(MaxCachedBuilderSize) // make sure it hasn't grown too big
        else sb.clear() // make sure it's emptied on release
        val builders = threadLocalBuilders.get()
        builders.push(sb)
        while (builders.size > MaxIdleBuilders) {
            builders.pop()
        }
        return string
    }

    private const val MaxCachedBuilderSize = 8 * 1024
    private const val MaxIdleBuilders = 8

    /**
     * A StringJoiner allows incremental / filtered joining of a set of stringable objects.
     * @since 1.14.1
     */
    class StringJoiner
    /**
     * Create a new joiner, that uses the specified separator. MUST call [.complete] or will leak a thread
     * local string builder.
     *
     * @param separator the token to insert between strings
     */(val separator: String?) {
        var sb = borrowBuilder() // sets null on builder release so can't accidentally be reused
        var first = true

        /**
         * Add another item to the joiner, will be separated
         */
        fun add(stringy: Any?): StringJoiner {
            Validate.notNull(sb) // don't reuse
            if (!first) sb.append(separator)
            sb.append(stringy)
            first = false
            return this
        }

        /**
         * Append content to the current item; not separated
         */
        fun append(stringy: Any?): StringJoiner {
            Validate.notNull(sb) // don't reuse
            sb.append(stringy)
            return this
        }

        /**
         * Return the joined string, and release the builder back to the pool. This joiner cannot be reused.
         */
        fun complete(): String {
            val string = releaseBuilder(sb)
//            sb = null
            return string
        }
    }
}