package net.codinux.ksoup


object ElementParser {

  val ExtractNodeNameRegex = Regex("<([a-zA-Z]+)[ >].*")

  /**
   * Actually this is the fully correct specification of allowed characters:
   *
   * [4] NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
   *
   * [4a] NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
   */
  val ExtractAttributesRegex = Regex("([a-zA-Z0-9_:\\-.]+)\\s?=\\s?[\"\'](.+)[\"\']")

  private val WhitespaceRegex = Regex("\\s")


  fun extractNodeName(element: Element): String {
    // the first group is the whole html, the second one the group from Regex and therefore the tag name
    return ExtractNodeNameRegex.find(element.outerHtml)?.groupValues?.get(1) ?: ""
  }

  fun extractAttributes(element: Element): Map<String, String> {

    return element.outerHtml.split(WhitespaceRegex) // TODO: does not fully work as doesn't take white spaces around equals sign like '= ' and ' =' into account
      .mapNotNull { ExtractAttributesRegex.find(it) } // find attribute name value pairs
      .filter { matchResult -> matchResult.groupValues.size == 3 } // should actually be the case for all match results, just to be on the safe side
      .associate { it.groupValues[1] to it.groupValues[2] } // the first group value is the whole match, the second the attribute name, the third the attribute value
  }

}