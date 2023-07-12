package net.dankito.ksoup

import java.io.IOException

/**
 * Signals that a HTTP request resulted in a not OK HTTP response.
 */
class HttpStatusException(message: String, @JvmField val statusCode: Int, @JvmField val url: String) :
    IOException(message + ". Status=" + statusCode + ", URL=[" + url + "]")
