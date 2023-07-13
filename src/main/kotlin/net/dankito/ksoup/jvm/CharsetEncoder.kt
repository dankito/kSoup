package net.dankito.ksoup.jvm

class CharsetEncoder internal constructor(private val charset: Charset) {

    private val isUnicode = charset.name.startsWith("UTF")

    fun charset() = charset

    fun canEncode(char: Char): Boolean =
        isUnicode || char.isSurrogate() == false // TODO: implement

    fun canEncode(string: String): Boolean =
        string.toCharArray().all { canEncode(it) }

}