package org.jsoup.nodes

import org.jsoup.helper.Validate
import org.jsoup.internal.StringUtil
import java.io.IOException

/**
 * A text node.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
open class TextNode(text: String) : LeafNode() {
    /**
     * Create a new TextNode representing the supplied (unencoded) text).
     *
     * @param text raw text
     * @see .createFromEncoded
     */
    init {
        value = text
    }

    override fun nodeName(): String? {
        return "#text"
    }

    /**
     * Get the text content of this text node.
     * @return Unencoded, normalised text.
     * @see TextNode.getWholeText
     */
    open fun text(): String? {
        return StringUtil.normaliseWhitespace(wholeText)
    }

    /**
     * Set the text content of this text node.
     * @param text unencoded text
     * @return this, for chaining
     */
    fun text(text: String?): TextNode {
        coreValue(text)
        return this
    }

    val wholeText: String?
        /**
         * Get the (unencoded) text of this text node, including any newlines and spaces present in the original.
         * @return text
         */
        get() = coreValue()
    val isBlank: Boolean
        /**
         * Test if this text node is blank -- that is, empty or only whitespace (including newlines).
         * @return true if this document is empty or only whitespace, false if it contains any text content.
         */
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
        Validate.isTrue(offset < text!!.length, "Split offset must not be greater than current text length")
        val head = text.substring(0, offset)
        val tail = text.substring(offset)
        text(head)
        val tailNode = TextNode(tail)
        if (parentNode != null) parentNode!!.addChildren(siblingIndex() + 1, tailNode)
        return tailNode
    }

    @Throws(IOException::class)
    override fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        val prettyPrint = out.prettyPrint()
        val parent = if (parentNode is Element) parentNode as Element? else null
        val normaliseWhite = prettyPrint && !Element.Companion.preserveWhitespace(parentNode)
        val trimLikeBlock = parent != null && (parent.tag()!!.isBlock || parent.tag()!!.formatAsBlock())
        var trimLeading = false
        var trimTrailing = false
        if (normaliseWhite) {
            trimLeading = trimLikeBlock && siblingIndex == 0 || parentNode is Document
            trimTrailing = trimLikeBlock && nextSibling() == null

            // if this text is just whitespace, and the next node will cause an indent, skip this text:
            val next = nextSibling()
            val prev = previousSibling()
            val isBlank = isBlank
            val couldSkip =
                next is Element && next.shouldIndent(out) || next is TextNode && next.isBlank || prev is Element && (prev.isBlock || prev.isNode(
                    "br"
                )) // br is a bit special - make sure we don't get a dangling blank line, but not a block otherwise wraps in head
            if (couldSkip && isBlank) return
            if (siblingIndex == 0 && parent != null && parent.tag()!!
                    .formatAsBlock() && !isBlank || out.outline() && siblingNodes().size > 0 && !isBlank || siblingIndex > 0 && Node.Companion.isNode(
                    prev,
                    "br"
                ) // special case wrap on inline <br> - doesn't make sense as a block tag
            ) indent(accum, depth, out)
        }
        Entities.escape(accum, coreValue(), out, false, normaliseWhite, trimLeading, trimTrailing)
    }

    @Throws(IOException::class)
    override fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings) {
    }

    override fun toString(): String {
        return outerHtml()!!
    }

    override fun clone(): TextNode {
        return super.clone() as TextNode
    }

    companion object {
        /**
         * Create a new TextNode from HTML encoded (aka escaped) data.
         * @param encodedText Text containing encoded HTML (e.g. `&lt;`)
         * @return TextNode containing unencoded data (e.g. `<`)
         */
        @JvmStatic
        fun createFromEncoded(encodedText: String?): TextNode {
            val text = Entities.unescape(encodedText)
            return TextNode(text!!)
        }

        @JvmStatic
        fun normaliseWhitespace(text: String?): String? {
            var text = text
            text = StringUtil.normaliseWhitespace(text)
            return text
        }

        @JvmStatic
        fun stripLeadingWhitespace(text: String): String {
            return text.replaceFirst("^\\s+".toRegex(), "")
        }

        fun lastCharIsWhitespace(sb: StringBuilder?): Boolean {
            return sb!!.length != 0 && sb[sb.length - 1] == ' '
        }
    }
}
