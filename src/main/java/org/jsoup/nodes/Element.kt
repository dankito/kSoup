package org.jsoup.nodes

import org.jsoup.helper.ChangeNotifyingArrayList
import org.jsoup.helper.Validate
import org.jsoup.internal.NonnullByDefault
import org.jsoup.internal.Normalizer
import org.jsoup.internal.StringUtil
import org.jsoup.parser.Tag
import org.jsoup.parser.TokenQueue
import org.jsoup.select.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * An HTML Element consists of a tag name, attributes, and child nodes (including text nodes and other elements).
 *
 *
 * From an Element, you can extract data, traverse the node graph, and manipulate the HTML.
 */
@NonnullByDefault
open class Element @JvmOverloads constructor(private var tag: Tag, baseUri: String?, internal var attributes: Attributes? = null) : Node() {

    private var shadowChildrenRef: WeakReference<List<Element>>? =
        null // points to child elements shadowed from node children
    @JvmField
    var childNodes: MutableList<Node>

    /**
     * Create a new, standalone element.
     * @param tag tag name
     */
    constructor(tag: String) : this(Tag.valueOf(tag), "", null)
    /**
     * Create a new, standalone Element. (Standalone in that it has no parent.)
     *
     * @param tag tag of this element
     * @param baseUri the base URI (optional, may be null to inherit from parent, or "" to clear parent's)
     * @param attributes initial attributes (optional, may be null)
     * @see .appendChild
     * @see .appendElement
     */
    /**
     * Create a new Element from a Tag and a base URI.
     *
     * @param tag element tag
     * @param baseUri the base URI of this element. Optional, and will inherit from its parent, if any.
     * @see Tag.valueOf
     */
    init {
        Validate.notNull(tag)
        childNodes = Node.EmptyNodes
        if (baseUri != null) {
            setBaseUri(baseUri)
        }
    }

    /**
     * Internal test to check if a nodelist object has been created.
     */
    fun hasChildNodes(): Boolean {
        return childNodes !== Node.Companion.EmptyNodes
    }

    public override fun ensureChildNodes(): List<Node?> {
        if (childNodes === Node.Companion.EmptyNodes) {
            childNodes = NodeList(this, 4)
        }
        return childNodes
    }

    public override fun hasAttributes(): Boolean {
        return attributes != null
    }

    override fun attributes(): Attributes {
        if (attributes == null) // not using hasAttributes, as doesn't clear warning
            attributes = Attributes()
        return attributes
    }

    override fun baseUri(): String {
        return searchUpForAttribute(this, BaseUriKey)
    }

    override fun doSetBaseUri(baseUri: String) {
        attributes().put(BaseUriKey, baseUri)
    }

    override fun childNodeSize(): Int {
        return childNodes.size
    }

    override fun nodeName(): String? {
        return tag.getName()
    }

    /**
     * Get the name of the tag for this element. E.g. `div`. If you are using [ case preserving parsing][ParseSettings.preserveCase], this will return the source's original case.
     *
     * @return the tag name
     */
    fun tagName(): String? {
        return tag.getName()
    }

    /**
     * Get the normalized name of this Element's tag. This will always be the lower-cased version of the tag, regardless
     * of the tag case preserving setting of the parser. For e.g., `<DIV>` and `<div>` both have a
     * normal name of `div`.
     * @return normal name
     */
    override fun normalName(): String? {
        return tag!!.normalName()
    }

    /**
     * Change (rename) the tag of this element. For example, convert a `<span>` to a `<div>` with
     * `el.tagName("div");`.
     *
     * @param tagName new tag name for this element
     * @return this element, for chaining
     * @see Elements.tagName
     */
    fun tagName(tagName: String?): Element {
        Validate.notEmptyParam(tagName, "tagName")
        tag = Tag.Companion.valueOf(
            tagName,
            NodeUtils.parser(this)!!.settings()
        ) // maintains the case option of the original parse
        return this
    }

    /**
     * Get the Tag for this element.
     *
     * @return the tag object
     */
    fun tag(): Tag? {
        return tag
    }

    val isBlock: Boolean
        /**
         * Test if this element is a block-level element. (E.g. `<div> == true` or an inline element
         * `<span> == false`).
         *
         * @return true if block, false if not (and thus inline)
         */
        get() = tag!!.isBlock

    /**
     * Get the `id` attribute of this element.
     *
     * @return The id attribute, if present, or an empty string if not.
     */
    fun id(): String? {
        return if (attributes != null) attributes!!.getIgnoreCase("id") else ""
    }

    /**
     * Set the `id` attribute of this element.
     * @param id the ID value to use
     * @return this Element, for chaining
     */
    fun id(id: String): Element {
        Validate.notNull(id)
        attr("id", id)
        return this
    }

    /**
     * Set an attribute value on this element. If this element already has an attribute with the
     * key, its value is updated; otherwise, a new attribute is added.
     *
     * @return this element
     */
    override fun attr(attributeKey: String, attributeValue: String?): Element {
        super.attr(attributeKey, attributeValue)
        return this
    }

    /**
     * Set a boolean attribute value on this element. Setting to `true` sets the attribute value to "" and
     * marks the attribute as boolean so no value is written out. Setting to `false` removes the attribute
     * with the same key if it exists.
     *
     * @param attributeKey the attribute key
     * @param attributeValue the attribute value
     *
     * @return this element
     */
    fun attr(attributeKey: String, attributeValue: Boolean): Element {
        attributes().put(attributeKey, attributeValue)
        return this
    }

    /**
     * Get this element's HTML5 custom data attributes. Each attribute in the element that has a key
     * starting with "data-" is included the dataset.
     *
     *
     * E.g., the element `<div data-package="jsoup" data-language="Java" class="group">...` has the dataset
     * `package=jsoup, language=java`.
     *
     *
     * This map is a filtered view of the element's attribute map. Changes to one map (add, remove, update) are reflected
     * in the other map.
     *
     *
     * You can find elements that have data attributes using the `[^data-]` attribute key prefix selector.
     * @return a map of `key=value` custom data attributes.
     */
    fun dataset(): Map<String, String> {
        return attributes().dataset()
    }

    override fun parent(): Element? {
        return parentNode as Element?
    }

    /**
     * Get this element's parent and ancestors, up to the document root.
     * @return this element's stack of parents, starting with the closest first.
     */
    fun parents(): Elements {
        val parents = Elements()
        var parent = parent()
        while (parent != null && !parent.isNode("#root")) {
            parents.add(parent)
            parent = parent.parent()
        }
        return parents
    }

    /**
     * Get a child element of this element, by its 0-based index number.
     *
     *
     * Note that an element can have both mixed Nodes and Elements as children. This method inspects
     * a filtered list of children that are elements, and the index is based on that filtered list.
     *
     *
     * @param index the index number of the element to retrieve
     * @return the child element, if it exists, otherwise throws an `IndexOutOfBoundsException`
     * @see .childNode
     */
    fun child(index: Int): Element {
        return childElementsList()[index]
    }

