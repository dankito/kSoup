package org.jsoup.nodes

import org.jsoup.SerializationException
import org.jsoup.helper.Validate
import org.jsoup.internal.StringUtil
import java.io.IOException

/**
 * An XML Declaration.
 */
class XmlDeclaration(name: String, isProcessingInstruction: Boolean) : LeafNode() {
    // todo this impl isn't really right, the data shouldn't be attributes, just a run of text after the name
    private val isProcessingInstruction // <! if true, <? if false, declaration (and last data char should be ?)
            : Boolean

    /**
     * Create a new XML declaration
     * @param name of declaration
     * @param isProcessingInstruction is processing instruction
     */
    init {
        Validate.notNull(name)
        value = name
        this.isProcessingInstruction = isProcessingInstruction
    }

    override fun nodeName(): String? {
        return "#declaration"
    }

    /**
     * Get the name of this declaration.
     * @return name of this declaration.
     */
    fun name(): String? {
        return coreValue()
    }

    val wholeDeclaration: String
        /**
         * Get the unencoded XML declaration.
         * @return XML declaration
         */
        get() {
            val sb = StringUtil.borrowBuilder()
            try {
                getWholeDeclaration(sb, Document.OutputSettings())
            } catch (e: IOException) {
                throw SerializationException(e)
            }
            return StringUtil.releaseBuilder(sb).trim { it <= ' ' }
        }

    @Throws(IOException::class)
    private fun getWholeDeclaration(accum: Appendable, out: Document.OutputSettings) {
        for (attribute in attributes()) {
            if (key != nodeName()) { // skips coreValue (name)
                accum!!.append(' ')
                // basically like Attribute, but skip empty vals in XML
                accum.append(key)
                if (!`val`!!.isEmpty()) {
                    accum.append("=\"")
                    Entities.escape(accum, `val`, out, true, false, false, false)
                    accum.append('"')
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        accum
            .append("<")
            .append(if (isProcessingInstruction) "!" else "?")
            .append(coreValue())
        getWholeDeclaration(accum, out)
        accum
            .append(if (isProcessingInstruction) "!" else "?")
            .append(">")
    }

    override fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings) {n}

    override fun toString(): String {
        return outerHtml()!!
    }

    override fun clone(): XmlDeclaration {
        return super.clone() as XmlDeclaration
    }
}
