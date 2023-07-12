package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.helper.ValidationException
import net.dankito.ksoup.parser.ParseSettings
import net.dankito.ksoup.parser.Parser
import net.dankito.ksoup.parser.Tag
import net.dankito.ksoup.select.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream


/**
 * Tests for Element (DOM stuff mostly).
 *
 * @author Jonathan Hedley
 */
class ElementTest {
    private val reference =
        "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>"

    @Test
    fun testId() {
        val doc = Jsoup.parse("<div id=Foo>")
        val el = doc.selectFirst("div")
        Assertions.assertEquals("Foo", el!!.id())
    }

    @Test
    fun testSetId() {
        val doc = Jsoup.parse("<div id=Boo>")
        val el = doc.selectFirst("div")
        el!!.id("Foo")
        Assertions.assertEquals("Foo", el.id())
    }

    @Test
    fun elementsByTagName() {
        val doc = Jsoup.parse(reference)
        val divs: List<Element> = doc.getElementsByTag("div")
        Assertions.assertEquals(2, divs.size)
        Assertions.assertEquals("div1", divs[0].id())
        Assertions.assertEquals("div2", divs[1].id())
        val ps: List<Element> = doc.getElementsByTag("p")
        Assertions.assertEquals(2, ps.size)
        Assertions.assertEquals("Hello", (ps[0].childNode(0) as TextNode).wholeText)
        Assertions.assertEquals("Another ", (ps[1].childNode(0) as TextNode).wholeText)
        val ps2: List<Element> = doc.getElementsByTag("P")
        Assertions.assertEquals(ps, ps2)
        val imgs: List<Element> = doc.getElementsByTag("img")
        Assertions.assertEquals("foo.png", imgs[0].attr("src"))
        val empty: List<Element> = doc.getElementsByTag("wtf")
        Assertions.assertEquals(0, empty.size)
    }

    @Test
    fun namespacedElementsByTag() {
        val doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div>")
        val els = doc.getElementsByTag("abc:def")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("1", els.first()!!.id())
        Assertions.assertEquals("abc:def", els.first()!!.tagName())
    }

    @Test
    fun testGetElementById() {
        val doc = Jsoup.parse(reference)
        val div = doc.getElementById("div1")
        Assertions.assertEquals("div1", div!!.id())
        Assertions.assertNull(doc.getElementById("none"))
        val doc2 = Jsoup.parse("<div id=1><div id=2><p>Hello <span id=2>world!</span></p></div></div>")
        val div2 = doc2.getElementById("2")
        Assertions.assertEquals("div", div2!!.tagName()) // not the span
        val span = div2.child(0).getElementById("2") // called from <p> context should be span
        Assertions.assertEquals("span", span!!.tagName())
    }

    @Test
    fun testGetText() {
        val doc = Jsoup.parse(reference)
        Assertions.assertEquals("Hello Another element", doc.text())
        Assertions.assertEquals("Another element", doc.getElementsByTag("p")[1].text())
    }

    @Test
    fun testGetChildText() {
        val doc = Jsoup.parse("<p>Hello <b>there</b> now")
        val p = doc.select("p").first()
        Assertions.assertEquals("Hello there now", p!!.text())
        Assertions.assertEquals("Hello now", p.ownText())
    }

    @Test
    fun testNormalisesText() {
        val h = "<p>Hello<p>There.</p> \n <p>Here <b>is</b> \n s<b>om</b>e text."
        val doc = Jsoup.parse(h)
        val text = doc.text()
        Assertions.assertEquals("Hello There. Here is some text.", text)
    }

    @Test
    fun testKeepsPreText() {
        val h = "<p>Hello \n \n there.</p> <div><pre>  What's \n\n  that?</pre>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("Hello there.   What's \n\n  that?", doc.text())
    }

    @Test
    fun testKeepsPreTextInCode() {
        val h = "<pre><code>code\n\ncode</code></pre>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("code\n\ncode", doc.text())
        Assertions.assertEquals("<pre><code>code\n\ncode</code></pre>", doc.body().html())
    }

    @Test
    fun testKeepsPreTextAtDepth() {
        val h = "<pre><code><span><b>code\n\ncode</b></span></code></pre>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("code\n\ncode", doc.text())
        Assertions.assertEquals("<pre><code><span><b>code\n\ncode</b></span></code></pre>", doc.body().html())
    }

    @Test
    fun doesNotWrapBlocksInPre() {
        // https://github.com/jhy/jsoup/issues/1891
        val h = "<pre><span><foo><div>TEST\n TEST</div></foo></span></pre>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("TEST\n TEST", doc.wholeText())
        Assertions.assertEquals(h, doc.body().html())
    }

    @Test
    fun testBrHasSpace() {
        var doc = Jsoup.parse("<p>Hello<br>there</p>")
        Assertions.assertEquals("Hello there", doc.text())
        Assertions.assertEquals("Hello there", doc.select("p").first()!!.ownText())
        doc = Jsoup.parse("<p>Hello <br> there</p>")
        Assertions.assertEquals("Hello there", doc.text())
    }

    @Test
    fun testBrHasSpaceCaseSensitive() {
        var doc = Jsoup.parse("<p>Hello<br>there<BR>now</p>", Parser.htmlParser().settings(ParseSettings.preserveCase))
        Assertions.assertEquals("Hello there now", doc.text())
        Assertions.assertEquals("Hello there now", doc.select("p").first()!!.ownText())
        doc = Jsoup.parse("<p>Hello <br> there <BR> now</p>")
        Assertions.assertEquals("Hello there now", doc.text())
    }

    @Test
    fun textHasSpacesAfterBlock() {
        val doc = Jsoup.parse("<div>One</div><div>Two</div><span>Three</span><p>Fou<i>r</i></p>")
        val text = doc.text()
        val wholeText = doc.wholeText()
        Assertions.assertEquals("One Two Three Four", text)
        Assertions.assertEquals("OneTwoThreeFour", wholeText)
        Assertions.assertEquals("OneTwo", Jsoup.parse("<span>One</span><span>Two</span>").text())
    }

    @Test
    fun testWholeText() {
        var doc = Jsoup.parse("<p> Hello\nthere &nbsp;  </p>")
        Assertions.assertEquals(" Hello\nthere    ", doc.wholeText())
        doc = Jsoup.parse("<p>Hello  \n  there</p>")
        Assertions.assertEquals("Hello  \n  there", doc.wholeText())
        doc = Jsoup.parse("<p>Hello  <div>\n  there</div></p>")
        Assertions.assertEquals("Hello  \n  there", doc.wholeText())
    }

    @Test
    fun testGetSiblings() {
        val doc = Jsoup.parse("<div><p>Hello<p id=1>there<p>this<p>is<p>an<p id=last>element</div>")
        val p = doc.getElementById("1")
        Assertions.assertEquals("there", p!!.text())
        Assertions.assertEquals("Hello", p.previousElementSibling()!!.text())
        Assertions.assertEquals("this", p.nextElementSibling()!!.text())
        Assertions.assertEquals("Hello", p.firstElementSibling()!!.text())
        Assertions.assertEquals("element", p.lastElementSibling()!!.text())
        Assertions.assertNull(p.lastElementSibling()!!.nextElementSibling())
        Assertions.assertNull(p.firstElementSibling()!!.previousElementSibling())
    }

    @Test
    fun nextElementSibling() {
        val doc = Jsoup.parse("<p>One</p>Two<p>Three</p>")
        val el = doc.expectFirst("p")
        Assertions.assertNull(el.previousElementSibling())
        val next = el.nextElementSibling()
        Assertions.assertNotNull(next)
        Assertions.assertEquals("Three", next!!.text())
        Assertions.assertNull(next.nextElementSibling())
    }

    @Test
    fun prevElementSibling() {
        val doc = Jsoup.parse("<p>One</p>Two<p>Three</p>")
        val el = doc.expectFirst("p:contains(Three)")
        Assertions.assertNull(el.nextElementSibling())
        val prev = el.previousElementSibling()
        Assertions.assertNotNull(prev)
        Assertions.assertEquals("One", prev!!.text())
        Assertions.assertNull(prev.previousElementSibling())
    }

    @Test
    fun testGetSiblingsWithDuplicateContent() {
        val doc = Jsoup.parse("<div><p>Hello<p id=1>there<p>this<p>this<p>is<p>an<p id=last>element</div>")
        val p = doc.getElementById("1")
        Assertions.assertEquals("there", p!!.text())
        Assertions.assertEquals("Hello", p.previousElementSibling()!!.text())
        Assertions.assertEquals("this", p.nextElementSibling()!!.text())
        Assertions.assertEquals("this", p.nextElementSibling()!!.nextElementSibling()!!.text())
        Assertions.assertEquals(
            "is", p.nextElementSibling()!!.nextElementSibling()!!
                .nextElementSibling()!!.text()
        )
        Assertions.assertEquals("Hello", p.firstElementSibling()!!.text())
        Assertions.assertEquals("element", p.lastElementSibling()!!.text())
    }

    @Test
    fun testFirstElementSiblingOnOrphan() {
        val p = Element("p")
        Assertions.assertSame(p, p.firstElementSibling())
        Assertions.assertSame(p, p.lastElementSibling())
    }

