package org.jsoup.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.CDataNode
import org.jsoup.nodes.Comment
import org.jsoup.nodes.TextNode
import org.jsoup.parser.Parser.Companion.htmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import java.util.*

class TokeniserTest {
    @Test
    fun bufferUpInAttributeVal() {
        // https://github.com/jhy/jsoup/issues/967

        // check each double, singlem, unquoted impls
        val quotes = arrayOf("\"", "'", "")
        for (quote in quotes) {
            val preamble = "<img src=$quote"
            val tail = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
            val sb = StringBuilder(preamble)
            val charsToFillBuffer = CharacterReader.maxBufferLen - preamble.length
            for (i in 0 until charsToFillBuffer) {
                sb.append('a')
            }
            sb.append('X') // First character to cross character buffer boundary
            sb.append(tail).append(quote).append(">\n")
            val html = sb.toString()
            val doc = Jsoup.parse(html)
            val src = doc.select("img").attr("src")
            Assertions.assertTrue(src!!.contains("X"), "Handles for quote $quote")
            Assertions.assertTrue(src.contains(tail))
        }
    }

    @Test
    fun handleSuperLargeTagNames() {
        // unlikely, but valid. so who knows.
        val sb = StringBuilder(CharacterReader.maxBufferLen)
        do {
            sb.append("LargeTagName")
        } while (sb.length < CharacterReader.maxBufferLen)
        val tag = sb.toString()
        val html = "<$tag>One</$tag>"
        val doc = htmlParser().settings(ParseSettings.preserveCase).parseInput(html, "")
        val els = doc.select(tag)
        Assertions.assertEquals(1, els.size)
        val el = els.first()
        Assertions.assertNotNull(el)
        Assertions.assertEquals("One", el!!.text())
        Assertions.assertEquals(tag, el.tagName())
    }

    @Test
    fun handleSuperLargeAttributeName() {
        val sb = StringBuilder(CharacterReader.maxBufferLen)
        do {
            sb.append("LargAttributeName")
        } while (sb.length < CharacterReader.maxBufferLen)
        val attrName = sb.toString()
        val html = "<p $attrName=foo>One</p>"
        val doc = Jsoup.parse(html)
        val els = doc.getElementsByAttribute(attrName)
        Assertions.assertEquals(1, els.size)
        val el = els.first()
        Assertions.assertNotNull(el)
        Assertions.assertEquals("One", el!!.text())
        val (key, value) = el.attributes().asList()[0]
        Assertions.assertEquals(attrName.lowercase(Locale.getDefault()), key)
        Assertions.assertEquals("foo", value)
    }

    @Test
    fun handleLargeText() {
        val sb = StringBuilder(CharacterReader.maxBufferLen)
        do {
            sb.append("A Large Amount of Text")
        } while (sb.length < CharacterReader.maxBufferLen)
        val text = sb.toString()
        val html = "<p>$text</p>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p")
        Assertions.assertEquals(1, els.size)
        val el = els.first()
        Assertions.assertNotNull(el)
        Assertions.assertEquals(text, el!!.text())
    }

    @Test
    fun handleLargeComment() {
        val sb = StringBuilder(CharacterReader.maxBufferLen)
        do {
            sb.append("Quite a comment ")
        } while (sb.length < CharacterReader.maxBufferLen)
        val comment = sb.toString()
        val html = "<p><!-- $comment --></p>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p")
        Assertions.assertEquals(1, els.size)
        val el = els.first()
        Assertions.assertNotNull(el)
        val child = el!!.childNode(0) as Comment
        Assertions.assertEquals(" $comment ", child.data)
    }

    @Test
    fun handleLargeCdata() {
        val sb = StringBuilder(CharacterReader.maxBufferLen)
        do {
            sb.append("Quite a lot of CDATA <><><><>")
        } while (sb.length < CharacterReader.maxBufferLen)
        val cdata = sb.toString()
        val html = "<p><![CDATA[$cdata]]></p>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p")
        Assertions.assertEquals(1, els.size)
        val el = els.first()
        Assertions.assertNotNull(el)
        val child = el!!.childNode(0) as TextNode
        Assertions.assertEquals(cdata, el.text())
        Assertions.assertEquals(cdata, child.wholeText)
    }

    @Test
    fun handleLargeTitle() {
        val sb = StringBuilder(CharacterReader.maxBufferLen)
        do {
            sb.append("Quite a long title")
        } while (sb.length < CharacterReader.maxBufferLen)
        val title = sb.toString()
        val html = "<title>$title</title>"
        val doc = Jsoup.parse(html)
        val els = doc.select("title")
        Assertions.assertEquals(1, els.size)
        val el = els.first()
        Assertions.assertNotNull(el)
        val child = el!!.childNode(0) as TextNode
        Assertions.assertEquals(title, el.text())
        Assertions.assertEquals(title, child.wholeText)
        Assertions.assertEquals(title, doc.title())
    }

    @Test
    fun cp1252Entities() {
        Assertions.assertEquals("\u20ac", Jsoup.parse("&#0128;").text())
        Assertions.assertEquals("\u201a", Jsoup.parse("&#0130;").text())
        Assertions.assertEquals("\u20ac", Jsoup.parse("&#x80;").text())
    }

    @Test
    fun cp1252EntitiesProduceError() {
        val parser = Parser(HtmlTreeBuilder())
        parser.setTrackErrors(10)
        Assertions.assertEquals("\u20ac", parser.parseInput("<html><body>&#0128;</body></html>", "").text())
        Assertions.assertEquals(1, parser.errors.size)
    }

    @Test
    fun cp1252SubstitutionTable() {
        for (index in Tokeniser.win1252Extensions.indices) {
            val s = String(byteArrayOf((index + Tokeniser.win1252ExtensionsStart).toByte()), Charset.forName("Windows-1252"))
            assertEquals(1, s.length)

            // some of these characters are illegal
            if (s[0] == '\ufffd') {
                continue
            }

            assertEquals(s[0], Tokeniser.win1252Extensions[index].toChar(), "At: $index")
        }
    }

    @Test
    fun canParseVeryLongBogusComment() {
        val commentData = StringBuilder(CharacterReader.maxBufferLen)
        do {
            commentData.append("blah blah blah blah ")
        } while (commentData.length < CharacterReader.maxBufferLen)
        val expectedCommentData = commentData.toString()
        val testMarkup = "<html><body><!$expectedCommentData></body></html>"
        val parser = Parser(HtmlTreeBuilder())
        val doc = parser.parseInput(testMarkup, "")
        val commentNode = doc.body().childNode(0)
        Assertions.assertTrue(commentNode is Comment, "Expected comment node")
        Assertions.assertEquals(expectedCommentData, (commentNode as Comment).data)
    }

    @Test
    fun canParseCdataEndingAtEdgeOfBuffer() {
        val cdataStart = "<![CDATA["
        val cdataEnd = "]]>"
        val bufLen = CharacterReader.maxBufferLen - cdataStart.length - 1 // also breaks with -2, but not with -3 or 0
        val cdataContentsArray = CharArray(bufLen)
        Arrays.fill(cdataContentsArray, 'x')
        val cdataContents = String(cdataContentsArray)
        val testMarkup = cdataStart + cdataContents + cdataEnd
        val parser = Parser(HtmlTreeBuilder())
        val doc = parser.parseInput(testMarkup, "")
        val cdataNode = doc.body().childNode(0)
        Assertions.assertTrue(cdataNode is CDataNode, "Expected CDATA node")
        Assertions.assertEquals(cdataContents, (cdataNode as CDataNode).text())
    }
}
