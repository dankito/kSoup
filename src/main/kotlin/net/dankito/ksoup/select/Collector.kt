package net.dankito.ksoup.select

import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.nodes.Node

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
    fun collect(eval: Evaluator, root: Element): Elements {
        eval.reset()
        val elements = Elements()

        NodeTraversor.traverse(GenericNodeVisitor { node ->
            if (node is Element && eval.matches(root, node)) {
                elements.add(node)
            }
        }, root)

        return elements
    }

    /**
     * Finds the first Element that matches the Evaluator that descends from the root, and stops the query once that first
     * match is found.
     * @param eval Evaluator to test elements against
     * @param root root of tree to descend
     * @return the first match; `null` if none
     */
    fun findFirst(eval: Evaluator, root: Element): Element? {
        eval.reset()
        val finder = FirstFinder(eval, root)
        return finder.find(root)
    }

    internal class FirstFinder(private val eval: Evaluator, private val root: Element) : NodeFilter {

        private var match: Element? = null

        fun find(start: Element): Element? {
            match = null
            NodeTraversor.filter(this, start)
            return match
        }

        override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
            if (node is Element && eval.matches(root, node)) {
                match = node
                return NodeFilter.FilterResult.STOP
            }

            return NodeFilter.FilterResult.CONTINUE
        }
    }
}
