package org.jsoup.parser

import org.jsoup.helper.Validate
import org.jsoup.nodes.Attributes

/**
 * Parse tokens for the Tokeniser.
 */
abstract class Token private constructor() {
    var type: TokenType? = null
    private var startPos: Int = 0
    private var endPos: Int = Unset // position in CharacterReader this token was read from
    fun tokenType(): String {
        return this.javaClass.getSimpleName()
    }

    /**
     * Reset the data represent by this token, for reuse. Prevents the need to create transfer objects for every
     * piece of data, which immediately get GCed.
     */
    open fun reset(): Token {
        startPos = Unset
        endPos = Unset
        return this
    }

    fun startPos(): Int {
        return startPos
    }

    fun startPos(pos: Int) {
        startPos = pos
    }

    fun endPos(): Int {
        return endPos
    }

    fun endPos(pos: Int) {
        endPos = pos
    }

    class Doctype() : Token() {
        val name: StringBuilder = StringBuilder()
        var pubSysKey: String? = null
        val publicIdentifier: StringBuilder = StringBuilder()
        val systemIdentifier: StringBuilder = StringBuilder()
        var isForceQuirks: Boolean = false

        init {
            type = TokenType.Doctype
        }

        public override fun reset(): Token {
            super.reset()
            reset(name)
            pubSysKey = null
            reset(publicIdentifier)
            reset(systemIdentifier)
            isForceQuirks = false
            return this
        }

        fun getName(): String {
            return name.toString()
        }

        fun getPublicIdentifier(): String {
            return publicIdentifier.toString()
        }

        fun getSystemIdentifier(): String {
            return systemIdentifier.toString()
        }

        public override fun toString(): String {
            return "<!doctype " + getName() + ">"
        }
    }

    internal abstract class Tag() : Token() {
        var tagName: String? = null
        var normalName: String? = null // lc version of tag name, for case insensitive tree build
        private val attrName: StringBuilder = StringBuilder() // try to get attr names and vals in one shot, vs Builder
        private var attrNameS: String? = null
        private var hasAttrName: Boolean = false
        private val attrValue: StringBuilder = StringBuilder()
        private var attrValueS: String? = null
        private var hasAttrValue: Boolean = false
        private var hasEmptyAttrValue: Boolean = false // distinguish boolean attribute from empty string value
        var isSelfClosing: Boolean = false
        var attributes: Attributes? =
            null // start tags get attributes on construction. End tags get attributes on first new attribute (but only for parser convenience, not used).

        public override fun reset(): Tag {
            super.reset()
            tagName = null
            normalName = null
            reset(attrName)
            attrNameS = null
            hasAttrName = false
            reset(attrValue)
            attrValueS = null
            hasEmptyAttrValue = false
            hasAttrValue = false
            isSelfClosing = false
            attributes = null
            return this
        }

        fun newAttribute() {
            if (attributes == null) attributes = Attributes()
            if (hasAttrName && attributes!!.size() < MaxAttributes) {
                // the tokeniser has skipped whitespace control chars, but trimming could collapse to empty for other control codes, so verify here
                var name: String = if (attrName.length > 0) attrName.toString() else (attrNameS)!!
                name = name.trim({ it <= ' ' })
                if (name.length > 0) {
                    val value: String?
                    if (hasAttrValue) value =
                        if (attrValue.length > 0) attrValue.toString() else attrValueS else if (hasEmptyAttrValue) value =
                        "" else value = null
                    // note that we add, not put. So that the first is kept, and rest are deduped, once in a context where case sensitivity is known (the appropriate tree builder).
                    attributes!!.add(name, value)
                }
            }
            reset(attrName)
            attrNameS = null
            hasAttrName = false
            reset(attrValue)
            attrValueS = null
            hasAttrValue = false
            hasEmptyAttrValue = false
        }

        fun hasAttributes(): Boolean {
            return attributes != null
        }

        fun hasAttribute(key: String?): Boolean {
            return attributes != null && attributes!!.hasKey(key)
        }

        fun finaliseTag() {
            // finalises for emit
            if (hasAttrName) {
                newAttribute()
            }
        }

        /** Preserves case  */
        fun name(): String? { // preserves case, for input into Tag.valueOf (which may drop case)
            Validate.isFalse(tagName == null || tagName!!.length == 0)
            return tagName
        }

        /** Lower case  */
        fun normalName(): String? { // lower case, used in tree building for working out where in tree it should go
            return normalName
        }

        fun toStringName(): String {
            return if (tagName != null) tagName!! else "[unset]"
        }

        fun name(name: String?): Tag {
            tagName = name
            normalName = ParseSettings.Companion.normalName(tagName)
            return this
        }

        // these appenders are rarely hit in not null state-- caused by null chars.
        fun appendTagName(append: String?) {
            // might have null chars - need to replace with null replacement character
            var append: String? = append
            append = append!!.replace(TokeniserState.Companion.nullChar, Tokeniser.Companion.replacementChar)
            tagName = if (tagName == null) append else tagName + append
            normalName = ParseSettings.Companion.normalName(tagName)
        }

        fun appendTagName(append: Char) {
            appendTagName(append.toString())
        }

        fun appendAttributeName(append: String?) {
            // might have null chars because we eat in one pass - need to replace with null replacement character
            var append: String? = append
            append = append!!.replace(TokeniserState.Companion.nullChar, Tokeniser.Companion.replacementChar)
            ensureAttrName()
            if (attrName.length == 0) {
                attrNameS = append
            } else {
                attrName.append(append)
            }
        }

