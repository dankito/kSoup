package net.dankito.ksoup

import java.io.IOException

/**
 * Signals that a HTTP response returned a mime type that is not supported.
 */
class UnsupportedMimeTypeException(message: String?, @JvmField val mimeType: String, @JvmField val url: String) : IOException(message) {

    public override fun toString(): String {
        return super.toString() + ". Mimetype=" + mimeType + ", URL=" + url
    }
}
