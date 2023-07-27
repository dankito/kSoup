package net.dankito.ksoup.platform

expect class Charset {

    val name: String

    val displayName: String

    companion object {

        fun forName(charsetName: String): Charset

        fun isSupported(charsetName: String): Boolean

    }

}


/**
 * On the JVM only two Charsets override canEncode() and return false: ISO-2022-CN and JISAutoDetect
 */
val CharsetsThatCanNotEncode = hashSetOf("ISO-2022-CN", "ISO2022CN", "x-JISAutoDetect", "JISAutoDetect")

/**
 * Tells whether or not this charset supports encoding.
 *
 * Nearly all charsets support encoding. The primary exceptions are special-purpose auto-detect charsets whose
 * decoders can determine which of several possible encoding schemes is in use by examining the input byte sequence.
 * Such charsets do not support encoding because there is no way to determine which encoding should be used on output.
 * Implementations of such charsets should override this method to return false.
 *
 * @return true if, and only if, this charset supports encoding
 */
fun Charset.canEncode(): Boolean =
    CharsetsThatCanNotEncode.contains(name) == false

fun Charset.newEncoder(): CharsetEncoder = CharsetEncoder(this)