package net.dankito.ksoup.nodes

//import net.dankito.ksoup.Jsoup
//import net.dankito.ksoup.integration.servlets.FileServlet
//import net.dankito.ksoup.parser.Parser
//import org.junit.jupiter.api.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull

class PositionTestJvm {

//    @Test
//    fun tracksFromFetch() {
//        val url: String = FileServlet.urlTo("/htmltests/large.html") // 280 K
//        val doc = Jsoup.connect(url).parser(PositionTest.TrackingParser).get()
//        val firstP = doc.expectFirst("p")
//        assertNotNull(firstP)
//        assertEquals("4,1:53-4,4:56", firstP.sourceRange().toString())
//        val p = doc.expectFirst("#xy")
//        assertNotNull(p)
//        assertEquals("1000,1:279646-1000,10:279655", p.sourceRange().toString())
//        assertEquals("1000,567:280212-1000,571:280216", p.endSourceRange().toString())
//        val text = p.firstChild() as TextNode?
//        assertNotNull(text)
//        assertEquals("1000,10:279655-1000,357:280002", text!!.sourceRange().toString())
//    }
//
//    @Test
//    fun tracksFromXmlFetch() {
//        val url: String = FileServlet.urlTo("/htmltests/test-rss.xml")
//        val doc = Jsoup.connect(url).parser(Parser.xmlParser().setTrackPosition(true)).get()
//        val item = doc.expectFirst("item + item")
//        assertNotNull(item)
//        assertEquals("13,5:496-13,11:502", item.sourceRange().toString())
//        assertEquals("17,5:779-17,12:786", item.endSourceRange().toString())
//    }

}