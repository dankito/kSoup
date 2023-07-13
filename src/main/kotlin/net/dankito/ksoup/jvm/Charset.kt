package net.dankito.ksoup.jvm

class Charset internal constructor(internal val jvmCharset: java.nio.charset.Charset) {

    val name: String = jvmCharset.name()

    // in my tests all Charsets had the same value for name() and for displayName() - for all Locales! So may remove displayName.
    // i also think in most cases the code should call name() not displayName()
    val displayName: String = jvmCharset.displayName()

    fun newEncoder(): CharsetEncoder = CharsetEncoder(this)

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
    fun canEncode(): Boolean =
        CharsetsThatCanNotEncode.contains(name) == false

    override fun toString() = name

    companion object {

        // on the JVM only two Charsets override canEncode() and return false: ISO-2022-CN and JISAutoDetect
        val CharsetsThatCanNotEncode = hashSetOf("ISO-2022-CN", "ISO2022CN", "x-JISAutoDetect", "JISAutoDetect")

        fun forName(charsetName: String): Charset =
            Charset(java.nio.charset.Charset.forName(charsetName))

        fun isSupported(charsetName: String): Boolean =
            java.nio.charset.Charset.isSupported(charsetName)

    }

}