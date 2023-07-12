package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

/**
 * Token queue tests.
 */
class TokenQueueTest {
    @Test
    fun chompBalanced() {
        val tq = TokenQueue(":contains(one (two) three) four")
        val pre = tq.consumeTo("(")
        val guts = tq.chompBalanced('(', ')')
        val remainder = tq.remainder()
        Assertions.assertEquals(":contains", pre)
        Assertions.assertEquals("one (two) three", guts)
        Assertions.assertEquals(" four", remainder)
    }

    @Test
    fun chompEscapedBalanced() {
        val tq = TokenQueue(":contains(one (two) \\( \\) \\) three) four")
        val pre = tq.consumeTo("(")
        val guts = tq.chompBalanced('(', ')')
        val remainder = tq.remainder()
        Assertions.assertEquals(":contains", pre)
        Assertions.assertEquals("one (two) \\( \\) \\) three", guts)
        Assertions.assertEquals("one (two) ( ) ) three", TokenQueue.unescape(guts))
        Assertions.assertEquals(" four", remainder)
    }

    @Test
    fun chompBalancedMatchesAsMuchAsPossible() {
        val tq = TokenQueue("unbalanced(something(or another)) else")
        tq.consumeTo("(")
        val match = tq.chompBalanced('(', ')')
        Assertions.assertEquals("something(or another)", match)
    }

    @Test
    fun unescape() {
        Assertions.assertEquals("one ( ) \\", TokenQueue.unescape("one \\( \\) \\\\"))
    }

    @Test
    fun unescape_2() {
        Assertions.assertEquals("\\&", TokenQueue.unescape("\\\\\\&"))
    }

    @Test
    fun escapeCssIdentifier() {
        Assertions.assertEquals("one\\#two\\.three\\/four\\\\five", TokenQueue.escapeCssIdentifier("one#two.three/four\\five"))
    }

    @Test
    fun chompToIgnoreCase() {
        val t = "<textarea>one < two </TEXTarea>"
        var tq = TokenQueue(t)
        var data = tq.chompToIgnoreCase("</textarea")
        Assertions.assertEquals("<textarea>one < two ", data)
        tq = TokenQueue("<textarea> one two < three </oops>")
        data = tq.chompToIgnoreCase("</textarea")
        Assertions.assertEquals("<textarea> one two < three </oops>", data)
    }

    @Test
    fun addFirst() {
        val tq = TokenQueue("One Two")
        tq.consumeWord()
        tq.addFirst("Three")
        Assertions.assertEquals("Three Two", tq.remainder())
    }

    @Test
    fun consumeToIgnoreSecondCallTest() {
        val t = "<textarea>one < two </TEXTarea> third </TEXTarea>"
        val tq = TokenQueue(t)
        var data = tq.chompToIgnoreCase("</textarea>")
        Assertions.assertEquals("<textarea>one < two ", data)
        data = tq.chompToIgnoreCase("</textarea>")
        Assertions.assertEquals(" third ", data)
    }

    @Test
    fun testNestedQuotes() {
        TokenQueueTest.Companion.validateNestedQuotes(
            "<html><body><a id=\"identifier\" onclick=\"func('arg')\" /></body></html>",
            "a[onclick*=\"('arg\"]"
        )
        TokenQueueTest.Companion.validateNestedQuotes(
            "<html><body><a id=\"identifier\" onclick=func('arg') /></body></html>",
            "a[onclick*=\"('arg\"]"
        )
        TokenQueueTest.Companion.validateNestedQuotes(
            "<html><body><a id=\"identifier\" onclick='func(\"arg\")' /></body></html>",
            "a[onclick*='(\"arg']"
        )
        TokenQueueTest.Companion.validateNestedQuotes(
            "<html><body><a id=\"identifier\" onclick=func(\"arg\") /></body></html>",
            "a[onclick*='(\"arg']"
        )
    }

    @Test
    fun chompBalancedThrowIllegalArgumentException() {
        try {
            val tq = TokenQueue("unbalanced(something(or another)) else")
            tq.consumeTo("(")
            tq.chompBalanced('(', '+')
            Assertions.fail<Any>("should have thrown IllegalArgumentException")
        } catch (expected: IllegalArgumentException) {
            Assertions.assertEquals("Did not find balanced marker at 'something(or another)) else'", expected.message)
        }
    }

    @Test
    fun testQuotedPattern() {
        val doc = Jsoup.parse("<div>\\) foo1</div><div>( foo2</div><div>1) foo3</div>")
        Assertions.assertEquals(
            "\n\\) foo1",
            doc.select("div:matches(" + Pattern.quote("\\)") + ")")[0].childNode(0).toString()
        )
        Assertions.assertEquals(
            "\n( foo2",
            doc.select("div:matches(" + Pattern.quote("(") + ")")[0].childNode(0).toString()
        )
        Assertions.assertEquals(
            "\n1) foo3",
            doc.select("div:matches(" + Pattern.quote("1)") + ")")[0].childNode(0).toString()
        )
    }

    @Test
    fun consumeEscapedTag() {
        val q = TokenQueue("p\\\\p p\\.p p\\:p p\\!p")
        Assertions.assertEquals("p\\p", q.consumeElementSelector())
        Assertions.assertTrue(q.consumeWhitespace())
        Assertions.assertEquals("p.p", q.consumeElementSelector())
        Assertions.assertTrue(q.consumeWhitespace())
        Assertions.assertEquals("p:p", q.consumeElementSelector())
        Assertions.assertTrue(q.consumeWhitespace())
        Assertions.assertEquals("p!p", q.consumeElementSelector())
        Assertions.assertTrue(q.isEmpty)
    }

    @Test
    fun consumeEscapedId() {
        val q = TokenQueue("i\\.d i\\\\d")
        Assertions.assertEquals("i.d", q.consumeCssIdentifier())
        Assertions.assertTrue(q.consumeWhitespace())
        Assertions.assertEquals("i\\d", q.consumeCssIdentifier())
        Assertions.assertTrue(q.isEmpty)
    }

    companion object {
        private fun validateNestedQuotes(html: String, selector: String) {
            Assertions.assertEquals(
                "#identifier", Jsoup.parse(html).select(selector).first()!!
                    .cssSelector()
            )
        }
    }
}
