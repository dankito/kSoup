package org.jsoup.nodes

import org.jsoup.Jsoupimport

org.jsoup.Jsoup.parseimport org.jsoup.TextUtilimport org.jsoup.parser.Tag.Companion.valueOfimport org.jsoup.select.NodeVisitorimport org.junit.jupiter.api.Assertionsimport org.junit.jupiter.api.Testimport java.util.function.Consumer
/**
 * Tests Nodes
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class NodeTest {
    @Test
    fun handlesBaseUri() {
        val tag = valueOf("a")
        val attribs = Attributes()
        attribs.put("relHref", "/foo")
        attribs.put("absHref", "http://bar/qux")
        val noBase = Element(tag, "", attribs)
        Assertions.assertEquals(
            "",
            noBase.absUrl("relHref")
        ) // with no base, should NOT fallback to href attrib, whatever it is
        Assertions.assertEquals("http://bar/qux", noBase.absUrl("absHref")) // no base but valid attrib, return attrib
        val withBase = Element(tag, "http://foo/", attribs)
        Assertions.assertEquals("http://foo/foo", withBase.absUrl("relHref")) // construct abs from base + rel
        Assertions.assertEquals("http://bar/qux", withBase.absUrl("absHref")) // href is abs, so returns that
        Assertions.assertEquals("", withBase.absUrl("noval"))
        val dodgyBase = Element(tag, "wtf://no-such-protocol/", attribs)
        Assertions.assertEquals("http://bar/qux", dodgyBase.absUrl("absHref")) // base fails, but href good, so get that
        Assertions.assertEquals("", dodgyBase.absUrl("relHref")) // base fails, only rel href, so return nothing
    }

    @Test
    fun setBaseUriIsRecursive() {
        val doc = Jsoup.parse("<div><p></p></div>")
        val baseUri = "https://jsoup.org"
        doc.setBaseUri(baseUri)
        Assertions.assertEquals(baseUri, doc.baseUri())
        Assertions.assertEquals(baseUri, doc.select("div").first()!!.baseUri())
        Assertions.assertEquals(baseUri, doc.select("p").first()!!.baseUri())
    }

    @Test
    fun handlesAbsPrefix() {
        val doc = parse("<a href=/foo>Hello</a>", "https://jsoup.org/")
        val a = doc.select("a").first()
        Assertions.assertEquals("/foo", a!!.attr("href"))
        Assertions.assertEquals("https://jsoup.org/foo", a.attr("abs:href"))
        Assertions.assertTrue(a.hasAttr("abs:href"))
    }

    @Test
    fun handlesAbsOnImage() {
        val doc = parse("<p><img src=\"/rez/osi_logo.png\" /></p>", "https://jsoup.org/")
        val img = doc.select("img").first()
        Assertions.assertEquals("https://jsoup.org/rez/osi_logo.png", img!!.attr("abs:src"))
        Assertions.assertEquals(img.absUrl("src"), img.attr("abs:src"))
    }

    @Test
    fun handlesAbsPrefixOnHasAttr() {
        // 1: no abs url; 2: has abs url
        val doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>")
        val one = doc.select("#1").first()
        val two = doc.select("#2").first()
        Assertions.assertFalse(one!!.hasAttr("abs:href"))
        Assertions.assertTrue(one.hasAttr("href"))
        Assertions.assertEquals("", one.absUrl("href"))
        Assertions.assertTrue(two!!.hasAttr("abs:href"))
        Assertions.assertTrue(two.hasAttr("href"))
        Assertions.assertEquals("https://jsoup.org/", two.absUrl("href"))
    }

    @Test
    fun literalAbsPrefix() {
        // if there is a literal attribute "abs:xxx", don't try and make absolute.
        val doc = Jsoup.parse("<a abs:href='odd'>One</a>")
        val el = doc.select("a").first()
        Assertions.assertTrue(el!!.hasAttr("abs:href"))
        Assertions.assertEquals("odd", el.attr("abs:href"))
    }

    @Test
    fun handleAbsOnFileUris() {
        val doc = parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file:/etc/")
        val one = doc.select("a").first()
        Assertions.assertEquals("file:/etc/password", one!!.absUrl("href"))
        val two = doc.select("a")[1]
        Assertions.assertEquals("file:/var/log/messages", two.absUrl("href"))
    }

    @Test
    fun handleAbsOnLocalhostFileUris() {
        val doc = parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file://localhost/etc/")
        val one = doc.select("a").first()
        Assertions.assertEquals("file://localhost/etc/password", one!!.absUrl("href"))
    }

    @Test
    fun handlesAbsOnProtocolessAbsoluteUris() {
        val doc1 = parse("<a href='//example.net/foo'>One</a>", "http://example.com/")
        val doc2 = parse("<a href='//example.net/foo'>One</a>", "https://example.com/")
        val one = doc1.select("a").first()
        val two = doc2.select("a").first()
        Assertions.assertEquals("http://example.net/foo", one!!.absUrl("href"))
        Assertions.assertEquals("https://example.net/foo", two!!.absUrl("href"))
        val doc3 = parse("<img src=//www.google.com/images/errors/logo_sm.gif alt=Google>", "https://google.com")
        Assertions.assertEquals("https://www.google.com/images/errors/logo_sm.gif", doc3.select("img").attr("abs:src"))
    }

    /*
    Test for an issue with Java's abs URL handler.
     */
    @Test
    fun absHandlesRelativeQuery() {
        val doc = parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "https://jsoup.org/path/file?bar")
        val a1 = doc.select("a").first()
        Assertions.assertEquals("https://jsoup.org/path/file?foo", a1!!.absUrl("href"))
        val a2 = doc.select("a")[1]
        Assertions.assertEquals("https://jsoup.org/path/bar.html?foo", a2.absUrl("href"))
    }

    @Test
    fun absHandlesDotFromIndex() {
        val doc = parse("<a href='./one/two.html'>One</a>", "http://example.com")
        val a1 = doc.select("a").first()
        Assertions.assertEquals("http://example.com/one/two.html", a1!!.absUrl("href"))
    }

    @Test
    fun handlesAbsOnUnknownProtocols() {
        // https://github.com/jhy/jsoup/issues/1610
        // URL would throw on unknown protocol tel: as no stream handler is registered
        val urls = arrayOf("mailto:example@example.com", "tel:867-5309") // mail has a handler, tel doesn't
        for (url in urls) {
            val attr = Attributes().put("href", url)
            val noBase = Element(valueOf("a"), null, attr)
            Assertions.assertEquals(url, noBase.absUrl("href"))
            val withBase = Element(valueOf("a"), "http://example.com/", attr)
            Assertions.assertEquals(url, withBase.absUrl("href"))
        }
    }

    @Test
    fun testRemove() {
        val doc = Jsoup.parse("<p>One <span>two</span> three</p>")
        val p = doc.select("p").first()
        p!!.childNode(0).remove()
        Assertions.assertEquals("two three", p.text())
        Assertions.assertEquals("<span>two</span> three", TextUtil.stripNewlines(p.html()))
    }

    @Test
    fun removeOnOrphanIsNoop() {
        // https://github.com/jhy/jsoup/issues/1898
        val node = Element("div")
        Assertions.assertNull(node.parentNode)
        node.remove()
        Assertions.assertNull(node.parentNode)
    }

    @Test
    fun testReplace() {
        val doc = Jsoup.parse("<p>One <span>two</span> three</p>")
        val p = doc.select("p").first()
        val insert = doc.createElement("em").text("foo")
        p!!.childNode(1).replaceWith(insert)
        Assertions.assertEquals("One <em>foo</em> three", p.html())
    }

    @Test
    fun ownerDocument() {
        val doc = Jsoup.parse("<p>Hello")
        val p = doc.select("p").first()
        Assertions.assertSame(p!!.ownerDocument(), doc)
        Assertions.assertSame(doc.ownerDocument(), doc)
        Assertions.assertNull(doc.parent())
    }

    @Test
    fun root() {
        val doc = Jsoup.parse("<div><p>Hello")
        val p = doc.select("p").first()
        val root: Node = p!!.root()
        Assertions.assertSame(doc, root)
        Assertions.assertNull(root.parent())
        Assertions.assertSame(doc.root(), doc)
        Assertions.assertSame(doc.root(), doc.ownerDocument())
        val standAlone = Element(valueOf("p"), "")
        Assertions.assertNull(standAlone.parent())
        Assertions.assertSame(standAlone.root(), standAlone)
        Assertions.assertNull(standAlone.ownerDocument())
    }

    @Test
    fun before() {
        val doc = Jsoup.parse("<p>One <b>two</b> three</p>")
        val newNode = Element(valueOf("em"), "")
        newNode.appendText("four")
        doc.select("b").first()!!.before(newNode)
        Assertions.assertEquals("<p>One <em>four</em><b>two</b> three</p>", doc.body().html())
        doc.select("b").first()!!.before("<i>five</i>")
        Assertions.assertEquals("<p>One <em>four</em><i>five</i><b>two</b> three</p>", doc.body().html())
    }

    @Test
    fun beforeShuffle() {
        // https://github.com/jhy/jsoup/issues/1898
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>")
        val div = doc.select("div")[0]
        val ps = doc.select("p")
        val p1 = ps[0]
        val p2 = ps[1]
        val p3 = ps[2]
        p2.before(p1)
        p3.before(p2)
        // ^ should be no-ops, they are already before
        Assertions.assertEquals("One Two Three", div.text())
        p2.before(p1)
        p1.before(p3)
        Assertions.assertEquals("Three One Two", div.text())
    }

    @Test
    fun after() {
        val doc = Jsoup.parse("<p>One <b>two</b> three</p>")
        val newNode = Element(valueOf("em"), "")
        newNode.appendText("four")
        doc.select("b").first()!!.after(newNode)
        Assertions.assertEquals("<p>One <b>two</b><em>four</em> three</p>", doc.body().html())
        doc.select("b").first()!!.after("<i>five</i>")
        Assertions.assertEquals("<p>One <b>two</b><i>five</i><em>four</em> three</p>", doc.body().html())
    }

    @Test
    fun afterShuffle() {
        // https://github.com/jhy/jsoup/issues/1898
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>")
        val div = doc.select("div")[0]
        val ps = doc.select("p")
        val p1 = ps[0]
        val p2 = ps[1]
        val p3 = ps[2]
        p1.after(p2)
        p2.after(p3)
        // ^ should be no-ops, they are already before
        Assertions.assertEquals("One Two Three", div.text())
        p3.after(p1)
        p1.after(p2)
        Assertions.assertEquals("Three One Two", div.text())
    }

    @Test
    fun unwrap() {
        val doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>")
        val span = doc.select("span").first()
        val twoText = span!!.childNode(0)
        val node = span.unwrap()
        Assertions.assertEquals("<div>One Two <b>Three</b> Four</div>", TextUtil.stripNewlines(doc.body().html()))
        Assertions.assertTrue(node is TextNode)
        Assertions.assertEquals("Two ", (node as TextNode?)!!.text())
        Assertions.assertEquals(node, twoText)
        Assertions.assertEquals(node!!.parent(), doc.select("div").first())
    }

    @Test
    fun unwrapNoChildren() {
        val doc = Jsoup.parse("<div>One <span></span> Two</div>")
        val span = doc.select("span").first()
        val node = span!!.unwrap()
        Assertions.assertEquals("<div>One  Two</div>", TextUtil.stripNewlines(doc.body().html()))
        Assertions.assertNull(node)
    }

    @Test
    fun traverse() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        doc.select("div").first()!!.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                accum.append("<").append(node.nodeName()).append(">")
            }

            override fun tail(node: Node, depth: Int) {
                accum.append("</").append(node.nodeName()).append(">")
            }
        })
        Assertions.assertEquals("<div><p><#text></#text></p></div>", accum.toString())
    }

    @Test
    fun forEachNode() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div><div id=1>Gone<p></div>")
        doc.forEachNode(Consumer { node: Node ->
            if (node is TextNode) {
                val textNode = node
                if (textNode.text() == "There") {
                    textNode.text("There Now")
                    textNode.after("<p>Another")
                }
            } else if (node.attr("id") == "1") node.remove()
        })
        Assertions.assertEquals(
            "<div><p>Hello</p></div><div>There Now<p>Another</p></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun orphanNodeReturnsNullForSiblingElements() {
        val node: Node = Element(valueOf("p"), "")
        val el = Element(valueOf("p"), "")
        Assertions.assertEquals(0, node.siblingIndex)
        Assertions.assertEquals(0, node.siblingNodes().size)
        Assertions.assertNull(node.previousSibling())
        Assertions.assertNull(node.nextSibling())
        Assertions.assertEquals(0, el.siblingElements().size)
        Assertions.assertNull(el.previousElementSibling())
        Assertions.assertNull(el.nextElementSibling())
    }

    @Test
    fun nodeIsNotASiblingOfItself() {
        val doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>")
        val p2 = doc.select("p")[1]
        Assertions.assertEquals("Two", p2.text())
        val nodes = p2.siblingNodes()
        Assertions.assertEquals(2, nodes.size)
        Assertions.assertEquals("<p>One</p>", nodes[0].outerHtml())
        Assertions.assertEquals("<p>Three</p>", nodes[1].outerHtml())
    }

    @Test
    fun childNodesCopy() {
        val doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>")
        val div1 = doc.select("#1").first()
        val div2 = doc.select("#2").first()
        val divChildren = div1!!.childNodesCopy()
        Assertions.assertEquals(5, divChildren.size)
        val tn1 = div1.childNode(0) as TextNode
        val tn2 = divChildren[0] as TextNode
        tn2.text("Text 1 updated")
        Assertions.assertEquals("Text 1 ", tn1.text())
        div2!!.insertChildren(-1, divChildren)
        Assertions.assertEquals(
            "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated"
                    + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>", TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun supportsClone() {
        val doc = Jsoup.parse("<div class=foo>Text</div>")
        val el = doc.select("div").first()
        Assertions.assertTrue(el!!.hasClass("foo"))
        val elClone = doc.clone().select("div").first()
        Assertions.assertTrue(elClone!!.hasClass("foo"))
        Assertions.assertEquals("Text", elClone.text())
        el.removeClass("foo")
        el.text("None")
        Assertions.assertFalse(el.hasClass("foo"))
        Assertions.assertTrue(elClone.hasClass("foo"))
        Assertions.assertEquals("None", el.text())
        Assertions.assertEquals("Text", elClone.text())
    }

    @Test
    fun changingAttributeValueShouldReplaceExistingAttributeCaseInsensitive() {
        val document = Jsoup.parse("<INPUT id=\"foo\" NAME=\"foo\" VALUE=\"\">")
        val inputElement = document.select("#foo").first()
        inputElement!!.attr("value", "bar")
        Assertions.assertEquals(singletonAttributes(), getAttributesCaseInsensitive(inputElement))
    }

    private fun getAttributesCaseInsensitive(element: Element?): Attributes {
        val matches = Attributes()
        for (attribute in element!!.attributes()) {
            if (attribute.key.equals("value", ignoreCase = true)) {
                matches.put(attribute)
            }
        }
        return matches
    }

    private fun singletonAttributes(): Attributes {
        val attributes = Attributes()
        attributes.put("value", "bar")
        return attributes
    }

    @Test
    fun clonedNodesHaveOwnerDocsAndIndependentSettings() {
        // https://github.com/jhy/jsoup/issues/763
        val doc = Jsoup.parse("<div>Text</div><div>Two</div>")
        doc.outputSettings().prettyPrint(false)
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)
        val text = div!!.childNode(0) as TextNode
        Assertions.assertNotNull(text)
        val textClone = text.clone()
        val docClone = textClone.ownerDocument()
        Assertions.assertNotNull(docClone)
        Assertions.assertFalse(docClone!!.outputSettings().prettyPrint())
        Assertions.assertNotSame(doc, docClone)
        doc.outputSettings().prettyPrint(true)
        Assertions.assertTrue(doc.outputSettings().prettyPrint())
        Assertions.assertFalse(docClone.outputSettings().prettyPrint())
        Assertions.assertEquals(
            1,
            docClone.childNodes().size
        ) // check did not get the second div as the owner's children
        Assertions.assertEquals(textClone, docClone.childNode(0)) // note not the head or the body -- not normalized
    }

    @Test
    fun firstAndLastChild() {
        val html = "<div>One <span>Two</span> <a href></a> Three</div>"
        val doc = Jsoup.parse(html)
        val div = doc.selectFirst("div")
        val a = doc.selectFirst("a")
        Assertions.assertNotNull(div)
        Assertions.assertNotNull(a)

        // nodes
        val first = div!!.firstChild() as TextNode?
        Assertions.assertEquals("One ", first!!.text())
        val last = div.lastChild() as TextNode?
        Assertions.assertEquals(" Three", last!!.text())
        Assertions.assertNull(a!!.firstChild())
        Assertions.assertNull(a.lastChild())

        // elements
        val firstEl = div.firstElementChild()
        Assertions.assertEquals("span", firstEl!!.tagName())
        val lastEl = div.lastElementChild()
        Assertions.assertEquals("a", lastEl!!.tagName())
        Assertions.assertNull(a.firstElementChild())
        Assertions.assertNull(a.lastElementChild())
        Assertions.assertNull(firstEl.firstElementChild())
        Assertions.assertNull(firstEl.lastElementChild())
    }

    @Test
    fun nodeName() {
        val div = Element("DIV")
        Assertions.assertEquals("DIV", div.tagName())
        Assertions.assertEquals("DIV", div.nodeName())
        Assertions.assertEquals("div", div.normalName())
        Assertions.assertTrue(div.isNode("div"))
        Assertions.assertTrue(isNode(div, "div"))
        val text = TextNode("Some Text")
        Assertions.assertEquals("#text", text.nodeName())
        Assertions.assertEquals("#text", text.normalName())
    }

    fun isNode(node: Node?, normalName: String): Boolean {
        return node != null && normalName == node.normalName()
    }
}
