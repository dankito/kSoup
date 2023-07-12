package net.dankito.ksoup.helper

import net.dankito.ksoup.helper.Validate.notNull
import net.dankito.ksoup.helper.Validate.notNullParam
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
        Assertions.assertTrue(threw)
    }

    @Test
    fun stacktraceFiltersOutValidateClass() {
        var threw = false
        try {
            notNull(null)
        } catch (e: ValidationException) {
            threw = true
            Assertions.assertEquals("Object must not be null", e.message)
            val stackTrace = e.stackTrace
            for (trace in stackTrace) {
                Assertions.assertNotEquals(trace.className, Validate::class.java.name)
            }
            Assertions.assertTrue(stackTrace.size >= 1)
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun nonnullParam() {
        val threw = true
        try {
            notNullParam(null, "foo")
        } catch (e: ValidationException) {
            Assertions.assertEquals("The parameter 'foo' must not be null.", e.message)
        }
        Assertions.assertTrue(threw)
    }
}
