package org.jsoup.nodes

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.Entities.escape
import org.jsoup.nodes.Entities.getByName
import org.jsoup.nodes.Entities.unescape
import org.jsoup.parser.Parser.Companion.xmlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EntitiesTest {
    @Test
    fun escape() {
        val text = "Hello &<> Å å π 新 there ¾ © »"
        val escapedAscii = escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base))
        val escapedAsciiFull =
            escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        val escapedAsciiXhtml =
            escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.xhtml))
        val escapedUtfFull =
            escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended))
        val escapedUtfMin =
            escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.xhtml))
        Assertions.assertEquals(
            "Hello &amp;&lt;&gt; &Aring; &aring; &#x3c0; &#x65b0; there &frac34; &copy; &raquo;",
            escapedAscii
        )
        Assertions.assertEquals(
            "Hello &amp;&lt;&gt; &angst; &aring; &pi; &#x65b0; there &frac34; &copy; &raquo;",
            escapedAsciiFull
        )
        Assertions.assertEquals(
            "Hello &amp;&lt;&gt; &#xc5; &#xe5; &#x3c0; &#x65b0; there &#xbe; &#xa9; &#xbb;",
            escapedAsciiXhtml
        )
        Assertions.assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfFull)
        Assertions.assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfMin)
        // odd that it's defined as aring in base but angst in full

        // round trip
        Assertions.assertEquals(text, unescape(escapedAscii))
        Assertions.assertEquals(text, unescape(escapedAsciiFull))
        Assertions.assertEquals(text, unescape(escapedAsciiXhtml))
        Assertions.assertEquals(text, unescape(escapedUtfFull))
        Assertions.assertEquals(text, unescape(escapedUtfMin))
    }

    @Test
    fun escapedSupplementary() {
        val text = "\uD835\uDD59"
        val escapedAscii = escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base))
        Assertions.assertEquals("&#x1d559;", escapedAscii)
        val escapedAsciiFull =
            escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        Assertions.assertEquals("&hopf;", escapedAsciiFull)
        val escapedUtf =
            escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended))
        Assertions.assertEquals(text, escapedUtf)
    }

    @Test
    fun unescapeMultiChars() {
        val text =
            "&NestedGreaterGreater; &nGg; &nGt; &nGtv; &Gt; &gg;" // gg is not combo, but 8811 could conflict with NestedGreaterGreater or others
        val un = "≫ ⋙̸ ≫⃒ ≫̸ ≫ ≫"
        Assertions.assertEquals(un, unescape(text))
        val escaped = escape(un, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        Assertions.assertEquals("&Gt; &Gg;&#x338; &Gt;&#x20d2; &Gt;&#x338; &Gt; &Gt;", escaped)
        Assertions.assertEquals(un, unescape(escaped))
    }

    @Test
    fun xhtml() {
        Assertions.assertEquals(38, Entities.EscapeMode.xhtml.codepointForName("amp"))
        Assertions.assertEquals(62, Entities.EscapeMode.xhtml.codepointForName("gt"))
        Assertions.assertEquals(60, Entities.EscapeMode.xhtml.codepointForName("lt"))
        Assertions.assertEquals(34, Entities.EscapeMode.xhtml.codepointForName("quot"))
        Assertions.assertEquals("amp", Entities.EscapeMode.xhtml.nameForCodepoint(38))
        Assertions.assertEquals("gt", Entities.EscapeMode.xhtml.nameForCodepoint(62))
        Assertions.assertEquals("lt", Entities.EscapeMode.xhtml.nameForCodepoint(60))
        Assertions.assertEquals("quot", Entities.EscapeMode.xhtml.nameForCodepoint(34))
    }

    @get:Test
    val byName: Unit
        get() {
            Assertions.assertEquals("≫⃒", getByName("nGt"))
            Assertions.assertEquals("fj", getByName("fjlig"))
            Assertions.assertEquals("≫", getByName("gg"))
            Assertions.assertEquals("©", getByName("copy"))
        }

    @Test
    fun escapeSupplementaryCharacter() {
        val text = String(Character.toChars(135361))
        val escapedAscii = escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base))
        Assertions.assertEquals("&#x210c1;", escapedAscii)
        val escapedUtf = escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.base))
        Assertions.assertEquals(text, escapedUtf)
    }

    @Test
    fun notMissingMultis() {
        val text = "&nparsl;"
        val un = "\u2AFD\u20E5"
        Assertions.assertEquals(un, unescape(text))
    }

    @Test
    fun notMissingSupplementals() {
        val text = "&npolint; &qfr;"
        val un = "⨔ \uD835\uDD2E" // 𝔮
        Assertions.assertEquals(un, unescape(text))
    }

    @Test
    fun unescape() {
        val text =
            "Hello &AElig; &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;"
        Assertions.assertEquals("Hello Æ &<> ® Å &angst π π 新 there &! ¾ © ©", unescape(text))
        Assertions.assertEquals("&0987654321; &unknown", unescape("&0987654321; &unknown"))
    }

    @Test
    fun strictUnescape() { // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        val text = "Hello &amp= &amp;"
        Assertions.assertEquals("Hello &amp= &", unescape(text, true))
        Assertions.assertEquals("Hello &= &", unescape(text))
        Assertions.assertEquals("Hello &= &", unescape(text, false))
    }

    @Test
    fun caseSensitive() {
        val unescaped = "Ü ü & &"
        Assertions.assertEquals(
            "&Uuml; &uuml; &amp; &amp;",
            escape(unescaped, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        )
        val escaped = "&Uuml; &uuml; &amp; &AMP"
        Assertions.assertEquals("Ü ü & &", unescape(escaped))
    }

    @Test
    fun quoteReplacements() {
        val escaped = "&#92; &#36;"
        val unescaped = "\\ $"
        Assertions.assertEquals(unescaped, unescape(escaped))
    }

    @Test
    fun letterDigitEntities() {
        val html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().charset("ascii")
        val p = doc.select("p").first()
        Assertions.assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p!!.html())
        Assertions.assertEquals("¹²³¼½¾", p.text())
        doc.outputSettings().charset("UTF-8")
        Assertions.assertEquals("¹²³¼½¾", p.html())
    }

    @Test
    fun noSpuriousDecodes() {
        val string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2"
        Assertions.assertEquals(string, unescape(string))
    }

    @Test
    fun escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML
        val docHtml = "<a title='<p>One</p>'>One</a>"
        val doc = Jsoup.parse(docHtml)
        val element = doc.select("a").first()
        doc.outputSettings().escapeMode(Entities.EscapeMode.base)
        Assertions.assertEquals("<a title=\"<p>One</p>\">One</a>", element!!.outerHtml())
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        Assertions.assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml())
    }

    @Test
    fun controlCharactersAreEscaped() {
        // https://github.com/jhy/jsoup/issues/1556
        // we escape ascii control characters in both HTML and XML for compatibility. Required in XML and probably
        // easier to read in HTML
        val input = "<a foo=\"&#x1b;esc&#x7;bell\">Text &#x1b; &#x7;</a>"
        val doc = Jsoup.parse(input)
        Assertions.assertEquals(input, doc.body().html())
        val xml = parse(input, "", xmlParser())
        Assertions.assertEquals(input, xml.html())
    }
}
