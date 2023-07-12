package org.jsoup.parser

import org.jsoup.Jsoup
import org.jsoup.Jsoup.clean
import org.jsoup.Jsoup.isValid
import org.jsoup.Jsoup.parse
import org.jsoup.Jsoup.parseBodyFragment
import org.jsoup.TextUtil
import org.jsoup.integration.ParseTest
import org.jsoup.internal.StringUtil
import org.jsoup.nodes.*
import org.jsoup.parser.*
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.Companion.parseFragment
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.safety.Safelist.Companion.basic
import org.jsoup.safety.Safelist.Companion.relaxed
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.*
import java.util.stream.Stream

/**
 * Tests for the Parser
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class HtmlParserTest {
    @Test
    fun parsesSimpleDocument() {
        val html =
            "<html><head><title>First!</title></head><body><p>First post! <img src=\"foo.png\" /></p></body></html>"
        val doc = Jsoup.parse(html)
        // need a better way to verify these:
        val p = doc.body().child(0)
        Assertions.assertEquals("p", p.tagName())
        val img = p.child(0)
        Assertions.assertEquals("foo.png", img.attr("src"))
        Assertions.assertEquals("img", img.tagName())
    }

    @Test
    fun parsesRoughAttributes() {
        val html =
            "<html><head><title>First!</title></head><body><p class=\"foo > bar\">First post! <img src=\"foo.png\" /></p></body></html>"
        val doc = Jsoup.parse(html)

        // need a better way to verify these:
        val p = doc.body().child(0)
        Assertions.assertEquals("p", p.tagName())
        Assertions.assertEquals("foo > bar", p.attr("class"))
    }

    @ParameterizedTest
    @MethodSource("dupeAttributeData")
    fun dropsDuplicateAttributes(html: String?, expected: String?) {
        val parser = htmlParser().setTrackErrors(10)
        val doc = parser.parseInput(html!!, "")
        val el = doc.expectFirst("body > *")
        Assertions.assertEquals(expected, el.outerHtml()) // normalized names due to lower casing
        val tag = el.normalName()
        Assertions.assertEquals(1, parser.errors.size)
        Assertions.assertEquals("Dropped duplicate attribute(s) in tag [$tag]", parser.errors[0].errorMessage)
    }

    @Test
    fun retainsAttributesOfDifferentCaseIfSensitive() {
        val html = "<p One=One One=Two one=Three two=Four two=Five Two=Six>Text</p>"
        val parser = htmlParser().settings(ParseSettings.preserveCase)
        val doc = parser.parseInput(html, "")
        Assertions.assertEquals(
            "<p One=\"One\" one=\"Three\" two=\"Four\" Two=\"Six\">Text</p>", doc.selectFirst("p")!!
                .outerHtml()
        )
    }

    @Test
    fun parsesQuiteRoughAttributes() {
        val html = "<p =a>One<a <p>Something</p>Else"
        // this (used to; now gets cleaner) gets a <p> with attr '=a' and an <a tag with an attribute named '<p'; and then auto-recreated
        var doc = Jsoup.parse(html)

        // NOTE: per spec this should be the test case. but impacts too many ppl
        // assertEquals("<p =a>One<a <p>Something</a></p>\n<a <p>Else</a>", doc.body().html());
        Assertions.assertEquals(
            "<p a>One<a></a></p><p><a>Something</a></p><a>Else</a>",
            TextUtil.stripNewlines(doc.body().html())
        )
        doc = Jsoup.parse("<p .....>")
        Assertions.assertEquals("<p .....></p>", doc.body().html())
    }

    @Test
    fun parsesComments() {
        val html = "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --><p>Hello</p></body></html>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val comment = body.childNode(1) as Comment // comment should not be sub of img, as it's an empty tag
        Assertions.assertEquals(" <table><tr><td></table> ", comment.data)
        val p = body.child(1)
        val text = p.childNode(0) as TextNode
        Assertions.assertEquals("Hello", text.wholeText)
    }

    @Test
    fun parsesUnterminatedComments() {
        val html = "<p>Hello<!-- <tr><td>"
        val doc = Jsoup.parse(html)
        val p = doc.getElementsByTag("p")[0]
        Assertions.assertEquals("Hello", p.text())
        val text = p.childNode(0) as TextNode
        Assertions.assertEquals("Hello", text.wholeText)
        val comment = p.childNode(1) as Comment
        Assertions.assertEquals(" <tr><td>", comment.data)
    }

    @Test
    fun allDashCommentsAreNotParseErrors() {
        // https://github.com/jhy/jsoup/issues/1667
        // <!-----> is not a parse error
        val html = "<!------>"
        val parser = htmlParser().setTrackErrors(10)
        val doc = parse(html, parser)
        val comment = doc.childNode(0) as Comment
        Assertions.assertEquals("--", comment.data)
        Assertions.assertEquals(0, parser.errors.size)
    }

    @Test
    fun dropsUnterminatedTag() {
        // jsoup used to parse this to <p>, but whatwg, webkit will drop.
        val h1 = "<p"
        var doc = Jsoup.parse(h1)
        Assertions.assertEquals(0, doc.getElementsByTag("p").size)
        Assertions.assertEquals("", doc.text())
        val h2 = "<div id=1<p id='2'"
        doc = Jsoup.parse(h2)
        Assertions.assertEquals("", doc.text())
    }

    @Test
    fun dropsUnterminatedAttribute() {
        // jsoup used to parse this to <p id="foo">, but whatwg, webkit will drop.
        val h1 = "<p id=\"foo"
        val doc = Jsoup.parse(h1)
        Assertions.assertEquals("", doc.text())
    }

    @Test
    fun parsesUnterminatedTextarea() {
        // don't parse right to end, but break on <p>
        val doc = Jsoup.parse("<body><p><textarea>one<p>two")
        val t = doc.select("textarea").first()
        Assertions.assertEquals("one", t!!.text())
        Assertions.assertEquals("two", doc.select("p")[1].text())
    }

    @Test
    fun parsesUnterminatedOption() {
        // bit weird this -- browsers and spec get stuck in select until there's a </select>
        val doc = Jsoup.parse("<body><p><select><option>One<option>Two</p><p>Three</p>")
        val options = doc.select("option")
        Assertions.assertEquals(2, options.size)
        Assertions.assertEquals("One", options.first()!!.text())
        Assertions.assertEquals("TwoThree", options.last()!!.text())
    }

    @Test
    fun testSelectWithOption() {
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val document = parser.parseInput("<select><option>Option 1</option></select>", "http://jsoup.org")
        Assertions.assertEquals(0, parser.errors.size)
    }

    @Test
    fun testSpaceAfterTag() {
        val doc = Jsoup.parse("<div > <a name=\"top\"></a ><p id=1 >Hello</p></div>")
        Assertions.assertEquals(
            "<div><a name=\"top\"></a><p id=\"1\">Hello</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun createsDocumentStructure() {
        val html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>"
        val doc = Jsoup.parse(html)
        val head = doc.head()
        val body = doc.body()
        Assertions.assertEquals(1, doc.children().size) // root node: contains html node
        Assertions.assertEquals(2, doc.child(0).children().size) // html node: head and body
        Assertions.assertEquals(3, head.children().size)
        Assertions.assertEquals(1, body.children().size)
        Assertions.assertEquals("keywords", head.getElementsByTag("meta")[0].attr("name"))
        Assertions.assertEquals(0, body.getElementsByTag("meta").size)
        Assertions.assertEquals("jsoup", doc.title())
        Assertions.assertEquals("Hello world", body.text())
        Assertions.assertEquals("Hello world", body.children()[0].text())
    }

    @Test
    fun createsStructureFromBodySnippet() {
        // the bar baz stuff naturally goes into the body, but the 'foo' goes into root, and the normalisation routine
        // needs to move into the start of the body
        val html = "foo <b>bar</b> baz"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("foo bar baz", doc.text())
    }

    @Test
    fun handlesEscapedData() {
        val html = "<div title='Surf &amp; Turf'>Reef &amp; Beef</div>"
        val doc = Jsoup.parse(html)
        val div = doc.getElementsByTag("div")[0]
        Assertions.assertEquals("Surf & Turf", div.attr("title"))
        Assertions.assertEquals("Reef & Beef", div.text())
    }

    @Test
    fun handlesDataOnlyTags() {
        val t = "<style>font-family: bold</style>"
        val tels: List<Element> = Jsoup.parse(t).getElementsByTag("style")
        Assertions.assertEquals("font-family: bold", tels[0].data())
        Assertions.assertEquals("", tels[0].text())
        val s = "<p>Hello</p><script>obj.insert('<a rel=\"none\" />');\ni++;</script><p>There</p>"
        val doc = Jsoup.parse(s)
        Assertions.assertEquals("Hello There", doc.text())
        Assertions.assertEquals("obj.insert('<a rel=\"none\" />');\ni++;", doc.data())
    }

    @Test
    fun handlesTextAfterData() {
        val h = "<html><body>pre <script>inner</script> aft</body></html>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head></head><body>pre <script>inner</script> aft</body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun handlesTextArea() {
        val doc = Jsoup.parse("<textarea>Hello</textarea>")
        val els = doc.select("textarea")
        Assertions.assertEquals("Hello", els.text())
        Assertions.assertEquals("Hello", els.value())
    }

    @Test
    fun preservesSpaceInTextArea() {
        // preserve because the tag is marked as preserve white space
        val doc = Jsoup.parse("<textarea>\n\tOne\n\tTwo\n\tThree\n</textarea>")
        val expect = "One\n\tTwo\n\tThree" // the leading and trailing spaces are dropped as a convenience to authors
        val el = doc.select("textarea").first()
        Assertions.assertEquals(expect, el!!.text())
        Assertions.assertEquals(expect, el.value())
        Assertions.assertEquals(expect, el.html())
        Assertions.assertEquals(
            "<textarea>\n\t$expect\n</textarea>",
            el.outerHtml()
        ) // but preserved in round-trip html
    }

    @Test
    fun preservesSpaceInScript() {
        // preserve because it's content is a data node
        val doc = Jsoup.parse("<script>\nOne\n\tTwo\n\tThree\n</script>")
        val expect = "\nOne\n\tTwo\n\tThree\n"
        val el = doc.select("script").first()
        Assertions.assertEquals(expect, el!!.data())
        Assertions.assertEquals("One\n\tTwo\n\tThree", el.html())
        Assertions.assertEquals("<script>$expect</script>", el.outerHtml())
    }

    @Test
    fun doesNotCreateImplicitLists() {
        // old jsoup used to wrap this in <ul>, but that's not to spec
        val h = "<li>Point one<li>Point two"
        val doc = Jsoup.parse(h)
        val ol = doc.select("ul") // should NOT have created a default ul.
        Assertions.assertEquals(0, ol.size)
        val lis = doc.select("li")
        Assertions.assertEquals(2, lis.size)
        Assertions.assertEquals("body", lis.first()!!.parent()!!.tagName())

        // no fiddling with non-implicit lists
        val h2 = "<ol><li><p>Point the first<li><p>Point the second"
        val doc2 = Jsoup.parse(h2)
        Assertions.assertEquals(0, doc2.select("ul").size)
        Assertions.assertEquals(1, doc2.select("ol").size)
        Assertions.assertEquals(2, doc2.select("ol li").size)
        Assertions.assertEquals(2, doc2.select("ol li p").size)
        Assertions.assertEquals(1, doc2.select("ol li")[0].children().size) // one p in first li
    }

    @Test
    fun discardsNakedTds() {
        // jsoup used to make this into an implicit table; but browsers make it into a text run
        val h = "<td>Hello<td><p>There<p>now"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("Hello<p>There</p><p>now</p>", TextUtil.stripNewlines(doc.body().html()))
        // <tbody> is introduced if no implicitly creating table, but allows tr to be directly under table
    }

    @Test
    fun handlesNestedImplicitTable() {
        val doc =
            Jsoup.parse("<table><td>1</td></tr> <td>2</td></tr> <td> <table><td>3</td> <td>4</td></table> <tr><td>5</table>")
        Assertions.assertEquals(
            "<table><tbody><tr><td>1</td></tr><tr><td>2</td></tr><tr><td><table><tbody><tr><td>3</td><td>4</td></tr></tbody></table></td></tr><tr><td>5</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesWhatWgExpensesTableExample() {
        // http://www.whatwg.org/specs/web-apps/current-work/multipage/tabular-data.html#examples-0
        val doc =
            Jsoup.parse("<table> <colgroup> <col> <colgroup> <col> <col> <col> <thead> <tr> <th> <th>2008 <th>2007 <th>2006 <tbody> <tr> <th scope=rowgroup> Research and development <td> $ 1,109 <td> $ 782 <td> $ 712 <tr> <th scope=row> Percentage of net sales <td> 3.4% <td> 3.3% <td> 3.7% <tbody> <tr> <th scope=rowgroup> Selling, general, and administrative <td> $ 3,761 <td> $ 2,963 <td> $ 2,433 <tr> <th scope=row> Percentage of net sales <td> 11.6% <td> 12.3% <td> 12.6% </table>")
        Assertions.assertEquals(
            "<table><colgroup><col></colgroup><colgroup><col><col><col></colgroup><thead><tr><th></th><th>2008</th><th>2007</th><th>2006</th></tr></thead><tbody><tr><th scope=\"rowgroup\">Research and development</th><td>$ 1,109</td><td>$ 782</td><td>$ 712</td></tr><tr><th scope=\"row\">Percentage of net sales</th><td>3.4%</td><td>3.3%</td><td>3.7%</td></tr></tbody><tbody><tr><th scope=\"rowgroup\">Selling, general, and administrative</th><td>$ 3,761</td><td>$ 2,963</td><td>$ 2,433</td></tr><tr><th scope=\"row\">Percentage of net sales</th><td>11.6%</td><td>12.3%</td><td>12.6%</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesTbodyTable() {
        val doc =
            Jsoup.parse("<html><head></head><body><table><tbody><tr><td>aaa</td><td>bbb</td></tr></tbody></table></body></html>")
        Assertions.assertEquals(
            "<table><tbody><tr><td>aaa</td><td>bbb</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesImplicitCaptionClose() {
        val doc = Jsoup.parse("<table><caption>A caption<td>One<td>Two")
        Assertions.assertEquals(
            "<table><caption>A caption</caption><tbody><tr><td>One</td><td>Two</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun noTableDirectInTable() {
        val doc = Jsoup.parse("<table> <td>One <td><table><td>Two</table> <table><td>Three")
        Assertions.assertEquals(
            "<table><tbody><tr><td>One</td><td><table><tbody><tr><td>Two</td></tr></tbody></table><table><tbody><tr><td>Three</td></tr></tbody></table></td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun ignoresDupeEndTrTag() {
        val doc =
            Jsoup.parse("<table><tr><td>One</td><td><table><tr><td>Two</td></tr></tr></table></td><td>Three</td></tr></table>") // two </tr></tr>, must ignore or will close table
        Assertions.assertEquals(
            "<table><tbody><tr><td>One</td><td><table><tbody><tr><td>Two</td></tr></tbody></table></td><td>Three</td></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesBaseTags() {
        // only listen to the first base href
        val h = "<a href=1>#</a><base href='/2/'><a href='3'>#</a><base href='http://bar'><a href=/4>#</a>"
        val doc = parse(h, "http://foo/")
        Assertions.assertEquals("http://foo/2/", doc.baseUri()) // gets set once, so doc and descendants have first only
        val anchors = doc.getElementsByTag("a")
        Assertions.assertEquals(3, anchors.size)
        Assertions.assertEquals("http://foo/2/", anchors[0].baseUri())
        Assertions.assertEquals("http://foo/2/", anchors[1].baseUri())
        Assertions.assertEquals("http://foo/2/", anchors[2].baseUri())
        Assertions.assertEquals("http://foo/2/1", anchors[0].absUrl("href"))
        Assertions.assertEquals("http://foo/2/3", anchors[1].absUrl("href"))
        Assertions.assertEquals("http://foo/4", anchors[2].absUrl("href"))
    }

    @Test
    fun handlesProtocolRelativeUrl() {
        val base = "https://example.com/"
        val html = "<img src='//example.net/img.jpg'>"
        val doc = parse(html, base)
        val el = doc.select("img").first()
        Assertions.assertEquals("https://example.net/img.jpg", el!!.absUrl("src"))
    }

    @Test
    fun handlesCdata() {
        // todo: as this is html namespace, should actually treat as bogus comment, not cdata. keep as cdata for now
        val h = "<div id=1><![CDATA[<html>\n <foo><&amp;]]></div>" // the &amp; in there should remain literal
        val doc = Jsoup.parse(h)
        val div = doc.getElementById("1")
        Assertions.assertEquals("<html>\n <foo><&amp;", div!!.text())
        Assertions.assertEquals(0, div.children().size)
        Assertions.assertEquals(1, div.childNodeSize()) // no elements, one text node
    }

    @Test
    fun roundTripsCdata() {
        val h = "<div id=1><![CDATA[\n<html>\n <foo><&amp;]]></div>"
        val doc = Jsoup.parse(h)
        val div = doc.getElementById("1")
        Assertions.assertEquals("<html>\n <foo><&amp;", div!!.text())
        Assertions.assertEquals(0, div.children().size)
        Assertions.assertEquals(1, div.childNodeSize()) // no elements, one text node
        Assertions.assertEquals("<div id=\"1\"><![CDATA[\n<html>\n <foo><&amp;]]>\n</div>", div.outerHtml())
        val cdata = div.textNodes()[0] as CDataNode
        Assertions.assertEquals("\n<html>\n <foo><&amp;", cdata.text())
    }

    @Test
    fun handlesCdataAcrossBuffer() {
        val sb = StringBuilder()
        while (sb.length <= CharacterReader.maxBufferLen) {
            sb.append("A suitable amount of CData.\n")
        }
        val cdata = sb.toString()
        val h = "<div><![CDATA[$cdata]]></div>"
        val doc = Jsoup.parse(h)
        val div = doc.selectFirst("div")
        val node = div!!.textNodes()[0] as CDataNode
        Assertions.assertEquals(cdata, node.text())
    }

    @Test
    fun handlesCdataInScript() {
        val html = "<script type=\"text/javascript\">//<![CDATA[\n\n  foo();\n//]]></script>"
        val doc = Jsoup.parse(html)
        val data = "//<![CDATA[\n\n  foo();\n//]]>"
        val script = doc.selectFirst("script")
        Assertions.assertEquals("", script!!.text()) // won't be parsed as cdata because in script data section
        Assertions.assertEquals(data, script.data())
        Assertions.assertEquals(html, script.outerHtml())
        val dataNode = script.childNode(0) as DataNode
        Assertions.assertEquals(data, dataNode.wholeData)
        // see - not a cdata node, because in script. contrast with XmlTreeBuilder - will be cdata.
    }

    @Test
    fun handlesUnclosedCdataAtEOF() {
        // https://github.com/jhy/jsoup/issues/349 would crash, as character reader would try to seek past EOF
        val h = "<![CDATA[]]"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(1, doc.body().childNodeSize())
    }

    @Test
    fun handleCDataInText() {
        val h = "<p>One <![CDATA[Two <&]]> Three</p>"
        val doc = Jsoup.parse(h)
        val p = doc.selectFirst("p")
        val nodes = p!!.childNodes()
        Assertions.assertEquals("One ", (nodes[0] as TextNode).wholeText)
        Assertions.assertEquals("Two <&", (nodes[1] as TextNode).wholeText)
        Assertions.assertEquals("Two <&", (nodes[1] as CDataNode).wholeText)
        Assertions.assertEquals(" Three", (nodes[2] as TextNode).wholeText)
        Assertions.assertEquals(h, p.outerHtml())
    }

    @Test
    fun cdataNodesAreTextNodes() {
        val h = "<p>One <![CDATA[ Two <& ]]> Three</p>"
        val doc = Jsoup.parse(h)
        val p = doc.selectFirst("p")
        val nodes = p!!.textNodes()
        Assertions.assertEquals("One ", nodes[0].text())
        Assertions.assertEquals(" Two <& ", nodes[1].text())
        Assertions.assertEquals(" Three", nodes[2].text())
    }

    @Test
    fun handlesInvalidStartTags() {
        val h = "<div>Hello < There <&amp;></div>" // parse to <div {#text=Hello < There <&>}>
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("Hello < There <&>", doc.select("div").first()!!.text())
    }

    @Test
    fun handlesUnknownTags() {
        val h = "<div><foo title=bar>Hello<foo title=qux>there</foo></div>"
        val doc = Jsoup.parse(h)
        val foos = doc.select("foo")
        Assertions.assertEquals(2, foos.size)
        Assertions.assertEquals("bar", foos.first()!!.attr("title"))
        Assertions.assertEquals("qux", foos.last()!!.attr("title"))
        Assertions.assertEquals("there", foos.last()!!.text())
    }

    @Test
    fun handlesUnknownInlineTags() {
        val h = "<p><cust>Test</cust></p><p><cust><cust>Test</cust></cust></p>"
        val doc = parseBodyFragment(h)
        val out = doc.body().html()
        Assertions.assertEquals(h, TextUtil.stripNewlines(out))
    }

    @Test
    fun parsesBodyFragment() {
        val h = "<!-- comment --><p><a href='foo'>One</a></p>"
        val doc = Jsoup.parseBodyFragment(h, "http://example.com")
        Assertions.assertEquals(
            "<body><!-- comment --><p><a href=\"foo\">One</a></p></body>",
            TextUtil.stripNewlines(doc.body().outerHtml())
        )
        Assertions.assertEquals("http://example.com/foo", doc.select("a").first()!!.absUrl("href"))
    }

    @Test
    fun parseBodyIsIndexNoAttributes() {
        // https://github.com/jhy/jsoup/issues/1404
        val expectedHtml = """<form>
 <hr><label>This is a searchable index. Enter search keywords: <input name="isindex"></label>
 <hr>
</form>"""
        var doc = Jsoup.parse("<isindex>")
        Assertions.assertEquals(expectedHtml, doc.body().html())
        doc = parseBodyFragment("<isindex>")
        Assertions.assertEquals(expectedHtml, doc.body().html())
        doc = parseBodyFragment("<table><input></table>")
        Assertions.assertEquals("<input>\n<table></table>", doc.body().html())
    }

    @Test
    fun handlesUnknownNamespaceTags() {
        // note that the first foo:bar should not really be allowed to be self closing, if parsed in html mode.
        val h = "<foo:bar id='1' /><abc:def id=2>Foo<p>Hello</p></abc:def><foo:bar>There</foo:bar>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<foo:bar id=\"1\" /><abc:def id=\"2\">Foo<p>Hello</p></abc:def><foo:bar>There</foo:bar>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesKnownEmptyBlocks() {
        // if a known tag, allow self closing outside of spec, but force an end tag. unknown tags can be self closing.
        val h =
            "<div id='1' /><script src='/foo' /><div id=2><img /><img></div><a id=3 /><i /><foo /><foo>One</foo> <hr /> hr text <hr> hr text two"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<div id=\"1\"></div><script src=\"/foo\"></script><div id=\"2\"><img><img></div><a id=\"3\"></a><i></i><foo /><foo>One</foo><hr> hr text <hr> hr text two",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesKnownEmptyNoFrames() {
        val h = "<html><head><noframes /><meta name=foo></head><body>One</body></html>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head><noframes></noframes><meta name=\"foo\"></head><body>One</body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun handlesKnownEmptyStyle() {
        val h = "<html><head><style /><meta name=foo></head><body>One</body></html>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head><style></style><meta name=\"foo\"></head><body>One</body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun handlesKnownEmptyTitle() {
        val h = "<html><head><title /><meta name=foo></head><body>One</body></html>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head><title></title><meta name=\"foo\"></head><body>One</body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun handlesKnownEmptyIframe() {
        val h = "<p>One</p><iframe id=1 /><p>Two"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head></head><body><p>One</p><iframe id=\"1\"></iframe><p>Two</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun handlesSolidusAtAttributeEnd() {
        // this test makes sure [<a href=/>link</a>] is parsed as [<a href="/">link</a>], not [<a href="" /><a>link</a>]
        val h = "<a href=/>link</a>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("<a href=\"/\">link</a>", doc.body().html())
    }

    @Test
    fun handlesMultiClosingBody() {
        val h = "<body><p>Hello</body><p>there</p></body></body></html><p>now"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(3, doc.select("p").size)
        Assertions.assertEquals(3, doc.body().children().size)
    }

    @Test
    fun handlesUnclosedDefinitionLists() {
        // jsoup used to create a <dl>, but that's not to spec
        val h = "<dt>Foo<dd>Bar<dt>Qux<dd>Zug"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(0, doc.select("dl").size) // no auto dl
        Assertions.assertEquals(4, doc.select("dt, dd").size)
        val dts = doc.select("dt")
        Assertions.assertEquals(2, dts.size)
        Assertions.assertEquals("Zug", dts[1].nextElementSibling()!!.text())
    }

    @Test
    fun handlesBlocksInDefinitions() {
        // per the spec, dt and dd are inline, but in practise are block
        val h = "<dl><dt><div id=1>Term</div></dt><dd><div id=2>Def</div></dd></dl>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("dt", doc.select("#1").first()!!.parent()!!.tagName())
        Assertions.assertEquals("dd", doc.select("#2").first()!!.parent()!!.tagName())
        Assertions.assertEquals(
            "<dl><dt><div id=\"1\">Term</div></dt><dd><div id=\"2\">Def</div></dd></dl>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesFrames() {
        val h =
            "<html><head><script></script><noscript></noscript></head><frameset><frame src=foo></frame><frame src=foo></frameset></html>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\"><frame src=\"foo\"></frameset></html>",
            TextUtil.stripNewlines(doc.html())
        )
        // no body auto vivification
    }

    @Test
    fun ignoresContentAfterFrameset() {
        val h = "<html><head><title>One</title></head><frameset><frame /><frame /></frameset><table></table></html>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<html><head><title>One</title></head><frameset><frame><frame></frameset></html>",
            TextUtil.stripNewlines(doc.html())
        )
        // no body, no table. No crash!
    }

    @Test
    fun handlesJavadocFont() {
        val h =
            "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>"
        val doc = Jsoup.parse(h)
        val a = doc.select("a").first()
        Assertions.assertEquals("Deprecated", a!!.text())
        Assertions.assertEquals("font", a.child(0).tagName())
        Assertions.assertEquals("b", a.child(0).child(0).tagName())
    }

    @Test
    fun handlesBaseWithoutHref() {
        val h = "<head><base target='_blank'></head><body><a href=/foo>Test</a></body>"
        val doc = parse(h, "http://example.com/")
        val a = doc.select("a").first()
        Assertions.assertEquals("/foo", a!!.attr("href"))
        Assertions.assertEquals("http://example.com/foo", a.attr("abs:href"))
    }

    @Test
    fun normalisesDocument() {
        val h = "<!doctype html>One<html>Two<head>Three<link></head>Four<body>Five </body>Six </html>Seven "
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<!doctype html><html><head></head><body>OneTwoThree<link>FourFive Six Seven</body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun normalisesEmptyDocument() {
        val doc = Jsoup.parse("")
        Assertions.assertEquals("<html><head></head><body></body></html>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun normalisesHeadlessBody() {
        val doc = Jsoup.parse("<html><body><span class=\"foo\">bar</span>")
        Assertions.assertEquals(
            "<html><head></head><body><span class=\"foo\">bar</span></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun normalisedBodyAfterContent() {
        val doc = Jsoup.parse("<font face=Arial><body class=name><div>One</div></body></font>")
        Assertions.assertEquals(
            "<html><head></head><body class=\"name\"><font face=\"Arial\"><div>One</div></font></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun findsCharsetInMalformedMeta() {
        val h = "<meta http-equiv=Content-Type content=text/html; charset=gb2312>"
        // example cited for reason of html5's <meta charset> element
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("gb2312", doc.select("meta").attr("charset"))
    }

    @Test
    fun testHgroup() {
        // jsoup used to not allow hgroup in h{n}, but that's not in spec, and browsers are OK
        val doc =
            Jsoup.parse("<h1>Hello <h2>There <hgroup><h1>Another<h2>headline</hgroup> <hgroup><h1>More</h1><p>stuff</p></hgroup>")
        Assertions.assertEquals(
            "<h1>Hello</h1><h2>There <hgroup><h1>Another</h1><h2>headline</h2></hgroup><hgroup><h1>More</h1><p>stuff</p></hgroup></h2>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testRelaxedTags() {
        val doc = Jsoup.parse("<abc_def id=1>Hello</abc_def> <abc-def>There</abc-def>")
        Assertions.assertEquals(
            "<abc_def id=\"1\">Hello</abc_def> <abc-def>There</abc-def>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testHeaderContents() {
        // h* tags (h1 .. h9) in browsers can handle any internal content other than other h*. which is not per any
        // spec, which defines them as containing phrasing content only. so, reality over theory.
        val doc = Jsoup.parse("<h1>Hello <div>There</div> now</h1> <h2>More <h3>Content</h3></h2>")
        Assertions.assertEquals(
            "<h1>Hello <div>There</div> now</h1><h2>More</h2><h3>Content</h3>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testSpanContents() {
        // like h1 tags, the spec says SPAN is phrasing only, but browsers and publisher treat span as a block tag
        val doc = Jsoup.parse("<span>Hello <div>there</div> <span>now</span></span>")
        Assertions.assertEquals(
            "<span>Hello <div>there</div><span>now</span></span>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testNoImagesInNoScriptInHead() {
        // jsoup used to allow, but against spec if parsing with noscript
        val doc = Jsoup.parse("<html><head><noscript><img src='foo'></noscript></head><body><p>Hello</p></body></html>")
        Assertions.assertEquals(
            "<html><head><noscript>&lt;img src=\"foo\"&gt;</noscript></head><body><p>Hello</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testUnclosedNoscriptInHead() {
        // Was getting "EOF" in html output, because the #anythingElse handler was calling an undefined toString, so used object.toString.
        val strings = arrayOf("<noscript>", "<noscript>One")
        for (html in strings) {
            val doc = Jsoup.parse(html)
            Assertions.assertEquals("$html</noscript>", TextUtil.stripNewlines(doc.head().html()))
        }
    }

    @Test
    fun testAFlowContents() {
        // html5 has <a> as either phrasing or block
        val doc = Jsoup.parse("<a>Hello <div>there</div> <span>now</span></a>")
        Assertions.assertEquals(
            "<a>Hello <div>there</div><span>now</span></a>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testFontFlowContents() {
        // html5 has no definition of <font>; often used as flow
        val doc = Jsoup.parse("<font>Hello <div>there</div> <span>now</span></font>")
        Assertions.assertEquals(
            "<font>Hello <div>there</div><span>now</span></font>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesMisnestedTagsBI() {
        // whatwg: <b><i></b></i>
        val h = "<p>1<b>2<i>3</b>4</i>5</p>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("<p>1<b>2<i>3</i></b><i>4</i>5</p>", doc.body().html())
        // adoption agency on </b>, reconstruction of formatters on 4.
    }

    @Test
    fun handlesMisnestedTagsBP() {
        //  whatwg: <b><p></b></p>
        val h = "<b>1<p>2</b>3</p>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("<b>1</b>\n<p><b>2</b>3</p>", doc.body().html())
    }

    @Test
    fun handlesMisnestedAInDivs() {
        val h = "<a href='#1'><div><div><a href='#2'>child</a></div</div></a>"
        val w =
            "<a href=\"#1\"></a> <div> <a href=\"#1\"></a> <div> <a href=\"#1\"></a><a href=\"#2\">child</a> </div> </div>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            StringUtil.normaliseWhitespace(w),
            StringUtil.normaliseWhitespace(doc.body().html())
        )
    }

    @Test
    fun handlesUnexpectedMarkupInTables() {
        // whatwg - tests markers in active formatting (if they didn't work, would get in table)
        // also tests foster parenting
        val h = "<table><b><tr><td>aaa</td></tr>bbb</table>ccc"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<b></b><b>bbb</b><table><tbody><tr><td>aaa</td></tr></tbody></table><b>ccc</b>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesUnclosedFormattingElements() {
        // whatwg: formatting elements get collected and applied, but excess elements are thrown away
        val h = """
            <!DOCTYPE html>
            <p><b class=x><b class=x><b><b class=x><b class=x><b>X
            <p>X
            <p><b><b class=x><b>X
            <p></b></b></b></b></b></b>X
            """.trimIndent()
        val doc = Jsoup.parse(h)
        doc.outputSettings().indentAmount(0)
        val want = """
            <!doctype html>
            <html>
            <head></head>
            <body>
            <p><b class="x"><b class="x"><b><b class="x"><b class="x"><b>X </b></b></b></b></b></b></p>
            <p><b class="x"><b><b class="x"><b class="x"><b>X </b></b></b></b></b></p>
            <p><b class="x"><b><b class="x"><b class="x"><b><b><b class="x"><b>X </b></b></b></b></b></b></b></b></p>
            <p>X</p>
            </body>
            </html>
            """.trimIndent()
        Assertions.assertEquals(want, doc.html())
    }

    @Test
    fun handlesUnclosedAnchors() {
        val h = "<a href='http://example.com/'>Link<p>Error link</a>"
        val doc = Jsoup.parse(h)
        val want = "<a href=\"http://example.com/\">Link</a>\n<p><a href=\"http://example.com/\">Error link</a></p>"
        Assertions.assertEquals(want, doc.body().html())
    }

    @Test
    fun reconstructFormattingElements() {
        // tests attributes and multi b
        val h = "<p><b class=one>One <i>Two <b>Three</p><p>Hello</p>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<p><b class=\"one\">One <i>Two <b>Three</b></i></b></p>\n<p><b class=\"one\"><i><b>Hello</b></i></b></p>",
            doc.body().html()
        )
    }

    @Test
    fun reconstructFormattingElementsInTable() {
        // tests that tables get formatting markers -- the <b> applies outside the table and does not leak in,
        // and the <i> inside the table and does not leak out.
        val h = "<p><b>One</p> <table><tr><td><p><i>Three<p>Four</i></td></tr></table> <p>Five</p>"
        val doc = Jsoup.parse(h)
        val want =
            "<p><b>One</b></p><b><table><tbody><tr><td><p><i>Three</i></p><p><i>Four</i></p></td></tr></tbody></table><p>Five</p></b>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.body().html()))
    }

    @Test
    fun commentBeforeHtml() {
        val h = "<!-- comment --><!-- comment 2 --><p>One</p>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(
            "<!-- comment --><!-- comment 2 --><html><head></head><body><p>One</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun emptyTdTag() {
        val h = "<table><tr><td>One</td><td id='2' /></tr></table>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("<td>One</td>\n<td id=\"2\"></td>", doc.select("tr").first()!!.html())
    }

    @Test
    fun handlesSolidusInA() {
        // test for bug #66
        val h = "<a class=lp href=/lib/14160711/>link text</a>"
        val doc = Jsoup.parse(h)
        val a = doc.select("a").first()
        Assertions.assertEquals("link text", a!!.text())
        Assertions.assertEquals("/lib/14160711/", a.attr("href"))
    }

    @Test
    fun handlesSpanInTbody() {
        // test for bug 64
        val h = "<table><tbody><span class='1'><tr><td>One</td></tr><tr><td>Two</td></tr></span></tbody></table>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(doc.select("span").first()!!.children().size, 0) // the span gets closed
        Assertions.assertEquals(doc.select("table").size, 1) // only one table
    }

    @Test
    fun handlesUnclosedTitleAtEof() {
        Assertions.assertEquals("Data", Jsoup.parse("<title>Data").title())
        Assertions.assertEquals("Data<", Jsoup.parse("<title>Data<").title())
        Assertions.assertEquals("Data</", Jsoup.parse("<title>Data</").title())
        Assertions.assertEquals("Data</t", Jsoup.parse("<title>Data</t").title())
        Assertions.assertEquals("Data</ti", Jsoup.parse("<title>Data</ti").title())
        Assertions.assertEquals("Data", Jsoup.parse("<title>Data</title>").title())
        Assertions.assertEquals("Data", Jsoup.parse("<title>Data</title >").title())
    }

    @Test
    fun handlesUnclosedTitle() {
        val one = Jsoup.parse("<title>One <b>Two <b>Three</TITLE><p>Test</p>") // has title, so <b> is plain text
        Assertions.assertEquals("One <b>Two <b>Three", one.title())
        Assertions.assertEquals("Test", one.select("p").first()!!.text())
        val two = Jsoup.parse("<title>One<b>Two <p>Test</p>") // no title, so <b> causes </title> breakout
        Assertions.assertEquals("One", two.title())
        Assertions.assertEquals("<b>Two \n <p>Test</p></b>", two.body().html())
    }

    @Test
    fun handlesUnclosedScriptAtEof() {
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data<", Jsoup.parse("<script>Data<").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</sc", Jsoup.parse("<script>Data</sc").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</-sc", Jsoup.parse("<script>Data</-sc").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</sc-", Jsoup.parse("<script>Data</sc-").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</sc--", Jsoup.parse("<script>Data</sc--").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data</script>").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</script", Jsoup.parse("<script>Data</script").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data</script ").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data</script n").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data</script n=").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data</script n=\"").select("script").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<script>Data</script n=\"p").select("script").first()!!
                .data()
        )
    }

    @Test
    fun handlesUnclosedRawtextAtEof() {
        Assertions.assertEquals(
            "Data", Jsoup.parse("<style>Data").select("style").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</st", Jsoup.parse("<style>Data</st").select("style").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data", Jsoup.parse("<style>Data</style>").select("style").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</style", Jsoup.parse("<style>Data</style").select("style").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</-style", Jsoup.parse("<style>Data</-style").select("style").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</style-", Jsoup.parse("<style>Data</style-").select("style").first()!!
                .data()
        )
        Assertions.assertEquals(
            "Data</style--", Jsoup.parse("<style>Data</style--").select("style").first()!!
                .data()
        )
    }

    @Test
    fun noImplicitFormForTextAreas() {
        // old jsoup parser would create implicit forms for form children like <textarea>, but no more
        val doc = Jsoup.parse("<textarea>One</textarea>")
        Assertions.assertEquals("<textarea>One</textarea>", doc.body().html())
    }

    @Test
    fun handlesEscapedScript() {
        val doc = Jsoup.parse("<script><!-- one <script>Blah</script> --></script>")
        Assertions.assertEquals(
            "<!-- one <script>Blah</script> -->", doc.select("script").first()!!
                .data()
        )
    }

    @Test
    fun handles0CharacterAsText() {
        val doc = Jsoup.parse("0<p>0</p>")
        Assertions.assertEquals("0\n<p>0</p>", doc.body().html())
    }

    @Test
    fun handlesNullInData() {
        val doc = Jsoup.parse("<p id=\u0000>Blah \u0000</p>")
        Assertions.assertEquals(
            "<p id=\"\uFFFD\">Blah &#x0;</p>",
            doc.body().html()
        ) // replaced in attr, NOT replaced in data (but is escaped as control char <0x20)
    }

    @Test
    fun handlesNullInComments() {
        val doc = Jsoup.parse("<body><!-- \u0000 \u0000 -->")
        Assertions.assertEquals("<!-- \uFFFD \uFFFD -->", doc.body().html())
    }

    @Test
    fun handlesNewlinesAndWhitespaceInTag() {
        val doc = Jsoup.parse("<a \n href=\"one\" \r\n id=\"two\" \u000c >")
        Assertions.assertEquals("<a href=\"one\" id=\"two\"></a>", doc.body().html())
    }

    @Test
    fun handlesWhitespaceInoDocType() {
        val html = """<!DOCTYPE html
      PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">"""
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">",
            doc.childNode(0).outerHtml()
        )
    }

    @Test
    fun tracksErrorsWhenRequested() {
        val html = "<p>One</p href='no'>\n<!DOCTYPE html>\n&arrgh;<font />&#33 &amp &#xD800;<br /></div><foo"
        val parser = htmlParser().setTrackErrors(500)
        val doc = parse(html, "http://example.com", parser)
        val errors: List<ParseError> = parser.errors
        Assertions.assertEquals(9, errors.size)
        Assertions.assertEquals("<1:21>: Attributes incorrectly present on end tag [/p]", errors[0].toString())
        Assertions.assertEquals(
            "<2:16>: Unexpected Doctype token [<!doctype html>] when in state [InBody]",
            errors[1].toString()
        )
        Assertions.assertEquals(
            "<3:2>: Invalid character reference: invalid named reference [arrgh]",
            errors[2].toString()
        )
        Assertions.assertEquals("<3:16>: Tag [font] cannot be self closing; not a void tag", errors[3].toString())
        Assertions.assertEquals(
            "<3:20>: Invalid character reference: missing semicolon on [&#33]",
            errors[4].toString()
        )
        Assertions.assertEquals(
            "<3:25>: Invalid character reference: missing semicolon on [&amp]",
            errors[5].toString()
        )
        Assertions.assertEquals(
            "<3:34>: Invalid character reference: character [55296] outside of valid range",
            errors[6].toString()
        )
        Assertions.assertEquals("<3:46>: Unexpected EndTag token [</div>] when in state [InBody]", errors[7].toString())
        Assertions.assertEquals(
            "<3:51>: Unexpectedly reached end of file (EOF) in input state [TagName]",
            errors[8].toString()
        )
    }

    @Test
    fun tracksLimitedErrorsWhenRequested() {
        val html = "<p>One</p href='no'>\n<!DOCTYPE html>\n&arrgh;<font /><br /><foo"
        val parser = htmlParser().setTrackErrors(3)
        val doc = parser.parseInput(html, "http://example.com")
        val errors: List<ParseError> = parser.errors
        Assertions.assertEquals(3, errors.size)
        Assertions.assertEquals("<1:21>: Attributes incorrectly present on end tag [/p]", errors[0].toString())
        Assertions.assertEquals(
            "<2:16>: Unexpected Doctype token [<!doctype html>] when in state [InBody]",
            errors[1].toString()
        )
        Assertions.assertEquals(
            "<3:2>: Invalid character reference: invalid named reference [arrgh]",
            errors[2].toString()
        )
    }

    @Test
    fun noErrorsByDefault() {
        val html = "<p>One</p href='no'>&arrgh;<font /><br /><foo"
        val parser = htmlParser()
        val doc = parse(html, "http://example.com", parser)
        val errors: List<ParseError> = parser.errors
        Assertions.assertEquals(0, errors.size)
    }

    @Test
    fun optionalPClosersAreNotErrors() {
        val html = "<body><div><p>One<p>Two</div></body>"
        val parser = htmlParser().setTrackErrors(128)
        val doc = parse(html, "", parser)
        val errors = parser.errors
        Assertions.assertEquals(0, errors.size)
    }

    @Test
    fun handlesCommentsInTable() {
        val html = "<table><tr><td>text</td><!-- Comment --></tr></table>"
        val node = parseBodyFragment(html)
        Assertions.assertEquals(
            "<html><head></head><body><table><tbody><tr><td>text</td><!-- Comment --></tr></tbody></table></body></html>",
            TextUtil.stripNewlines(node.outerHtml())
        )
    }

    @Test
    fun handlesQuotesInCommentsInScripts() {
        val html = """<script>
  <!--
    document.write('</scr' + 'ipt>');
  // -->
</script>"""
        val node = parseBodyFragment(html)
        Assertions.assertEquals(
            """<script>
  <!--
    document.write('</scr' + 'ipt>');
  // -->
</script>""", node.body().html()
        )
    }

    @Test
    fun handleNullContextInParseFragment() {
        val html = "<ol><li>One</li></ol><p>Two</p>"
        val nodes = parseFragment(html, null, "http://example.com/")
        Assertions.assertEquals(
            1,
            nodes.size
        ) // returns <html> node (not document) -- no context means doc gets created
        Assertions.assertEquals("html", nodes[0].nodeName())
        Assertions.assertEquals(
            "<html> <head></head> <body> <ol> <li>One</li> </ol> <p>Two</p> </body> </html>",
            StringUtil.normaliseWhitespace(
                nodes[0].outerHtml()
            )
        )
    }

    @Test
    fun doesNotFindShortestMatchingEntity() {
        // previous behaviour was to identify a possible entity, then chomp down the string until a match was found.
        // (as defined in html5.) However in practise that lead to spurious matches against the author's intent.
        val html = "One &clubsuite; &clubsuit;"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(StringUtil.normaliseWhitespace("One &amp;clubsuite; ♣"), doc.body().html())
    }

    @Test
    fun relaxedBaseEntityMatchAndStrictExtendedMatch() {
        // extended entities need a ; at the end to match, base does not
        val html = "&amp &quot &reg &icy &hopf &icy; &hopf;"
        val doc = Jsoup.parse(html)
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended)
            .charset("ascii") // modifies output only to clarify test
        Assertions.assertEquals("&amp; \" &reg; &amp;icy &amp;hopf &icy; &hopf;", doc.body().html())
    }

    @Test
    fun handlesXmlDeclarationAsBogusComment() {
        val html = "<?xml encoding='UTF-8' ?><body>One</body>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            "<!--?xml encoding='UTF-8' ?--> <html> <head></head> <body> One </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
    }

    @Test
    fun handlesTagsInTextarea() {
        val html = "<textarea><p>Jsoup</p></textarea>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<textarea>&lt;p&gt;Jsoup&lt;/p&gt;</textarea>", doc.body().html())
    }

    // form tests
    @Test
    fun createsFormElements() {
        val html = "<body><form><input id=1><input id=2></form></body>"
        val doc = Jsoup.parse(html)
        val el = doc.select("form").first()
        Assertions.assertTrue(el is FormElement, "Is form element")
        val form = el as FormElement?
        val controls = form!!.elements()
        Assertions.assertEquals(2, controls.size)
        Assertions.assertEquals("1", controls[0].id())
        Assertions.assertEquals("2", controls[1].id())
    }

    @Test
    fun associatedFormControlsWithDisjointForms() {
        // form gets closed, isn't parent of controls
        val html = "<table><tr><form><input type=hidden id=1><td><input type=text id=2></td><tr></table>"
        val doc = Jsoup.parse(html)
        val el = doc.select("form").first()
        Assertions.assertTrue(el is FormElement, "Is form element")
        val form = el as FormElement?
        val controls = form!!.elements()
        Assertions.assertEquals(2, controls.size)
        Assertions.assertEquals("1", controls[0].id())
        Assertions.assertEquals("2", controls[1].id())
        Assertions.assertEquals(
            "<table><tbody><tr><form></form><input type=\"hidden\" id=\"1\"><td><input type=\"text\" id=\"2\"></td></tr><tr></tr></tbody></table>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun handlesInputInTable() {
        val h = """<body>
<input type="hidden" name="a" value="">
<table>
<input type="hidden" name="b" value="" />
</table>
</body>"""
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(1, doc.select("table input").size)
        Assertions.assertEquals(2, doc.select("input").size)
    }

    @Test
    fun convertsImageToImg() {
        // image to img, unless in a svg. old html cruft.
        val h = "<body><image><svg><image /></svg></body>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("<img>\n<svg>\n <image />\n</svg>", doc.body().html())
    }

    @Test
    fun handlesInvalidDoctypes() {
        // would previously throw invalid name exception on empty doctype
        var doc = Jsoup.parse("<!DOCTYPE>")
        Assertions.assertEquals(
            "<!doctype> <html> <head></head> <body></body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
        doc = Jsoup.parse("<!DOCTYPE><html><p>Foo</p></html>")
        Assertions.assertEquals(
            "<!doctype> <html> <head></head> <body> <p>Foo</p> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
        doc = Jsoup.parse("<!DOCTYPE \u0000>")
        Assertions.assertEquals(
            "<!doctype �> <html> <head></head> <body></body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
    }

    @Test
    fun handlesManyChildren() {
        // Arrange
        val longBody = StringBuilder(500000)
        for (i in 0..24999) {
            longBody.append(i).append("<br>")
        }

        // Act
        val start = System.currentTimeMillis()
        val doc = Parser.parseBodyFragment(longBody.toString(), "")

        // Assert
        Assertions.assertEquals(50000, doc.body().childNodeSize())
        Assertions.assertTrue(System.currentTimeMillis() - start < 1000)
    }

    @Test
    @Throws(IOException::class)
    fun testInvalidTableContents() {
        val `in`: File = ParseTest.getFile("/htmltests/table-invalid-elements.html")
        val doc = parse(`in`, "UTF-8")
        doc.outputSettings().prettyPrint(true)
        val rendered = doc.toString()
        val endOfEmail = rendered.indexOf("Comment")
        val guarantee = rendered.indexOf("Why am I here?")
        Assertions.assertTrue(endOfEmail > -1, "Comment not found")
        Assertions.assertTrue(guarantee > -1, "Search text not found")
        Assertions.assertTrue(guarantee > endOfEmail, "Search text did not come after comment")
    }

    @Test
    fun testNormalisesIsIndex() {
        val doc = Jsoup.parse("<body><isindex action='/submit'></body>")
        val html = doc.outerHtml()
        Assertions.assertEquals(
            "<form action=\"/submit\"> <hr><label>This is a searchable index. Enter search keywords: <input name=\"isindex\"></label> <hr> </form>",
            StringUtil.normaliseWhitespace(doc.body().html())
        )
    }

    @Test
    fun testReinsertionModeForThCelss() {
        val body =
            "<body> <table> <tr> <th> <table><tr><td></td></tr></table> <div> <table><tr><td></td></tr></table> </div> <div></div> <div></div> <div></div> </th> </tr> </table> </body>"
        val doc = Jsoup.parse(body)
        Assertions.assertEquals(1, doc.body().children().size)
    }

    @Test
    fun testUsingSingleQuotesInQueries() {
        val body = "<body> <div class='main'>hello</div></body>"
        val doc = Jsoup.parse(body)
        val main = doc.select("div[class='main']")
        Assertions.assertEquals("hello", main.text())
    }

    @Test
    fun testSupportsNonAsciiTags() {
        val body = "<a進捗推移グラフ>Yes</a進捗推移グラフ><bрусский-тэг>Correct</<bрусский-тэг>"
        val doc = Jsoup.parse(body)
        var els = doc.select("a進捗推移グラフ")
        Assertions.assertEquals("Yes", els.text())
        els = doc.select("bрусский-тэг")
        Assertions.assertEquals("Correct", els.text())
    }

    @Test
    fun testSupportsPartiallyNonAsciiTags() {
        val body = "<div>Check</divá>"
        val doc = Jsoup.parse(body)
        val els = doc.select("div")
        Assertions.assertEquals("Check", els.text())
    }

    @Test
    fun testFragment() {
        // make sure when parsing a body fragment, a script tag at start goes into the body
        val html = """
            <script type="text/javascript">console.log('foo');</script>
            <div id="somecontent">some content</div>
            <script type="text/javascript">console.log('bar');</script>
            """.trimIndent()
        val body = parseBodyFragment(html)
        Assertions.assertEquals(
            """<script type="text/javascript">console.log('foo');</script>
<div id="somecontent">
 some content
</div>
<script type="text/javascript">console.log('bar');</script>""", body.body().html()
        )
    }

    @Test
    fun testHtmlLowerCase() {
        val html = "<!doctype HTML><DIV ID=1>One</DIV>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            "<!doctype html> <html> <head></head> <body> <div id=\"1\"> One </div> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
        val div = doc.selectFirst("#1")
        div!!.after("<TaG>One</TaG>")
        Assertions.assertEquals(
            "<tag>One</tag>", TextUtil.stripNewlines(
                div.nextElementSibling()!!.outerHtml()
            )
        )
    }

    @Test
    fun testHtmlLowerCaseAttributesOfVoidTags() {
        val html = "<!doctype HTML><IMG ALT=One></DIV>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            "<!doctype html> <html> <head></head> <body> <img alt=\"One\"> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
    }

    @Test
    fun testHtmlLowerCaseAttributesForm() {
        val html = "<form NAME=one>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("<form name=\"one\"></form>", StringUtil.normaliseWhitespace(doc.body().html()))
    }

    @Test
    fun canPreserveTagCase() {
        val parser = htmlParser()
        parser.settings(ParseSettings(true, false))
        val doc = parser.parseInput("<div id=1><SPAN ID=2>", "")
        Assertions.assertEquals(
            "<html> <head></head> <body> <div id=\"1\"> <SPAN id=\"2\"></SPAN> </div> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
        val div = doc.selectFirst("#1")
        div!!.after("<TaG ID=one>One</TaG>")
        Assertions.assertEquals(
            "<TaG id=\"one\">One</TaG>", TextUtil.stripNewlines(
                div.nextElementSibling()!!.outerHtml()
            )
        )
    }

    @Test
    fun canPreserveAttributeCase() {
        val parser = htmlParser()
        parser.settings(ParseSettings(false, true))
        val doc = parser.parseInput("<div id=1><SPAN ID=2>", "")
        Assertions.assertEquals(
            "<html> <head></head> <body> <div id=\"1\"> <span ID=\"2\"></span> </div> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
        val div = doc.selectFirst("#1")
        div!!.after("<TaG ID=one>One</TaG>")
        Assertions.assertEquals(
            "<tag ID=\"one\">One</tag>", TextUtil.stripNewlines(
                div.nextElementSibling()!!.outerHtml()
            )
        )
    }

    @Test
    fun canPreserveBothCase() {
        val parser = htmlParser()
        parser.settings(ParseSettings(true, true))
        val doc = parser.parseInput("<div id=1><SPAN ID=2>", "")
        Assertions.assertEquals(
            "<html> <head></head> <body> <div id=\"1\"> <SPAN ID=\"2\"></SPAN> </div> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
        val div = doc.selectFirst("#1")
        div!!.after("<TaG ID=one>One</TaG>")
        Assertions.assertEquals(
            "<TaG ID=\"one\">One</TaG>", TextUtil.stripNewlines(
                div.nextElementSibling()!!.outerHtml()
            )
        )
    }

    @Test
    fun handlesControlCodeInAttributeName() {
        val doc = Jsoup.parse("<p><a \u0006=foo>One</a><a/\u0006=bar><a foo\u0006=bar>Two</a></p>")
        Assertions.assertEquals("<p><a>One</a><a></a><a foo=\"bar\">Two</a></p>", doc.body().html())
    }

    @Test
    fun caseSensitiveParseTree() {
        val html = "<r><X>A</X><y>B</y></r>"
        val parser = htmlParser()
        parser.settings(ParseSettings.preserveCase)
        val doc = parser.parseInput(html, "")
        Assertions.assertEquals("<r> <X> A </X> <y> B </y> </r>", StringUtil.normaliseWhitespace(doc.body().html()))
    }

    @Test
    fun caseInsensitiveParseTree() {
        val html = "<r><X>A</X><y>B</y></r>"
        val parser = htmlParser()
        val doc = parser.parseInput(html, "")
        Assertions.assertEquals("<r> <x> A </x> <y> B </y> </r>", StringUtil.normaliseWhitespace(doc.body().html()))
    }

    @Test
    fun preservedCaseLinksCantNest() {
        val html = "<A>ONE <A>Two</A></A>"
        val doc = htmlParser()
            .settings(ParseSettings.preserveCase)
            .parseInput(html, "")
        //assertEquals("<A>ONE </A><A>Two</A>", StringUtil.normaliseWhitespace(doc.body().html()));
        Assertions.assertEquals("<A>ONE </A><A>Two</A>", doc.body().html())
    }

    @Test
    fun normalizesDiscordantTags() {
        val document = Jsoup.parse("<div>test</DIV><p></p>")
        Assertions.assertEquals("<div>\n test\n</div>\n<p></p>", document.body().html())
    }

    @Test
    fun selfClosingVoidIsNotAnError() {
        val html = "<p>test<br/>test<br/></p>"
        val parser = htmlParser().setTrackErrors(5)
        parser.parseInput(html, "")
        Assertions.assertEquals(0, parser.errors.size)
        Assertions.assertTrue(isValid(html, basic()))
        val clean = clean(html, basic())
        Assertions.assertEquals("<p>test<br>\n test<br></p>", clean)
    }

    @Test
    fun selfClosingOnNonvoidIsError() {
        val html = "<p>test</p>\n\n<div /><div>Two</div>"
        val parser = htmlParser().setTrackErrors(5)
        parser.parseInput(html, "")
        Assertions.assertEquals(1, parser.errors.size)
        Assertions.assertEquals("<3:8>: Tag [div] cannot be self closing; not a void tag", parser.errors[0].toString())
        Assertions.assertFalse(isValid(html, relaxed()))
        val clean = clean(html, relaxed())
        Assertions.assertEquals("<p>test</p> <div></div> <div> Two </div>", StringUtil.normaliseWhitespace(clean))
    }

    @Test
    @Throws(IOException::class)
    fun testTemplateInsideTable() {
        val `in`: File = ParseTest.Companion.getFile("/htmltests/table-polymer-template.html")
        val doc = parse(`in`, "UTF-8")
        doc.outputSettings().prettyPrint(true)
        val templates = doc.body().getElementsByTag("template")
        for (template in templates) {
            Assertions.assertTrue(template.childNodes().size > 1)
        }
    }

    @Test
    fun testHandlesDeepSpans() {
        val sb = StringBuilder()
        for (i in 0..199) {
            sb.append("<span>")
        }
        sb.append("<p>One</p>")
        val doc = Jsoup.parse(sb.toString())
        Assertions.assertEquals(200, doc.select("span").size)
        Assertions.assertEquals(1, doc.select("p").size)
    }

    @Test
    fun commentAtEnd() {
        val doc = Jsoup.parse("<!")
        Assertions.assertTrue(doc.childNode(0) is Comment)
    }

    @Test
    fun preSkipsFirstNewline() {
        val doc = Jsoup.parse("<pre>\n\nOne\nTwo\n</pre>")
        val pre = doc.selectFirst("pre")
        Assertions.assertEquals("One\nTwo", pre!!.text())
        Assertions.assertEquals("\nOne\nTwo\n", pre.wholeText())
    }

    @Test
    @Throws(IOException::class)
    fun handlesXmlDeclAndCommentsBeforeDoctype() {
        val `in`: File = ParseTest.Companion.getFile("/htmltests/comments.html")
        val doc = parse(`in`, "UTF-8")
        Assertions.assertEquals(
            "<!--?xml version=\"1.0\" encoding=\"utf-8\"?--><!-- so --> <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><!-- what --> <html xml:lang=\"en\" lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"> <!-- now --> <head> <!-- then --> <meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\"> <title>A Certain Kind of Test</title> </head> <body> <h1>Hello</h1>h1&gt; (There is a UTF8 hidden BOM at the top of this file.) </body> </html>",
            StringUtil.normaliseWhitespace(doc.html())
        )
        Assertions.assertEquals("A Certain Kind of Test", doc.head().select("title").text())
    }

    @Test
    @Throws(IOException::class)
    fun fallbackToUtfIfCantEncode() {
        // that charset can't be encoded, so make sure we flip to utf
        val `in` = "<html><meta charset=\"ISO-2022-CN\"/>One</html>"
        val doc = parse(ByteArrayInputStream(`in`.toByteArray()), null, "")
        Assertions.assertEquals("UTF-8", doc.charset()!!.name())
        Assertions.assertEquals("One", doc.text())
        val html = doc.outerHtml()
        Assertions.assertEquals(
            "<html><head><meta charset=\"UTF-8\"></head><body>One</body></html>",
            TextUtil.stripNewlines(html)
        )
    }

    @Test
    @Throws(IOException::class)
    fun characterReaderBuffer() {
        val `in`: File = ParseTest.Companion.getFile("/htmltests/character-reader-buffer.html.gz")
        val doc = parse(`in`, "UTF-8")
        val expectedHref = "http://www.domain.com/path?param_one=value&param_two=value"
        val links = doc.select("a")
        Assertions.assertEquals(2, links.size)
        Assertions.assertEquals(expectedHref, links[0].attr("href")) // passes
        Assertions.assertEquals(
            expectedHref,
            links[1].attr("href")
        ) // fails, "but was:<...ath?param_one=value&[]_two-value>"
    }

    @Test
    fun selfClosingTextAreaDoesntLeaveDroppings() {
        // https://github.com/jhy/jsoup/issues/1220
        val doc = Jsoup.parse("<div><div><textarea/></div></div>")
        Assertions.assertFalse(doc.body().html().contains("&lt;"))
        Assertions.assertFalse(doc.body().html().contains("&gt;"))
        Assertions.assertEquals(
            "<div><div><textarea></textarea></div></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testNoSpuriousSpace() {
        val doc = Jsoup.parse("Just<a>One</a><a>Two</a>")
        Assertions.assertEquals("Just<a>One</a><a>Two</a>", doc.body().html())
        Assertions.assertEquals("JustOneTwo", doc.body().text())
    }

    @Test
    fun pTagsGetIndented() {
        val html = "<div><p><a href=one>One</a><p><a href=two>Two</a></p></div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            """<div>
 <p><a href="one">One</a></p>
 <p><a href="two">Two</a></p>
</div>""", doc.body().html()
        )
    }

    @Test
    fun indentRegardlessOfCase() {
        val html = "<p>1</p><P>2</P>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            """<body>
 <p>1</p>
 <p>2</p>
</body>""", doc.body().outerHtml()
        )
        val caseDoc = parse(html, "", htmlParser().settings(ParseSettings.preserveCase))
        Assertions.assertEquals(
            """<body>
 <p>1</p>
 <P>2</P>
</body>""", caseDoc.body().outerHtml()
        )
    }

    @Test
    fun testH20() {
        // https://github.com/jhy/jsoup/issues/731
        val html = "H<sub>2</sub>O"
        val clean = clean(html, basic())
        Assertions.assertEquals("H<sub>2</sub>O", clean)
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("H2O", doc.text())
    }

    @Test
    fun testUNewlines() {
        // https://github.com/jhy/jsoup/issues/851
        val html = "t<u>es</u>t <b>on</b> <i>f</i><u>ir</u>e"
        val clean = clean(html, basic())
        Assertions.assertEquals("t<u>es</u>t <b>on</b> <i>f</i><u>ir</u>e", clean)
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("test on fire", doc.text())
    }

    @Test
    fun testFarsi() {
        // https://github.com/jhy/jsoup/issues/1227
        val text = "نیمه\u200Cشب"
        val doc = Jsoup.parse("<p>$text")
        Assertions.assertEquals(text, doc.text())
    }

    @Test
    fun testStartOptGroup() {
        // https://github.com/jhy/jsoup/issues/1313
        val html = """<select>
  <optgroup label="a">
  <option>one
  <option>two
  <option>three
  <optgroup label="b">
  <option>four
  <option>fix
  <option>six
</select>"""
        val doc = Jsoup.parse(html)
        val select = doc.selectFirst("select")
        Assertions.assertEquals(2, select!!.childrenSize())
        Assertions.assertEquals(
            "<optgroup label=\"a\"> <option>one </option><option>two </option><option>three </option></optgroup><optgroup label=\"b\"> <option>four </option><option>fix </option><option>six </option></optgroup>",
            select.html()
        )
    }

    @Test
    fun readerClosedAfterParse() {
        val doc = Jsoup.parse("Hello")
        val treeBuilder = doc.parser().treeBuilder
        Assertions.assertNull(treeBuilder.reader)
        //        assertNull(treeBuilder.tokeniser); // TODO
    }

    @Test
    fun scriptInDataNode() {
        var doc = Jsoup.parse("<script>Hello</script><style>There</style>")
        Assertions.assertTrue(doc.selectFirst("script")!!.childNode(0) is DataNode)
        Assertions.assertTrue(doc.selectFirst("style")!!.childNode(0) is DataNode)
        doc = parse("<SCRIPT>Hello</SCRIPT><STYLE>There</STYLE>", "", htmlParser().settings(ParseSettings.preserveCase))
        Assertions.assertTrue(doc.selectFirst("script")!!.childNode(0) is DataNode)
        Assertions.assertTrue(doc.selectFirst("style")!!.childNode(0) is DataNode)
    }

    @Test
    fun textareaValue() {
        val html = "<TEXTAREA>YES YES</TEXTAREA>"
        var doc = Jsoup.parse(html)
        Assertions.assertEquals("YES YES", doc.selectFirst("textarea")!!.value())
        doc = parse(html, "", htmlParser().settings(ParseSettings.preserveCase))
        Assertions.assertEquals("YES YES", doc.selectFirst("textarea")!!.value())
    }

    @Test
    fun preserveWhitespaceInHead() {
        val html =
            "\n<!doctype html>\n<html>\n<head>\n<title>Hello</title>\n</head>\n<body>\n<p>One</p>\n</body>\n</html>\n"
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(false)
        Assertions.assertEquals(
            "<!doctype html>\n<html>\n<head>\n<title>Hello</title>\n</head>\n<body>\n<p>One</p>\n</body>\n</html>\n",
            doc.outerHtml()
        )
    }

    @Test
    fun handleContentAfterBody() {
        val html = "<body>One</body>  <p>Hello!</p></html> <p>There</p>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(false)
        Assertions.assertEquals(
            "<html><head></head><body>One<p>Hello!</p><p>There</p></body>  </html> ",
            doc.outerHtml()
        )
    }

    @Test
    fun preservesTabs() {
        // testcase to demonstrate tab retention - https://github.com/jhy/jsoup/issues/1240
        val html = "<pre>One\tTwo</pre><span>\tThree\tFour</span>"
        val doc = Jsoup.parse(html)
        val pre = doc.selectFirst("pre")
        val span = doc.selectFirst("span")
        Assertions.assertEquals("One\tTwo", pre!!.text())
        Assertions.assertEquals("Three Four", span!!.text()) // normalized, including overall trim
        Assertions.assertEquals(
            "\tThree\tFour",
            span.wholeText()
        ) // text normalizes, wholeText retains original spaces incl tabs
        Assertions.assertEquals("One\tTwo Three Four", doc.body().text())
        Assertions.assertEquals(
            "<pre>One\tTwo</pre><span> Three Four</span>",
            doc.body().html()
        ) // html output provides normalized space, incl tab in pre but not in span
        doc.outputSettings().prettyPrint(false)
        Assertions.assertEquals(
            html,
            doc.body().html()
        ) // disabling pretty-printing - round-trips the tab throughout, as no normalization occurs
    }

    @Test
    fun wholeTextTreatsBRasNewline() {
        val html = "<div>\nOne<br>Two <p>Three<br>Four</div>"
        val doc = Jsoup.parse(html)
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        Assertions.assertEquals("\nOne\nTwo Three\nFour", div!!.wholeText())
        Assertions.assertEquals("\nOne\nTwo ", div.wholeOwnText())
    }

    @Test
    fun canDetectAutomaticallyAddedElements() {
        val bare = "<script>One</script>"
        val full = "<html><head><title>Check</title></head><body><p>One</p></body></html>"
        Assertions.assertTrue(didAddElements(bare))
        Assertions.assertFalse(didAddElements(full))
    }

    private fun didAddElements(input: String): Boolean {
        // two passes, one as XML and one as HTML. XML does not vivify missing/optional tags
        val html = Jsoup.parse(input)
        val xml = parse(input, "", xmlParser())
        val htmlElementCount = html.allElements.size
        val xmlElementCount = xml.allElements.size
        return htmlElementCount > xmlElementCount
    }

    @Test
    fun canSetHtmlOnCreatedTableElements() {
        // https://github.com/jhy/jsoup/issues/1603
        val element = Element("tr")
        element.html("<tr><td>One</td></tr>")
        Assertions.assertEquals("<tr>\n <tr>\n  <td>One</td>\n </tr>\n</tr>", element.outerHtml())
    }

    @Test
    fun parseFragmentOnCreatedDocument() {
        // https://github.com/jhy/jsoup/issues/1601
        val bareFragment = "<h2>text</h2>"
        val nodes = Document("").parser().parseFragmentInput(bareFragment, Element("p"), "")
        Assertions.assertEquals(1, nodes.size)
        val node = nodes[0]
        Assertions.assertEquals("h2", node.nodeName())
        Assertions.assertEquals("<p>\n <h2>text</h2></p>", node.parent()!!.outerHtml())
    }

    @Test
    fun nestedPFragments() {
        // https://github.com/jhy/jsoup/issues/1602
        val bareFragment = "<p></p><a></a>"
        val nodes = Document("").parser().parseFragmentInput(bareFragment, Element("p"), "")
        Assertions.assertEquals(2, nodes.size)
        val node = nodes[0]
        Assertions.assertEquals(
            "<p>\n <p></p><a></a></p>",
            node.parent()!!.outerHtml()
        ) // mis-nested because fragment forced into the element, OK
    }

    @Test
    fun nestedAnchorAdoption() {
        // https://github.com/jhy/jsoup/issues/1608
        val html = "<a>\n<b>\n<div>\n<a>test</a>\n</div>\n</b>\n</a>"
        val doc = Jsoup.parse(html)
        Assertions.assertNotNull(doc)
        Assertions.assertEquals(
            "<a> <b> </b></a><b><div><a> </a><a>test</a></div></b>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun tagsMustStartWithAscii() {
        // https://github.com/jhy/jsoup/issues/1006
        val valid = arrayOf("a一", "a会员挂单金额5", "table(╯°□°)╯")
        val invalid = arrayOf("一", "会员挂单金额5", "(╯°□°)╯")
        for (tag in valid) {
            val doc = Jsoup.parse("<$tag>Text</$tag>")
            val els = doc.getElementsByTag(tag)
            Assertions.assertEquals(1, els.size)
            Assertions.assertEquals(tag, els[0].tagName())
            Assertions.assertEquals("Text", els[0].text())
        }
        for (tag in invalid) {
            val doc = Jsoup.parse("<$tag>Text</$tag>")
            val els = doc.getElementsByTag(tag)
            Assertions.assertEquals(0, els.size)
            Assertions.assertEquals("&lt;$tag&gt;Text<!--/$tag-->", doc.body().html())
        }
    }

    @Test
    fun htmlOutputCorrectsInvalidAttributeNames() {
        val html = "<body style=\"color: red\" \" name\"><div =\"\"></div></body>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(Document.OutputSettings.Syntax.html, doc.outputSettings().syntax())
        val out = doc.body().outerHtml()
        Assertions.assertEquals("<body style=\"color: red\" name>\n <div></div>\n</body>", out)
    }

    @Test
    fun templateInHead() {
        // https://try.jsoup.org/~EGp3UZxQe503TJDHQEQEzm8IeUc
        val html =
            "<head><template id=1><meta name=tmpl></template><title>Test</title><style>One</style></head><body><p>Two</p>"
        val doc = Jsoup.parse(html)
        var want =
            "<html><head><template id=\"1\"><meta name=\"tmpl\"></template><title>Test</title><style>One</style></head><body><p>Two</p></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))
        val template = doc.select("template#1")
        template.select("meta").attr("content", "Yes")
        template.unwrap()
        want =
            "<html><head><meta name=\"tmpl\" content=\"Yes\"><title>Test</title><style>One</style></head><body><p>Two</p></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun nestedTemplateInBody() {
        val html =
            "<body><template id=1><table><tr><template id=2><td>One</td><td>Two</td></template></tr></template></body>"
        val doc = Jsoup.parse(html)
        var want =
            "<html><head></head><body><template id=\"1\"><table><tbody><tr><template id=\"2\"><td>One</td><td>Two</td></template></tr></tbody></table></template></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))

        // todo - will be nice to add some simpler template element handling like clone children etc?
        val tmplTbl = doc.selectFirst("template#1")
        val tmplRow = doc.selectFirst("template#2")
        Assertions.assertNotNull(tmplRow)
        Assertions.assertNotNull(tmplTbl)
        tmplRow!!.appendChild(tmplRow.clone())
        doc.select("template").unwrap()
        want =
            "<html><head></head><body><table><tbody><tr><td>One</td><td>Two</td><td>One</td><td>Two</td></tr></tbody></table></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun canSelectIntoTemplate() {
        val html = "<body><div><template><p>Hello</p>"
        val doc = Jsoup.parse(html)
        val want = "<html><head></head><body><div><template><p>Hello</p></template></div></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))
        val p = doc.selectFirst("div p")
        val p1 = doc.selectFirst("template :containsOwn(Hello)")
        Assertions.assertEquals("p", p!!.normalName())
        Assertions.assertEquals(p, p1)
    }

    @Test
    fun tableRowFragment() {
        val doc = Jsoup.parse("<body><table></table></body")
        val html = "<tr><td><img></td></tr>"
        val table = doc.selectFirst("table")
        table!!.html(html) // invokes the fragment parser with table as context
        var want = "<tbody><tr><td><img></td></tr></tbody>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(table.html()))
        want = "<table><tbody><tr><td><img></td></tr></tbody></table>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.body().html()))
    }

    @Test
    fun templateTableRowFragment() {
        // https://github.com/jhy/jsoup/issues/1409 (per the fragment <tr> use case)
        val doc = Jsoup.parse("<body><table><template></template></table></body")
        val html = "<tr><td><img></td></tr>"
        val tmpl = doc.selectFirst("template")
        tmpl!!.html(html) // invokes the fragment parser with template as context
        var want = "<tr><td><img></td></tr>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(tmpl.html()))
        tmpl.unwrap()
        want = "<html><head></head><body><table><tr><td><img></td></tr></table></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun templateNotInTableRowFragment() {
        // https://github.com/jhy/jsoup/issues/1409 (per the fragment <tr> use case)
        val doc = Jsoup.parse("<body><template></template></body")
        val html = "<tr><td><img></td></tr>"
        val tmpl = doc.selectFirst("template")
        tmpl!!.html(html) // invokes the fragment parser with template as context
        var want = "<tr><td><img></td></tr>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(tmpl.html()))
        tmpl.unwrap()
        want = "<html><head></head><body><tr><td><img></td></tr></body></html>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun templateFragment() {
        // https://github.com/jhy/jsoup/issues/1315
        val html = "<template id=\"lorem-ipsum\"><tr><td>Lorem</td><td>Ipsum</td></tr></template>"
        val frag = parseBodyFragment(html)
        val want = "<template id=\"lorem-ipsum\"><tr><td>Lorem</td><td>Ipsum</td></tr></template>"
        Assertions.assertEquals(want, TextUtil.stripNewlines(frag.body().html()))
    }

    @Test
    fun templateInferredForm() {
        // https://github.com/jhy/jsoup/issues/1637 | https://bugs.chromium.org/p/oss-fuzz/issues/detail?id=38987
        val doc = Jsoup.parse("<template><isindex action>")
        Assertions.assertNotNull(doc)
        Assertions.assertEquals(
            "<template><form><hr><label>This is a searchable index. Enter search keywords: <input name=\"isindex\"></label><hr></form></template>",
            TextUtil.stripNewlines(doc.head().html())
        )
    }

    @Test
    fun trimNormalizeElementNamesInBuilder() {
        // https://github.com/jhy/jsoup/issues/1637 | https://bugs.chromium.org/p/oss-fuzz/issues/detail?id=38983
        // This is interesting - in TB state, the element name was "template\u001E", so no name checks matched. Then,
        // when the Element is created, the name got normalized to "template" and so looked like there should be a
        // template on the stack during resetInsertionMode for the select.
        // The issue was that the normalization in Tag.valueOf did a trim which the Token.Tag did not
        val doc = Jsoup.parse("<template\u001E<select<input<")
        Assertions.assertNotNull(doc)
        Assertions.assertEquals(
            "<template><select></select><input>&lt;</template>",
            TextUtil.stripNewlines(doc.head().html())
        )
    }

    @Test
    fun errorsBeforeHtml() {
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse("<!doctype html><!doctype something></div>", parser)
        val errors = parser.errors
        Assertions.assertEquals(2, errors.size)
        Assertions.assertEquals(
            "<1:36>: Unexpected Doctype token [<!doctype something>] when in state [BeforeHtml]",
            errors[0].toString()
        )
        Assertions.assertEquals(
            "<1:42>: Unexpected EndTag token [</div>] when in state [BeforeHtml]",
            errors[1].toString()
        )
        Assertions.assertEquals(
            "<!doctype html><html><head></head><body></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun afterHeadReAdds() {
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse("<head></head><meta charset=UTF8><p>Hello", parser)
        val errors = parser.errors
        Assertions.assertEquals(1, errors.size)
        Assertions.assertEquals(
            "<1:33>: Unexpected StartTag token [<meta  charset=\"UTF8\">] when in state [AfterHead]",
            errors[0].toString()
        )
        Assertions.assertEquals(
            "<html><head><meta charset=\"UTF8\"></head><body><p>Hello</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
        // meta gets added back into head
    }

    @Test
    fun mergeHtmlAttributesFromBody() {
        val doc = Jsoup.parse("<html id=1 class=foo><body><html class=bar data=x><p>One")
        Assertions.assertEquals(
            "<html id=\"1\" class=\"foo\" data=\"x\"><head></head><body><p>One</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun mergeHtmlNoAttributesFromBody() {
        val doc = Jsoup.parse("<html id=1 class=foo><body><html><p>One")
        Assertions.assertEquals(
            "<html id=\"1\" class=\"foo\"><head></head><body><p>One</p></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun supportsRuby() {
        val html =
            "<ruby><rbc><rb>10</rb><rb>31</rb><rb>2002</rb></rbc><rtc><rt>Month</rt><rt>Day</rt><rt>Year</rt></rtc><rtc><rt>Expiration Date</rt><rp>(*)</rtc></ruby>"
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse(html, parser)
        val errors = parser.errors
        Assertions.assertEquals(3, errors.size)
        val ruby = doc.expectFirst("ruby")
        Assertions.assertEquals(
            "<ruby><rbc><rb>10</rb><rb>31</rb><rb>2002</rb></rbc><rtc><rt>Month</rt><rt>Day</rt><rt>Year</rt></rtc><rtc><rt>Expiration Date</rt><rp>(*)</rp></rtc></ruby>",
            TextUtil.stripNewlines(ruby.outerHtml())
        )
        Assertions.assertEquals(
            "<1:38>: Unexpected StartTag token [<rb>] when in state [InBody]",
            errors[2].toString()
        ) // 3 errors from rb in rtc as undefined
    }

    @Test
    fun rubyRpRtImplicitClose() {
        val html = "<ruby><rp>(<rt>Hello<rt>Hello<rp>)</ruby>\n"
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse(html, parser)
        Assertions.assertEquals(0, parser.errors.size)
        val ruby = doc.expectFirst("ruby")
        Assertions.assertEquals(
            "<ruby><rp>(</rp><rt>Hello</rt><rt>Hello</rt><rp>)</rp></ruby>",
            TextUtil.stripNewlines(ruby.outerHtml())
        )
    }

    @Test
    fun rubyScopeError() {
        val html = "<ruby><div><rp>Hello"
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse(html, parser)
        val errors = parser.errors
        Assertions.assertEquals(2, errors.size)
        val ruby = doc.expectFirst("ruby")
        Assertions.assertEquals(
            "<ruby><div><rp>Hello</rp></div></ruby>",
            TextUtil.stripNewlines(ruby.outerHtml())
        )
        Assertions.assertEquals("<1:16>: Unexpected StartTag token [<rp>] when in state [InBody]", errors[0].toString())
    }

    @Test
    fun errorOnEofIfOpen() {
        val html = "<div>"
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse(html, parser)
        val errors = parser.errors
        Assertions.assertEquals(1, errors.size)
        Assertions.assertEquals("Unexpected EOF token [] when in state [InBody]", errors[0].errorMessage)
    }

    @Test
    fun NoErrorOnEofIfBodyOpen() {
        val html = "<body>"
        val parser = htmlParser()
        parser.setTrackErrors(10)
        val doc = parse(html, parser)
        val errors = parser.errors
        Assertions.assertEquals(0, errors.size)
    }

    @Test
    fun htmlClose() {
        // https://github.com/jhy/jsoup/issues/1851
        val html = "<body><div>One</html>Two</div></body>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("OneTwo", doc.expectFirst("body > div").text())
    }

    @Test
    fun largeTextareaContents() {
        // https://github.com/jhy/jsoup/issues/1929
        val sb = StringBuilder()
        val num = 2000
        for (i in 0..num) {
            sb.append("\n<text>foo</text>\n")
        }
        val textContent = sb.toString()
        val sourceHtml = "<textarea>$textContent</textarea>"
        val doc = Jsoup.parse(sourceHtml)
        val textArea = doc.expectFirst("textarea")
        Assertions.assertEquals(textContent, textArea.wholeText())
    }

    companion object {
        @JvmStatic
        private fun dupeAttributeData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "<p One=One ONE=Two Two=two one=Three One=Four two=Five>Text</p>",
                    "<p one=\"One\" two=\"two\">Text</p>"
                ),
                Arguments.of(
                    "<img One=One ONE=Two Two=two one=Three One=Four two=Five>",
                    "<img one=\"One\" two=\"two\">"
                ),
                Arguments.of(
                    "<form One=One ONE=Two Two=two one=Three One=Four two=Five></form>",
                    "<form one=\"One\" two=\"two\"></form>"
                )
            )
        }
    }
}
