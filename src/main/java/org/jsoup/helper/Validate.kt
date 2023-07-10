package org.jsoup.helper

/**
 * Validators to check that method arguments meet expectations.
 */
object Validate {
    /**
     * Validates that the object is not null
     * @param obj object to test
     * @throws ValidationException if the object is null
     */
    @JvmStatic
    fun notNull(obj: Any?) {
        if (obj == null) throw ValidationException("Object must not be null")
    }

    /**
     * Validates that the parameter is not null
     *
     * @param obj the parameter to test
     * @param param the name of the parameter, for presentation in the validation exception.
     * @throws ValidationException if the object is null
     */
    @JvmStatic
    fun notNullParam(obj: Any?, param: String?) {
        if (obj == null) throw ValidationException(String.format("The parameter '%s' must not be null.", param))
    }

    /**
     * Validates that the object is not null
     * @param obj object to test
     * @param msg message to include in the Exception if validation fails
     * @throws ValidationException if the object is null
     */
    fun notNull(obj: Any?, msg: String?) {
        if (obj == null) throw ValidationException(msg)
    }

    /**
     * Verifies the input object is not null, and returns that object. Effectively this casts a nullable object to a non-
     * null object. (Works around lack of Objects.requestNonNull in Android version.)
     * @param obj nullable object to case to not-null
     * @return the object, or throws an exception if it is null
     * @throws ValidationException if the object is null
     */
    fun ensureNotNull(obj: Any?): Any {
        if (obj == null) throw ValidationException("Object must not be null") else return obj
    }

    /**
     * Verifies the input object is not null, and returns that object. Effectively this casts a nullable object to a non-
     * null object. (Works around lack of Objects.requestNonNull in Android version.)
     * @param obj nullable object to case to not-null
     * @param msg the String format message to include in the validation exception when thrown
     * @param args the arguments to the msg
     * @return the object, or throws an exception if it is null
     * @throws ValidationException if the object is null
     */
    fun ensureNotNull(obj: Any?, msg: String?, vararg args: Any?): Any {
        if (obj == null) throw ValidationException(String.format((msg)!!, *args)) else return obj
    }

    /**
     * Validates that the value is true
     * @param val object to test
     * @throws ValidationException if the object is not true
     */
    fun isTrue(`val`: Boolean) {
        if (!`val`) throw ValidationException("Must be true")
    }

    /**
     * Validates that the value is true
     * @param val object to test
     * @param msg message to include in the Exception if validation fails
     * @throws ValidationException if the object is not true
     */
    fun isTrue(`val`: Boolean, msg: String?) {
        if (!`val`) throw ValidationException(msg)
    }

    /**
     * Validates that the value is false
     * @param val object to test
     * @throws ValidationException if the object is not false
     */
    fun isFalse(`val`: Boolean) {
        if (`val`) throw ValidationException("Must be false")
    }

    /**
     * Validates that the value is false
     * @param val object to test
     * @param msg message to include in the Exception if validation fails
     * @throws ValidationException if the object is not false
     */
    fun isFalse(`val`: Boolean, msg: String?) {
        if (`val`) throw ValidationException(msg)
    }
    /**
     * Validates that the array contains no null elements
     * @param objects the array to test
     * @param msg message to include in the Exception if validation fails
     * @throws ValidationException if the array contains a null element
     */
    /**
     * Validates that the array contains no null elements
     * @param objects the array to test
     * @throws ValidationException if the array contains a null element
     */
    @JvmOverloads
    fun noNullElements(objects: Array<Any?>, msg: String? = "Array must not contain any null objects") {
        for (obj: Any? in objects) if (obj == null) throw ValidationException(msg)
    }

    /**
     * Validates that the string is not null and is not empty
     * @param string the string to test
     * @throws ValidationException if the string is null or empty
     */
    fun notEmpty(string: String?) {
        if (string == null || string.length == 0) throw ValidationException("String must not be empty")
    }

    /**
     * Validates that the string parameter is not null and is not empty
     * @param string the string to test
     * @param param the name of the parameter, for presentation in the validation exception.
     * @throws ValidationException if the string is null or empty
     */
    fun notEmptyParam(string: String?, param: String?) {
        if (string == null || string.length == 0) throw ValidationException(
            String.format(
                "The '%s' parameter must not be empty.",
                param
            )
        )
    }

    /**
     * Validates that the string is not null and is not empty
     * @param string the string to test
     * @param msg message to include in the Exception if validation fails
     * @throws ValidationException if the string is null or empty
     */
    fun notEmpty(string: String?, msg: String?) {
        if (string == null || string.length == 0) throw ValidationException(msg)
    }

    /**
     * Blow up if we reach an unexpected state.
     * @param msg message to think about
     * @throws IllegalStateException if we reach this state
     */
    fun wtf(msg: String?) {
        throw IllegalStateException(msg)
    }

    /**
     * Cause a failure.
     * @param msg message to output.
     * @throws IllegalStateException if we reach this state
     */
    fun fail(msg: String?) {
        throw ValidationException(msg)
    }

    /**
     * Cause a failure, but return false so it can be used in an assert statement.
     * @param msg message to output.
     * @return false, always
     * @throws IllegalStateException if we reach this state
     */
    fun assertFail(msg: String?): Boolean {
        fail(msg)
        return false
    }

    /**
     * Cause a failure.
     * @param msg message to output.
     * @param args the format arguments to the msg
     * @throws IllegalStateException if we reach this state
     */
    fun fail(msg: String?, vararg args: Any?) {
        throw ValidationException(String.format((msg)!!, *args))
    }
}
