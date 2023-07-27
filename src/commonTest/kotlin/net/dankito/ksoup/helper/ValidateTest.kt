package net.dankito.ksoup.helper

import net.dankito.ksoup.helper.Validate.notNull
import net.dankito.ksoup.helper.Validate.notNullParam
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ValidateTest {

    @Test
    fun testNotNull() {
        notNull("foo")
        var threw = false
        try {
            notNull(null)
        } catch (e: IllegalArgumentException) {
            threw = true
        }
        assertTrue(threw)
    }

//    @Test
//    fun stacktraceFiltersOutValidateClass() {
//        var threw = false
//        try {
//            notNull(null)
//        } catch (e: ValidationException) {
//            threw = true
//            assertEquals("Object must not be null", e.message)
//            val stackTrace = e.stackTrace
//            for (trace in stackTrace) {
//                assertNotEquals(trace.className, Validate::class.java.name)
//            }
//            assertTrue(stackTrace.size >= 1)
//        }
//        assertTrue(threw)
//    }
//
//    @Test
//    fun nonnullParam() {
//        val threw = true
//        try {
//            notNullParam(null, "foo")
//        } catch (e: ValidationException) {
//            assertEquals("The parameter 'foo' must not be null.", e.message)
//        }
//        assertTrue(threw)
//    }
}
