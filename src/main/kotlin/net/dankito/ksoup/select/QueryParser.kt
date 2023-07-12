package net.dankito.ksoup.select

import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.internal.Normalizer
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.parser.TokenQueue
import net.dankito.ksoup.select.Evaluator.*
import net.dankito.ksoup.select.Selector.SelectorParseException
import net.dankito.ksoup.select.StructuralEvaluator.*

/**
 * Parses a CSS selector into an Evaluator tree.
 */
class QueryParser private constructor(query: String) {
    private val tq: TokenQueue
    private val query: String
    private val evals = ArrayList<Evaluator>()

    /**
     * Create a new QueryParser.
     * @param query CSS query
     */
    init {
        Validate.notEmpty(query)
        this.query = query.trim()
        tq = TokenQueue(this.query)
    }

    /**
     * Parse the query
     * @return Evaluator
     */
    fun parse(): Evaluator {
        tq.consumeWhitespace()

        if (tq.matchesAny(*Combinators)) { // if starts with a combinator, use root as elements
            evals.add(StructuralEvaluator.Root())
            combinator(tq.consume())
        } else {
            evals.add(consumeEvaluator())
        }

        while (!tq.isEmpty) {
            // hierarchy and extras
            val seenWhite: Boolean = tq.consumeWhitespace()

            if (tq.matchesAny(*Combinators)) {
                combinator(tq.consume())
            } else if (seenWhite) {
                combinator(' ')
            } else { // E.class, E#id, E[attr] etc. AND
                evals.add(consumeEvaluator()) // take next el, #. etc off queue
            }
        }

        return if (evals.size == 1) {
            evals[0]
        } else {
            CombiningEvaluator.And(evals)
        }
    }

    private fun combinator(combinator: Char) {
        tq.consumeWhitespace()
        val subQuery = consumeSubQuery() // support multi > childs
        var rootEval: Evaluator? // the new topmost evaluator
        var currentEval: Evaluator? // the evaluator the new eval will be combined to. could be root, or rightmost or.
        val newEval = parse(subQuery) // the evaluator to add into target evaluator
        var replaceRightMost = false
        if (evals.size == 1) {
            currentEval = evals[0]
            rootEval = currentEval
            // make sure OR (,) has precedence:
            if (rootEval is CombiningEvaluator.Or && combinator != ',') {
                currentEval = (currentEval as CombiningEvaluator.Or?)!!.rightMostEvaluator()
                requireNotNull(currentEval) // rightMost signature can return null (if none set), but always will have one by this point
                replaceRightMost = true
            }
        } else {
            currentEval = CombiningEvaluator.And(evals)
            rootEval = currentEval
        }
        evals.clear()
        when (combinator) {
            '>' -> currentEval = CombiningEvaluator.And(ImmediateParent(currentEval), newEval)
            ' ' -> currentEval = CombiningEvaluator.And(Parent(currentEval), newEval)
            '+' -> currentEval = CombiningEvaluator.And(ImmediatePreviousSibling(currentEval), newEval)
            '~' -> currentEval = CombiningEvaluator.And(PreviousSibling(currentEval), newEval)
            ',' -> {
                val or: CombiningEvaluator.Or?
                if (currentEval is CombiningEvaluator.Or) {
                    or = currentEval
                } else {
                    or = CombiningEvaluator.Or()
                    or.add(currentEval)
                }
                or!!.add(newEval)
                currentEval = or
            }

            else -> throw SelectorParseException("Unknown combinator '$combinator'")
        }
        if (replaceRightMost) (rootEval as CombiningEvaluator.Or?)!!.replaceRightMostEvaluator(currentEval) else rootEval =
            currentEval
        evals.add(rootEval)
    }

    private fun consumeSubQuery(): String {
        val sq= StringUtil.borrowBuilder()
        while (!tq.isEmpty) {
            if (tq.matches("(")) sq.append("(").append(tq.chompBalanced('(', ')'))
                .append(")") else if (tq.matches("[")) sq.append("[").append(tq.chompBalanced('[', ']'))
                .append("]") else if (tq.matchesAny(*Combinators)
            ) if (sq.length > 0) break else tq.consume() else sq.append(tq.consume())
        }
        return StringUtil.releaseBuilder(sq)
    }

    private fun consumeEvaluator(): Evaluator {
        return when {
            tq.matchChomp("#") -> byId()
            tq.matchChomp(".") -> byClass()
            tq.matchesWord() || tq.matches("*|") -> byTag()
            tq.matches("[") -> byAttribute()
            tq.matchChomp("*") -> AllElements()
            tq.matchChomp(":") -> parsePseudoSelector()
            else -> throw SelectorParseException("Could not parse query '$query': unexpected token at '${tq.remainder()}'")
        }
    }

