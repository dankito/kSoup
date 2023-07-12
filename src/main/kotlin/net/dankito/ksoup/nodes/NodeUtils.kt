package net.dankito.ksoup.nodes

import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.helper.W3CDom
import net.dankito.ksoup.parser.HtmlTreeBuilder
import net.dankito.ksoup.parser.Parser

/**
 * Internal helpers for Nodes, to keep the actual node APIs relatively clean. A jsoup internal class, so don't use it as
 * there is no contract API).
 */
internal object NodeUtils {
    /**
     * Get the output setting for this node,  or if this node has no document (or parent), retrieve the default output
     * settings
     */
    fun outputSettings(node: Node): Document.OutputSettings {
        val owner = node.ownerDocument()
        return owner?.outputSettings() ?: Document("").outputSettings()
    }

    /**
     * Get the parser that was used to make this node, or the default HTML parser if it has no parent.
     */
    fun parser(node: Node): Parser {
        val doc = node.ownerDocument()
        return doc?.parser() ?: Parser(HtmlTreeBuilder())
    }

    /**
     * This impl works by compiling the input xpath expression, and then evaluating it against a W3C Document converted
     * from the original jsoup element. The original jsoup elements are then fetched from the w3c doc user data (where we
     * stashed them during conversion). This process could potentially be optimized by transpiling the compiled xpath
     * expression to a jsoup Evaluator when there's 1:1 support, thus saving the W3C document conversion stage.
     */
    fun <T : Node> selectXpath(xpath: String, el: Element, nodeType: Class<T>): List<T> {
        Validate.notEmpty(xpath)
        Validate.notNull(el)
        Validate.notNull(nodeType)
        val w3c = W3CDom().namespaceAware(false)
        val wDoc = w3c.fromJsoup(el)
        val contextNode = w3c.contextNode(wDoc)
        val nodeList = w3c.selectXpath(xpath, contextNode)
        return w3c.sourceNodes(nodeList, nodeType)
    }
}
