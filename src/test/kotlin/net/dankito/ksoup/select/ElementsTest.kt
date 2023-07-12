package net.dankito.ksoup.select

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.nodes.Node
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Tests for ElementList.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class ElementsTest {
    @Test
    fun filter() {
        val h =
            "<p>Excl</p><div class=headline><p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>"
        val doc = Jsoup.parse(h)
        val els = doc.select(".headline").select("p")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("Hello", els[0].text())
        Assertions.assertEquals("There", els[1].text())
    }

    @Test
    fun attributes() {
        val h = "<p title=foo><p title=bar><p class=foo><p class=bar>"
        val doc = Jsoup.parse(h)
        val withTitle = doc.select("p[title]")
        Assertions.assertEquals(2, withTitle.size)
        Assertions.assertTrue(withTitle.hasAttr("title"))
        Assertions.assertFalse(withTitle.hasAttr("class"))
        Assertions.assertEquals("foo", withTitle.attr("title"))
        withTitle.removeAttr("title")
        Assertions.assertEquals(2, withTitle.size) // existing Elements are not reevaluated
        Assertions.assertEquals(0, doc.select("p[title]").size)
        val ps = doc.select("p").attr("style", "classy")
        Assertions.assertEquals(4, ps.size)
        Assertions.assertEquals("classy", ps.last()!!.attr("style"))
        Assertions.assertEquals("bar", ps.last()!!.attr("class"))
    }

    @Test
    fun hasAttr() {
        val doc = Jsoup.parse("<p title=foo><p title=bar><p class=foo><p class=bar>")
        val ps = doc.select("p")
        Assertions.assertTrue(ps.hasAttr("class"))
        Assertions.assertFalse(ps.hasAttr("style"))
    }

    @Test
    fun hasAbsAttr() {
        val doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org'>Two</a>")
        val one = doc.select("#1")
        val two = doc.select("#2")
        val both = doc.select("a")
        Assertions.assertFalse(one.hasAttr("abs:href"))
        Assertions.assertTrue(two.hasAttr("abs:href"))
        Assertions.assertTrue(both.hasAttr("abs:href")) // hits on #2
    }

    @Test
    fun attr() {
        val doc = Jsoup.parse("<p title=foo><p title=bar><p class=foo><p class=bar>")
        val classVal = doc.select("p").attr("class")
        Assertions.assertEquals("foo", classVal)
    }

    @Test
    fun absAttr() {
        val doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org'>Two</a>")
        val one = doc.select("#1")
        val two = doc.select("#2")
        val both = doc.select("a")
        Assertions.assertEquals("", one.attr("abs:href"))
        Assertions.assertEquals("https://jsoup.org", two.attr("abs:href"))
        Assertions.assertEquals("https://jsoup.org", both.attr("abs:href"))
    }

    @Test
    fun classes() {
        val doc = Jsoup.parse("<div><p class='mellow yellow'></p><p class='red green'></p>")
        val els = doc.select("p")
        Assertions.assertTrue(els.hasClass("red"))
        Assertions.assertFalse(els.hasClass("blue"))
        els.addClass("blue")
        els.removeClass("yellow")
        els.toggleClass("mellow")
        Assertions.assertEquals("blue", els[0].className())
        Assertions.assertEquals("red green blue mellow", els[1].className())
    }

    @Test
    fun hasClassCaseInsensitive() {
        val els = Jsoup.parse("<p Class=One>One <p class=Two>Two <p CLASS=THREE>THREE").select("p")
        val one = els[0]
        val two = els[1]
        val thr = els[2]
        Assertions.assertTrue(one.hasClass("One"))
        Assertions.assertTrue(one.hasClass("ONE"))
        Assertions.assertTrue(two.hasClass("TWO"))
        Assertions.assertTrue(two.hasClass("Two"))
        Assertions.assertTrue(thr.hasClass("ThreE"))
        Assertions.assertTrue(thr.hasClass("three"))
    }

    @Test
    fun text() {
        val h = "<div><p>Hello<p>there<p>world</div>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("Hello there world", doc.select("div > *").text())
    }

    @Test
    fun hasText() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div><p></p></div>")
        val divs = doc.select("div")
        Assertions.assertTrue(divs.hasText())
        Assertions.assertFalse(doc.select("div + div").hasText())
    }

    @Test
    fun html() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div><p>There</p></div>")
        val divs = doc.select("div")
        Assertions.assertEquals("<p>Hello</p>\n<p>There</p>", divs.html())
    }

    @Test
    fun outerHtml() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div><p>There</p></div>")
        val divs = doc.select("div")
        Assertions.assertEquals(
            "<div><p>Hello</p></div><div><p>There</p></div>",
            TextUtil.stripNewlines(divs.outerHtml())
        )
    }

    @Test
    fun setHtml() {
        val doc = Jsoup.parse("<p>One</p><p>Two</p><p>Three</p>")
        val ps = doc.select("p")
        ps.prepend("<b>Bold</b>").append("<i>Ital</i>")
        Assertions.assertEquals(
            "<p><b>Bold</b>Two<i>Ital</i></p>", TextUtil.stripNewlines(
                ps[1].outerHtml()
            )
        )
        ps.html("<span>Gone</span>")
        Assertions.assertEquals("<p><span>Gone</span></p>", TextUtil.stripNewlines(ps[1].outerHtml()))
    }

    @Test
    fun `val`() {
        val doc = Jsoup.parse("<input value='one' /><textarea>two</textarea>")
        val els = doc.select("input, textarea")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("one", els.value())
        Assertions.assertEquals("two", els.last()!!.value())
        els.value("three")
        Assertions.assertEquals("three", els.first()!!.value())
        Assertions.assertEquals("three", els.last()!!.value())
        Assertions.assertEquals("<textarea>three</textarea>", els.last()!!.outerHtml())
    }

    @Test
    fun before() {
        val doc = Jsoup.parse("<p>This <a>is</a> <a>jsoup</a>.</p>")
        doc.select("a").before("<span>foo</span>")
        Assertions.assertEquals(
            "<p>This <span>foo</span><a>is</a> <span>foo</span><a>jsoup</a>.</p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun after() {
        val doc = Jsoup.parse("<p>This <a>is</a> <a>jsoup</a>.</p>")
        doc.select("a").after("<span>foo</span>")
        Assertions.assertEquals(
            "<p>This <a>is</a><span>foo</span> <a>jsoup</a><span>foo</span>.</p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun wrap() {
        val h = "<p><b>This</b> is <b>jsoup</b></p>"
        val doc = Jsoup.parse(h)
        doc.select("b").wrap("<i></i>")
        Assertions.assertEquals("<p><i><b>This</b></i> is <i><b>jsoup</b></i></p>", doc.body().html())
    }

    @Test
    fun wrapDiv() {
        val h = "<p><b>This</b> is <b>jsoup</b>.</p> <p>How do you like it?</p>"
        val doc = Jsoup.parse(h)
        doc.select("p").wrap("<div></div>")
        Assertions.assertEquals(
            "<div>\n <p><b>This</b> is <b>jsoup</b>.</p>\n</div>\n<div>\n <p>How do you like it?</p>\n</div>",
            doc.body().html()
        )
    }

    @Test
    fun unwrap() {
        val h = "<div><font>One</font> <font><a href=\"/\">Two</a></font></div"
        val doc = Jsoup.parse(h)
        doc.select("font").unwrap()
        Assertions.assertEquals(
            """<div>
 One <a href="/">Two</a>
</div>""", doc.body().html()
        )
    }

    @Test
    fun unwrapP() {
        val h = "<p><a>One</a> Two</p> Three <i>Four</i> <p>Fix <i>Six</i></p>"
        val doc = Jsoup.parse(h)
        doc.select("p").unwrap()
        Assertions.assertEquals(
            "<a>One</a> Two Three <i>Four</i> Fix <i>Six</i>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun unwrapKeepsSpace() {
        val h = "<p>One <span>two</span> <span>three</span> four</p>"
        val doc = Jsoup.parse(h)
        doc.select("span").unwrap()
        Assertions.assertEquals("<p>One two three four</p>", doc.body().html())
    }

    @Test
    fun empty() {
        val doc = Jsoup.parse("<div><p>Hello <b>there</b></p> <p>now!</p></div>")
        doc.outputSettings().prettyPrint(false)
        doc.select("p").empty()
        Assertions.assertEquals("<div><p></p> <p></p></div>", doc.body().html())
    }

    @Test
    fun remove() {
        val doc = Jsoup.parse("<div><p>Hello <b>there</b></p> jsoup <p>now!</p></div>")
        doc.outputSettings().prettyPrint(false)
        doc.select("p").remove()
        Assertions.assertEquals("<div> jsoup </div>", doc.body().html())
    }

    @Test
    fun eq() {
        val h = "<p>Hello<p>there<p>world"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("there", doc.select("p").eq(1).text())
        Assertions.assertEquals("there", doc.select("p")[1].text())
    }

    @Test
    fun `is`() {
        val h = "<p>Hello<p title=foo>there<p>world"
        val doc = Jsoup.parse(h)
        val ps = doc.select("p")
        Assertions.assertTrue(ps.`is`("[title=foo]"))
        Assertions.assertFalse(ps.`is`("[title=bar]"))
    }

    @Test
    fun parents() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><p>There</p>")
        val parents = doc.select("p").parents()
        Assertions.assertEquals(3, parents.size)
        Assertions.assertEquals("div", parents[0].tagName())
        Assertions.assertEquals("body", parents[1].tagName())
        Assertions.assertEquals("html", parents[2].tagName())
    }

    @Test
    operator fun not() {
        val doc = Jsoup.parse("<div id=1><p>One</p></div> <div id=2><p><span>Two</span></p></div>")
        val div1 = doc.select("div").not(":has(p > span)")
        Assertions.assertEquals(1, div1.size)
        Assertions.assertEquals("1", div1.first()!!.id())
        val div2 = doc.select("div").not("#1")
        Assertions.assertEquals(1, div2.size)
        Assertions.assertEquals("2", div2.first()!!.id())
    }

    @Test
    fun tagNameSet() {
        val doc = Jsoup.parse("<p>Hello <i>there</i> <i>now</i></p>")
        doc.select("i").tagName("em")
        Assertions.assertEquals("<p>Hello <em>there</em> <em>now</em></p>", doc.body().html())
    }

    @Test
    fun traverse() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        doc.select("div").traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                accum.append("<").append(node.nodeName()).append(">")
            }

            override fun tail(node: Node, depth: Int) {
                accum.append("</").append(node.nodeName()).append(">")
            }
        })
        Assertions.assertEquals("<div><p><#text></#text></p></div><div><#text></#text></div>", accum.toString())
    }

    @Test
    fun forms() {
        val doc = Jsoup.parse("<form id=1><input name=q></form><div /><form id=2><input name=f></form>")
        val els = doc.select("form, div")
        Assertions.assertEquals(3, els.size)
        val forms = els.forms()
        Assertions.assertEquals(2, forms.size)
        Assertions.assertNotNull(forms[0])
        Assertions.assertNotNull(forms[1])
        Assertions.assertEquals("1", forms[0].id())
        Assertions.assertEquals("2", forms[1].id())
    }

    @Test
    fun comments() {
        val doc = Jsoup.parse("<!-- comment1 --><p><!-- comment2 --><p class=two><!-- comment3 -->")
        val comments = doc.select("p").comments()
        Assertions.assertEquals(2, comments.size)
        Assertions.assertEquals(" comment2 ", comments[0].data)
        Assertions.assertEquals(" comment3 ", comments[1].data)
        val comments1 = doc.select("p.two").comments()
        Assertions.assertEquals(1, comments1.size)
        Assertions.assertEquals(" comment3 ", comments1[0].data)
    }

    @Test
    fun textNodes() {
        val doc = Jsoup.parse("One<p>Two<a>Three</a><p>Four</p>Five")
        val textNodes = doc.select("p").textNodes()
        Assertions.assertEquals(2, textNodes.size)
        Assertions.assertEquals("Two", textNodes[0].text())
        Assertions.assertEquals("Four", textNodes[1].text())
    }

    @Test
    fun dataNodes() {
        var doc = Jsoup.parse("<p>One</p><script>Two</script><style>Three</style>")
        val dataNodes = doc.select("p, script, style").dataNodes()
        Assertions.assertEquals(2, dataNodes.size)
        Assertions.assertEquals("Two", dataNodes[0].wholeData)
        Assertions.assertEquals("Three", dataNodes[1].wholeData)
        doc = Jsoup.parse("<head><script type=application/json><crux></script><script src=foo>Blah</script>")
        val script = doc.select("script[type=application/json]")
        val scriptNode = script.dataNodes()
        Assertions.assertEquals(1, scriptNode.size)
        val dataNode = scriptNode[0]
        Assertions.assertEquals("<crux>", dataNode.wholeData)

        // check if they're live
        dataNode.setWholeData("<cromulent>")
        Assertions.assertEquals("<script type=\"application/json\"><cromulent></script>", script.outerHtml())
    }

    @Test
    fun nodesEmpty() {
        val doc = Jsoup.parse("<p>")
        Assertions.assertEquals(0, doc.select("form").textNodes().size)
    }

    @Test
    fun classWithHyphen() {
        val doc = Jsoup.parse("<p class='tab-nav'>Check</p>")
        val els = doc.getElementsByClass("tab-nav")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Check", els.text())
    }

    @Test
    fun siblings() {
        val doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12</div>")
        val els = doc.select("p:eq(3)") // gets p4 and p10
        Assertions.assertEquals(2, els.size)
        val next = els.next()
        Assertions.assertEquals(2, next.size)
        Assertions.assertEquals("5", next.first()!!.text())
        Assertions.assertEquals("11", next.last()!!.text())
        Assertions.assertEquals(0, els.next("p:contains(6)").size)
        val nextF = els.next("p:contains(5)")
        Assertions.assertEquals(1, nextF.size)
        Assertions.assertEquals("5", nextF.first()!!.text())
        val nextA = els.nextAll()
        Assertions.assertEquals(4, nextA.size)
        Assertions.assertEquals("5", nextA.first()!!.text())
        Assertions.assertEquals("12", nextA.last()!!.text())
        val nextAF = els.nextAll("p:contains(6)")
        Assertions.assertEquals(1, nextAF.size)
        Assertions.assertEquals("6", nextAF.first()!!.text())
        val prev = els.prev()
        Assertions.assertEquals(2, prev.size)
        Assertions.assertEquals("3", prev.first()!!.text())
        Assertions.assertEquals("9", prev.last()!!.text())
        Assertions.assertEquals(0, els.prev("p:contains(1)").size)
        val prevF = els.prev("p:contains(3)")
        Assertions.assertEquals(1, prevF.size)
        Assertions.assertEquals("3", prevF.first()!!.text())
        val prevA = els.prevAll()
        Assertions.assertEquals(6, prevA.size)
        Assertions.assertEquals("3", prevA.first()!!.text())
        Assertions.assertEquals("7", prevA.last()!!.text())
        val prevAF = els.prevAll("p:contains(1)")
        Assertions.assertEquals(1, prevAF.size)
        Assertions.assertEquals("1", prevAF.first()!!.text())
    }

    @Test
    fun eachText() {
        val doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>")
        val divText = doc.select("div").eachText()
        Assertions.assertEquals(2, divText.size)
        Assertions.assertEquals("1 2 3 4 5 6", divText[0])
        Assertions.assertEquals("7 8 9 10 11 12", divText[1])
        val pText = doc.select("p").eachText()
        val ps = doc.select("p")
        Assertions.assertEquals(13, ps.size)
        Assertions.assertEquals(12, pText.size) // not 13, as last doesn't have text
        Assertions.assertEquals("1", pText[0])
        Assertions.assertEquals("2", pText[1])
        Assertions.assertEquals("5", pText[4])
        Assertions.assertEquals("7", pText[6])
        Assertions.assertEquals("12", pText[11])
    }

    @Test
    fun eachAttr() {
        val doc = Jsoup.parse(
            "<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a>4</a>",
            "http://example.com"
        )
        val hrefAttrs = doc.select("a").eachAttr("href")
        Assertions.assertEquals(3, hrefAttrs.size)
        Assertions.assertEquals("/foo", hrefAttrs[0])
        Assertions.assertEquals("http://example.com/bar", hrefAttrs[1])
        Assertions.assertEquals("", hrefAttrs[2])
        Assertions.assertEquals(4, doc.select("a").size)
        val absAttrs = doc.select("a").eachAttr("abs:href")
        Assertions.assertEquals(3, absAttrs.size)
        Assertions.assertEquals(3, absAttrs.size)
        Assertions.assertEquals("http://example.com/foo", absAttrs[0])
        Assertions.assertEquals("http://example.com/bar", absAttrs[1])
        Assertions.assertEquals("http://example.com", absAttrs[2])
    }
}
