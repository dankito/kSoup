package net.dankito.ksoup.parser

import net.dankito.ksoup.helper.Validate
import net.dankito.ksoup.nodes.*
import net.dankito.ksoup.nodes.Range.Position
import net.dankito.ksoup.parser.Token.EndTag
import net.dankito.ksoup.parser.Token.StartTag
import java.io.Reader

/**
 * @author Jonathan Hedley
 */
abstract class TreeBuilder {
    // TODO: make TreeBuilder immutable so that we don't have to deal with null values
    internal var parser: Parser? = null
    var reader: CharacterReader? = null
    internal var tokeniser: Tokeniser? = null
    protected lateinit var doc: Document // current doc we are building into
    internal var stack = ArrayList<Element>(32) // the stack of open elements
    internal var baseUri: String? = null // current base uri, for creating new elements
    protected var currentToken: Token? = null // currentToken is used only for error tracking.
    internal lateinit var settings: ParseSettings
    protected var seenTags = HashMap<String, Tag>() // tags we've used in this parse; saves tag GC for custom tags.
    private val start = StartTag() // start tag to process
    private val end = EndTag()
    private var trackSourceRange = false // optionally tracks the source range of nodes

    abstract fun defaultSettings(): ParseSettings

    protected open fun initialiseParse(input: Reader, baseUri: String, parser: Parser) {
        Validate.notNullParam(input, "input")
        Validate.notNullParam(baseUri, "baseUri")
        Validate.notNull(parser)

        doc = Document(baseUri)
        doc.parser(parser)

        this.parser = parser
        settings = parser.settings()

        val reader = CharacterReader(input)
        this.reader = reader

        trackSourceRange = parser.isTrackPosition
        reader.trackNewlines(parser.isTrackErrors || trackSourceRange) // when tracking errors or source ranges, enable newline tracking for better legibility
        currentToken = null
        tokeniser = Tokeniser(reader, parser.errors)
        stack = ArrayList(32)
        seenTags = HashMap()
        this.baseUri = baseUri
    }

    fun parse(input: Reader, baseUri: String, parser: Parser): Document {
        initialiseParse(input, baseUri, parser)
        runParser()

        // tidy up - as the Parser and Treebuilder are retained in document for settings / fragments
        reader?.close()
        reader = null
        tokeniser = null
        stack.clear()
        seenTags.clear()

        return doc
    }

    /**
     * Create a new copy of this TreeBuilder
     * @return copy, ready for a new parse
     */
    abstract fun newInstance(): TreeBuilder

    abstract fun parseFragment(inputFragment: String, context: Element?, baseUri: String, parser: Parser): List<Node>

    protected fun runParser() {
        val tokeniser = tokeniser
        val eof = Token.TokenType.EOF

        if (tokeniser != null) {
            while (true) {
                val token = tokeniser.read()
                process(token)
                token.reset()
                if (token.type === eof) {
                    break
                }
            }
        }
    }

    abstract fun process(token: Token): Boolean

    internal fun processStartTag(name: String): Boolean {
        // these are "virtual" start tags (auto-created by the treebuilder), so not tracking the start position
        val start: StartTag = start
        if (currentToken === start) { // don't recycle an in-use token
            return process(StartTag().name(name))
        }
        return process(start.reset().name(name))
    }

    fun processStartTag(name: String, attrs: Attributes?): Boolean {
        val start: StartTag = start
        if (currentToken === start) { // don't recycle an in-use token
            return process(StartTag().nameAttr(name, attrs))
        }
        start.reset()
        start.nameAttr(name, attrs)
        return process(start)
    }

    internal fun processEndTag(name: String): Boolean {
        if (currentToken === end) { // don't recycle an in-use token
            return process(EndTag().name(name))
        }
        return process(end.reset().name(name))
    }

    /**
     * Get the current element (last on the stack). If all items have been removed, returns the document instead
     * (which might not actually be on the stack; use stack.size() == 0 to test if required.
     * @return the last element on the stack, if any; or the root document
     */
    internal fun currentElement(): Element {
        return stack.lastOrNull() ?: doc
    }

    /**
     * Checks if the Current Element's normal name equals the supplied name.
     * @param normalName name to check
     * @return true if there is a current element on the stack, and its name equals the supplied
     */
    internal fun currentElementIs(normalName: String?): Boolean {
        if (stack.isEmpty()) return false

        val current = currentElement()
        return current.normalName() == normalName
    }

    /**
     * If the parser is tracking errors, add an error at the current position.
     * @param msg error message template
     * @param args template arguments
     */
    protected fun error(msg: String) {
        parser?.errors?.let { errors ->
            if (errors.canAddError()) {
                reader?.let { reader ->
                    errors.add(ParseError(reader, msg))
                }
            }
        }
    }

    /**
     * (An internal method, visible for Element. For HTML parse, signals that script and style text should be treated as
     * Data Nodes).
     */
    internal open fun isContentForTagData(normalName: String): Boolean {
        return false
    }

    internal fun tagFor(tagName: String, settings: ParseSettings): Tag {
        var tag = seenTags[tagName] // note that we don't normalize the cache key. But tag via valueOf may be normalized.
        if (tag == null) {
            tag = Tag.valueOf(tagName, settings)
            seenTags[tagName] = tag
        }
        return tag
    }

    /**
     * Called by implementing TreeBuilders when a node has been inserted. This implementation includes optionally tracking
     * the source range of the node.
     * @param node the node that was just inserted
     * @param token the (optional) token that created this node
     */
    internal fun onNodeInserted(node: Node, token: Token?) {
        trackNodePosition(node, token, true)
    }

    /**
     * Called by implementing TreeBuilders when a node is explicitly closed. This implementation includes optionally
     * tracking the closing source range of the node.
     * @param node the node being closed
     * @param token the end-tag token that closed this node
     */
    protected fun onNodeClosed(node: Node, token: Token?) {
        trackNodePosition(node, token, false)
    }

    private fun trackNodePosition(node: Node, token: Token?, start: Boolean) {
        val reader = this.reader
        if (trackSourceRange && token != null && reader != null) {
            val startPos = token.startPos
            if (startPos == Token.Unset) return  // untracked, virtual token

            val startRange = Position(startPos, reader.lineNumber(startPos), reader.columnNumber(startPos))
            val endPos: Int = token.endPos
            val endRange = Position(endPos, reader.lineNumber(endPos), reader.columnNumber(endPos))
            val range = Range(startRange, endRange)
            range.track(node, start)
        }
    }
}
