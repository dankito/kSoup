package net.codinux.ksoup

actual class Element(actual val outerHtml: String) {

  actual val nodeName: String by lazy {
    ElementParser.extractNodeName(this)
  }

  private val attributes: Map<String, String> by lazy {
    ElementParser.extractAttributes(this)
  }

  actual fun attr(attributeName: String): String? {
    return attributes[attributeName]
  }

}