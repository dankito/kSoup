package net.dankito.ksoup.parser

import net.dankito.ksoup.UncheckedIOException
import net.dankito.ksoup.integration.ParseTest
import net.dankito.ksoup.jvm.StringReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*

/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class CharacterReaderTest {
    @Test
    fun consume() {
        val r = CharacterReader("one")
        Assertions.assertEquals(0, r.pos())
        Assertions.assertEquals('o', r.current())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.current())
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
    }

    @Test
    fun unconsume() {
        val r = CharacterReader("one")
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('n', r.current())
        r.unconsume()
        Assertions.assertEquals('o', r.current())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        r.unconsume()
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals('e', r.current())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
        r.unconsume() // read past, so have to eat again
        Assertions.assertTrue(r.isEmpty)
        r.unconsume()
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
        Assertions.assertTrue(r.isEmpty)

        // unconsume all remaining characters
        for (i in 0..3) {
            r.unconsume()
        }
        Assertions.assertThrows(UncheckedIOException::class.java) { r.unconsume() }
    }

    @Test
    fun mark() {
        val r = CharacterReader("one")
        r.consume()
        r.mark()
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        r.rewindToMark()
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals(2, r.pos())
    }

    @Test
    fun rewindToMark() {
        val r = CharacterReader("nothing")
        // marking should be invalid
        Assertions.assertThrows(UncheckedIOException::class.java) { r.rewindToMark() }
    }

    @Test
    fun consumeToEnd() {
        val `in` = "one two three"
        val r = CharacterReader(`in`)
        val toEnd = r.consumeToEnd()
        Assertions.assertEquals(`in`, toEnd)
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun nextIndexOfChar() {
        val `in` = "blah blah"
        val r = CharacterReader(`in`)
        Assertions.assertEquals(-1, r.nextIndexOf('x'))
        Assertions.assertEquals(3, r.nextIndexOf('h'))
        val pull = r.consumeTo('h')
        Assertions.assertEquals("bla", pull)
        r.consume()
        Assertions.assertEquals(2, r.nextIndexOf('l'))
        Assertions.assertEquals(" blah", r.consumeToEnd())
        Assertions.assertEquals(-1, r.nextIndexOf('x'))
    }

    @Test
    fun nextIndexOfString() {
        val `in` = "One Two something Two Three Four"
        val r = CharacterReader(`in`)
        Assertions.assertEquals(-1, r.nextIndexOf("Foo"))
        Assertions.assertEquals(4, r.nextIndexOf("Two"))
        Assertions.assertEquals("One Two ", r.consumeTo("something"))
        Assertions.assertEquals(10, r.nextIndexOf("Two"))
        Assertions.assertEquals("something Two Three Four", r.consumeToEnd())
        Assertions.assertEquals(-1, r.nextIndexOf("Two"))
    }

    @Test
    fun nextIndexOfUnmatched() {
        val r = CharacterReader("<[[one]]")
        Assertions.assertEquals(-1, r.nextIndexOf("]]>"))
    }

    @Test
    fun consumeToChar() {
        val r = CharacterReader("One Two Three")
        Assertions.assertEquals("One ", r.consumeTo('T'))
        Assertions.assertEquals("", r.consumeTo('T')) // on Two
        Assertions.assertEquals('T', r.consume())
        Assertions.assertEquals("wo ", r.consumeTo('T'))
        Assertions.assertEquals('T', r.consume())
        Assertions.assertEquals("hree", r.consumeTo('T')) // consume to end
    }

    @Test
    fun consumeToString() {
        val r = CharacterReader("One Two Two Four")
        Assertions.assertEquals("One ", r.consumeTo("Two"))
        Assertions.assertEquals('T', r.consume())
        Assertions.assertEquals("wo ", r.consumeTo("Two"))
        Assertions.assertEquals('T', r.consume())
        // To handle strings straddling across buffers, consumeTo() may return the
        // data in multiple pieces near EOF.
        val builder = StringBuilder()
        var part: String
        do {
            part = r.consumeTo("Qux")
            builder.append(part)
        } while (!part.isEmpty())
        Assertions.assertEquals("wo Four", builder.toString())
    }

    @Test
    fun advance() {
        val r = CharacterReader("One Two Three")
        Assertions.assertEquals('O', r.consume())
        r.advance()
        Assertions.assertEquals('e', r.consume())
    }

    @Test
    fun consumeToAny() {
        val r = CharacterReader("One &bar; qux")
        Assertions.assertEquals("One ", r.consumeToAny('&', ';'))
        Assertions.assertTrue(r.matches('&'))
        Assertions.assertTrue(r.matches("&bar;"))
        Assertions.assertEquals('&', r.consume())
        Assertions.assertEquals("bar", r.consumeToAny('&', ';'))
        Assertions.assertEquals(';', r.consume())
        Assertions.assertEquals(" qux", r.consumeToAny('&', ';'))
    }

    @Test
    fun consumeLetterSequence() {
        val r = CharacterReader("One &bar; qux")
        Assertions.assertEquals("One", r.consumeLetterSequence())
        Assertions.assertEquals(" &", r.consumeTo("bar;"))
        Assertions.assertEquals("bar", r.consumeLetterSequence())
        Assertions.assertEquals("; qux", r.consumeToEnd())
    }

    @Test
    fun consumeLetterThenDigitSequence() {
        val r = CharacterReader("One12 Two &bar; qux")
        Assertions.assertEquals("One12", r.consumeLetterThenDigitSequence())
        Assertions.assertEquals(' ', r.consume())
        Assertions.assertEquals("Two", r.consumeLetterThenDigitSequence())
        Assertions.assertEquals(" &bar; qux", r.consumeToEnd())
    }

    @Test
    fun matches() {
        val r = CharacterReader("One Two Three")
        Assertions.assertTrue(r.matches('O'))
        Assertions.assertTrue(r.matches("One Two Three"))
        Assertions.assertTrue(r.matches("One"))
        Assertions.assertFalse(r.matches("one"))
        Assertions.assertEquals('O', r.consume())
        Assertions.assertFalse(r.matches("One"))
        Assertions.assertTrue(r.matches("ne Two Three"))
        Assertions.assertFalse(r.matches("ne Two Three Four"))
        Assertions.assertEquals("ne Two Three", r.consumeToEnd())
        Assertions.assertFalse(r.matches("ne"))
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun matchesIgnoreCase() {
        val r = CharacterReader("One Two Three")
        Assertions.assertTrue(r.matchesIgnoreCase("O"))
        Assertions.assertTrue(r.matchesIgnoreCase("o"))
        Assertions.assertTrue(r.matches('O'))
        Assertions.assertFalse(r.matches('o'))
        Assertions.assertTrue(r.matchesIgnoreCase("One Two Three"))
        Assertions.assertTrue(r.matchesIgnoreCase("ONE two THREE"))
        Assertions.assertTrue(r.matchesIgnoreCase("One"))
        Assertions.assertTrue(r.matchesIgnoreCase("one"))
        Assertions.assertEquals('O', r.consume())
        Assertions.assertFalse(r.matchesIgnoreCase("One"))
        Assertions.assertTrue(r.matchesIgnoreCase("NE Two Three"))
        Assertions.assertFalse(r.matchesIgnoreCase("ne Two Three Four"))
        Assertions.assertEquals("ne Two Three", r.consumeToEnd())
        Assertions.assertFalse(r.matchesIgnoreCase("ne"))
    }

    @Test
    fun containsIgnoreCase() {
        val r = CharacterReader("One TWO three")
        Assertions.assertTrue(r.containsIgnoreCase("two"))
        Assertions.assertTrue(r.containsIgnoreCase("three"))
        // weird one: does not find one, because it scans for consistent case only
        Assertions.assertFalse(r.containsIgnoreCase("one"))
    }

    @Test
    fun containsIgnoreCaseBuffer() {
        val html =
            "<p><p><p></title><p></TITLE><p>" + CharacterReaderTest.Companion.BufferBuster("Foo Bar Qux ") + "<foo><bar></title>"
        val r = CharacterReader(html)
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>")) // cached, but we only test functionally here
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        r.consumeTo("</title>")
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        r.consumeTo("<p>")
        Assertions.assertTrue(r.matches("<p>"))
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>"))
        r.consumeTo("</TITLE>")
        r.consumeTo("<p>")
        Assertions.assertTrue(r.matches("<p>"))
        Assertions.assertFalse(r.containsIgnoreCase("</title>")) // because we haven't buffered up yet, we don't know
        r.consumeTo("<foo>")
        Assertions.assertFalse(r.matches("<foo>")) // buffer underrun
        r.consumeTo("<foo>")
        Assertions.assertTrue(r.matches("<foo>")) // cross the buffer
        Assertions.assertTrue(r.containsIgnoreCase("</TITLE>"))
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
    }

    @Test
    fun matchesAny() {
        val scan = charArrayOf(' ', '\n', '\t')
        val r = CharacterReader("One\nTwo\tThree")
        Assertions.assertFalse(r.matchesAny(*scan))
        Assertions.assertEquals("One", r.consumeToAny(*scan))
        Assertions.assertTrue(r.matchesAny(*scan))
        Assertions.assertEquals('\n', r.consume())
        Assertions.assertFalse(r.matchesAny(*scan))
        // nothing to match
        r.consumeToEnd()
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertFalse(r.matchesAny(*scan))
    }

    @Test
    fun matchesDigit() {
        val r = CharacterReader("42")
        r.consumeToEnd()
        Assertions.assertTrue(r.isEmpty)
        // nothing to match
        Assertions.assertFalse(r.matchesDigit())
        r.unconsume()
        Assertions.assertTrue(r.matchesDigit())
    }

    @Test
    fun cachesStrings() {
        val r = CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars")
        val one = r.consumeTo('\t')
        r.consume()
        val two = r.consumeTo('\t')
        r.consume()
        val three = r.consumeTo('\t')
        r.consume()
        val four = r.consumeTo('\t')
        r.consume()
        val five = r.consumeTo('\t')
        Assertions.assertEquals("Check", one)
        Assertions.assertEquals("Check", two)
        Assertions.assertEquals("Check", three)
        Assertions.assertEquals("CHOKE", four)
        Assertions.assertSame(one, two)
        Assertions.assertSame(two, three)
        Assertions.assertNotSame(three, four)
        Assertions.assertNotSame(four, five)
        Assertions.assertEquals(five, "A string that is longer than 16 chars")
    }

    @Test
    fun rangeEquals() {
        val r = CharacterReader("Check\tCheck\tCheck\tCHOKE")
        Assertions.assertTrue(r.rangeEquals(0, 5, "Check"))
        Assertions.assertFalse(r.rangeEquals(0, 5, "CHOKE"))
        Assertions.assertFalse(r.rangeEquals(0, 5, "Chec"))
        Assertions.assertTrue(r.rangeEquals(6, 5, "Check"))
        Assertions.assertFalse(r.rangeEquals(6, 5, "Chuck"))
        Assertions.assertTrue(r.rangeEquals(12, 5, "Check"))
        Assertions.assertFalse(r.rangeEquals(12, 5, "Cheeky"))
        Assertions.assertTrue(r.rangeEquals(18, 5, "CHOKE"))
        Assertions.assertFalse(r.rangeEquals(18, 5, "CHIKE"))
    }

    @Test
    fun empty() {
        var r = CharacterReader("One")
        Assertions.assertTrue(r.matchConsume("One"))
        Assertions.assertTrue(r.isEmpty)
        r = CharacterReader("Two")
        val two = r.consumeToEnd()
        Assertions.assertEquals("Two", two)
    }

    @Test
    fun consumeToNonexistentEndWhenAtAnd() {
        val r = CharacterReader("<!")
        Assertions.assertTrue(r.matchConsume("<!"))
        Assertions.assertTrue(r.isEmpty)
        val after = r.consumeTo('>')
        Assertions.assertEquals("", after)
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun notEmptyAtBufferSplitPoint() {
        val r = CharacterReader(StringReader("How about now"), 3)
        Assertions.assertEquals("How", r.consumeTo(' '))
        Assertions.assertFalse(r.isEmpty, "Should not be empty")
        Assertions.assertEquals(' ', r.consume())
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals(4, r.pos())
        Assertions.assertEquals('a', r.consume())
        Assertions.assertEquals(5, r.pos())
        Assertions.assertEquals('b', r.consume())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('u', r.consume())
        Assertions.assertEquals('t', r.consume())
        Assertions.assertEquals(' ', r.consume())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('w', r.consume())
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun bufferUp() {
        val note = "HelloThere" // + ! = 11 chars
        val loopCount = 64
        val sb = StringBuilder()
        for (i in 0 until loopCount) {
            sb.append(note)
            sb.append("!")
        }
        val s = sb.toString()
        val r = CharacterReader(StringReader(s))
        for (i in 0 until loopCount) {
            val pull = r.consumeTo('!')
            Assertions.assertEquals(note, pull)
            Assertions.assertEquals('!', r.current())
            r.advance()
        }
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun canEnableAndDisableLineNumberTracking() {
        val reader = CharacterReader("Hello!")
        Assertions.assertFalse(reader.isTrackNewlines)
        reader.trackNewlines(true)
        Assertions.assertTrue(reader.isTrackNewlines)
        reader.trackNewlines(false)
        Assertions.assertFalse(reader.isTrackNewlines)
    }

    @Test
    fun canTrackNewlines() {
        val builder = StringBuilder()
        builder.append("<foo>\n<bar>\n<qux>\n")
        while (builder.length < CharacterReaderTest.Companion.maxBufferLen) builder.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        builder.append("[foo]\n[bar]")
        val content = builder.toString()
        val noTrack = CharacterReader(content)
        Assertions.assertFalse(noTrack.isTrackNewlines)
        val track = CharacterReader(content)
        track.trackNewlines(true)
        Assertions.assertTrue(track.isTrackNewlines)

        // check that no tracking works as expected (pos is 0 indexed, line number stays at 1, col is pos+1)
        Assertions.assertEquals(0, noTrack.pos())
        Assertions.assertEquals(1, noTrack.lineNumber())
        Assertions.assertEquals(1, noTrack.columnNumber())
        noTrack.consumeTo("<qux>")
        Assertions.assertEquals(12, noTrack.pos())
        Assertions.assertEquals(1, noTrack.lineNumber())
        Assertions.assertEquals(13, noTrack.columnNumber())
        Assertions.assertEquals("1:13", noTrack.cursorPos())
        // get over the buffer
        while (!noTrack.matches("[foo]")) noTrack.consumeTo("[foo]")
        Assertions.assertEquals(32778, noTrack.pos())
        Assertions.assertEquals(1, noTrack.lineNumber())
        Assertions.assertEquals(noTrack.pos() + 1, noTrack.columnNumber())
        Assertions.assertEquals("1:32779", noTrack.cursorPos())

        // and the line numbers: "<foo>\n<bar>\n<qux>\n"
        Assertions.assertEquals(0, track.pos())
        Assertions.assertEquals(1, track.lineNumber())
        Assertions.assertEquals(1, track.columnNumber())
        track.consumeTo('\n')
        Assertions.assertEquals(1, track.lineNumber())
        Assertions.assertEquals(6, track.columnNumber())
        track.consume()
        Assertions.assertEquals(2, track.lineNumber())
        Assertions.assertEquals(1, track.columnNumber())
        Assertions.assertEquals("<bar>", track.consumeTo('\n'))
        Assertions.assertEquals(2, track.lineNumber())
        Assertions.assertEquals(6, track.columnNumber())
        Assertions.assertEquals("\n", track.consumeTo("<qux>"))
        Assertions.assertEquals(12, track.pos())
        Assertions.assertEquals(3, track.lineNumber())
        Assertions.assertEquals(1, track.columnNumber())
        Assertions.assertEquals("3:1", track.cursorPos())
        Assertions.assertEquals("<qux>", track.consumeTo('\n'))
        Assertions.assertEquals("3:6", track.cursorPos())
        // get over the buffer
        while (!track.matches("[foo]")) track.consumeTo("[foo]")
        Assertions.assertEquals(32778, track.pos())
        Assertions.assertEquals(4, track.lineNumber())
        Assertions.assertEquals(32761, track.columnNumber())
        Assertions.assertEquals("4:32761", track.cursorPos())
        track.consumeTo('\n')
        Assertions.assertEquals("4:32766", track.cursorPos())
        track.consumeTo("[bar]")
        Assertions.assertEquals(5, track.lineNumber())
        Assertions.assertEquals("5:1", track.cursorPos())
        track.consumeToEnd()
        Assertions.assertEquals("5:6", track.cursorPos())
    }

    @Test
    fun countsColumnsOverBufferWhenNoNewlines() {
        val builder = StringBuilder()
        while (builder.length < CharacterReaderTest.Companion.maxBufferLen * 4) builder.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        val content = builder.toString()
        val reader = CharacterReader(content)
        reader.trackNewlines(true)
        Assertions.assertEquals("1:1", reader.cursorPos())
        while (!reader.isEmpty) reader.consume()
        Assertions.assertEquals(131096, reader.pos())
        Assertions.assertEquals(reader.pos() + 1, reader.columnNumber())
        Assertions.assertEquals(1, reader.lineNumber())
    }

    @Test
    @Throws(IOException::class)
    fun linenumbersAgreeWithEditor() {
        val content: String = ParseTest.Companion.getFileAsString(ParseTest.Companion.getFile("/htmltests/large.html"))
        val reader = CharacterReader(content)
        reader.trackNewlines(true)
        val scan = "<p>VESTIBULUM" // near the end of the file
        while (!reader.matches(scan)) reader.consumeTo(scan)
        Assertions.assertEquals(280218, reader.pos())
        Assertions.assertEquals(1002, reader.lineNumber())
        Assertions.assertEquals(1, reader.columnNumber())
        reader.consumeTo(' ')
        Assertions.assertEquals(1002, reader.lineNumber())
        Assertions.assertEquals(14, reader.columnNumber())
    }

    companion object {
        const val maxBufferLen = CharacterReader.maxBufferLen
        fun BufferBuster(content: String?): String {
            val builder = StringBuilder()
            while (builder.length < CharacterReaderTest.Companion.maxBufferLen) builder.append(content)
            return builder.toString()
        }
    }
}
