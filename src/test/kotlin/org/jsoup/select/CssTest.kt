package org.jsoup.select

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Tag
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CssTest {
    private var html: Document? = null
    @BeforeEach
    fun init() {
        html = Jsoup.parse(CssTest.Companion.htmlString)
    }

    @Test
    fun firstChild() {
        check(html!!.select("#pseudo :first-child"), "1")
        check(html!!.select("html:first-child"))
    }

    @Test
    fun lastChild() {
        check(html!!.select("#pseudo :last-child"), "10")
        check(html!!.select("html:last-child"))
    }

    @Test
    fun nthChild_simple() {
        for (i in 1..10) {
            check(html!!.select(String.format("#pseudo :nth-child(%d)", i)), i.toString())
        }
    }

    @Test
    fun nthOfType_unknownTag() {
        for (i in 1..10) {
            check(html!!.select(String.format("#type svg:nth-of-type(%d)", i)), i.toString())
        }
    }

    @Test
    fun nthLastChild_simple() {
        for (i in 1..10) {
            check(html!!.select(String.format("#pseudo :nth-last-child(%d)", i)), (11 - i).toString())
        }
    }

    @Test
    fun nthOfType_simple() {
        for (i in 1..10) {
            check(html!!.select(String.format("#type p:nth-of-type(%d)", i)), i.toString())
        }
    }

    @Test
    fun nthLastOfType_simple() {
        for (i in 1..10) {
            check(
                html!!.select(String.format("#type :nth-last-of-type(%d)", i)),
                (11 - i).toString(),
                (11 - i).toString(),
                (11 - i).toString(),
                (11 - i).toString()
            )
        }
    }

    @Test
    fun nthChild_advanced() {
        check(html!!.select("#pseudo :nth-child(-5)"))
        check(html!!.select("#pseudo :nth-child(odd)"), "1", "3", "5", "7", "9")
        check(html!!.select("#pseudo :nth-child(2n-1)"), "1", "3", "5", "7", "9")
        check(html!!.select("#pseudo :nth-child(2n+1)"), "1", "3", "5", "7", "9")
        check(html!!.select("#pseudo :nth-child(2n+3)"), "3", "5", "7", "9")
        check(html!!.select("#pseudo :nth-child(even)"), "2", "4", "6", "8", "10")
        check(html!!.select("#pseudo :nth-child(2n)"), "2", "4", "6", "8", "10")
        check(html!!.select("#pseudo :nth-child(3n-1)"), "2", "5", "8")
        check(html!!.select("#pseudo :nth-child(-2n+5)"), "1", "3", "5")
        check(html!!.select("#pseudo :nth-child(+5)"), "5")
    }

    @Test
    fun nthOfType_advanced() {
        check(html!!.select("#type :nth-of-type(-5)"))
        check(html!!.select("#type p:nth-of-type(odd)"), "1", "3", "5", "7", "9")
        check(html!!.select("#type em:nth-of-type(2n-1)"), "1", "3", "5", "7", "9")
        check(html!!.select("#type p:nth-of-type(2n+1)"), "1", "3", "5", "7", "9")
        check(html!!.select("#type span:nth-of-type(2n+3)"), "3", "5", "7", "9")
        check(html!!.select("#type p:nth-of-type(even)"), "2", "4", "6", "8", "10")
        check(html!!.select("#type p:nth-of-type(2n)"), "2", "4", "6", "8", "10")
        check(html!!.select("#type p:nth-of-type(3n-1)"), "2", "5", "8")
        check(html!!.select("#type p:nth-of-type(-2n+5)"), "1", "3", "5")
        check(html!!.select("#type :nth-of-type(+5)"), "5", "5", "5", "5")
    }

    @Test
    fun nthLastChild_advanced() {
        check(html!!.select("#pseudo :nth-last-child(-5)"))
        check(html!!.select("#pseudo :nth-last-child(odd)"), "2", "4", "6", "8", "10")
        check(html!!.select("#pseudo :nth-last-child(2n-1)"), "2", "4", "6", "8", "10")
        check(html!!.select("#pseudo :nth-last-child(2n+1)"), "2", "4", "6", "8", "10")
        check(html!!.select("#pseudo :nth-last-child(2n+3)"), "2", "4", "6", "8")
        check(html!!.select("#pseudo :nth-last-child(even)"), "1", "3", "5", "7", "9")
        check(html!!.select("#pseudo :nth-last-child(2n)"), "1", "3", "5", "7", "9")
        check(html!!.select("#pseudo :nth-last-child(3n-1)"), "3", "6", "9")
        check(html!!.select("#pseudo :nth-last-child(-2n+5)"), "6", "8", "10")
        check(html!!.select("#pseudo :nth-last-child(+5)"), "6")
    }

    @Test
    fun nthLastOfType_advanced() {
        check(html!!.select("#type :nth-last-of-type(-5)"))
        check(html!!.select("#type p:nth-last-of-type(odd)"), "2", "4", "6", "8", "10")
        check(html!!.select("#type em:nth-last-of-type(2n-1)"), "2", "4", "6", "8", "10")
        check(html!!.select("#type p:nth-last-of-type(2n+1)"), "2", "4", "6", "8", "10")
        check(html!!.select("#type span:nth-last-of-type(2n+3)"), "2", "4", "6", "8")
        check(html!!.select("#type p:nth-last-of-type(even)"), "1", "3", "5", "7", "9")
        check(html!!.select("#type p:nth-last-of-type(2n)"), "1", "3", "5", "7", "9")
        check(html!!.select("#type p:nth-last-of-type(3n-1)"), "3", "6", "9")
        check(html!!.select("#type span:nth-last-of-type(-2n+5)"), "6", "8", "10")
        check(html!!.select("#type :nth-last-of-type(+5)"), "6", "6", "6", "6")
    }

    @Test
    fun firstOfType() {
        check(html!!.select("div:not(#only) :first-of-type"), "1", "1", "1", "1", "1")
    }

    @Test
    fun lastOfType() {
        check(html!!.select("div:not(#only) :last-of-type"), "10", "10", "10", "10", "10")
    }

    @Test
    fun empty() {
        val sel = html!!.select(":empty")
        Assertions.assertEquals(3, sel.size)
        Assertions.assertEquals("head", sel[0].tagName())
        Assertions.assertEquals("br", sel[1].tagName())
        Assertions.assertEquals("p", sel[2].tagName())
    }

    @Test
    fun onlyChild() {
        val sel = html!!.select("span :only-child")
        Assertions.assertEquals(1, sel.size)
        Assertions.assertEquals("br", sel[0].tagName())
        check(html!!.select("#only :only-child"), "only")
    }

    @Test
    fun onlyOfType() {
        val sel = html!!.select(":only-of-type")
        Assertions.assertEquals(6, sel.size)
        Assertions.assertEquals("head", sel[0].tagName())
        Assertions.assertEquals("body", sel[1].tagName())
        Assertions.assertEquals("span", sel[2].tagName())
        Assertions.assertEquals("br", sel[3].tagName())
        Assertions.assertEquals("p", sel[4].tagName())
        Assertions.assertTrue(sel[4].hasClass("empty"))
        Assertions.assertEquals("em", sel[5].tagName())
    }

    protected fun check(result: Elements, vararg expectedContent: String?) {
        Assertions.assertEquals(expectedContent.size, result.size, "Number of elements")
        for (i in expectedContent.indices) {
            Assertions.assertNotNull(result[i])
            Assertions.assertEquals(expectedContent[i], result[i].ownText(), "Expected element")
        }
    }

    @Test
    fun root() {
        val sel = html!!.select(":root")
        Assertions.assertEquals(1, sel.size)
        Assertions.assertNotNull(sel[0])
        Assertions.assertEquals(Tag.valueOf("html"), sel[0].tag())
        val sel2 = html!!.select("body").select(":root")
        Assertions.assertEquals(1, sel2.size)
        Assertions.assertNotNull(sel2[0])
        Assertions.assertEquals(Tag.valueOf("body"), sel2[0].tag())
    }

    companion object {
        private val htmlString: String

        init {
            val sb = StringBuilder("<html><head></head><body>")
            sb.append("<div id='pseudo'>")
            for (i in 1..10) {
                sb.append(String.format("<p>%d</p>", i))
            }
            sb.append("</div>")
            sb.append("<div id='type'>")
            for (i in 1..10) {
                sb.append(String.format("<p>%d</p>", i))
                sb.append(String.format("<span>%d</span>", i))
                sb.append(String.format("<em>%d</em>", i))
                sb.append(String.format("<svg>%d</svg>", i))
            }
            sb.append("</div>")
            sb.append("<span id='onlySpan'><br /></span>")
            sb.append("<p class='empty'><!-- Comment only is still empty! --></p>")
            sb.append("<div id='only'>")
            sb.append("Some text before the <em>only</em> child in this div")
            sb.append("</div>")
            sb.append("</body></html>")

            CssTest.Companion.htmlString = sb.toString()
        }
    }
}