    /**
     * Get the number of child nodes of this element that are elements.
     *
     *
     * This method works on the same filtered list like [.child]. Use [.childNodes] and [ ][.childNodeSize] to get the unfiltered Nodes (e.g. includes TextNodes etc.)
     *
     *
     * @return the number of child nodes that are elements
     * @see .children
     * @see .child
     */
    fun childrenSize(): Int {
        return childElementsList().size
    }

    /**
     * Get this element's child elements.
     *
     *
     * This is effectively a filter on [.childNodes] to get Element nodes.
     *
     * @return child elements. If this element has no children, returns an empty list.
     * @see .childNodes
     */
    fun children(): Elements {
        return Elements(childElementsList())
    }

    /**
     * Maintains a shadow copy of this element's child elements. If the nodelist is changed, this cache is invalidated.
     * TODO - think about pulling this out as a helper as there are other shadow lists (like in Attributes) kept around.
     * @return a list of child elements
     */
    fun childElementsList(): List<Element> {
        if (childNodeSize() == 0) { // short circuit creating empty
            return EmptyChildren
        }

        var children = shadowChildrenRef?.get()
        if (children == null) {
            val size = childNodes.size
            children = ArrayList(size)
            for (i in 0 until size) {
                val node = childNodes[i]
                if (node is Element) {
                    children.add(node)
                }
            }
            shadowChildrenRef = WeakReference(children)
        }

        return children
    }

    /**
     * Clears the cached shadow child elements.
     */
    public override fun nodelistChanged() {
        super.nodelistChanged()
        shadowChildrenRef = null
    }

    /**
     * Get this element's child text nodes. The list is unmodifiable but the text nodes may be manipulated.
     *
     *
     * This is effectively a filter on [.childNodes] to get Text nodes.
     * @return child text nodes. If this element has no text nodes, returns an
     * empty list.
     *
     * For example, with the input HTML: `<p>One <span>Two</span> Three <br> Four</p>` with the `p` element selected:
     *
     *  * `p.text()` = `"One Two Three Four"`
     *  * `p.ownText()` = `"One Three Four"`
     *  * `p.children()` = `Elements[<span>, <br>]`
     *  * `p.childNodes()` = `List<Node>["One ", <span>, " Three ", <br>, " Four"]`
     *  * `p.textNodes()` = `List<TextNode>["One ", " Three ", " Four"]`
     *
     */
    fun textNodes(): List<TextNode> {
        val textNodes: MutableList<TextNode> = ArrayList()
        for (node in childNodes) {
            if (node is TextNode) textNodes.add(node)
        }
        return Collections.unmodifiableList(textNodes)
    }

    /**
     * Get this element's child data nodes. The list is unmodifiable but the data nodes may be manipulated.
     *
     *
     * This is effectively a filter on [.childNodes] to get Data nodes.
     *
     * @return child data nodes. If this element has no data nodes, returns an
     * empty list.
     * @see .data
     */
    fun dataNodes(): List<DataNode> {
        val dataNodes: MutableList<DataNode> = ArrayList()
        for (node in childNodes) {
            if (node is DataNode) dataNodes.add(node)
        }
        return Collections.unmodifiableList(dataNodes)
    }

    /**
     * Find elements that match the [Selector] CSS query, with this element as the starting context. Matched elements
     * may include this element, or any of its children.
     *
     * This method is generally more powerful to use than the DOM-type `getElementBy*` methods, because
     * multiple filters can be combined, e.g.:
     *
     *  * `el.select("a[href]")` - finds links (`a` tags with `href` attributes)
     *  * `el.select("a[href*=example.com]")` - finds links pointing to example.com (loosely)
     *
     *
     * See the query syntax documentation in [org.jsoup.select.Selector].
     *
     * Also known as `querySelectorAll()` in the Web DOM.
     *
     * @param cssQuery a [Selector] CSS-like query
     * @return an [Elements] list containing elements that match the query (empty if none match)
     * @see Selector selector query syntax
     *
     * @see QueryParser.parse
     * @throws Selector.SelectorParseException (unchecked) on an invalid CSS query.
     */
    fun select(cssQuery: String?): Elements {
        return Selector.select(cssQuery, this)
    }

    /**
     * Find elements that match the supplied Evaluator. This has the same functionality as [.select], but
     * may be useful if you are running the same query many times (on many documents) and want to save the overhead of
     * repeatedly parsing the CSS query.
     * @param evaluator an element evaluator
     * @return an [Elements] list containing elements that match the query (empty if none match)
     */
    fun select(evaluator: Evaluator?): Elements {
        return Selector.select(evaluator, this)
    }

    /**
     * Find the first Element that matches the [Selector] CSS query, with this element as the starting context.
     *
     * This is effectively the same as calling `element.select(query).first()`, but is more efficient as query
     * execution stops on the first hit.
     *
     * Also known as `querySelector()` in the Web DOM.
     * @param cssQuery cssQuery a [Selector] CSS-like query
     * @return the first matching element, or **`null`** if there is no match.
     * @see .expectFirst
     */
    fun selectFirst(cssQuery: String?): Element? {
        return Selector.selectFirst(cssQuery, this)
    }

    /**
     * Finds the first Element that matches the supplied Evaluator, with this element as the starting context, or
     * `null` if none match.
     *
     * @param evaluator an element evaluator
     * @return the first matching element (walking down the tree, starting from this element), or `null` if none
     * match.
     */
    fun selectFirst(evaluator: Evaluator?): Element? {
        return Collector.findFirst(evaluator, this)
    }

    /**
     * Just like [.selectFirst], but if there is no match, throws an [IllegalArgumentException]. This
     * is useful if you want to simply abort processing on a failed match.
     * @param cssQuery a [Selector] CSS-like query
     * @return the first matching element
     * @throws IllegalArgumentException if no match is found
     * @since 1.15.2
     */
    fun expectFirst(cssQuery: String?): Element? {
        return Validate.ensureNotNull(
            Selector.selectFirst(cssQuery, this),
            if (parent() != null) "No elements matched the query '%s' on element '%s'." else "No elements matched the query '%s' in the document.",
            cssQuery,
            this.tagName()
        ) as Element
    }

    /**
     * Checks if this element matches the given [Selector] CSS query. Also knows as `matches()` in the Web
     * DOM.
     *
     * @param cssQuery a [Selector] CSS query
     * @return if this element matches the query
     */
    fun `is`(cssQuery: String?): Boolean {
        return `is`(QueryParser.Companion.parse(cssQuery))
    }

    /**
     * Check if this element matches the given evaluator.
     * @param evaluator an element evaluator
     * @return if this element matches
     */
    fun `is`(evaluator: Evaluator?): Boolean {
        return evaluator!!.matches(root(), this)
    }

    /**
     * Find the closest element up the tree of parents that matches the specified CSS query. Will return itself, an
     * ancestor, or `null` if there is no such matching element.
     * @param cssQuery a [Selector] CSS query
     * @return the closest ancestor element (possibly itself) that matches the provided evaluator. `null` if not
     * found.
     */
    fun closest(cssQuery: String?): Element? {
        return closest(QueryParser.Companion.parse(cssQuery))
    }

