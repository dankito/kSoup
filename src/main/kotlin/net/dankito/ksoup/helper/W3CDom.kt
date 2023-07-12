package net.dankito.ksoup.helper

import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.nodes.Comment
import net.dankito.ksoup.nodes.Document
import net.dankito.ksoup.nodes.DocumentType
import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.parser.HtmlTreeBuilder
import net.dankito.ksoup.select.NodeTraversor
import net.dankito.ksoup.select.NodeVisitor
import net.dankito.ksoup.select.Selector
import org.w3c.dom.*
import org.w3c.dom.Node
import java.io.*
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.*

/**
 * Helper class to transform a [net.dankito.ksoup.nodes.Document] to a [org.w3c.dom.Document],
 * for integration with toolsets that use the W3C DOM.
 */
class W3CDom {
    protected var factory: DocumentBuilderFactory
    private var namespaceAware: Boolean = true // false when using selectXpath, for user's query convenience

    init {
        factory = DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
    }

    /**
     * Returns if this W3C DOM is namespace aware. By default, this will be `true`, but is disabled for simplicity
     * when using XPath selectors in [net.dankito.ksoup.nodes.Element.selectXpath].
     * @return the current namespace aware setting.
     */
    fun namespaceAware(): Boolean {
        return namespaceAware
    }

    /**
     * Update the namespace aware setting. This impacts the factory that is used to create W3C nodes from jsoup nodes.
     *
     * For HTML documents, controls if the document will be in the default `http://www.w3.org/1999/xhtml`
     * namespace if otherwise unset..
     * @param namespaceAware the updated setting
     * @return this W3CDom, for chaining.
     */
    fun namespaceAware(namespaceAware: Boolean): W3CDom {
        this.namespaceAware = namespaceAware
        factory.setNamespaceAware(namespaceAware)
        return this
    }

    /**
     * Convert a jsoup Document to a W3C Document. The created nodes will link back to the original
     * jsoup nodes in the user property [.SourceProperty] (but after conversion, changes on one side will not
     * flow to the other).
     *
     * @param in jsoup doc
     * @return a W3C DOM Document representing the jsoup Document or Element contents.
     */
    fun fromJsoup(`in`: Document): org.w3c.dom.Document {
        // just method API backcompat
        return fromJsoup(`in` as Element)
    }

