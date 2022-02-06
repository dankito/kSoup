package net.codinux.ksoup

import org.jsoup.Jsoup

internal actual class kSoupExecutor {

  actual fun select(html: String, cssQuery: String): Elements {
    val elements = Jsoup.parse(html).select(cssQuery)

    return Elements(elements.map { Element(it) })
  }

}