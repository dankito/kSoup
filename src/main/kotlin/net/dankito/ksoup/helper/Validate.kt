package net.dankito.ksoup.helper

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Simple validation methods. Designed for jsoup internal use
 */
@OptIn(ExperimentalContracts::class)
object Validate {
    /**
     * Validates that the object is not null
     * @param obj object to test
     */
    @JvmStatic
    fun notNull(obj: Any?) {
        contract {
            returns() implies (obj != null)
        }

        if (obj == null) {
            throw ValidationException("Object must not be null")
        }
    }

    /**
     * Validates that the parameter is not null
     *
     * @param obj the parameter to test
     * @param param the name of the parameter, for presentation in the validation exception.
     * @throws ValidationException if the object is null
     */
    @JvmStatic
    fun notNullParam(obj: Any?, param: String) {
        contract {
            returns() implies (obj != null)
        }

        if (obj == null) {
            throw ValidationException("The parameter '$param' must not be null.")
        }
    }

    /**
     * Validates that the object is not null
     * @param obj object to test
     * @param msg message to output if validation fails
     */
    fun notNull(obj: Any?, msg: String) {
        contract {
            returns() implies (obj != null)
        }

        if (obj == null) {
            throw ValidationException(msg)
        }
    }

    /**
     * Verifies the input object is not null, and returns that object. Effectively this casts a nullable object to a non-
     * null object. (Works around lack of Objects.requestNonNull in Android version.)
     * @param obj nullable object to case to not-null
     * @return the object, or throws an exception if it is null
     * @throws ValidationException if the object is null
     */
    fun <T> ensureNotNull(obj: T?): T {
        contract {
            returns() implies (obj != null)
        }

        return ensureNotNull(obj, "Object must not be null")
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
    fun <T> ensureNotNull(obj: T?, msg: String): T {
        contract {
            returns() implies (obj != null)
        }

        return obj ?: throw ValidationException(msg)
    }

    /**
     * Validates that the value is true
     * @param val object to test
     */
    fun isTrue(`val`: Boolean) {
        isTrue(`val`, "Must be true")
    }

    /**
     * Validates that the value is true
     * @param val object to test
     * @param msg message to output if validation fails
     */
    fun isTrue(`val`: Boolean, msg: String) {
        if (!`val`) {
            throw ValidationException(msg)
        }
    }

    /**
     * Validates that the value is false
     * @param val object to test
     */
    fun isFalse(`val`: Boolean) {
        isFalse(`val`, "Must be false")
    }

    /**
     * Validates that the value is false
     * @param val object to test
     * @param msg message to output if validation fails
     */
    fun isFalse(`val`: Boolean, msg: String) {
        if (`val`) {
            throw ValidationException(msg)
        }
    }
    /**
     * Validates that the array contains no null elements
     * @param objects the array to test
     * @param msg message to output if validation fails
     */
    /**
     * Validates that the array contains no null elements
     * @param objects the array to test
     */
    @JvmOverloads
    fun <T : Any?> noNullElements(objects: Array<T>, msg: String = "Array must not contain any null objects") {
        for (obj in objects) {
            if (obj == null) {
                throw ValidationException(msg)
            }
        }
    }

    /**
     * Validates that the string is not null and is not empty
     * @param string the string to test
     */
    fun notEmpty(string: String?) {
        contract {
            returns() implies (string != null)
        }

        if (string.isNullOrEmpty()) {
            throw ValidationException("String must not be empty")
        }
    }

    /**
     * Validates that the string parameter is not null and is not empty
     * @param string the string to test
     * @param param the name of the parameter, for presentation in the validation exception.
     * @throws ValidationException if the string is null or empty
     */
    fun notEmptyParam(string: String?, param: String?) {
        contract {
            returns() implies (string != null)
        }

        if (string.isNullOrEmpty()) {
            throw ValidationException("The '$param' parameter must not be empty.")
        }
    }

    /**
     * Validates that the string is not null and is not empty
     * @param string the string to test
     * @param msg message to output if validation fails
     */
    fun notEmpty(string: String?, msg: String) {
        contract {
            returns() implies (string != null)
        }

        if (string.isNullOrEmpty()) {
            throw ValidationException(msg)
        }
    }

    /**
     * Blow up if we reach an unexpected state.
     * @param msg message to think about
     */
    fun wtf(msg: String?) {
        throw IllegalStateException(msg)
    }

    /**
     * Cause a failure.
     * @param msg message to output.
     */
    fun fail(msg: String) {
        throw ValidationException(msg)
    }

    /**
     * Cause a failure, but return false so it can be used in an assert statement.
     * @param msg message to output.
     * @return false, always
     * @throws IllegalStateException if we reach this state
     */
    fun assertFail(msg: String): Boolean {
        fail(msg)
        return false
    }
}