    @Test
    fun testFirstAndLastSiblings() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three")
        val div = doc.expectFirst("div")
        val one = div.child(0)
        val two = div.child(1)
        val three = div.child(2)
        Assertions.assertSame(one, one.firstElementSibling())
        Assertions.assertSame(one, two.firstElementSibling())
        Assertions.assertSame(three, three.lastElementSibling())
        Assertions.assertSame(three, two.lastElementSibling())
        Assertions.assertNull(one.previousElementSibling())
        Assertions.assertNull(three.nextElementSibling())
    }

    @Test
    fun testGetParents() {
        val doc = Jsoup.parse("<div><p>Hello <span>there</span></div>")
        val span = doc.select("span").first()
        val parents = span!!.parents()
        Assertions.assertEquals(4, parents.size)
        Assertions.assertEquals("p", parents[0].tagName())
        Assertions.assertEquals("div", parents[1].tagName())
        Assertions.assertEquals("body", parents[2].tagName())
        Assertions.assertEquals("html", parents[3].tagName())
        val orphan = Element("p")
        val none = orphan.parents()
        Assertions.assertEquals(0, none.size)
    }

    @Test
    fun testElementSiblingIndex() {
        val doc = Jsoup.parse("<div><p>One</p>...<p>Two</p>...<p>Three</p>")
        val ps = doc.select("p")
        Assertions.assertEquals(0, ps[0].elementSiblingIndex())
        Assertions.assertEquals(1, ps[1].elementSiblingIndex())
        Assertions.assertEquals(2, ps[2].elementSiblingIndex())
    }

    @Test
    fun testElementSiblingIndexSameContent() {
        val doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>")
        val ps = doc.select("p")
        Assertions.assertEquals(0, ps[0].elementSiblingIndex())
        Assertions.assertEquals(1, ps[1].elementSiblingIndex())
        Assertions.assertEquals(2, ps[2].elementSiblingIndex())
    }

    @Test
    fun testGetElementsWithClass() {
        val doc =
            Jsoup.parse("<div class='mellow yellow'><span class=mellow>Hello <b class='yellow'>Yellow!</b></span><p>Empty</p></div>")
        val els: List<Element> = doc.getElementsByClass("mellow")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("div", els[0].tagName())
        Assertions.assertEquals("span", els[1].tagName())
        val els2: List<Element> = doc.getElementsByClass("yellow")
        Assertions.assertEquals(2, els2.size)
        Assertions.assertEquals("div", els2[0].tagName())
        Assertions.assertEquals("b", els2[1].tagName())
        val none: List<Element> = doc.getElementsByClass("solo")
        Assertions.assertEquals(0, none.size)
    }

    @Test
    fun testGetElementsWithAttribute() {
        val doc = Jsoup.parse("<div style='bold'><p title=qux><p><b style></b></p></div>")
        val els: List<Element> = doc.getElementsByAttribute("style")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("div", els[0].tagName())
        Assertions.assertEquals("b", els[1].tagName())
        val none: List<Element> = doc.getElementsByAttribute("class")
        Assertions.assertEquals(0, none.size)
    }

    @Test
    fun testGetElementsWithAttributeDash() {
        val doc =
            Jsoup.parse("<meta http-equiv=content-type value=utf8 id=1> <meta name=foo content=bar id=2> <div http-equiv=content-type value=utf8 id=3>")
        val meta = doc.select("meta[http-equiv=content-type], meta[charset]")
        Assertions.assertEquals(1, meta.size)
        Assertions.assertEquals("1", meta.first()!!.id())
    }

    @Test
    fun testGetElementsWithAttributeValue() {
        val doc = Jsoup.parse("<div style='bold'><p><p><b style></b></p></div>")
        val els: List<Element> = doc.getElementsByAttributeValue("style", "bold")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("div", els[0].tagName())
        val none: List<Element> = doc.getElementsByAttributeValue("style", "none")
        Assertions.assertEquals(0, none.size)
    }

    @Test
    fun testClassDomMethods() {
        val doc = Jsoup.parse("<div><span class=' mellow yellow '>Hello <b>Yellow</b></span></div>")
        val els: List<Element> = doc.getElementsByAttribute("class")
        val span = els[0]
        Assertions.assertEquals("mellow yellow", span.className())
        Assertions.assertTrue(span.hasClass("mellow"))
        Assertions.assertTrue(span.hasClass("yellow"))
        var classes: Set<String?> = span.classNames()
        Assertions.assertEquals(2, classes.size)
        Assertions.assertTrue(classes.contains("mellow"))
        Assertions.assertTrue(classes.contains("yellow"))
        Assertions.assertEquals("", doc.className())
        classes = doc.classNames()
        Assertions.assertEquals(0, classes.size)
        Assertions.assertFalse(doc.hasClass("mellow"))
    }

    @Test
    fun testHasClassDomMethods() {
        val tag = Tag.valueOf("a")
        val attribs = Attributes()
        val el = Element(tag, "", attribs)
        attribs.put("class", "toto")
        var hasClass = el.hasClass("toto")
        Assertions.assertTrue(hasClass)
        attribs.put("class", " toto")
        hasClass = el.hasClass("toto")
        Assertions.assertTrue(hasClass)
        attribs.put("class", "toto ")
        hasClass = el.hasClass("toto")
        Assertions.assertTrue(hasClass)
        attribs.put("class", "\ttoto ")
        hasClass = el.hasClass("toto")
        Assertions.assertTrue(hasClass)
        attribs.put("class", "  toto ")
        hasClass = el.hasClass("toto")
        Assertions.assertTrue(hasClass)
        attribs.put("class", "ab")
        hasClass = el.hasClass("toto")
        Assertions.assertFalse(hasClass)
        attribs.put("class", "     ")
        hasClass = el.hasClass("toto")
        Assertions.assertFalse(hasClass)
        attribs.put("class", "tototo")
        hasClass = el.hasClass("toto")
        Assertions.assertFalse(hasClass)
        attribs.put("class", "raulpismuth  ")
        hasClass = el.hasClass("raulpismuth")
        Assertions.assertTrue(hasClass)
        attribs.put("class", " abcd  raulpismuth efgh ")
        hasClass = el.hasClass("raulpismuth")
        Assertions.assertTrue(hasClass)
        attribs.put("class", " abcd efgh raulpismuth")
        hasClass = el.hasClass("raulpismuth")
        Assertions.assertTrue(hasClass)
        attribs.put("class", " abcd efgh raulpismuth ")
        hasClass = el.hasClass("raulpismuth")
        Assertions.assertTrue(hasClass)
    }

    @Test
    fun testClassUpdates() {
        val doc = Jsoup.parse("<div class='mellow yellow'></div>")
        val div = doc.select("div").first()
        div!!.addClass("green")
        Assertions.assertEquals("mellow yellow green", div.className())
        div.removeClass("red") // noop
        div.removeClass("yellow")
        Assertions.assertEquals("mellow green", div.className())
        div.toggleClass("green").toggleClass("red")
        Assertions.assertEquals("mellow red", div.className())
    }

    @Test
    fun testOuterHtml() {
        val doc = Jsoup.parse("<div title='Tags &amp;c.'><img src=foo.png><p><!-- comment -->Hello<p>there")
        Assertions.assertEquals(
            "<html><head></head><body><div title=\"Tags &amp;c.\"><img src=\"foo.png\"><p><!-- comment -->Hello</p><p>there</p></div></body></html>",
            TextUtil.stripNewlines(doc.outerHtml())
        )
    }

    @Test
    fun testInnerHtml() {
        val doc = Jsoup.parse("<div>\n <p>Hello</p> </div>")
        Assertions.assertEquals("<p>Hello</p>", doc.getElementsByTag("div")[0].html())
    }

    @Test
    fun testFormatHtml() {
        val doc =
            Jsoup.parse("<title>Format test</title><div><p>Hello <span>jsoup <span>users</span></span></p><p>Good.</p></div>")
        Assertions.assertEquals(
            "<html>\n <head>\n  <title>Format test</title>\n </head>\n <body>\n  <div>\n   <p>Hello <span>jsoup <span>users</span></span></p>\n   <p>Good.</p>\n  </div>\n </body>\n</html>",
            doc.html()
        )
    }

    @Test
    fun testFormatOutline() {
        val doc =
            Jsoup.parse("<title>Format test</title><div><p>Hello <span>jsoup <span>users</span></span></p><p>Good.</p></div>")
        doc.outputSettings().outline(true)
        Assertions.assertEquals(
            "<html>\n <head>\n  <title>Format test</title>\n </head>\n <body>\n  <div>\n   <p>\n    Hello \n    <span>\n     jsoup \n     <span>users</span>\n    </span>\n   </p>\n   <p>Good.</p>\n  </div>\n </body>\n</html>",
            doc.html()
        )
    }

    @Test
    fun testSetIndent() {
        val doc = Jsoup.parse("<div><p>Hello\nthere</p></div>")
        doc.outputSettings().indentAmount(0)
        Assertions.assertEquals(
            "<html>\n<head></head>\n<body>\n<div>\n<p>Hello there</p>\n</div>\n</body>\n</html>",
            doc.html()
        )
    }

    @Test
    fun testIndentLevel() {
        // deep to test default and extended max
        val divs = StringBuilder()
        for (i in 0..39) {
            divs.append("<div>")
        }
        divs.append("Foo")
        val doc = Jsoup.parse(divs.toString())
        val settings = doc.outputSettings()
        val defaultMax = 30
        Assertions.assertEquals(defaultMax, settings.maxPaddingWidth())
        var html = doc.html()
        Assertions.assertTrue(
            html.contains(
                """                              <div>
                              Foo
                              </div>"""
            )
        )
        settings.maxPaddingWidth(32)
        Assertions.assertEquals(32, settings.maxPaddingWidth())
        html = doc.html()
        Assertions.assertTrue(
            html.contains(
                """                                <div>
                                Foo
                                </div>"""
            )
        )
        settings.maxPaddingWidth(-1)
        Assertions.assertEquals(-1, settings.maxPaddingWidth())
        html = doc.html()
        Assertions.assertTrue(
            html.contains(
                """                                         <div>
                                          Foo
                                         </div>"""
            )
        )
    }

    @Test
    fun testNotPretty() {
        val doc = Jsoup.parse("<div>   \n<p>Hello\n there\n</p></div>")
        doc.outputSettings().prettyPrint(false)
        Assertions.assertEquals(
            "<html><head></head><body><div>   \n<p>Hello\n there\n</p></div></body></html>",
            doc.html()
        )
        val div = doc.select("div").first()
        Assertions.assertEquals("   \n<p>Hello\n there\n</p>", div!!.html())
    }

    @Test
    fun testNotPrettyWithEnDashBody() {
        val html = "<div><span>1:15</span>&ndash;<span>2:15</span>&nbsp;p.m.</div>"
        val document = Jsoup.parse(html)
        document.outputSettings().prettyPrint(false)
        Assertions.assertEquals("<div><span>1:15</span>–<span>2:15</span>&nbsp;p.m.</div>", document.body().html())
    }

    @Test
    fun testPrettyWithEnDashBody() {
        val html = "<div><span>1:15</span>&ndash;<span>2:15</span>&nbsp;p.m.</div>"
        val document = Jsoup.parse(html)
        Assertions.assertEquals("<div>\n <span>1:15</span>–<span>2:15</span>&nbsp;p.m.\n</div>", document.body().html())
    }

    @Test
    fun testPrettyAndOutlineWithEnDashBody() {
        val html = "<div><span>1:15</span>&ndash;<span>2:15</span>&nbsp;p.m.</div>"
        val document = Jsoup.parse(html)
        document.outputSettings().outline(true)
        Assertions.assertEquals(
            "<div>\n <span>1:15</span>\n –\n <span>2:15</span>\n &nbsp;p.m.\n</div>",
            document.body().html()
        )
    }

    @Test
    fun testBasicFormats() {
        val html = "<span>0</span>.<div><span>1</span>-<span>2</span><p><span>3</span>-<span>4</span><div>5</div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            """<span>0</span>.
<div>
 <span>1</span>-<span>2</span>
 <p><span>3</span>-<span>4</span></p>
 <div>
  5
 </div>
</div>""", doc.body().html()
        )
    }

    @Test
    fun testEmptyElementFormatHtml() {
        // don't put newlines into empty blocks
        val doc = Jsoup.parse("<section><div></div></section>")
        Assertions.assertEquals(
            "<section>\n <div></div>\n</section>", doc.select("section").first()!!
                .outerHtml()
        )
    }

    @Test
    fun testNoIndentOnScriptAndStyle() {
        // don't newline+indent closing </script> and </style> tags
        val doc = Jsoup.parse("<script>one\ntwo</script>\n<style>three\nfour</style>")
        Assertions.assertEquals("<script>one\ntwo</script>\n<style>three\nfour</style>", doc.head().html())
    }

    @Test
    fun testContainerOutput() {
        val doc = Jsoup.parse("<title>Hello there</title> <div><p>Hello</p><p>there</p></div> <div>Another</div>")
        Assertions.assertEquals("<title>Hello there</title>", doc.select("title").first()!!.outerHtml())
        Assertions.assertEquals(
            "<div>\n <p>Hello</p>\n <p>there</p>\n</div>", doc.select("div").first()!!
                .outerHtml()
        )
        Assertions.assertEquals(
            "<div>\n <p>Hello</p>\n <p>there</p>\n</div>\n<div>\n Another\n</div>", doc.select("body").first()!!
                .html()
        )
    }

    @Test
    fun testSetText() {
        val h = "<div id=1>Hello <p>there <b>now</b></p></div>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("Hello there now", doc.text()) // need to sort out node whitespace
        Assertions.assertEquals("there now", doc.select("p")[0].text())
        val div = doc.getElementById("1")!!.text("Gone")
        Assertions.assertEquals("Gone", div.text())
        Assertions.assertEquals(0, doc.select("p").size)
    }

    @Test
    fun testAddNewElement() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.appendElement("p").text("there")
        div.appendElement("P").attr("CLASS", "second").text("now")
        // manually specifying tag and attributes should maintain case based on parser settings
        Assertions.assertEquals(
            "<html><head></head><body><div id=\"1\"><p>Hello</p><p>there</p><p class=\"second\">now</p></div></body></html>",
            TextUtil.stripNewlines(doc.html())
        )

        // check sibling index (with short circuit on reindexChildren):
        val ps = doc.select("p")
        for (i in ps.indices) {
            Assertions.assertEquals(i, ps[i].siblingIndex)
        }
    }

    @Test
    fun testAddBooleanAttribute() {
        val div = Element(Tag.valueOf("div"), "")
        div.attr("true", true)
        div.attr("false", "value")
        div.attr("false", false)
        Assertions.assertTrue(div.hasAttr("true"))
        Assertions.assertEquals("", div.attr("true"))
        val attributes = div.attributes().asList()
        Assertions.assertEquals(1, attributes.size, "There should be one attribute")
        Assertions.assertFalse(div.hasAttr("false"))
        Assertions.assertEquals("<div true></div>", div.outerHtml())
    }

    @Test
    fun testAppendRowToTable() {
        val doc = Jsoup.parse("<table><tr><td>1</td></tr></table>")
        val table = doc.select("tbody").first()
        table!!.append("<tr><td>2</td></tr>")
        Assertions.assertEquals(
            "<table><tbody><tr><td>1</td></tr><tr><td>2</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testPrependRowToTable() {
        val doc = Jsoup.parse("<table><tr><td>1</td></tr></table>")
        val table = doc.select("tbody").first()
        table!!.prepend("<tr><td>2</td></tr>")
        Assertions.assertEquals(
            "<table><tbody><tr><td>2</td></tr><tr><td>1</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )

        // check sibling index (reindexChildren):
        val ps = doc.select("tr")
        for (i in ps.indices) {
            Assertions.assertEquals(i, ps[i].siblingIndex)
        }
    }

    @Test
    fun testPrependElement() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.prependElement("p").text("Before")
        Assertions.assertEquals("Before", div.child(0).text())
        Assertions.assertEquals("Hello", div.child(1).text())
    }

    @Test
    fun testAddNewText() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.appendText(" there & now >")
        Assertions.assertEquals("Hello there & now >", div.text())
        Assertions.assertEquals(
            "<p>Hello</p> there &amp; now &gt;", TextUtil.stripNewlines(
                div.html()
            )
        )
    }

    @Test
    fun testPrependText() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.prependText("there & now > ")
        Assertions.assertEquals("there & now > Hello", div.text())
        Assertions.assertEquals(
            "there &amp; now &gt; <p>Hello</p>", TextUtil.stripNewlines(
                div.html()
            )
        )
    }

    @Test
    fun testThrowsOnAddNullText() {
        Assertions.assertThrows(NullPointerException::class.java) {
            val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
            val div = doc.getElementById("1")
            div!!.appendText(null as String)
        }
    }

    @Test
    fun testThrowsOnPrependNullText() {
        Assertions.assertThrows(NullPointerException::class.java) {
            val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
            val div = doc.getElementById("1")
            div!!.prependText(null as String)
        }
    }

    @Test
    fun testAddNewHtml() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.append("<p>there</p><p>now</p>")
        Assertions.assertEquals(
            "<p>Hello</p><p>there</p><p>now</p>", TextUtil.stripNewlines(
                div.html()
            )
        )

        // check sibling index (no reindexChildren):
        val ps = doc.select("p")
        for (i in ps.indices) {
            Assertions.assertEquals(i, ps[i].siblingIndex)
        }
    }

    @Test
    fun testPrependNewHtml() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.prepend("<p>there</p><p>now</p>")
        Assertions.assertEquals(
            "<p>there</p><p>now</p><p>Hello</p>", TextUtil.stripNewlines(
                div.html()
            )
        )

        // check sibling index (reindexChildren):
        val ps = doc.select("p")
        for (i in ps.indices) {
            Assertions.assertEquals(i, ps[i].siblingIndex)
        }
    }

    @Test
    fun prependNodes() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val p = doc.expectFirst("p")
        p.prepend("Text <!-- comment --> ")
        Assertions.assertEquals("Text <!-- comment --> Hello", TextUtil.stripNewlines(p.html()))
    }

    @Test
    fun appendNodes() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val p = doc.expectFirst("p")
        p.append(" Text <!-- comment -->")
        Assertions.assertEquals("Hello Text <!-- comment -->", TextUtil.stripNewlines(p.html()))
    }

    @Test
    fun testSetHtml() {
        val doc = Jsoup.parse("<div id=1><p>Hello</p></div>")
        val div = doc.getElementById("1")
        div!!.html("<p>there</p><p>now</p>")
        Assertions.assertEquals("<p>there</p><p>now</p>", TextUtil.stripNewlines(div.html()))
    }

    @Test
    fun testSetHtmlTitle() {
        val doc = Jsoup.parse("<html><head id=2><title id=1></title></head></html>")
        val title = doc.getElementById("1")
        title!!.html("good")
        Assertions.assertEquals("good", title.html())
        title.html("<i>bad</i>")
        Assertions.assertEquals("&lt;i&gt;bad&lt;/i&gt;", title.html())
        val head = doc.getElementById("2")
        head!!.html("<title><i>bad</i></title>")
        Assertions.assertEquals("<title>&lt;i&gt;bad&lt;/i&gt;</title>", head.html())
    }

    @Test
    fun testWrap() {
        val doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>")
        val p = doc.select("p").first()
        p!!.wrap("<div class='head'></div>")
        Assertions.assertEquals(
            "<div><div class=\"head\"><p>Hello</p></div><p>There</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
        val ret = p.wrap("<div><div class=foo></div><p>What?</p></div>")
        Assertions.assertEquals(
            "<div><div class=\"head\"><div><div class=\"foo\"><p>Hello</p></div><p>What?</p></div></div><p>There</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
        Assertions.assertEquals(ret, p)
    }

    @Test
    fun testWrapNoop() {
        val doc = Jsoup.parse("<div><p>Hello</p></div>")
        val p: Node? = doc.select("p").first()
        val wrapped = p!!.wrap("Some junk")
        Assertions.assertSame(p, wrapped)
        Assertions.assertEquals("<div><p>Hello</p></div>", TextUtil.stripNewlines(doc.body().html()))
        // should be a NOOP
    }

    @Test
    fun testWrapOnOrphan() {
        val orphan = Element("span").text("Hello!")
        Assertions.assertFalse(orphan.hasParent())
        val wrapped = orphan.wrap("<div></div> There!")
        Assertions.assertSame(orphan, wrapped)
        Assertions.assertTrue(orphan.hasParent()) // should now be in the DIV
        Assertions.assertNotNull(orphan.parent())
        Assertions.assertEquals("div", orphan.parent()!!.tagName())
        Assertions.assertEquals("<div>\n <span>Hello!</span>\n</div>", orphan.parent()!!.outerHtml())
    }

    @Test
    fun testWrapArtificialStructure() {
        // div normally couldn't get into a p, but explicitly want to wrap
        val doc = Jsoup.parse("<p>Hello <i>there</i> now.")
        val i = doc.selectFirst("i")
        i!!.wrap("<div id=id1></div> quite")
        Assertions.assertEquals("div", i.parent()!!.tagName())
        Assertions.assertEquals(
            "<p>Hello <div id=\"id1\"><i>there</i></div> quite now.</p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun before() {
        val doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>")
        val p1 = doc.select("p").first()
        p1!!.before("<div>one</div><div>two</div>")
        Assertions.assertEquals(
            "<div><div>one</div><div>two</div><p>Hello</p><p>There</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
        doc.select("p").last()!!.before("<p>Three</p><!-- four -->")
        Assertions.assertEquals(
            "<div><div>one</div><div>two</div><p>Hello</p><p>Three</p><!-- four --><p>There</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun after() {
        val doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>")
        val p1 = doc.select("p").first()
        p1!!.after("<div>one</div><div>two</div>")
        Assertions.assertEquals(
            "<div><p>Hello</p><div>one</div><div>two</div><p>There</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
        doc.select("p").last()!!.after("<p>Three</p><!-- four -->")
        Assertions.assertEquals(
            "<div><p>Hello</p><div>one</div><div>two</div><p>There</p><p>Three</p><!-- four --></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testWrapWithRemainder() {
        val doc = Jsoup.parse("<div><p>Hello</p></div>")
        val p = doc.select("p").first()
        p!!.wrap("<div class='head'></div><p>There!</p>")
        Assertions.assertEquals(
            "<div><div class=\"head\"><p>Hello</p></div><p>There!</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testWrapWithSimpleRemainder() {
        val doc = Jsoup.parse("<p>Hello")
        val p = doc.selectFirst("p")
        val body = p!!.parent()
        Assertions.assertNotNull(body)
        Assertions.assertEquals("body", body!!.tagName())
        p.wrap("<div></div> There")
        val div = p.parent()
        Assertions.assertNotNull(div)
        Assertions.assertEquals("div", div!!.tagName())
        Assertions.assertSame(div, p.parent())
        Assertions.assertSame(body, div.parent())
        Assertions.assertEquals("<div><p>Hello</p></div> There", TextUtil.stripNewlines(doc.body().html()))
    }

    @Test
    fun testHasText() {
        val doc = Jsoup.parse("<div><p>Hello</p><p></p></div>")
        val div = doc.select("div").first()
        val ps = doc.select("p")
        Assertions.assertTrue(div!!.hasText())
        Assertions.assertTrue(ps.first()!!.hasText())
        Assertions.assertFalse(ps.last()!!.hasText())
    }

    @Test
    fun dataset() {
        val doc = Jsoup.parse("<div id=1 data-name=jsoup class=new data-package=jar>Hello</div><p id=2>Hello</p>")
        val div = doc.select("div").first()
        val dataset = div!!.dataset() as MutableMap
        val attributes = div.attributes()

        // size, get, set, add, remove
        Assertions.assertEquals(2, dataset.size)
        Assertions.assertEquals("jsoup", dataset["name"])
        Assertions.assertEquals("jar", dataset["package"])
        dataset.put("name", "jsoup updated")
        dataset.put("language", "java")
        dataset.remove("package")
        Assertions.assertEquals(2, dataset.size)
        Assertions.assertEquals(4, attributes.size())
        Assertions.assertEquals("jsoup updated", attributes["data-name"])
        Assertions.assertEquals("jsoup updated", dataset["name"])
        Assertions.assertEquals("java", attributes["data-language"])
        Assertions.assertEquals("java", dataset["language"])
        attributes.put("data-food", "bacon")
        Assertions.assertEquals(3, dataset.size)
        Assertions.assertEquals("bacon", dataset["food"])
        attributes.put("data-", "empty")
        Assertions.assertNull(dataset[""]) // data- is not a data attribute
        val p = doc.select("p").first()
        Assertions.assertEquals(0, p!!.dataset().size)
    }

    @Test
    fun parentlessToString() {
        val doc = Jsoup.parse("<img src='foo'>")
        val img = doc.select("img").first()
        Assertions.assertEquals("<img src=\"foo\">", img.toString())
        img!!.remove() // lost its parent
        Assertions.assertEquals("<img src=\"foo\">", img.toString())
    }

    @Test
    fun orphanDivToString() {
        val orphan = Element("div").id("foo").text("Hello")
        Assertions.assertEquals("<div id=\"foo\">\n Hello\n</div>", orphan.toString())
    }

    @Test
    fun testClone() {
        val doc = Jsoup.parse("<div><p>One<p><span>Two</div>")
        val p = doc.select("p")[1]
        val clone = p.clone()
        Assertions.assertNotNull(clone.parentNode) // should be a cloned document just containing this clone
        Assertions.assertEquals(1, clone.parentNode!!.childNodeSize())
        Assertions.assertSame(clone.ownerDocument(), clone.parentNode)
        Assertions.assertEquals(0, clone.siblingIndex)
        Assertions.assertEquals(1, p.siblingIndex)
        Assertions.assertNotNull(p.parent())
        clone.append("<span>Three")
        Assertions.assertEquals("<p><span>Two</span><span>Three</span></p>", TextUtil.stripNewlines(clone.outerHtml()))
        Assertions.assertEquals(
            "<div><p>One</p><p><span>Two</span></p></div>",
            TextUtil.stripNewlines(doc.body().html())
        ) // not modified
        doc.body().appendChild(clone) // adopt
        Assertions.assertNotNull(clone.parent())
        Assertions.assertEquals(
            "<div><p>One</p><p><span>Two</span></p></div><p><span>Two</span><span>Three</span></p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testClonesClassnames() {
        val doc = Jsoup.parse("<div class='one two'></div>")
        val div = doc.select("div").first()
        val classes: Set<String> = div!!.classNames()
        Assertions.assertEquals(2, classes.size)
        Assertions.assertTrue(classes.contains("one"))
        Assertions.assertTrue(classes.contains("two"))
        val copy = div.clone()
        val copyClasses = copy.classNames()
        Assertions.assertEquals(2, copyClasses.size)
        Assertions.assertTrue(copyClasses.contains("one"))
        Assertions.assertTrue(copyClasses.contains("two"))
        copyClasses.add("three")
        copyClasses.remove("one")
        Assertions.assertTrue(classes.contains("one"))
        Assertions.assertFalse(classes.contains("three"))
        Assertions.assertFalse(copyClasses.contains("one"))
        Assertions.assertTrue(copyClasses.contains("three"))
        Assertions.assertEquals("", div.html())
        Assertions.assertEquals("", copy.html())
    }

    @Test
    fun testShallowClone() {
        val base = "http://example.com/"
        val doc = Jsoup.parse("<div id=1 class=one><p id=2 class=two>One", base)
        val d = doc.selectFirst("div")
        val p = doc.selectFirst("p")
        val t = p!!.textNodes()[0]
        val d2 = d!!.shallowClone()
        val p2 = p.shallowClone()
        val t2 = t.shallowClone() as TextNode?
        Assertions.assertEquals(1, d.childNodeSize())
        Assertions.assertEquals(0, d2.childNodeSize())
        Assertions.assertEquals(1, p.childNodeSize())
        Assertions.assertEquals(0, p2.childNodeSize())
        Assertions.assertEquals("", p2.text())
        Assertions.assertEquals("One", t2!!.text())
        Assertions.assertEquals("two", p2.className())
        p2.removeClass("two")
        Assertions.assertEquals("two", p.className())
        d2.append("<p id=3>Three")
        Assertions.assertEquals(1, d2.childNodeSize())
        Assertions.assertEquals("Three", d2.text())
        Assertions.assertEquals("One", d.text())
        Assertions.assertEquals(base, d2.baseUri())
    }

    @Test
    fun testTagNameSet() {
        val doc = Jsoup.parse("<div><i>Hello</i>")
        doc.select("i").first()!!.tagName("em")
        Assertions.assertEquals(0, doc.select("i").size)
        Assertions.assertEquals(1, doc.select("em").size)
        Assertions.assertEquals("<em>Hello</em>", doc.select("div").first()!!.html())
    }

    @Test
    fun testHtmlContainsOuter() {
        val doc = Jsoup.parse("<title>Check</title> <div>Hello there</div>")
        doc.outputSettings().indentAmount(0)
        Assertions.assertTrue(doc.html().contains(doc.select("title").outerHtml()))
        Assertions.assertTrue(doc.html().contains(doc.select("div").outerHtml()))
    }

    @Test
    fun testGetTextNodes() {
        val doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>")
        val textNodes = doc.select("p").first()!!.textNodes()
        Assertions.assertEquals(3, textNodes.size)
        Assertions.assertEquals("One ", textNodes[0].text())
        Assertions.assertEquals(" Three ", textNodes[1].text())
        Assertions.assertEquals(" Four", textNodes[2].text())
        Assertions.assertEquals(0, doc.select("br").first()!!.textNodes().size)
    }

    @Test
    fun testManipulateTextNodes() {
        val doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>")
        val p = doc.select("p").first()
        val textNodes = p!!.textNodes()
        textNodes[1].text(" three-more ")
        textNodes[2].splitText(3).text("-ur")
        Assertions.assertEquals("One Two three-more Fo-ur", p.text())
        Assertions.assertEquals("One three-more Fo-ur", p.ownText())
        Assertions.assertEquals(4, p.textNodes().size) // grew because of split
    }

    @Test
    fun testGetDataNodes() {
        val doc = Jsoup.parse("<script>One Two</script> <style>Three Four</style> <p>Fix Six</p>")
        val script = doc.select("script").first()
        val style = doc.select("style").first()
        val p = doc.select("p").first()
        val scriptData = script!!.dataNodes()
        Assertions.assertEquals(1, scriptData.size)
        Assertions.assertEquals("One Two", scriptData[0].wholeData)
        val styleData = style!!.dataNodes()
        Assertions.assertEquals(1, styleData.size)
        Assertions.assertEquals("Three Four", styleData[0].wholeData)
        val pData = p!!.dataNodes()
        Assertions.assertEquals(0, pData.size)
    }

    @Test
    fun elementIsNotASiblingOfItself() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>")
        val p2 = doc.select("p")[1]
        Assertions.assertEquals("Two", p2.text())
        val els = p2.siblingElements()
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("<p>One</p>", els[0].outerHtml())
        Assertions.assertEquals("<p>Three</p>", els[1].outerHtml())
    }

    @Test
    fun testChildThrowsIndexOutOfBoundsOnMissing() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p></div>")
        val div = doc.select("div").first()
        Assertions.assertEquals(2, div!!.children().size)
        Assertions.assertEquals("One", div.child(0).text())
        try {
            div.child(3)
            Assertions.fail<Any>("Should throw index out of bounds")
        } catch (e: IndexOutOfBoundsException) {
        }
    }

    @Test
    fun moveByAppend() {
        // test for https://github.com/jhy/jsoup/issues/239
        // can empty an element and append its children to another element
        val doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>")
        val div1 = doc.select("div")[0]
        val div2 = doc.select("div")[1]
        Assertions.assertEquals(4, div1.childNodeSize())
        val children = div1.childNodes()
        Assertions.assertEquals(4, children.size)
        div2.insertChildren(0, children)
        Assertions.assertEquals(
            4,
            children.size
        ) // children is NOT backed by div1.childNodes but a wrapper, so should still be 4 (but re-parented)
        Assertions.assertEquals(0, div1.childNodeSize())
        Assertions.assertEquals(4, div2.childNodeSize())
        Assertions.assertEquals(
            "<div id=\"1\"></div>\n<div id=\"2\">\n Text \n <p>One</p> Text \n <p>Two</p>\n</div>",
            doc.body().html()
        )
    }

    @Test
    fun insertChildrenArgumentValidation() {
        val doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>")
        val div1 = doc.select("div")[0]
        val div2 = doc.select("div")[1]
        val children = div1.childNodes()
        try {
            div2.insertChildren(6, children)
            Assertions.fail<Any>()
        } catch (e: IllegalArgumentException) {
        }
        try {
            div2.insertChildren(-5, children)
            Assertions.fail<Any>()
        } catch (e: IllegalArgumentException) {
        }
        try {
            div2.insertChildren(0, null as Collection<Node>)
            Assertions.fail<Any>()
        } catch (e: NullPointerException) {
        }
    }

    @Test
    fun insertChildrenAtPosition() {
        val doc = Jsoup.parse("<div id=1>Text1 <p>One</p> Text2 <p>Two</p></div><div id=2>Text3 <p>Three</p></div>")
        val div1 = doc.select("div")[0]
        val p1s = div1.select("p")
        val div2 = doc.select("div")[1]
        Assertions.assertEquals(2, div2.childNodeSize())
        div2.insertChildren(-1, p1s)
        Assertions.assertEquals(2, div1.childNodeSize()) // moved two out
        Assertions.assertEquals(4, div2.childNodeSize())
        Assertions.assertEquals(3, p1s[1].siblingIndex) // should be last
        val els: MutableList<Node> = ArrayList()
        val el1 = Element(Tag.valueOf("span"), "").text("Span1")
        val el2 = Element(Tag.valueOf("span"), "").text("Span2")
        val tn1 = TextNode("Text4")
        els.add(el1)
        els.add(el2)
        els.add(tn1)
        Assertions.assertNull(el1.parent())
        div2.insertChildren(-2, els)
        Assertions.assertEquals(div2, el1.parent())
        Assertions.assertEquals(7, div2.childNodeSize())
        Assertions.assertEquals(3, el1.siblingIndex)
        Assertions.assertEquals(4, el2.siblingIndex)
        Assertions.assertEquals(5, tn1.siblingIndex)
    }

    @Test
    fun insertChildrenAsCopy() {
        val doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>")
        val div1 = doc.select("div")[0]
        val div2 = doc.select("div")[1]
        val ps = doc.select("p").clone()
        ps.first()!!.text("One cloned")
        div2.insertChildren(-1, ps)
        Assertions.assertEquals(4, div1.childNodeSize()) // not moved -- cloned
        Assertions.assertEquals(2, div2.childNodeSize())
        Assertions.assertEquals(
            "<div id=\"1\">Text <p>One</p> Text <p>Two</p></div><div id=\"2\"><p>One cloned</p><p>Two</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testCssPath() {
        val doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>")
        val divA = doc.select("div")[0]
        val divB = doc.select("div")[1]
        val divC = doc.select("div")[2]
        Assertions.assertEquals(divA.cssSelector(), "#id1")
        Assertions.assertEquals(divB.cssSelector(), "html > body > div:nth-child(2)")
        Assertions.assertEquals(divC.cssSelector(), "html > body > div.c1.c2")
        Assertions.assertSame(divA, doc.select(divA.cssSelector()).first())
        Assertions.assertSame(divB, doc.select(divB.cssSelector()).first())
        Assertions.assertSame(divC, doc.select(divC.cssSelector()).first())
    }

    @Test
    fun testCssPathDuplicateIds() {
        // https://github.com/jhy/jsoup/issues/1147 - multiple elements with same ID, use the non-ID form
        val doc = Jsoup.parse("<article><div id=dupe>A</div><div id=dupe>B</div><div id=dupe class=c1>")
        val divA = doc.select("div")[0]
        val divB = doc.select("div")[1]
        val divC = doc.select("div")[2]
        Assertions.assertEquals(divA.cssSelector(), "html > body > article > div:nth-child(1)")
        Assertions.assertEquals(divB.cssSelector(), "html > body > article > div:nth-child(2)")
        Assertions.assertEquals(divC.cssSelector(), "html > body > article > div.c1")
        Assertions.assertSame(divA, doc.select(divA.cssSelector()).first())
        Assertions.assertSame(divB, doc.select(divB.cssSelector()).first())
        Assertions.assertSame(divC, doc.select(divC.cssSelector()).first())
    }

    @Test
    fun cssSelectorEscaped() {
        // https://github.com/jhy/jsoup/issues/1742
        val doc = Jsoup.parse("<p\\p>One</p\\p> <p id='one.two'>Two</p> <p class='one.two:three/four'>Three</p>")
        val one = doc.expectFirst("p\\\\p")
        val ps = doc.select("p")
        val two = ps[0]
        val three = ps[1]
        val oneSelect = one.cssSelector()
        Assertions.assertEquals("html > body > p\\\\p", oneSelect)
        Assertions.assertEquals(one, doc.expectFirst(oneSelect))
        val twoSelect = two.cssSelector()
        Assertions.assertEquals("#one\\.two", twoSelect)
        Assertions.assertEquals(two, doc.expectFirst(twoSelect))
        val threeSelect = three.cssSelector()
        Assertions.assertEquals("html > body > p.one\\.two\\:three\\/four", threeSelect)
        Assertions.assertEquals(three, doc.expectFirst(threeSelect))
    }

    @Test
    fun cssEscapedAmp() {
        val doc = Jsoup.parse("<p class='\\&'>One</p>")
        val one = doc.expectFirst(".\\\\\\&") // tested matches js querySelector
        Assertions.assertEquals("One", one.text())
        val q = one.cssSelector()
        Assertions.assertEquals("html > body > p.\\\\\\&", q)
        Assertions.assertEquals(one, doc.expectFirst(q))
    }

    @Test
    fun cssSelectorEscapedClass() {
        // example in https://github.com/jhy/jsoup/issues/838
        val html = "<div class='B\\&W\\?'><div class=test>Text</div></div>"
        val parse = Jsoup.parse(html)
        val el = parse.expectFirst(".test")
        Assertions.assertEquals("Text", el.text())
        val q = el.cssSelector()
        Assertions.assertEquals("html > body > div.B\\\\\\&W\\\\\\? > div.test", q)
        val found = parse.expectFirst(q)
        Assertions.assertEquals(found, el)
    }

    @Test
    fun testClassNames() {
        val doc = Jsoup.parse("<div class=\"c1 c2\">C</div>")
        val div = doc.select("div")[0]
        Assertions.assertEquals("c1 c2", div.className())
        val set1 = div.classNames()
        val arr1: Array<Any> = set1.toTypedArray()
        Assertions.assertEquals(2, arr1.size)
        Assertions.assertEquals("c1", arr1[0])
        Assertions.assertEquals("c2", arr1[1])

        // Changes to the set should not be reflected in the Elements getters
        set1.add("c3")
        Assertions.assertEquals(2, div.classNames().size)
        Assertions.assertEquals("c1 c2", div.className())

        // Update the class names to a fresh set
        val newSet: MutableSet<String> = LinkedHashSet(3)
        newSet.addAll(set1)
        newSet.add("c3")
        div.classNames(newSet)
        Assertions.assertEquals("c1 c2 c3", div.className())
        val set2: Set<String> = div.classNames()
        val arr2: Array<Any> = set2.toTypedArray()
        Assertions.assertEquals(3, arr2.size)
        Assertions.assertEquals("c1", arr2[0])
        Assertions.assertEquals("c2", arr2[1])
        Assertions.assertEquals("c3", arr2[2])
    }

    @Test
    fun testHashAndEqualsAndValue() {
        // .equals and hashcode are identity. value is content.
        val doc1 = "<div id=1><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>" +
                "<div id=2><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>"
        val doc = Jsoup.parse(doc1)
        val els = doc.select("p")

        /*
        for (Element el : els) {
            System.out.println(el.hashCode() + " - " + el.outerHtml());
        }

        0 1534787905 - <p class="one">One</p>
        1 1534787905 - <p class="one">One</p>
        2 1539683239 - <p class="one">Two</p>
        3 1535455211 - <p class="two">One</p>
        4 1534787905 - <p class="one">One</p>
        5 1534787905 - <p class="one">One</p>
        6 1539683239 - <p class="one">Two</p>
        7 1535455211 - <p class="two">One</p>
        */Assertions.assertEquals(8, els.size)
        val e0 = els[0]
        val e1 = els[1]
        val e2 = els[2]
        val e3 = els[3]
        val e4 = els[4]
        val e5 = els[5]
        val e6 = els[6]
        val e7 = els[7]
        Assertions.assertEquals(e0, e0)
        Assertions.assertTrue(e0.hasSameValue(e1))
        Assertions.assertTrue(e0.hasSameValue(e4))
        Assertions.assertTrue(e0.hasSameValue(e5))
        Assertions.assertNotEquals(e0, e2)
        Assertions.assertFalse(e0.hasSameValue(e2))
        Assertions.assertFalse(e0.hasSameValue(e3))
        Assertions.assertFalse(e0.hasSameValue(e6))
        Assertions.assertFalse(e0.hasSameValue(e7))
        Assertions.assertEquals(e0.hashCode(), e0.hashCode())
        Assertions.assertNotEquals(e0.hashCode(), e2.hashCode())
        Assertions.assertNotEquals(e0.hashCode(), e3.hashCode())
        Assertions.assertNotEquals(e0.hashCode(), e6.hashCode())
        Assertions.assertNotEquals(e0.hashCode(), e7.hashCode())
    }

    @Test
    fun testRelativeUrls() {
        val html =
            "<body><a href='./one.html'>One</a> <a href='two.html'>two</a> <a href='../three.html'>Three</a> <a href='//example2.com/four/'>Four</a> <a href='https://example2.com/five/'>Five</a> <a>Six</a> <a href=''>Seven</a>"
        val doc = Jsoup.parse(html, "http://example.com/bar/")
        val els = doc.select("a")
        Assertions.assertEquals("http://example.com/bar/one.html", els[0].absUrl("href"))
        Assertions.assertEquals("http://example.com/bar/two.html", els[1].absUrl("href"))
        Assertions.assertEquals("http://example.com/three.html", els[2].absUrl("href"))
        Assertions.assertEquals("http://example2.com/four/", els[3].absUrl("href"))
        Assertions.assertEquals("https://example2.com/five/", els[4].absUrl("href"))
        Assertions.assertEquals("", els[5].absUrl("href"))
        Assertions.assertEquals("http://example.com/bar/", els[6].absUrl("href"))
    }

    @Test
    fun testRelativeIdnUrls() {
        val idn = "https://www.测试.测试/"
        val idnFoo = idn + "foo.html?bar"
        val doc = Jsoup.parse("<a href=''>One</a><a href='/bar.html?qux'>Two</a>", idnFoo)
        val els = doc.select("a")
        val one = els[0]
        val two = els[1]
        val hrefOne = one.absUrl("href")
        val hrefTwo = two.absUrl("href")
        Assertions.assertEquals(idnFoo, hrefOne)
        Assertions.assertEquals("https://www.测试.测试/bar.html?qux", hrefTwo)
    }

    @Test
    fun appendMustCorrectlyMoveChildrenInsideOneParentElement() {
        val doc = Document("")
        val body = doc.appendElement("body")
        body.appendElement("div1")
        body.appendElement("div2")
        val div3 = body.appendElement("div3")
        div3.text("Check")
        val div4 = body.appendElement("div4")
        val toMove = ArrayList<Element>()
        toMove.add(div3)
        toMove.add(div4)
        body.insertChildren(0, toMove)
        val result = doc.toString().replace("\\s+".toRegex(), "")
        Assertions.assertEquals("<body><div3>Check</div3><div4></div4><div1></div1><div2></div2></body>", result)
    }

    @Test
    fun testHashcodeIsStableWithContentChanges() {
        val root = Element(Tag.valueOf("root"), "")
        val set = HashSet<Element>()
        // Add root node:
        set.add(root)
        root.appendChild(Element(Tag.valueOf("a"), ""))
        Assertions.assertTrue(set.contains(root))
    }

    @Test
    fun testNamespacedElements() {
        // Namespaces with ns:tag in HTML must be translated to ns|tag in CSS.
        val html = "<html><body><fb:comments /></body></html>"
        val doc = Jsoup.parse(html, "http://example.com/bar/")
        val els = doc.select("fb|comments")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("html > body > fb|comments", els[0].cssSelector())
    }

    @Test
    fun testChainedRemoveAttributes() {
        val html = "<a one two three four>Text</a>"
        val doc = Jsoup.parse(html)
        val a = doc.select("a").first()!!
        a
            .removeAttr("zero")
            .removeAttr("one")
            .removeAttr("two")
            .removeAttr("three")
            .removeAttr("four")
            .removeAttr("five")
        Assertions.assertEquals("<a>Text</a>", a.outerHtml())
    }

    @Test
    fun testLoopedRemoveAttributes() {
        val html = "<a one two three four>Text</a><p foo>Two</p>"
        val doc = Jsoup.parse(html)
        for (el in doc.allElements) {
            el.clearAttributes()
        }
        Assertions.assertEquals("<a>Text</a>\n<p>Two</p>", doc.body().html())
    }

    @Test
    fun testIs() {
        val html = "<div><p>One <a class=big>Two</a> Three</p><p>Another</p>"
        val doc = Jsoup.parse(html)
        val p = doc.select("p").first()
        Assertions.assertTrue(p!!.`is`("p"))
        Assertions.assertFalse(p.`is`("div"))
        Assertions.assertTrue(p.`is`("p:has(a)"))
        Assertions.assertFalse(p.`is`("a")) // does not descend
        Assertions.assertTrue(p.`is`("p:first-child"))
        Assertions.assertFalse(p.`is`("p:last-child"))
        Assertions.assertTrue(p.`is`("*"))
        Assertions.assertTrue(p.`is`("div p"))
        val q = doc.select("p").last()
        Assertions.assertTrue(q!!.`is`("p"))
        Assertions.assertTrue(q.`is`("p ~ p"))
        Assertions.assertTrue(q.`is`("p + p"))
        Assertions.assertTrue(q.`is`("p:last-child"))
        Assertions.assertFalse(q.`is`("p a"))
        Assertions.assertFalse(q.`is`("a"))
    }

    @Test
    fun testEvalMethods() {
        val doc = Jsoup.parse("<div><p>One <a class=big>Two</a> Three</p><p>Another</p>")
        val p = doc.selectFirst(QueryParser.parse("p"))
        Assertions.assertEquals("One Three", p!!.ownText())
        Assertions.assertTrue(p.`is`(QueryParser.parse("p")))
        val aEval = QueryParser.parse("a")
        Assertions.assertFalse(p.`is`(aEval))
        val a = p.selectFirst(aEval)
        Assertions.assertEquals(
            "div", a!!.closest(QueryParser.parse("div:has( > p)"))!!
                .tagName()
        )
        val body = p.closest(QueryParser.parse("body"))
        Assertions.assertEquals("body", body!!.nodeName())
    }

    @Test
    fun testClosest() {
        val html = """<article>
  <div id=div-01>Here is div-01
    <div id=div-02>Here is div-02
      <div id=div-03>Here is div-03</div>
    </div>
  </div>
</article>"""
        val doc = Jsoup.parse(html)
        val el = doc.selectFirst("#div-03")
        Assertions.assertEquals("Here is div-03", el!!.text())
        Assertions.assertEquals("div-03", el.id())
        Assertions.assertEquals("div-02", el.closest("#div-02")!!.id())
        Assertions.assertEquals(el, el.closest("div div")) // closest div in a div is itself
        Assertions.assertEquals("div-01", el.closest("article > div")!!.id())
        Assertions.assertEquals("article", el.closest(":not(div)")!!.tagName())
        Assertions.assertNull(el.closest("p"))
    }

    @Test
    fun elementByTagName() {
        val a = Element("P")
        Assertions.assertEquals("P", a.tagName())
    }

    @Test
    fun testChildrenElements() {
        val html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>"
        val doc = Jsoup.parse(html)
        val div = doc.select("div").first()
        val p = doc.select("p").first()
        val span = doc.select("span").first()
        val foo = doc.select("foo").first()
        val img = doc.select("img").first()
        val docChildren = div!!.children()
        Assertions.assertEquals(2, docChildren.size)
        Assertions.assertEquals("<p><a>One</a></p>", docChildren[0].outerHtml())
        Assertions.assertEquals("<p><a>Two</a></p>", docChildren[1].outerHtml())
        Assertions.assertEquals(3, div.childNodes().size)
        Assertions.assertEquals("Three", div.childNodes()[2].outerHtml())
        Assertions.assertEquals(1, p!!.children().size)
        Assertions.assertEquals("One", p.children().text())
        Assertions.assertEquals(0, span!!.children().size)
        Assertions.assertEquals(1, span.childNodes().size)
        Assertions.assertEquals("Four", span.childNodes()[0].outerHtml())
        Assertions.assertEquals(0, foo!!.children().size)
        Assertions.assertEquals(0, foo.childNodes().size)
        Assertions.assertEquals(0, img!!.children().size)
        Assertions.assertEquals(0, img.childNodes().size)
    }

    @Test
    fun testShadowElementsAreUpdated() {
        val html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>"
        val doc = Jsoup.parse(html)
        val div = doc.select("div").first()
        val els = div!!.children()
        val nodes = div.childNodes()
        Assertions.assertEquals(2, els.size) // the two Ps
        Assertions.assertEquals(3, nodes.size) // the "Three" textnode
        val p3 = Element("p").text("P3")
        val p4 = Element("p").text("P4")
        div.insertChildren(1, p3)
        div.insertChildren(3, p4)
        val els2 = div.children()

        // first els should not have changed
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals(4, els2.size)
        Assertions.assertEquals(
            """
    <p><a>One</a></p>
    <p>P3</p>
    <p><a>Two</a></p>
    <p>P4</p>Three
    """.trimIndent(), div.html()
        )
        Assertions.assertEquals("P3", els2[1].text())
        Assertions.assertEquals("P4", els2[3].text())
        p3.after("<span>Another</span")
        val els3 = div.children()
        Assertions.assertEquals(5, els3.size)
        Assertions.assertEquals("span", els3[2].tagName())
        Assertions.assertEquals("Another", els3[2].text())
        Assertions.assertEquals(
            """
    <p><a>One</a></p>
    <p>P3</p><span>Another</span>
    <p><a>Two</a></p>
    <p>P4</p>Three
    """.trimIndent(), div.html()
        )
    }

    @Test
    fun classNamesAndAttributeNameIsCaseInsensitive() {
        val html = "<p Class='SomeText AnotherText'>One</p>"
        val doc = Jsoup.parse(html)
        val p = doc.select("p").first()
        Assertions.assertEquals("SomeText AnotherText", p!!.className())
        Assertions.assertTrue(p.classNames().contains("SomeText"))
        Assertions.assertTrue(p.classNames().contains("AnotherText"))
        Assertions.assertTrue(p.hasClass("SomeText"))
        Assertions.assertTrue(p.hasClass("sometext"))
        Assertions.assertTrue(p.hasClass("AnotherText"))
        Assertions.assertTrue(p.hasClass("anothertext"))
        val p1 = doc.select(".SomeText").first()
        val p2 = doc.select(".sometext").first()
        val p3 = doc.select("[class=SomeText AnotherText]").first()
        val p4 = doc.select("[Class=SomeText AnotherText]").first()
        val p5 = doc.select("[class=sometext anothertext]").first()
        val p6 = doc.select("[class=SomeText AnotherText]").first()
        val p7 = doc.select("[class^=sometext]").first()
        val p8 = doc.select("[class$=nothertext]").first()
        val p9 = doc.select("[class^=sometext]").first()
        val p10 = doc.select("[class$=AnotherText]").first()
        Assertions.assertEquals("One", p1!!.text())
        Assertions.assertEquals(p1, p2)
        Assertions.assertEquals(p1, p3)
        Assertions.assertEquals(p1, p4)
        Assertions.assertEquals(p1, p5)
        Assertions.assertEquals(p1, p6)
        Assertions.assertEquals(p1, p7)
        Assertions.assertEquals(p1, p8)
        Assertions.assertEquals(p1, p9)
        Assertions.assertEquals(p1, p10)
    }

    @Test
    fun testAppendTo() {
        val parentHtml = "<div class='a'></div>"
        val childHtml = "<div class='b'></div><p>Two</p>"
        val parentDoc = Jsoup.parse(parentHtml)
        val parent = parentDoc.body()
        val childDoc = Jsoup.parse(childHtml)
        val div = childDoc.select("div").first()
        val p = childDoc.select("p").first()
        val appendTo1 = div!!.appendTo(parent)
        Assertions.assertEquals(div, appendTo1)
        val appendTo2 = p!!.appendTo(div)
        Assertions.assertEquals(p, appendTo2)
        Assertions.assertEquals(
            "<div class=\"a\"></div>\n<div class=\"b\">\n <p>Two</p>\n</div>",
            parentDoc.body().html()
        )
        Assertions.assertEquals("", childDoc.body().html()) // got moved out
    }

    @Test
    fun testNormalizesNbspInText() {
        val escaped = "You can't always get what you&nbsp;want."
        val withNbsp = "You can't always get what you want." // there is an nbsp char in there
        val doc = Jsoup.parse("<p>$escaped")
        val p = doc.select("p").first()
        Assertions.assertEquals("You can't always get what you want.", p!!.text()) // text is normalized
        Assertions.assertEquals("<p>$escaped</p>", p.outerHtml()) // html / whole text keeps &nbsp;
        Assertions.assertEquals(withNbsp, p.textNodes()[0].wholeText)
        Assertions.assertEquals(160, withNbsp[29].code)
        val matched = doc.select("p:contains(get what you want)").first()
        Assertions.assertEquals("p", matched!!.nodeName())
        Assertions.assertTrue(matched.`is`(":containsOwn(get what you want)"))
    }

    @Test
    fun testNormalizesInvisiblesInText() {
        val escaped = "This&shy;is&#x200b;one&shy;long&shy;word"
        val decoded =
            "This\u00ADis\u200Bone\u00ADlong\u00ADword" // browser would not display those soft hyphens / other chars, so we don't want them in the text
        val doc = Jsoup.parse("<p>$escaped")
        val p = doc.select("p").first()
        doc.outputSettings().charset("ascii") // so that the outer html is easier to see with escaped invisibles
        Assertions.assertEquals("Thisisonelongword", p!!.text()) // text is normalized
        Assertions.assertEquals("<p>$escaped</p>", p.outerHtml()) // html / whole text keeps &shy etc;
        Assertions.assertEquals(decoded, p.textNodes()[0].wholeText)
        val matched = doc.select("p:contains(Thisisonelongword)").first() // really just oneloneword, no invisibles
        Assertions.assertEquals("p", matched!!.nodeName())
        Assertions.assertTrue(matched.`is`(":containsOwn(Thisisonelongword)"))
    }

    @Test
    fun testRemoveBeforeIndex() {
        val doc = Jsoup.parse(
            "<html><body><div><p>before1</p><p>before2</p><p>XXX</p><p>after1</p><p>after2</p></div></body></html>",
            ""
        )
        val body = doc.select("body").first()
        val elems = body!!.select("p:matchesOwn(XXX)")
        val xElem = elems.first()
        val beforeX = xElem!!.parent()!!.getElementsByIndexLessThan(xElem.elementSiblingIndex())
        for (p in beforeX) {
            p.remove()
        }
        Assertions.assertEquals(
            "<body><div><p>XXX</p><p>after1</p><p>after2</p></div></body>", TextUtil.stripNewlines(
                body.outerHtml()
            )
        )
    }

    @Test
    fun testRemoveAfterIndex() {
        val doc2 = Jsoup.parse(
            "<html><body><div><p>before1</p><p>before2</p><p>XXX</p><p>after1</p><p>after2</p></div></body></html>",
            ""
        )
        val body = doc2.select("body").first()
        val elems = body!!.select("p:matchesOwn(XXX)")
        val xElem = elems.first()
        val afterX = xElem!!.parent()!!.getElementsByIndexGreaterThan(xElem.elementSiblingIndex())
        for (p in afterX) {
            p.remove()
        }
        Assertions.assertEquals(
            "<body><div><p>before1</p><p>before2</p><p>XXX</p></div></body>", TextUtil.stripNewlines(
                body.outerHtml()
            )
        )
    }

    @Test
    fun whiteSpaceClassElement() {
        val tag = Tag.valueOf("a")
        val attribs = Attributes()
        val el = Element(tag, "", attribs)
        attribs.put("class", "abc ")
        val hasClass = el.hasClass("ab")
        Assertions.assertFalse(hasClass)
    }

    @Test
    fun testNextElementSiblingAfterClone() {
        // via https://github.com/jhy/jsoup/issues/951
        val html = "<!DOCTYPE html><html lang=\"en\"><head></head><body><div>Initial element</div></body></html>"
        val expectedText = "New element"
        val cloneExpect = "New element in clone"
        val original = Jsoup.parse(html)
        val clone = original.clone()
        val originalElement = original.body().child(0)
        originalElement.after("<div>$expectedText</div>")
        val originalNextElementSibling = originalElement.nextElementSibling()
        val originalNextSibling = originalElement.nextSibling() as Element?
        Assertions.assertEquals(expectedText, originalNextElementSibling!!.text())
        Assertions.assertEquals(expectedText, originalNextSibling!!.text())
        val cloneElement = clone.body().child(0)
        cloneElement.after("<div>$cloneExpect</div>")
        val cloneNextElementSibling = cloneElement.nextElementSibling()
        val cloneNextSibling = cloneElement.nextSibling() as Element?
        Assertions.assertEquals(cloneExpect, cloneNextElementSibling!!.text())
        Assertions.assertEquals(cloneExpect, cloneNextSibling!!.text())
    }

    @Test
    fun testRemovingEmptyClassAttributeWhenLastClassRemoved() {
        // https://github.com/jhy/jsoup/issues/947
        val doc = Jsoup.parse("<img class=\"one two\" />")
        val img = doc.select("img").first()
        img!!.removeClass("one")
        img.removeClass("two")
        Assertions.assertFalse(doc.body().html().contains("class=\"\""))
    }

    @Test
    fun booleanAttributeOutput() {
        val doc = Jsoup.parse("<img src=foo noshade='' nohref async=async autofocus=false>")
        val img = doc.selectFirst("img")
        Assertions.assertEquals("<img src=\"foo\" noshade nohref async autofocus=\"false\">", img!!.outerHtml())
    }

    @Test
    fun textHasSpaceAfterBlockTags() {
        val doc = Jsoup.parse("<div>One</div>Two")
        Assertions.assertEquals("One Two", doc.text())
    }

    @Test
    fun textHasSpaceBetweenDivAndCenterTags() {
        val doc = Jsoup.parse("<div>One</div><div>Two</div><center>Three</center><center>Four</center>")
        Assertions.assertEquals("One Two Three Four", doc.text())
    }

    @Test
    fun testNextElementSiblings() {
        val doc = Jsoup.parse(
            "<ul id='ul'>" +
                    "<li id='a'>a</li>" +
                    "<li id='b'>b</li>" +
                    "<li id='c'>c</li>" +
                    "</ul> Not An Element but a node" +
                    "<div id='div'>" +
                    "<li id='d'>d</li>" +
                    "</div>"
        )
        val element = doc.getElementById("a")
        val elementSiblings = element!!.nextElementSiblings()
        Assertions.assertNotNull(elementSiblings)
        Assertions.assertEquals(2, elementSiblings.size)
        Assertions.assertEquals("b", elementSiblings[0].id())
        Assertions.assertEquals("c", elementSiblings[1].id())
        val element1 = doc.getElementById("b")
        val elementSiblings1: List<Element> = element1!!.nextElementSiblings()
        Assertions.assertNotNull(elementSiblings1)
        Assertions.assertEquals(1, elementSiblings1.size)
        Assertions.assertEquals("c", elementSiblings1[0].id())
        val element2 = doc.getElementById("c")
        val elementSiblings2: List<Element> = element2!!.nextElementSiblings()
        Assertions.assertEquals(0, elementSiblings2.size)
        val ul = doc.getElementById("ul")
        val elementSiblings3: List<Element> = ul!!.nextElementSiblings()
        Assertions.assertNotNull(elementSiblings3)
        Assertions.assertEquals(1, elementSiblings3.size)
        Assertions.assertEquals("div", elementSiblings3[0].id())
        val div = doc.getElementById("div")
        val elementSiblings4: List<Element> = div!!.nextElementSiblings()
        Assertions.assertEquals(0, elementSiblings4.size)
    }

    @Test
    fun testPreviousElementSiblings() {
        val doc = Jsoup.parse(
            "<ul id='ul'>" +
                    "<li id='a'>a</li>" +
                    "<li id='b'>b</li>" +
                    "<li id='c'>c</li>" +
                    "</ul>" +
                    "<div id='div'>" +
                    "<li id='d'>d</li>" +
                    "</div>"
        )
        val element = doc.getElementById("b")
        val elementSiblings = element!!.previousElementSiblings()
        Assertions.assertNotNull(elementSiblings)
        Assertions.assertEquals(1, elementSiblings.size)
        Assertions.assertEquals("a", elementSiblings[0].id())
        val element1 = doc.getElementById("a")
        val elementSiblings1: List<Element> = element1!!.previousElementSiblings()
        Assertions.assertEquals(0, elementSiblings1.size)
        val element2 = doc.getElementById("c")
        val elementSiblings2: List<Element> = element2!!.previousElementSiblings()
        Assertions.assertNotNull(elementSiblings2)
        Assertions.assertEquals(2, elementSiblings2.size)
        Assertions.assertEquals("b", elementSiblings2[0].id())
        Assertions.assertEquals("a", elementSiblings2[1].id())
        val ul = doc.getElementById("ul")
        val elementSiblings3: List<Element> = ul!!.previousElementSiblings()
        Assertions.assertEquals(0, elementSiblings3.size)
    }

    @Test
    fun testClearAttributes() {
        val el = Element("a").attr("href", "http://example.com").text("Hello")
        Assertions.assertEquals("<a href=\"http://example.com\">Hello</a>", el.outerHtml())
        val el2 = el.clearAttributes() // really just force testing the return type is Element
        Assertions.assertSame(el, el2)
        Assertions.assertEquals("<a>Hello</a>", el2.outerHtml())
    }

    @Test
    fun testRemoveAttr() {
        val el = Element("a")
            .attr("href", "http://example.com")
            .attr("id", "1")
            .text("Hello")
        Assertions.assertEquals("<a href=\"http://example.com\" id=\"1\">Hello</a>", el.outerHtml())
        val el2 = el.removeAttr("href") // really just force testing the return type is Element
        Assertions.assertSame(el, el2)
        Assertions.assertEquals("<a id=\"1\">Hello</a>", el2.outerHtml())
    }

    @Test
    fun testRoot() {
        val el = Element("a")
        el.append("<span>Hello</span>")
        Assertions.assertEquals("<a><span>Hello</span></a>", el.outerHtml())
        val span = el.selectFirst("span")
        Assertions.assertNotNull(span)
        val el2 = span!!.root()
        Assertions.assertSame(el, el2)
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three")
        val div = doc.selectFirst("div")
        Assertions.assertSame(doc, div!!.root())
        Assertions.assertSame(doc, div.ownerDocument())
    }

    @Test
    fun testTraverse() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three")
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val counter = AtomicInteger(0)
        val div2 = div!!.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                counter.incrementAndGet()
            }

            override fun tail(node: Node, depth: Int) {}
        })
        Assertions.assertEquals(7, counter.get())
        Assertions.assertEquals(div2, div)
    }

    @Test
    fun testTraverseLambda() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three")
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val counter = AtomicInteger(0)
        val div2 = div!!.traverse(GenericNodeVisitor.jvmNodeVisitor { node: Node? -> counter.incrementAndGet() })
        Assertions.assertEquals(7, counter.get())
        Assertions.assertEquals(div2, div)
    }

    @Test
    fun testFilterCallReturnsElement() {
        // doesn't actually test the filter so much as the return type for Element. See node.nodeFilter for an actual test
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three")
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val div2 = div!!.filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                return NodeFilter.FilterResult.CONTINUE
            }
        })
        Assertions.assertSame(div, div2)
    }

    @Test
    fun testFilterAsLambda() {
        val doc = Jsoup.parse("<div><p>One<p id=2>Two<p>Three")
        doc.filter(GenericNodeFilter.jvmNodeFilter { node: Node -> if (node.attr("id") == "2") NodeFilter.FilterResult.REMOVE else NodeFilter.FilterResult.CONTINUE })
        Assertions.assertEquals("<div><p>One</p><p>Three</p></div>", TextUtil.stripNewlines(doc.body().html()))
    }

    @Test
    fun testForEach() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div><div id=1>Gone<p></div>")
        doc.forEach { el: Element ->
            if (el.id() == "1") el.remove() else if (el.text() == "There") {
                el.text("There Now")
                el.append("<p>Another</p>")
            }
        }
        Assertions.assertEquals(
            "<div><p>Hello</p></div><div>There Now<p>Another</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun doesntDeleteZWJWhenNormalizingText() {
        val text = "\uD83D\uDC69\u200D\uD83D\uDCBB\uD83E\uDD26\uD83C\uDFFB\u200D\u2642\uFE0F"
        val doc = Jsoup.parse("<p>$text</p><div>One&zwj;Two</div>")
        val p = doc.selectFirst("p")
        val d = doc.selectFirst("div")
        Assertions.assertEquals(12, p!!.text().length)
        Assertions.assertEquals(text, p.text())
        Assertions.assertEquals(7, d!!.text().length)
        Assertions.assertEquals("One\u200DTwo", d.text())
        val found = doc.selectFirst("div:contains(One\u200DTwo)")
        Assertions.assertTrue(found!!.hasSameValue(d))
    }

    @Test
    fun testReparentSeperateNodes() {
        val html = "<div><p>One<p>Two"
        val doc = Jsoup.parse(html)
        val new1 = Element("p").text("Three")
        val new2 = Element("p").text("Four")
        doc.body().insertChildren(-1, new1, new2)
        Assertions.assertEquals(
            "<div><p>One</p><p>Two</p></div><p>Three</p><p>Four</p>",
            TextUtil.stripNewlines(doc.body().html())
        )

        // note that these get moved from the above - as not copied
        doc.body().insertChildren(0, new1, new2)
        Assertions.assertEquals(
            "<p>Three</p><p>Four</p><div><p>One</p><p>Two</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
        doc.body().insertChildren(0, new2.clone(), new1.clone())
        Assertions.assertEquals(
            "<p>Four</p><p>Three</p><p>Three</p><p>Four</p><div><p>One</p><p>Two</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )

        // shifted to end
        doc.body().appendChild(new1)
        Assertions.assertEquals(
            "<p>Four</p><p>Three</p><p>Four</p><div><p>One</p><p>Two</p></div><p>Three</p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testNotActuallyAReparent() {
        // prep
        val html = "<div>"
        val doc = Jsoup.parse(html)
        val div = doc.selectFirst("div")
        val new1 = Element("p").text("One")
        val new2 = Element("p").text("Two")
        div!!.addChildren(new1, new2)
        Assertions.assertEquals(
            "<div><p>One</p><p>Two</p></div>", TextUtil.stripNewlines(
                div.outerHtml()
            )
        )

        // and the issue setup:
        val new3 = Element("p").text("Three")
        val wrap = Element("nav")
        wrap.addChildren(0, new1, new3)
        Assertions.assertEquals("<nav><p>One</p><p>Three</p></nav>", TextUtil.stripNewlines(wrap.outerHtml()))
        div.addChildren(wrap)
        // now should be that One moved into wrap, leaving Two in div.
        Assertions.assertEquals(
            "<div><p>Two</p><nav><p>One</p><p>Three</p></nav></div>", TextUtil.stripNewlines(
                div.outerHtml()
            )
        )
        Assertions.assertEquals(
            "<div><p>Two</p><nav><p>One</p><p>Three</p></nav></div>", TextUtil.stripNewlines(
                div.outerHtml()
            )
        )
    }

    @Test
    fun testChildSizeWithMixedContent() {
        val doc = Jsoup.parse("<table><tbody>\n<tr>\n<td>15:00</td>\n<td>sport</td>\n</tr>\n</tbody></table>")
        val row = doc.selectFirst("table tbody tr")
        Assertions.assertEquals(2, row!!.childrenSize())
        Assertions.assertEquals(5, row.childNodeSize())
    }

    @Test
    fun isBlock() {
        val html = "<div><p><span>Hello</span>"
        val doc = Jsoup.parse(html)
        Assertions.assertTrue(doc.selectFirst("div")!!.isBlock)
        Assertions.assertTrue(doc.selectFirst("p")!!.isBlock)
        Assertions.assertFalse(doc.selectFirst("span")!!.isBlock)
    }

    @Test
    fun testScriptTextHtmlSetAsData() {
        var src = "var foo = 5 < 2;\nvar bar = 1 && 2;"
        val html = "<script>$src</script>"
        val doc = Jsoup.parse(html)
        val el = doc.selectFirst("script")
        Assertions.assertNotNull(el)
        validateScriptContents(src, el)
        src = "var foo = 4 < 2;\nvar bar > 1 && 2;"
        el!!.html(src)
        validateScriptContents(src, el)

        // special case for .text (in HTML; in XML will just be regular text)
        el.text(src)
        validateScriptContents(src, el)

        // XML, no special treatment, get escaped correctly
        val xml = Parser.xmlParser().parseInput(html, "")
        val xEl = xml.selectFirst("script")
        Assertions.assertNotNull(xEl)
        src = "var foo = 5 < 2;\nvar bar = 1 && 2;"
        val escaped = "var foo = 5 &lt; 2;\nvar bar = 1 &amp;&amp; 2;"
        validateXmlScriptContents(xEl)
        xEl!!.text(src)
        validateXmlScriptContents(xEl)
        xEl.html(src)
        validateXmlScriptContents(xEl)
        Assertions.assertEquals("<script>var foo = 4 < 2;\nvar bar > 1 && 2;</script>", el.outerHtml())
        Assertions.assertEquals("<script>$escaped</script>", xEl.outerHtml()) // escaped in xml as no special treatment
    }

    @Test
    fun testShallowCloneToString() {
        // https://github.com/jhy/jsoup/issues/1410
        val doc = Jsoup.parse("<p><i>Hello</i></p>")
        val p = doc.selectFirst("p")
        val i = doc.selectFirst("i")
        val pH = p!!.shallowClone().toString()
        val iH = i!!.shallowClone().toString()
        Assertions.assertEquals("<p></p>", pH) // shallow, so no I
        Assertions.assertEquals("<i></i>", iH)
        Assertions.assertEquals(p.outerHtml(), p.toString())
        Assertions.assertEquals(i.outerHtml(), i.toString())
    }

    @Test
    fun styleHtmlRoundTrips() {
        val styleContents = "foo < bar > qux {color:white;}"
        val html = "<head><style>$styleContents</style></head>"
        val doc = Jsoup.parse(html)
        val head = doc.head()
        val style = head.selectFirst("style")
        Assertions.assertNotNull(style)
        Assertions.assertEquals(styleContents, style!!.html())
        style.html(styleContents)
        Assertions.assertEquals(styleContents, style.html())
        Assertions.assertEquals("", style.text())
        style.text(styleContents) // pushes the HTML, not the Text
        Assertions.assertEquals("", style.text())
        Assertions.assertEquals(styleContents, style.html())
    }

    @Test
    fun moveChildren() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div></div>")
        val divs = doc.select("div")
        val a = divs[0]
        val b = divs[1]
        b.insertChildren(-1, a.childNodes())
        Assertions.assertEquals(
            "<div></div>\n<div>\n <p>One</p>\n <p>Two</p>\n <p>Three</p>\n</div>",
            doc.body().html()
        )
    }

    @Test
    fun moveChildrenToOuter() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div></div>")
        val divs = doc.select("div")
        val a = divs[0]
        val b = doc.body()
        b.insertChildren(-1, a.childNodes())
        Assertions.assertEquals(
            "<div></div>\n<div></div>\n<p>One</p>\n<p>Two</p>\n<p>Three</p>",
            doc.body().html()
        )
    }

    @Test
    fun appendChildren() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div><p>Four</div>")
        val divs = doc.select("div")
        val a = divs[0]
        val b = divs[1]
        b.appendChildren(a.childNodes())
        Assertions.assertEquals(
            "<div></div>\n<div>\n <p>Four</p>\n <p>One</p>\n <p>Two</p>\n <p>Three</p>\n</div>",
            doc.body().html()
        )
    }

    @Test
    fun prependChildren() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div><p>Four</div>")
        val divs = doc.select("div")
        val a = divs[0]
        val b = divs[1]
        b.prependChildren(a.childNodes())
        Assertions.assertEquals(
            "<div></div>\n<div>\n <p>One</p>\n <p>Two</p>\n <p>Three</p>\n <p>Four</p>\n</div>",
            doc.body().html()
        )
    }

    @Test
    fun loopMoveChildren() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div><div><p>Four</div>")
        val divs = doc.select("div")
        val a = divs[0]
        val b = divs[1]
        val outer = b.parent()
        Assertions.assertNotNull(outer)
        for (node in a.childNodes()) {
            outer!!.appendChild(node)
        }
        Assertions.assertEquals(
            "<div></div>\n<div>\n <p>Four</p>\n</div>\n<p>One</p>\n<p>Two</p>\n<p>Three</p>",
            doc.body().html()
        )
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun accessorsDoNotVivifyAttributes() {
        // internally, we don't want to create empty Attribute objects unless actually used for something
        val doc = Jsoup.parse("<div><p><a href=foo>One</a>")
        val div = doc.selectFirst("div")
        val p = doc.selectFirst("p")
        val a = doc.selectFirst("a")

        // should not create attributes
        Assertions.assertEquals("", div!!.attr("href"))
        p!!.removeAttr("href")
        val hrefs = doc.select("[href]")
        Assertions.assertEquals(1, hrefs.size)
        Assertions.assertFalse(div.hasAttributes())
        Assertions.assertFalse(p.hasAttributes())
        Assertions.assertTrue(a!!.hasAttributes())
    }

    @Test
    fun childNodesAccessorDoesNotVivify() {
        val doc = Jsoup.parse("<p></p>")
        val p = doc.selectFirst("p")
        Assertions.assertFalse(p!!.hasChildNodes())
        Assertions.assertEquals(0, p.childNodeSize())
        Assertions.assertEquals(0, p.childrenSize())
        val childNodes = p.childNodes()
        Assertions.assertEquals(0, childNodes.size)
        val children = p.children()
        Assertions.assertEquals(0, children.size)
        Assertions.assertFalse(p.hasChildNodes())
    }

    @Test
    fun emptyChildrenElementsIsModifiable() {
        // using unmodifiable empty in childElementList as short circuit, but people may be modifying Elements.
        val p = Element("p")
        val els = p.children()
        Assertions.assertEquals(0, els.size)
        els.add(Element("a"))
        Assertions.assertEquals(1, els.size)
    }

    @Test
    fun attributeSizeDoesNotAutoVivify() {
        val doc = Jsoup.parse("<p></p>")
        val p = doc.selectFirst("p")
        Assertions.assertNotNull(p)
        Assertions.assertFalse(p!!.hasAttributes())
        Assertions.assertEquals(0, p.attributesSize())
        Assertions.assertFalse(p.hasAttributes())
        p.attr("foo", "bar")
        Assertions.assertEquals(1, p.attributesSize())
        Assertions.assertTrue(p.hasAttributes())
        p.removeAttr("foo")
        Assertions.assertEquals(0, p.attributesSize())
    }

    @Test
    fun clonedElementsHaveOwnerDocsAndIndependentSettings() {
        // https://github.com/jhy/jsoup/issues/763
        val doc = Jsoup.parse("<div>Text</div><div>Two</div>")
        doc.outputSettings().prettyPrint(false)
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val text = div!!.childNode(0)
        Assertions.assertNotNull(text)
        val divClone = div.clone()
        val docClone = divClone.ownerDocument()
        Assertions.assertNotNull(docClone)
        Assertions.assertFalse(docClone!!.outputSettings().prettyPrint())
        Assertions.assertNotSame(doc, docClone)
        Assertions.assertSame(docClone, divClone.childNode(0).ownerDocument())
        // the cloned text has same owner doc as the cloned div
        doc.outputSettings().prettyPrint(true)
        Assertions.assertTrue(doc.outputSettings().prettyPrint())
        Assertions.assertFalse(docClone.outputSettings().prettyPrint())
        Assertions.assertEquals(1, docClone.children().size) // check did not get the second div as the owner's children
        Assertions.assertEquals(divClone, docClone.child(0)) // note not the head or the body -- not normalized
    }

    @ParameterizedTest
    @MethodSource("testOutputSettings")
    fun prettySerializationRoundTrips(settings: Document.OutputSettings?) {
        // https://github.com/jhy/jsoup/issues/1688
        // tests that repeated html() and Jsoup.parse() does not accumulate errant spaces / newlines
        val doc = Jsoup.parse("<div>\nFoo\n<p>\nBar\nqux</p></div>\n<script>\n alert('Hello!');\n</script>")
        doc.outputSettings(settings!!)
        val html = doc.html()
        val doc2 = Jsoup.parse(html)
        doc2.outputSettings(settings)
        val html2 = doc2.html()
        Assertions.assertEquals(html, html2)
    }

    @Test
    fun prettyPrintScriptsDoesNotGrowOnRepeat() {
        val doc = Jsoup.parse("<div>\nFoo\n<p>\nBar\nqux</p></div>\n<script>\n alert('Hello!');\n</script>")
        val settings = doc.outputSettings()
        settings
            .prettyPrint(true)
            .outline(true)
            .indentAmount(4)
        val html = doc.html()
        val doc2 = Jsoup.parse(html)
        doc2.outputSettings(settings)
        val html2 = doc2.html()
        Assertions.assertEquals(html, html2)
    }

    @Test
    fun elementBrText() {
        // testcase for https://github.com/jhy/jsoup/issues/1437
        val html = "<p>Hello<br>World</p>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(false) // otherwise html serializes as Hello<br>\n World.
        val p = doc.select("p").first()
        Assertions.assertNotNull(p)
        Assertions.assertEquals(html, p!!.outerHtml())
        Assertions.assertEquals("Hello World", p.text())
        Assertions.assertEquals("Hello\nWorld", p.wholeText())
    }

    @Test
    fun wrapTextAfterBr() {
        // https://github.com/jhy/jsoup/issues/1858
        val html = "<p>Hello<br>there<br>now.</p>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<p>Hello<br>\n there<br>\n now.</p>", doc.body().html())
    }

    @Test
    fun prettyprintBrInBlock() {
        val html = "<div><br> </div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<div>\n <br>\n</div>", doc.body().html()) // not div\n br\n \n/div
    }

    @Test
    fun prettyprintBrWhenNotFirstChild() {
        // https://github.com/jhy/jsoup/issues/1911
        val h = "<div><p><br>Foo</p><br></div>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            """<div>
 <p><br>
  Foo</p>
 <br>
</div>""", doc.body().html()
        )
        // br gets wrapped if in div, but not in p (block vs inline), but always wraps after
    }

    @Test
    fun preformatFlowsToChildTextNodes() {
        // https://github.com/jhy/jsoup/issues/1776
        val html =
            "<div><pre>One\n<span>\nTwo</span>\n <span>  \nThree</span>\n <span>Four <span>Five</span>\n  Six\n</pre>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().indentAmount(2).prettyPrint(true)
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val actual = div!!.outerHtml()
        val expect = "<div>\n" +
                "  <pre>One\n" +
                "<span>\n" +
                "Two</span>\n" +
                " <span>  \n" +
                "Three</span>\n" +
                " <span>Four <span>Five</span>\n" +
                "  Six\n" +
                "</span></pre>\n" +
                "</div>"
        Assertions.assertEquals(expect, actual)
        val expectText = "One\n" +
                "\n" +
                "Two\n" +
                "   \n" +
                "Three\n" +
                " Four Five\n" +
                "  Six\n"
        Assertions.assertEquals(expectText, div.wholeText())
        val expectOwn = "One\n" +
                "\n" +
                " \n" +
                " "
        Assertions.assertEquals(expectOwn, div.child(0).wholeOwnText())
    }

    @Test
    fun inlineInBlockShouldIndent() {
        // was inconsistent between <div>\n<span> and <div><span> - former would print inline, latter would wrap(!)
        val html =
            "<div>One <span>Hello</span><span>!</span></div><div>\n<span>There</span></div><div> <span>Now</span></div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            """<div>
 One <span>Hello</span><span>!</span>
</div>
<div>
 <span>There</span>
</div>
<div>
 <span>Now</span>
</div>""",
            doc.body().html()
        )
    }

    @Test
    fun testExpectFirst() {
        val doc = Jsoup.parse("<p>One</p><p>Two <span>Three</span> <span>Four</span>")
        val span = doc.expectFirst("span")
        Assertions.assertEquals("Three", span.text())
        Assertions.assertNull(doc.selectFirst("div"))
        var threw = false
        try {
            val div = doc.expectFirst("div")
        } catch (e: IllegalArgumentException) {
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun testExpectFirstMessage() {
        val doc = Jsoup.parse("<p>One</p><p>Two <span>Three</span> <span>Four</span>")
        var threw = false
        val p = doc.expectFirst("P")
        try {
            val span = p.expectFirst("span.doesNotExist")
        } catch (e: ValidationException) {
            threw = true
            Assertions.assertEquals("No elements matched the query 'span.doesNotExist' on element 'p'.", e.message)
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun testExpectFirstMessageDoc() {
        val doc = Jsoup.parse("<p>One</p><p>Two <span>Three</span> <span>Four</span>")
        var threw = false
        val p = doc.expectFirst("P")
        try {
            val span = doc.expectFirst("span.doesNotExist")
        } catch (e: ValidationException) {
            threw = true
            Assertions.assertEquals("No elements matched the query 'span.doesNotExist' in the document.", e.message)
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun spanRunsMaintainSpace() {
        // https://github.com/jhy/jsoup/issues/1787
        val doc = Jsoup.parse("<p><span>One</span>\n<span>Two</span>\n<span>Three</span></p>")
        val text = "One Two Three"
        val body = doc.body()
        Assertions.assertEquals(text, body.text())
        val p = doc.expectFirst("p")
        val html = p.html()
        p.html(html)
        Assertions.assertEquals(text, body.text())
        Assertions.assertEquals("<p><span>One</span> <span>Two</span> <span>Three</span></p>", body.html())
    }

    @Test
    fun doctypeIsPrettyPrinted() {
        // resolves underlying issue raised in https://github.com/jhy/jsoup/pull/1664
        val doc1 = Jsoup.parse("<!--\nlicense\n-->\n \n<!doctype html>\n<html>")
        val doc2 = Jsoup.parse("\n  <!doctype html><html>")
        val doc3 = Jsoup.parse("<!doctype html>\n<html>")
        val doc4 = Jsoup.parse("\n<!doctype html>\n<html>")
        val doc5 = Jsoup.parse("\n<!--\n comment \n -->  <!doctype html>\n<html>")
        val doc6 = Jsoup.parse("<!--\n comment \n -->  <!doctype html>\n<html>")
        Assertions.assertEquals(
            "<!--\nlicense\n-->\n<!doctype html>\n<html>\n <head></head>\n <body></body>\n</html>",
            doc1.html()
        )
        doc1.outputSettings().prettyPrint(false)
        Assertions.assertEquals(
            "<!--\nlicense\n--><!doctype html>\n<html><head></head><body></body></html>",
            doc1.html()
        )
        // note that the whitespace between the comment and the doctype is not retained, in Initial state
        Assertions.assertEquals("<!doctype html>\n<html>\n <head></head>\n <body></body>\n</html>", doc2.html())
        Assertions.assertEquals("<!doctype html>\n<html>\n <head></head>\n <body></body>\n</html>", doc3.html())
        Assertions.assertEquals("<!doctype html>\n<html>\n <head></head>\n <body></body>\n</html>", doc4.html())
        Assertions.assertEquals(
            "<!--\n comment \n -->\n<!doctype html>\n<html>\n <head></head>\n <body></body>\n</html>",
            doc5.html()
        )
        Assertions.assertEquals(
            "<!--\n comment \n -->\n<!doctype html>\n<html>\n <head></head>\n <body></body>\n</html>",
            doc6.html()
        )
    }

    @Test
    fun textnodeInBlockIndent() {
        val html = "<div>\n{{ msg }} \n </div>\n<div>\n{{ msg }} \n </div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<div>\n {{ msg }}\n</div>\n<div>\n {{ msg }}\n</div>", doc.body().html())
    }

    @Test
    fun stripTrailing() {
        val html = "<p> This <span>is </span>fine. </p>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<p>This <span>is </span>fine.</p>", doc.body().html())
    }

    @Test
    fun elementIndentAndSpaceTrims() {
        val html = "<body><div> <p> One Two </p> <a>  Hello </a><p>\nSome text \n</p>\n </div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            """<div>
 <p>One Two</p><a> Hello </a>
 <p>Some text</p>
</div>""", doc.body().html()
        )
    }

    @Test
    fun divAInlineable() {
        val html = "<body><div> <a>Text</a>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            """<div>
 <a>Text</a>
</div>""", doc.body().html()
        )
    }

    @Test
    fun noDanglingSpaceAfterCustomElement() {
        // https://github.com/jhy/jsoup/issues/1852
        var html = "<bar><p/>\n</bar>"
        var doc = Jsoup.parse(html)
        Assertions.assertEquals("<bar>\n <p></p>\n</bar>", doc.body().html())
        html = "<foo>\n  <bar />\n</foo>"
        doc = Jsoup.parse(html)
        Assertions.assertEquals("<foo>\n <bar />\n</foo>", doc.body().html())
    }

    @Test
    fun spanInBlockTrims() {
        val html = "<p>Lorem ipsum</p>\n<span>Thanks</span>"
        val doc = Jsoup.parse(html)
        val outHtml = doc.body().html()
        Assertions.assertEquals("<p>Lorem ipsum</p><span>Thanks</span>", outHtml)
    }

    @Test
    fun replaceWithSelf() {
        // https://github.com/jhy/jsoup/issues/1843
        val doc = Jsoup.parse("<p>One<p>Two")
        val ps = doc.select("p")
        val first = ps.first()
        Assertions.assertNotNull(first)
        first!!.replaceWith(first)
        Assertions.assertEquals(ps[1], first.nextSibling())
        Assertions.assertEquals("<p>One</p>\n<p>Two</p>", first.parent()!!.html())
    }

    @Test
    fun select() {
        val eval = QueryParser.parse("div")
        val doc = Jsoup.parse(reference)
        val els = doc.select("div")
        val els2 = doc.select(eval)
        Assertions.assertEquals(els, els2)
    }

    @Test
    fun insertChildrenValidation() {
        val doc = Jsoup.parse(reference)
        val div = doc.expectFirst("div")
        val ex: Throwable = Assertions.assertThrows(
            ValidationException::class.java
        ) { div.insertChildren(20, Element("div")) }
        Assertions.assertEquals("Insert position out of bounds.", ex.message)
    }

    @Test
    fun cssSelectorNoDoc() {
        val el = Element("div")
        el.id("one")
        Assertions.assertEquals("#one", el.cssSelector())
    }

    @Test
    fun cssSelectorNoParent() {
        val el = Element("div")
        Assertions.assertEquals("div", el.cssSelector())
    }

    @Test
    fun orphanSiblings() {
        val el = Element("div")
        Assertions.assertEquals(0, el.siblingElements().size)
        Assertions.assertEquals(0, el.nextElementSiblings().size)
        Assertions.assertEquals(0, el.previousElementSiblings().size)
        Assertions.assertNull(el.nextElementSibling())
        Assertions.assertNull(el.previousElementSibling())
    }

    @Test
    fun elementsByAttributeStarting() {
        val doc = Jsoup.parse("<div data-one=1 data-two=2 id=1><p data-one=3 id=2>Text</div><div>")
        val els = doc.getElementsByAttributeStarting(" data- ")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("1", els[0].id())
        Assertions.assertEquals("2", els[1].id())
        Assertions.assertEquals(0, doc.getElementsByAttributeStarting("not-data").size)
    }

    @Test
    fun elementsByAttributeValueNot() {
        val doc = Jsoup.parse("<div data-one=1 data-two=2 id=1><p data-one=3 id=2>Text</div><div id=3>")
        val els = doc.body().getElementsByAttributeValueNot("data-one", "1")
        Assertions.assertEquals(3, els.size) // the body, p, and last div
        Assertions.assertEquals("body", els[0].normalName())
        Assertions.assertEquals("2", els[1].id())
        Assertions.assertEquals("3", els[2].id())
    }

    @Test
    fun elementsByAttributeValueStarting() {
        val doc = Jsoup.parse("<a href=one1></a><a href=one2></a><a href=else</a>")
        val els = doc.getElementsByAttributeValueStarting("href", "one")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("one1", els[0].attr("href"))
        Assertions.assertEquals("one2", els[1].attr("href"))
    }

    @Test
    fun elementsByAttributeValueEnding() {
        val doc = Jsoup.parse("<a href=1one></a><a href=2one></a><a href=else</a>")
        val els = doc.getElementsByAttributeValueEnding("href", "one")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("1one", els[0].attr("href"))
        Assertions.assertEquals("2one", els[1].attr("href"))
    }

    @Test
    fun elementsByAttributeValueContaining() {
        val doc = Jsoup.parse("<a href=1one></a><a href=2one></a><a href=else</a>")
        val els = doc.getElementsByAttributeValueContaining("href", "on")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("1one", els[0].attr("href"))
        Assertions.assertEquals("2one", els[1].attr("href"))
    }

    @Test
    fun elementsByAttributeValueMatchingPattern() {
        val doc = Jsoup.parse("<a href=1one></a><a href=2one></a><a href=else</a>")
        val els = doc.getElementsByAttributeValueMatching("href", Regex("^\\d\\w+"))
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("1one", els[0].attr("href"))
        Assertions.assertEquals("2one", els[1].attr("href"))
    }

    @Test
    fun elementsByAttributeValueMatching() {
        val doc = Jsoup.parse("<a href=1one></a><a href=2one></a><a href=else</a>")
        val els = doc.getElementsByAttributeValueMatching("href", "^\\d\\w+")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("1one", els[0].attr("href"))
        Assertions.assertEquals("2one", els[1].attr("href"))
    }

    @Test
    fun elementsByAttributeValueMatchingValidation() {
        val doc = Jsoup.parse(reference)
        val ex: Throwable = Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { doc.getElementsByAttributeValueMatching("key", "\\x") }
        Assertions.assertEquals("Pattern syntax error: \\x", ex.message)
    }

    @Test
    fun elementsByIndexEquals() {
        val doc = Jsoup.parse("<a href=1one></a><a href=2one></a><a href=else</a>")
        val els = doc.body().getElementsByIndexEquals(1)
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("body", els[0].normalName())
        Assertions.assertEquals("2one", els[1].attr("href"))
    }

    @Test
    fun elementsContainingText() {
        val doc = Jsoup.parse("<div id=1>One</div><div>Two</div>")
        val els = doc.body().getElementsContainingText("one")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("body", els[0].normalName())
        Assertions.assertEquals("1", els[1].id())
    }

    @Test
    fun elementsContainingOwnText() {
        val doc = Jsoup.parse("<div id=1>One</div><div>Two</div>")
        val els = doc.body().getElementsContainingOwnText("one")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("1", els[0].id())
    }

    @Test
    fun elementsMatchingTextValidation() {
        val doc = Jsoup.parse(reference)
        val ex: Throwable = Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { doc.getElementsMatchingText("\\x") }
        Assertions.assertEquals("Pattern syntax error: \\x", ex.message)
    }

    @Test
    fun elementsMatchingText() {
        val doc = Jsoup.parse("<div id=1>One</div><div>Two</div>")
        val els = doc.body().getElementsMatchingText("O\\w+")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("body", els[0].normalName())
        Assertions.assertEquals("1", els[1].id())
    }

    @Test
    fun elementsMatchingOwnText() {
        val doc = Jsoup.parse("<div id=1>One</div><div>Two</div>")
        val els = doc.body().getElementsMatchingOwnText("O\\w+")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("1", els[0].id())
    }

    @Test
    fun elementsMatchingOwnTextValidation() {
        val doc = Jsoup.parse(reference)
        val ex: Throwable = Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { doc.getElementsMatchingOwnText("\\x") }
        Assertions.assertEquals("Pattern syntax error: \\x", ex.message)
    }

    @Test
    fun hasText() {
        val doc =
            Jsoup.parse("<div id=1><p><i>One</i></p></div><div id=2>Two</div><div id=3><script>data</script> </div>")
        Assertions.assertTrue(doc.getElementById("1")!!.hasText())
        Assertions.assertTrue(doc.getElementById("2")!!.hasText())
        Assertions.assertFalse(doc.getElementById("3")!!.hasText())
    }

    @Test
    fun dataInCdataNode() {
        val el = Element("div")
        val cdata = CDataNode("Some CData")
        el.appendChild(cdata)
        Assertions.assertEquals("Some CData", el.data())
        val parse = Jsoup.parse("One <![CDATA[Hello]]>")
        Assertions.assertEquals("Hello", parse.data())
    }

    @Test
    fun outerHtmlAppendable() {
        // tests not string builder flow
        val doc = Jsoup.parse("<div>One</div>")
        val buffer = StringBuffer()
        doc.body().outerHtml(buffer)
        Assertions.assertEquals("\n<body>\n <div>\n  One\n </div>\n</body>", buffer.toString())
        val builder = StringBuilder()
        doc.body().outerHtml(builder)
        Assertions.assertEquals("<body>\n <div>\n  One\n </div>\n</body>", builder.toString())
    }

    @Test
    fun rubyInline() {
        val html = "<ruby>T<rp>(</rp><rtc>!</rtc><rt>)</rt></ruby>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(html, doc.body().html())
    }

    @Test
    fun nestedFormatAsInlinePrintsAsBlock() {
        // https://github.com/jhy/jsoup/issues/1926
        val h = """        <table>
            <tr>
                <td>
                    <p style="display:inline;">A</p>
                    <p style="display:inline;">B</p>
                </td>
            </tr>
        </table>"""
        val doc = Jsoup.parse(h)
        val out = doc.body().html()
        Assertions.assertEquals(
            """<table>
 <tbody>
  <tr>
   <td>
    <p style="display:inline;">A</p>
    <p style="display:inline;">B</p></td>
  </tr>
 </tbody>
</table>""", out
        )
        // todo - I would prefer the </td> to wrap down there - but need to reimplement pretty printer to simplify and track indented state
    }

    companion object {
        private fun validateScriptContents(src: String, el: Element?) {
            Assertions.assertEquals("", el!!.text()) // it's not text
            Assertions.assertEquals("", el.ownText())
            Assertions.assertEquals("", el.wholeText())
            Assertions.assertEquals(src, el.html())
            Assertions.assertEquals(src, el.data())
        }

        private fun validateXmlScriptContents(el: Element?) {
            Assertions.assertEquals("var foo = 5 < 2; var bar = 1 && 2;", el!!.text())
            Assertions.assertEquals("var foo = 5 < 2; var bar = 1 && 2;", el.ownText())
            Assertions.assertEquals("var foo = 5 < 2;\nvar bar = 1 && 2;", el.wholeText())
            Assertions.assertEquals("var foo = 5 &lt; 2;\nvar bar = 1 &amp;&amp; 2;", el.html())
            Assertions.assertEquals("", el.data())
        }

        @JvmStatic
        private fun testOutputSettings(): Stream<Document.OutputSettings> {
            return Stream.of(
                Document.OutputSettings().prettyPrint(true).indentAmount(4),
                Document.OutputSettings().prettyPrint(true).indentAmount(1),
                Document.OutputSettings().prettyPrint(true).indentAmount(4).outline(true),
                Document.OutputSettings().prettyPrint(false)
            )
        }
    }
}