    /**
     * Convert a jsoup DOM to a W3C Document. The created nodes will link back to the original
     * jsoup nodes in the user property [.SourceProperty] (but after conversion, changes on one side will not
     * flow to the other). The input Element is used as a context node, but the whole surrounding jsoup Document is
     * converted. (If you just want a subtree converted, use [.convert].)
     *
     * @param in jsoup element or doc
     * @return a W3C DOM Document representing the jsoup Document or Element contents.
     * @see .sourceNodes
     * @see .contextNode
     */
    fun fromJsoup(`in`: Element): org.w3c.dom.Document {
        Validate.notNull(`in`)
        val builder: DocumentBuilder
        try {
            builder = factory.newDocumentBuilder()
            val impl: DOMImplementation = builder.getDOMImplementation()
            val out: org.w3c.dom.Document = builder.newDocument()
            val inDoc: Document? = `in`.ownerDocument()
            val doctype: DocumentType? = if (inDoc != null) inDoc.documentType() else null
            if (doctype != null) {
                val documentType: org.w3c.dom.DocumentType =
                    impl.createDocumentType(doctype.name(), doctype.publicId(), doctype.systemId())
                out.appendChild(documentType)
            }
            out.setXmlStandalone(true)
            // if in is Document, use the root element, not the wrapping document, as the context:
            val context: Element? = if ((`in` is Document)) `in`.firstElementChild() else `in`
            out.setUserData(ContextProperty, context, null)
            convert(if (inDoc != null) inDoc else `in`, out)
            return out
        } catch (e: ParserConfigurationException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Converts a jsoup document into the provided W3C Document. If required, you can set options on the output
     * document before converting.
     *
     * @param in jsoup doc
     * @param out w3c doc
     * @see net.dankito.ksoup.helper.W3CDom.fromJsoup
     */
    fun convert(`in`: Document, out: org.w3c.dom.Document) {
        // just provides method API backcompat
        convert(`in` as Element, out)
    }

    /**
     * Converts a jsoup element into the provided W3C Document. If required, you can set options on the output
     * document before converting.
     *
     * @param in jsoup element
     * @param out w3c doc
     * @see net.dankito.ksoup.helper.W3CDom.fromJsoup
     */
    fun convert(`in`: Element, out: org.w3c.dom.Document) {
        val builder = W3CBuilder(out)
        builder.namespaceAware = namespaceAware
        val inDoc: Document? = `in`.ownerDocument()
        if (inDoc != null) {
            if (!StringUtil.isBlank(inDoc.location())) {
                out.setDocumentURI(inDoc.location())
            }
            builder.syntax = inDoc.outputSettings().syntax()
        }
        val rootEl: Element? =
            if (`in` is Document) `in`.firstElementChild() else `in` // skip the #root node if a Document
        NodeTraversor.traverse(builder, rootEl)
    }

    /**
     * Evaluate an XPath query against the supplied document, and return the results.
     * @param xpath an XPath query
     * @param doc the document to evaluate against
     * @return the matches nodes
     */
    fun selectXpath(xpath: String, doc: org.w3c.dom.Document?): NodeList {
        return selectXpath(xpath, doc as Node?)
    }

    /**
     * Evaluate an XPath query against the supplied context node, and return the results.
     * @param xpath an XPath query
     * @param contextNode the context node to evaluate against
     * @return the matches nodes
     */
    fun selectXpath(xpath: String, contextNode: Node?): NodeList {
        Validate.notEmptyParam(xpath, "xpath")
        Validate.notNullParam(contextNode, "contextNode")
        val nodeList: NodeList
        try {
            // if there is a configured XPath factory, use that instead of the Java base impl:
            val property: String? = System.getProperty(XPathFactoryProperty)
            val xPathFactory: XPathFactory =
                if (property != null) XPathFactory.newInstance("jsoup") else XPathFactory.newInstance()
            val expression: XPathExpression = xPathFactory.newXPath().compile(xpath)
            nodeList =
                expression.evaluate(contextNode, XPathConstants.NODESET) as NodeList // love the strong typing here /s
            Validate.notNull(nodeList)
        } catch (e: XPathExpressionException) {
            throw Selector.SelectorParseException("Could not evaluate XPath query [$xpath]: ${e.message}", e)
        } catch (e: XPathFactoryConfigurationException) {
            throw Selector.SelectorParseException("Could not evaluate XPath query [$xpath]: ${e.message}", e)
        }
        return nodeList
    }

    /**
     * Retrieves the original jsoup DOM nodes from a nodelist created by this convertor.
     * @param nodeList the W3C nodes to get the original jsoup nodes from
     * @param nodeType the jsoup node type to retrieve (e.g. Element, DataNode, etc)
     * @param <T> node type
     * @return a list of the original nodes
    </T> */
    fun <T : net.dankito.ksoup.nodes.Node?> sourceNodes(nodeList: NodeList, nodeType: Class<T>): List<T> {
        Validate.notNull(nodeList)
        Validate.notNull(nodeType)
        val nodes: MutableList<T> = ArrayList(nodeList.getLength())
        for (i in 0 until nodeList.getLength()) {
            val node: Node = nodeList.item(i)
            val source: Any = node.getUserData(SourceProperty)
            if (nodeType.isInstance(source)) nodes.add(nodeType.cast(source))
        }
        return nodes
    }

    /**
     * For a Document created by [.fromJsoup], retrieves the W3C context node.
     * @param wDoc Document created by this class
     * @return the corresponding W3C Node to the jsoup Element that was used as the creating context.
     */
    fun contextNode(wDoc: org.w3c.dom.Document?): Node {
        return wDoc!!.getUserData(ContextNodeProperty) as Node
    }

    /**
     * Implements the conversion by walking the input.
     */
    class W3CBuilder(private val doc: org.w3c.dom.Document) : NodeVisitor {
        var namespaceAware: Boolean = true
            internal set
        private val namespacesStack: Stack<HashMap<String, String?>> = Stack() // stack of namespaces, prefix => urn
        private var dest: Node
        var syntax = Document.OutputSettings.Syntax.xml // the syntax (to coerce attributes to). From the input doc if available.
            internal set
        private val contextElement: Element?

        init {
            namespacesStack.push(HashMap())
            dest = doc
            contextElement =
                doc.getUserData(ContextProperty) as Element? // Track the context jsoup Element, so we can save the corresponding w3c element
            val inDoc: Document? = contextElement!!.ownerDocument()
            if (namespaceAware && (inDoc != null) && inDoc.parser().treeBuilder is HtmlTreeBuilder) {
                // as per the WHATWG HTML5 spec § 2.1.3, elements are in the HTML namespace by default
                namespacesStack.peek().put("", xhtmlNs)
            }
        }

        override fun head(source: net.dankito.ksoup.nodes.Node, depth: Int) {
            namespacesStack.push(HashMap(namespacesStack.peek())) // inherit from above on the stack
            if (source is Element) {
                val prefix = updateNamespaces(source)
                val namespace: String? = if (namespaceAware) namespacesStack.peek().get(prefix) else null
                val tagName = source.tagName()

                /* Tag names in XML are quite permissive, but less permissive than HTML. Rather than reimplement the validation,
                we just try to use it as-is. If it fails, insert as a text node instead. We don't try to normalize the
                tagname to something safe, because that isn't going to be meaningful downstream. This seems(?) to be
                how browsers handle the situation, also. https://github.com/jhy/jsoup/issues/1093 */
                try {
                    // use an empty namespace if none is present but the tag name has a prefix
                    val imputedNamespace = if (namespace == null && tagName?.contains(":") == true) "" else namespace
                    val el: org.w3c.dom.Element = doc.createElementNS(imputedNamespace, tagName)
                    copyAttributes(source, el)
                    append(el, source)
                    if (source === contextElement) doc.setUserData(ContextNodeProperty, el, null)
                    dest = el // descend
                } catch (e: DOMException) {
                    append(doc.createTextNode("<" + tagName + ">"), source)
                }
            } else if (source is TextNode) {
                val sourceText: TextNode = source
                val text: Text = doc.createTextNode(sourceText.wholeText)
                append(text, sourceText)
            } else if (source is Comment) {
                val sourceComment: Comment = source
                val comment: org.w3c.dom.Comment = doc.createComment(sourceComment.data)
                append(comment, sourceComment)
            } else if (source is DataNode) {
                val sourceData: DataNode = source
                val node: Text = doc.createTextNode(sourceData.wholeData)
                append(node, sourceData)
            } else {
                // unhandled. note that doctype is not handled here - rather it is used in the initial doc creation
            }
        }

        private fun append(append: Node, source: net.dankito.ksoup.nodes.Node) {
            append.setUserData(SourceProperty, source, null)
            dest.appendChild(append)
        }

        override fun tail(source: net.dankito.ksoup.nodes.Node, depth: Int) {
            if (source is Element && dest.getParentNode() is org.w3c.dom.Element) {
                dest = dest.getParentNode() // undescend
            }
            namespacesStack.pop()
        }

        private fun copyAttributes(source: net.dankito.ksoup.nodes.Node, el: org.w3c.dom.Element) {
            for (attribute: Attribute in source.attributes()) {
                val key: String? = Attribute.Companion.getValidKey(attribute.key, syntax)
                if (key != null) { // null if couldn't be coerced to validity
                    el.setAttribute(key, attribute.value)
                }
            }
        }

        /**
         * Finds any namespaces defined in this element. Returns any tag prefix.
         */
        private fun updateNamespaces(el: Element): String {
            // scan the element for namespace declarations
            // like: xmlns="blah" or xmlns:prefix="blah"
            val attributes: Attributes? = el.attributes()
            for (attr: Attribute in attributes!!) {
                val key: String? = attr.key
                var prefix: String?
                if ((key == xmlnsKey)) {
                    prefix = ""
                } else if (key!!.startsWith(xmlnsPrefix)) {
                    prefix = key.substring(xmlnsPrefix.length)
                } else {
                    continue
                }
                namespacesStack.peek().put(prefix, attr.value)
            }

            // get the element prefix if any
            val pos: Int = el.tagName()!!.indexOf(':')
            return if (pos > 0) el.tagName()!!.substring(0, pos) else ""
        }

        companion object {
            private val xmlnsKey: String = "xmlns"
            private val xmlnsPrefix: String = "xmlns:"
            private val xhtmlNs: String = "http://www.w3.org/1999/xhtml"
        }
    }

    companion object {
        /** For W3C Documents created by this class, this property is set on each node to link back to the original jsoup node.  */
        @JvmField
        val SourceProperty: String = "jsoupSource"
        private val ContextProperty: String = "jsoupContextSource" // tracks the jsoup context element on w3c doc
        private val ContextNodeProperty: String = "jsoupContextNode" // the w3c node used as the creating context

        /**
         * To get support for XPath versions &gt; 1, set this property to the classname of an alternate XPathFactory
         * implementation. (For e.g. `net.sf.saxon.xpath.XPathFactoryImpl`).
         */
        @JvmField
        val XPathFactoryProperty: String = "javax.xml.xpath.XPathFactory:jsoup"

        /**
         * Converts a jsoup DOM to a W3C DOM.
         *
         * @param in jsoup Document
         * @return W3C Document
         */
        @JvmStatic
        fun convert(`in`: Document): org.w3c.dom.Document {
            return (W3CDom().fromJsoup(`in`))
        }
        /**
         * Serialize a W3C document to a String. Provide Properties to define output settings including if HTML or XML. If
         * you don't provide the properties (`null`), the output will be auto-detected based on the content of the
         * document.
         *
         * @param doc Document
         * @param properties (optional/nullable) the output properties to use. See [     ][Transformer.setOutputProperties] and [OutputKeys]
         * @return Document as string
         * @see .OutputHtml
         *
         * @see .OutputXml
         *
         * @see OutputKeys.ENCODING
         *
         * @see OutputKeys.OMIT_XML_DECLARATION
         *
         * @see OutputKeys.STANDALONE
         *
         * @see OutputKeys.STANDALONE
         *
         * @see OutputKeys.DOCTYPE_PUBLIC
         *
         * @see OutputKeys.CDATA_SECTION_ELEMENTS
         *
         * @see OutputKeys.INDENT
         *
         * @see OutputKeys.MEDIA_TYPE
         */
        /**
         * Serialize a W3C document to a String. The output format will be XML or HTML depending on the content of the doc.
         *
         * @param doc Document
         * @return Document as string
         * @see W3CDom.asString
         */
        @JvmStatic
        @JvmOverloads
        fun asString(doc: org.w3c.dom.Document, properties: Map<String, String>? = null): String {
            try {
                val domSource = DOMSource(doc)
                val writer = StringWriter()
                val result = StreamResult(writer)
                val tf: TransformerFactory = TransformerFactory.newInstance()
                val transformer: Transformer = tf.newTransformer()
                if (properties != null) transformer.setOutputProperties(propertiesFromMap(properties))
                if (doc.getDoctype() != null) {
                    val doctype: org.w3c.dom.DocumentType = doc.getDoctype()
                    if (!StringUtil.isBlank(doctype.getPublicId())) transformer.setOutputProperty(
                        OutputKeys.DOCTYPE_PUBLIC,
                        doctype.getPublicId()
                    )
                    if (!StringUtil.isBlank(doctype.getSystemId())) transformer.setOutputProperty(
                        OutputKeys.DOCTYPE_SYSTEM,
                        doctype.getSystemId()
                    ) else if ((doctype.getName().equals("html", ignoreCase = true)
                                && StringUtil.isBlank(doctype.getPublicId())
                                && StringUtil.isBlank(doctype.getSystemId()))
                    ) transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "about:legacy-compat")
                }
                transformer.transform(domSource, result)
                return writer.toString()
            } catch (e: TransformerException) {
                throw IllegalStateException(e)
            }
        }

        fun propertiesFromMap(map: Map<String, String>): Properties {
            val props = Properties()
            props.putAll(map)
            return props
        }

        /** Canned default for HTML output.  */
        @JvmStatic
        fun OutputHtml(): HashMap<String, String> {
            return methodMap("html")
        }

        /** Canned default for XML output.  */
        @JvmStatic
        fun OutputXml(): HashMap<String, String> {
            return methodMap("xml")
        }

        private fun methodMap(method: String): HashMap<String, String> {
            val map: HashMap<String, String> = HashMap()
            map.put(OutputKeys.METHOD, method)
            return map
        }
    }
}
