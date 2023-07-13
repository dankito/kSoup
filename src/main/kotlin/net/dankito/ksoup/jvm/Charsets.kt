package net.dankito.ksoup.jvm

object Charsets {

    /**
     * Eight-bit UCS Transformation Format.
     */
    val UTF_8 by lazy { Charset.forName("UTF-8") }

    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark.
     */
    val UTF_16 by lazy { Charset.forName("UTF-16") }

    /**
     * Sixteen-bit UCS Transformation Format, big-endian byte order.
     */
    val UTF_16BE by lazy { Charset.forName("UTF-16BE") }

    /**
     * Sixteen-bit UCS Transformation Format, little-endian byte order.
     */
    val UTF_16LE by lazy { Charset.forName("UTF-16LE") }

    /**
     * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the
     * Unicode character set.
     */
    val US_ASCII by lazy { Charset.forName("US-ASCII") }

    /**
     * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.
     */
    val ISO_8859_1 by lazy { Charset.forName("ISO-8859-1") }

    /**
     * 32-bit Unicode (or UCS) Transformation Format, byte order identified by an optional byte-order mark
     */
    val UTF_32 by lazy { Charset.forName("UTF-32") }

    /**
     * 32-bit Unicode (or UCS) Transformation Format, little-endian byte order.
     */
    val UTF_32LE by lazy { Charset.forName("UTF-32LE") }

    /**
     * 32-bit Unicode (or UCS) Transformation Format, big-endian byte order.
     */
    val UTF_32BE by lazy { Charset.forName("UTF-32BE") }

}