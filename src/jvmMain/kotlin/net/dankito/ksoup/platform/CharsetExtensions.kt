package net.dankito.ksoup.platform

import java.nio.ByteBuffer

// later on this will only be available on the JVM
fun Charset.asJvmCharset() = this.jvmCharset

// later on this will only be available on the JVM
fun Charset.decode(bytes: ByteBuffer) = this.asJvmCharset().decode(bytes)

inline fun String.toByteArray(charset: Charset = Charsets.UTF_8): ByteArray =
    this.toByteArray(charset.asJvmCharset())

inline fun String(bytes: ByteArray, charset: Charset): String =
    String(bytes, charset.asJvmCharset())