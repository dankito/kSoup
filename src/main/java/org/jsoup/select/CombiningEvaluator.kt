package org.jsoup.select

import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Element

/**
 * Base combining (and, or) evaluator.
 */
abstract class CombiningEvaluator internal constructor() : Evaluator() {
    val evaluators = ArrayList<Evaluator>() // maintain original order so that #toString() is sensible
    val sortedEvaluators = ArrayList<Evaluator>() // cost ascending order
    var num = 0
    var cost = 0

    internal constructor(evaluators: Collection<Evaluator>) : this() {
        this.evaluators.addAll(evaluators)
        updateEvaluators()
    }

    override fun reset() {
        for (evaluator in evaluators) {
            evaluator.reset()
        }
        super.reset()
    }

    override fun cost(): Int {
        return cost
    }

    fun rightMostEvaluator(): Evaluator? {
        return if (num > 0) evaluators.get(num - 1) else null
    }

    fun replaceRightMostEvaluator(replacement: Evaluator) {
        evaluators.set(num - 1, replacement)
        updateEvaluators()
    }

    fun updateEvaluators() {
        // used so we don't need to bash on size() for every match test
        num = evaluators.size

        // sort the evaluators by lowest cost first, to optimize the evaluation order
        cost = 0
        for (evaluator in evaluators) {
            cost += evaluator.cost()
        }
        sortedEvaluators.clear()
        sortedEvaluators.addAll(evaluators)
        sortedEvaluators.sortWith(costComparator)
    }

    // ^ comparingInt, sortedEvaluators.sort not available in targeted version
    class And internal constructor(evaluators: Collection<Evaluator>) : CombiningEvaluator(evaluators) {

        internal constructor(vararg evaluators: Evaluator) : this(evaluators.toList())

        override fun matches(root: Element, element: Element): Boolean {
            for (i in 0 until num) {
                val s: Evaluator = sortedEvaluators.get(i)
                if (!s.matches(root, element)) return false
            }
            return true
        }

        override fun toString(): String {
            return StringUtil.join(evaluators, "")
        }
    }

    class Or() : CombiningEvaluator() {
        /**
         * Create a new Or evaluator. The initial evaluators are ANDed together and used as the first clause of the OR.
         * @param evaluators initial OR clause (these are wrapped into an AND evaluator).
         */
        internal constructor(evaluators: Collection<Evaluator>) : this() {
            if (num > 1) {
                this.evaluators.add(And(evaluators))
            } else { // 0 or 1
                this.evaluators.addAll(evaluators)
            }
            updateEvaluators()
        }

        internal constructor(vararg evaluators: Evaluator) : this(evaluators.toList())

        fun add(e: Evaluator) {
            evaluators.add(e)
            updateEvaluators()
        }

        override fun matches(root: Element, node: Element): Boolean {
            for (i in 0 until num) {
                val s: Evaluator = sortedEvaluators.get(i)
                if (s.matches(root, node)) return true
            }
            return false
        }

        override fun toString(): String {
            return StringUtil.join(evaluators, ", ")
        }
    }

    companion object {
        private val costComparator: Comparator<Evaluator> =
            Comparator { o1: Evaluator, o2: Evaluator -> o1.cost() - o2.cost() }
    }
}
