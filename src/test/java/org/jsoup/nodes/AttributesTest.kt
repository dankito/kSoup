package org.jsoup.nodes

import org.jsoup.Jsoup
import org.jsoup.nodes.Attributes.Companion.internalKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Tests for Attributes.
 *
 * @author Jonathan Hedley
 */
class AttributesTest {
    @Test
    fun html() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("Hello", "There")
        a.put("data-name", "Jsoup")
        Assertions.assertEquals(3, a.size())
        Assertions.assertTrue(a.hasKey("Tot"))
        Assertions.assertTrue(a.hasKey("Hello"))
        Assertions.assertTrue(a.hasKey("data-name"))
        Assertions.assertFalse(a.hasKey("tot"))
        Assertions.assertTrue(a.hasKeyIgnoreCase("tot"))
        Assertions.assertEquals("There", a.getIgnoreCase("hEllo"))
        val dataset = a.dataset()
        Assertions.assertEquals(1, dataset.size)
        Assertions.assertEquals("Jsoup", dataset["name"])
        Assertions.assertEquals("", a["tot"])
        Assertions.assertEquals("a&p", a["Tot"])
        Assertions.assertEquals("a&p", a.getIgnoreCase("tot"))
        Assertions.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html())
        Assertions.assertEquals(a.html(), a.toString())
    }

    @Test
    fun testIteratorRemovable() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("Hello", "There")
        a.put("data-name", "Jsoup")
        Assertions.assertTrue(a.hasKey("Tot"))
        val iterator = a.iterator()
        var attr = iterator.next()
        Assertions.assertEquals("Tot", attr.key)
        iterator.remove()
        Assertions.assertEquals(2, a.size())
        attr = iterator.next()
        Assertions.assertEquals("Hello", attr.key)
        Assertions.assertEquals("There", attr.value)

        // make sure that's flowing to the underlying attributes object
        Assertions.assertEquals(2, a.size())
        Assertions.assertEquals("There", a["Hello"])
        Assertions.assertFalse(a.hasKey("Tot"))
    }

    @Test
    fun testIteratorUpdateable() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("Hello", "There")
        Assertions.assertFalse(a.hasKey("Foo"))
        val iterator: Iterator<Attribute> = a.iterator()
        var attr = iterator.next()
        attr.key = "Foo"
        attr = iterator.next()
        attr.key = "Bar"
        attr.setValue("Qux")
        Assertions.assertEquals("a&p", a["Foo"])
        Assertions.assertEquals("Qux", a["Bar"])
        Assertions.assertFalse(a.hasKey("Tot"))
        Assertions.assertFalse(a.hasKey("Hello"))
    }

    @Test
    fun testIteratorHasNext() {
        val a = Attributes()
        a.put("Tot", "1")
        a.put("Hello", "2")
        a.put("data-name", "3")
        var seen = 0
        for ((_, value) in a) {
            seen++
            Assertions.assertEquals(seen.toString(), value)
        }
        Assertions.assertEquals(3, seen)
    }

    @Test
    fun testIterator() {
        val a = Attributes()
        val datas = arrayOf(arrayOf("Tot", "raul"), arrayOf("Hello", "pismuth"), arrayOf("data-name", "Jsoup"))
        for (atts in datas) {
            a.put(atts[0], atts[1])
        }
        val iterator: Iterator<Attribute> = a.iterator()
        Assertions.assertTrue(iterator.hasNext())
        var i = 0
        for ((key, value) in a) {
            Assertions.assertEquals(datas[i][0], key)
            Assertions.assertEquals(datas[i][1], value)
            i++
        }
        Assertions.assertEquals(datas.size, i)
    }

    @Test
    fun testIteratorSkipsInternal() {
        val a = Attributes()
        a.put("One", "One")
        a.put(internalKey("baseUri"), "example.com")
        a.put("Two", "Two")
        a.put(internalKey("another"), "example.com")
        val it: Iterator<Attribute> = a.iterator()
        Assertions.assertTrue(it.hasNext())
        Assertions.assertEquals("One", it.next().key)
        Assertions.assertTrue(it.hasNext())
        Assertions.assertEquals("Two", it.next().key)
        Assertions.assertFalse(it.hasNext())
        var seen = 0
        for ((key, value) in a) {
            seen++
        }
        Assertions.assertEquals(2, seen)
    }

    @Test
    fun testListSkipsInternal() {
        val a = Attributes()
        a.put("One", "One")
        a.put(internalKey("baseUri"), "example.com")
        a.put("Two", "Two")
        a.put(internalKey("another"), "example.com")
        val attributes = a.asList()
        Assertions.assertEquals(2, attributes.size)
        Assertions.assertEquals("One", attributes[0].key)
        Assertions.assertEquals("Two", attributes[1].key)
    }

    @Test
    fun htmlSkipsInternals() {
        val a = Attributes()
        a.put("One", "One")
        a.put(internalKey("baseUri"), "example.com")
        a.put("Two", "Two")
        a.put(internalKey("another"), "example.com")
        Assertions.assertEquals(" One=\"One\" Two=\"Two\"", a.html())
    }

    @Test
    fun testIteratorEmpty() {
        val a = Attributes()
        val iterator: Iterator<Attribute> = a.iterator()
        Assertions.assertFalse(iterator.hasNext())
    }

    @Test
    fun removeCaseSensitive() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("tot", "one")
        a.put("Hello", "There")
        a.put("hello", "There")
        a.put("data-name", "Jsoup")
        Assertions.assertEquals(5, a.size())
        a.remove("Tot")
        a.remove("Hello")
        Assertions.assertEquals(3, a.size())
        Assertions.assertTrue(a.hasKey("tot"))
        Assertions.assertFalse(a.hasKey("Tot"))
    }

    @Test
    fun testSetKeyConsistency() {
        val a = Attributes()
        a.put("a", "a")
        for (at in a) {
            at.key = "b"
        }
        Assertions.assertFalse(a.hasKey("a"), "Attribute 'a' not correctly removed")
        Assertions.assertTrue(a.hasKey("b"), "Attribute 'b' not present after renaming")
    }

    @Test
    fun testBoolean() {
        val ats = Attributes()
        ats.put("a", "a")
        ats.put("B", "b")
        ats.put("c", null)
        Assertions.assertTrue(ats.hasDeclaredValueForKey("a"))
        Assertions.assertFalse(ats.hasDeclaredValueForKey("A"))
        Assertions.assertTrue(ats.hasDeclaredValueForKeyIgnoreCase("A"))
        Assertions.assertFalse(ats.hasDeclaredValueForKey("c"))
        Assertions.assertFalse(ats.hasDeclaredValueForKey("C"))
        Assertions.assertFalse(ats.hasDeclaredValueForKeyIgnoreCase("C"))
    }

    @Test
    fun testSizeWhenHasInternal() {
        val a = Attributes()
        a.put("One", "One")
        a.put("Two", "Two")
        Assertions.assertEquals(2, a.size())
        a.put(internalKey("baseUri"), "example.com")
        a.put(internalKey("another"), "example.com")
        a.put(internalKey("last"), "example.com")
        a.remove(internalKey("last"))
        Assertions.assertEquals(4, a.size())
        Assertions.assertEquals(2, a.asList().size) // excluded from lists
    }

    @Test
    fun testBooleans() {
        // want unknown=null, and known like async=null, async="", and async=async to collapse
        val html = "<a foo bar=\"\" async=async qux=qux defer=deferring ismap inert=\"\">"
        val el = Jsoup.parse(html).selectFirst("a")
        Assertions.assertEquals(
            " foo bar=\"\" async qux=\"qux\" defer=\"deferring\" ismap inert",
            el!!.attributes().html()
        )
    }

    @Test
    fun booleanNullAttributesConsistent() {
        val attributes = Attributes()
        attributes.put("key", null)
        val attribute = attributes.iterator().next()
        Assertions.assertEquals("key", attribute.html())
        Assertions.assertEquals(" key", attributes.html())
    }

    @Test
    fun booleanEmptyString() {
        val attributes = Attributes()
        attributes.put("checked", "")
        val attribute = attributes.iterator().next()
        Assertions.assertEquals("checked", attribute.html())
        Assertions.assertEquals(" checked", attributes.html())
    }

    @Test
    fun booleanCaseInsensitive() {
        val attributes = Attributes()
        attributes.put("checked", "CHECKED")
        val attribute = attributes.iterator().next()
        Assertions.assertEquals("checked", attribute.html())
        Assertions.assertEquals(" checked", attributes.html())
    }

    @Test
    fun equalsIsOrderInsensitive() {
        val one = Attributes()
            .add("Key1", "Val1")
            .add("Key2", "Val2")
            .add("Key3", null)
        val two = Attributes()
            .add("Key1", "Val1")
            .add("Key2", "Val2")
            .add("Key3", null)
        val three = Attributes()
            .add("Key2", "Val2")
            .add("Key3", null)
            .add("Key1", "Val1")
        val four = Attributes()
            .add("Key1", "Val1")
            .add("Key2", "Val2")
            .add("Key3", null)
            .add("Key4", "Val4")
        Assertions.assertEquals(one, one.clone())
        Assertions.assertEquals(one, two)
        Assertions.assertEquals(two, two)
        Assertions.assertEquals(one, three)
        Assertions.assertEquals(two, three)
        Assertions.assertEquals(three, three)
        Assertions.assertEquals(three, three.clone())
        Assertions.assertEquals(four, four)
        Assertions.assertEquals(four, four.clone())
        Assertions.assertNotEquals(one, four)
    }

    @Test
    fun cloneAttributes() {
        val one = Attributes()
            .add("Key1", "Val1")
            .add("Key2", "Val2")
            .add("Key3", null)
        val two = one.clone()
        Assertions.assertEquals(3, two.size())
        Assertions.assertEquals("Val2", two["Key2"])
        Assertions.assertEquals(one, two)
        two.add("Key4", "Val4")
        Assertions.assertEquals(4, two.size())
        Assertions.assertEquals(3, one.size())
        Assertions.assertNotEquals(one, two)
    }
}
