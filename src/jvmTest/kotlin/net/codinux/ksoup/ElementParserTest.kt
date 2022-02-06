package net.codinux.ksoup

import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ElementParserTest {

  @Test
  fun extractTitleNodeName() {
    val result = ElementParser.extractNodeName(createElement("title", "<title>Simple example</title>"))

    assertEquals("title", result)
  }

  @Test
  fun extractLinkNodeName() {
    val result = ElementParser.extractNodeName(createDefaultFaviconLinkElement())

    assertEquals("link", result)
  }

  @Test
  fun extractRelAttribute() {
    val result = ElementParser.extractAttributes(createDefaultFaviconLinkElement())

    assertEquals("apple-touch-icon", result["rel"])
  }

  @Test
  fun extractSizesAttribute() {
    val result = ElementParser.extractAttributes(createDefaultFaviconLinkElement())

    assertEquals("114x114", result["sizes"])
  }

  @Test
  fun extractHrefAttribute() {
    val result = ElementParser.extractAttributes(createDefaultFaviconLinkElement())

    assertEquals("https://cdn.prod.www.spiegel.de/public/spon/images/icons/touch-icon114.png", result["href"])
  }


  private fun createDefaultFaviconLinkElement(): Element {
    return createElement("link", "<link rel=\"apple-touch-icon\" sizes=\"114x114\" href=\"https://cdn.prod.www.spiegel.de/public/spon/images/icons/touch-icon114.png\">")
  }

  private fun createElement(nodeName: String, html: String): Element {
    val jsoupElement = Jsoup.parse(html).select(nodeName).first()!!

    return Element(jsoupElement)
  }

}