/**
 * Contains example programs and use of jsoup. See the [jsoup cookbook](https://jsoup.org/cookbook/).
 */
package org.jsoup.examples

import java.io.IOException
import java.util.LinkedList
import java.util.regex.PatternSyntaxException
import java.util.concurrent.atomic.AtomicBoolean
import java.nio.charset.Charset
import java.nio.charset.CharsetEncoder
import java.io.FileInputStream
import java.io.CharArrayReader
import java.io.BufferedReader
import java.nio.charset.IllegalCharsetNameException
import java.net.URISyntaxException
import java.net.MalformedURLException
import java.net.IDN
import org.jsoup.helper.UrlBuilder
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.net.URLDecoder
import java.net.CookieManager
import java.net.InetSocketAddress
import java.io.ByteArrayInputStream
import java.io.BufferedInputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.util.Locale
import org.jsoup.examples.ListLinks
import org.jsoup.examples.Wikipedia
import org.jsoup.examples.HtmlToPlainText.FormattingVisitor
import org.jsoup.examples.HtmlToPlainText
import java.net.SocketTimeoutException
