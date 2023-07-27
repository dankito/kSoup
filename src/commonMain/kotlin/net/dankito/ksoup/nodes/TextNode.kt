package net.dankito.ksoup.nodes

import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.internal.StringUtil
import kotlin.jvm.JvmStatic

/**
 * A text node.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
open class TextNode(text: String?) : LeafNode(text) {

    override fun nodeName(): String {
        return "#text"
    }

    /**
     * Get the text content of this text node.
     * @return Unencoded, normalised text.
     * @see TextNode.getWholeText
     */
    open fun text(): String {
        return StringUtil.normaliseWhitespace(wholeText)
    }

    /**
     * Set the text content of this text node.
     * @param text unencoded text
     * @return this, for chaining
     */
    fun text(text: String): TextNode {
        coreValue(text)
        return this
    }

    /**
     * Get the (unencoded) text of this text node, including any newlines and spaces present in the original.
     * @return text
     */
    val wholeText: String
        get() = coreValue()

    /**
     * Test if this text node is blank -- that is, empty or only whitespace (including newlines).
     * @return true if this document is empty or only whitespace, false if it contains any text content.
     */
    val isBlank: Boolean
        get() = StringUtil.isBlank(coreValue())

    val isNotBlank: Boolean
        get() = !isBlank

    /**
     * Split this text node into two nodes at the specified string offset. After splitting, this node will contain the
     * original text up to the offset, and will have a new text node sibling containing the text after the offset.
     * @param offset string offset point to split node at.
     * @return the newly created text node containing the text after the offset.
     */
    fun splitText(offset: Int): TextNode {
        val text = coreValue()
        Validate.isTrue(offset >= 0, "Split offset must be not be negative")
        Validate.isTrue(offset < text.length, "Split offset must not be greater than current text length")
        val head = text.substring(0, offset)
        val tail = text.substring(offset)
        text(head)
        val tailNode = TextNode(tail)
        parentNode?.addChildren(siblingIndex + 1, tailNode)

        return tailNode
    }

    override fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        val prettyPrint = out.prettyPrint()
        val parent = parentNode as? Element
        val normaliseWhite = prettyPrint && !Element.preserveWhitespace(parentNode)
        val trimLikeBlock = parent != null && (parent.tag().isBlock || parent.tag().formatAsBlock)
        var trimLeading = false
        var trimTrailing = false
        if (normaliseWhite) {
            trimLeading = trimLikeBlock && siblingIndex == 0 || parentNode is Document
            trimTrailing = trimLikeBlock && nextSibling() == null

            // if this text is just whitespace, and the next node will cause an indent, skip this text:
            val next = nextSibling()
            val prev = previousSibling()
            val isBlank = isBlank
            val couldSkip = next is Element && next.shouldIndent(out)
                    || next is TextNode && next.isBlank
                    || prev is Element && (prev.isBlock || prev.isNode("br")) // br is a bit special - make sure we don't get a dangling blank line, but not a block otherwise wraps in head
            if (couldSkip && isBlank) return
            if (siblingIndex == 0 && parent != null && parent.tag().formatAsBlock && !isBlank
                || out.outline() && siblingNodes().size > 0 && !isBlank || siblingIndex > 0 && isNode(prev, "br") // special case wrap on inline <br> - doesn't make sense as a block tag
            ) {
                indent(accum, depth, out)
            }
        }
        Entities.escape(accum, coreValue(), out, false, normaliseWhite, trimLeading, trimTrailing)
    }

    override fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings) { }

    override fun toString(): String {
        return outerHtml()
    }

    override fun clone(): TextNode {
        return super.clone() as TextNode
    }

    override fun createInstanceForClone() = TextNode(value as? String)

    companion object {
        /**
         * Create a new TextNode from HTML encoded (aka escaped) data.
         * @param encodedText Text containing encoded HTML (e.g. `&lt;`)
         * @return TextNode containing unencoded data (e.g. `<`)
         */
        @JvmStatic
        fun createFromEncoded(encodedText: String): TextNode {
            val text: String = Entities.unescape(encodedText)
            return TextNode(text)
        }

        @JvmStatic
        fun normaliseWhitespace(text: String) =
            StringUtil.normaliseWhitespace(text)

        @JvmStatic
        fun stripLeadingWhitespace(text: String): String {
            return text.replaceFirst("^\\s+".toRegex(), "")
        }

        fun lastCharIsWhitespace(sb: StringBuilder): Boolean {
            return sb.isNotEmpty() && sb.last() == ' '
        }
    }
}
