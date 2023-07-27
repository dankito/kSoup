package net.dankito.ksoup.parser

import net.dankito.ksoup.internal.StringUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HtmlTreeBuilderStateTestJvm {

    @Test
    fun ensureArraysAreSorted() {
        val constants = findConstantArrays(HtmlTreeBuilderState.Constants::class.java)
        ensureSorted(constants)
        assertEquals(39, constants.size)
    }

    @Test
    fun ensureTagSearchesAreKnownTags() {
        val constants = findConstantArrays(HtmlTreeBuilderState.Constants::class.java)
        for (constant in constants) {
            val tagNames = constant as Array<String>
            for (tagName in tagNames) {
                if (StringUtil.inSorted(tagName, HtmlTreeBuilderState.Constants.InBodyStartInputAttribs)) {
                    continue // odd one out in the constant
                }
                assertTrue(Tag.isKnownTag(tagName), "Unknown tag name: $tagName");
            }
        }
    }

    companion object {
        fun findConstantArrays(aClass: Class<*>): List<Array<Any>> {
            val array = ArrayList<Array<Any>>()
            val fields = aClass.declaredFields
            for (field in fields) {
                val modifiers = field.modifiers
                if (Modifier.isStatic(modifiers) && field.type.isArray) {
                    try {
                        field.isAccessible = true
                        array.add(field[null] as Array<Any>)
                    } catch (e: IllegalAccessException) {
                        throw IllegalStateException(e)
                    }
                }
            }
            return array
        }

        fun ensureSorted(constants: List<Array<Any>>) {
            for (array in constants) {
                val copy = array.copyOf()
                copy.sort()
                Assertions.assertArrayEquals(array, copy)
            }
        }
    }

}