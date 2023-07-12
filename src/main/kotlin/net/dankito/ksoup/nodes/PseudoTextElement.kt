package net.dankito.ksoup.nodes

import net.dankito.ksoup.parser.Tag

/**
 * Represents a [TextNode] as an [Element], to enable text nodes to be selected with
 * the [net.dankito.ksoup.select.Selector] `:matchText` syntax.
 */
class PseudoTextElement(tag: Tag, baseUri: String?, attributes: Attributes?) : Element(tag, baseUri, attributes) {

    override fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings) { }
    override fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings) { }

}
