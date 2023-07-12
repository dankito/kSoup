package org.jsoup

import java.util.regex.Pattern

/**
 * Text utils to ease testing
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
object TextUtil {
    var stripper = Pattern.compile("\\r?\\n\\s*")
    var stripLines = Pattern.compile("\\r?\\n?")
    var spaceCollapse = Pattern.compile("\\s{2,}")
    var tagSpaceCollapse = Pattern.compile(">\\s+<")
    var stripCRs = Pattern.compile("\\r*")
    fun stripNewlines(text: String?): String {
        return TextUtil.stripper.matcher(text).replaceAll("")
    }

    fun normalizeSpaces(text: String?): String {
        var text = text
        text = TextUtil.stripLines.matcher(text).replaceAll("")
        text = TextUtil.stripper.matcher(text).replaceAll("")
        text = TextUtil.spaceCollapse.matcher(text).replaceAll(" ")
        text = TextUtil.tagSpaceCollapse.matcher(text).replaceAll("><")
        return text
    }

    fun stripCRs(text: String?): String {
        return TextUtil.stripCRs.matcher(text).replaceAll("")
    }
}
