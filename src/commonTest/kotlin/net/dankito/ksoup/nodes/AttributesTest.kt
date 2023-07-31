package net.dankito.ksoup.nodes

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.nodes.Attributes.Companion.internalKey
import kotlin.test.*

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
        assertEquals(3, a.size())
        assertTrue(a.hasKey("Tot"))
        assertTrue(a.hasKey("Hello"))
        assertTrue(a.hasKey("data-name"))
        assertFalse(a.hasKey("tot"))
        assertTrue(a.hasKeyIgnoreCase("tot"))
        assertEquals("There", a.getIgnoreCase("hEllo"))
        val dataset = a.dataset()
        assertEquals(1, dataset.size)
        assertEquals("Jsoup", dataset["name"])
        assertEquals("", a["tot"])
        assertEquals("a&p", a["Tot"])
        assertEquals("a&p", a.getIgnoreCase("tot"))
        assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html())
        assertEquals(a.html(), a.toString())
    }

    @Test
    fun testIteratorRemovable() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("Hello", "There")
        a.put("data-name", "Jsoup")
        assertTrue(a.hasKey("Tot"))

        val iterator = a.iterator()
        var attr = iterator.next()
        assertEquals("Tot", attr.key)
        
        iterator.remove()
        assertEquals(2, a.size())
        attr = iterator.next()
        assertEquals("Hello", attr.key)
        assertEquals("There", attr.value)

        // make sure that's flowing to the underlying attributes object
        assertEquals(2, a.size())
        assertEquals("There", a["Hello"])
        assertFalse(a.hasKey("Tot"))
    }

    @Test
    fun testIteratorUpdateable() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("Hello", "There")
        assertFalse(a.hasKey("Foo"))
        val iterator: Iterator<Attribute> = a.iterator()
        var attr = iterator.next()
        attr.key = "Foo"
        attr = iterator.next()
        attr.key = "Bar"
        attr.setValue("Qux")
        assertEquals("a&p", a["Foo"])
        assertEquals("Qux", a["Bar"])
        assertFalse(a.hasKey("Tot"))
        assertFalse(a.hasKey("Hello"))
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
            assertEquals(seen.toString(), value)
        }
        assertEquals(3, seen)
    }

    @Test
    fun testIterator() {
        val a = Attributes()
        val datas = arrayOf(arrayOf("Tot", "raul"), arrayOf("Hello", "pismuth"), arrayOf("data-name", "Jsoup"))
        for (atts in datas) {
            a.put(atts[0], atts[1])
        }
        val iterator: Iterator<Attribute> = a.iterator()
        assertTrue(iterator.hasNext())
        var i = 0
        for ((key, value) in a) {
            assertEquals(datas[i][0], key)
            assertEquals(datas[i][1], value)
            i++
        }
        assertEquals(datas.size, i)
    }

    @Test
    fun testIteratorSkipsInternal() {
        val a = Attributes()
        a.put("One", "One")
        a.put(internalKey("baseUri"), "example.com")
        a.put("Two", "Two")
        a.put(internalKey("another"), "example.com")
        val it: Iterator<Attribute> = a.iterator()
        assertTrue(it.hasNext())
        assertEquals("One", it.next().key)
        assertTrue(it.hasNext())
        assertEquals("Two", it.next().key)
        assertFalse(it.hasNext())
        var seen = 0
        for ((key, value) in a) {
            seen++
        }
        assertEquals(2, seen)
    }

    @Test
    fun testListSkipsInternal() {
        val a = Attributes()
        a.put("One", "One")
        a.put(internalKey("baseUri"), "example.com")
        a.put("Two", "Two")
        a.put(internalKey("another"), "example.com")
        val attributes = a.asList()
        assertEquals(2, attributes.size)
        assertEquals("One", attributes[0].key)
        assertEquals("Two", attributes[1].key)
    }

    @Test
    fun htmlSkipsInternals() {
        val a = Attributes()
        a.put("One", "One")
        a.put(internalKey("baseUri"), "example.com")
        a.put("Two", "Two")
        a.put(internalKey("another"), "example.com")
        assertEquals(" One=\"One\" Two=\"Two\"", a.html())
    }

    @Test
    fun testIteratorEmpty() {
        val a = Attributes()
        val iterator: Iterator<Attribute> = a.iterator()
        assertFalse(iterator.hasNext())
    }

    @Test
    fun removeCaseSensitive() {
        val a = Attributes()
        a.put("Tot", "a&p")
        a.put("tot", "one")
        a.put("Hello", "There")
        a.put("hello", "There")
        a.put("data-name", "Jsoup")
        assertEquals(5, a.size())
        a.remove("Tot")
        a.remove("Hello")
        assertEquals(3, a.size())
        assertTrue(a.hasKey("tot"))
        assertFalse(a.hasKey("Tot"))
    }

    @Test
    fun testSetKeyConsistency() {
        val a = Attributes()
        a.put("a", "a")
        for (at in a) {
            at.key = "b"
        }
        assertFalse(a.hasKey("a"), "Attribute 'a' not correctly removed")
        assertTrue(a.hasKey("b"), "Attribute 'b' not present after renaming")
    }

    @Test
    fun testBoolean() {
        val ats = Attributes()
        ats.put("a", "a")
        ats.put("B", "b")
        ats.put("c", null)
        assertTrue(ats.hasDeclaredValueForKey("a"))
        assertFalse(ats.hasDeclaredValueForKey("A"))
        assertTrue(ats.hasDeclaredValueForKeyIgnoreCase("A"))
        assertFalse(ats.hasDeclaredValueForKey("c"))
        assertFalse(ats.hasDeclaredValueForKey("C"))
        assertFalse(ats.hasDeclaredValueForKeyIgnoreCase("C"))
    }

    @Test
    fun testSizeWhenHasInternal() {
        val a = Attributes()
        a.put("One", "One")
        a.put("Two", "Two")
        assertEquals(2, a.size())
        a.put(internalKey("baseUri"), "example.com")
        a.put(internalKey("another"), "example.com")
        a.put(internalKey("last"), "example.com")
        a.remove(internalKey("last"))
        assertEquals(4, a.size())
        assertEquals(2, a.asList().size) // excluded from lists
    }

    @Test
    fun testBooleans() {
        // want unknown=null, and known like async=null, async="", and async=async to collapse
        val html = "<a foo bar=\"\" async=async qux=qux defer=deferring ismap inert=\"\">"
        val el = Jsoup.parse(html).selectFirst("a")
        assertEquals(
            " foo bar=\"\" async qux=\"qux\" defer=\"deferring\" ismap inert",
            el!!.attributes().html()
        )
    }

    @Test
    fun booleanNullAttributesConsistent() {
        val attributes = Attributes()
        attributes.put("key", null)
        val attribute = attributes.iterator().next()
        assertEquals("key", attribute.html())
        assertEquals(" key", attributes.html())
    }

    @Test
    fun booleanEmptyString() {
        val attributes = Attributes()
        attributes.put("checked", "")
        val attribute = attributes.iterator().next()
        assertEquals("checked", attribute.html())
        assertEquals(" checked", attributes.html())
    }

    @Test
    fun booleanCaseInsensitive() {
        val attributes = Attributes()
        attributes.put("checked", "CHECKED")
        val attribute = attributes.iterator().next()
        assertEquals("checked", attribute.html())
        assertEquals(" checked", attributes.html())
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
        assertEquals(one, one.clone())
        assertEquals(one, two)
        assertEquals(two, two)
        assertEquals(one, three)
        assertEquals(two, three)
        assertEquals(three, three)
        assertEquals(three, three.clone())
        assertEquals(four, four)
        assertEquals(four, four.clone())
        assertNotEquals(one, four)
    }

    @Test
    fun cloneAttributes() {
        val one = Attributes()
            .add("Key1", "Val1")
            .add("Key2", "Val2")
            .add("Key3", null)
        val two = one.clone()
        assertEquals(3, two.size())
        assertEquals("Val2", two["Key2"])
        assertEquals(one, two)
        two.add("Key4", "Val4")
        assertEquals(4, two.size())
        assertEquals(3, one.size())
        assertNotEquals(one, two)
    }
}
