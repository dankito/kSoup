package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.nodes.Comment
import net.dankito.ksoup.nodes.Document
import net.dankito.ksoup.nodes.TextNode
import net.dankito.ksoup.parser.ParseErrorList.Companion.tracking
import net.dankito.ksoup.parser.Parser.Companion.parseFragment
import net.dankito.ksoup.select.Elements
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class TokeniserStateTest {
    val whiteSpace = charArrayOf('\t', '\n', '\r', '\u000C', ' ')
    val quote = charArrayOf('\'', '"')
    @Test
    fun ensureSearchArraysAreSorted() {
        val arrays = arrayOf<CharArray>()
        for (array in arrays) {
            val copy = Arrays.copyOf(array, array.size)
            Arrays.sort(array)
            Assertions.assertArrayEquals(array, copy)
        }
    }

    @Test
    fun testCharacterReferenceInRcdata() {
        val body = "<textarea>You&I</textarea>"
        val doc = Jsoup.parse(body)
        val els = doc.select("textarea")
        Assertions.assertEquals("You&I", els.text())
    }

    @Test
    fun testBeforeTagName() {
        for (c in whiteSpace) {
            val body = "<div$c>test</div>"
            val doc = Jsoup.parse(body)
            val els = doc.select("div")
            Assertions.assertEquals("test", els.text())
        }
    }

    @Test
    fun testEndTagOpen() {
        var body: String
        var doc: Document
        var els: Elements
        body = "<div>hello world</"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("hello world</", els.text())
        body = "<div>hello world</div>"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("hello world", els.text())
        body = "<div>fake</></div>"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("fake", els.text())
        body = "<div>fake</?</div>"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("fake", els.text())
    }

    @Test
    fun testRcdataLessthanSign() {
        var body: String
        var doc: Document
        var els: Elements
        body = "<textarea><fake></textarea>"
        doc = Jsoup.parse(body)
        els = doc.select("textarea")
        Assertions.assertEquals("<fake>", els.text())
        body = "<textarea><open"
        doc = Jsoup.parse(body)
        els = doc.select("textarea")
        Assertions.assertEquals("", els.text())
        body = "<textarea>hello world</?fake</textarea>"
        doc = Jsoup.parse(body)
        els = doc.select("textarea")
        Assertions.assertEquals("hello world</?fake", els.text())
    }

    @Test
    fun testRCDATAEndTagName() {
        for (c in whiteSpace) {
            val body = "<textarea>data</textarea$c>"
            val doc = Jsoup.parse(body)
            val els = doc.select("textarea")
            Assertions.assertEquals("data", els.text())
        }
    }

    @Test
    fun testCommentEndCoverage() {
        val html =
            "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --! --- --><p>Hello</p></body></html>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val comment = body.childNode(1) as Comment
        Assertions.assertEquals(" <table><tr><td></table> --! --- ", comment.data)
        val p = body.child(1)
        val text = p.childNode(0) as TextNode
        Assertions.assertEquals("Hello", text.wholeText)
    }

    @Test
    fun testCommentEndBangCoverage() {
        val html =
            "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --!---!>--><p>Hello</p></body></html>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val comment = body.childNode(1) as Comment
        Assertions.assertEquals(" <table><tr><td></table> --!-", comment.data)
        val p = body.child(1)
        val text = p.childNode(0) as TextNode
        Assertions.assertEquals("Hello", text.wholeText)
    }

    @Test
    fun testPublicIdentifiersWithWhitespace() {
        val expectedOutput = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\">"
        for (q in quote) {
            for (ws in whiteSpace) {
                val htmls = arrayOf(
                    "<!DOCTYPE html${ws}PUBLIC ${q}-//W3C//DTD HTML 4.0//EN${q}>",
                    "<!DOCTYPE html ${ws}PUBLIC ${q}-//W3C//DTD HTML 4.0//EN${q}>",
                    "<!DOCTYPE html PUBLIC${ws}${q}-//W3C//DTD HTML 4.0//EN${q}>",
                    "<!DOCTYPE html PUBLIC ${ws}${q}-//W3C//DTD HTML 4.0//EN${q}>",
                    "<!DOCTYPE html PUBLIC ${q}-//W3C//DTD HTML 4.0//EN${q}${ws}>",
                    "<!DOCTYPE html PUBLIC${q}-//W3C//DTD HTML 4.0//EN${q}${ws}>"
                )
                for (html in htmls) {
                    val doc = Jsoup.parse(html)
                    Assertions.assertEquals(expectedOutput, doc.childNode(0).outerHtml())
                }
            }
        }
    }

    @Test
    fun testSystemIdentifiersWithWhitespace() {
        val expectedOutput = "<!DOCTYPE html SYSTEM \"http://www.w3.org/TR/REC-html40/strict.dtd\">"
        for (q in quote) {
            for (ws in whiteSpace) {
                val htmls = arrayOf(
                    "<!DOCTYPE html${ws}SYSTEM ${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}>",
                    "<!DOCTYPE html ${ws}SYSTEM ${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}>",
                    "<!DOCTYPE html SYSTEM${ws}${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}>",
                    "<!DOCTYPE html SYSTEM ${ws}${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}>",
                    "<!DOCTYPE html SYSTEM ${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}${ws}>",
                    "<!DOCTYPE html SYSTEM${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}${ws}>"
                )
                for (html in htmls) {
                    val doc = Jsoup.parse(html)
                    Assertions.assertEquals(expectedOutput, doc.childNode(0).outerHtml())
                }
            }
        }
    }

    @Test
    fun testPublicAndSystemIdentifiersWithWhitespace() {
        val expectedOutput = ("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\""
                + " \"http://www.w3.org/TR/REC-html40/strict.dtd\">")
        for (q in quote) {
            for (ws in whiteSpace) {
                val htmls = arrayOf(
                    "<!DOCTYPE html PUBLIC ${q}-//W3C//DTD HTML 4.0//EN${q}"
                                + "${ws}${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}>",
                    "<!DOCTYPE html PUBLIC ${q}-//W3C//DTD HTML 4.0//EN${q}"
                                + "${q}http://www.w3.org/TR/REC-html40/strict.dtd${q}>"
                )
                for (html in htmls) {
                    val doc = Jsoup.parse(html)
                    Assertions.assertEquals(expectedOutput, doc.childNode(0).outerHtml())
                }
            }
        }
    }

    @Test
    fun handlesLessInTagThanAsNewTag() {
        // out of spec, but clear author intent
        val html = "<p\n<p<div id=one <span>Two"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            "<p></p><p></p><div id=\"one\"><span>Two</span></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testUnconsumeAtBufferBoundary() {
        val triggeringSnippet = "<a href=\"\"foo"
        val padding =
            CharArray(CharacterReader.readAheadLimit - triggeringSnippet.length + 2) // The "foo" part must be just at the limit.
        Arrays.fill(padding, ' ')
        val paddedSnippet = String(padding) + triggeringSnippet
        val errorList = tracking(1)
        parseFragment(paddedSnippet, null, "", errorList)
        Assertions.assertEquals(CharacterReader.readAheadLimit - 1, errorList[0].position)
    }

    @Test
    fun testUnconsumeAfterBufferUp() {
        // test for after consume() a bufferUp occurs (look-forward) but then attempts to unconsume. Would throw a "No buffer left to unconsume"
        val triggeringSnippet = "<title>One <span>Two"
        val padding =
            CharArray(CharacterReader.readAheadLimit - triggeringSnippet.length + 8) // The "<span" part must be just at the limit. The "containsIgnoreCase" scan does a bufferUp, losing the unconsume
        Arrays.fill(padding, ' ')
        val paddedSnippet = String(padding) + triggeringSnippet
        val errorList = tracking(1)
        parseFragment(paddedSnippet, null, "", errorList)
        // just asserting we don't get a WTF on unconsume
    }

    @Test
    fun testOpeningAngleBracketInsteadOfAttribute() {
        val triggeringSnippet = "<html <"
        val errorList = tracking(1)
        parseFragment(triggeringSnippet, null, "", errorList)
        Assertions.assertEquals(6, errorList[0].position)
    }

    @Test
    fun testMalformedSelfClosingTag() {
        val triggeringSnippet = "<html /ouch"
        val errorList = tracking(1)
        parseFragment(triggeringSnippet, null, "", errorList)
        Assertions.assertEquals(7, errorList[0].position)
    }

    @Test
    fun testOpeningAngleBracketInTagName() {
        val triggeringSnippet = "<html<"
        val errorList = tracking(1)
        parseFragment(triggeringSnippet, null, "", errorList)
        Assertions.assertEquals(5, errorList[0].position)
    }

    @Test
    fun rcData() {
        val doc = Jsoup.parse("<title>One \u0000Two</title>")
        Assertions.assertEquals("One �Two", doc.title())
    }

    @Test
    fun plaintext() {
        val doc = Jsoup.parse("<div>One<plaintext><div>Two</plaintext>\u0000no < Return")
        Assertions.assertEquals(
            "<html><head></head><body><div>One<plaintext>&lt;div&gt;Two&lt;/plaintext&gt;�no &lt; Return</plaintext></div></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun nullInTag() {
        val doc = Jsoup.parse("<di\u0000v>One</di\u0000v>Two")
        Assertions.assertEquals("<di�v>\n One\n</di�v>Two", doc.body().html())
    }

    @Test
    fun attributeValUnquoted() {
        var doc = Jsoup.parse("<p name=foo&lt;bar>")
        val p = doc.selectFirst("p")
        Assertions.assertEquals("foo<bar", p!!.attr("name"))
        doc = Jsoup.parse("<p foo=")
        Assertions.assertEquals("<p foo></p>", doc.body().html())
    }
}
