package net.dankito.ksoup.safety

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.clean
import net.dankito.ksoup.Jsoup.isValid
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.nodes.Document
import net.dankito.ksoup.nodes.Entities
import net.dankito.ksoup.parser.Parser.Companion.htmlParser
import net.dankito.ksoup.safety.Safelist.Companion.basic
import net.dankito.ksoup.safety.Safelist.Companion.basicWithImages
import net.dankito.ksoup.safety.Safelist.Companion.none
import net.dankito.ksoup.safety.Safelist.Companion.relaxed
import net.dankito.ksoup.safety.Safelist.Companion.simpleText
import kotlin.test.*

/**
 * Tests for the cleaner.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class CleanerTest {

    @Test
    fun simpleBehaviourTest() {
        val h = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a></div>"
        val cleanHtml = clean(h, simpleText())
        assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun simpleBehaviourTest2() {
        val h = "Hello <b>there</b>!"
        val cleanHtml = clean(h, simpleText())
        assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun basicBehaviourTest() {
        val h = "<div><p><a href='javascript:sendAllMoney()'>Dodgy</a> <A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>"
        val cleanHtml = clean(h, basic())

        assertEquals(
            "<p><a rel=\"nofollow\">Dodgy</a> <a href=\"http://nice.com\" rel=\"nofollow\">Nice</a></p><blockquote>Hello</blockquote>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun basicWithImagesTest() {
        val h = "<div><p><img src='http://example.com/' alt=Image></p><p><img src='ftp://ftp.example.com'></p></div>"
        val cleanHtml = clean(h, basicWithImages())
        assertEquals(
            "<p><img src=\"http://example.com/\" alt=\"Image\"></p><p><img></p>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testRelaxed() {
        val h = "<h1>Head</h1><table><tr><td>One<td>Two</td></tr></table>"
        val cleanHtml = clean(h, relaxed())
        assertEquals(
            "<h1>Head</h1><table><tbody><tr><td>One</td><td>Two</td></tr></tbody></table>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testRemoveTags() {
        val h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>"
        val cleanHtml = clean(h, basic().removeTags("a"))
        assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun testRemoveAttributes() {
        val h = "<div><p>Nice</p><blockquote cite='http://example.com/quotations'>Hello</blockquote>"
        val cleanHtml = clean(h, basic().removeAttributes("blockquote", "cite"))
        assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun allAttributes() {
        val h = "<div class=foo data=true><p class=bar>Text</p></div><blockquote cite='https://example.com'>Foo"
        val safelist = relaxed()
        safelist.addAttributes(":all", "class")
        safelist.addAttributes("div", "data")
        val clean1 = clean(h, safelist)
        assertEquals(
            "<div class=\"foo\" data=\"true\"><p class=\"bar\">Text</p></div><blockquote cite=\"https://example.com\">Foo</blockquote>",
            TextUtil.stripNewlines(clean1)
        )
        safelist.removeAttributes(":all", "class", "cite")
        val clean2 = clean(h, safelist)
        assertEquals(
            "<div data=\"true\"><p>Text</p></div><blockquote>Foo</blockquote>",
            TextUtil.stripNewlines(clean2)
        )
    }

    @Test
    fun removeProtocols() {
        val h = "<a href='any://example.com'>Link</a>"
        val safelist = relaxed()
        val clean1 = clean(h, safelist)
        assertEquals("<a>Link</a>", clean1)
        safelist.removeProtocols("a", "href", "ftp", "http", "https", "mailto")
        val clean2 = clean(h, safelist) // all removed means any will work
        assertEquals("<a href=\"any://example.com\">Link</a>", clean2)
    }

    @Test
    fun testRemoveEnforcedAttributes() {
        val h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>"
        val cleanHtml = clean(h, basic().removeEnforcedAttribute("a", "rel"))
        assertEquals(
            "<p><a href=\"http://nice.com\">Nice</a></p><blockquote>Hello</blockquote>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testRemoveProtocols() {
        val h = "<p>Contact me <a href='mailto:info@example.com'>here</a></p>"
        val cleanHtml = clean(h, basic().removeProtocols("a", "href", "ftp", "mailto"))
        assertEquals(
            "<p>Contact me <a rel=\"nofollow\">here</a></p>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testDropComments() {
        val h = "<p>Hello<!-- no --></p>"
        val cleanHtml = clean(h, relaxed())
        assertEquals("<p>Hello</p>", cleanHtml)
    }

    @Test
    fun testDropXmlProc() {
        val h = "<?import namespace=\"xss\"><p>Hello</p>"
        val cleanHtml = clean(h, relaxed())
        assertEquals("<p>Hello</p>", cleanHtml)
    }

    @Test
    fun testDropScript() {
        val h = "<SCRIPT SRC=//ha.ckers.org/.j><SCRIPT>alert(/XSS/.source)</SCRIPT>"
        val cleanHtml = clean(h, relaxed())
        assertEquals("", cleanHtml)
    }

    @Test
    fun testDropImageScript() {
        val h = "<IMG SRC=\"javascript:alert('XSS')\">"
        val cleanHtml = clean(h, relaxed())
        assertEquals("<img>", cleanHtml)
    }

    @Test
    fun testCleanJavascriptHref() {
        val h = "<A HREF=\"javascript:document.location='http://www.google.com/'\">XSS</A>"
        val cleanHtml = clean(h, relaxed())
        assertEquals("<a>XSS</a>", cleanHtml)
    }

    @Test
    fun testCleanAnchorProtocol() {
        val validAnchor = "<a href=\"#valid\">Valid anchor</a>"
        val invalidAnchor = "<a href=\"#anchor with spaces\">Invalid anchor</a>"

        // A Safelist that does not allow anchors will strip them out.
        var cleanHtml = clean(validAnchor, relaxed())
        assertEquals("<a>Valid anchor</a>", cleanHtml)
        cleanHtml = clean(invalidAnchor, relaxed())
        assertEquals("<a>Invalid anchor</a>", cleanHtml)

        // A Safelist that allows them will keep them.
        val relaxedWithAnchor = relaxed().addProtocols("a", "href", "#")
        cleanHtml = clean(validAnchor, relaxedWithAnchor)
        assertEquals(validAnchor, cleanHtml)

        // An invalid anchor is never valid.
        cleanHtml = clean(invalidAnchor, relaxedWithAnchor)
        assertEquals("<a>Invalid anchor</a>", cleanHtml)
    }

    @Test
    fun testDropsUnknownTags() {
        val h = "<p><custom foo=true>Test</custom></p>"
        val cleanHtml = clean(h, relaxed())
        assertEquals("<p>Test</p>", cleanHtml)
    }

    @Test
    fun testHandlesEmptyAttributes() {
        val h = "<img alt=\"\" src= unknown=''>"
        val cleanHtml = clean(h, basicWithImages())
        assertEquals("<img alt=\"\">", cleanHtml)
    }

    @Test
    fun testIsValidBodyHtml() {
        val ok = "<p>Test <b><a href='http://example.com/' rel='nofollow'>OK</a></b></p>"
        val ok1 =
            "<p>Test <b><a href='http://example.com/'>OK</a></b></p>" // missing enforced is OK because still needs run thru cleaner
        val nok1 = "<p><script></script>Not <b>OK</b></p>"
        val nok2 = "<p align=right>Test Not <b>OK</b></p>"
        val nok3 = "<!-- comment --><p>Not OK</p>" // comments and the like will be cleaned
        val nok4 = "<html><head>Foo</head><body><b>OK</b></body></html>" // not body html
        val nok5 = "<p>Test <b><a href='http://example.com/' rel='nofollowme'>OK</a></b></p>"
        val nok6 = "<p>Test <b><a href='http://example.com/'>OK</b></p>" // missing close tag
        val nok7 = "</div>What"
        assertTrue(isValid(ok, basic()))
        assertTrue(isValid(ok1, basic()))
        assertFalse(isValid(nok1, basic()))
        assertFalse(isValid(nok2, basic()))
        assertFalse(isValid(nok3, basic()))
        assertFalse(isValid(nok4, basic()))
        assertFalse(isValid(nok5, basic()))
        assertFalse(isValid(nok6, basic()))
        assertFalse(isValid(ok, none()))
        assertFalse(isValid(nok7, basic()))
    }

    @Test
    fun testIsValidDocument() {
        val ok = "<html><head></head><body><p>Hello</p></body><html>"
        val nok = "<html><head><script>woops</script><title>Hello</title></head><body><p>Hello</p></body><html>"
        val relaxed = relaxed()
        val cleaner = Cleaner(relaxed)
        val okDoc = Jsoup.parse(ok)
        assertTrue(cleaner.isValid(okDoc))
        assertFalse(cleaner.isValid(Jsoup.parse(nok)))
        assertFalse(Cleaner(none()).isValid(okDoc))
    }

    @Test
    fun resolvesRelativeLinks() {
        val html = "<a href='/foo'>Link</a><img src='/bar'>"
        val clean = clean(html, "http://example.com/", basicWithImages())
        assertEquals(
            "<a href=\"http://example.com/foo\" rel=\"nofollow\">Link</a><img src=\"http://example.com/bar\">",
            clean
        )
    }

    @Test
    fun preservesRelativeLinksIfConfigured() {
        val html = "<a href='/foo'>Link</a><img src='/bar'> <img src='javascript:alert()'>"
        val clean = clean(html, "http://example.com/", basicWithImages().preserveRelativeLinks(true))
        assertEquals("<a href=\"/foo\" rel=\"nofollow\">Link</a><img src=\"/bar\"> <img>", clean)
    }

    @Test
    fun dropsUnresolvableRelativeLinks() {
        val html = "<a href='/foo'>Link</a>"
        val clean = clean(html, basic())
        assertEquals("<a rel=\"nofollow\">Link</a>", clean)
    }

    @Test
    fun dropsConcealedJavascriptProtocolWhenRelativesLinksEnabled() {
        val safelist = basic().preserveRelativeLinks(true)
        val html = "<a href=\"&#0013;ja&Tab;va&Tab;script&#0010;:alert(1)\">Link</a>"
        val clean = clean(html, "https://", safelist)
        assertEquals("<a rel=\"nofollow\">Link</a>", clean)
        val colon = "<a href=\"ja&Tab;va&Tab;script&colon;alert(1)\">Link</a>"
        val cleanColon = clean(colon, "https://", safelist)
        assertEquals("<a rel=\"nofollow\">Link</a>", cleanColon)
    }

    @Test
    fun dropsConcealedJavascriptProtocolWhenRelativesLinksDisabled() {
        val safelist = basic().preserveRelativeLinks(false)
        val html = "<a href=\"ja&Tab;vas&#0013;cript:alert(1)\">Link</a>"
        val clean = clean(html, "https://", safelist)
        assertEquals("<a rel=\"nofollow\">Link</a>", clean)
    }

    @Test
    fun handlesCustomProtocols() {
        val html = "<img src='cid:12345' /> <img src='data:gzzt' />"
        val dropped = clean(html, basicWithImages())
        assertEquals("<img> <img>", dropped)
        val preserved = clean(html, basicWithImages().addProtocols("img", "src", "cid", "data"))
        assertEquals("<img src=\"cid:12345\"> <img src=\"data:gzzt\">", preserved)
    }

    @Test
    fun handlesAllPseudoTag() {
        val html = "<p class='foo' src='bar'><a class='qux'>link</a></p>"
        val safelist = Safelist()
            .addAttributes(":all", "class")
            .addAttributes("p", "style")
            .addTags("p", "a")
        val clean = clean(html, safelist)
        assertEquals("<p class=\"foo\"><a class=\"qux\">link</a></p>", clean)
    }

    @Test
    fun addsTagOnAttributesIfNotSet() {
        val html = "<p class='foo' src='bar'>One</p>"
        val safelist = Safelist()
            .addAttributes("p", "class")
        // ^^ safelist does not have explicit tag add for p, inferred from add attributes.
        val clean = clean(html, safelist)
        assertEquals("<p class=\"foo\">One</p>", clean)
    }

    @Test
    fun supplyOutputSettings() {
        // test that one can override the default document output settings
        val os = Document.OutputSettings()
        os.prettyPrint(false)
        os.escapeMode(Entities.EscapeMode.extended)
        os.charset("ascii")
        val html = "<div><p>&bernou;</p></div>"
        val customOut = clean(html, "http://foo.com/", relaxed(), os)
        val defaultOut = clean(html, "http://foo.com/", relaxed())
        assertNotSame(defaultOut, customOut)
        assertEquals("<div><p>&Bscr;</p></div>", customOut) // entities now prefers shorted names if aliased
        assertEquals(
            """<div>
 <p>ℬ</p>
</div>""", defaultOut
        )
        os.charset("ASCII")
        os.escapeMode(Entities.EscapeMode.base)
        val customOut2 = clean(html, "http://foo.com/", relaxed(), os)
        assertEquals("<div><p>&#x212c;</p></div>", customOut2)
    }

    @Test
    fun handlesFramesets() {
        val dirty =
            "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\" /><frame src=\"foo\" /></frameset></html>"
        val clean = clean(dirty, basic())
        assertEquals("", clean) // nothing good can come out of that
        val dirtyDoc = Jsoup.parse(dirty)
        val cleanDoc = Cleaner(basic()).clean(dirtyDoc)
        assertNotNull(cleanDoc)
        assertEquals(0, cleanDoc.body().childNodeSize())
    }

    @Test
    fun cleansInternationalText() {
        assertEquals("привет", clean("привет", none()))
    }

    @Test
    fun testScriptTagInSafeList() {
        val safelist = relaxed()
        safelist.addTags("script")
        assertTrue(isValid("Hello<script>alert('Doh')</script>World !", safelist))
    }

    @Test
    fun bailsIfRemovingProtocolThatsNotSet() {
        assertFailsWith(IllegalArgumentException::class) {

            // a case that came up on the email list
            val w = none()

            // note no add tag, and removing protocol without adding first
            w.addAttributes("a", "href")
            w.removeProtocols("a", "href", "javascript") // with no protocols enforced, this was a noop. Now validates.
        }
    }

    @Test
    fun handlesControlCharactersAfterTagName() {
        val html = "<a/\u0006>"
        val clean = clean(html, basic())
        assertEquals("<a rel=\"nofollow\"></a>", clean)
    }

    @Test
    fun handlesAttributesWithNoValue() {
        // https://github.com/jhy/jsoup/issues/973
        val clean = clean("<a href>Clean</a>", basic())
        assertEquals("<a rel=\"nofollow\">Clean</a>", clean)
    }

    @Test
    fun handlesNoHrefAttribute() {
        val dirty = "<a>One</a> <a href>Two</a>"
        val relaxedWithAnchor = relaxed().addProtocols("a", "href", "#")
        val clean = clean(dirty, relaxedWithAnchor)
        assertEquals("<a>One</a> <a>Two</a>", clean)
    }

    @Test
    fun handlesNestedQuotesInAttribute() {
        // https://github.com/jhy/jsoup/issues/1243 - no repro
        val orig = "<div style=\"font-family: 'Calibri'\">Will (not) fail</div>"
        val allow = relaxed()
            .addAttributes("div", "style")
        val clean = clean(orig, allow)
        val isValid = isValid(orig, allow)
        assertEquals(orig, TextUtil.stripNewlines(clean)) // only difference is pretty print wrap & indent
        assertTrue(isValid)
    }

    @Test
    fun copiesOutputSettings() {
        val orig = Jsoup.parse("<p>test<br></p>")
        orig.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        orig.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        val safelist = none().addTags("p", "br")
        val result = Cleaner(safelist).clean(orig)
        assertEquals(Document.OutputSettings.Syntax.xml, result.outputSettings().syntax())
        assertEquals("<p>test<br /></p>", result.body().html())
    }

    @Test
    fun preservesSourcePositionViaUserData() {
        val orig = parse("<script>xss</script>\n <p>Hello</p>", htmlParser().setTrackPosition(true))
        val p = orig.expectFirst("p")
        val origRange = p.sourceRange()
        assertEquals("2,2:22-2,5:25", origRange.toString())
        val clean = Cleaner(relaxed()).clean(orig)
        val cleanP = clean.expectFirst("p")
        val cleanRange = cleanP.sourceRange()
        assertEquals(cleanRange, origRange)
        assertEquals(clean.endSourceRange(), orig.endSourceRange())
    }
}
