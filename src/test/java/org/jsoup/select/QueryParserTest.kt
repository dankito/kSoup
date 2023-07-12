package org.jsoup.select

import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Tests for the Selector Query Parser.
 *
 * @author Jonathan Hedley
 */
class QueryParserTest {
    @Test
    fun testConsumeSubQuery() {
        val doc = Jsoup.parse(
            "<html><head>h</head><body>" +
                    "<li><strong>l1</strong></li>" +
                    "<a><li><strong>l2</strong></li></a>" +
                    "<p><strong>yes</strong></p>" +
                    "</body></html>"
        )
        Assertions.assertEquals("l1 l2 yes", doc.body().select(">p>strong,>*>li>strong").text())
        Assertions.assertEquals("l2 yes", doc.select("body>p>strong,body>*>li>strong").text())
        Assertions.assertEquals("yes", doc.select(">body>*>li>strong,>body>p>strong").text())
        Assertions.assertEquals("l2", doc.select(">body>p>strong,>body>*>li>strong").text())
    }

    @Test
    fun testOrGetsCorrectPrecedence() {
        // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
        // top level or, three child ands
        val eval = QueryParser.parse("a b, c d, e f")
        Assertions.assertTrue(eval is CombiningEvaluator.Or)
        val or = eval as CombiningEvaluator.Or
        Assertions.assertEquals(3, or.evaluators.size)
        for (innerEval in or.evaluators) {
            Assertions.assertTrue(innerEval is CombiningEvaluator.And)
            val and = innerEval as CombiningEvaluator.And
            Assertions.assertEquals(2, and.evaluators.size)
            //            assertTrue(and.getEvaluators().get(0) instanceof StructuralEvaluator.Parent); // TODO
            Assertions.assertTrue(and.evaluators[1] is Evaluator.Tag)
        }
    }

    @Test
    fun testParsesMultiCorrectly() {
        val query = ".foo > ol, ol > li + li"
        val eval = QueryParser.parse(query)
        Assertions.assertTrue(eval is CombiningEvaluator.Or)
        val or = eval as CombiningEvaluator.Or
        Assertions.assertEquals(2, or.evaluators.size)
        val andLeft = or.evaluators[0] as CombiningEvaluator.And
        val andRight = or.evaluators[1] as CombiningEvaluator.And
        Assertions.assertEquals(".foo > ol", andLeft.toString())
        Assertions.assertEquals(2, andLeft.evaluators.size)
        Assertions.assertEquals("ol > li + li", andRight.toString())
        Assertions.assertEquals(2, andRight.evaluators.size)
        Assertions.assertEquals(query, eval.toString())
    }

    @Test
    fun exceptionOnUncloseAttribute() {
        Assertions.assertThrows(Selector.SelectorParseException::class.java) { QueryParser.parse("section > a[href=\"]") }
    }

    @Test
    fun testParsesSingleQuoteInContains() {
        Assertions.assertThrows(Selector.SelectorParseException::class.java) { QueryParser.parse("p:contains(One \" One)") }
    }

    @Test
    fun exceptOnEmptySelector() {
        Assertions.assertThrows(Selector.SelectorParseException::class.java) { QueryParser.parse("") }
    }

    @Test
    fun exceptOnNullSelector() {
        Assertions.assertThrows(NullPointerException::class.java) { QueryParser.parse(null as String) }
    }

    @Test
    fun okOnSpacesForeAndAft() {
        val parse = QueryParser.parse(" span div  ")
        Assertions.assertEquals("span div", parse.toString())
    }

    @Test
    fun structuralEvaluatorsToString() {
        val q = "a:not(:has(span.foo)) b d > e + f ~ g"
        val parse = QueryParser.parse(q)
        Assertions.assertEquals(q, parse.toString())
    }
}
