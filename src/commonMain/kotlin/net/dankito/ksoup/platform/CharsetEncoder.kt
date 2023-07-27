package net.dankito.ksoup.platform

class CharsetEncoder internal constructor(private val charset: Charset) {

    private val isUnicode = charset.name.startsWith("UTF", true)

    private val isAscii = charset.name.contains("ascii", true)

    fun charset() = charset

    fun canEncode(char: Char): Boolean =
        isUnicode
                || (isAscii && char.code < 0x80)
                || char.isSurrogate() == false // TODO: implement

    fun canEncode(string: String): Boolean =
        string.toCharArray().all { canEncode(it) }

    override fun toString() = "Encoder for $charset"

}