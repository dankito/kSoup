package org.jsoup.select

import org.jsoup.helper.Validate
import org.jsoup.internal.Normalizer
import org.jsoup.internal.StringUtil
import org.jsoup.nodes.*
import java.util.regex.Matcher
import java.util.regex.Pattern

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
    abstract fun matches(root: Element?, element: Element?): Boolean

    /**
     * Reset any internal state in this Evaluator before executing a new Collector evaluation.
     */
    open fun reset() {}

    /**
     * A relative evaluator cost function. During evaluation, Evaluators are sorted by ascending cost as an optimization.
     * @return the relative cost of this Evaluator
     */
    open fun cost(): Int {
        return 5 // a nominal default cost
    }

    /**
     * Evaluator for tag name
     */
    class Tag(private val tagName: String?) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return ((element.normalName() == tagName))
        }

        protected override fun cost(): Int {
            return 1
        }

        public override fun toString(): String {
            return String.format("%s", tagName)
        }
    }

    /**
     * Evaluator for tag name that ends with
     */
    class TagEndsWith(private val tagName: String) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return (element.normalName().endsWith(tagName))
        }

        public override fun toString(): String {
            return String.format("%s", tagName)
        }
    }

    /**
     * Evaluator for element id
     */
    class Id(private val id: String?) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return ((id == element.id()))
        }

        protected override fun cost(): Int {
            return 2
        }

        public override fun toString(): String {
            return String.format("#%s", id)
        }
    }

    /**
     * Evaluator for element class
     */
    class Class(private val className: String) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return (element.hasClass(className))
        }

        protected override fun cost(): Int {
            return 6 // does whitespace scanning
        }

        public override fun toString(): String {
            return String.format(".%s", className)
        }
    }

    /**
     * Evaluator for attribute name matching
     */
    class Attribute(private val key: String?) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.hasAttr(key)
        }

        protected override fun cost(): Int {
            return 2
        }

        public override fun toString(): String {
            return String.format("[%s]", key)
        }
    }

    /**
     * Evaluator for attribute name prefix matching
     */
    class AttributeStarting(keyPrefix: String?) : Evaluator() {
        private val keyPrefix: String?

        init {
            Validate.notEmpty(keyPrefix)
            this.keyPrefix = Normalizer.lowerCase(keyPrefix)
        }

        public override fun matches(root: Element?, element: Element?): Boolean {
            val values: List<org.jsoup.nodes.Attribute?>? = element.attributes().asList()
            for (attribute: org.jsoup.nodes.Attribute? in values) {
                if (Normalizer.lowerCase(attribute.key).startsWith(keyPrefix)) return true
            }
            return false
        }

        protected override fun cost(): Int {
            return 6
        }

        public override fun toString(): String {
            return String.format("[^%s]", keyPrefix)
        }
    }

    /**
     * Evaluator for attribute name/value matching
     */
    class AttributeWithValue(key: String?, value: String?) : AttributeKeyPair(key, value) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.hasAttr(key) && value.equals(element.attr(key).trim({ it <= ' ' }), ignoreCase = true)
        }

        protected override fun cost(): Int {
            return 3
        }

        public override fun toString(): String {
            return String.format("[%s=%s]", key, value)
        }
    }

    /**
     * Evaluator for attribute name != value matching
     */
    class AttributeWithValueNot(key: String?, value: String?) : AttributeKeyPair(key, value) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return !value.equals(element.attr(key), ignoreCase = true)
        }

        protected override fun cost(): Int {
            return 3
        }

        public override fun toString(): String {
            return String.format("[%s!=%s]", key, value)
        }
    }

    /**
     * Evaluator for attribute name/value matching (value prefix)
     */
    class AttributeWithValueStarting(key: String?, value: String?) : AttributeKeyPair(key, value, false) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key))
                .startsWith(value) // value is lower case already
        }

        protected override fun cost(): Int {
            return 4
        }

        public override fun toString(): String {
            return String.format("[%s^=%s]", key, value)
        }
    }

    /**
     * Evaluator for attribute name/value matching (value ending)
     */
    class AttributeWithValueEnding(key: String?, value: String?) : AttributeKeyPair(key, value, false) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key))
                .endsWith(value) // value is lower case
        }

        protected override fun cost(): Int {
            return 4
        }

        public override fun toString(): String {
            return String.format("[%s$=%s]", key, value)
        }
    }

    /**
     * Evaluator for attribute name/value matching (value containing)
     */
    class AttributeWithValueContaining(key: String?, value: String?) : AttributeKeyPair(key, value) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key))
                .contains(value) // value is lower case
        }

        protected override fun cost(): Int {
            return 6
        }

        public override fun toString(): String {
            return String.format("[%s*=%s]", key, value)
        }
    }

    /**
     * Evaluator for attribute name/value matching (value regex matching)
     */
    class AttributeWithValueMatching(key: String?, var pattern: Pattern) : Evaluator() {
        var key: String?

        init {
            this.key = Normalizer.normalize(key)
        }

        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.hasAttr(key) && pattern.matcher(element.attr(key)).find()
        }

        protected override fun cost(): Int {
            return 8
        }

        public override fun toString(): String {
            return String.format("[%s~=%s]", key, pattern.toString())
        }
    }

    /**
     * Abstract evaluator for attribute name/value matching
     */
    abstract class AttributeKeyPair @JvmOverloads constructor(key: String?, value: String?, trimValue: Boolean = true) :
        Evaluator() {
        var key: String?
        var value: String?

        init {
            var value: String? = value
            Validate.notEmpty(key)
            Validate.notEmpty(value)
            this.key = Normalizer.normalize(key)
            val isStringLiteral: Boolean = (value.startsWith("'") && value.endsWith("'")
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
    class AllElements() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return true
        }

        protected override fun cost(): Int {
            return 10
        }

        public override fun toString(): String {
            return "*"
        }
    }

    /**
     * Evaluator for matching by sibling index number (e &lt; idx)
     */
    class IndexLessThan(index: Int) : IndexEvaluator(index) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return root !== element && element.elementSiblingIndex() < index
        }

        public override fun toString(): String {
            return String.format(":lt(%d)", index)
        }
    }

    /**
     * Evaluator for matching by sibling index number (e &gt; idx)
     */
    class IndexGreaterThan(index: Int) : IndexEvaluator(index) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.elementSiblingIndex() > index
        }

        public override fun toString(): String {
            return String.format(":gt(%d)", index)
        }
    }

    /**
     * Evaluator for matching by sibling index number (e = idx)
     */
    class IndexEquals(index: Int) : IndexEvaluator(index) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.elementSiblingIndex() == index
        }

        public override fun toString(): String {
            return String.format(":eq(%d)", index)
        }
    }

    /**
     * Evaluator for matching the last sibling (css :last-child)
     */
    class IsLastChild() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val p: Element? = element.parent()
            return (p != null) && !(p is Document) && (element === p.lastElementChild())
        }

        public override fun toString(): String {
            return ":last-child"
        }
    }

    class IsFirstOfType() : IsNthOfType(0, 1) {
        public override fun toString(): String {
            return ":first-of-type"
        }
    }

    class IsLastOfType() : IsNthLastOfType(0, 1) {
        public override fun toString(): String {
            return ":last-of-type"
        }
    }

    abstract class CssNthEvaluator(protected val a: Int, protected val b: Int) : Evaluator() {
        constructor(b: Int) : this(0, b)

        public override fun matches(root: Element?, element: Element?): Boolean {
            val p: Element? = element.parent()
            if (p == null || (p is Document)) return false
            val pos: Int = calculatePosition(root, element)
            if (a == 0) return pos == b
            return (pos - b) * a >= 0 && (pos - b) % a == 0
        }

        public override fun toString(): String {
            if (a == 0) return String.format(":%s(%d)", pseudoClass, b)
            if (b == 0) return String.format(":%s(%dn)", pseudoClass, a)
            return String.format(":%s(%dn%+d)", pseudoClass, a, b)
        }

        protected abstract val pseudoClass: String?
        protected abstract fun calculatePosition(root: Element?, element: Element?): Int
    }

    /**
     * css-compatible Evaluator for :eq (css :nth-child)
     *
     * @see IndexEquals
     */
    class IsNthChild(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element?, element: Element?): Int {
            return element.elementSiblingIndex() + 1
        }

        protected val pseudoClass: String?
            protected get() {
                return "nth-child"
            }
    }

    /**
     * css pseudo class :nth-last-child)
     *
     * @see IndexEquals
     */
    class IsNthLastChild(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element?, element: Element?): Int {
            if (element.parent() == null) return 0
            return element.parent().childrenSize() - element.elementSiblingIndex()
        }

        protected val pseudoClass: String?
            protected get() {
                return "nth-last-child"
            }
    }

    /**
     * css pseudo class nth-of-type
     *
     */
    open class IsNthOfType(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element?, element: Element?): Int {
            val parent: Element? = element.parent()
            if (parent == null) return 0
            var pos: Int = 0
            val size: Int = parent.childNodeSize()
            for (i in 0 until size) {
                val node: Node? = parent.childNode(i)
                if ((node.normalName() == element.normalName())) pos++
                if (node === element) break
            }
            return pos
        }

        protected val pseudoClass: String?
            protected get() {
                return "nth-of-type"
            }
    }

    open class IsNthLastOfType(a: Int, b: Int) : CssNthEvaluator(a, b) {
        override fun calculatePosition(root: Element?, element: Element?): Int {
            val parent: Element? = element.parent()
            if (parent == null) return 0
            var pos: Int = 0
            var next: Element? = element
            while (next != null) {
                if ((next.normalName() == element.normalName())) pos++
                next = next.nextElementSibling()
            }
            return pos
        }

        protected val pseudoClass: String?
            protected get() {
                return "nth-last-of-type"
            }
    }

    /**
     * Evaluator for matching the first sibling (css :first-child)
     */
    class IsFirstChild() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val p: Element? = element.parent()
            return (p != null) && !(p is Document) && (element === p.firstElementChild())
        }

        public override fun toString(): String {
            return ":first-child"
        }
    }

    /**
     * css3 pseudo-class :root
     * @see [:root selector](http://www.w3.org/TR/selectors/.root-pseudo)
     */
    class IsRoot() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val r: Element? = if (root is Document) root.firstElementChild() else root
            return element === r
        }

        protected override fun cost(): Int {
            return 1
        }

        public override fun toString(): String {
            return ":root"
        }
    }

    class IsOnlyChild() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val p: Element? = element.parent()
            return (p != null) && !(p is Document) && element.siblingElements().isEmpty()
        }

        public override fun toString(): String {
            return ":only-child"
        }
    }

    class IsOnlyOfType() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val p: Element? = element.parent()
            if (p == null || p is Document) return false
            var pos: Int = 0
            var next: Element? = p.firstElementChild()
            while (next != null) {
                if ((next.normalName() == element.normalName())) pos++
                if (pos > 1) break
                next = next.nextElementSibling()
            }
            return pos == 1
        }

        public override fun toString(): String {
            return ":only-of-type"
        }
    }

    class IsEmpty() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val family: List<Node?>? = element.childNodes()
            for (n: Node? in family) {
                if (!(n is Comment || n is XmlDeclaration || n is DocumentType)) return false
            }
            return true
        }

        public override fun toString(): String {
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
    class ContainsText(searchText: String?) : Evaluator() {
        private val searchText: String?

        init {
            this.searchText = Normalizer.lowerCase(StringUtil.normaliseWhitespace(searchText))
        }

        public override fun matches(root: Element?, element: Element?): Boolean {
            return Normalizer.lowerCase(element.text()).contains(searchText)
        }

        protected override fun cost(): Int {
            return 10
        }

        public override fun toString(): String {
            return String.format(":contains(%s)", searchText)
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) wholeText. Neither the input nor the element text is
     * normalized. `:containsWholeText()`
     * @since 1.15.1.
     */
    class ContainsWholeText(private val searchText: String?) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.wholeText().contains(searchText)
        }

        protected override fun cost(): Int {
            return 10
        }

        public override fun toString(): String {
            return String.format(":containsWholeText(%s)", searchText)
        }
    }

    /**
     * Evaluator for matching Element (but **not** its descendants) wholeText. Neither the input nor the element text is
     * normalized. `:containsWholeOwnText()`
     * @since 1.15.1.
     */
    class ContainsWholeOwnText(private val searchText: String?) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return element.wholeOwnText().contains(searchText)
        }

        public override fun toString(): String {
            return String.format(":containsWholeOwnText(%s)", searchText)
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) data
     */
    class ContainsData(searchText: String?) : Evaluator() {
        private val searchText: String?

        init {
            this.searchText = Normalizer.lowerCase(searchText)
        }

        public override fun matches(root: Element?, element: Element?): Boolean {
            return Normalizer.lowerCase(element.data()).contains(searchText) // not whitespace normalized
        }

        public override fun toString(): String {
            return String.format(":containsData(%s)", searchText)
        }
    }

    /**
     * Evaluator for matching Element's own text
     */
    class ContainsOwnText(searchText: String?) : Evaluator() {
        private val searchText: String?

        init {
            this.searchText = Normalizer.lowerCase(StringUtil.normaliseWhitespace(searchText))
        }

        public override fun matches(root: Element?, element: Element?): Boolean {
            return Normalizer.lowerCase(element.ownText()).contains(searchText)
        }

        public override fun toString(): String {
            return String.format(":containsOwn(%s)", searchText)
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) text with regex
     */
    class Matches(private val pattern: Pattern) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val m: Matcher = pattern.matcher(element.text())
            return m.find()
        }

        protected override fun cost(): Int {
            return 8
        }

        public override fun toString(): String {
            return String.format(":matches(%s)", pattern)
        }
    }

    /**
     * Evaluator for matching Element's own text with regex
     */
    class MatchesOwn(private val pattern: Pattern) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val m: Matcher = pattern.matcher(element.ownText())
            return m.find()
        }

        protected override fun cost(): Int {
            return 7
        }

        public override fun toString(): String {
            return String.format(":matchesOwn(%s)", pattern)
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) whole text with regex.
     * @since 1.15.1.
     */
    class MatchesWholeText(private val pattern: Pattern) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val m: Matcher = pattern.matcher(element.wholeText())
            return m.find()
        }

        protected override fun cost(): Int {
            return 8
        }

        public override fun toString(): String {
            return String.format(":matchesWholeText(%s)", pattern)
        }
    }

    /**
     * Evaluator for matching Element's own whole text with regex.
     * @since 1.15.1.
     */
    class MatchesWholeOwnText(private val pattern: Pattern) : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            val m: Matcher = pattern.matcher(element.wholeOwnText())
            return m.find()
        }

        protected override fun cost(): Int {
            return 7
        }

        public override fun toString(): String {
            return String.format(":matchesWholeOwnText(%s)", pattern)
        }
    }

    class MatchText() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            if (element is PseudoTextElement) return true
            val textNodes: List<TextNode?>? = element.textNodes()
            for (textNode: TextNode? in textNodes) {
                val pel: PseudoTextElement = PseudoTextElement(
                    valueOf(element.tagName()), element.baseUri(), element.attributes()
                )
                textNode.replaceWith(pel)
                pel.appendChild(textNode)
            }
            return false
        }

        protected override fun cost(): Int {
            return -1 // forces first evaluation, which prepares the DOM for later evaluator matches
        }

        public override fun toString(): String {
            return ":matchText"
        }
    }
}
