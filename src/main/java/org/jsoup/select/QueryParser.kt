package org.jsoup.select

import org.jsoup.helper.Validate
import org.jsoup.internal.Normalizer
import org.jsoup.internal.StringUtil
import org.jsoup.parser.TokenQueue
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Parses a CSS selector into an Evaluator tree.
 */
class QueryParser private constructor(query: String?) {
    private val tq: TokenQueue
    private val query: String
    private val evals: MutableList<Evaluator?> = ArrayList()

    /**
     * Parse the query
     * @return Evaluator
     */
    fun parse(): Evaluator? {
        tq.consumeWhitespace()
        if (tq.matchesAny(*Combinators)) { // if starts with a combinator, use root as elements
            evals.add(StructuralEvaluator.Root())
            combinator(tq.consume())
        } else {
            evals.add(consumeEvaluator())
        }
        while (!tq.isEmpty()) {
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
        if (evals.size == 1) return evals.get(0)
        return CombiningEvaluator.And(evals)
    }

    private fun combinator(combinator: Char) {
        tq.consumeWhitespace()
        val subQuery: String? = consumeSubQuery() // support multi > childs
        var rootEval: Evaluator? // the new topmost evaluator
        var currentEval: Evaluator? // the evaluator the new eval will be combined to. could be root, or rightmost or.
        val newEval: Evaluator? = parse(subQuery) // the evaluator to add into target evaluator
        var replaceRightMost: Boolean = false
        if (evals.size == 1) {
            currentEval = evals.get(0)
            rootEval = currentEval
            // make sure OR (,) has precedence:
            if (rootEval is CombiningEvaluator.Or && combinator != ',') {
                currentEval = (currentEval as CombiningEvaluator.Or?).rightMostEvaluator()
                assert(
                    currentEval != null // rightMost signature can return null (if none set), but always will have one by this point
                )
                replaceRightMost = true
            }
        } else {
            currentEval = CombiningEvaluator.And(evals)
            rootEval = currentEval
        }
        evals.clear()
        when (combinator) {
            '>' -> currentEval = CombiningEvaluator.And(StructuralEvaluator.ImmediateParent(currentEval), newEval)
            ' ' -> currentEval = CombiningEvaluator.And(StructuralEvaluator.Parent(currentEval), newEval)
            '+' -> currentEval =
                CombiningEvaluator.And(StructuralEvaluator.ImmediatePreviousSibling(currentEval), newEval)

            '~' -> currentEval = CombiningEvaluator.And(StructuralEvaluator.PreviousSibling(currentEval), newEval)
            ',' -> {
                val or: CombiningEvaluator.Or
                if (currentEval is CombiningEvaluator.Or) {
                    or = currentEval as CombiningEvaluator.Or
                } else {
                    or = CombiningEvaluator.Or()
                    or.add(currentEval)
                }
                or.add(newEval)
                currentEval = or
            }

            else -> throw Selector.SelectorParseException("Unknown combinator '%s'", combinator)
        }
        if (replaceRightMost) (rootEval as CombiningEvaluator.Or?).replaceRightMostEvaluator(currentEval) else rootEval =
            currentEval
        evals.add(rootEval)
    }

    private fun consumeSubQuery(): String? {
        val sq: StringBuilder? = StringUtil.borrowBuilder()
        while (!tq.isEmpty()) {
            if (tq.matches("(")) sq.append("(").append(tq.chompBalanced('(', ')'))
                .append(")") else if (tq.matches("[")) sq.append("[").append(tq.chompBalanced('[', ']'))
                .append("]") else if (tq.matchesAny(*Combinators)) if (sq.length > 0) break else tq.consume() else sq.append(
                tq.consume()
            )
        }
        return StringUtil.releaseBuilder(sq)
    }

    private fun consumeEvaluator(): Evaluator {
        if (tq.matchChomp("#")) return byId() else if (tq.matchChomp(".")) return byClass() else if (tq.matchesWord() || tq.matches(
                "*|"
            )
        ) return byTag() else if (tq.matches("[")) return byAttribute() else if (tq.matchChomp("*")) return Evaluator.AllElements() else if (tq.matchChomp(
                ":"
            )
        ) return parsePseudoSelector() else  // unhandled
            throw Selector.SelectorParseException(
                "Could not parse query '%s': unexpected token at '%s'",
                query,
                tq.remainder()
            )
    }

    private fun parsePseudoSelector(): Evaluator {
        val pseudo: String? = tq.consumeCssIdentifier()
        when (pseudo) {
            "lt" -> return Evaluator.IndexLessThan(consumeIndex())
            "gt" -> return Evaluator.IndexGreaterThan(consumeIndex())
            "eq" -> return Evaluator.IndexEquals(consumeIndex())
            "has" -> return has()
            "contains" -> return contains(false)
            "containsOwn" -> return contains(true)
            "containsWholeText" -> return containsWholeText(false)
            "containsWholeOwnText" -> return containsWholeText(true)
            "containsData" -> return containsData()
            "matches" -> return matches(false)
            "matchesOwn" -> return matches(true)
            "matchesWholeText" -> return matchesWholeText(false)
            "matchesWholeOwnText" -> return matchesWholeText(true)
            "not" -> return not()
            "nth-child" -> return cssNthChild(false, false)
            "nth-last-child" -> return cssNthChild(true, false)
            "nth-of-type" -> return cssNthChild(false, true)
            "nth-last-of-type" -> return cssNthChild(true, true)
            "first-child" -> return Evaluator.IsFirstChild()
            "last-child" -> return Evaluator.IsLastChild()
            "first-of-type" -> return Evaluator.IsFirstOfType()
            "last-of-type" -> return Evaluator.IsLastOfType()
            "only-child" -> return Evaluator.IsOnlyChild()
            "only-of-type" -> return Evaluator.IsOnlyOfType()
            "empty" -> return Evaluator.IsEmpty()
            "root" -> return Evaluator.IsRoot()
            "matchText" -> return Evaluator.MatchText()
            else -> throw Selector.SelectorParseException(
                "Could not parse query '%s': unexpected token at '%s'",
                query,
                tq.remainder()
            )
        }
    }

    private fun byId(): Evaluator {
        val id: String? = tq.consumeCssIdentifier()
        Validate.notEmpty(id)
        return Evaluator.Id(id)
    }

    private fun byClass(): Evaluator {
        val className: String? = tq.consumeCssIdentifier()
        Validate.notEmpty(className)
        return Evaluator.Class(className.trim({ it <= ' ' }))
    }

    private fun byTag(): Evaluator {
        // todo - these aren't dealing perfectly with case sensitivity. For case sensitive parsers, we should also make
        // the tag in the selector case-sensitive (and also attribute names). But for now, normalize (lower-case) for
        // consistency - both the selector and the element tag
        var tagName: String? = Normalizer.normalize(tq.consumeElementSelector())
        Validate.notEmpty(tagName)
        val eval: Evaluator

        // namespaces: wildcard match equals(tagName) or ending in ":"+tagName
        if (tagName.startsWith("*|")) {
            val plainTag: String = tagName.substring(2) // strip *|
            eval = CombiningEvaluator.Or(
                Evaluator.Tag(plainTag),
                Evaluator.TagEndsWith(tagName.replace("*|", ":"))
            )
        } else {
            // namespaces: if element name is "abc:def", selector must be "abc|def", so flip:
            if (tagName.contains("|")) tagName = tagName.replace("|", ":")
            eval = Evaluator.Tag(tagName)
        }
        return eval
    }

    private fun byAttribute(): Evaluator {
        val cq: TokenQueue = TokenQueue(tq.chompBalanced('[', ']')) // content queue
        val key: String? = cq.consumeToAny(*AttributeEvals) // eq, not, start, end, contain, match, (no val)
        Validate.notEmpty(key)
        cq.consumeWhitespace()
        val eval: Evaluator
        if (cq.isEmpty()) {
            if (key.startsWith("^")) eval = Evaluator.AttributeStarting(key.substring(1)) else eval =
                Evaluator.Attribute(key)
        } else {
            if (cq.matchChomp("=")) eval =
                Evaluator.AttributeWithValue(key, cq.remainder()) else if (cq.matchChomp("!=")) eval =
                Evaluator.AttributeWithValueNot(key, cq.remainder()) else if (cq.matchChomp("^=")) eval =
                Evaluator.AttributeWithValueStarting(key, cq.remainder()) else if (cq.matchChomp("$=")) eval =
                Evaluator.AttributeWithValueEnding(key, cq.remainder()) else if (cq.matchChomp("*=")) eval =
                Evaluator.AttributeWithValueContaining(key, cq.remainder()) else if (cq.matchChomp("~=")) eval =
                Evaluator.AttributeWithValueMatching(
                    key,
                    Pattern.compile(cq.remainder())
                ) else throw Selector.SelectorParseException(
                "Could not parse attribute query '%s': unexpected token at '%s'",
                query,
                cq.remainder()
            )
        }
        return eval
    }

    /**
     * Create a new QueryParser.
     * @param query CSS query
     */
    init {
        var query: String? = query
        Validate.notEmpty(query)
        query = query.trim({ it <= ' ' })
        this.query = query
        tq = TokenQueue(query)
    }

    private fun cssNthChild(backwards: Boolean, ofType: Boolean): Evaluator {
        val arg: String? = Normalizer.normalize(consumeParens())
        val mAB: Matcher = NTH_AB.matcher(arg)
        val mB: Matcher = NTH_B.matcher(arg)
        val a: Int
        val b: Int
        if (("odd" == arg)) {
            a = 2
            b = 1
        } else if (("even" == arg)) {
            a = 2
            b = 0
        } else if (mAB.matches()) {
            a = if (mAB.group(3) != null) mAB.group(1).replaceFirst("^\\+".toRegex(), "").toInt() else 1
            b = if (mAB.group(4) != null) mAB.group(4).replaceFirst("^\\+".toRegex(), "").toInt() else 0
        } else if (mB.matches()) {
            a = 0
            b = mB.group().replaceFirst("^\\+".toRegex(), "").toInt()
        } else {
            throw Selector.SelectorParseException("Could not parse nth-index '%s': unexpected format", arg)
        }
        val eval: Evaluator
        if (ofType) if (backwards) eval = Evaluator.IsNthLastOfType(a, b) else eval = Evaluator.IsNthOfType(a, b) else {
            if (backwards) eval = (Evaluator.IsNthLastChild(a, b)) else eval = Evaluator.IsNthChild(a, b)
        }
        return eval
    }

    private fun consumeParens(): String? {
        return tq.chompBalanced('(', ')')
    }

    private fun consumeIndex(): Int {
        val index: String = consumeParens().trim({ it <= ' ' })
        Validate.isTrue(StringUtil.isNumeric(index), "Index must be numeric")
        return index.toInt()
    }

    // pseudo selector :has(el)
    private fun has(): Evaluator {
        val subQuery: String? = consumeParens()
        Validate.notEmpty(subQuery, ":has(selector) sub-select must not be empty")
        return StructuralEvaluator.Has(parse(subQuery))
    }

    // pseudo selector :contains(text), containsOwn(text)
    private fun contains(own: Boolean): Evaluator {
        val query: String = if (own) ":containsOwn" else ":contains"
        val searchText: String? = TokenQueue.Companion.unescape(consumeParens())
        Validate.notEmpty(searchText, query + "(text) query must not be empty")
        return if (own) Evaluator.ContainsOwnText(searchText) else Evaluator.ContainsText(searchText)
    }

    private fun containsWholeText(own: Boolean): Evaluator {
        val query: String = if (own) ":containsWholeOwnText" else ":containsWholeText"
        val searchText: String? = TokenQueue.Companion.unescape(consumeParens())
        Validate.notEmpty(searchText, query + "(text) query must not be empty")
        return if (own) Evaluator.ContainsWholeOwnText(searchText) else Evaluator.ContainsWholeText(searchText)
    }

    // pseudo selector :containsData(data)
    private fun containsData(): Evaluator {
        val searchText: String? = TokenQueue.Companion.unescape(consumeParens())
        Validate.notEmpty(searchText, ":containsData(text) query must not be empty")
        return Evaluator.ContainsData(searchText)
    }

    // :matches(regex), matchesOwn(regex)
    private fun matches(own: Boolean): Evaluator {
        val query: String = if (own) ":matchesOwn" else ":matches"
        val regex: String? = consumeParens() // don't unescape, as regex bits will be escaped
        Validate.notEmpty(regex, query + "(regex) query must not be empty")
        return if (own) Evaluator.MatchesOwn(Pattern.compile(regex)) else Evaluator.Matches(Pattern.compile(regex))
    }

    // :matches(regex), matchesOwn(regex)
    private fun matchesWholeText(own: Boolean): Evaluator {
        val query: String = if (own) ":matchesWholeOwnText" else ":matchesWholeText"
        val regex: String? = consumeParens() // don't unescape, as regex bits will be escaped
        Validate.notEmpty(regex, query + "(regex) query must not be empty")
        return if (own) Evaluator.MatchesWholeOwnText(Pattern.compile(regex)) else Evaluator.MatchesWholeText(
            Pattern.compile(
                regex
            )
        )
    }

    // :not(selector)
    private operator fun not(): Evaluator {
        val subQuery: String? = consumeParens()
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty")
        return StructuralEvaluator.Not(parse(subQuery))
    }

    public override fun toString(): String {
        return query
    }

    companion object {
        private val Combinators: CharArray = charArrayOf(',', '>', '+', '~', ' ')
        private val AttributeEvals: Array<String> = arrayOf("=", "!=", "^=", "$=", "*=", "~=")

        /**
         * Parse a CSS query into an Evaluator.
         * @param query CSS query
         * @return Evaluator
         * @see Selector selector query syntax
         */
        @JvmStatic
        fun parse(query: String?): Evaluator? {
            try {
                val p: QueryParser = QueryParser(query)
                return p.parse()
            } catch (e: IllegalArgumentException) {
                throw Selector.SelectorParseException(e.message)
            }
        }

        //pseudo selectors :first-child, :last-child, :nth-child, ...
        private val NTH_AB: Pattern =
            Pattern.compile("(([+-])?(\\d+)?)n(\\s*([+-])?\\s*\\d+)?", Pattern.CASE_INSENSITIVE)
        private val NTH_B: Pattern = Pattern.compile("([+-])?(\\d+)")
    }
}
