package org.jsoup.select

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.*
import java.util.function.Supplier

/**
 * Base structural evaluator.
 */
internal abstract class StructuralEvaluator(val evaluator: Evaluator?) : Evaluator() {
    // Memoize inner matches, to save repeated re-evaluations of parent, sibling etc.
    // root + element: Boolean matches. ThreadLocal in case the Evaluator is compiled then reused across multi threads
    @JvmField
    val threadMemo: ThreadLocal<IdentityHashMap<Element, IdentityHashMap<Element, Boolean>>> =
        ThreadLocal.withInitial<IdentityHashMap<Element, IdentityHashMap<Element, Boolean>>>(
            Supplier<IdentityHashMap<Element, IdentityHashMap<Element, Boolean>>>({ IdentityHashMap() })
        )

    fun memoMatches(root: Element, element: Element): Boolean {
        // not using computeIfAbsent, as the lambda impl requires a new Supplier closure object on every hit: tons of GC
        val rootMemo: IdentityHashMap<Element, IdentityHashMap<Element, Boolean>> = threadMemo.get()
        var memo: IdentityHashMap<Element, Boolean>? = rootMemo.get(root)
        if (memo == null) {
            memo = IdentityHashMap()
            rootMemo.put(root, memo)
        }
        var matches: Boolean? = memo.get(element)
        if (matches == null) {
            matches = evaluator.matches(root, element)
            memo.put(element, matches)
        }
        return matches
    }

    override fun reset() {
        threadMemo.get().clear()
        super.reset()
    }

    internal class Root() : Evaluator() {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return root === element
        }

        override fun cost(): Int {
            return 1
        }

        public override fun toString(): String {
            return ""
        }
    }

    internal class Has(evaluator: Evaluator?) : StructuralEvaluator(evaluator) {
        val finder: Collector.FirstFinder

        init {
            finder = Collector.FirstFinder(evaluator)
        }

        public override fun matches(root: Element?, element: Element?): Boolean {
            // for :has, we only want to match children (or below), not the input element. And we want to minimize GCs
            for (i in 0 until element.childNodeSize()) {
                val node: Node? = element.childNode(i)
                if (node is Element) {
                    val match: Element? = finder.find(element, node as Element)
                    if (match != null) return true
                }
            }
            return false
        }

        override fun cost(): Int {
            return 10 * evaluator.cost()
        }

        public override fun toString(): String {
            return String.format(":has(%s)", evaluator)
        }
    }

    internal class Not(evaluator: Evaluator?) : StructuralEvaluator(evaluator) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            return !memoMatches(root, element)
        }

        override fun cost(): Int {
            return 2 + evaluator.cost()
        }

        public override fun toString(): String {
            return String.format(":not(%s)", evaluator)
        }
    }

    internal class Parent(evaluator: Evaluator?) : StructuralEvaluator(evaluator) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            if (root === element) return false
            var parent: Element? = element.parent()
            while (parent != null) {
                if (memoMatches(root, parent)) return true
                if (parent === root) break
                parent = parent.parent()
            }
            return false
        }

        override fun cost(): Int {
            return 2 * evaluator.cost()
        }

        public override fun toString(): String {
            return String.format("%s ", evaluator)
        }
    }

    internal class ImmediateParent(evaluator: Evaluator?) : StructuralEvaluator(evaluator) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            if (root === element) return false
            val parent: Element? = element.parent()
            return parent != null && memoMatches(root, parent)
        }

        override fun cost(): Int {
            return 1 + evaluator.cost()
        }

        public override fun toString(): String {
            return String.format("%s > ", evaluator)
        }
    }

    internal class PreviousSibling(evaluator: Evaluator?) : StructuralEvaluator(evaluator) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            if (root === element) return false
            var sibling: Element? = element.firstElementSibling()
            while (sibling != null) {
                if (sibling === element) break
                if (memoMatches(root, sibling)) return true
                sibling = sibling.nextElementSibling()
            }
            return false
        }

        override fun cost(): Int {
            return 3 * evaluator.cost()
        }

        public override fun toString(): String {
            return String.format("%s ~ ", evaluator)
        }
    }

    internal class ImmediatePreviousSibling(evaluator: Evaluator?) : StructuralEvaluator(evaluator) {
        public override fun matches(root: Element?, element: Element?): Boolean {
            if (root === element) return false
            val prev: Element? = element.previousElementSibling()
            return prev != null && memoMatches(root, prev)
        }

        override fun cost(): Int {
            return 2 + evaluator.cost()
        }

        public override fun toString(): String {
            return String.format("%s + ", evaluator)
        }
    }
}
