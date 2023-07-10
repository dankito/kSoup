package org.jsoup.helper

import org.jsoup.Connection
import org.jsoup.internal.StringUtil
import java.io.UnsupportedEncodingException
import java.net.*

/**
 * A utility class to normalize input URLs. jsoup internal; API subject to change.
 *
 * Normalization includes puny-coding the host, and encoding non-ascii path components. Any non-ascii characters in
 * the query string (or the fragment/anchor) are escaped, but any existing escapes in those components are preserved.
 */
internal class UrlBuilder(var u: URL?) {
    var q: StringBuilder? = null

    init {
        if (u!!.getQuery() != null) q = StringUtil.borrowBuilder().append(u!!.getQuery())
    }

    fun build(): URL? {
        try {
            // use the URI class to encode non-ascii in path
            val uri: URI = URI(
                u!!.getProtocol(),
                u!!.getUserInfo(),
                IDN.toASCII(decodePart(u!!.getHost())),  // puny-code
                u!!.getPort(),
                decodePart(u!!.getPath()),
                null, null // query and fragment appended later so as not to encode
            )
            var normUrl: String? = uri.toASCIIString()
            if (q != null || u!!.getRef() != null) {
                val sb: StringBuilder = StringUtil.borrowBuilder().append(normUrl)
                if (q != null) {
                    sb.append('?')
                    appendToAscii(StringUtil.releaseBuilder(q), true, sb)
                }
                if (u!!.getRef() != null) {
                    sb.append('#')
                    appendToAscii(u!!.getRef(), false, sb)
                }
                normUrl = StringUtil.releaseBuilder(sb)
            }
            u = URL(normUrl)
            return u
        } catch (e: MalformedURLException) {
            // we assert here so that any incomplete normalization issues can be caught in devel. but in practise,
            // the remote end will be able to handle it, so in prod we just pass the original URL.
            // The UnsupportedEncodingException would never happen as always UTF8
            assert(Validate.assertFail(e.toString()))
            return u
        } catch (e: URISyntaxException) {
            assert(Validate.assertFail(e.toString()))
            return u
        } catch (e: UnsupportedEncodingException) {
            assert(Validate.assertFail(e.toString()))
            return u
        }
    }

    @Throws(UnsupportedEncodingException::class)
    fun appendKeyVal(kv: Connection.KeyVal?) {
        if (q == null) q = StringUtil.borrowBuilder() else q!!.append('&')
        q!!
            .append(URLEncoder.encode(kv!!.key(), DataUtil.UTF_8.name()))
            .append('=')
            .append(URLEncoder.encode(kv.value(), DataUtil.UTF_8.name()))
    }

    companion object {
        private fun decodePart(encoded: String): String {
            try {
                return URLDecoder.decode(encoded, DataUtil.UTF_8.name())
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e) // wtf!
            }
        }

        @Throws(UnsupportedEncodingException::class)
        private fun appendToAscii(s: String?, spaceAsPlus: Boolean, sb: StringBuilder) {
            // minimal normalization of Unicode -> Ascii, and space normal. Existing escapes are left as-is.
            for (i in 0 until s!!.length) {
                val c: Int = s.codePointAt(i)
                if (c == ' '.code) {
                    sb.append(if (spaceAsPlus) '+' else "%20")
                } else if (c > 127) { // out of ascii range
                    sb.append(URLEncoder.encode(String(Character.toChars(c)), DataUtil.UTF_8.name()))
                    // ^^ is a bit heavy-handed - if perf critical, we could optimize
                } else {
                    sb.append(c.toChar())
                }
            }
        }
    }
}
