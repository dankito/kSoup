package net.codinux.ksoup

import kotlin.test.Test
import kotlin.test.assertEquals


class kSoupExecutorTest {

    private val underTest = kSoupExecutor()


    @Test
    fun selectTitleElement() {
        val result = underTest.select("<html><head><title>Simple example</title></head></html>", "head > title")

        assertEquals(1, result.elements.size)
        assertEquals("<title>Simple example</title>", result.elements.first().outerHtml)
    }

}