    /**
     * Find the closest element up the tree of parents that matches the specified evaluator. Will return itself, an
     * ancestor, or `null` if there is no such matching element.
     * @param evaluator a query evaluator
     * @return the closest ancestor element (possibly itself) that matches the provided evaluator. `null` if not
     * found.
     */
    fun closest(evaluator: Evaluator?): Element? {
        Validate.notNull(evaluator)
        var el: Element? = this
        val root = root()
        do {
            if (evaluator!!.matches(root, el)) return el
            el = el!!.parent()
        } while (el != null)
        return null
    }

    /**
     * Find Elements that match the supplied XPath expression.
     *
     * Note that for convenience of writing the Xpath expression, namespaces are disabled, and queries can be
     * expressed using the element's local name only.
     *
     * By default, XPath 1.0 expressions are supported. If you would to use XPath 2.0 or higher, you can provide an
     * alternate XPathFactory implementation:
     *
     *  1. Add the implementation to your classpath. E.g. to use [Saxon-HE](https://www.saxonica.com/products/products.xml), add [net.sf.saxon:Saxon-HE](https://mvnrepository.com/artifact/net.sf.saxon/Saxon-HE) to your build.
     *  1. Set the system property `javax.xml.xpath.XPathFactory:jsoup` to the implementing classname. E.g.:<br></br>
     * `System.setProperty(W3CDom.XPathFactoryProperty, "net.sf.saxon.xpath.XPathFactoryImpl");`
     *
     *
     *
     * @param xpath XPath expression
     * @return matching elements, or an empty list if none match.
     * @see .selectXpath
     * @since 1.14.3
     */
    fun selectXpath(xpath: String?): Elements {
        return Elements(NodeUtils.selectXpath(xpath, this, Element::class.java))
    }

    /**
     * Find Nodes that match the supplied XPath expression.
     *
     * For example, to select TextNodes under `p` elements:
     * <pre>List&lt;TextNode&gt; textNodes = doc.selectXpath("//body//p//text()", TextNode.class);</pre>
     *
     * Note that in the jsoup DOM, Attribute objects are not Nodes. To directly select attribute values, do something
     * like:
     * <pre>List&lt;String&gt; hrefs = doc.selectXpath("//a").eachAttr("href");</pre>
     * @param xpath XPath expression
     * @param nodeType the jsoup node type to return
     * @see .selectXpath
     * @return a list of matching nodes
     * @since 1.14.3
     */
    fun <T : Node?> selectXpath(xpath: String?, nodeType: Class<T>): List<T?>? {
        return NodeUtils.selectXpath(xpath, this, nodeType)
    }

    /**
     * Insert a node to the end of this Element's children. The incoming node will be re-parented.
     *
     * @param child node to add.
     * @return this Element, for chaining
     * @see .prependChild
     * @see .insertChildren
     */
    fun appendChild(child: Node?): Element {
        Validate.notNull(child)

        // was - Node#addChildren(child). short-circuits an array create and a loop.
        reparentChild(child!!)
        ensureChildNodes()
        childNodes.add(child)
        child.setSiblingIndex(childNodes.size - 1)
        return this
    }

    /**
     * Insert the given nodes to the end of this Element's children.
     *
     * @param children nodes to add
     * @return this Element, for chaining
     * @see .insertChildren
     */
    fun appendChildren(children: Collection<Node?>?): Element {
        insertChildren(-1, children)
        return this
    }

    /**
     * Add this element to the supplied parent element, as its next child.
     *
     * @param parent element to which this element will be appended
     * @return this element, so that you can continue modifying the element
     */
    fun appendTo(parent: Element): Element {
        Validate.notNull(parent)
        parent.appendChild(this)
        return this
    }

    /**
     * Add a node to the start of this element's children.
     *
     * @param child node to add.
     * @return this element, so that you can add more child nodes or elements.
     */
    fun prependChild(child: Node?): Element {
        Validate.notNull(child)
        addChildren(0, child!!)
        return this
    }

    /**
     * Insert the given nodes to the start of this Element's children.
     *
     * @param children nodes to add
     * @return this Element, for chaining
     * @see .insertChildren
     */
    fun prependChildren(children: Collection<Node?>?): Element {
        insertChildren(0, children)
        return this
    }

    /**
     * Inserts the given child nodes into this element at the specified index. Current nodes will be shifted to the
     * right. The inserted nodes will be moved from their current parent. To prevent moving, copy the nodes first.
     *
     * @param index 0-based index to insert children at. Specify `0` to insert at the start, `-1` at the
     * end
     * @param children child nodes to insert
     * @return this element, for chaining.
     */
    fun insertChildren(index: Int, children: Collection<Node?>?): Element {
        var index = index
        Validate.notNull(children, "Children collection to be inserted must not be null.")
        val currentSize = childNodeSize()
        if (index < 0) index += currentSize + 1 // roll around
        Validate.isTrue(index >= 0 && index <= currentSize, "Insert position out of bounds.")
        val nodes = ArrayList(children)
        val nodeArray = nodes.toTypedArray<Node?>()
        addChildren(index, *nodeArray)
        return this
    }

    /**
     * Inserts the given child nodes into this element at the specified index. Current nodes will be shifted to the
     * right. The inserted nodes will be moved from their current parent. To prevent moving, copy the nodes first.
     *
     * @param index 0-based index to insert children at. Specify `0` to insert at the start, `-1` at the
     * end
     * @param children child nodes to insert
     * @return this element, for chaining.
     */
    fun insertChildren(index: Int, vararg children: Node?): Element {
        var index = index
        Validate.notNull(children, "Children collection to be inserted must not be null.")
        val currentSize = childNodeSize()
        if (index < 0) index += currentSize + 1 // roll around
        Validate.isTrue(index >= 0 && index <= currentSize, "Insert position out of bounds.")
        addChildren(index, *children)
        return this
    }

    /**
     * Create a new element by tag name, and add it as the last child.
     *
     * @param tagName the name of the tag (e.g. `div`).
     * @return the new element, to allow you to add content to it, e.g.:
     * `parent.appendElement("h1").attr("id", "header").text("Welcome");`
     */
    fun appendElement(tagName: String?): Element {
        val child = Element(
            Tag.Companion.valueOf(
                tagName, NodeUtils.parser(this)!!
                    .settings()
            ), baseUri()
        )
        appendChild(child)
        return child
    }

    /**
     * Create a new element by tag name, and add it as the first child.
     *
     * @param tagName the name of the tag (e.g. `div`).
     * @return the new element, to allow you to add content to it, e.g.:
     * `parent.prependElement("h1").attr("id", "header").text("Welcome");`
     */
    fun prependElement(tagName: String?): Element {
        val child = Element(
            Tag.Companion.valueOf(
                tagName, NodeUtils.parser(this)!!
                    .settings()
            ), baseUri()
        )
        prependChild(child)
        return child
    }

