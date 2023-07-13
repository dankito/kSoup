package net.dankito.ksoup.select

import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.*

/**
 * A list of [Element]s, with methods that act on every element in the list.
 *
 *
 * To get an `Elements` object, use the [Element.select] method.
 *
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class Elements : ArrayList<Element> {
    constructor()
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(elements: Collection<Element>) : super(elements)
    constructor(elements: List<Element>) : super(elements)
    constructor(vararg elements: Element) : super(elements.asList())

    /**
     * Creates a deep copy of these elements.
     * @return a deep copy
     */
    override fun clone(): Elements {
        val clone = Elements(size)
        for (e: Element in this) clone.add(e.clone())
        return clone
    }

    // attribute methods
    /**
     * Get an attribute value from the first matched element that has the attribute.
     * @param attributeKey The attribute key.
     * @return The attribute value from the first matched element that has the attribute.. If no elements were matched (isEmpty() == true),
     * or if the no elements have the attribute, returns empty string.
     * @see .hasAttr
     */
    fun attr(attributeKey: String): String? {
        for (element: Element in this) {
            if (element.hasAttr(attributeKey)) return element.attr(attributeKey)
        }
        return ""
    }

    /**
     * Checks if any of the matched elements have this attribute defined.
     * @param attributeKey attribute key
     * @return true if any of the elements have the attribute; false if none do.
     */
    fun hasAttr(attributeKey: String): Boolean {
        for (element: Element in this) {
            if (element.hasAttr(attributeKey)) return true
        }
        return false
    }

    /**
     * Get the attribute value for each of the matched elements. If an element does not have this attribute, no value is
     * included in the result set for that element.
     * @param attributeKey the attribute name to return values for. You can add the `abs:` prefix to the key to
     * get absolute URLs from relative URLs, e.g.: `doc.select("a").eachAttr("abs:href")` .
     * @return a list of each element's attribute value for the attribute
     */
    fun eachAttr(attributeKey: String): List<String> {
        val attrs = ArrayList<String>(size)
        for (element: Element in this) {
            if (element.hasAttr(attributeKey)) attrs.add(element.attr(attributeKey))
        }
        return attrs
    }

    /**
     * Set an attribute on all matched elements.
     * @param attributeKey attribute key
     * @param attributeValue attribute value
     * @return this
     */
    fun attr(attributeKey: String, attributeValue: String?): Elements {
        for (element: Element in this) {
            element.attr(attributeKey, attributeValue)
        }
        return this
    }

    /**
     * Remove an attribute from every matched element.
     * @param attributeKey The attribute to remove.
     * @return this (for chaining)
     */
    fun removeAttr(attributeKey: String): Elements {
        for (element: Element in this) {
            element.removeAttr(attributeKey)
        }
        return this
    }

    /**
     * Add the class name to every matched element's `class` attribute.
     * @param className class name to add
     * @return this
     */
    fun addClass(className: String): Elements {
        for (element: Element in this) {
            element.addClass(className)
        }
        return this
    }

    /**
     * Remove the class name from every matched element's `class` attribute, if present.
     * @param className class name to remove
     * @return this
     */
    fun removeClass(className: String): Elements {
        for (element: Element in this) {
            element.removeClass(className)
        }
        return this
    }

    /**
     * Toggle the class name on every matched element's `class` attribute.
     * @param className class name to add if missing, or remove if present, from every element.
     * @return this
     */
    fun toggleClass(className: String): Elements {
        for (element: Element in this) {
            element.toggleClass(className)
        }
        return this
    }

    /**
     * Determine if any of the matched elements have this class name set in their `class` attribute.
     * @param className class name to check for
     * @return true if any do, false if none do
     */
    fun hasClass(className: String): Boolean {
        for (element: Element in this) {
            if (element.hasClass(className)) return true
        }
        return false
    }

    /**
     * Get the form element's value of the first matched element.
     * @return The form element's value, or empty if not set.
     * @see Element.val
     */
    fun value(): String {
        return first()?.value() ?: ""
    }

    /**
     * Set the form element's value in each of the matched elements.
     * @param value The value to set into each matched element
     * @return this (for chaining)
     */
    fun value(value: String): Elements {
        for (element: Element in this) element.value(value)
        return this
    }

    /**
     * Get the combined text of all the matched elements.
     *
     *
     * Note that it is possible to get repeats if the matched elements contain both parent elements and their own
     * children, as the Element.text() method returns the combined text of a parent and all its children.
     * @return string of all text: unescaped and no HTML.
     * @see Element.text
     * @see .eachText
     */
    fun text(): String {
        val sb = StringUtil.borrowBuilder()
        for (element in this) {
            if (sb.isNotEmpty()) sb.append(" ")
            sb.append(element.text())
        }
        return StringUtil.releaseBuilder(sb)
    }

    /**
     * Test if any matched Element has any text content, that is not just whitespace.
     * @return true if any element has non-blank text content.
     * @see Element.hasText
     */
    fun hasText(): Boolean {
        for (element: Element in this) {
            if (element.hasText()) return true
        }
        return false
    }

    /**
     * Get the text content of each of the matched elements. If an element has no text, then it is not included in the
     * result.
     * @return A list of each matched element's text content.
     * @see Element.text
     * @see Element.hasText
     * @see .text
     */
    fun eachText(): List<String> {
        val texts = ArrayList<String>(size)
        for (el: Element in this) {
            if (el.hasText()) texts.add(el.text())
        }
        return texts
    }

    /**
     * Get the combined inner HTML of all matched elements.
     * @return string of all element's inner HTML.
     * @see .text
     * @see .outerHtml
     */
    fun html(): String {
        val sb = StringUtil.borrowBuilder()

        for (element in this) {
            if (sb.isNotEmpty()) {
                sb.append("\n")
            }
            sb.append(element.html())
        }

        return StringUtil.releaseBuilder(sb)
    }

    /**
     * Get the combined outer HTML of all matched elements.
     * @return string of all element's outer HTML.
     * @see .text
     * @see .html
     */
    fun outerHtml(): String {
        val sb = StringUtil.borrowBuilder()

        for (element in this) {
            if (sb.isNotEmpty()) {
                sb.append("\n")
            }
            sb.append(element.outerHtml())
        }

        return StringUtil.releaseBuilder(sb)
    }

    /**
     * Get the combined outer HTML of all matched elements. Alias of [.outerHtml].
     * @return string of all element's outer HTML.
     * @see .text
     * @see .html
     */
    override fun toString(): String {
        return outerHtml()
    }

    /**
     * Update (rename) the tag name of each matched element. For example, to change each `<i>` to a `<em>`, do
     * `doc.select("i").tagName("em");`
     *
     * @param tagName the new tag name
     * @return this, for chaining
     * @see Element.tagName
     */
    fun tagName(tagName: String): Elements {
        for (element in this) {
            element.tagName(tagName)
        }
        return this
    }

    /**
     * Set the inner HTML of each matched element.
     * @param html HTML to parse and set into each matched element.
     * @return this, for chaining
     * @see Element.html
     */
    fun html(html: String): Elements {
        for (element: Element in this) {
            element.html(html)
        }
        return this
    }

    /**
     * Add the supplied HTML to the start of each matched element's inner HTML.
     * @param html HTML to add inside each element, before the existing HTML
     * @return this, for chaining
     * @see Element.prepend
     */
    fun prepend(html: String): Elements {
        for (element: Element in this) {
            element.prepend(html)
        }
        return this
    }

    /**
     * Add the supplied HTML to the end of each matched element's inner HTML.
     * @param html HTML to add inside each element, after the existing HTML
     * @return this, for chaining
     * @see Element.append
     */
    fun append(html: String): Elements {
        for (element: Element in this) {
            element.append(html)
        }
        return this
    }

    /**
     * Insert the supplied HTML before each matched element's outer HTML.
     * @param html HTML to insert before each element
     * @return this, for chaining
     * @see Element.before
     */
    fun before(html: String): Elements {
        for (element: Element in this) {
            element.before(html)
        }
        return this
    }

    /**
     * Insert the supplied HTML after each matched element's outer HTML.
     * @param html HTML to insert after each element
     * @return this, for chaining
     * @see Element.after
     */
    fun after(html: String): Elements {
        for (element: Element in this) {
            element.after(html)
        }
        return this
    }

    /**
     * Wrap the supplied HTML around each matched elements. For example, with HTML
     * `<p><b>This</b> is <b>Jsoup</b></p>`,
     * `doc.select("b").wrap("<i></i>");`
     * becomes `<p><i><b>This</b></i> is <i><b>jsoup</b></i></p>`
     * @param html HTML to wrap around each element, e.g. `<div class="head"></div>`. Can be arbitrarily deep.
     * @return this (for chaining)
     * @see Element.wrap
     */
    fun wrap(html: String): Elements {
        Validate.notEmpty(html)
        for (element: Element in this) {
            element.wrap(html)
        }
        return this
    }

    /**
     * Removes the matched elements from the DOM, and moves their children up into their parents. This has the effect of
     * dropping the elements but keeping their children.
     *
     *
     * This is useful for e.g removing unwanted formatting elements but keeping their contents.
     *
     *
     * E.g. with HTML:
     *
     *`<div><font>One</font> <font><a href="/">Two</a></font></div>`
     *
     * `doc.select("font").unwrap();`
     *
     * HTML = `<div>One <a href="/">Two</a></div>`
     *
     * @return this (for chaining)
     * @see Node.unwrap
     */
    fun unwrap(): Elements {
        for (element: Element in this) {
            element.unwrap()
        }
        return this
    }

    /**
     * Empty (remove all child nodes from) each matched element. This is similar to setting the inner HTML of each
     * element to nothing.
     *
     *
     * E.g. HTML: `<div><p>Hello <b>there</b></p> <p>now</p></div>`<br></br>
     * `doc.select("p").empty();`<br></br>
     * HTML = `<div><p></p> <p></p></div>`
     * @return this, for chaining
     * @see Element.empty
     * @see .remove
     */
    fun empty(): Elements {
        for (element: Element in this) {
            element.empty()
        }
        return this
    }

    /**
     * Remove each matched element from the DOM. This is similar to setting the outer HTML of each element to nothing.
     *
     *
     * E.g. HTML: `<div><p>Hello</p> <p>there</p> <img /></div>`<br></br>
     * `doc.select("p").remove();`<br></br>
     * HTML = `<div> <img /></div>`
     *
     *
     * Note that this method should not be used to clean user-submitted HTML; rather, use [net.dankito.ksoup.safety.Cleaner] to clean HTML.
     * @return this, for chaining
     * @see Element.empty
     * @see .empty
     */
    fun remove(): Elements {
        for (element: Element in this) {
            element.remove()
        }
        return this
    }

    // filters
    /**
     * Find matching elements within this element list.
     * @param query A [Selector] query
     * @return the filtered list of elements, or an empty list if none match.
     */
    fun select(query: String): Elements {
        return Selector.select(query, this)
    }

    /**
     * Remove elements from this list that match the [Selector] query.
     *
     *
     * E.g. HTML: `<div class=logo>One</div> <div>Two</div>`<br></br>
     * `Elements divs = doc.select("div").not(".logo");`<br></br>
     * Result: `divs: [<div>Two</div>]`
     *
     *
     * @param query the selector query whose results should be removed from these elements
     * @return a new elements list that contains only the filtered results
     */
    fun not(query: String): Elements {
        val out: Elements = Selector.select(query, this)
        return Selector.filterOut(this, out)
    }

    /**
     * Get the *nth* matched element as an Elements object.
     *
     *
     * See also [.get] to retrieve an Element.
     * @param index the (zero-based) index of the element in the list to retain
     * @return Elements containing only the specified element, or, if that element did not exist, an empty list.
     */
    fun eq(index: Int): Elements {
        return if (size > index) Elements(get(index)) else Elements()
    }

    /**
     * Test if any of the matched elements match the supplied query.
     * @param query A selector
     * @return true if at least one element in the list matches the query.
     */
    fun `is`(query: String): Boolean {
        val eval = QueryParser.parse(query)
        for (e: Element in this) {
            if (e.`is`(eval)) return true
        }
        return false
    }

    /**
     * Get the immediate next element sibling of each element in this list.
     * @return next element siblings.
     */
    operator fun next(): Elements {
        return siblings(null, true, false)
    }

    /**
     * Get the immediate next element sibling of each element in this list, filtered by the query.
     * @param query CSS query to match siblings against
     * @return next element siblings.
     */
    fun next(query: String): Elements {
        return siblings(query, true, false)
    }

    /**
     * Get each of the following element siblings of each element in this list.
     * @return all following element siblings.
     */
    fun nextAll(): Elements {
        return siblings(null, true, true)
    }

    /**
     * Get each of the following element siblings of each element in this list, that match the query.
     * @param query CSS query to match siblings against
     * @return all following element siblings.
     */
    fun nextAll(query: String): Elements {
        return siblings(query, true, true)
    }

    /**
     * Get the immediate previous element sibling of each element in this list.
     * @return previous element siblings.
     */
    fun prev(): Elements {
        return siblings(null, false, false)
    }

    /**
     * Get the immediate previous element sibling of each element in this list, filtered by the query.
     * @param query CSS query to match siblings against
     * @return previous element siblings.
     */
    fun prev(query: String): Elements {
        return siblings(query, false, false)
    }

    /**
     * Get each of the previous element siblings of each element in this list.
     * @return all previous element siblings.
     */
    fun prevAll(): Elements {
        return siblings(null, false, true)
    }

    /**
     * Get each of the previous element siblings of each element in this list, that match the query.
     * @param query CSS query to match siblings against
     * @return all previous element siblings.
     */
    fun prevAll(query: String): Elements {
        return siblings(query, false, true)
    }

    private fun siblings(query: String?, next: Boolean, all: Boolean): Elements {
        val els = Elements()
        val eval: Evaluator? = if (query != null) QueryParser.parse(query) else null
        for (e in this) {
            var iterator = e
            do {
                val sib = (if (next) iterator.nextElementSibling() else iterator.previousElementSibling()) ?: break
                if (eval == null) els.add(sib) else if (sib.`is`(eval)) els.add(sib)
                iterator = sib
            } while (all)
        }
        return els
    }

    /**
     * Get all of the parents and ancestor elements of the matched elements.
     * @return all of the parents and ancestor elements of the matched elements
     */
    fun parents(): Elements {
        val combo: HashSet<Element> = LinkedHashSet()
        for (e: Element in this) {
            combo.addAll(e.parents())
        }
        return Elements(combo)
    }

    // list-like methods
    /**
     * Get the first matched element.
     * @return The first matched element, or `null` if contents is empty.
     */
    fun first(): Element? {
        return if (isEmpty()) null else get(0)
    }

    /**
     * Get the last matched element.
     * @return The last matched element, or `null` if contents is empty.
     */
    fun last(): Element? {
        return if (isEmpty()) null else get(size - 1)
    }

    /**
     * Perform a depth-first traversal on each of the selected elements.
     * @param nodeVisitor the visitor callbacks to perform on each node
     * @return this, for chaining
     */
    fun traverse(nodeVisitor: NodeVisitor): Elements {
        NodeTraversor.traverse(nodeVisitor, this)
        return this
    }

    /**
     * Perform a depth-first filtering on each of the selected elements.
     * @param nodeFilter the filter callbacks to perform on each node
     * @return this, for chaining
     */
    fun filter(nodeFilter: NodeFilter): Elements {
        NodeTraversor.filter(nodeFilter, this)
        return this
    }

    /**
     * Get the [FormElement] forms from the selected elements, if any.
     * @return a list of [FormElement]s pulled from the matched elements. The list will be empty if the elements contain
     * no forms.
     */
    fun forms(): List<FormElement> {
        return this.filterIsInstance<FormElement>()
    }

    /**
     * Get [Comment] nodes that are direct child nodes of the selected elements.
     * @return Comment nodes, or an empty list if none.
     */
    fun comments(): List<Comment> {
        return childNodesOfType(Comment::class.java)
    }

    /**
     * Get [TextNode] nodes that are direct child nodes of the selected elements.
     * @return TextNode nodes, or an empty list if none.
     */
    fun textNodes(): List<TextNode> {
        return childNodesOfType(TextNode::class.java)
    }

    /**
     * Get [DataNode] nodes that are direct child nodes of the selected elements. DataNode nodes contain the
     * content of tags such as `script`, `style` etc and are distinct from [TextNode]s.
     * @return Comment nodes, or an empty list if none.
     */
    fun dataNodes(): List<DataNode> {
        return childNodesOfType(DataNode::class.java)
    }

    private fun <T : Node> childNodesOfType(tClass: Class<T>): List<T> {
        return this.flatMap {
            it.childNodes().filter { tClass.isInstance(it) }
        } as List<T>

//        val nodes = ArrayList<T>()
//        for (el in this) {
//            for (i in 0 until el.childNodeSize()) {
//                val node = el.childNode(i)
//                if (tClass.isInstance(node)) {
//                    nodes.add(tClass as T)
//                }
//            }
//        }
//        return nodes
    }
}
