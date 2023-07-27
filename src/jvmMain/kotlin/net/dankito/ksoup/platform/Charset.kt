package net.dankito.ksoup.platform

actual class Charset internal constructor(internal val jvmCharset: java.nio.charset.Charset) {

    actual val name: String = jvmCharset.name()

    // in my tests all Charsets had the same value for name() and for displayName() - for all Locales! So may remove displayName.
    // i also think in most cases the code should call name() not displayName()
    actual val displayName: String = jvmCharset.displayName()

    override fun toString() = name

    actual companion object {

        actual fun forName(charsetName: String): Charset =
            Charset(java.nio.charset.Charset.forName(charsetName))

        actual fun isSupported(charsetName: String): Boolean =
            java.nio.charset.Charset.isSupported(charsetName)

    }

}