package net.dankito.ksoup

import kotlin.test.assertEquals
import kotlin.test.fail

object Assertions {

    fun <T> assertElementsEqual(list1: List<T>, list2: List<T>) {
        assertEquals(list1.size, list2.size, "List 1 (${list1.size}) should have the same size as List 2 (${list2.size})")

        list1.forEachIndexed { index, firstElement ->
            val secondElement = list2[index]
            assertEquals(firstElement, secondElement, "At index $index element of first List ($firstElement) does not equal element of second List ($secondElement)")
        }
    }

    fun <T> assertElementsDoNotEqual(list1: List<T>, list2: List<T>) {
        if (list1.size == list2.size) {
            val allElementsEqual = list1.mapIndexed { index, firstElement ->
                val secondElement = list2[index]
                firstElement == secondElement
            }

            if (allElementsEqual.contains(false) == false) {
                fail("$list1 should not equal $list2, but all elements are equal")
            }
        }
    }

}