    /**
     * Create and append a new TextNode to this element.
     *
     * @param text the (un-encoded) text to add
     * @return this element
     */
    fun appendText(text: String): Element {
        Validate.notNull(text)
        val node = TextNode(text)
        appendChild(node)
        return this
    }

    /**
     * Create and prepend a new TextNode to this element.
     *
     * @param text the decoded text to add
     * @return this element
     */
    fun prependText(text: String): Element {
        Validate.notNull(text)
        val node = TextNode(text)
        prependChild(node)
        return this
    }

    /**
     * Add inner HTML to this element. The supplied HTML will be parsed, and each node appended to the end of the children.
     * @param html HTML to add inside this element, after the existing HTML
     * @return this element
     * @see .html
     */
    fun append(html: String?): Element {
        Validate.notNull(html)
        val nodes = NodeUtils.parser(this)!!
            .parseFragmentInput(html, this, baseUri())
        addChildren(*nodes!!.toTypedArray<Node?>())
        return this
    }

    /**
     * Add inner HTML into this element. The supplied HTML will be parsed, and each node prepended to the start of the element's children.
     * @param html HTML to add inside this element, before the existing HTML
     * @return this element
     * @see .html
     */
    fun prepend(html: String?): Element {
        Validate.notNull(html)
        val nodes = NodeUtils.parser(this)!!
            .parseFragmentInput(html, this, baseUri())
        addChildren(0, *nodes!!.toTypedArray<Node?>())
        return this
    }

    /**
     * Insert the specified HTML into the DOM before this element (as a preceding sibling).
     *
     * @param html HTML to add before this element
     * @return this element, for chaining
     * @see .after
     */
    override fun before(html: String): Element? {
        return super.before(html) as Element
    }

    /**
     * Insert the specified node into the DOM before this node (as a preceding sibling).
     * @param node to add before this element
     * @return this Element, for chaining
     * @see .after
     */
    override fun before(node: Node?): Element? {
        return super.before(node) as Element
    }

    /**
     * Insert the specified HTML into the DOM after this element (as a following sibling).
     *
     * @param html HTML to add after this element
     * @return this element, for chaining
     * @see .before
     */
    override fun after(html: String): Element? {
        return super.after(html) as Element
    }

    /**
     * Insert the specified node into the DOM after this node (as a following sibling).
     * @param node to add after this element
     * @return this element, for chaining
     * @see .before
     */
    override fun after(node: Node?): Element? {
        return super.after(node) as Element
    }

    /**
     * Remove all the element's child nodes. Any attributes are left as-is.
     * @return this element
     */
    override fun empty(): Element {
        childNodes.clear()
        return this
    }

    /**
     * Wrap the supplied HTML around this element.
     *
     * @param html HTML to wrap around this element, e.g. `<div class="head"></div>`. Can be arbitrarily deep.
     * @return this element, for chaining.
     */
    override fun wrap(html: String?): Element? {
        return super.wrap(html) as Element
    }

    /**
     * Get a CSS selector that will uniquely select this element.
     *
     *
     * If the element has an ID, returns #id;
     * otherwise returns the parent (if any) CSS selector, followed by &#39;&gt;&#39;,
     * followed by a unique selector for the element (tag.class.class:nth-child(n)).
     *
     *
     * @return the CSS Path that can be used to retrieve the element in a selector.
     */
    fun cssSelector(): String? {
        if (id()!!.length > 0) {
            // prefer to return the ID - but check that it's actually unique first!
            val idSel = "#" + TokenQueue.Companion.escapeCssIdentifier(id())
            val doc = ownerDocument()
            if (doc != null) {
                val els = doc.select(idSel)
                if (els!!.size == 1 && els[0] === this) // otherwise, continue to the nth-child impl
                    return idSel
            } else {
                return idSel // no ownerdoc, return the ID selector
            }
        }

        // Escape tagname, and translate HTML namespace ns:tag to CSS namespace syntax ns|tag
        val tagName: String = TokenQueue.Companion.escapeCssIdentifier(tagName())!!
            .replace("\\:", "|")
        val selector = StringUtil.borrowBuilder().append(tagName)
        // String classes = StringUtil.join(classNames().stream().map(TokenQueue::escapeCssIdentifier).iterator(), ".");
        // todo - replace with ^^ in 1.16.1 when we enable Android support for stream etc
        val escapedClasses = StringUtil.StringJoiner(".")
        for (name in classNames()) escapedClasses.add(TokenQueue.Companion.escapeCssIdentifier(name))
        val classes = escapedClasses.complete()
        if (classes!!.length > 0) selector.append('.').append(classes)
        if (parent() == null || parent() is Document) // don't add Document to selector, as will always have a html node
            return StringUtil.releaseBuilder(selector)
        selector.insert(0, " > ")
        if (parent()!!.select(selector.toString())!!.size > 1) selector.append(
            String.format(
                ":nth-child(%d)", elementSiblingIndex() + 1
            )
        )
        return parent()!!.cssSelector() + StringUtil.releaseBuilder(selector)
    }

    /**
     * Get sibling elements. If the element has no sibling elements, returns an empty list. An element is not a sibling
     * of itself, so will not be included in the returned list.
     * @return sibling elements
     */
    fun siblingElements(): Elements {
        val parent = parent()
        if (parent == null) {
            return Elements(0)
        }

        val elements: List<Element> = parent.childElementsList()
        val siblings = Elements(elements.size - 1)
        for (el in elements) if (el !== this) siblings.add(el)
        return siblings
    }

    /**
     * Gets the next sibling element of this element. E.g., if a `div` contains two `p`s,
     * the `nextElementSibling` of the first `p` is the second `p`.
     *
     *
     * This is similar to [.nextSibling], but specifically finds only Elements
     *
     * @return the next element, or null if there is no next element
     * @see .previousElementSibling
     */
    fun nextElementSibling(): Element? {
        var next: Node = this
        while (next.nextSibling().also { next = it!! } != null) {
            if (next is Element) return next as Element
        }
        return null
    }

    /**
     * Get each of the sibling elements that come after this element.
     *
     * @return each of the element siblings after this element, or an empty list if there are no next sibling elements
     */
    fun nextElementSiblings(): Elements? {
        return nextElementSiblings(true)
    }

    /**
     * Gets the previous element sibling of this element.
     * @return the previous element, or null if there is no previous element
     * @see .nextElementSibling
     */
    fun previousElementSibling(): Element? {
        var prev: Node = this
        while (prev.previousSibling().also { prev = it!! } != null) {
            if (prev is Element) return prev as Element
        }
        return null
    }

    /**
     * Get each of the element siblings before this element.
     *
     * @return the previous element siblings, or an empty list if there are none.
     */
    fun previousElementSiblings(): Elements? {
        return nextElementSiblings(false)
    }

    private fun nextElementSiblings(next: Boolean): Elements? {
        val els = Elements()
        if (parentNode == null) return els
        els.add(this)
        return if (next) els.nextAll() else els.prevAll()
    }

