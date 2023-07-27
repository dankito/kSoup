package net.dankito.ksoup.parser

import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import kotlin.test.assertEquals

class TokeniserTestJvm {

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

}