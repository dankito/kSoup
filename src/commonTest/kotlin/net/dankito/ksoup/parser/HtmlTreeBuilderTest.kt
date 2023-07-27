package net.dankito.ksoup.parser

import net.dankito.ksoup.jvm.Reader
import net.dankito.ksoup.test.NullPointerExceptionOfPlatform
import kotlin.test.Test
import kotlin.test.assertFailsWith

class HtmlTreeBuilderTest {

    @Test
    fun nonnull() {
        assertFailsWith(NullPointerExceptionOfPlatform) {
            val treeBuilder = HtmlTreeBuilder()
            treeBuilder.parse(null as Reader, null as String, null as Parser) // not sure how to test that these visual warnings actually appear! - test below checks for method annotation
        } // I'm not convinced that this lambda is easier to read than the old Junit 4 @Test(expected=IEA.class)...
    }
}
