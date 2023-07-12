package net.dankito.ksoup.nodes

import net.dankito.ksoup.SerializationException
import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.select.GenericNodeVisitor
import net.dankito.ksoup.select.NodeFilter
import net.dankito.ksoup.select.NodeTraversor
import net.dankito.ksoup.select.NodeVisitor
import java.util.*

/**
 * The base, abstract Node model. Elements, Documents, Comments etc are all Node instances.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
abstract class Node
/**
 * Default constructor. Doesn't set up base uri, children, or attributes; use with caution.
 */
protected constructor() : java.lang.Cloneable {

    /**
     * Gets this node's parent node. Not overridable by extending classes, so useful if you really just need the Node type.
     * @return parent node; or null if no parent.
     */
    var parentNode : Node? = null // Nodes don't always have parents
        protected set

    /**
     * Get the list index of this node in its node sibling list. E.g. if this is the first node
     * sibling, returns 0.
     * @return position in node sibling list
     * @see net.dankito.ksoup.nodes.Element.elementSiblingIndex
     */
    var siblingIndex = 0

    /**
     * Get the node name of this node. Use for debugging purposes and not logic switching (for that, use instanceof).
     * @return node name
     */
    abstract fun nodeName(): String

    /**
     * Get the normalized name of this node. For node types other than Element, this is the same as [.nodeName].
     * For an Element, will be the lower-cased tag name.
     * @return normalized node name
     * @since 1.15.4.
     */
    open fun normalName(): String? {
        return nodeName()
    }

    /**
     * Check if this Node has an actual Attributes object.
     */
    abstract fun hasAttributes(): Boolean

    /**
     * Checks if this node has a parent. Nodes won't have parents if (e.g.) they are newly created and not added as a child
     * to an existing node, or if they are a [.shallowClone]. In such cases, [.parent] will return `null`.
     * @return if this node has a parent.
     */
    fun hasParent(): Boolean {
        return parentNode != null
    }

    /**
     * Get an attribute's value by its key. **Case insensitive**
     *
     *
     * To get an absolute URL from an attribute that may be a relative URL, prefix the key with `**abs**`,
     * which is a shortcut to the [.absUrl] method.
     *
     * E.g.:
     * <blockquote>`String url = a.attr("abs:href");`</blockquote>
     *
     * @param attributeKey The attribute key.
     * @return The attribute, or empty string if not present (to avoid nulls).
     * @see .attributes
     * @see .hasAttr
     * @see .absUrl
     */
    open fun attr(attributeKey: String): String {
        Validate.notNull(attributeKey)
        if (!hasAttributes()) return EmptyString
        val `val` = attributes().getIgnoreCase(attributeKey)
        return if (`val`.length > 0) `val` else if (attributeKey.startsWith("abs:")) absUrl(attributeKey.substring("abs:".length)) else ""
    }

    /**
     * Get each of the element's attributes.
     * @return attributes (which implements iterable, in same order as presented in original HTML).
     */
    abstract fun attributes(): Attributes

    /**
     * Get the number of attributes that this Node has.
     * @return the number of attributes
     * @since 1.14.2
     */
    fun attributesSize(): Int {
        // added so that we can test how many attributes exist without implicitly creating the Attributes object
        return if (hasAttributes()) attributes().size() else 0
    }

    /**
     * Set an attribute (key=value). If the attribute already exists, it is replaced. The attribute key comparison is
     * **case insensitive**. The key will be set with case sensitivity as set in the parser settings.
     * @param attributeKey The attribute key.
     * @param attributeValue The attribute value.
     * @return this (for chaining)
     */
    open fun attr(attributeKey: String, attributeValue: String?): Node {
        val normalizedKey = NodeUtils.parser(this).settings().normalizeAttribute(attributeKey)
        attributes().putIgnoreCase(normalizedKey, attributeValue)
        return this
    }

    /**
     * Test if this Node has an attribute. **Case insensitive**.
     * @param attributeKey The attribute key to check.
     * @return true if the attribute exists, false if not.
     */
    open fun hasAttr(attributeKey: String): Boolean {
        Validate.notNull(attributeKey)
        if (!hasAttributes()) return false
        if (attributeKey.startsWith("abs:")) {
            val key = attributeKey.substring("abs:".length)
            if (attributes().hasKeyIgnoreCase(key) && !absUrl(key).isEmpty()) return true
        }
        return attributes().hasKeyIgnoreCase(attributeKey)
    }

    /**
     * Remove an attribute from this node.
     * @param attributeKey The attribute to remove.
     * @return this (for chaining)
     */
    open fun removeAttr(attributeKey: String): Node {
        Validate.notNull(attributeKey)
        if (hasAttributes()) attributes().removeIgnoreCase(attributeKey)
        return this
    }

    /**
     * Clear (remove) each of the attributes in this node.
     * @return this, for chaining
     */
    open fun clearAttributes(): Node {
        if (hasAttributes()) {
            val it = attributes().iterator()
            while (it.hasNext()) {
                it.next()
                it.remove()
            }
        }
        return this
    }

    /**
     * Get the base URI that applies to this node. Will return an empty string if not defined. Used to make relative links
     * absolute.
     *
     * @return base URI
     * @see .absUrl
     */
    abstract fun baseUri(): String

    /**
     * Set the baseUri for just this node (not its descendants), if this Node tracks base URIs.
     * @param baseUri new URI
     */
    protected abstract fun doSetBaseUri(baseUri: String)

    /**
     * Update the base URI of this node and all of its descendants.
     * @param baseUri base URI to set
     */
    fun setBaseUri(baseUri: String) {
        Validate.notNull(baseUri)
        doSetBaseUri(baseUri)
    }

    /**
     * Get an absolute URL from a URL attribute that may be relative (such as an `<a href>` or
     * `<img src>`).
     *
     *
     * E.g.: `String absUrl = linkEl.absUrl("href");`
     *
     *
     *
     * If the attribute value is already absolute (i.e. it starts with a protocol, like
     * `http://` or `https://` etc), and it successfully parses as a URL, the attribute is
     * returned directly. Otherwise, it is treated as a URL relative to the element's [.baseUri], and made
     * absolute using that.
     *
     *
     *
     * As an alternate, you can use the [.attr] method with the `abs:` prefix, e.g.:
     * `String absUrl = linkEl.attr("abs:href");`
     *
     *
     * @param attributeKey The attribute key
     * @return An absolute URL if one could be made, or an empty string (not null) if the attribute was missing or
     * could not be made successfully into a URL.
     * @see .attr
     *
     * @see java.net.URL.URL
     */
    open fun absUrl(attributeKey: String): String {
        Validate.notEmpty(attributeKey)
        return if (!(hasAttributes() && attributes().hasKeyIgnoreCase(attributeKey))) {
            ""
        } else {
            StringUtil.resolve(baseUri(), attributes().getIgnoreCase(attributeKey))
        }
    }

    internal abstract fun ensureChildNodes(): MutableList<Node>

    /**
     * Get a child node by its 0-based index.
     * @param index index of child node
     * @return the child node at this index. Throws a `IndexOutOfBoundsException` if the index is out of bounds.
     */
    fun childNode(index: Int): Node {
        return ensureChildNodes()[index]
    }

    /**
     * Get this node's children. Presented as an unmodifiable list: new children can not be added, but the child nodes
     * themselves can be manipulated.
     * @return list of children. If no children, returns an empty list.
     */
    fun childNodes(): List<Node> {
        if (childNodeSize() == 0) return EmptyNodes
        val children = ensureChildNodes()

        val rewrap = ArrayList(children) // wrapped so that looping and moving will not throw a CME as the source changes
        return Collections.unmodifiableList(rewrap)
    }

    /**
     * Returns a deep copy of this node's children. Changes made to these nodes will not be reflected in the original
     * nodes
     * @return a deep copy of this node's children
     */
    fun childNodesCopy(): List<Node> {
        val nodes = ensureChildNodes()
        val children = ArrayList<Node>(nodes.size)
        for (node in nodes) {
            children.add(node.clone())
        }
        return children
    }

    /**
     * Get the number of child nodes that this node holds.
     * @return the number of child nodes that this node holds.
     */
    abstract fun childNodeSize(): Int

    protected fun childNodesAsArray(): Array<Node> {
        return ensureChildNodes().toTypedArray()
    }

    /**
     * Delete all this node's children.
     * @return this node, for chaining
     */
    abstract fun empty(): Node

    /**
     * Gets this node's parent node.
     * @return parent node; or null if no parent.
     * @see .hasParent
     */
    open fun parent(): Node? {
        return parentNode
    }

    /**
     * Get this node's root node; that is, its topmost ancestor. If this node is the top ancestor, returns `this`.
     * @return topmost ancestor.
     */
    open fun root(): Node? {
        var node: Node? = this
        while (node!!.parentNode != null) node = node.parentNode
        return node
    }

    /**
     * Gets the Document associated with this Node.
     * @return the Document associated with this Node, or null if there is no such Document.
     */
    fun ownerDocument(): Document? {
        val root = root()
        return if (root is Document) root else null
    }

    /**
     * Remove (delete) this node from the DOM tree. If this node has children, they are also removed.
     */
    fun remove() {
        parentNode?.removeChild(this)
    }

    /**
     * Insert the specified HTML into the DOM before this node (as a preceding sibling).
     * @param html HTML to add before this node
     * @return this node, for chaining
     * @see .after
     */
    open fun before(html: String): Node? {
        addSiblingHtml(siblingIndex, html)
        return this
    }

    /**
     * Insert the specified node into the DOM before this node (as a preceding sibling).
     * @param node to add before this node
     * @return this node, for chaining
     * @see .after
     */
    open fun before(node: Node): Node {
        Validate.notNull(node)
        Validate.notNull(parentNode)

        // if the incoming node is a sibling of this, remove it first so siblingIndex is correct on add
        if (node.parentNode === parentNode) node.remove()
        parentNode?.addChildren(siblingIndex, node)
        return this
    }

    /**
     * Insert the specified HTML into the DOM after this node (as a following sibling).
     * @param html HTML to add after this node
     * @return this node, for chaining
     * @see .before
     */
    open fun after(html: String): Node {
        addSiblingHtml(siblingIndex + 1, html)
        return this
    }

    /**
     * Insert the specified node into the DOM after this node (as a following sibling).
     * @param node to add after this node
     * @return this node, for chaining
     * @see .before
     */
    open fun after(node: Node): Node {
        Validate.notNull(node)
        Validate.notNull(parentNode)

        // if the incoming node is a sibling of this, remove it first so siblingIndex is correct on add
        if (node.parentNode === parentNode) node.remove()
        parentNode!!.addChildren(siblingIndex + 1, node)
        return this
    }

    private fun addSiblingHtml(index: Int, html: String) {
        Validate.notNull(html)
        val parentNode = this.parentNode
        Validate.notNull(parentNode)

        baseUri()?.let { baseUri ->
            val context = if (parent() is Element) parent() as Element? else null
            val nodes = NodeUtils.parser(this).parseFragmentInput(html, context, baseUri)
            parentNode.addChildren(index, *nodes.toTypedArray())
        }
    }

    /**
     * Wrap the supplied HTML around this node.
     *
     * @param html HTML to wrap around this node, e.g. `<div class="head"></div>`. Can be arbitrarily deep. If
     * the input HTML does not parse to a result starting with an Element, this will be a no-op.
     * @return this node, for chaining.
     */
    open fun wrap(html: String): Node {
        Validate.notEmpty(html)

        baseUri()?.let { baseUri ->
            // Parse context - parent (because wrapping), this, or null
            val context = if (parentNode != null && parentNode is Element) parentNode as Element else (if (this is Element) this else null)!!
            val wrapChildren = NodeUtils.parser(this).parseFragmentInput(html, context, baseUri)
            val wrapNode = wrapChildren[0] as? Element // nothing to wrap with; noop
                ?: return this
            val wrap = wrapNode
            val deepest = getDeepChild(wrap)
            parentNode?.replaceChild(this, wrap)
            deepest.addChildren(this) // side effect of tricking wrapChildren to lose first

            // remainder (unbalanced wrap, like <div></div><p></p> -- The <p> is remainder
            if (wrapChildren.size > 0) {
                for (i in wrapChildren.indices) {
                    val remainder = wrapChildren[i]
                    // if no parent, this could be the wrap node, so skip
                    if (wrap === remainder) continue
                    remainder.parentNode?.removeChild(remainder)
                    wrap.after(remainder)
                }
            }
        }

        return this
    }

    /**
     * Removes this node from the DOM, and moves its children up into the node's parent. This has the effect of dropping
     * the node but keeping its children.
     *
     *
     * For example, with the input html:
     *
     *
     * `<div>One <span>Two <b>Three</b></span></div>`
     * Calling `element.unwrap()` on the `span` element will result in the html:
     *
     * `<div>One Two <b>Three</b></div>`
     * and the `"Two "` [TextNode] being returned.
     *
     * @return the first child of this node, after the node has been unwrapped. @{code Null} if the node had no children.
     * @see .remove
     * @see .wrap
     */
    fun unwrap(): Node? {
        Validate.notNull(parentNode)
        val childNodes: List<Node> = ensureChildNodes()
        val firstChild = if (childNodes.size > 0) childNodes[0] else null
        parentNode!!.addChildren(siblingIndex, *childNodesAsArray())
        this.remove()
        return firstChild
    }

    private fun getDeepChild(element: Element): Element {
//        val child = element.firstElementChild()
//        return if (child != null) getDeepChild(child) else element

        var match = element
        var child = element.firstElementChild()

        while (child != null) {
            match = child
            child = child.firstElementChild()
        }

        return match
    }

    open fun nodelistChanged() {
        // Element overrides this to clear its shadow children elements
    }

    /**
     * Replace this node in the DOM with the supplied node.
     * @param in the node that will replace the existing node.
     */
    fun replaceWith(`in`: Node) {
        Validate.notNull(`in`)
        Validate.notNull(parentNode)
        parentNode!!.replaceChild(this, `in`)
    }

    protected fun replaceChild(out: Node, `in`: Node) {
        Validate.isTrue(out.parentNode === this)
        Validate.notNull(`in`)
        if (out === `in`) return  // no-op self replacement

        if (`in`.parentNode != null) `in`.parentNode!!.removeChild(`in`)
        val index = out.siblingIndex
        ensureChildNodes()[index] = `in`
        `in`.parentNode = this
        `in`.siblingIndex = index
        out.parentNode = null
    }

    open fun removeChild(out: Node) {
        Validate.isTrue(out.parentNode === this)
        val index = out.siblingIndex
        ensureChildNodes().removeAt(index)
        reindexChildren(index)
        out.parentNode = null
    }

    fun addChildren(vararg children: Node) {
        //most used. short circuit addChildren(int), which hits reindex children and array copy
        val nodes = ensureChildNodes()
        for (child in children) {
            reparentChild(child)
            nodes.add(child)
            child.siblingIndex = nodes.size - 1
        }
    }

    fun addChildren(index: Int, vararg children: Node) {
        Validate.notNull(children)
        if (children.size == 0) {
            return
        }
        val nodes = ensureChildNodes()

        // fast path - if used as a wrap (index=0, children = child[0].parent.children - do inplace
        val firstParent = children[0].parent()
        if (firstParent != null && firstParent.childNodeSize() == children.size) {
            var sameList = true
            val firstParentNodes = firstParent.ensureChildNodes()
            // identity check contents to see if same
            var i = children.size
            while (i-- > 0) {
                if (children[i] !== firstParentNodes[i]) {
                    sameList = false
                    break
                }
            }
            if (sameList) { // moving, so OK to empty firstParent and short-circuit
                val wasEmpty = childNodeSize() == 0
                firstParent.empty()
                nodes.addAll(index, children.toList())
                i = children.size
                while (i-- > 0) {
                    children[i].parentNode = this
                }
                if (!(wasEmpty && children[0].siblingIndex == 0)) // skip reindexing if we just moved
                    reindexChildren(index)
                return
            }
        }
        Validate.noNullElements(children)
        for (child in children) {
            reparentChild(child)
        }
        nodes.addAll(index, children.toList())
        reindexChildren(index)
    }

    protected fun reparentChild(child: Node) {
        Validate.notNull(child)
        child.parentNode?.removeChild(child)
        child.parentNode = this
    }

    private fun reindexChildren(start: Int) {
        val size = childNodeSize()
        if (size == 0) return

        val childNodes = ensureChildNodes()
        for (i in start until size) {
            childNodes[i].siblingIndex = i
        }
    }

    /**
     * Retrieves this node's sibling nodes. Similar to [node.parent.childNodes()][.childNodes], but does not
     * include this node (a node is not a sibling of itself).
     * @return node siblings. If the node has no parent, returns an empty list.
     */
    fun siblingNodes(): List<Node> {
        val parent = parentNode
        if (parent == null) {
            return emptyList()
        }

        val nodes = parent.ensureChildNodes()
        val siblings = ArrayList<Node>(nodes.size - 1)
        for (node in nodes) {
            if (node !== this) {
                siblings.add(node)
            }
        }
        return siblings
    }

    /**
     * Get this node's next sibling.
     * @return next sibling, or @{code null} if this is the last sibling
     */
    fun nextSibling(): Node? {
        if (parentNode == null) return null // root
        val siblings = parentNode!!.ensureChildNodes()
        val index = siblingIndex + 1
        return if (siblings.size > index) siblings[index] else null
    }

    /**
     * Get this node's previous sibling.
     * @return the previous sibling, or @{code null} if this is the first sibling
     */
    fun previousSibling(): Node? {
        if (parentNode == null) return null // root
        return if (siblingIndex > 0) parentNode!!.ensureChildNodes()[siblingIndex - 1] else null
    }

    /**
     * Gets the first child node of this node, or `null` if there is none. This could be any Node type, such as an
     * Element, TextNode, Comment, etc. Use [Element.firstElementChild] to get the first Element child.
     * @return the first child node, or null if there are no children.
     * @see Element.firstElementChild
     * @see .lastChild
     * @since 1.15.2
     */
    fun firstChild(): Node? {
        return if (childNodeSize() == 0) null else ensureChildNodes()[0]
    }

    /**
     * Gets the last child node of this node, or `null` if there is none.
     * @return the last child node, or null if there are no children.
     * @see Element.lastElementChild
     * @see .firstChild
     * @since 1.15.2
     */
    fun lastChild(): Node? {
        val size = childNodeSize()
        if (size == 0) return null
        val children = ensureChildNodes()
        return children[size - 1]
    }

    /**
     * Perform a depth-first traversal through this node and its descendants.
     * @param nodeVisitor the visitor callbacks to perform on each node
     * @return this node, for chaining
     */
    open fun traverse(nodeVisitor: NodeVisitor): Node {
        Validate.notNull(nodeVisitor)
        NodeTraversor.traverse(nodeVisitor, this)
        return this
    }

    /**
     * Perform the supplied action on this Node and each of its descendants, during a depth-first traversal. Nodes may be
     * inspected, changed, added, replaced, or removed.
     * @param action the function to perform on the node
     * @return this Node, for chaining
     * @see Element.forEach
     */
    open fun forEachNode(action: (Node) -> Unit): Node {
        Validate.notNull(action)
        NodeTraversor.traverse(GenericNodeVisitor(action), this)
        return this
    }

    /**
     * Perform a depth-first filtering through this node and its descendants.
     * @param nodeFilter the filter callbacks to perform on each node
     * @return this node, for chaining
     */
    open fun filter(nodeFilter: NodeFilter): Node {
        Validate.notNull(nodeFilter)
        NodeTraversor.filter(nodeFilter, this)
        return this
    }

    /**
     * Get the outer HTML of this node. For example, on a `p` element, may return `<p>Para</p>`.
     * @return outer HTML
     * @see Element.html
     * @see Element.text
     */
    open fun outerHtml(): String {
        val accum = StringUtil.borrowBuilder()
        outerHtml(accum)
        return StringUtil.releaseBuilder(accum)
    }

    fun outerHtml(accum: Appendable) {
        NodeTraversor.traverse(OuterHtmlVisitor(accum, NodeUtils.outputSettings(this)), this)
    }

    /**
     * Get the outer HTML of this node.
     * @param accum accumulator to place HTML into
     * @throws net.dankito.ksoup.jvm.IOException if appending to the given accumulator fails.
     */
    abstract fun outerHtmlHead(accum: Appendable, depth: Int, out: Document.OutputSettings)

    abstract fun outerHtmlTail(accum: Appendable, depth: Int, out: Document.OutputSettings)

    /**
     * Write this node and its children to the given [Appendable].
     *
     * @param appendable the [Appendable] to write to.
     * @return the supplied [Appendable], for chaining.
     */
    open fun <T : Appendable> html(appendable: T): T {
        outerHtml(appendable)
        return appendable
    }

    /**
     * Get the source range (start and end positions) in the original input source that this node was parsed from. Position
     * tracking must be enabled prior to parsing the content. For an Element, this will be the positions of the start tag.
     * @return the range for the start of the node.
     * @see net.dankito.ksoup.parser.Parser.setTrackPosition
     * @see Element.endSourceRange
     * @since 1.15.2
     */
    open fun sourceRange(): Range {
        return Range.of(this, true)
    }

    /** Test if this node is not null and has the supplied normal name.  */
    open fun isNode(node: Node?, normalName: String): Boolean {
        return node != null && node.normalName() == normalName
    }

    /** Test if this node has the supplied normal name.  */
    fun isNode(normalName: String): Boolean {
        return normalName() == normalName
    }

    /** Test if this node is the first child, or first following blank text.  */
    val isEffectivelyFirst: Boolean
        get() {
            if (siblingIndex == 0) return true
            if (siblingIndex == 1) {
                val prev = previousSibling()
                return prev is TextNode && prev.isBlank
            }
            return false
        }

    /**
     * Gets this node's outer HTML.
     * @return outer HTML.
     * @see .outerHtml
     */
    override fun toString(): String {
        return outerHtml()
    }

    protected fun indent(accum: Appendable, depth: Int, out: Document.OutputSettings) {
        accum.append('\n').append(StringUtil.padding(depth * out.indentAmount(), out.maxPaddingWidth()))
    }

    /**
     * Check if this node is has the same content as another node. A node is considered the same if its name, attributes and content match the
     * other node; particularly its position in the tree does not influence its similarity.
     * @param other other object to compare to
     * @return true if the content of this node is the same as the other
     */
    fun hasSameValue(other: Any?): Boolean {
        if (this === other) return true
        return if (other == null || this::class != other::class) {
            false
        } else {
            this.outerHtml() == (other as Node).outerHtml()
        }
    }

    /**
     * Create a stand-alone, deep copy of this node, and all of its children. The cloned node will have no siblings or
     * parent node. As a stand-alone object, any changes made to the clone or any of its children will not impact the
     * original node.
     *
     *
     * The cloned node may be adopted into another Document or node structure using [Element.appendChild].
     * @return a stand-alone cloned node, including clones of any children
     * @see .shallowClone
     */
    open fun clone(): Node {
        val thisClone = doClone(null) // splits for orphan

        // Queue up nodes that need their children cloned (BFS).
        val nodesToProcess = ArrayList<Node>()
        nodesToProcess.add(thisClone)

        while (!nodesToProcess.isEmpty()) {
            val currParent = nodesToProcess.removeFirst()
            val size = currParent.childNodeSize()
            for (i in 0 until size) {
                val childNodes = currParent.ensureChildNodes()
                val childClone = childNodes[i].doClone(currParent)
                childNodes[i] = childClone
                nodesToProcess.add(childClone)
            }
        }

        return thisClone
    }

    /**
     * Create a stand-alone, shallow copy of this node. None of its children (if any) will be cloned, and it will have
     * no parent or sibling nodes.
     * @return a single independent copy of this node
     * @see .clone
     */
    open fun shallowClone(): Node? {
        return doClone(null)
    }

    /*
       * Return a clone of the node using the given parent (which can be null).
       * Not a deep copy of children.
       */
    protected open fun doClone(parent: Node?): Node {
//        val clone = clone()
        val clone = createInstanceForClone()

        clone.parentNode = parent // can be null, to create an orphan split
        clone.siblingIndex = if (parent == null) 0 else siblingIndex
        // if not keeping the parent, shallowClone the ownerDocument to preserve its settings
        if (parent == null && this !is Document) {
            val doc = ownerDocument()
            if (doc != null) {
                val docClone = doc.shallowClone()
                clone.parentNode = docClone
                docClone.ensureChildNodes().add(clone)
            }
        }
        return clone
    }

    protected open fun createInstanceForClone(): Node =
        this::class.java.getDeclaredConstructor().newInstance()

    private class OuterHtmlVisitor internal constructor(private val accum: Appendable, private val out: Document.OutputSettings) : NodeVisitor {
        init {
            out.prepareEncoder()
        }

        override fun head(node: Node, depth: Int) {
            try {
                node.outerHtmlHead(accum, depth, out)
            } catch (exception: Exception) {
                throw SerializationException(exception)
            }
        }

        override fun tail(node: Node, depth: Int) {
            if (node.nodeName() != "#text") { // saves a void hit.
                try {
                    node.outerHtmlTail(accum, depth, out)
                } catch (exception: Exception) {
                    throw SerializationException(exception)
                }
            }
        }
    }

    companion object {
        val EmptyNodes = mutableListOf<Node>() // TODO: make immutable
        const val EmptyString = ""

        /** Test if this node is not null and has the supplied normal name.  */
//        @JvmStatic
//        fun isNode(node: Node?, normalName: String): Boolean {
//            return node != null && node.normalName() == normalName
//        }
    }
}