package org.jsoup.select

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

/**
 * Collects a list of elements that match the supplied criteria.
 *
 * @author Jonathan Hedley
 */
object Collector {
    /**
     * Build a list of elements, by visiting root and every descendant of root, and testing it against the evaluator.
     * @param eval Evaluator to test elements against
     * @param root root of tree to descend
     * @return list of matches; empty if none
     */
    fun collect(eval: Evaluator?, root: Element?): Elements {
        eval.reset()
        val elements: Elements = Elements()
        NodeTraversor.traverse(NodeVisitor({ node: Node?, depth: Int ->
            if (node is Element) {
                val el: Element = node as Element
                if (eval.matches(root, el)) elements.add(el)
            }
        }), root)
        return elements
    }

    /**
     * Finds the first Element that matches the Evaluator that descends from the root, and stops the query once that first
     * match is found.
     * @param eval Evaluator to test elements against
     * @param root root of tree to descend
     * @return the first match; `null` if none
     */
    fun findFirst(eval: Evaluator?, root: Element): Element? {
        eval.reset()
        val finder: FirstFinder = FirstFinder(eval)
        return finder.find(root, root)
    }

    internal class FirstFinder(private val eval: Evaluator) : NodeFilter {
        private var evalRoot: Element? = null
        private var match: Element? = null
        fun find(root: Element?, start: Element): Element? {
            evalRoot = root
            match = null
            NodeTraversor.filter(this, start)
            return match
        }

        override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
            if (node is Element) {
                val el: Element = node as Element
                if (eval.matches(evalRoot, el)) {
                    match = el
                    return NodeFilter.FilterResult.STOP
                }
            }
            return NodeFilter.FilterResult.CONTINUE
        }
    }
}
