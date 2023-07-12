package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.parser.HtmlTreeBuilderState.Constants.InBodyStartInputAttribs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import java.util.*

class HtmlTreeBuilderStateTest {
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
                if (StringUtil.inSorted(tagName, InBodyStartInputAttribs)) {
                    continue // odd one out in the constant
                }
                assertTrue(Tag.isKnownTag(tagName), String.format("Unknown tag name: %s", tagName));
            }
        }
    }

    @Test
    fun nestedAnchorElements01() {
        val html = """<html>
  <body>
    <a href='#1'>
        <div>
          <a href='#2'>child</a>
        </div>
    </a>
  </body>
</html>"""
        val s = Jsoup.parse(html).toString()
        Assertions.assertEquals(
            """<html>
 <head></head>
 <body>
  <a href="#1"> </a>
  <div>
   <a href="#1"> </a><a href="#2">child</a>
  </div>
 </body>
</html>""", s
        )
    }

    @Test
    fun nestedAnchorElements02() {
        val html = """<html>
  <body>
    <a href='#1'>
      <div>
        <div>
          <a href='#2'>child</a>
        </div>
      </div>
    </a>
  </body>
</html>"""
        val s = Jsoup.parse(html).toString()
        Assertions.assertEquals(
            """<html>
 <head></head>
 <body>
  <a href="#1"> </a>
  <div>
   <a href="#1"> </a>
   <div>
    <a href="#1"> </a><a href="#2">child</a>
   </div>
  </div>
 </body>
</html>""", s
        )
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
                val copy = Arrays.copyOf(array, array.size)
                Arrays.sort(array)
                Assertions.assertArrayEquals(array, copy)
            }
        }
    }
}