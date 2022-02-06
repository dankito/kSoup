package net.codinux.ksoup

import org.jsoup.nodes.Element

actual class Element(private val delegate: Element) {

  actual val outerHtml: String
    get() = delegate.outerHtml()

  actual val nodeName: String
    get() = delegate.nodeName()

  actual fun attr(attributeName: String): String? {
    val value = delegate.attr(attributeName)

    if (value.isNullOrBlank()) {
      return null
    }

    return value
  }

}