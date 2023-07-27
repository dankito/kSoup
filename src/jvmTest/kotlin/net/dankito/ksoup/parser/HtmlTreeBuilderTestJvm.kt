package net.dankito.ksoup.parser

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HtmlTreeBuilderTestJvm {

    @Test
    fun ensureSearchArraysAreSorted() {
        val constants: List<Array<Any>> = HtmlTreeBuilderStateTestJvm.findConstantArrays(
            HtmlTreeBuilder::class.java
        )
        HtmlTreeBuilderStateTestJvm.ensureSorted(constants)
        assertEquals(8, constants.size)
    }

}