package net.dankito.ksoup.parser

import net.dankito.ksoup.integration.ParseTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterReaderTestJvm {

    @Test
    fun linenumbersAgreeWithEditor() {
        val content: String = ParseTest.getFileAsString(ParseTest.getFile("/htmltests/large.html"))
        val reader = CharacterReader(content)
        reader.trackNewlines(true)
        val scan = "<p>VESTIBULUM" // near the end of the file
        while (!reader.matches(scan)) reader.consumeTo(scan)
        assertEquals(280218, reader.pos())
        assertEquals(1002, reader.lineNumber())
        assertEquals(1, reader.columnNumber())
        reader.consumeTo(' ')
        assertEquals(1002, reader.lineNumber())
        assertEquals(14, reader.columnNumber())
    }

}