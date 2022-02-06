package net.codinux.ksoup


expect class Element {

  val outerHtml: String

  val nodeName: String

  fun attr(attributeName: String): String?

}