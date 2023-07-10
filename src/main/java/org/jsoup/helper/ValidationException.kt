package org.jsoup.helper

/**
 * Validation exceptions, as thrown by the methods in [Validate].
 */
class ValidationException(msg: String?) : IllegalArgumentException(msg) {
    @Synchronized
    public override fun fillInStackTrace(): Throwable {
        // Filters out the Validate class from the stacktrace, to more clearly point at the root-cause.
        super.fillInStackTrace()
        val stackTrace: Array<StackTraceElement> = getStackTrace()
        val filteredTrace: MutableList<StackTraceElement> = ArrayList()
        for (trace: StackTraceElement in stackTrace) {
            if ((trace.getClassName() == Validator)) continue
            filteredTrace.add(trace)
        }
        setStackTrace(filteredTrace.toTypedArray<StackTraceElement>())
        return this
    }

    companion object {
        val Validator: String = Validate::class.java.getName()
    }
}
