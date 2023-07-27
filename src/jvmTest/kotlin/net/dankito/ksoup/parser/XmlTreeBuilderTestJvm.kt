package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.TextUtil
import net.dankito.ksoup.parse
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import kotlin.test.assertEquals

class XmlTreeBuilderTestJvm {

//    @Disabled
//    @Test
//    fun testSupplyParserToConnection() {
//        val xmlUrl = "http://direct.infohound.net/tools/jsoup-xml-test.xml"
//
//        // parse with both xml and html parser, ensure different
//        val xmlDoc = Jsoup.connect(xmlUrl).parser(Parser.xmlParser()).get()
//        val htmlDoc = Jsoup.connect(xmlUrl).parser(Parser.htmlParser()).get()
//        val autoXmlDoc = Jsoup.connect(xmlUrl).get() // check connection auto detects xml, uses xml parser
//        assertEquals(
//            "<doc><val>One<val>Two</val>Three</val></doc>",
//            TextUtil.stripNewlines(xmlDoc.html())
//        )
//        assertNotEquals(htmlDoc, xmlDoc)
//        assertEquals(xmlDoc, autoXmlDoc)
//        assertEquals(1, htmlDoc.select("head").size) // html parser normalises
//        assertEquals(0, xmlDoc.select("head").size) // xml parser does not
//        assertEquals(0, autoXmlDoc.select("head").size) // xml parser does not
//    }

    @Test
    fun testSupplyParserToDataStream() {
        val xmlFile = File(XmlTreeBuilder::class.java.getResource("/htmltests/xml-test.xml").toURI())
        val inStream: InputStream = FileInputStream(xmlFile)
        val doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser())
        assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testDetectCharsetEncodingDeclaration() {
        val xmlFile = File(XmlTreeBuilder::class.java.getResource("/htmltests/xml-charset.xml").toURI())
        val inStream: InputStream = FileInputStream(xmlFile)
        val doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser())
        assertEquals("ISO-8859-1", doc.charset()!!.name)
        assertEquals(
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><data>äöåéü</data>",
            TextUtil.stripNewlines(doc.html())
        )
    }

}