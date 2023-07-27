package net.dankito.ksoup.nodes

import net.dankito.ksoup.parser.ParseSettings
import net.dankito.ksoup.parser.Parser

/**
 * A comment node.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class Comment(data: String) : LeafNode(data) {

    override fun nodeName(): String {
        return "#comment"
    }

    val data: String
        /**
         * Get the contents of the comment.
         * @return comment content
         */
        get() = coreValue()

    fun setData(data: String?): Comment {
        coreValue(data)
        return this
    }

    override fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        if (out.prettyPrint() && (isEffectivelyFirst && parentNode is Element && (parentNode as Element).tag().formatAsBlock || out.outline())) {
            indent(accum, depth, out)
        }

        accum
            .append("<!--")
            .append(data)
            .append("-->")
    }

    override fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings) { }

    override fun toString(): String {
        return outerHtml()
    }

    override fun clone(): Comment {
        return super.clone() as Comment
    }

    override fun createInstanceForClone() = Comment(value as String)

    val isXmlDeclaration: Boolean
        /**
         * Check if this comment looks like an XML Declaration.
         * @return true if it looks like, maybe, it's an XML Declaration.
         */
        get() {
            val data = data
            return isXmlDeclarationData(data)
        }

    /**
     * Attempt to cast this comment to an XML Declaration node.
     * @return an XML declaration if it could be parsed as one, null otherwise.
     */
    fun asXmlDeclaration(): XmlDeclaration? {
        val data = data
        var decl: XmlDeclaration? = null
        val declContent = data!!.substring(1, data.length - 1)

        // make sure this bogus comment is not immediately followed by another, treat as comment if so
        if (isXmlDeclarationData(declContent)) {
            return null
        }

        val fragment = "<$declContent>"
        // use the HTML parser not XML, so we don't get into a recursive XML Declaration on contrived data
        val doc: Document = Parser.htmlParser().settings(ParseSettings.Companion.preserveCase).parseInput(fragment, baseUri())
        if (doc.body().childrenSize() > 0) {
            val el = doc.body().child(0)
            decl = XmlDeclaration(
                NodeUtils.parser(doc).settings().normalizeTag(el.tagName()),
                data.startsWith("!")
            )
            decl.attributes().addAll(el.attributes())
        }
        return decl
    }

    companion object {
        private fun isXmlDeclarationData(data: String?): Boolean {
            return data!!.length > 1 && (data.startsWith("!") || data.startsWith("?"))
        }
    }
}
