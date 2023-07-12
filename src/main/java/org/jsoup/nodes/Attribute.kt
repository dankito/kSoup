package org.jsoup.nodes

import org.jsoup.SerializationException
import org.jsoup.helper.Validate
import org.jsoup.internal.Normalizer
import org.jsoup.internal.StringUtil
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

/**
 * A single key + value attribute. (Only used for presentation.)
 */
class Attribute(key: String?, `val`: String?, parent: Attributes?) : MutableMap.MutableEntry<String?, String?>,
    Cloneable {
    override var key: String
    private var `val`: String?
    @JvmField
    var parent // used to update the holding Attributes when the key / value is changed via this interface
            : Attributes?

    /**
     * Create a new attribute from unencoded (raw) key and value.
     * @param key attribute key; case is preserved.
     * @param value attribute value (may be null)
     * @see .createFromEncoded
     */
    constructor(key: String?, value: String?) : this(key, value, null)

    /**
     * Get the attribute key.
     * @return the attribute key
     */
    override fun getKey(): String {
        return key
    }

    /**
     * Set the attribute key; case is preserved.
     * @param key the new key; must not be null
     */
    fun setKey(key: String) {
        var key = key
        Validate.notNull(key)
        key = key.trim { it <= ' ' }
        Validate.notEmpty(key) // trimming could potentially make empty, so validate here
        if (parent != null) {
            val i = parent!!.indexOfKey(this.key)
            if (i != Attributes.Companion.NotFound) parent!!.keys[i] = key
        }
        this.key = key
    }

    override val value: String
        /**
         * Get the attribute value. Will return an empty string if the value is not set.
         * @return the attribute value
         */
        get() = Attributes.Companion.checkNotNull(`val`)

    /**
     * Check if this Attribute has a value. Set boolean attributes have no value.
     * @return if this is a boolean attribute / attribute without a value
     */
    fun hasDeclaredValue(): Boolean {
        return `val` != null
    }

    /**
     * Set the attribute value.
     * @param val the new attribute value; may be null (to set an enabled boolean attribute)
     * @return the previous value (if was null; an empty string)
     */
    override fun setValue(`val`: String?): String? {
        var oldVal = this.`val`
        if (parent != null) {
            val i = parent!!.indexOfKey(key)
            if (i != Attributes.Companion.NotFound) {
                oldVal = parent!![key] // trust the container more
                parent!!.vals[i] = `val`
            }
        }
        this.`val` = `val`
        return Attributes.Companion.checkNotNull(oldVal)
    }

    /**
     * Get the HTML representation of this attribute; e.g. `href="index.html"`.
     * @return HTML
     */
    fun html(): String? {
        val sb = StringUtil.borrowBuilder()
        try {
            html(sb, Document("").outputSettings())
        } catch (exception: IOException) {
            throw SerializationException(exception)
        }
        return StringUtil.releaseBuilder(sb)
    }

    @Throws(IOException::class)
    protected fun html(accum: Appendable?, out: Document.OutputSettings?) {
        html(key, `val`, accum, out)
    }

    /**
     * Create a new attribute from unencoded (raw) key and value.
     * @param key attribute key; case is preserved.
     * @param val attribute value (may be null)
     * @param parent the containing Attributes (this Attribute is not automatically added to said Attributes)
     * @see .createFromEncoded
     */
    init {
        var key = key
        Validate.notNull(key)
        key = key!!.trim { it <= ' ' }
        Validate.notEmpty(key) // trimming could potentially make empty, so validate here
        this.key = key
        this.`val` = `val`
        this.parent = parent
    }

    /**
     * Get the string representation of this attribute, implemented as [.html].
     * @return string
     */
    override fun toString(): String {
        return html()!!
    }

    val isDataAttribute: Boolean
        get() = isDataAttribute(key)

    /**
     * Collapsible if it's a boolean attribute and value is empty or same as name
     *
     * @param out output settings
     * @return  Returns whether collapsible or not
     */
    protected fun shouldCollapseAttribute(out: Document.OutputSettings?): Boolean {
        return shouldCollapseAttribute(key, `val`, out)
    }

    override fun equals(o: Any?): Boolean { // note parent not considered
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val attribute = o as Attribute
        if (if (key != null) key != attribute.key else attribute.key != null) return false
        return if (`val` != null) `val` == attribute.`val` else attribute.`val` == null
    }

    override fun hashCode(): Int { // note parent not considered
        var result = if (key != null) key.hashCode() else 0
        result = 31 * result + if (`val` != null) `val`.hashCode() else 0
        return result
    }

    public override fun clone(): Attribute {
        return try {
            super.clone() as Attribute
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val booleanAttributes = arrayOf(
            "allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled",
            "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize",
            "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected",
            "sortable", "truespeed", "typemustmatch"
        )

        @Throws(IOException::class)
        protected fun html(key: String?, `val`: String?, accum: Appendable?, out: Document.OutputSettings?) {
            var key = key
            key = getValidKey(key, out!!.syntax())
            if (key == null) return  // can't write it :(
            htmlNoValidate(key, `val`, accum, out)
        }

        @Throws(IOException::class)
        fun htmlNoValidate(key: String?, `val`: String?, accum: Appendable?, out: Document.OutputSettings?) {
            // structured like this so that Attributes can check we can write first, so it can add whitespace correctly
            accum!!.append(key)
            if (!shouldCollapseAttribute(key, `val`, out)) {
                accum.append("=\"")
                Entities.escape(accum, Attributes.Companion.checkNotNull(`val`), out, true, false, false, false)
                accum.append('"')
            }
        }

        private val xmlKeyValid = Pattern.compile("[a-zA-Z_:][-a-zA-Z0-9_:.]*")
        private val xmlKeyReplace = Pattern.compile("[^-a-zA-Z0-9_:.]")
        private val htmlKeyValid = Pattern.compile("[^\\x00-\\x1f\\x7f-\\x9f \"'/=]+")
        private val htmlKeyReplace = Pattern.compile("[\\x00-\\x1f\\x7f-\\x9f \"'/=]")
        fun getValidKey(key: String?, syntax: Document.OutputSettings.Syntax?): String? {
            // we consider HTML attributes to always be valid. XML checks key validity
            var key = key
            if (syntax == Document.OutputSettings.Syntax.xml && !xmlKeyValid.matcher(key).matches()) {
                key = xmlKeyReplace.matcher(key).replaceAll("")
                return if (xmlKeyValid.matcher(key).matches()) key else null // null if could not be coerced
            } else if (syntax == Document.OutputSettings.Syntax.html && !htmlKeyValid.matcher(key).matches()) {
                key = htmlKeyReplace.matcher(key).replaceAll("")
                return if (htmlKeyValid.matcher(key).matches()) key else null // null if could not be coerced
            }
            return key
        }

        /**
         * Create a new Attribute from an unencoded key and a HTML attribute encoded value.
         * @param unencodedKey assumes the key is not encoded, as can be only run of simple \w chars.
         * @param encodedValue HTML attribute encoded value
         * @return attribute
         */
        fun createFromEncoded(unencodedKey: String?, encodedValue: String?): Attribute {
            val value = Entities.unescape(encodedValue, true)
            return Attribute(unencodedKey, value, null) // parent will get set when Put
        }

        protected fun isDataAttribute(key: String): Boolean {
            return key.startsWith(Attributes.Companion.dataPrefix) && key.length > Attributes.Companion.dataPrefix.length
        }

        // collapse unknown foo=null, known checked=null, checked="", checked=checked; write out others
        protected fun shouldCollapseAttribute(key: String?, `val`: String?, out: Document.OutputSettings?): Boolean {
            return out!!.syntax() == Document.OutputSettings.Syntax.html &&
                    (`val` == null || (`val`.isEmpty() || `val`.equals(key, ignoreCase = true)) && isBooleanAttribute(
                        key
                    ))
        }

        /**
         * Checks if this attribute name is defined as a boolean attribute in HTML5
         */
        @JvmStatic
        fun isBooleanAttribute(key: String?): Boolean {
            return Arrays.binarySearch(booleanAttributes, Normalizer.lowerCase(key)) >= 0
        }
    }
}
