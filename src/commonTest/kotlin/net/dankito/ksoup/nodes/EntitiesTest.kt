package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.nodes.Entities.getByName
import net.dankito.ksoup.parser.Parser.Companion.xmlParser
import kotlin.test.Test
import kotlin.test.assertEquals

class EntitiesTest {
    
    @Test
    fun escape() {
        val text = "Hello &<> Å å π 新 there ¾ © »"

        val escapedAscii = Entities.escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base))
        assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; &#x3c0; &#x65b0; there &frac34; &copy; &raquo;", escapedAscii)
        // round trip
        assertEquals(text, Entities.unescape(escapedAscii))

        val escapedAsciiFull = Entities.escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        assertEquals("Hello &amp;&lt;&gt; &angst; &aring; &pi; &#x65b0; there &frac34; &copy; &raquo;", escapedAsciiFull)
        assertEquals(text, Entities.unescape(escapedAsciiFull))

        val escapedAsciiXhtml = Entities.escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.xhtml))
        assertEquals("Hello &amp;&lt;&gt; &#xc5; &#xe5; &#x3c0; &#x65b0; there &#xbe; &#xa9; &#xbb;", escapedAsciiXhtml)
        assertEquals(text, Entities.unescape(escapedAsciiXhtml))

        val escapedUtfFull = Entities.escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended))
        assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfFull)
        assertEquals(text, Entities.unescape(escapedUtfFull))

        val escapedUtfMin = Entities.escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.xhtml))
        assertEquals("Hello &amp;&lt;&gt; Å å π 新 there ¾ © »", escapedUtfMin)
        // odd that it's defined as aring in base but angst in full
        assertEquals(text, Entities.unescape(escapedUtfMin))
    }

    @Test
    fun escapedSupplementary() {
        val text = "\uD835\uDD59"
        val escapedAscii = Entities.escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base))
        assertEquals("&#x1d559;", escapedAscii)
        val escapedAsciiFull =
            Entities.escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        assertEquals("&hopf;", escapedAsciiFull)
        val escapedUtf =
            Entities.escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.extended))
        assertEquals(text, escapedUtf)
    }

    @Test
    fun unescapeMultiChars() {
        val text =
            "&NestedGreaterGreater; &nGg; &nGt; &nGtv; &Gt; &gg;" // gg is not combo, but 8811 could conflict with NestedGreaterGreater or others
        val un = "≫ ⋙̸ ≫⃒ ≫̸ ≫ ≫"
        assertEquals(un, Entities.unescape(text))
        val escaped = Entities.escape(un, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        assertEquals("&Gt; &Gg;&#x338; &Gt;&#x20d2; &Gt;&#x338; &Gt; &Gt;", escaped)
        assertEquals(un, Entities.unescape(escaped))
    }

    @Test
    fun xhtml() {
        assertEquals(38, Entities.EscapeMode.xhtml.codepointForName("amp"))
        assertEquals(62, Entities.EscapeMode.xhtml.codepointForName("gt"))
        assertEquals(60, Entities.EscapeMode.xhtml.codepointForName("lt"))
        assertEquals(34, Entities.EscapeMode.xhtml.codepointForName("quot"))
        assertEquals("amp", Entities.EscapeMode.xhtml.nameForCodepoint(38))
        assertEquals("gt", Entities.EscapeMode.xhtml.nameForCodepoint(62))
        assertEquals("lt", Entities.EscapeMode.xhtml.nameForCodepoint(60))
        assertEquals("quot", Entities.EscapeMode.xhtml.nameForCodepoint(34))
    }

    @Test
    fun byName() {
        assertEquals("≫⃒", getByName("nGt"))
        assertEquals("fj", getByName("fjlig"))
        assertEquals("≫", getByName("gg"))
        assertEquals("©", getByName("copy"))
    }

    @Test
    fun escapeSupplementaryCharacter() {
        val text = StringUtil.codePointToString(135361)
        val escapedAscii = Entities.escape(text, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.base))
        assertEquals("&#x210c1;", escapedAscii)
        val escapedUtf = Entities.escape(text, Document.OutputSettings().charset("UTF-8").escapeMode(Entities.EscapeMode.base))
        assertEquals(text, escapedUtf)
    }

    @Test
    fun notMissingMultis() {
        val text = "&nparsl;"
        val un = "\u2AFD\u20E5"
        assertEquals(un, Entities.unescape(text))
    }

    @Test
    fun notMissingSupplementals() {
        val text = "&npolint; &qfr;"
        val un = "⨔ \uD835\uDD2E" // 𝔮
        assertEquals(un, Entities.unescape(text))
    }

    @Test
    fun unescape() {
        val text =
            "Hello &AElig; &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;"
        assertEquals("Hello Æ &<> ® Å &angst π π 新 there &! ¾ © ©", Entities.unescape(text))
        assertEquals("&0987654321; &unknown", Entities.unescape("&0987654321; &unknown"))
    }

    @Test
    fun strictUnescape() { // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        val text = "Hello &amp= &amp;"
        assertEquals("Hello &amp= &", Entities.unescape(text, true))
        assertEquals("Hello &= &", Entities.unescape(text))
        assertEquals("Hello &= &", Entities.unescape(text, false))
    }

    @Test
    fun caseSensitive() {
        val unescaped = "Ü ü & &"
        assertEquals(
            "&Uuml; &uuml; &amp; &amp;",
            Entities.escape(unescaped, Document.OutputSettings().charset("ascii").escapeMode(Entities.EscapeMode.extended))
        )
        val escaped = "&Uuml; &uuml; &amp; &AMP"
        assertEquals("Ü ü & &", Entities.unescape(escaped))
    }

    @Test
    fun quoteReplacements() {
        val escaped = "&#92; &#36;"
        val unescaped = "\\ $"
        assertEquals(unescaped, Entities.unescape(escaped))
    }

    @Test
    fun letterDigitEntities() {
        val html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().charset("ascii")
        val p = doc.select("p").first()
        assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p!!.html())
        assertEquals("¹²³¼½¾", p.text())
        doc.outputSettings().charset("UTF-8")
        assertEquals("¹²³¼½¾", p.html())
    }

    @Test
    fun noSpuriousDecodes() {
        val string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2"
        assertEquals(string, Entities.unescape(string))
    }

    @Test
    fun escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML
        val docHtml = "<a title='<p>One</p>'>One</a>"
        val doc = Jsoup.parse(docHtml)
        val element = doc.select("a").first()
        doc.outputSettings().escapeMode(Entities.EscapeMode.base)
        assertEquals("<a title=\"<p>One</p>\">One</a>", element!!.outerHtml())
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml())
    }

    @Test
    fun controlCharactersAreEscaped() {
        // https://github.com/jhy/jsoup/issues/1556
        // we escape ascii control characters in both HTML and XML for compatibility. Required in XML and probably
        // easier to read in HTML
        val input = "<a foo=\"&#x1b;esc&#x7;bell\">Text &#x1b; &#x7;</a>"
        val doc = Jsoup.parse(input)
        assertEquals(input, doc.body().html())
        val xml = parse(input, "", xmlParser())
        assertEquals(input, xml.html())
    }
}
