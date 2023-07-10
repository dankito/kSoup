package org.jsoup.nodes

import org.jsoup.Jsoupimport

org.junit.jupiter.api.Assertionsimport org.junit.jupiter.api.Test
class ElementIT {
    @Test
    fun testFastReparent() {
        val htmlBuf = StringBuilder()
        val rows = 300000
        for (i in 1..rows) {
            htmlBuf
                .append("<p>El-")
                .append(i)
                .append("</p>")
        }
        val html = htmlBuf.toString()
        val doc = Jsoup.parse(html)
        val start = System.currentTimeMillis()
        val wrapper = Element("div")
        val childNodes = doc.body().childNodes()
        wrapper.insertChildren(0, childNodes)
        val runtime = System.currentTimeMillis() - start
        Assertions.assertEquals(rows, wrapper.childNodes.size)
        Assertions.assertEquals(rows, childNodes.size) // child nodes is a wrapper, so still there
        Assertions.assertEquals(0, doc.body().childNodes().size) // but on a fresh look, all gone
        doc.body().empty().appendChild(wrapper)
        val wrapperAcutal = doc.body().children()[0]
        Assertions.assertEquals(wrapper, wrapperAcutal)
        Assertions.assertEquals("El-1", wrapperAcutal.children()[0].text())
        Assertions.assertEquals("El-$rows", wrapperAcutal.children()[rows - 1].text())
        Assertions.assertTrue(runtime <= 10000)
    }

    @Test
    fun testFastReparentExistingContent() {
        val htmlBuf = StringBuilder()
        val rows = 300000
        for (i in 1..rows) {
            htmlBuf
                .append("<p>El-")
                .append(i)
                .append("</p>")
        }
        val html = htmlBuf.toString()
        val doc = Jsoup.parse(html)
        val start = System.currentTimeMillis()
        val wrapper = Element("div")
        wrapper.append("<p>Prior Content</p>")
        wrapper.append("<p>End Content</p>")
        Assertions.assertEquals(2, wrapper.childNodes.size)
        val childNodes = doc.body().childNodes()
        wrapper.insertChildren(1, childNodes)
        val runtime = System.currentTimeMillis() - start
        Assertions.assertEquals(rows + 2, wrapper.childNodes.size)
        Assertions.assertEquals(rows, childNodes.size) // child nodes is a wrapper, so still there
        Assertions.assertEquals(0, doc.body().childNodes().size) // but on a fresh look, all gone
        doc.body().empty().appendChild(wrapper)
        val wrapperAcutal = doc.body().children()[0]
        Assertions.assertEquals(wrapper, wrapperAcutal)
        Assertions.assertEquals("Prior Content", wrapperAcutal.children()[0].text())
        Assertions.assertEquals("El-1", wrapperAcutal.children()[1].text())
        Assertions.assertEquals("El-$rows", wrapperAcutal.children()[rows].text())
        Assertions.assertEquals("End Content", wrapperAcutal.children()[rows + 1].text())
        Assertions.assertTrue(runtime <= 10000)
    }

    // These overflow tests take a couple seconds to run, so are in the slow tests
    @Test
    fun hasTextNoOverflow() {
        // hasText() was recursive, so could overflow
        val doc = Document("https://example.com/")
        var el = doc.body()
        for (i in 0..50000) {
            el = el.appendElement("p")
        }
        Assertions.assertFalse(doc.hasText())
        el.text("Hello")
        Assertions.assertTrue(doc.hasText())
        Assertions.assertEquals(el.text(), doc.text())
    }

    @Test
    fun dataNoOverflow() {
        // data() was recursive, so could overflow
        val doc = Document("https://example.com/")
        var el = doc.body()
        for (i in 0..50000) {
            el = el.appendElement("p")
        }
        val script = el.appendElement("script")
        script.text("script") // holds data nodes, so inserts as data, not text
        Assertions.assertFalse(script.hasText())
        Assertions.assertEquals("script", script.data())
        Assertions.assertEquals(el.data(), doc.data())
    }

    @Test
    fun parentsNoOverflow() {
        // parents() was recursive, so could overflow
        val doc = Document("https://example.com/")
        var el = doc.body()
        val num = 50000
        for (i in 0..num) {
            el = el.appendElement("p")
        }
        val parents = el.parents()
        Assertions.assertEquals(num + 2, parents.size) // +2 for html and body
        Assertions.assertEquals(doc, el.ownerDocument())
    }

    @Test
    fun wrapNoOverflow() {
        // deepChild was recursive, so could overflow if presented with a fairly insane wrap
        val doc = Document("https://example.com/")
        val el = doc.body().appendElement("p")
        val num = 50000
        val sb = StringBuilder()
        for (i in 0..num) {
            sb.append("<div>")
        }
        el.wrap(sb.toString())
        val html = doc.body().html()
        Assertions.assertTrue(html.startsWith("<div>"))
        Assertions.assertEquals(num + 3, el.parents().size)
    }
}
