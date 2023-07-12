package org.jsoup.select

import org.jsoup.Jsoup
import org.jsoup.TextUtil
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.GenericNodeVisitor.Companion.jvmNodeVisitor
import org.jsoup.select.NodeTraversor.filter
import org.jsoup.select.NodeTraversor.traverse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TraversorTest {
    // Note: NodeTraversor.traverse(new NodeVisitor) is tested in
    // ElementsTest#traverse()
    @Test
    fun filterVisit() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("<").append(node.nodeName()).append(">")
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("</").append(node.nodeName()).append(">")
                return NodeFilter.FilterResult.CONTINUE
            }
        }, doc.select("div"))
        Assertions.assertEquals("<div><p><#text></#text></p></div><div><#text></#text></div>", accum.toString())
    }

    @Test
    fun filterSkipChildren() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("<").append(node.nodeName()).append(">")
                // OMIT contents of p:
                return if ("p" == node.nodeName()) NodeFilter.FilterResult.SKIP_CHILDREN else NodeFilter.FilterResult.CONTINUE
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("</").append(node.nodeName()).append(">")
                return NodeFilter.FilterResult.CONTINUE
            }
        }, doc.select("div"))
        Assertions.assertEquals("<div><p></p></div><div><#text></#text></div>", accum.toString())
    }

    @Test
    fun filterSkipEntirely() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                // OMIT p:
                if ("p" == node.nodeName()) return NodeFilter.FilterResult.SKIP_ENTIRELY
                accum.append("<").append(node.nodeName()).append(">")
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("</").append(node.nodeName()).append(">")
                return NodeFilter.FilterResult.CONTINUE
            }
        }, doc.select("div"))
        Assertions.assertEquals("<div></div><div><#text></#text></div>", accum.toString())
    }

    @Test
    fun filterRemove() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There be <b>bold</b></div>")
        filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                // Delete "p" in head:
                return if ("p" == node.nodeName()) NodeFilter.FilterResult.REMOVE else NodeFilter.FilterResult.CONTINUE
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                // Delete "b" in tail:
                return if ("b" == node.nodeName()) NodeFilter.FilterResult.REMOVE else NodeFilter.FilterResult.CONTINUE
            }
        }, doc.select("div"))
        Assertions.assertEquals("<div></div>\n<div>\n There be\n</div>", doc.select("body").html())
    }

    @Test
    fun filterStop() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("<").append(node.nodeName()).append(">")
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                accum.append("</").append(node.nodeName()).append(">")
                // Stop after p.
                return if ("p" == node.nodeName()) NodeFilter.FilterResult.STOP else NodeFilter.FilterResult.CONTINUE
            }
        }, doc.select("div"))
        Assertions.assertEquals("<div><p><#text></#text></p>", accum.toString())
    }

    @Test
    fun replaceElement() {
        // https://github.com/jhy/jsoup/issues/1289
        // test we can replace an element during traversal
        val html = "<div><p>One <i>two</i> <i>three</i> four.</p></div>"
        val doc = Jsoup.parse(html)
        traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                if (node is Element) {
                    val el = node
                    if (el.normalName() == "i") {
                        val u = Element("u").insertChildren(0, el.childNodes())
                        el.replaceWith(u)
                    }
                }
            }

            override fun tail(node: Node, depth: Int) {}
        }, doc)
        val p = doc.selectFirst("p")
        Assertions.assertNotNull(p)
        Assertions.assertEquals("<p>One <u>two</u> <u>three</u> four.</p>", p!!.outerHtml())
    }

    @Test
    fun canAddChildren() {
        val doc = Jsoup.parse("<div><p></p><p></p></div>")
        traverse(object : NodeVisitor {
            var i = 0
            override fun head(node: Node, depth: Int) {
                if (node.nodeName() == "p") {
                    val p = node as Element
                    p.append("<span>" + i++ + "</span>")
                }
            }

            override fun tail(node: Node, depth: Int) {
                if (node.nodeName() == "p") {
                    val p = node as Element
                    p.append("<span>" + i++ + "</span>")
                }
            }
        }, doc)
        Assertions.assertEquals(
            """<div>
 <p><span>0</span><span>1</span></p>
 <p><span>2</span><span>3</span></p>
</div>""", doc.body().html()
        )
    }

    @Test
    fun canSpecifyOnlyHead() {
        // really, a compilation test - works as a lambda if just head
        val doc = Jsoup.parse("<div><p>One</p></div>")
        val count = intArrayOf(0)
        traverse(jvmNodeVisitor { node: Node? -> count[0]++ }, doc)
        Assertions.assertEquals(7, count[0])
    }

    @Test
    fun canRemoveDuringHead() {
        val doc = Jsoup.parse("<div><p id=1>Zero<p id=1>One<p id=2>Two<p>Three</div>")
        traverse(
            jvmNodeVisitor { node: Node -> if (node.attr("id") == "1") node.remove() else if (node is TextNode && node.text() == "Three") node.remove() },
            doc
        )
        Assertions.assertEquals("<div><p id=\"2\">Two</p><p></p></div>", TextUtil.stripNewlines(doc.body().html()))
    }
}
