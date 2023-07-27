package net.dankito.ksoup

import net.dankito.ksoup.jvm.IOException

/**
 * Signals that a HTTP request resulted in a not OK HTTP response.
 */
class HttpStatusException(message: String, val statusCode: Int, val url: String) :
    IOException("$message. Status=$statusCode, URL=[$url]")
