package net.dankito.ksoup.select

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.test.NullPointerExceptionOfPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
        assertEquals("l1 l2 yes", doc.body().select(">p>strong,>*>li>strong").text())
        assertEquals("l2 yes", doc.select("body>p>strong,body>*>li>strong").text())
        assertEquals("yes", doc.select(">body>*>li>strong,>body>p>strong").text())
        assertEquals("l2", doc.select(">body>p>strong,>body>*>li>strong").text())
    }

    @Test
    fun testOrGetsCorrectPrecedence() {
        // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
        // top level or, three child ands
        val eval = QueryParser.parse("a b, c d, e f")
        assertTrue(eval is CombiningEvaluator.Or)
        val or = eval as CombiningEvaluator.Or
        assertEquals(3, or.evaluators.size)
        for (innerEval in or.evaluators) {
            assertTrue(innerEval is CombiningEvaluator.And)
            val and = innerEval as CombiningEvaluator.And
            assertEquals(2, and.evaluators.size)
            //            assertTrue(and.getEvaluators().get(0) instanceof StructuralEvaluator.Parent); // TODO
            assertTrue(and.evaluators[1] is Evaluator.Tag)
        }
    }

    @Test
    fun testParsesMultiCorrectly() {
        val query = ".foo > ol, ol > li + li"
        val eval = QueryParser.parse(query)
        assertTrue(eval is CombiningEvaluator.Or)
        val or = eval as CombiningEvaluator.Or
        assertEquals(2, or.evaluators.size)
        val andLeft = or.evaluators[0] as CombiningEvaluator.And
        val andRight = or.evaluators[1] as CombiningEvaluator.And
        assertEquals(".foo > ol", andLeft.toString())
        assertEquals(2, andLeft.evaluators.size)
        assertEquals("ol > li + li", andRight.toString())
        assertEquals(2, andRight.evaluators.size)
        assertEquals(query, eval.toString())
    }

    @Test
    fun exceptionOnUncloseAttribute() {
        assertFailsWith(Selector.SelectorParseException::class) { QueryParser.parse("section > a[href=\"]") }
    }

    @Test
    fun testParsesSingleQuoteInContains() {
        assertFailsWith(Selector.SelectorParseException::class) { QueryParser.parse("p:contains(One \" One)") }
    }

    @Test
    fun exceptOnEmptySelector() {
        assertFailsWith(Selector.SelectorParseException::class) { QueryParser.parse("") }
    }

    @Test
    fun exceptOnNullSelector() {
        assertFailsWith(NullPointerExceptionOfPlatform) {
            QueryParser.parse(null as String)
        }
    }

    @Test
    fun okOnSpacesForeAndAft() {
        val parse = QueryParser.parse(" span div  ")
        assertEquals("span div", parse.toString())
    }

    @Test
    fun structuralEvaluatorsToString() {
        val q = "a:not(:has(span.foo)) b d > e + f ~ g"
        val parse = QueryParser.parse(q)
        assertEquals(q, parse.toString())
    }
}
