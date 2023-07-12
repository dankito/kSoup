package org.jsoup.examples

import org.jsoup.Jsoup
import org.jsoup.helper.Validate
import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor
import java.io.IOException

/**
 * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
 * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
 * scrape.
 *
 *
 * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
 *
 *
 *
 * To invoke from the command line, assuming you've downloaded the jsoup jar to your current directory:
 *
 * `java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]`
 * where *url* is the URL to fetch, and *selector* is an optional CSS selector.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class HtmlToPlainText {
    /**
     * Format an Element to plain-text
     * @param element the root element to format
     * @return formatted text
     */
    fun getPlainText(element: Element?): String {
        val formatter = FormattingVisitor()
        NodeTraversor.traverse(formatter, element) // walk the DOM, and call .head() and .tail() for each node
        return formatter.toString()
    }

    // the formatting rules, implemented in a breadth-first DOM traverse
    private class FormattingVisitor : NodeVisitor {
        private var width: Int = 0
        private val accum: StringBuilder = StringBuilder() // holds the accumulated text

        // hit when the node is first seen
        override fun head(node: Node, depth: Int) {
            val name = node.nodeName()
            if (node is TextNode) append(node.text()) // TextNodes carry all user-readable text in the DOM.
            else if ((name == "li")) append("\n * ") else if ((name == "dt")) append("  ") else if (StringUtil.`in`(
                    name,
                    "p",
                    "h1",
                    "h2",
                    "h3",
                    "h4",
                    "h5",
                    "tr"
                )
            ) append("\n")
        }

        // hit when all of the node's children (if any) have been visited
        override fun tail(node: Node, depth: Int) {
            val name = node.nodeName()
            if (StringUtil.`in`(
                    name,
                    "br",
                    "dd",
                    "dt",
                    "p",
                    "h1",
                    "h2",
                    "h3",
                    "h4",
                    "h5"
                )
            ) append("\n") else if ((name == "a")) append(
                String.format(" <%s>", node.absUrl("href"))
            )
        }

        // appends text to the string builder with a simple word wrap method
        private fun append(text: String) {
            if (text.startsWith("\n")) width =
                0 // reset counter if starts with a newline. only from formats above, not in natural text
            if ((text == " ") &&
                (accum.length == 0 || StringUtil.`in`(accum.substring(accum.length - 1), " ", "\n"))
            ) return  // don't accumulate long runs of empty spaces
            if (text.length + width > maxWidth) { // won't fit, needs to wrap
                val words: Array<String> = text.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                for (i in words.indices) {
                    var word: String = words.get(i)
                    val last: Boolean = i == words.size - 1
                    if (!last) // insert a space if not the last word
                        word = word + " "
                    if (word.length + width > maxWidth) { // wrap and reset counter
                        accum.append("\n").append(word)
                        width = word.length
                    } else {
                        accum.append(word)
                        width += word.length
                    }
                }
            } else { // fits as is, without need to wrap text
                accum.append(text)
                width += text.length
            }
        }

        override fun toString(): String {
            return accum.toString()
        }

        companion object {
            private val maxWidth: Int = 80
        }
    }

    companion object {
        private val userAgent: String = "Mozilla/5.0 (jsoup)"
        private val timeout: Int = 5 * 1000
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            Validate.isTrue(
                args.size == 1 || args.size == 2,
                "usage: java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]"
            )
            val url: String = args.get(0)
            val selector: String? = if (args.size == 2) args.get(1) else null

            // fetch the specified URL and parse to a HTML DOM
            val doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get()
            val formatter = HtmlToPlainText()
            if (selector != null) {
                val elements: Elements = doc.select(selector) // get each element that matches the CSS selector
                for (element: Element? in elements) {
                    val plainText: String = formatter.getPlainText(element) // format that element to plain text
                    println(plainText)
                }
            } else { // format the whole doc
                val plainText: String = formatter.getPlainText(doc)
                println(plainText)
            }
        }
    }
}
