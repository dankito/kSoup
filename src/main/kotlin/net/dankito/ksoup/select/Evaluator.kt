package net.dankito.ksoup.select

import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.internal.Normalizer
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.*

/**
 * Evaluates that an element matches the selector.
 */
abstract class Evaluator protected constructor() {
    /**
     * Test if the element meets the evaluator's requirements.
     *
     * @param root    Root of the matching subtree
     * @param element tested element
     * @return Returns <tt>true</tt> if the requirements are met or
     * <tt>false</tt> otherwise
     */
    abstract fun matches(root: Element, element: Element): Boolean

    /**
     * Reset any internal state in this Evaluator before executing a new Collector evaluation.
     */
    internal open fun reset() {}

    /**
     * A relative evaluator cost function. During evaluation, Evaluators are sorted by ascending cost as an optimization.
     * @return the relative cost of this Evaluator
     */
    internal open fun cost(): Int {
        return 5 // a nominal default cost
    }

    /**
     * Evaluator for tag name
     */
    class Tag(private val tagName: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return element.normalName().equals(tagName)
        }

        override fun cost(): Int {
            return 1
        }

        override fun toString(): String {
            return tagName
        }
    }

    /**
     * Evaluator for tag name that ends with
     */
    class TagEndsWith(private val tagName: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return element.normalName().endsWith(tagName)
        }

        override fun toString(): String {
            return tagName
        }
    }

    /**
     * Evaluator for element id
     */
    class Id(private val id: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return id == element.id()
        }

        override fun cost(): Int {
            return 2
        }

        override fun toString(): String {
            return "#$id"
        }
    }

    /**
     * Evaluator for element class
     */
    class Class(private val className: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return element.hasClass(className)
        }

        override fun cost(): Int {
            return 6 // does whitespace scanning
        }

        override fun toString(): String {
            return ".$className"
        }
    }

    /**
     * Evaluator for attribute name matching
     */
    class Attribute(private val key: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return element.hasAttr(key)
        }

        override fun cost(): Int {
            return 2
        }

        override fun toString(): String {
            return "[$key]"
        }
    }

    /**
     * Evaluator for attribute name prefix matching
     */
    class AttributeStarting(keyPrefix: String?) : Evaluator() {
        private val keyPrefix: String

        init {
            Validate.notEmpty(keyPrefix)
            this.keyPrefix = Normalizer.lowerCase(keyPrefix)
        }

        override fun matches(root: Element, element: Element): Boolean {
            val values = element.attributes().asList()
            for (attribute in values) {
                if (Normalizer.lowerCase(attribute.key).startsWith(keyPrefix)) return true
            }
            return false
        }

        override fun cost(): Int {
            return 6
        }

        override fun toString(): String {
            return "[^$keyPrefix]"
        }
    }

    /**
     * Evaluator for attribute name/value matching
     */
    class AttributeWithValue(key: String, value: String) : AttributeKeyPair(key, value) {
        override fun matches(root: Element, element: Element): Boolean {
            return element.hasAttr(key) && value.equals(element.attr(key).trim(), true)
        }

        override fun cost(): Int {
            return 3
        }

        override fun toString(): String {
            return "[$key=$value]"
        }
    }

    /**
     * Evaluator for attribute name != value matching
     */
    class AttributeWithValueNot(key: String, value: String) : AttributeKeyPair(key, value) {
        override fun matches(root: Element, element: Element): Boolean {
            return !value.equals(element.attr(key), true)
        }

        override fun cost(): Int {
            return 3
        }

        override fun toString(): String {
            return "[$key!=$value]"
        }
    }

    /**
     * Evaluator for attribute name/value matching (value prefix)
     */
    class AttributeWithValueStarting(key: String, value: String) : AttributeKeyPair(key, value, false) {
        override fun matches(root: Element, element: Element): Boolean {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key)).startsWith(value) // value is lower case already
        }

        override fun cost(): Int {
            return 4
        }

        override fun toString(): String {
            return "[$key^=$value]"
        }
    }

    /**
     * Evaluator for attribute name/value matching (value ending)
     */
    class AttributeWithValueEnding(key: String, value: String) : AttributeKeyPair(key, value, false) {
        override fun matches(root: Element, element: Element): Boolean {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key)).endsWith(value) // value is lower case
        }

        override fun cost(): Int {
            return 4
        }

        override fun toString(): String {
            return "[$key$=$value]"
        }
    }

    /**
     * Evaluator for attribute name/value matching (value containing)
     */
    class AttributeWithValueContaining(key: String, value: String) : AttributeKeyPair(key, value) {
        override fun matches(root: Element, element: Element): Boolean {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key)).contains(value) // value is lower case
        }

        override fun cost(): Int {
            return 6
        }

        override fun toString(): String {
            return "[$key*=$value]"
        }
    }

    /**
     * Evaluator for attribute name/value matching (value regex matching)
     */
    class AttributeWithValueMatching(key: String?, private val pattern: Regex) : Evaluator() {

        val key = Normalizer.normalize(key)

        override fun matches(root: Element, element: Element): Boolean {
            return element.hasAttr(key) && pattern.find(element.attr(key)) != null
        }

        override fun cost(): Int {
            return 8
        }

        override fun toString(): String {
            return "[$key~=$pattern]"
        }
    }

    /**
     * Abstract evaluator for attribute name/value matching
     */
    abstract class AttributeKeyPair @JvmOverloads constructor(key: String, value: String, trimValue: Boolean = true) :
        Evaluator() {
        var key: String
        var value: String

        init {
            var value = value
            Validate.notEmpty(key)
            Validate.notEmpty(value)
            this.key = Normalizer.normalize(key)
            val isStringLiteral = (value.startsWith("'") && value.endsWith("'")
                    || value.startsWith("\"") && value.endsWith("\""))
            if (isStringLiteral) {
                value = value.substring(1, value.length - 1)
            }
            this.value = if (trimValue) Normalizer.normalize(value) else Normalizer.normalize(value, isStringLiteral)
        }
    }

    /**
     * Evaluator for any / all element matching
     */
    class AllElements : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return true
        }

        override fun cost(): Int {
            return 10
        }

        override fun toString(): String {
            return "*"
        }
    }

    /**
     * Evaluator for matching by sibling index number (e &lt; idx)
     */
    class IndexLessThan(index: Int) : IndexEvaluator(index) {
        override fun matches(root: Element, element: Element): Boolean {
            return root !== element && element.elementSiblingIndex() < index
        }

        override fun toString(): String {
            return ":lt($index)"
        }
    }

    /**
     * Evaluator for matching by sibling index number (e &gt; idx)
     */
    class IndexGreaterThan(index: Int) : IndexEvaluator(index) {
        override fun matches(root: Element, element: Element): Boolean {
            return element.elementSiblingIndex() > index
        }

        override fun toString(): String {
            return ":gt($index)"
        }
    }

    /**
     * Evaluator for matching by sibling index number (e = idx)
     */
    class IndexEquals(index: Int) : IndexEvaluator(index) {
        override fun matches(root: Element, element: Element): Boolean {
            return element.elementSiblingIndex() === index
        }

        override fun toString(): String {
            return ":eq($index)"
        }
    }

    /**
     * Evaluator for matching the last sibling (css :last-child)
     */
    class IsLastChild : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            val p = element.parent()
            return p != null && p !is Document && element === p.lastElementChild()
        }

        override fun toString(): String {
            return ":last-child"
        }
    }

    class IsFirstOfType : IsNthOfType(0, 1) {
        override fun toString(): String {
            return ":first-of-type"
        }
    }

    class IsLastOfType : IsNthLastOfType(0, 1) {
        override fun toString(): String {
            return ":last-of-type"
        }
    }

    abstract class CssNthEvaluator(protected val a: Int, protected val b: Int) : Evaluator() {
        constructor(b: Int) : this(0, b)

        override fun matches(root: Element, element: Element): Boolean {
            val p = element.parent()
            if (p == null || p is Document) return false
            val pos = calculatePosition(root, element)
            return if (a == 0) pos == b else (pos - b) * a >= 0 && (pos - b) % a == 0
        }

        override fun toString(): String {
            return if (a == 0) {
                ":$pseudoClass($b)"
            } else  if (b == 0) {
                ":$pseudoClass(${a}n)"
            } else {
                ":$pseudoClass(${a}n${if (b > 0) "+" else ""}$b)"
            }
        }

        protected abstract val pseudoClass: String
        protected abstract fun calculatePosition(root: Element, element: Element): Int
    }

    /**
     * css-compatible Evaluator for :eq (css :nth-child)
     *
     * @see IndexEquals
     */
    class IsNthChild(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element, element: Element): Int {
            return element.elementSiblingIndex() + 1
        }

        override val pseudoClass: String
            get() = "nth-child"
    }

    /**
     * css pseudo class :nth-last-child)
     *
     * @see IndexEquals
     */
    class IsNthLastChild(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element, element: Element): Int {
            return element.parent()?.let { parent ->
                parent.childrenSize() - element.elementSiblingIndex()
            } ?: 0
        }

        override val pseudoClass: String
            get() = "nth-last-child"
    }

    /**
     * css pseudo class nth-of-type
     *
     */
    open class IsNthOfType(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element, element: Element): Int {
            val parent: Element = element.parent() ?: return 0
            var pos = 0
            val size: Int = parent.childNodeSize()
            for (i in 0 until size) {
                val node = parent.childNode(i)
                if (node.normalName().equals(element.normalName())) pos++
                if (node === element) break
            }
            return pos
        }

        override val pseudoClass: String
            get() = "nth-of-type"
    }

    open class IsNthLastOfType(a: Int, b: Int) : CssNthEvaluator(a, b) {

        override fun calculatePosition(root: Element, element: Element): Int {
            if (element.parent() == null) {
                return 0
            }

            var pos = 0
            var next: Element? = element

            while (next != null) {
                if (next.normalName().equals(element.normalName())) pos++
                next = next.nextElementSibling()
            }

            return pos
        }

        override val pseudoClass: String
            get() = "nth-last-of-type"
    }

    /**
     * Evaluator for matching the first sibling (css :first-child)
     */
    class IsFirstChild : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            val p = element.parent()
            return p != null && p !is Document && element === p.firstElementChild()
        }

        override fun toString(): String {
            return ":first-child"
        }
    }

    /**
     * css3 pseudo-class :root
     * @see [:root selector](http://www.w3.org/TR/selectors/.root-pseudo)
     */
    class IsRoot : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            val r = if (root is Document) root.firstElementChild() else root
            return element === r
        }

        override fun cost(): Int {
            return 1
        }

        override fun toString(): String {
            return ":root"
        }
    }

    class IsOnlyChild : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            val p = element.parent()
            return p != null && p !is Document && element.siblingElements().isEmpty()
        }

        override fun toString(): String {
            return ":only-child"
        }
    }

    class IsOnlyOfType : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            val p = element.parent()
            if (p == null || p is Document) return false
            var pos = 0
            var next = p.firstElementChild()
            while (next != null) {
                if (next.normalName().equals(element.normalName())) pos++
                if (pos > 1) break
                next = next.nextElementSibling()
            }
            return pos == 1
        }

        override fun toString(): String {
            return ":only-of-type"
        }
    }

    class IsEmpty : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            val family: List<Node> = element.childNodes()
            for (n in family) {
                if (!(n is Comment || n is XmlDeclaration || n is DocumentType)) return false
            }
            return true
        }

        override fun toString(): String {
            return ":empty"
        }
    }

    /**
     * Abstract evaluator for sibling index matching
     *
     * @author ant
     */
    abstract class IndexEvaluator(var index: Int) : Evaluator()

    /**
     * Evaluator for matching Element (and its descendants) text
     */
    class ContainsText(searchText: String) : Evaluator() {
        private val searchText: String

        init {
            this.searchText = Normalizer.lowerCase(StringUtil.normaliseWhitespace(searchText))
        }

        override fun matches(root: Element, element: Element): Boolean {
            return Normalizer.lowerCase(element.text()).contains(searchText)
        }

        override fun cost(): Int {
            return 10
        }

        override fun toString(): String {
            return ":contains($searchText)"
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) wholeText. Neither the input nor the element text is
     * normalized. `:containsWholeText()`
     * @since 1.15.1.
     */
    class ContainsWholeText(private val searchText: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return element.wholeText().contains(searchText)
        }

        override fun cost(): Int {
            return 10
        }

        override fun toString(): String {
            return ":containsWholeText($searchText)"
        }
    }

    /**
     * Evaluator for matching Element (but **not** its descendants) wholeText. Neither the input nor the element text is
     * normalized. `:containsWholeOwnText()`
     * @since 1.15.1.
     */
    class ContainsWholeOwnText(private val searchText: String) : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            return element.wholeOwnText().contains(searchText)
        }

        override fun toString(): String {
            return ":containsWholeOwnText($searchText)"
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) data
     */
    class ContainsData(searchText: String?) : Evaluator() {
        private val searchText: String

        init {
            this.searchText = Normalizer.lowerCase(searchText)
        }

        override fun matches(root: Element, element: Element): Boolean {
            return Normalizer.lowerCase(element.data()).contains(searchText) // not whitespace normalized
        }

        override fun toString(): String {
            return ":containsData($searchText)"
        }
    }

    /**
     * Evaluator for matching Element's own text
     */
    class ContainsOwnText(searchText: String) : Evaluator() {
        private val searchText: String

        init {
            this.searchText = Normalizer.lowerCase(StringUtil.normaliseWhitespace(searchText))
        }

        override fun matches(root: Element, element: Element): Boolean {
            return Normalizer.lowerCase(element.ownText()).contains(searchText)
        }

        override fun toString(): String {
            return ":containsOwn($searchText)"
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) text with regex
     */
    class Matches(private val regex: Regex) : Evaluator() {

        override fun matches(root: Element, element: Element): Boolean {
            return regex.find(element.text()) != null
        }

        override fun cost(): Int {
            return 8
        }

        override fun toString(): String {
            return ":matches($regex)"
        }
    }

    /**
     * Evaluator for matching Element's own text with regex
     */
    class MatchesOwn(private val regex: Regex) : Evaluator() {

        override fun matches(root: Element, element: Element): Boolean {
            return regex.find(element.ownText()) != null
        }

        override fun cost(): Int {
            return 7
        }

        override fun toString(): String {
            return ":matchesOwn($regex)"
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) whole text with regex.
     * @since 1.15.1.
     */
    class MatchesWholeText(private val regex: Regex) : Evaluator() {

        override fun matches(root: Element, element: Element): Boolean {
            return regex.find(element.wholeText()) != null
        }

        override fun cost(): Int {
            return 8
        }

        override fun toString(): String {
            return ":matchesWholeText($regex)"
        }
    }

    /**
     * Evaluator for matching Element's own whole text with regex.
     * @since 1.15.1.
     */
    class MatchesWholeOwnText(private val regex: Regex) : Evaluator() {

        override fun matches(root: Element, element: Element): Boolean {
            return regex.matches(element.wholeOwnText())
        }

        override fun cost(): Int {
            return 7
        }

        override fun toString(): String {
            return ":matchesWholeOwnText($regex)"
        }
    }

    class MatchText : Evaluator() {
        override fun matches(root: Element, element: Element): Boolean {
            if (element is PseudoTextElement) return true
            val textNodes: List<TextNode> = element.textNodes()
            for (textNode in textNodes) {
                val pel = PseudoTextElement(net.dankito.ksoup.parser.Tag.valueOf(element.tagName()), element.baseUri(), element.attributes())
                textNode.replaceWith(pel)
                pel.appendChild(textNode)
            }
            return false
        }

        override fun cost(): Int {
            return -1 // forces first evaluation, which prepares the DOM for later evaluator matches
        }

        override fun toString(): String {
            return ":matchText"
        }
    }
}
