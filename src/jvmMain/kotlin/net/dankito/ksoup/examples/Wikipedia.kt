package net.dankito.ksoup.examples

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.select.Elements

/**
 * A simple example, used on the jsoup website.
 */
object Wikipedia {

    @JvmStatic
    fun main(args: Array<String>) {
        val doc = Jsoup.connect("http://en.wikipedia.org/").get()
        log(doc.title())
        val newsHeadlines: Elements = doc.select("#mp-itn b a")
        for (headline: Element in newsHeadlines) {
            log("${headline.attr("title")}\n\t${headline.absUrl("href")}")
        }
    }

    private fun log(msg: String) {
        println(msg)
    }
}
