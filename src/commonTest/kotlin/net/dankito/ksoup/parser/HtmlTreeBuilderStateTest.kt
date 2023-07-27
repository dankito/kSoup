package net.dankito.ksoup.parser

import net.dankito.ksoup.Jsoup
import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlTreeBuilderStateTest {

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
        assertEquals(
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
        assertEquals(
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
}