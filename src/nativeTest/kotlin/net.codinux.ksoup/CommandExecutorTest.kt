package net.codinux.ksoup

import kotlin.test.Test
import kotlin.test.assertNotNull


class CommandExecutorTest {

    private val underTest = CommandExecutor()


    @Test
    fun simpleCommand() {
        val result = underTest.executeCommand("ls -la")

        assertNotNull(result)
    }

}