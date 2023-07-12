package net.dankito.ksoup.nodes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.integration.UrlConnectTest
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

/**
 * Fetches HTML entity names from w3.org json, and outputs data files for optimized used in Entities.
 * I refuse to believe that entity names like "NotNestedLessLess" are valuable or useful for HTML authors. Implemented
 * only to be complete.
 */
internal object BuildEntities {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val url = "https://www.w3.org/TR/2012/WD-html5-20121025/entities.json"
        val res = Jsoup.connect(url)
            .ignoreContentType(true)
            .userAgent(UrlConnectTest.Companion.browserUa)
            .execute()
        val gson = Gson()
        val input = gson.fromJson<Map<String, CharacterRef>>(
            res.body(),
            object : TypeToken<Map<String?, CharacterRef?>?>() {}.type
        )


        // build name sorted base and full character lists:
        val base = ArrayList<CharacterRef>()
        val full = ArrayList<CharacterRef>()
        for ((key, ref) in input) {
            var name = key.substring(1) // name is like &acute or &acute; , trim &
            if (name.endsWith(";")) {
                name = name.substring(0, name.length - 1)
                full.add(ref)
            } else {
                base.add(ref)
            }
            ref.name = name
        }
        base.sortWith(byName)
        full.sortWith(byName)

        // now determine code point order
        val baseByCode = ArrayList(base)
        val fullByCode = ArrayList(full)
        baseByCode.sortWith(byCode)
        fullByCode.sortWith(byCode)

        // and update their codepoint index.
        val codelists: Array<ArrayList<CharacterRef>> = arrayOf(baseByCode, fullByCode)
        for (codelist in codelists) {
            for (i in codelist.indices) {
                codelist[i].codeIndex = i
            }
        }

        // now write them
        persist("entities-full", full)
        persist("entities-base", base)
        println("Full size: " + full.size + ", base size: " + base.size)
    }

    @Throws(IOException::class)
    private fun persist(name: String, refs: ArrayList<CharacterRef>) {
        val file = Files.createTempFile(name, ".txt").toFile()
        val writer = FileWriter(file, false)
        writer.append("static final String points = \"")
        for (ref in refs) {
            writer.append(ref.toString()).append('&')
        }
        writer.append("\";\n")
        writer.close()
        println("Wrote " + name + " to " + file.absolutePath)
    }

    private fun d(d: Int): String {
        return Integer.toString(d, Entities.codepointRadix)
    }

    private val byName = ByName()
    private val byCode = ByCode()

    private class CharacterRef {
        var codepoints: IntArray = intArrayOf()
        var name: String? = null
        var codeIndex = 0
        override fun toString(): String {
            return (name
                    + "="
                    + d(codepoints[0])
                    + (if (codepoints.size > 1) "," + d(codepoints[1]) else "")
                    + ";" + d(codeIndex))
        }
    }

    private class ByName : Comparator<CharacterRef> {
        override fun compare(o1: CharacterRef, o2: CharacterRef): Int {
            return o1.name!!.compareTo(o2.name!!)
        }
    }

    private class ByCode : Comparator<CharacterRef> {
        override fun compare(o1: CharacterRef, o2: CharacterRef): Int {
            val c1 = o1.codepoints
            val c2 = o2.codepoints
            val first = c1[0] - c2[0]
            if (first != 0) return first
            if (c1.size == 1 && c2.size == 1) { // for the same code, use the shorter name
                val len = o2.name!!.length - o1.name!!.length
                return if (len != 0) len else o1.name!!.compareTo(o2.name!!)
            }
            return if (c1.size == 2 && c2.size == 2) c1[1] - c2[1] else c2.size - c1.size // pushes multi down the list so hits on singles first (don't support multi lookup by codepoint yet)
        }
    }
}