    /**
     * Gets the first Element sibling of this element. That may be this element.
     * @return the first sibling that is an element (aka the parent's first element child)
     */
    fun firstElementSibling(): Element? {
        return if (parent() != null) {
            parent()!!.firstElementChild()
        } else this // orphan is its own first sibling
    }

    /**
     * Get the list index of this element in its element sibling list. I.e. if this is the first element
     * sibling, returns 0.
     * @return position in element sibling list
     */
    fun elementSiblingIndex(): Int {
        return if (parent() == null) 0 else indexInList(
            this,
            parent()!!.childElementsList()
        )
    }

    /**
     * Gets the last element sibling of this element. That may be this element.
     * @return the last sibling that is an element (aka the parent's last element child)
     */
    fun lastElementSibling(): Element? {
        return if (parent() != null) {
            parent()!!.lastElementChild()
        } else this
    }

    /**
     * Gets the first child of this Element that is an Element, or `null` if there is none.
     * @return the first Element child node, or null.
     * @see .firstChild
     * @see .lastElementChild
     * @since 1.15.2
     */
    fun firstElementChild(): Element? {
        var child = firstChild()
        while (child != null) {
            if (child is Element) return child
            child = child.nextSibling()
        }
        return null
    }

    /**
     * Gets the last child of this Element that is an Element, or @{code null} if there is none.
     * @return the last Element child node, or null.
     * @see .lastChild
     * @see .firstElementChild
     * @since 1.15.2
     */
    fun lastElementChild(): Element? {
        var child = lastChild()
        while (child != null) {
            if (child is Element) return child
            child = child.previousSibling()
        }
        return null
    }
    // DOM type methods
    /**
     * Finds elements, including and recursively under this element, with the specified tag name.
     * @param tagName The tag name to search for (case insensitively).
     * @return a matching unmodifiable list of elements. Will be empty if this element and none of its children match.
     */
    fun getElementsByTag(tagName: String?): Elements? {
        var tagName = tagName
        Validate.notEmpty(tagName)
        tagName = Normalizer.normalize(tagName)
        return Collector.collect(Evaluator.Tag(tagName), this)
    }

    /**
     * Find an element by ID, including or under this element.
     *
     *
     * Note that this finds the first matching ID, starting with this element. If you search down from a different
     * starting point, it is possible to find a different element by ID. For unique element by ID within a Document,
     * use [Document.getElementById]
     * @param id The ID to search for.
     * @return The first matching element by ID, starting with this element, or null if none found.
     */
    fun getElementById(id: String?): Element? {
        Validate.notEmpty(id)
        val elements = Collector.collect(Evaluator.Id(id), this)
        return if (elements.size > 0) elements[0] else null
    }

    /**
     * Find elements that have this class, including or under this element. Case-insensitive.
     *
     *
     * Elements can have multiple classes (e.g. `<div class="header round first">`). This method
     * checks each class, so you can find the above with `el.getElementsByClass("header");`.
     *
     * @param className the name of the class to search for.
     * @return elements with the supplied class name, empty if none
     * @see .hasClass
     * @see .classNames
     */
    fun getElementsByClass(className: String): Elements? {
        Validate.notEmpty(className)
        return Collector.collect(Evaluator.Class(className), this)
    }

    /**
     * Find elements that have a named attribute set. Case-insensitive.
     *
     * @param key name of the attribute, e.g. `href`
     * @return elements that have this attribute, empty if none
     */
    fun getElementsByAttribute(key: String): Elements? {
        var key = key
        Validate.notEmpty(key)
        key = key.trim { it <= ' ' }
        return Collector.collect(Evaluator.Attribute(key), this)
    }

    /**
     * Find elements that have an attribute name starting with the supplied prefix. Use `data-` to find elements
     * that have HTML5 datasets.
     * @param keyPrefix name prefix of the attribute e.g. `data-`
     * @return elements that have attribute names that start with the prefix, empty if none.
     */
    fun getElementsByAttributeStarting(keyPrefix: String): Elements? {
        var keyPrefix = keyPrefix
        Validate.notEmpty(keyPrefix)
        keyPrefix = keyPrefix.trim { it <= ' ' }
        return Collector.collect(Evaluator.AttributeStarting(keyPrefix), this)
    }

    /**
     * Find elements that have an attribute with the specific value. Case-insensitive.
     *
     * @param key name of the attribute
     * @param value value of the attribute
     * @return elements that have this attribute with this value, empty if none
     */
    fun getElementsByAttributeValue(key: String?, value: String?): Elements? {
        return Collector.collect(Evaluator.AttributeWithValue(key, value), this)
    }

    /**
     * Find elements that either do not have this attribute, or have it with a different value. Case-insensitive.
     *
     * @param key name of the attribute
     * @param value value of the attribute
     * @return elements that do not have a matching attribute
     */
    fun getElementsByAttributeValueNot(key: String?, value: String?): Elements? {
        return Collector.collect(Evaluator.AttributeWithValueNot(key, value), this)
    }

    /**
     * Find elements that have attributes that start with the value prefix. Case-insensitive.
     *
     * @param key name of the attribute
     * @param valuePrefix start of attribute value
     * @return elements that have attributes that start with the value prefix
     */
    fun getElementsByAttributeValueStarting(key: String?, valuePrefix: String?): Elements? {
        return Collector.collect(Evaluator.AttributeWithValueStarting(key, valuePrefix), this)
    }

    /**
     * Find elements that have attributes that end with the value suffix. Case-insensitive.
     *
     * @param key name of the attribute
     * @param valueSuffix end of the attribute value
     * @return elements that have attributes that end with the value suffix
     */
    fun getElementsByAttributeValueEnding(key: String?, valueSuffix: String?): Elements? {
        return Collector.collect(Evaluator.AttributeWithValueEnding(key, valueSuffix), this)
    }

    /**
     * Find elements that have attributes whose value contains the match string. Case-insensitive.
     *
     * @param key name of the attribute
     * @param match substring of value to search for
     * @return elements that have attributes containing this text
     */
    fun getElementsByAttributeValueContaining(key: String?, match: String?): Elements? {
        return Collector.collect(Evaluator.AttributeWithValueContaining(key, match), this)
    }

    /**
     * Find elements that have an attribute whose value matches the supplied regular expression.
     * @param key name of the attribute
     * @param pattern compiled regular expression to match against attribute values
     * @return elements that have attributes matching this regular expression
     */
    fun getElementsByAttributeValueMatching(key: String?, pattern: Pattern): Elements? {
        return Collector.collect(Evaluator.AttributeWithValueMatching(key, pattern), this)
    }

