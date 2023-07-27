package net.dankito.ksoup.platform

actual class Charset(actual val name: String) {

    actual val displayName: String = name

    override fun toString() = name

    actual companion object {

        private val supportedCharsets = hashSetOf("US-ASCII", "UTF-8")

        actual fun forName(charsetName: String): Charset =
            Charset(charsetName)

        actual fun isSupported(charsetName: String): Boolean =
            supportedCharsets.contains(charsetName)

    }

}