package net.dankito.ksoup.select

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.MultiLocaleTest
import java.util.*
import kotlin.test.assertEquals

class SelectorTestJvm {

    @MultiLocaleTest
    fun testByAttribute(locale: Locale?) {
        Locale.setDefault(locale)
        val h = "<div Title=Foo /><div Title=Bar /><div Style=Qux /><div title=Balim /><div title=SLIM />" +
                "<div data-name='with spaces'/>"
        val doc = Jsoup.parse(h)
        val withTitle = doc.select("[title]")
        assertEquals(4, withTitle.size)
        val foo = doc.select("[TITLE=foo]")
        assertEquals(1, foo.size)
        val foo2 = doc.select("[title=\"foo\"]")
        assertEquals(1, foo2.size)
        val foo3 = doc.select("[title=\"Foo\"]")
        assertEquals(1, foo3.size)
        val dataName = doc.select("[data-name=\"with spaces\"]")
        assertEquals(1, dataName.size)
        assertEquals("with spaces", dataName.first()!!.attr("data-name"))
        val not = doc.select("div[title!=bar]")
        assertEquals(5, not.size)
        assertEquals("Foo", not.first()!!.attr("title"))
        val starts = doc.select("[title^=ba]")
        assertEquals(2, starts.size)
        assertEquals("Bar", starts.first()!!.attr("title"))
        assertEquals("Balim", starts.last()!!.attr("title"))
        val ends = doc.select("[title$=im]")
        assertEquals(2, ends.size)
        assertEquals("Balim", ends.first()!!.attr("title"))
        assertEquals("SLIM", ends.last()!!.attr("title"))
        val contains = doc.select("[title*=i]")
        assertEquals(2, contains.size)
        assertEquals("Balim", contains.first()!!.attr("title"))
        assertEquals("SLIM", contains.last()!!.attr("title"))
    }

    @MultiLocaleTest
    fun testByAttributeStarting(locale: Locale?) {
        Locale.setDefault(locale)
        val doc = Jsoup.parse("<div id=1 ATTRIBUTE data-name=jsoup>Hello</div><p data-val=5 id=2>There</p><p id=3>No</p>")
        var withData = doc.select("[^data-]")
        assertEquals(2, withData.size)
        assertEquals("1", withData.first()!!.id())
        assertEquals("2", withData.last()!!.id())
        withData = doc.select("p[^data-]")
        assertEquals(1, withData.size)
        assertEquals("2", withData.first()!!.id())
        assertEquals(1, doc.select("[^attrib]").size)
    }

    @MultiLocaleTest
    fun testPseudoContains(locale: Locale?) {
        Locale.setDefault(locale)
        val doc = Jsoup.parse("<div><p>The Rain.</p> <p class=light>The <i>RAIN</i>.</p> <p>Rain, the.</p></div>")
        val ps1 = doc.select("p:contains(Rain)")
        assertEquals(3, ps1.size)
        val ps2 = doc.select("p:contains(the rain)")
        assertEquals(2, ps2.size)
        assertEquals("The Rain.", ps2.first()!!.html())
        assertEquals("The <i>RAIN</i>.", ps2.last()!!.html())
        val ps3 = doc.select("p:contains(the Rain):has(i)")
        assertEquals(1, ps3.size)
        assertEquals("light", ps3.first()!!.className())
        val ps4 = doc.select(".light:contains(rain)")
        assertEquals(1, ps4.size)
        assertEquals("light", ps3.first()!!.className())
        val ps5 = doc.select(":contains(rain)")
        assertEquals(8, ps5.size) // html, body, div,...
        val ps6 = doc.select(":contains(RAIN)")
        assertEquals(8, ps6.size)
    }

    @MultiLocaleTest
    fun containsOwn(locale: Locale?) {
        Locale.setDefault(locale)
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b> igor</p>")
        val ps = doc.select("p:containsOwn(Hello IGOR)")
        assertEquals(1, ps.size)
        assertEquals("1", ps.first()!!.id())
        assertEquals(0, doc.select("p:containsOwn(there)").size)
        val doc2 = Jsoup.parse("<p>Hello <b>there</b> IGOR</p>")
        assertEquals(1, doc2.select("p:containsOwn(igor)").size)
    }

    @MultiLocaleTest
    fun containsData(locale: Locale?) {
        Locale.setDefault(locale)
        val html = "<p>function</p><script>FUNCTION</script><style>item</style><span><!-- comments --></span>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val dataEls1 = body.select(":containsData(function)")
        val dataEls2 = body.select("script:containsData(function)")
        val dataEls3 = body.select("span:containsData(comments)")
        val dataEls4 = body.select(":containsData(o)")
        val dataEls5 = body.select("style:containsData(ITEM)")
        assertEquals(2, dataEls1.size) // body and script
        assertEquals(1, dataEls2.size)
        assertEquals(dataEls1.last(), dataEls2.first())
        assertEquals("<script>FUNCTION</script>", dataEls2.outerHtml())
        assertEquals(1, dataEls3.size)
        assertEquals("span", dataEls3.first()!!.tagName())
        assertEquals(3, dataEls4.size)
        assertEquals("body", dataEls4.first()!!.tagName())
        assertEquals("script", dataEls4[1].tagName())
        assertEquals("span", dataEls4[2].tagName())
        assertEquals(1, dataEls5.size)
    }

}