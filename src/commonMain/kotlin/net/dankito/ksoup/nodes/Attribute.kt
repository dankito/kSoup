package net.dankito.ksoup.nodes

import net.dankito.ksoup.SerializationException
import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.internal.Normalizer
import net.dankito.ksoup.internal.StringUtil
import net.dankito.ksoup.jvm.Cloneable
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A single key + value attribute. (Only used for presentation.)
 */
class Attribute @JvmOverloads constructor(
    key: String,
    private var nullableValue: String?,
    var parent: Attributes? = null // used to update the holding Attributes when the key / value is changed via this interface
) : MutableMap.MutableEntry<String, String?>, Cloneable<Attribute> {

    constructor(key: String, value: Any?, parent: Attributes? = null) : this(key, value as? String, parent)


    override var key: String = key.trim()
        // visible for testing
        public set(value) {
            Validate.notNull(value)
            val key = value.trim()
            Validate.notEmpty(key) // trimming could potentially make empty, so validate here
            parent?.let { parent ->
                val i = parent.indexOfKey(field)
                if (i != Attributes.NotFound) {
                    parent.keys[i] = key
                }
            }
            field = value
        }

    /**
     * Create a new attribute from unencoded (raw) key and value.
     * @param key attribute key; case is preserved.
     * @param val attribute value (may be null)
     * @param parent the containing Attributes (this Attribute is not automatically added to said Attributes)
     * @see .createFromEncoded
     */
    init {
        Validate.notNull(key)
        Validate.notEmpty(this.key) // trimming could potentially make empty, so validate here
    }

    /**
     * Get the attribute value. Will return an empty string if the value is not set.
     * @return the attribute value
     */
    override val value: String
        get() = Attributes.checkNotNull(nullableValue)

    /**
     * Check if this Attribute has a value. Set boolean attributes have no value.
     * @return if this is a boolean attribute / attribute without a value
     */
    fun hasDeclaredValue(): Boolean {
        return nullableValue != null
    }

    /**
     * Set the attribute value.
     * @param `val` the new attribute value; may be null (to set an enabled boolean attribute)
     * @return the previous value (if was null; an empty string)
     */
    override fun setValue(newValue: String?): String {
        var oldVal = this.nullableValue
        if (parent != null) {
            val i = parent!!.indexOfKey(key)
            if (i != Attributes.NotFound) {
                oldVal = parent!![key] // trust the container more
                parent!!.vals[i] = newValue
            }
        }
        this.nullableValue = newValue
        return Attributes.checkNotNull(oldVal)
    }

    /**
     * Get the HTML representation of this attribute; e.g. `href="index.html"`.
     * @return HTML
     */
    fun html(): String {
        val sb = StringUtil.borrowBuilder()
        try {
            html(sb, Document("").outputSettings())
        } catch (exception: Exception) {
            throw SerializationException(exception)
        }
        return StringUtil.releaseBuilder(sb)
    }

    protected fun html(accum: Appendable, out: Document.OutputSettings) {
        html(key, nullableValue, accum, out)
    }

    /**
     * Get the string representation of this attribute, implemented as [.html].
     * @return string
     */
    override fun toString(): String {
        return html()
    }

    val isDataAttribute: Boolean
        get() = isDataAttribute(key)

    /**
     * Collapsible if it's a boolean attribute and value is empty or same as name
     *
     * @param out output settings
     * @return  Returns whether collapsible or not
     */
    protected fun shouldCollapseAttribute(out: Document.OutputSettings): Boolean {
        return shouldCollapseAttribute(key, nullableValue, out)
    }

    override fun equals(other: Any?): Boolean { // note parent not considered
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val attribute = other as Attribute
        if (if (key != null) key != attribute.key else attribute.key != null) return false
        return if (nullableValue != null) nullableValue == attribute.nullableValue else attribute.nullableValue == null
    }

    override fun hashCode(): Int { // note parent not considered
        var result = if (key != null) key.hashCode() else 0
        result = 31 * result + if (nullableValue != null) nullableValue.hashCode() else 0
        return result
    }

    override fun clone(): Attribute {
        return Attribute(key, value, parent)
    }

    companion object {
        private val booleanAttributes = arrayOf(
            "allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled",
            "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize",
            "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected",
            "sortable", "truespeed", "typemustmatch"
        )

        protected fun html(key: String, `val`: String?, accum: Appendable, out: Document.OutputSettings) {
            val key = getValidKey(key, out.syntax())
            if (key == null) return  // can't write it :(
            htmlNoValidate(key, `val`, accum, out)
        }

        fun htmlNoValidate(key: String?, value: String?, accum: Appendable, out: Document.OutputSettings) {
            // structured like this so that Attributes can check we can write first, so it can add whitespace correctly
            accum.append(key)
            if (!shouldCollapseAttribute(key, value, out)) {
                accum.append("=\"")
                Entities.escape(accum, Attributes.checkNotNull(value), out, true, false, false, false)
                accum.append('"')
            }
        }

        private val xmlKeyValid = Regex("[a-zA-Z_:][-a-zA-Z0-9_:.]*")
        private val xmlKeyReplace = Regex("[^-a-zA-Z0-9_:.]")
        private val htmlKeyValid = Regex("[^\\x00-\\x1f\\x7f-\\x9f \"'/=]+")
        private val htmlKeyReplace = Regex("[\\x00-\\x1f\\x7f-\\x9f \"'/=]")

        fun getValidKey(key: String, syntax: Document.OutputSettings.Syntax): String? {
            // we consider HTML attributes to always be valid. XML checks key validity
            var key = key
            if (syntax == Document.OutputSettings.Syntax.xml && !xmlKeyValid.matches(key)) { // TODO: is .matches() correct or should i use .matchesEntire() != null
                key = xmlKeyReplace.replace(key, "")
                return if (xmlKeyValid.matches(key)) key else null // null if could not be coerced
            } else if (syntax == Document.OutputSettings.Syntax.html && !htmlKeyValid.matches(key)) {
                key = htmlKeyReplace.replace(key, "")
                return if (htmlKeyValid.matches(key)) key else null // null if could not be coerced
            }
            return key
        }

        /**
         * Create a new Attribute from an unencoded key and a HTML attribute encoded value.
         * @param unencodedKey assumes the key is not encoded, as can be only run of simple \w chars.
         * @param encodedValue HTML attribute encoded value
         * @return attribute
         */
        fun createFromEncoded(unencodedKey: String, encodedValue: String): Attribute {
            val value = Entities.unescape(encodedValue, true)
            return Attribute(unencodedKey, value, null) // parent will get set when Put
        }

        protected fun isDataAttribute(key: String): Boolean {
            return key.startsWith(Attributes.dataPrefix) && key.length > Attributes.dataPrefix.length
        }

        // collapse unknown foo=null, known checked=null, checked="", checked=checked; write out others
        protected fun shouldCollapseAttribute(key: String?, `val`: String?, out: Document.OutputSettings): Boolean {
            return out.syntax() == Document.OutputSettings.Syntax.html &&
                    (`val` == null || (`val`.isEmpty() || `val`.equals(key, ignoreCase = true)) && isBooleanAttribute(key))
        }

        /**
         * Checks if this attribute name is defined as a boolean attribute in HTML5
         */
        @JvmStatic
        fun isBooleanAttribute(key: String?): Boolean {
            return booleanAttributes.asList().binarySearch(Normalizer.lowerCase(key)) >= 0
        }

    }
}