    /**
     * Find elements that have attributes whose values match the supplied regular expression.
     * @param key name of the attribute
     * @param regex regular expression to match against attribute values. You can use [embedded flags](http://java.sun.com/docs/books/tutorial/essential/regex/pattern.html#embedded) (such as (?i) and (?m) to control regex options.
     * @return elements that have attributes matching this regular expression
     */
    fun getElementsByAttributeValueMatching(key: String?, regex: String): Elements? {
        val pattern: Pattern
        pattern = try {
            Pattern.compile(regex)
        } catch (e: PatternSyntaxException) {
            throw IllegalArgumentException("Pattern syntax error: $regex", e)
        }
        return getElementsByAttributeValueMatching(key, pattern)
    }

    /**
     * Find elements whose sibling index is less than the supplied index.
     * @param index 0-based index
     * @return elements less than index
     */
    fun getElementsByIndexLessThan(index: Int): Elements? {
        return Collector.collect(Evaluator.IndexLessThan(index), this)
    }

    /**
     * Find elements whose sibling index is greater than the supplied index.
     * @param index 0-based index
     * @return elements greater than index
     */
    fun getElementsByIndexGreaterThan(index: Int): Elements? {
        return Collector.collect(Evaluator.IndexGreaterThan(index), this)
    }

    /**
     * Find elements whose sibling index is equal to the supplied index.
     * @param index 0-based index
     * @return elements equal to index
     */
    fun getElementsByIndexEquals(index: Int): Elements? {
        return Collector.collect(Evaluator.IndexEquals(index), this)
    }

    /**
     * Find elements that contain the specified string. The search is case-insensitive. The text may appear directly
     * in the element, or in any of its descendants.
     * @param searchText to look for in the element's text
     * @return elements that contain the string, case-insensitive.
     * @see Element.text
     */
    fun getElementsContainingText(searchText: String?): Elements? {
        return Collector.collect(Evaluator.ContainsText(searchText), this)
    }

    /**
     * Find elements that directly contain the specified string. The search is case-insensitive. The text must appear directly
     * in the element, not in any of its descendants.
     * @param searchText to look for in the element's own text
     * @return elements that contain the string, case-insensitive.
     * @see Element.ownText
     */
    fun getElementsContainingOwnText(searchText: String?): Elements? {
        return Collector.collect(Evaluator.ContainsOwnText(searchText), this)
    }

    /**
     * Find elements whose text matches the supplied regular expression.
     * @param pattern regular expression to match text against
     * @return elements matching the supplied regular expression.
     * @see Element.text
     */
    fun getElementsMatchingText(pattern: Pattern): Elements? {
        return Collector.collect(Evaluator.Matches(pattern), this)
    }

    /**
     * Find elements whose text matches the supplied regular expression.
     * @param regex regular expression to match text against. You can use [embedded flags](http://java.sun.com/docs/books/tutorial/essential/regex/pattern.html#embedded) (such as (?i) and (?m) to control regex options.
     * @return elements matching the supplied regular expression.
     * @see Element.text
     */
    fun getElementsMatchingText(regex: String): Elements? {
        val pattern: Pattern
        pattern = try {
            Pattern.compile(regex)
        } catch (e: PatternSyntaxException) {
            throw IllegalArgumentException("Pattern syntax error: $regex", e)
        }
        return getElementsMatchingText(pattern)
    }

    /**
     * Find elements whose own text matches the supplied regular expression.
     * @param pattern regular expression to match text against
     * @return elements matching the supplied regular expression.
     * @see Element.ownText
     */
    fun getElementsMatchingOwnText(pattern: Pattern): Elements? {
        return Collector.collect(Evaluator.MatchesOwn(pattern), this)
    }

    /**
     * Find elements whose own text matches the supplied regular expression.
     * @param regex regular expression to match text against. You can use [embedded flags](http://java.sun.com/docs/books/tutorial/essential/regex/pattern.html#embedded) (such as (?i) and (?m) to control regex options.
     * @return elements matching the supplied regular expression.
     * @see Element.ownText
     */
    fun getElementsMatchingOwnText(regex: String): Elements? {
        val pattern: Pattern
        pattern = try {
            Pattern.compile(regex)
        } catch (e: PatternSyntaxException) {
            throw IllegalArgumentException("Pattern syntax error: $regex", e)
        }
        return getElementsMatchingOwnText(pattern)
    }

    val allElements: Elements?
        /**
         * Find all elements under this element (including self, and children of children).
         *
         * @return all elements
         */
        get() = Collector.collect(Evaluator.AllElements(), this)

    /**
     * Gets the **normalized, combined text** of this element and all its children. Whitespace is normalized and
     * trimmed.
     *
     * For example, given HTML `<p>Hello  <b>there</b> now! </p>`, `p.text()` returns `"Hello there
     * now!"`
     *
     * If you do not want normalized text, use [.wholeText]. If you want just the text of this node (and not
     * children), use [.ownText]
     *
     * Note that this method returns the textual content that would be presented to a reader. The contents of data
     * nodes (such as `<script>` tags) are not considered text. Use [.data] or [.html] to retrieve
     * that content.
     *
     * @return decoded, normalized text, or empty string if none.
     * @see .wholeText
     * @see .ownText
     * @see .textNodes
     */
    fun text(): String {
        val accum = StringUtil.borrowBuilder()
        NodeTraversor.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                if (node is TextNode) {
                    appendNormalisedText(accum, node)
                } else if (node is Element) {
                    val element = node
                    if (accum.length > 0 &&
                        (element.isBlock || element.isNode("br")) &&
                        !TextNode.Companion.lastCharIsWhitespace(accum)
                    ) accum.append(' ')
                }
            }

