package org.jsoup.parser

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Test suite for attribute parser.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class AttributeParseTest {
    @Test
    fun parsesRoughAttributeString() {
        val html = "<a id=\"123\" class=\"baz = 'bar'\" style = 'border: 2px'qux zim foo = 12 mux=18 />"
        // should be: <id=123>, <class=baz = 'bar'>, <qux=>, <zim=>, <foo=12>, <mux.=18>
        val el = Jsoup.parse(html).getElementsByTag("a")[0]
        val attr = el.attributes()
        Assertions.assertEquals(7, attr.size())
        Assertions.assertEquals("123", attr["id"])
        Assertions.assertEquals("baz = 'bar'", attr["class"])
        Assertions.assertEquals("border: 2px", attr["style"])
        Assertions.assertEquals("", attr["qux"])
        Assertions.assertEquals("", attr["zim"])
        Assertions.assertEquals("12", attr["foo"])
        Assertions.assertEquals("18", attr["mux"])
    }

    @Test
    fun handlesNewLinesAndReturns() {
        val html = "<a\r\nfoo='bar\r\nqux'\r\nbar\r\n=\r\ntwo>One</a>"
        val el = Jsoup.parse(html).select("a").first()
        Assertions.assertEquals(2, el!!.attributes().size())
        Assertions.assertEquals(
            "bar\r\nqux",
            el.attr("foo")
        ) // currently preserves newlines in quoted attributes. todo confirm if should.
        Assertions.assertEquals("two", el.attr("bar"))
    }

    @Test
    fun parsesEmptyString() {
        val html = "<a />"
        val el = Jsoup.parse(html).getElementsByTag("a")[0]
        val attr = el.attributes()
        Assertions.assertEquals(0, attr.size())
    }

    @Test
    fun canStartWithEq() {
        val html = "<a =empty />"
        // TODO this is the weirdest thing in the spec - why not consider this an attribute with an empty name, not where name is '='?
        // am I reading it wrong? https://html.spec.whatwg.org/multipage/parsing.html#before-attribute-name-state
        val el = Jsoup.parse(html).getElementsByTag("a")[0]
        val attr = el.attributes()
        Assertions.assertEquals(1, attr.size())
        Assertions.assertTrue(attr.hasKey("=empty"))
        Assertions.assertEquals("", attr["=empty"])
    }

    @Test
    fun strictAttributeUnescapes() {
        val html = "<a id=1 href='?foo=bar&mid&lt=true'>One</a> <a id=2 href='?foo=bar&lt;qux&lg=1'>Two</a>"
        val els = Jsoup.parse(html).select("a")
        Assertions.assertEquals("?foo=bar&mid&lt=true", els.first()!!.attr("href"))
        Assertions.assertEquals("?foo=bar<qux&lg=1", els.last()!!.attr("href"))
    }

    @Test
    fun moreAttributeUnescapes() {
        val html = "<a href='&wr_id=123&mid-size=true&ok=&wr'>Check</a>"
        val els = Jsoup.parse(html).select("a")
        Assertions.assertEquals("&wr_id=123&mid-size=true&ok=&wr", els.first()!!.attr("href"))
    }

    @Test
    fun parsesBooleanAttributes() {
        val html = "<a normal=\"123\" boolean empty=\"\"></a>"
        val el = Jsoup.parse(html).select("a").first()
        Assertions.assertEquals("123", el!!.attr("normal"))
        Assertions.assertEquals("", el.attr("boolean"))
        Assertions.assertEquals("", el.attr("empty"))
        val attributes = el.attributes().asList()
        Assertions.assertEquals(3, attributes.size, "There should be 3 attribute present")
        Assertions.assertEquals(html, el.outerHtml()) // vets boolean syntax
    }

    @Test
    fun dropsSlashFromAttributeName() {
        val html = "<img /onerror='doMyJob'/>"
        var doc = Jsoup.parse(html)
        Assertions.assertFalse(doc.select("img[onerror]").isEmpty(), "SelfClosingStartTag ignores last character")
        Assertions.assertEquals("<img onerror=\"doMyJob\">", doc.body().html())
        doc = parse(html, "", xmlParser())
        Assertions.assertEquals("<img onerror=\"doMyJob\" />", doc.html())
    }
}
