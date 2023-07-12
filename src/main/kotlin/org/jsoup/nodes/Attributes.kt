package org.jsoup.nodes

import org.jsoup.SerializationException
import org.jsoup.helper.Validate
import org.jsoup.internal.Normalizer
import org.jsoup.internal.StringUtil
import org.jsoup.parser.ParseSettings
import java.io.IOException
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * The attributes of an Element.
 *
 *
 * Attributes are treated as a map: there can be only one value associated with an attribute key/name.
 *
 *
 *
 * Attribute name and value comparisons are generally **case sensitive**. By default for HTML, attribute names are
 * normalized to lower-case on parsing. That means you should use lower-case strings when referring to attributes by
 * name.
 *
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class Attributes : Iterable<Attribute>, java.lang.Cloneable {
    // the number of instance fields is kept as low as possible giving an object size of 24 bytes
    private var size = 0 // number of slots used (not total capacity, which is keys.length)
    var keys = arrayOfNulls<String>(InitialCapacity)
    var vals = arrayOfNulls<Any>(InitialCapacity)

    // check there's room for more
    private fun checkCapacity(minNewSize: Int) {
        Validate.isTrue(minNewSize >= size)
        val curCap = keys.size
        if (curCap >= minNewSize) return
        var newCap = if (curCap >= InitialCapacity) size * GrowthFactor else InitialCapacity
        if (minNewSize > newCap) newCap = minNewSize
        keys = keys.copyOf(newCap)
        vals = vals.copyOf(newCap)
    }

    fun indexOfKey(key: String?): Int {
        Validate.notNull(key)
        for (i in 0 until size) {
            if (key == keys[i]) return i
        }
        return NotFound
    }

    private fun indexOfKeyIgnoreCase(key: String): Int {
        Validate.notNull(key)
        for (i in 0 until size) {
            if (key.equals(keys[i], ignoreCase = true)) return i
        }
        return NotFound
    }

    /**
     * Get an attribute value by key.
     * @param key the (case-sensitive) attribute key
     * @return the attribute value if set; or empty string if not set (or a boolean attribute).
     * @see .hasKey
     */
    operator fun get(key: String): String {
        return checkNotNull(getOrNull(key))
    }

    internal fun getOrNull(key: String): String? {
        val i = indexOfKey(key)
        return if (i == NotFound) {
            null
        } else {
            vals[i] as? String
        }
    }

    /**
     * Get an attribute's value by case-insensitive key
     * @param key the attribute name
     * @return the first matching attribute value if set; or empty string if not set (ora boolean attribute).
     */
    fun getIgnoreCase(key: String): String {
        val i = indexOfKeyIgnoreCase(key)
        return if (i == NotFound) {
            EmptyString
        } else {
            checkNotNull(vals[i])
        }
    }

    /**
     * Get an arbitrary user data object by key.
     * @param key case sensitive key to the object.
     * @return the object associated to this key, or `null` if not found.
     */
    fun getUserData(key: String): Any? {
        Validate.notNull(key)

        val key = if (!isInternalKey(key)) {
            internalKey(key)
        } else {
            key
        }

        val i = indexOfKeyIgnoreCase(key)
        return if (i == NotFound) null else vals[i]
    }

    /**
     * Adds a new attribute. Will produce duplicates if the key already exists.
     * @see Attributes.put
     */
    fun add(key: String, value: String?): Attributes {
        addObject(key, value)
        return this
    }

    private fun addObject(key: String, value: Any?) {
        checkCapacity(size + 1)
        keys[size] = key
        vals[size] = value
        size++
    }

    /**
     * Set a new attribute, or replace an existing one by key.
     * @param key case sensitive attribute key (not null)
     * @param value attribute value (may be null, to set a boolean attribute)
     * @return these attributes, for chaining
     */
    fun put(key: String, value: String?): Attributes {
        Validate.notNull(key)

        val i = indexOfKey(key)
        if (i != NotFound) {
            vals[i] = value
        } else {
            add(key, value)
        }

        return this
    }

    /**
     * Put an arbitrary user-data object by key. Will be treated as an internal attribute, so will not be emitted in HTML.
     * @param key case sensitive key
     * @param value object value
     * @return these attributes
     * @see .getUserData
     */
    fun putUserData(key: String, value: Any?): Attributes {
        Validate.notNull(key)
        Validate.notNull(value)

        val key = if (!isInternalKey(key)) {
            internalKey(key)
        } else {
            key
        }

        val i = indexOfKey(key)
        if (i != NotFound) {
            vals[i] = value
        } else {
            addObject(key, value)
        }

        return this
    }

    fun putIgnoreCase(key: String, value: String?) {
        val i = indexOfKeyIgnoreCase(key)
        if (i != NotFound) {
            vals[i] = value
            if (keys[i] != key) // case changed, update
                keys[i] = key
        } else add(key, value)
    }

    /**
     * Set a new boolean attribute, remove attribute if value is false.
     * @param key case **insensitive** attribute key
     * @param value attribute value
     * @return these attributes, for chaining
     */
    fun put(key: String, value: Boolean): Attributes {
        if (value) putIgnoreCase(key, null) else remove(key)
        return this
    }

    /**
     * Set a new attribute, or replace an existing one by key.
     * @param attribute attribute with case sensitive key
     * @return these attributes, for chaining
     */
    fun put(attribute: Attribute): Attributes {
        Validate.notNull(attribute)
        put(attribute.key, attribute.value)
        attribute.parent = this
        return this
    }

    // removes and shifts up
    private fun remove(index: Int) {
        Validate.isFalse(index >= size)
        val shifted = size - index - 1
        if (shifted > 0) {
            System.arraycopy(keys, index + 1, keys, index, shifted)
            System.arraycopy(vals, index + 1, vals, index, shifted)
//            // TODO: does this work?
//            keys = keys.toMutableList().apply {
//                IntRange(0, index).forEach { this.removeFirst() }
//            }.toTypedArray()
//            vals = vals.toMutableList().apply {
//                IntRange(0, index).forEach { this.removeFirst() }
//            }.toTypedArray()
        }
        size--
        keys[size] = null // release hold
        vals[size] = null
    }

    /**
     * Remove an attribute by key. **Case sensitive.**
     * @param key attribute key to remove
     */
    fun remove(key: String?) {
        val i = indexOfKey(key)
        if (i != NotFound) remove(i)
    }

    /**
     * Remove an attribute by key. **Case insensitive.**
     * @param key attribute key to remove
     */
    fun removeIgnoreCase(key: String) {
        val i = indexOfKeyIgnoreCase(key)
        if (i != NotFound) remove(i)
    }

    /**
     * Tests if these attributes contain an attribute with this key.
     * @param key case-sensitive key to check for
     * @return true if key exists, false otherwise
     */
    fun hasKey(key: String?): Boolean {
        return indexOfKey(key) != NotFound
    }

    /**
     * Tests if these attributes contain an attribute with this key.
     * @param key key to check for
     * @return true if key exists, false otherwise
     */
    fun hasKeyIgnoreCase(key: String): Boolean {
        return indexOfKeyIgnoreCase(key) != NotFound
    }

    /**
     * Check if these attributes contain an attribute with a value for this key.
     * @param key key to check for
     * @return true if key exists, and it has a value
     */
    fun hasDeclaredValueForKey(key: String?): Boolean {
        val i = indexOfKey(key)
        return i != NotFound && vals[i] != null
    }

    /**
     * Check if these attributes contain an attribute with a value for this key.
     * @param key case-insensitive key to check for
     * @return true if key exists, and it has a value
     */
    fun hasDeclaredValueForKeyIgnoreCase(key: String): Boolean {
        val i = indexOfKeyIgnoreCase(key)
        return i != NotFound && vals[i] != null
    }

    /**
     * Get the number of attributes in this set, including any jsoup internal-only attributes. Internal attributes are
     * excluded from the [.html], [.asList], and [.iterator] methods.
     * @return size
     */
    fun size(): Int {
        return size
    }

    /**
     * Test if this Attributes list is empty (size==0).
     */
    val isEmpty: Boolean
        get() = size == 0

    /**
     * Add all the attributes from the incoming set to this set.
     * @param incoming attributes to add to these attributes.
     */
    fun addAll(incoming: Attributes) {
        if (incoming.size() == 0) return
        checkCapacity(size + incoming.size)
        val needsPut = size != 0 // if this set is empty, no need to check existing set, so can add() vs put()
        // (and save bashing on the indexOfKey()
        for (attr in incoming) {
            if (needsPut) put(attr) else add(attr.key, attr.value)
        }
    }

    override fun iterator(): MutableIterator<Attribute> {
        return object : MutableIterator<Attribute> {
            var i = 0
            override fun hasNext(): Boolean {
                while (i < size) {
                    if (isInternalKey(keys[i])) { // skip over internal keys
                        i++
                    } else {
                        break
                    }
                }
                return i < size
            }

            override fun next(): Attribute {
                val attr = Attribute(keys[i]!!, vals[i], this@Attributes)
                i++
                return attr
            }

            override fun remove() {
                this@Attributes.remove(--i) // next() advanced, so rewind
            }
        }
    }

    /**
     * Get the attributes as a List, for iteration.
     * @return an view of the attributes as an unmodifiable List.
     */
    fun asList(): List<Attribute> {
        val list = ArrayList<Attribute>(size)
        for (i in 0 until size) {
            val key = keys[i]
            if (key == null || isInternalKey(key)) continue  // skip internal keys
            val attr = Attribute(key, vals[i], this@Attributes)
            list.add(attr)
        }
        return list.toList()
    }

    /**
     * Retrieves a filtered view of attributes that are HTML5 custom data attributes; that is, attributes with keys
     * starting with `data-`.
     * @return map of custom data attributes.
     */
    fun dataset(): Map<String, String?> {
        return Dataset(this)
    }

    /**
     * Get the HTML representation of these attributes.
     * @return HTML
     */
    fun html(): String {
        val sb = StringUtil.borrowBuilder()
        try {
            html(sb, Document("").outputSettings()) // output settings a bit funky, but this html() seldom used
        } catch (e: IOException) { // ought never happen
            throw SerializationException(e)
        }
        return StringUtil.releaseBuilder(sb)
    }

    @Throws(IOException::class)
    fun html(accum: Appendable, out: Document.OutputSettings) {
        val sz = size
        for (i in 0 until sz) {
            var key = keys[i]
            if (key == null || isInternalKey(key)) continue
            key = Attribute.getValidKey(key, out.syntax())
            if (key != null) Attribute.htmlNoValidate(key, vals[i] as? String, accum.append(' '), out)
        }
    }

    override fun toString(): String {
        return html()
    }

    /**
     * Checks if these attributes are equal to another set of attributes, by comparing the two sets. Note that the order
     * of the attributes does not impact this equality (as per the Map interface equals()).
     * @param other attributes to compare with
     * @return if both sets of attributes have the same content
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as Attributes
        if (size != that.size) return false
        for (i in 0 until size) {
            val key = keys[i]
            val thatI = that.indexOfKey(key)
            if (thatI == NotFound) return false
            val `val` = vals[i]
            val thatVal = that.vals[thatI]
            if (`val` == null) {
                if (thatVal != null) return false
            } else if (`val` != thatVal) return false
        }
        return true
    }

    /**
     * Calculates the hashcode of these attributes, by iterating all attributes and summing their hashcodes.
     * @return calculated hashcode
     */
    override fun hashCode(): Int {
        var result = size
        result = 31 * result + keys.hashCode()
        result = 31 * result + vals.hashCode()
        return result
    }

    fun clone(): Attributes {
        // TODO: does this work?
        val clone: Attributes = Attributes()
//        val clone: Attributes
//        clone = try {
//            super.clone() as Attributes
//        } catch (e: CloneNotSupportedException) {
//            throw RuntimeException(e)
//        }

        clone.size = size
        clone.keys = arrayOf(*keys)
        clone.vals = arrayOf(*vals)
        return clone
    }

    /**
     * Internal method. Lowercases all keys.
     */
    fun normalize() {
        for (i in 0 until size) {
            keys[i] = Normalizer.lowerCase(keys[i])
        }
    }

    /**
     * Internal method. Removes duplicate attribute by name. Settings for case sensitivity of key names.
     * @param settings case sensitivity
     * @return number of removed dupes
     */
    fun deduplicate(settings: ParseSettings): Int {
        if (isEmpty) return 0
        val preserve = settings.preserveAttributeCase()
        var dupes = 0
        OUTER@ for (i in keys.indices) {
            var j = i + 1
            while (j < keys.size) {
                if (keys[j] == null) continue@OUTER   // keys.length doesn't shrink when removing, so re-test
                if (preserve && keys[i] == keys[j] || !preserve && keys[i].equals(keys[j], ignoreCase = true)) {
                    dupes++
                    remove(j)
                    j--
                }
                j++
            }
        }
        return dupes
    }

    private class Dataset internal constructor(private val attributes: Attributes) : AbstractMutableMap<String, String?>() {

        override val entries: MutableSet<MutableMap.MutableEntry<String, String?>>
            get() = EntrySet()

        override fun put(key: String, value: String?): String? {
            val dataKey = dataKey(key)
            val oldValue = if (attributes.hasKey(dataKey)) attributes[dataKey] else null
            attributes.put(dataKey, value)
            return oldValue
        }

        private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<String, String?>>() {

            override val size: Int
                get() {
                    var count = 0
                    val iter: Iterator<*> = DatasetIterator()
                    while (iter.hasNext()) count++
                    return count
                }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String?>> {
                return DatasetIterator()
            }

            override fun add(element: MutableMap.MutableEntry<String, String?>): Boolean {
                put(element.key, element.value)
                return true
            }
        }

        private inner class DatasetIterator : MutableIterator<MutableMap.MutableEntry<String, String?>> {
            private val attrIter: Iterator<Attribute> = attributes.iterator()
            private var attr: Attribute? = null
            override fun hasNext(): Boolean {
                while (attrIter.hasNext()) {
                    attr = attrIter.next()
                    if (attr!!.isDataAttribute) {
                        return true
                    }
                }
                return false
            }

            override fun next(): MutableMap.MutableEntry<String, String?> {
                return Attribute(attr!!.key.substring(dataPrefix.length), attr!!.value)
            }

            override fun remove() {
                attributes.remove(attr!!.key)
            }
        }
    }

    @OptIn(ExperimentalContracts::class)
    private fun isInternalKey(key: String?): Boolean {
        contract {
            returns(true) implies (key != null)
        }

        return key != null && key.length > 1 && key[0] == InternalPrefix
    }

    companion object {
        // The Attributes object is only created on the first use of an attribute; the Element will just have a null
        // Attribute slot otherwise
        const val dataPrefix = "data-"

        // Indicates a jsoup internal key. Can't be set via HTML. (It could be set via accessor, but not too worried about
        // that. Suppressed from list, iter.
        const val InternalPrefix = '/'
        private const val InitialCapacity = 3 // sampling found mean count when attrs present = 1.49; 1.08 overall. 2.6:1 don't have any attrs.

        // manages the key/val arrays
        private const val GrowthFactor = 2
        const val NotFound = -1
        private const val EmptyString = ""

        // we track boolean attributes as null in values - they're just keys. so returns empty for consumers
        // casts to String, so only for non-internal attributes
        internal fun checkNotNull(value: Any?): String {
            return if (value == null) {
                EmptyString
            } else {
                value as String
            }
        }

        private fun dataKey(key: String): String {
            return dataPrefix + key
        }

        @JvmStatic
        fun internalKey(key: String): String {
            return InternalPrefix.toString() + key
        }
    }
}