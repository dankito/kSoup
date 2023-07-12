package org.jsoup.parser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.Reader

class HtmlTreeBuilderTest {
    @Test
    fun ensureSearchArraysAreSorted() {
        val constants: List<Array<Any>> = HtmlTreeBuilderStateTest.Companion.findConstantArrays(
            HtmlTreeBuilder::class.java
        )
        HtmlTreeBuilderStateTest.Companion.ensureSorted(constants)
        Assertions.assertEquals(8, constants.size)
    }

    @Test
    fun nonnull() {
        Assertions.assertThrows(NullPointerException::class.java) {
            val treeBuilder = HtmlTreeBuilder()
            treeBuilder.parse(null as Reader, null as String, null as Parser) // not sure how to test that these visual warnings actually appear! - test below checks for method annotation
        } // I'm not convinced that this lambda is easier to read than the old Junit 4 @Test(expected=IEA.class)...
    }
}
