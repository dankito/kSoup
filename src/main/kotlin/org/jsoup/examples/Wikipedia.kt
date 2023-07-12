package org.jsoup.examples

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException

/**
 * A simple example, used on the jsoup website.
 */
object Wikipedia {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val doc = Jsoup.connect("http://en.wikipedia.org/").get()
        log(doc.title())
        val newsHeadlines: Elements = doc.select("#mp-itn b a")
        for (headline: Element in newsHeadlines) {
            log("%s\n\t%s", headline.attr("title"), headline.absUrl("href"))
        }
    }

    private fun log(msg: String, vararg vals: String) {
        println(String.format(msg, *vals))
    }
}
