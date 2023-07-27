package net.dankito.ksoup.select

import net.dankito.ksoup.Assertions.assertElementsDoNotEqual
import net.dankito.ksoup.Assertions.assertElementsEqual
import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import kotlin.test.*

/**
 * Tests that the selector selects correctly.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class SelectorTest {

    @Test
    fun testByTag() {
        // should be case insensitive
        val els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV")
        assertEquals(3, els.size)
        assertEquals("1", els[0].id())
        assertEquals("2", els[1].id())
        assertEquals("3", els[2].id())
        val none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span")
        assertEquals(0, none.size)
    }

    @Test
    fun byEscapedTag() {
        // tested same result as js document.querySelector
        val doc = Jsoup.parse("<p.p>One</p.p> <p\\p>Two</p\\p>")
        val one = doc.expectFirst("p\\.p")
        assertEquals("One", one.text())
        val two = doc.expectFirst("p\\\\p")
        assertEquals("Two", two.text())
    }

    @Test
    fun testById() {
        val els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo")
        assertEquals(2, els.size)
        assertEquals("Hello", els[0].text())
        assertEquals("Foo two!", els[1].text())
        val none = Jsoup.parse("<div id=1></div>").select("#foo")
        assertEquals(0, none.size)
    }

    @Test
    fun byEscapedId() {
        val doc = Jsoup.parse("<p id='i.d'>One</p> <p id='i\\d'>Two</p> <p id='one-two/three'>Three</p>")
        val one = doc.expectFirst("#i\\.d")
        assertEquals("One", one.text())
        val two = doc.expectFirst("#i\\\\d")
        assertEquals("Two", two.text())
        val thr = doc.expectFirst("p#one-two\\/three")
        assertEquals("Three", thr.text())
    }

    @Test
    fun testByClass() {
        val els = Jsoup.parse("<p id=0 class='ONE two'><p id=1 class='one'><p id=2 class='two'>").select("P.One")
        assertEquals(2, els.size)
        assertEquals("0", els[0].id())
        assertEquals("1", els[1].id())
        val none = Jsoup.parse("<div class='one'></div>").select(".foo")
        assertEquals(0, none.size)
        val els2 = Jsoup.parse("<div class='One-Two'></div>").select(".one-two")
        assertEquals(1, els2.size)
    }

    @Test
    fun byEscapedClass() {
        val els: Element = Jsoup.parse("<p class='one.two#three'>One</p>")
        val one = els.expectFirst("p.one\\.two\\#three")
        assertEquals("One", one.text())
    }

    @Test
    fun testByClassCaseInsensitive() {
        val html = "<p Class=foo>One <p Class=Foo>Two <p class=FOO>Three <p class=farp>Four"
        val elsFromClass = Jsoup.parse(html).select("P.Foo")
        val elsFromAttr = Jsoup.parse(html).select("p[class=foo]")
        assertEquals(elsFromAttr.size, elsFromClass.size)
        assertEquals(3, elsFromClass.size)
        assertEquals("Two", elsFromClass[1].text())
    }

    @Test
    fun testNamespacedTag() {
        val doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>")
        val byTag = doc.select("abc|def")
        assertEquals(2, byTag.size)
        assertEquals("1", byTag.first()!!.id())
        assertEquals("2", byTag.last()!!.id())
        val byAttr = doc.select(".bold")
        assertEquals(1, byAttr.size)
        assertEquals("2", byAttr.last()!!.id())
        val byTagAttr = doc.select("abc|def.bold")
        assertEquals(1, byTagAttr.size)
        assertEquals("2", byTagAttr.last()!!.id())
        val byContains = doc.select("abc|def:contains(e)")
        assertEquals(2, byContains.size)
        assertEquals("1", byContains.first()!!.id())
        assertEquals("2", byContains.last()!!.id())
    }

    @Test
    fun testWildcardNamespacedTag() {
        val doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>")
        val byTag = doc.select("*|def")
        assertEquals(2, byTag.size)
        assertEquals("1", byTag.first()!!.id())
        assertEquals("2", byTag.last()!!.id())
        val byAttr = doc.select(".bold")
        assertEquals(1, byAttr.size)
        assertEquals("2", byAttr.last()!!.id())
        val byTagAttr = doc.select("*|def.bold")
        assertEquals(1, byTagAttr.size)
        assertEquals("2", byTagAttr.last()!!.id())
        val byContains = doc.select("*|def:contains(e)")
        assertEquals(2, byContains.size)
        assertEquals("1", byContains.first()!!.id())
        assertEquals("2", byContains.last()!!.id())
    }

    @Test
    fun testWildcardNamespacedXmlTag() {
        val doc = Jsoup.parse(
            "<div><Abc:Def id=1>Hello</Abc:Def></div> <Abc:Def class=bold id=2>There</abc:def>",
            "", xmlParser()
        )
        val byTag = doc.select("*|Def")
        assertEquals(2, byTag.size)
        assertEquals("1", byTag.first()!!.id())
        assertEquals("2", byTag.last()!!.id())
        val byAttr = doc.select(".bold")
        assertEquals(1, byAttr.size)
        assertEquals("2", byAttr.last()!!.id())
        val byTagAttr = doc.select("*|Def.bold")
        assertEquals(1, byTagAttr.size)
        assertEquals("2", byTagAttr.last()!!.id())
        val byContains = doc.select("*|Def:contains(e)")
        assertEquals(2, byContains.size)
        assertEquals("1", byContains.first()!!.id())
        assertEquals("2", byContains.last()!!.id())
    }

    @Test
    fun testWildCardNamespacedCaseVariations() {
        val doc = Jsoup.parse("<One:Two>One</One:Two><three:four>Two</three:four>", "", xmlParser())
        val els1 = doc.select("One|Two")
        val els2 = doc.select("one|two")
        val els3 = doc.select("Three|Four")
        val els4 = doc.select("three|Four")
        assertElementsEqual(els1, els2)
        assertElementsEqual(els3, els4)
        assertEquals("One", els1.text())
        assertEquals(1, els1.size)
        assertEquals("Two", els3.text())
        assertEquals(1, els2.size)
    }

    @Test
    fun testByAttributeRegex() {
        val doc =
            Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif><img></p>")
        val imgs = doc.select("img[src~=(?i)\\.(png|jpe?g)]")
        assertEquals(3, imgs.size)
        assertEquals("1", imgs[0].id())
        assertEquals("2", imgs[1].id())
        assertEquals("3", imgs[2].id())
    }

    @Test
    fun testByAttributeRegexCharacterClass() {
        val doc =
            Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif id=4></p>")
        val imgs = doc.select("img[src~=[o]]")
        assertEquals(2, imgs.size)
        assertEquals("1", imgs[0].id())
        assertEquals("4", imgs[1].id())
    }

    @Test
    fun testByAttributeRegexCombined() {
        val doc = Jsoup.parse("<div><table class=x><td>Hello</td></table></div>")
        val els = doc.select("div table[class~=x|y]")
        assertEquals(1, els.size)
        assertEquals("Hello", els.text())
    }

    @Test
    fun testCombinedWithContains() {
        val doc = Jsoup.parse("<p id=1>One</p><p>Two +</p><p>Three +</p>")
        val els = doc.select("p#1 + :contains(+)")
        assertEquals(1, els.size)
        assertEquals("Two +", els.text())
        assertEquals("p", els.first()!!.tagName())
    }

    @Test
    fun testAllElements() {
        val h = "<div><p>Hello</p><p><b>there</b></p></div>"
        val doc = Jsoup.parse(h)
        val allDoc = doc.select("*")
        val allUnderDiv = doc.select("div *")
        assertEquals(8, allDoc.size)
        assertEquals(3, allUnderDiv.size)
        assertEquals("p", allUnderDiv.first()!!.tagName())
    }

    @Test
    fun testAllWithClass() {
        val h = "<p class=first>One<p class=first>Two<p>Three"
        val doc = Jsoup.parse(h)
        val ps = doc.select("*.first")
        assertEquals(2, ps.size)
    }

    @Test
    fun testGroupOr() {
        val h = "<div title=foo /><div title=bar /><div /><p></p><img /><span title=qux>"
        val doc = Jsoup.parse(h)
        val els = doc.select("p,div,[title]")
        assertEquals(5, els.size)
        assertEquals("div", els[0].tagName())
        assertEquals("foo", els[0].attr("title"))
        assertEquals("div", els[1].tagName())
        assertEquals("bar", els[1].attr("title"))
        assertEquals("div", els[2].tagName())
        assertEquals(0, els[2].attr("title").length) // missing attributes come back as empty string
        assertFalse(els[2].hasAttr("title"))
        assertEquals("p", els[3].tagName())
        assertEquals("span", els[4].tagName())
    }

    @Test
    fun testGroupOrAttribute() {
        val h = "<div id=1 /><div id=2 /><div title=foo /><div title=bar />"
        val els = Jsoup.parse(h).select("[id],[title=foo]")
        assertEquals(3, els.size)
        assertEquals("1", els[0].id())
        assertEquals("2", els[1].id())
        assertEquals("foo", els[2].attr("title"))
    }

    @Test
    fun descendant() {
        val h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>"
        val doc = Jsoup.parse(h)
        val root = doc.getElementsByClass("HEAD").first()
        val els = root!!.select(".head p")
        assertEquals(2, els.size)
        assertEquals("Hello", els[0].text())
        assertEquals("There", els[1].text())
        val p = root.select("p.first")
        assertEquals(1, p.size)
        assertEquals("Hello", p[0].text())
        val empty = root.select("p .first") // self, not descend, should not match
        assertEquals(0, empty.size)
        val aboveRoot = root.select("body div.head")
        assertEquals(0, aboveRoot.size)
    }

    @Test
    fun and() {
        val h = "<div id=1 class='foo bar' title=bar name=qux><p class=foo title=bar>Hello</p></div"
        val doc = Jsoup.parse(h)
        val div = doc.select("div.foo")
        assertEquals(1, div.size)
        assertEquals("div", div.first()!!.tagName())
        val p = doc.select("div .foo") // space indicates like "div *.foo"
        assertEquals(1, p.size)
        assertEquals("p", p.first()!!.tagName())
        val div2 = doc.select("div#1.foo.bar[title=bar][name=qux]") // very specific!
        assertEquals(1, div2.size)
        assertEquals("div", div2.first()!!.tagName())
        val p2 = doc.select("div *.foo") // space indicates like "div *.foo"
        assertEquals(1, p2.size)
        assertEquals("p", p2.first()!!.tagName())
    }

    @Test
    fun deeperDescendant() {
        val h =
            "<div class=head><p><span class=first>Hello</div><div class=head><p class=first><span>Another</span><p>Again</div>"
        val doc = Jsoup.parse(h)
        val root = doc.getElementsByClass("head").first()
        val els = root!!.select("div p .first")
        assertEquals(1, els.size)
        assertEquals("Hello", els.first()!!.text())
        assertEquals("span", els.first()!!.tagName())
        val aboveRoot = root.select("body p .first")
        assertEquals(0, aboveRoot.size)
    }

    @Test
    fun parentChildElement() {
        val h = "<div id=1><div id=2><div id = 3></div></div></div><div id=4></div>"
        val doc = Jsoup.parse(h)
        val divs = doc.select("div > div")
        assertEquals(2, divs.size)
        assertEquals("2", divs[0].id()) // 2 is child of 1
        assertEquals("3", divs[1].id()) // 3 is child of 2
        val div2 = doc.select("div#1 > div")
        assertEquals(1, div2.size)
        assertEquals("2", div2[0].id())
    }

    @Test
    fun parentWithClassChild() {
        val h = "<h1 class=foo><a href=1 /></h1><h1 class=foo><a href=2 class=bar /></h1><h1><a href=3 /></h1>"
        val doc = Jsoup.parse(h)
        val allAs = doc.select("h1 > a")
        assertEquals(3, allAs.size)
        assertEquals("a", allAs.first()!!.tagName())
        val fooAs = doc.select("h1.foo > a")
        assertEquals(2, fooAs.size)
        assertEquals("a", fooAs.first()!!.tagName())
        val barAs = doc.select("h1.foo > a.bar")
        assertEquals(1, barAs.size)
    }

    @Test
    fun parentChildStar() {
        val h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><span>Hi</span></div>"
        val doc = Jsoup.parse(h)
        val divChilds = doc.select("div > *")
        assertEquals(3, divChilds.size)
        assertEquals("p", divChilds[0].tagName())
        assertEquals("p", divChilds[1].tagName())
        assertEquals("span", divChilds[2].tagName())
    }

    @Test
    fun multiChildDescent() {
        val h = "<div id=foo><h1 class=bar><a href=http://example.com/>One</a></h1></div>"
        val doc = Jsoup.parse(h)
        val els = doc.select("div#foo > h1.bar > a[href*=example]")
        assertEquals(1, els.size)
        assertEquals("a", els.first()!!.tagName())
    }

    @Test
    fun caseInsensitive() {
        val h = "<dIv tItle=bAr><div>" // mixed case so a simple toLowerCase() on value doesn't catch
        val doc = Jsoup.parse(h)
        assertEquals(2, doc.select("DiV").size)
        assertEquals(1, doc.select("DiV[TiTLE]").size)
        assertEquals(1, doc.select("DiV[TiTLE=BAR]").size)
        assertEquals(0, doc.select("DiV[TiTLE=BARBARELLA]").size)
    }

    @Test
    fun adjacentSiblings() {
        val h = "<ol><li>One<li>Two<li>Three</ol>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("li + li")
        assertEquals(2, sibs.size)
        assertEquals("Two", sibs[0].text())
        assertEquals("Three", sibs[1].text())
    }

    @Test
    fun adjacentSiblingsWithId() {
        val h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("li#1 + li#2")
        assertEquals(1, sibs.size)
        assertEquals("Two", sibs[0].text())
    }

    @Test
    fun notAdjacent() {
        val h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("li#1 + li#3")
        assertEquals(0, sibs.size)
    }

    @Test
    fun mixCombinator() {
        val h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("body > div.foo li + li")
        assertEquals(2, sibs.size)
        assertEquals("Two", sibs[0].text())
        assertEquals("Three", sibs[1].text())
    }

    @Test
    fun mixCombinatorGroup() {
        val h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>"
        val doc = Jsoup.parse(h)
        val els = doc.select(".foo > ol, ol > li + li")
        assertEquals(3, els.size)
        assertEquals("ol", els[0].tagName())
        assertEquals("Two", els[1].text())
        assertEquals("Three", els[2].text())
    }

    @Test
    fun generalSiblings() {
        val h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>"
        val doc = Jsoup.parse(h)
        val els = doc.select("#1 ~ #3")
        assertEquals(1, els.size)
        assertEquals("Three", els.first()!!.text())
    }

    // for http://github.com/jhy/jsoup/issues#issue/10
    @Test
    fun testCharactersInIdAndClass() {
        // using CSS spec for identifiers (id and class): a-z0-9, -, _. NOT . (which is OK in html spec, but not css)
        val h = "<div><p id='a1-foo_bar'>One</p><p class='b2-qux_bif'>Two</p></div>"
        val doc = Jsoup.parse(h)
        val el1 = doc.getElementById("a1-foo_bar")
        assertEquals("One", el1!!.text())
        val el2 = doc.getElementsByClass("b2-qux_bif").first()
        assertEquals("Two", el2!!.text())
        val el3 = doc.select("#a1-foo_bar").first()
        assertEquals("One", el3!!.text())
        val el4 = doc.select(".b2-qux_bif").first()
        assertEquals("Two", el4!!.text())
    }

    // for http://github.com/jhy/jsoup/issues#issue/13
    @Test
    fun testSupportsLeadingCombinator() {
        var h = "<div><p><span>One</span><span>Two</span></p></div>"
        var doc = Jsoup.parse(h)
        val p = doc.select("div > p").first()
        val spans = p!!.select("> span")
        assertEquals(2, spans.size)
        assertEquals("One", spans.first()!!.text())

        // make sure doesn't get nested
        h = "<div id=1><div id=2><div id=3></div></div></div>"
        doc = Jsoup.parse(h)
        val div = doc.select("div").select(" > div").first()
        assertEquals("2", div!!.id())
    }

    @Test
    fun testPseudoLessThan() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>")
        val ps = doc.select("div p:lt(2)")
        assertEquals(3, ps.size)
        assertEquals("One", ps[0].text())
        assertEquals("Two", ps[1].text())
        assertEquals("Four", ps[2].text())
    }

    @Test
    fun testPseudoGreaterThan() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>")
        val ps = doc.select("div p:gt(0)")
        assertEquals(2, ps.size)
        assertEquals("Two", ps[0].text())
        assertEquals("Three", ps[1].text())
    }

    @Test
    fun testPseudoEquals() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>")
        val ps = doc.select("div p:eq(0)")
        assertEquals(2, ps.size)
        assertEquals("One", ps[0].text())
        assertEquals("Four", ps[1].text())
        val ps2 = doc.select("div:eq(0) p:eq(0)")
        assertEquals(1, ps2.size)
        assertEquals("One", ps2[0].text())
        assertEquals("p", ps2[0].tagName())
    }

    @Test
    fun testPseudoBetween() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>")
        val ps = doc.select("div p:gt(0):lt(2)")
        assertEquals(1, ps.size)
        assertEquals("Two", ps[0].text())
    }

    @Test
    fun testPseudoCombined() {
        val doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>")
        val ps = doc.select("div.foo p:gt(0)")
        assertEquals(1, ps.size)
        assertEquals("Two", ps[0].text())
    }

    @Test
    fun testPseudoHas() {
        val doc =
            Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>")
        val divs1 = doc.select("div:has(span)")
        assertEquals(2, divs1.size)
        assertEquals("0", divs1[0].id())
        assertEquals("1", divs1[1].id())
        val divs2 = doc.select("div:has([class])")
        assertEquals(1, divs2.size)
        assertEquals("1", divs2[0].id())
        val divs3 = doc.select("div:has(span, p)")
        assertEquals(3, divs3.size)
        assertEquals("0", divs3[0].id())
        assertEquals("1", divs3[1].id())
        assertEquals("2", divs3[2].id())
        val els1 = doc.body().select(":has(p)")
        assertEquals(3, els1.size) // body, div, div
        assertEquals("body", els1.first()!!.tagName())
        assertEquals("0", els1[1].id())
        assertEquals("2", els1[2].id())
        val els2 = doc.body().select(":has(> span)")
        assertEquals(2, els2.size) // p, div
        assertEquals("p", els2.first()!!.tagName())
        assertEquals("1", els2[1].id())
    }

    @Test
    fun testNestedHas() {
        val doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p></div>")
        var divs = doc.select("div:has(p:has(span))")
        assertEquals(1, divs.size)
        assertEquals("One", divs.first()!!.text())

        // test matches in has
        divs = doc.select("div:has(p:matches((?i)two))")
        assertEquals(1, divs.size)
        assertEquals("div", divs.first()!!.tagName())
        assertEquals("Two", divs.first()!!.text())

        // test contains in has
        divs = doc.select("div:has(p:contains(two))")
        assertEquals(1, divs.size)
        assertEquals("div", divs.first()!!.tagName())
        assertEquals("Two", divs.first()!!.text())
    }

    @Test
    fun testPsuedoContainsWithParentheses() {
        val doc = Jsoup.parse("<div><p id=1>This (is good)</p><p id=2>This is bad)</p>")
        val ps1 = doc.select("p:contains(this (is good))")
        assertEquals(1, ps1.size)
        assertEquals("1", ps1.first()!!.id())
        val ps2 = doc.select("p:contains(this is bad\\))")
        assertEquals(1, ps2.size)
        assertEquals("2", ps2.first()!!.id())
    }

    @Test
    fun containsWholeText() {
        var doc = Jsoup.parse("<div><p> jsoup\n The <i>HTML</i> Parser</p><p>jsoup The HTML Parser</div>")
        val ps = doc.select("p")
        val es1 = doc.select("p:containsWholeText( jsoup\n The HTML Parser)")
        val es2 = doc.select("p:containsWholeText(jsoup The HTML Parser)")
        assertEquals(1, es1.size)
        assertEquals(1, es2.size)
        assertEquals(ps[0], es1.first())
        assertEquals(ps[1], es2.first())
        assertEquals(0, doc.select("div:containsWholeText(jsoup the html parser)").size)
        assertEquals(0, doc.select("div:containsWholeText(jsoup\n the html parser)").size)
        doc = Jsoup.parse("<div><p></p><p> </p><p>.  </p>")
        val blanks = doc.select("p:containsWholeText(  )")
        assertEquals(1, blanks.size)
        assertEquals(".  ", blanks.first()!!.wholeText())
    }

    @Test
    fun containsWholeOwnText() {
        var doc = Jsoup.parse("<div><p> jsoup\n The <i>HTML</i> Parser</p><p>jsoup The HTML Parser<br></div>")
        val ps = doc.select("p")
        val es1 = doc.select("p:containsWholeOwnText( jsoup\n The  Parser)")
        val es2 = doc.select("p:containsWholeOwnText(jsoup The HTML Parser\n)")
        assertEquals(1, es1.size)
        assertEquals(1, es2.size)
        assertEquals(ps[0], es1.first())
        assertEquals(ps[1], es2.first())
        assertEquals(0, doc.select("div:containsWholeOwnText(jsoup the html parser)").size)
        assertEquals(0, doc.select("div:containsWholeOwnText(jsoup\n the  parser)").size)
        doc = Jsoup.parse("<div><p></p><p> </p><p>.  </p>")
        val blanks = doc.select("p:containsWholeOwnText(  )")
        assertEquals(1, blanks.size)
        assertEquals(".  ", blanks.first()!!.wholeText())
    }

    @Test
    fun testMatches() {
        val doc =
            Jsoup.parse("<p id=1>The <i>Rain</i></p> <p id=2>There are 99 bottles.</p> <p id=3>Harder (this)</p> <p id=4>Rain</p>")
        val p1 = doc.select("p:matches(The rain)") // no match, case sensitive
        assertEquals(0, p1.size)
        val p2 = doc.select("p:matches((?i)the rain)") // case insense. should include root, html, body
        assertEquals(1, p2.size)
        assertEquals("1", p2.first()!!.id())
        val p4 = doc.select("p:matches((?i)^rain$)") // bounding
        assertEquals(1, p4.size)
        assertEquals("4", p4.first()!!.id())
        val p5 = doc.select("p:matches(\\d+)")
        assertEquals(1, p5.size)
        assertEquals("2", p5.first()!!.id())
        val p6 = doc.select("p:matches(\\w+\\s+\\(\\w+\\))") // test bracket matching
        assertEquals(1, p6.size)
        assertEquals("3", p6.first()!!.id())
        val p7 = doc.select("p:matches((?i)the):has(i)") // multi
        assertEquals(1, p7.size)
        assertEquals("1", p7.first()!!.id())
    }

    @Test
    fun matchesOwn() {
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b> now</p>")
        val p1 = doc.select("p:matchesOwn((?i)hello now)")
        assertEquals(1, p1.size)
        assertEquals("1", p1.first()!!.id())
        assertEquals(0, doc.select("p:matchesOwn(there)").size)
    }

    @Test
    fun matchesWholeText() {
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b>\n now</p><p id=2> </p><p id=3></p>")
        val p1 = doc.select("p:matchesWholeText((?i)hello there\n now)")
        assertEquals(1, p1.size)
        assertEquals("1", p1.first()!!.id())
        assertEquals(1, doc.select("p:matchesWholeText(there\n now)").size)
        assertEquals(0, doc.select("p:matchesWholeText(There\n now)").size)
        val p2 = doc.select("p:matchesWholeText(^\\s+$)")
        assertEquals(1, p2.size)
        assertEquals("2", p2.first()!!.id())
        val p3 = doc.select("p:matchesWholeText(^$)")
        assertEquals(1, p3.size)
        assertEquals("3", p3.first()!!.id())
    }

    @Test
    fun matchesWholeOwnText() {
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b>\n now</p><p id=2> </p><p id=3><i>Text</i></p>")
        val p1 = doc.select("p:matchesWholeOwnText((?i)hello \n now)")
        assertEquals(1, p1.size)
        assertEquals("1", p1.first()!!.id())
        assertEquals(0, doc.select("p:matchesWholeOwnText(there\n now)").size)
        val p2 = doc.select("p:matchesWholeOwnText(^\\s+$)")
        assertEquals(1, p2.size)
        assertEquals("2", p2.first()!!.id())
        val p3 = doc.select("p:matchesWholeOwnText(^$)")
        assertEquals(1, p3.size)
        assertEquals("3", p3.first()!!.id())
    }

    @Test
    fun testRelaxedTags() {
        val doc = Jsoup.parse("<abc_def id=1>Hello</abc_def> <abc-def id=2>There</abc-def>")
        val el1 = doc.select("abc_def")
        assertEquals(1, el1.size)
        assertEquals("1", el1.first()!!.id())
        val el2 = doc.select("abc-def")
        assertEquals(1, el2.size)
        assertEquals("2", el2.first()!!.id())
    }

    @Test
    fun notParas() {
        val doc = Jsoup.parse("<p id=1>One</p> <p>Two</p> <p><span>Three</span></p>")
        val el1 = doc.select("p:not([id=1])")
        assertEquals(2, el1.size)
        assertEquals("Two", el1.first()!!.text())
        assertEquals("Three", el1.last()!!.text())
        val el2 = doc.select("p:not(:has(span))")
        assertEquals(2, el2.size)
        assertEquals("One", el2.first()!!.text())
        assertEquals("Two", el2.last()!!.text())
    }

    @Test
    fun notAll() {
        val doc = Jsoup.parse("<p>Two</p> <p><span>Three</span></p>")
        val el1 = doc.body().select(":not(p)") // should just be the span
        assertEquals(2, el1.size)
        assertEquals("body", el1.first()!!.tagName())
        assertEquals("span", el1.last()!!.tagName())
    }

    @Test
    fun notClass() {
        val doc = Jsoup.parse("<div class=left>One</div><div class=right id=1><p>Two</p></div>")
        val el1 = doc.select("div:not(.left)")
        assertEquals(1, el1.size)
        assertEquals("1", el1.first()!!.id())
    }

    @Test
    fun handlesCommasInSelector() {
        val doc = Jsoup.parse("<p name='1,2'>One</p><div>Two</div><ol><li>123</li><li>Text</li></ol>")
        val ps = doc.select("[name=1,2]")
        assertEquals(1, ps.size)
        val containers = doc.select("div, li:matches([0-9,]+)")
        assertEquals(2, containers.size)
        assertEquals("div", containers[0].tagName())
        assertEquals("li", containers[1].tagName())
        assertEquals("123", containers[1].text())
    }

    @Test
    fun selectSupplementaryCharacter() {
        val s = StringUtil.codePointToString(135361)
        val doc = Jsoup.parse("<div k$s='$s'>^$s$/div>")
        assertEquals("div", doc.select("div[k$s]").first()!!.tagName())
        assertEquals("div", doc.select("div:containsOwn($s)").first()!!.tagName())
    }

    @Test
    fun selectClassWithSpace() {
        val html = """
            <div class="value">class without space</div>
            <div class="value ">class with space</div>
            """.trimIndent()
        val doc = Jsoup.parse(html)
        var found = doc.select("div[class=value ]")
        assertEquals(2, found.size)
        assertEquals("class without space", found[0].text())
        assertEquals("class with space", found[1].text())
        found = doc.select("div[class=\"value \"]")
        assertEquals(2, found.size)
        assertEquals("class without space", found[0].text())
        assertEquals("class with space", found[1].text())
        found = doc.select("div[class=\"value\\ \"]")
        assertEquals(0, found.size)
    }

    @Test
    fun selectSameElements() {
        val html = "<div>one</div><div>one</div>"
        val doc = Jsoup.parse(html)
        val els = doc.select("div")
        assertEquals(2, els.size)
        val subSelect = els.select(":contains(one)")
        assertEquals(2, subSelect.size)
    }

    @Test
    fun attributeWithBrackets() {
        val html = "<div data='End]'>One</div> <div data='[Another)]]'>Two</div>"
        val doc = Jsoup.parse(html)
        assertEquals("One", doc.select("div[data='End]']").first()!!.text())
        assertEquals("Two", doc.select("div[data='[Another)]]']").first()!!.text())
        assertEquals("One", doc.select("div[data=\"End]\"]").first()!!.text())
        assertEquals("Two", doc.select("div[data=\"[Another)]]\"]").first()!!.text())
    }

    @Test
    fun containsWithQuote() {
        val html = "<p>One'One</p><p>One'Two</p>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p:contains(One\\'One)")
        assertEquals(1, els.size)
        assertEquals("One'One", els.text())
    }

    @Test
    fun selectFirst() {
        val html = "<p>One<p>Two<p>Three"
        val doc = Jsoup.parse(html)
        assertEquals("One", doc.selectFirst("p")!!.text())
    }

    @Test
    fun selectFirstWithAnd() {
        val html = "<p>One<p class=foo>Two<p>Three"
        val doc = Jsoup.parse(html)
        assertEquals("Two", doc.selectFirst("p.foo")!!.text())
    }

    @Test
    fun selectFirstWithOr() {
        val html = "<p>One<p>Two<p>Three<div>Four"
        val doc = Jsoup.parse(html)
        assertEquals("One", doc.selectFirst("p, div")!!.text())
    }

    @Test
    fun matchText() {
        val html = "<p>One<br>Two</p>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(false)
        val origHtml = doc.html()
        val one = doc.select("p:matchText:first-child")
        assertEquals("One", one.first()!!.text())
        val two = doc.select("p:matchText:last-child")
        assertEquals("Two", two.first()!!.text())
        assertEquals(origHtml, doc.html())
        assertEquals("Two", doc.select("p:matchText + br + *").text())
    }

    @Test
    fun nthLastChildWithNoParent() {
        val el = Element("p").text("Orphan")
        val els = el.select("p:nth-last-child(1)")
        assertEquals(0, els.size)
    }

    @Test
    fun splitOnBr() {
        val html = "<div><p>One<br>Two<br>Three</p></div>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p:matchText")
        assertEquals(3, els.size)
        assertEquals("One", els[0].text())
        assertEquals("Two", els[1].text())
        assertEquals("Three", els[2].toString())
    }

    @Test
    fun matchTextAttributes() {
        val doc = Jsoup.parse("<div><p class=one>One<br>Two<p class=two>Three<br>Four")
        val els = doc.select("p.two:matchText:last-child")
        assertEquals(1, els.size)
        assertEquals("Four", els.text())
    }

    @Test
    fun findBetweenSpan() {
        val doc = Jsoup.parse("<p><span>One</span> Two <span>Three</span>")
        val els = doc.select("span ~ p:matchText") // the Two becomes its own p, sibling of the span
        // todo - think this should really be 'p:matchText span ~ p'. The :matchText should behave as a modifier to expand the nodes.
        assertEquals(1, els.size)
        assertEquals("Two", els.text())
    }

    @Test
    fun startsWithBeginsWithSpace() {
        val doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net\">(abc@def.net)</a></small>")
        val els = doc.select("a[href^=' mailto']")
        assertEquals(1, els.size)
    }

    @Test
    fun endsWithEndsWithSpaces() {
        val doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net \">(abc@def.net)</a></small>")
        val els = doc.select("a[href$='.net ']")
        assertEquals(1, els.size)
    }

    // https://github.com/jhy/jsoup/issues/1257
    private val mixedCase = "<html xmlns:n=\"urn:ns\"><n:mixedCase>text</n:mixedCase></html>"
    private val lowercase = "<html xmlns:n=\"urn:ns\"><n:lowercase>text</n:lowercase></html>"
    @Test
    fun html_mixed_case_simple_name() {
        val doc = Jsoup.parse(mixedCase, "", htmlParser())
        assertEquals(0, doc.select("mixedCase").size)
    }

    @Test
    fun html_mixed_case_wildcard_name() {
        val doc = Jsoup.parse(mixedCase, "", htmlParser())
        assertEquals(1, doc.select("*|mixedCase").size)
    }

    @Test
    fun html_lowercase_simple_name() {
        val doc = Jsoup.parse(lowercase, "", htmlParser())
        assertEquals(0, doc.select("lowercase").size)
    }

    @Test
    fun html_lowercase_wildcard_name() {
        val doc = Jsoup.parse(lowercase, "", htmlParser())
        assertEquals(1, doc.select("*|lowercase").size)
    }

    @Test
    fun xml_mixed_case_simple_name() {
        val doc = Jsoup.parse(mixedCase, "", xmlParser())
        assertEquals(0, doc.select("mixedCase").size)
    }

    @Test
    fun xml_mixed_case_wildcard_name() {
        val doc = Jsoup.parse(mixedCase, "", xmlParser())
        assertEquals(1, doc.select("*|mixedCase").size)
    }

    @Test
    fun xml_lowercase_simple_name() {
        val doc = Jsoup.parse(lowercase, "", xmlParser())
        assertEquals(0, doc.select("lowercase").size)
    }

    @Test
    fun xml_lowercase_wildcard_name() {
        val doc = Jsoup.parse(lowercase, "", xmlParser())
        assertEquals(1, doc.select("*|lowercase").size)
    }

    @Test
    fun trimSelector() {
        // https://github.com/jhy/jsoup/issues/1274
        val doc = Jsoup.parse("<p><span>Hello")
        val els = doc.select(" p span ")
        assertEquals(1, els.size)
        assertEquals("Hello", els.first()!!.text())
    }

    @Test
    fun xmlWildcardNamespaceTest() {
        // https://github.com/jhy/jsoup/issues/1208
        val doc = Jsoup.parse("<ns1:MyXmlTag>1111</ns1:MyXmlTag><ns2:MyXmlTag>2222</ns2:MyXmlTag>", "", xmlParser())
        val select = doc.select("*|MyXmlTag")
        assertEquals(2, select.size)
        assertEquals("1111", select[0].text())
        assertEquals("2222", select[1].text())
    }

    @Test
    fun childElements() {
        // https://github.com/jhy/jsoup/issues/1292
        val html = "<body><span id=1>One <span id=2>Two</span></span></body>"
        val doc = Jsoup.parse(html)
        val outer = doc.selectFirst("span")
        val span = outer!!.selectFirst("span")
        val inner = outer.selectFirst("* span")
        assertEquals("1", outer.id())
        assertEquals("1", span!!.id())
        assertEquals("2", inner!!.id())
        assertEquals(outer, span)
        assertNotEquals(outer, inner)
    }

    @Test
    fun selectFirstLevelChildrenOnly() {
        // testcase for https://github.com/jhy/jsoup/issues/984
        val html = "<div><span>One <span>Two</span></span> <span>Three <span>Four</span></span>"
        val doc = Jsoup.parse(html)
        val div = doc.selectFirst("div")
        assertNotNull(div)

        // want to select One and Three only - the first level children
        val spans = div!!.select(":root > span")
        assertEquals(2, spans.size)
        assertEquals("One Two", spans[0].text())
        assertEquals("Three Four", spans[1].text())
    }

    @Test
    fun wildcardNamespaceMatchesNoNamespace() {
        // https://github.com/jhy/jsoup/issues/1565
        val xml = "<package><meta>One</meta><opf:meta>Two</opf:meta></package>"
        val doc = Jsoup.parse(xml, "", xmlParser())
        val metaEls = doc.select("meta")
        assertEquals(1, metaEls.size)
        assertEquals("One", metaEls[0].text())
        val nsEls = doc.select("*|meta")
        assertEquals(2, nsEls.size)
        assertEquals("One", nsEls[0].text())
        assertEquals("Two", nsEls[1].text())
    }

    @Test
    fun containsTextQueryIsNormalized() {
        val doc = Jsoup.parse("<p><p id=1>Hello  there now<em>!</em>")
        val a = doc.select("p:contains(Hello   there  now!)")
        val b = doc.select(":containsOwn(hello   there  now)")
        val c = doc.select("p:contains(Hello there now)")
        val d = doc.select(":containsOwn(hello There now)")
        val e = doc.select("p:contains(HelloThereNow)")
        assertEquals(1, a.size)
        assertElementsEqual(a, b)
        assertElementsEqual(a, c)
        assertElementsEqual(a, d)
        assertEquals(0, e.size)
        assertElementsDoNotEqual(a, e)
    }

    @Test
    fun selectorExceptionNotStringFormatException() {
        val ex = Selector.SelectorParseException("%&" as String?) // otherwise wrong constructor gets called
        assertEquals("%&", ex.message)
    }

    @Test
    fun evaluatorMemosAreReset() {
        val eval = QueryParser.parse("p ~ p")
        val andEval = eval as CombiningEvaluator.And

        val prevEval = andEval.evaluators[0] as StructuralEvaluator.PreviousSibling
        val map = prevEval.threadMemo.get()!!
        assertEquals(0, map.size) // no memo yet

        val doc1 = Jsoup.parse("<p>One<p>Two<p>Three")
        val doc2 = Jsoup.parse("<p>One2<p>Two2<p>Three2")

        val s1 = doc1.select(eval)
        assertEquals(2, s1.size)
        assertEquals("Two", s1.first()?.text())
        val s2 = doc2.select(eval)
        assertEquals(2, s2.size)
        assertEquals("Two2", s2.first()?.text())

        assertEquals(1, map.size) // root of doc 2
    }

    @Test
    fun testForIdentityNotEquality_EqualElements() {
        val elements = listOf(
            Element("div").appendText("Equal"),
            Element("div").appendText("Equal")
        )

        val result = Selector.select("div", elements)

        assertEquals(2, result.size)
        assertTrue(result.contains(elements[0]))
        assertTrue(result.contains(elements[1]))
    }

    @Test
    fun testForIdentityNotEquality_IdenticalElements() {
        val element = Element("div").appendText("Identical")
        val elements = listOf(element, element)

        val result = Selector.select("div", elements)

        assertEquals(1, result.size)
    }

}