            override fun tail(node: Node, depth: Int) {
                // make sure there is a space between block tags and immediately following text nodes or inline elements <div>One</div>Two should be "One Two".
                if (node is Element) {
                    val next = node.nextSibling()
                    if (node.isBlock && (next is TextNode || next is Element && !next.tag.formatAsBlock()) && !TextNode.lastCharIsWhitespace(
                            accum
                        )
                    ) accum.append(' ')
                }
            }
        }, this)
        return StringUtil.releaseBuilder(accum).trim { it <= ' ' }
    }

    /**
     * Get the non-normalized, decoded text of this element and its children, including only any newlines and spaces
     * present in the original source.
     * @return decoded, non-normalized text
     * @see .text
     * @see .wholeOwnText
     */
    fun wholeText(): String {
        val accum = StringUtil.borrowBuilder()
        NodeTraversor.traverse(GenericNodeVisitor { node -> appendWholeText(node, accum) }, this)
        return StringUtil.releaseBuilder(accum)
    }

    /**
     * Get the non-normalized, decoded text of this element, **not including** any child elements, including only any
     * newlines and spaces present in the original source.
     * @return decoded, non-normalized text that is a direct child of this Element
     * @see .text
     * @see .wholeText
     * @see .ownText
     * @since 1.15.1
     */
    fun wholeOwnText(): String {
        val accum = StringUtil.borrowBuilder()
        val size = childNodeSize()
        for (i in 0 until size) {
            val node = childNodes[i]
            appendWholeText(node, accum)
        }
        return StringUtil.releaseBuilder(accum)
    }

    /**
     * Gets the (normalized) text owned by this element only; does not get the combined text of all children.
     *
     *
     * For example, given HTML `<p>Hello <b>there</b> now!</p>`, `p.ownText()` returns `"Hello now!"`,
     * whereas `p.text()` returns `"Hello there now!"`.
     * Note that the text within the `b` element is not returned, as it is not a direct child of the `p` element.
     *
     * @return decoded text, or empty string if none.
     * @see .text
     * @see .textNodes
     */
    fun ownText(): String {
        val sb = StringUtil.borrowBuilder()
        ownText(sb)
        return StringUtil.releaseBuilder(sb).trim { it <= ' ' }
    }

    private fun ownText(accum: StringBuilder) {
        for (i in 0 until childNodeSize()) {
            val child = childNodes[i]
            if (child is TextNode) {
                appendNormalisedText(accum, child)
            } else if (child.isNode("br") && !TextNode.Companion.lastCharIsWhitespace(accum)) {
                accum.append(" ")
            }
        }
    }

    /**
     * Set the text of this element. Any existing contents (text or elements) will be cleared.
     *
     * As a special case, for `<script>` and `<style>` tags, the input text will be treated as data,
     * not visible text.
     * @param text decoded text
     * @return this element
     */
    open fun text(text: String): Element {
        Validate.notNull(text)
        empty()
        // special case for script/style in HTML: should be data node
        val owner = ownerDocument()
        // an alternate impl would be to run through the parser
        if (owner != null && owner.parser()
                .isContentForTagData(normalName())
        ) appendChild(DataNode(text)) else appendChild(
            TextNode(text)
        )
        return this
    }

    /**
     * Checks if the current element or any of its child elements contain non-whitespace text.
     * @return `true` if the element has non-blank text content, `false` otherwise.
     */
    fun hasText(): Boolean {
        val hasText = AtomicBoolean(false)
        filter(GenericNodeFilter { node ->
            if (node is TextNode && node.isNotBlank) {
                hasText.set(true)
                return@GenericNodeFilter NodeFilter.FilterResult.STOP
            }

            NodeFilter.FilterResult.CONTINUE
        })

        return hasText.get()
    }

    /**
     * Get the combined data of this element. Data is e.g. the inside of a `<script>` tag. Note that data is NOT the
     * text of the element. Use [.text] to get the text that would be visible to a user, and `data()`
     * for the contents of scripts, comments, CSS styles, etc.
     *
     * @return the data, or empty string if none
     *
     * @see .dataNodes
     */
    fun data(): String {
        val stringBuilder = StringUtil.borrowBuilder()

        traverse(GenericNodeVisitor { childNode ->
            when (childNode) {
                is DataNode -> stringBuilder.append(childNode.wholeData)
                is Comment -> stringBuilder.append(childNode.data)
                // this shouldn't really happen because the html parser won't see the cdata as anything special when parsing script.
                // but in case another type gets through.
                is CDataNode -> stringBuilder.append(childNode.wholeText)
            }
        })

        return StringUtil.releaseBuilder(stringBuilder)
    }

    /**
     * Gets the literal value of this element's "class" attribute, which may include multiple class names, space
     * separated. (E.g. on `<div class="header gray">` returns, "`header gray`")
     * @return The literal class attribute, or **empty string** if no class attribute set.
     */
    fun className(): String {
        return attr("class").trim()
    }

    /**
     * Get each of the element's class names. E.g. on element `<div class="header gray">`,
     * returns a set of two elements `"header", "gray"`. Note that modifications to this set are not pushed to
     * the backing `class` attribute; use the [.classNames] method to persist them.
     * @return set of classnames, empty if no class attribute
     */
    fun classNames(): MutableSet<String> {
        val names = ClassSplit.split(className())
        val classNames: MutableSet<String> = LinkedHashSet(Arrays.asList(*names))
        classNames.remove("") // if classNames() was empty, would include an empty class
        return classNames
    }

    /**
     * Set the element's `class` attribute to the supplied class names.
     * @param classNames set of classes
     * @return this element, for chaining
     */
    fun classNames(classNames: Set<String>): Element {
        Validate.notNull(classNames)
        if (classNames.isEmpty()) {
            attributes().remove("class")
        } else {
            attributes().put("class", StringUtil.join(classNames, " "))
        }
        return this
    }

    /**
     * Tests if this element has a class. Case-insensitive.
     * @param className name of class to check for
     * @return true if it does, false if not
     */
    // performance sensitive
    fun hasClass(className: String): Boolean {
        if (attributes == null) return false
        val classAttr = attributes!!.getIgnoreCase("class")
        val len = classAttr.length
        val wantLen = className.length
        if (len == 0 || len < wantLen) {
            return false
        }

        // if both lengths are equal, only need compare the className with the attribute
        if (len == wantLen) {
            return className.equals(classAttr, ignoreCase = true)
        }

        // otherwise, scan for whitespace and compare regions (with no string or arraylist allocations)
        var inClass = false
        var start = 0
        for (i in 0 until len) {
            if (Character.isWhitespace(classAttr[i])) {
                if (inClass) {
                    // white space ends a class name, compare it with the requested one, ignore case
                    if (i - start == wantLen && classAttr.regionMatches(
                            start,
                            className,
                            0,
                            wantLen,
                            ignoreCase = true
                        )
                    ) {
                        return true
                    }
                    inClass = false
                }
            } else {
                if (!inClass) {
                    // we're in a class name : keep the start of the substring
                    inClass = true
                    start = i
                }
            }
        }

        // check the last entry
        return if (inClass && len - start == wantLen) {
            classAttr.regionMatches(start, className, 0, wantLen, ignoreCase = true)
        } else false
    }

    /**
     * Add a class name to this element's `class` attribute.
     * @param className class name to add
     * @return this element
     */
    fun addClass(className: String): Element {
        Validate.notNull(className)
        val classes = classNames()
        classes.add(className)
        classNames(classes)
        return this
    }

    /**
     * Remove a class name from this element's `class` attribute.
     * @param className class name to remove
     * @return this element
     */
    fun removeClass(className: String): Element {
        Validate.notNull(className)
        val classes = classNames()
        classes.remove(className)
        classNames(classes)
        return this
    }

    /**
     * Toggle a class name on this element's `class` attribute: if present, remove it; otherwise add it.
     * @param className class name to toggle
     * @return this element
     */
    fun toggleClass(className: String): Element {
        Validate.notNull(className)
        val classes = classNames()
        if (classes.contains(className)) classes.remove(className) else classes.add(className)
        classNames(classes)
        return this
    }

    /**
     * Get the value of a form element (input, textarea, etc).
     * @return the value of the form element, or empty string if not set.
     */
    fun `val`(): String? {
        return if (normalName() == "textarea") text() else attr("value")
    }

    /**
     * Set the value of a form element (input, textarea, etc).
     * @param value value to set
     * @return this element (for chaining)
     */
    fun `val`(value: String): Element {
        if (normalName() == "textarea") text(value) else attr("value", value)
        return this
    }

    /**
     * Get the source range (start and end positions) of the end (closing) tag for this Element. Position tracking must be
     * enabled prior to parsing the content.
     * @return the range of the closing tag for this element, if it was explicitly closed in the source. `Untracked`
     * otherwise.
     * @see org.jsoup.parser.Parser.setTrackPosition
     * @see Node.sourceRange
     * @since 1.15.2
     */
    fun endSourceRange(): Range? {
        return Range.Companion.of(this, false)
    }

    fun shouldIndent(out: Document.OutputSettings?): Boolean {
        return out!!.prettyPrint() && isFormatAsBlock(out) && !isInlineable(out) && !preserveWhitespace(parentNode)
    }

    @Throws(IOException::class)
    override fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        if (shouldIndent(out)) {
            if (accum is StringBuilder) {
                if (accum.length > 0) indent(accum, depth, out)
            } else {
                indent(accum, depth, out)
            }
        }
        accum.append('<').append(tagName())
        if (attributes != null) attributes!!.html(accum, out)

        // selfclosing includes unknown tags, isEmpty defines tags that are always empty
        if (childNodes.isEmpty() && tag.isSelfClosing) {
            if (out.syntax() == Document.OutputSettings.Syntax.html && tag.isEmpty) accum.append('>') else accum.append(
                " />"
            ) // <img> in html, <img /> in xml
        } else accum.append('>')
    }

    @Throws(IOException::class)
    override fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        if (!(childNodes.isEmpty() && tag.isSelfClosing)) {
            if (out.prettyPrint() && !childNodes.isEmpty() && (tag.formatAsBlock() && !preserveWhitespace(parentNode) || out.outline() && (childNodes.size > 1 || childNodes.size == 1 && childNodes[0] is Element))) indent(
                accum, depth, out
            )
            accum.append("</").append(tagName()).append('>')
        }
    }

    /**
     * Retrieves the element's inner HTML. E.g. on a `<div>` with one empty `<p>`, would return
     * `<p></p>`. (Whereas [.outerHtml] would return `<div><p></p></div>`.)
     *
     * @return String of HTML.
     * @see .outerHtml
     */
    fun html(): String {
        val accum = StringUtil.borrowBuilder()
        html(accum)
        val html = StringUtil.releaseBuilder(accum)
        return if (NodeUtils.outputSettings(this)!!.prettyPrint()) html!!.trim { it <= ' ' } else html!!
    }

    override fun <T : Appendable?> html(appendable: T): T {
        val size = childNodes.size
        for (i in 0 until size) childNodes[i]!!.outerHtml(appendable)
        return appendable
    }

    /**
     * Set this element's inner HTML. Clears the existing HTML first.
     * @param html HTML to parse and set into this element
     * @return this element
     * @see .append
     */
    fun html(html: String?): Element {
        empty()
        append(html)
        return this
    }

    override fun clone(): Element {
        return super.clone() as Element
    }

    override fun shallowClone(): Element {
        // simpler than implementing a clone version with no child copy
        return Element(tag, baseUri(), if (attributes == null) null else attributes!!.clone())
    }

    override fun doClone(parent: Node?): Element {
        val clone = super.doClone(parent) as Element

        clone.attributes = attributes?.clone()
        clone.childNodes = NodeList(clone, childNodes.size)
        clone.childNodes.addAll(childNodes) // the children then get iterated and cloned in Node.clone

        return clone
    }

    // overrides of Node for call chaining
    override fun clearAttributes(): Element {
        if (attributes != null) {
            super.clearAttributes()
            attributes = null
        }
        return this
    }

    override fun removeAttr(attributeKey: String?): Element? {
        return super.removeAttr(attributeKey) as Element
    }

    override fun root(): Element? {
        return super.root() as Element // probably a document, but always at least an element
    }

    override fun traverse(nodeVisitor: NodeVisitor): Element? {
        return super.traverse(nodeVisitor) as Element
    }

    override fun forEachNode(action: Consumer<in Node?>): Element? {
        return super.forEachNode(action) as Element
    }

    /**
     * Perform the supplied action on this Element and each of its descendant Elements, during a depth-first traversal.
     * Elements may be inspected, changed, added, replaced, or removed.
     * @param action the function to perform on the element
     * @return this Element, for chaining
     * @see Node.forEachNode
     */
    fun forEach(action: Consumer<in Element>): Element {
        Validate.notNull(action)

        NodeTraversor.traverse(GenericNodeVisitor { node ->
            if (node is Element) {
                action.accept(node)
            }
        }, this)

        return this
    }

    override fun filter(nodeFilter: NodeFilter): Element? {
        return super.filter(nodeFilter) as Element
    }

    private class NodeList internal constructor(private val owner: Element, initialCapacity: Int) : ChangeNotifyingArrayList<Node>(initialCapacity) {
        override fun onContentsChanged() {
            owner.nodelistChanged()
        }
    }

    private fun isFormatAsBlock(out: Document.OutputSettings?): Boolean {
        return tag.isBlock || parent() != null && parent()!!.tag()!!.formatAsBlock() || out!!.outline()
    }

    private fun isInlineable(out: Document.OutputSettings?): Boolean {
        return if (!tag.isInline) {
            false
        } else {
            ((parent() == null || parent()!!.isBlock)
                    && !isEffectivelyFirst
                    && !out!!.outline()
                    && !isNode("br"))
        }
    }

    companion object {
        private val EmptyChildren: List<Element> = emptyList<Element>()
        private val ClassSplit = Pattern.compile("\\s+")
        private val BaseUriKey: String = Attributes.Companion.internalKey("baseUri")

        private fun searchUpForAttribute(start: Element, key: String): String {
            var element: Element? = start

            while (element != null) {
                element.attributes?.let { attributes ->
                    if (attributes.hasKey(key)) {
                        return attributes[key]
                    }
                }

                element = element.parent()
            }

            return ""
        }

        private fun <E : Element?> indexInList(search: Element, elements: List<E>): Int {
            val size = elements.size
            for (i in 0 until size) {
                if (elements[i] === search) return i
            }
            return 0
        }

        private fun appendWholeText(node: Node, accum: StringBuilder) {
            if (node is TextNode) {
                accum.append(node.wholeText)
            } else if (node.isNode("br")) {
                accum.append("\n")
            }
        }

        private fun appendNormalisedText(accum: StringBuilder?, textNode: TextNode) {
            val text = textNode.wholeText
            if (preserveWhitespace(textNode.parentNode) || textNode is CDataNode) accum!!.append(text) else StringUtil.appendNormalisedWhitespace(
                accum,
                text,
                TextNode.Companion.lastCharIsWhitespace(accum)
            )
        }

        fun preserveWhitespace(node: Node?): Boolean {
            // looks only at this element and five levels up, to prevent recursion & needless stack searches
            if (node is Element) {
                var el = node as Element?
                var i = 0
                do {
                    if (el!!.tag.preserveWhitespace()) return true
                    el = el.parent()
                    i++
                } while (i < 6 && el != null)
            }
            return false
        }
    }
}