        fun appendAttributeName(append: Char) {
            ensureAttrName()
            attrName.append(append)
        }

        fun appendAttributeValue(append: String?) {
            ensureAttrValue()
            if (attrValue.length == 0) {
                attrValueS = append
            } else {
                attrValue.append(append)
            }
        }

        fun appendAttributeValue(append: Char) {
            ensureAttrValue()
            attrValue.append(append)
        }

        fun appendAttributeValue(append: CharArray?) {
            ensureAttrValue()
            attrValue.append(append)
        }

        fun appendAttributeValue(appendCodepoints: IntArray) {
            ensureAttrValue()
            for (codepoint: Int in appendCodepoints) {
                attrValue.appendCodePoint(codepoint)
            }
        }

        fun setEmptyAttributeValue() {
            hasEmptyAttrValue = true
        }

        private fun ensureAttrName() {
            hasAttrName = true
            // if on second hit, we'll need to move to the builder
            if (attrNameS != null) {
                attrName.append(attrNameS)
                attrNameS = null
            }
        }

        private fun ensureAttrValue() {
            hasAttrValue = true
            // if on second hit, we'll need to move to the builder
            if (attrValueS != null) {
                attrValue.append(attrValueS)
                attrValueS = null
            }
        }

        abstract override fun toString(): String

        companion object {
            /* Limits runaway crafted HTML from spewing attributes and getting a little sluggish in ensureCapacity.
        Real-world HTML will P99 around 8 attributes, so plenty of headroom. Implemented here and not in the Attributes
        object so that API users can add more if ever required. */
            private val MaxAttributes: Int = 512
        }
    }

    class StartTag() : Tag() {
        init {
            type = TokenType.StartTag
        }

        public override fun reset(): Tag {
            super.reset()
            attributes = null
            return this
        }

        fun nameAttr(name: String?, attributes: Attributes?): StartTag {
            tagName = name
            this.attributes = attributes
            normalName = ParseSettings.Companion.normalName(tagName)
            return this
        }

        public override fun toString(): String {
            if (hasAttributes() && attributes!!.size() > 0) return "<" + toStringName() + " " + attributes.toString() + ">" else return "<" + toStringName() + ">"
        }
    }

    class EndTag() : Tag() {
        init {
            type = TokenType.EndTag
        }

        public override fun toString(): String {
            return "</" + toStringName() + ">"
        }
    }

    class Comment() : Token() {
        private val data: StringBuilder = StringBuilder()
        private var dataS: String? = null // try to get in one shot
        var bogus: Boolean = false
        public override fun reset(): Token {
            super.reset()
            reset(data)
            dataS = null
            bogus = false
            return this
        }

        init {
            type = TokenType.Comment
        }

        fun getData(): String {
            return if (dataS != null) dataS!! else data.toString()
        }

        fun append(append: String?): Comment {
            ensureData()
            if (data.length == 0) {
                dataS = append
            } else {
                data.append(append)
            }
            return this
        }

        fun append(append: Char): Comment {
            ensureData()
            data.append(append)
            return this
        }

        private fun ensureData() {
            // if on second hit, we'll need to move to the builder
            if (dataS != null) {
                data.append(dataS)
                dataS = null
            }
        }

        public override fun toString(): String {
            return "<!--" + getData() + "-->"
        }
    }

    open class Character() : Token(), Cloneable {
        var data: String? = null
            private set

        init {
            type = TokenType.Character
        }

        public override fun reset(): Token {
            super.reset()
            data = null
            return this
        }

        fun data(data: String?): Character {
            this.data = data
            return this
        }

        public override fun toString(): String {
            return (data)!!
        }

        public override fun clone(): Character {
            try {
                return super.clone() as Character
            } catch (e: CloneNotSupportedException) {
                throw RuntimeException(e)
            }
        }
    }

    internal class CData(data: String?) : Character() {
        init {
            data(data)
        }

        public override fun toString(): String {
            return "<![CDATA[" + data + "]]>"
        }
    }

    internal class EOF() : Token() {
        init {
            type = TokenType.EOF
        }

        public override fun reset(): Token {
            super.reset()
            return this
        }

        public override fun toString(): String {
            return ""
        }
    }

    val isDoctype: Boolean
        get() {
            return type == TokenType.Doctype
        }

    fun asDoctype(): Doctype {
        return this as Doctype
    }

    val isStartTag: Boolean
        get() {
            return type == TokenType.StartTag
        }

    fun asStartTag(): StartTag {
        return this as StartTag
    }

    val isEndTag: Boolean
        get() {
            return type == TokenType.EndTag
        }

    fun asEndTag(): EndTag {
        return this as EndTag
    }

    val isComment: Boolean
        get() {
            return type == TokenType.Comment
        }

    fun asComment(): Comment {
        return this as Comment
    }

    val isCharacter: Boolean
        get() {
            return type == TokenType.Character
        }
    val isCData: Boolean
        get() {
            return this is CData
        }

    fun asCharacter(): Character {
        return this as Character
    }

    val isEOF: Boolean
        get() {
            return type == TokenType.EOF
        }

    enum class TokenType {
        Doctype,
        StartTag,
        EndTag,
        Comment,
        Character,

        // note no CData - treated in builder as an extension of Character
        EOF
    }

    companion object {
        val Unset: Int = -1
        fun reset(sb: StringBuilder?) {
            if (sb != null) {
                sb.delete(0, sb.length)
            }
        }
    }
}