    private fun parsePseudoSelector(): Evaluator {
        val pseudo = tq.consumeCssIdentifier()
        return when (pseudo) {
            "lt" -> IndexLessThan(consumeIndex())
            "gt" -> IndexGreaterThan(consumeIndex())
            "eq" -> IndexEquals(consumeIndex())
            "has" -> has()
            "contains" -> contains(false)
            "containsOwn" -> contains(true)
            "containsWholeText" -> containsWholeText(false)
            "containsWholeOwnText" -> containsWholeText(true)
            "containsData" -> containsData()
            "matches" -> matches(false)
            "matchesOwn" -> matches(true)
            "matchesWholeText" -> matchesWholeText(false)
            "matchesWholeOwnText" -> matchesWholeText(true)
            "not" -> not()
            "nth-child" -> cssNthChild(false, false)
            "nth-last-child" -> cssNthChild(true, false)
            "nth-of-type" -> cssNthChild(false, true)
            "nth-last-of-type" -> cssNthChild(true, true)
            "first-child" -> IsFirstChild()
            "last-child" -> IsLastChild()
            "first-of-type" -> IsFirstOfType()
            "last-of-type" -> IsLastOfType()
            "only-child" -> IsOnlyChild()
            "only-of-type" -> IsOnlyOfType()
            "empty" -> IsEmpty()
            "root" -> IsRoot()
            "matchText" -> MatchText()
            else -> throw SelectorParseException("Could not parse query '$query': unexpected token at '${tq.remainder()}'")
        }
    }

    private fun byId(): Evaluator {
        val id = tq.consumeCssIdentifier()
        Validate.notEmpty(id)
        return Id(id)
    }

    private fun byClass(): Evaluator {
        val className = tq.consumeCssIdentifier()
        Validate.notEmpty(className)
        return Evaluator.Class(className.trim { it <= ' ' })
    }

    private fun byTag(): Evaluator {
        // todo - these aren't dealing perfectly with case sensitivity. For case sensitive parsers, we should also make
        // the tag in the selector case-sensitive (and also attribute names). But for now, normalize (lower-case) for
        // consistency - both the selector and the element tag
        var tagName = Normalizer.normalize(tq.consumeElementSelector())
        Validate.notEmpty(tagName)
        val eval: Evaluator

        // namespaces: wildcard match equals(tagName) or ending in ":"+tagName
        if (tagName.startsWith("*|")) {
            val plainTag = tagName.substring(2) // strip *|
            eval = CombiningEvaluator.Or(
                Evaluator.Tag(plainTag),
                TagEndsWith(tagName.replace("*|", ":"))
            )
        } else {
            // namespaces: if element name is "abc:def", selector must be "abc|def", so flip:
            if (tagName.contains("|")) tagName = tagName.replace("|", ":")
            eval = Evaluator.Tag(tagName)
        }
        return eval
    }

    private fun byAttribute(): Evaluator {
        val cq = TokenQueue(tq.chompBalanced('[', ']')) // content queue
        val key: String = cq.consumeToAny(*AttributeEvals) // eq, not, start, end, contain, match, (no val)
        Validate.notEmpty(key)
        cq.consumeWhitespace()

        return when {
            cq.isEmpty -> {
                if (key.startsWith("^")) {
                    AttributeStarting(key.substring(1))
                } else {
                    Evaluator.Attribute(key)
                }
            }
            cq.matchChomp("=") -> AttributeWithValue(key, cq.remainder())
            cq.matchChomp("!=") -> AttributeWithValueNot(key, cq.remainder())
            cq.matchChomp("^=") -> AttributeWithValueStarting(key, cq.remainder())
            cq.matchChomp("$=") -> AttributeWithValueEnding(key, cq.remainder())
            cq.matchChomp("*=") -> AttributeWithValueContaining(key, cq.remainder())
            cq.matchChomp("~=") -> AttributeWithValueMatching(key, Regex(cq.remainder()))
            else -> throw SelectorParseException("Could not parse attribute query '$query': unexpected token at '$cq.remainder()'")
        }
    }

