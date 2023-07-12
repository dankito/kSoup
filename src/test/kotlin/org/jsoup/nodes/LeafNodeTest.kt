package org.jsoup.nodes

import org.jsoup.Jsoup
import org.jsoup.select.NodeFilter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LeafNodeTest {
    @Test
    fun doesNotGetAttributesTooEasily() {
        // test to make sure we're not setting attributes on all nodes right away
        val body = "<p>One <!-- Two --> Three<![CDATA[Four]]></p>"
        val doc = Jsoup.parse(body)
        Assertions.assertTrue(hasAnyAttributes(doc)) // should have one - the base uri on the doc
        val html = doc.child(0)
        Assertions.assertFalse(hasAnyAttributes(html))
        val s = doc.outerHtml()
        Assertions.assertFalse(hasAnyAttributes(html))
        var els = doc.select("p")
        val p = els.first()
        Assertions.assertEquals(1, els.size)
        Assertions.assertFalse(hasAnyAttributes(html))
        els = doc.select("p.none")
        Assertions.assertFalse(hasAnyAttributes(html))
        val id = p!!.id()
        Assertions.assertEquals("", id)
        Assertions.assertFalse(p.hasClass("Foobs"))
        Assertions.assertFalse(hasAnyAttributes(html))
        p.addClass("Foobs")
        Assertions.assertTrue(p.hasClass("Foobs"))
        Assertions.assertTrue(hasAnyAttributes(html))
        Assertions.assertTrue(hasAnyAttributes(p))
        val attributes = p.attributes()
        Assertions.assertTrue(attributes.hasKey("class"))
        p.clearAttributes()
        Assertions.assertFalse(hasAnyAttributes(p))
        Assertions.assertFalse(hasAnyAttributes(html))
        Assertions.assertFalse(attributes.hasKey("class"))
    }

    private fun hasAnyAttributes(node: Node?): Boolean {
        val found = BooleanArray(1)
        node!!.filter(object : NodeFilter {
            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                return if (node.hasAttributes()) {
                    found[0] = true
                    NodeFilter.FilterResult.STOP
                } else {
                    NodeFilter.FilterResult.CONTINUE
                }
            }

            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                return NodeFilter.FilterResult.CONTINUE
            }
        })
        return found[0]
    }
}
