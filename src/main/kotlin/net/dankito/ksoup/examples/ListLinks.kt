package net.dankito.ksoup.examples

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.select.Elements
import java.io.IOException

/**
 * Example program to list links from a URL.
 */
object ListLinks {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        Validate.isTrue(args.size == 1, "usage: supply url to fetch")
        val url: String = args.get(0)
        print("Fetching %s...", url)
        val doc = Jsoup.connect(url).get()
        val links: Elements = doc.select("a[href]")
        val media: Elements = doc.select("[src]")
        val imports: Elements = doc.select("link[href]")
        print("\nMedia: (%d)", media.size)
        for (src: Element in media) {
            if ((src.normalName() == "img")) print(
                " * %s: <%s> %sx%s (%s)",
                src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                trim(src.attr("alt"), 20)
            ) else print(" * %s: <%s>", src.tagName(), src.attr("abs:src"))
        }
        print("\nImports: (%d)", imports.size)
        for (link: Element in imports) {
            print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"))
        }
        print("\nLinks: (%d)", links.size)
        for (link: Element in links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35))
        }
    }

    private fun print(msg: String, vararg args: Any) {
        println(String.format(msg, *args))
    }

    private fun trim(s: String, width: Int): String {
        if (s.length > width) return s.substring(0, width - 1) + "." else return s
    }
}