    private fun cssNthChild(backwards: Boolean, ofType: Boolean): Evaluator {
        val arg: String = Normalizer.normalize(consumeParens())
        val mAB = NTH_AB.find(arg)
        val mB = NTH_B.find(arg)
        val a: Int
        val b: Int
        if ("odd" == arg) {
            a = 2
            b = 1
        } else if ("even" == arg) {
            a = 2
            b = 0
        } else if (mAB != null) {
            a = mAB.groups[1]?.value?.replaceFirst("^\\+".toRegex(), "")?.toInt() ?: 1
            b = mAB.groups[4]?.value?.replaceFirst("^\\+".toRegex(), "")?.toInt() ?: 0
        } else if (mB != null) {
            a = 0
            b = mB.value.replaceFirst("^\\+".toRegex(), "").toInt() // TODO: is .value correct or should i use .groupValues[0]?
        } else {
            throw SelectorParseException("Could not parse nth-index '$arg': unexpected format")
        }
        val eval: Evaluator
        if (ofType) if (backwards) eval = IsNthLastOfType(a, b) else eval = IsNthOfType(a, b) else {
            if (backwards) eval = IsNthLastChild(a, b) else eval = IsNthChild(a, b)
        }
        return eval
    }

    private fun consumeParens(): String {
        return tq.chompBalanced('(', ')')
    }

    private fun consumeIndex(): Int {
        val index = consumeParens().trim { it <= ' ' }
        Validate.isTrue(StringUtil.isNumeric(index), "Index must be numeric")
        return index.toInt()
    }

    // pseudo selector :has(el)
    private fun has(): Evaluator {
        val subQuery = consumeParens()
        Validate.notEmpty(subQuery, ":has(selector) sub-select must not be empty")
        return Has(parse(subQuery))
    }

    // pseudo selector :contains(text), containsOwn(text)
    private fun contains(own: Boolean): Evaluator {
        val query = if (own) ":containsOwn" else ":contains"
        val searchText = TokenQueue.unescape(consumeParens())
        Validate.notEmpty(searchText, "$query(text) query must not be empty")
        return if (own) ContainsOwnText(searchText) else ContainsText(searchText)
    }

    private fun containsWholeText(own: Boolean): Evaluator {
        val query = if (own) ":containsWholeOwnText" else ":containsWholeText"
        val searchText = TokenQueue.unescape(consumeParens())
        Validate.notEmpty(searchText, "$query(text) query must not be empty")
        return if (own) ContainsWholeOwnText(searchText) else ContainsWholeText(searchText)
    }

    // pseudo selector :containsData(data)
    private fun containsData(): Evaluator {
        val searchText = TokenQueue.unescape(consumeParens())
        Validate.notEmpty(searchText, ":containsData(text) query must not be empty")
        return ContainsData(searchText)
    }

    // :matches(regex), matchesOwn(regex)
    private fun matches(own: Boolean): Evaluator {
        val query = if (own) ":matchesOwn" else ":matches"
        val regex = consumeParens() // don't unescape, as regex bits will be escaped
        Validate.notEmpty(regex, "$query(regex) query must not be empty")
        return if (own) MatchesOwn(Regex(regex)) else Matches(
            Regex(
                regex
            )
        )
    }

    // :matches(regex), matchesOwn(regex)
    private fun matchesWholeText(own: Boolean): Evaluator {
        val query = if (own) ":matchesWholeOwnText" else ":matchesWholeText"
        val regex = consumeParens() // don't unescape, as regex bits will be escaped
        Validate.notEmpty(regex, "$query(regex) query must not be empty")
        return if (own) MatchesWholeOwnText(Regex(regex)) else MatchesWholeText(
            Regex(
                regex
            )
        )
    }

    // :not(selector)
    private operator fun not(): Evaluator {
        val subQuery = consumeParens()
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty")
        return Not(parse(subQuery))
    }

    override fun toString(): String {
        return query
    }

    companion object {
        private val Combinators = charArrayOf(',', '>', '+', '~', ' ')
        private val AttributeEvals = arrayOf("=", "!=", "^=", "$=", "*=", "~=")

        /**
         * Parse a CSS query into an Evaluator.
         * @param query CSS query
         * @return Evaluator
         * @see Selector selector query syntax
         */
        @JvmStatic
        fun parse(query: String): Evaluator {
            return try {
                val p = QueryParser(query)
                p.parse()
            } catch (e: IllegalArgumentException) {
                throw SelectorParseException(e.message)
            }
        }

        //pseudo selectors :first-child, :last-child, :nth-child, ...
        private val NTH_AB = Regex("(([+-])?(\\d+)?)n(\\s*([+-])?\\s*\\d+)?", RegexOption.IGNORE_CASE)
        private val NTH_B = Regex("([+-])?(\\d+)")
    }
}
