package org.jsoup.parserimport

import org.jsoup.Jsoup
import org.jsoup.TextUtil
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.parser.CharacterReader
import org.jsoup.parser.ParseErrorList.Companion.tracking
import org.jsoup.parser.Parser.Companion.parseFragment
import org.jsoup.select.Elements
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

org.jsoup.parser.Tag.Companion.valueOf
import org.jsoup.nodes.Attributes.put
import org.jsoup.nodes.Node.absUrl
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.Node.setBaseUri
import org.jsoup.nodes.Element.baseUri
import org.jsoup.nodes.Element.select
import org.jsoup.select.Elements.first
import org.jsoup.nodes.Node.attr
import org.jsoup.nodes.Node.hasAttr
import org.jsoup.select.Elements.attr
import org.jsoup.nodes.Node.childNode
import org.jsoup.nodes.Node.remove
import org.jsoup.nodes.Element.text
import org.jsoup.nodes.Element.html
import org.jsoup.nodes.Node.parentNode
import org.jsoup.nodes.Document.createElement
import org.jsoup.nodes.Node.replaceWith
import org.jsoup.nodes.Node.ownerDocument
import org.jsoup.nodes.Element.parent
import org.jsoup.nodes.Element.root
import org.jsoup.nodes.Node.parent
import org.jsoup.nodes.Element.appendText
import org.jsoup.nodes.Element.before
import org.jsoup.nodes.Document.body
import org.jsoup.nodes.Element.after
import org.jsoup.nodes.Node.unwrap
import org.jsoup.nodes.TextNode.text
import org.jsoup.nodes.Element.traverse
import org.jsoup.nodes.Node.nodeName
import org.jsoup.nodes.Element.forEachNode
import org.jsoup.nodes.Node.after
import org.jsoup.nodes.Node.siblingIndex
import org.jsoup.nodes.Node.siblingNodes
import org.jsoup.nodes.Node.previousSibling
import org.jsoup.nodes.Node.nextSibling
import org.jsoup.nodes.Element.siblingElements
import org.jsoup.nodes.Element.previousElementSibling
import org.jsoup.nodes.Element.nextElementSibling
import org.jsoup.nodes.Node.outerHtml
import org.jsoup.nodes.Node.childNodesCopy
import org.jsoup.nodes.Element.insertChildren
import org.jsoup.nodes.Element.hasClass
import org.jsoup.nodes.Document.clone
import org.jsoup.nodes.Element.removeClass
import org.jsoup.nodes.Element.attr
import org.jsoup.nodes.Element.attributes
import org.jsoup.nodes.Attribute.key
import org.jsoup.nodes.Document.outputSettings
import org.jsoup.nodes.Document.OutputSettings.prettyPrint
import org.jsoup.nodes.Element.selectFirst
import org.jsoup.nodes.TextNode.clone
import org.jsoup.nodes.Node.childNodes
import org.jsoup.nodes.Node.firstChild
import org.jsoup.nodes.Node.lastChild
import org.jsoup.nodes.Element.firstElementChild
import org.jsoup.nodes.Element.tagName
import org.jsoup.nodes.Element.lastElementChild
import org.jsoup.nodes.Element.nodeName
import org.jsoup.nodes.Element.normalName
import org.jsoup.nodes.Node.isNode
import org.jsoup.nodes.TextNode.nodeName
import org.jsoup.nodes.Node.normalName
import org.jsoup.nodes.Element.empty
import org.jsoup.nodes.Element.appendChild
import org.jsoup.nodes.Element.children
import org.jsoup.nodes.Element.append
import org.jsoup.nodes.Element.appendElement
import org.jsoup.nodes.Element.hasText
import org.jsoup.nodes.Element.data
import org.jsoup.nodes.Element.parents
import org.jsoup.nodes.Element.wrap
import org.jsoup.nodes.Comment.nodeName
import org.jsoup.nodes.Comment.data
import org.jsoup.nodes.Comment.toString
import org.jsoup.nodes.Comment.clone
import org.jsoup.nodes.Comment.setData
import org.jsoup.nodes.Comment.isXmlDeclaration
import org.jsoup.nodes.Comment.asXmlDeclaration
import org.jsoup.nodes.Element.ownText
import org.jsoup.nodes.Element.wholeText
import org.jsoup.nodes.Element.id
import org.jsoup.nodes.Element.getElementsByTag
import org.jsoup.nodes.TextNode.wholeText
import org.jsoup.nodes.Element.getElementById
import org.jsoup.nodes.Element.child
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.settings
import org.jsoup.nodes.Element.firstElementSibling
import org.jsoup.nodes.Element.lastElementSibling
import org.jsoup.nodes.Element.expectFirst
import org.jsoup.nodes.Element.elementSiblingIndex
import org.jsoup.nodes.Element.getElementsByClass
import org.jsoup.nodes.Element.getElementsByAttribute
import org.jsoup.nodes.Element.getElementsByAttributeValue
import org.jsoup.nodes.Element.className
import org.jsoup.nodes.Element.classNames
import org.jsoup.nodes.Element.addClass
import org.jsoup.nodes.Element.toggleClass
import org.jsoup.nodes.Document.outerHtml
import org.jsoup.nodes.Document.OutputSettings.outline
import org.jsoup.nodes.Document.OutputSettings.indentAmount
import org.jsoup.nodes.Document.OutputSettings.maxPaddingWidth
import org.jsoup.nodes.Document.head
import org.jsoup.nodes.Attributes.asList
import org.jsoup.nodes.Element.prepend
import org.jsoup.nodes.Element.prependElement
import org.jsoup.nodes.Element.prependText
import org.jsoup.nodes.Node.wrap
import org.jsoup.nodes.Node.hasParent
import org.jsoup.select.Elements.last
import org.jsoup.nodes.Element.dataset
import org.jsoup.nodes.Attributes.size
import org.jsoup.nodes.Attributes.get
import org.jsoup.nodes.Node.toString
import org.jsoup.nodes.Element.clone
import org.jsoup.nodes.Node.childNodeSize
import org.jsoup.nodes.Element.textNodes
import org.jsoup.nodes.Element.shallowClone
import org.jsoup.nodes.Node.shallowClone
import org.jsoup.nodes.Element.childNodeSize
import org.jsoup.select.Elements.outerHtml
import org.jsoup.nodes.TextNode.splitText
import org.jsoup.nodes.Element.dataNodes
import org.jsoup.nodes.DataNode.wholeData
import org.jsoup.select.Elements.clone
import org.jsoup.nodes.Element.cssSelector
import org.jsoup.nodes.Node.hasSameValue
import org.jsoup.nodes.Element.removeAttr
import org.jsoup.nodes.Element.allElements
import org.jsoup.nodes.Element.clearAttributes
import org.jsoup.nodes.Element.`is`
import org.jsoup.select.QueryParser.Companion.parse
import org.jsoup.nodes.Element.closest
import org.jsoup.select.Elements.text
import org.jsoup.nodes.Element.appendTo
import org.jsoup.nodes.Document.OutputSettings.charset
import org.jsoup.nodes.Element.getElementsByIndexLessThan
import org.jsoup.nodes.Element.getElementsByIndexGreaterThan
import org.jsoup.nodes.Element.nextElementSiblings
import org.jsoup.nodes.Element.previousElementSiblings
import org.jsoup.select.GenericNodeVisitor.Companion.jvmNodeVisitor
import org.jsoup.nodes.Element.filter
import org.jsoup.select.GenericNodeFilter.Companion.jvmNodeFilter
import org.jsoup.nodes.Element.forEach
import org.jsoup.nodes.Node.addChildren
import org.jsoup.nodes.Element.childrenSize
import org.jsoup.nodes.Element.isBlock
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.parser.Parser.parseInput
import org.jsoup.nodes.Element.appendChildren
import org.jsoup.nodes.Element.prependChildren
import org.jsoup.nodes.Element.hasAttributes
import org.jsoup.nodes.Element.hasChildNodes
import org.jsoup.nodes.Node.attributesSize
import org.jsoup.nodes.Element.wholeOwnText
import org.jsoup.nodes.Element.getElementsByAttributeStarting
import org.jsoup.nodes.Element.getElementsByAttributeValueNot
import org.jsoup.nodes.Element.getElementsByAttributeValueStarting
import org.jsoup.nodes.Element.getElementsByAttributeValueEnding
import org.jsoup.nodes.Element.getElementsByAttributeValueContaining
import org.jsoup.nodes.Element.getElementsByAttributeValueMatching
import org.jsoup.nodes.Element.getElementsByIndexEquals
import org.jsoup.nodes.Element.getElementsContainingText
import org.jsoup.nodes.Element.getElementsContainingOwnText
import org.jsoup.nodes.Element.getElementsMatchingText
import org.jsoup.nodes.Element.getElementsMatchingOwnText
import org.jsoup.nodes.Document.text
import org.jsoup.nodes.Document.title
import org.jsoup.nodes.Document.OutputSettings.escapeMode
import org.jsoup.nodes.Document.location
import org.jsoup.nodes.Document.OutputSettings.syntax
import org.jsoup.nodes.Document.updateMetaCharsetElement
import org.jsoup.nodes.Document.charset
import org.jsoup.nodes.Document.Companion.createShell
import org.jsoup.nodes.LeafNode.attr
import org.jsoup.nodes.Element.prependChild
import org.jsoup.nodes.Document.documentType
import org.jsoup.nodes.DocumentType.name
import org.jsoup.nodes.Document.forms
import org.jsoup.nodes.FormElement.elements
import org.jsoup.nodes.Document.expectForm
import org.jsoup.nodes.Entities.escape
import org.jsoup.nodes.Entities.unescape
import org.jsoup.nodes.Entities.EscapeMode.codepointForName
import org.jsoup.nodes.Entities.EscapeMode.nameForCodepoint
import org.jsoup.nodes.Entities.getByName
import org.jsoup.nodes.Attributes.hasKey
import org.jsoup.nodes.Node.filter
import org.jsoup.nodes.Node.hasAttributes
import org.jsoup.parser.Parser.setTrackPosition
import org.jsoup.parser.Parser.isTrackPosition
import org.jsoup.nodes.Node.sourceRange
import org.jsoup.nodes.Range.isTracked
import org.jsoup.nodes.Range.toString
import org.jsoup.nodes.Element.endSourceRange
import org.jsoup.nodes.Range.start
import org.jsoup.nodes.Range.Position.isTracked
import org.jsoup.nodes.Range.Position.pos
import org.jsoup.nodes.Range.Position.columnNumber
import org.jsoup.nodes.Range.Position.lineNumber
import org.jsoup.nodes.Range.Position.toString
import org.jsoup.nodes.Range.end
import org.jsoup.nodes.CDataNode.text
import org.jsoup.Jsoup.connect
import org.jsoup.Connection.parser
import org.jsoup.Connection.get
import org.jsoup.select.NodeTraversor.traverse
import org.jsoup.nodes.TextNode.isBlank
import org.jsoup.nodes.TextNode.toString
import org.jsoup.nodes.LeafNode.attributes
import org.jsoup.internal.StringUtil.isBlank
import org.jsoup.nodes.TextNode.Companion.createFromEncoded
import org.jsoup.nodes.TextNode.Companion.normaliseWhitespace
import org.jsoup.nodes.TextNode.Companion.stripLeadingWhitespace
import org.jsoup.nodes.LeafNode.hasAttributes
import org.jsoup.nodes.LeafNode.coreValue
import org.jsoup.nodes.LeafNode.hasAttr
import org.jsoup.nodes.LeafNode.removeAttr
import org.jsoup.nodes.LeafNode.baseUri
import org.jsoup.nodes.LeafNode.absUrl
import org.jsoup.nodes.Element.doSetBaseUri
import org.jsoup.nodes.LeafNode.childNodeSize
import org.jsoup.nodes.LeafNode.empty
import org.jsoup.nodes.LeafNode.ensureChildNodes
import org.jsoup.nodes.Attribute.html
import org.jsoup.nodes.Attribute.toString
import org.jsoup.nodes.Attributes.iterator
import org.jsoup.nodes.Attribute.value
import org.jsoup.nodes.Attribute.hasDeclaredValue
import org.jsoup.nodes.Attribute.Companion.isBooleanAttribute
import org.jsoup.nodes.Attribute.setValue
import org.jsoup.nodes.Attribute.parent
import org.jsoup.Connection.ignoreContentType
import org.jsoup.Connection.userAgent
import org.jsoup.Connection.execute
import org.jsoup.Connection.Response.body
import org.jsoup.nodes.Attributes.hasKeyIgnoreCase
import org.jsoup.nodes.Attributes.getIgnoreCase
import org.jsoup.nodes.Attributes.dataset
import org.jsoup.nodes.Attributes.html
import org.jsoup.nodes.Attributes.toString
import org.jsoup.nodes.Attributes.Companion.internalKey
import org.jsoup.nodes.Attributes.remove
import org.jsoup.nodes.Attributes.hasDeclaredValueForKey
import org.jsoup.nodes.Attributes.hasDeclaredValueForKeyIgnoreCase
import org.jsoup.nodes.Attributes.add
import org.jsoup.nodes.Attributes.clone
import org.jsoup.nodes.FormElement.formData
import org.jsoup.nodes.FormElement.submit
import org.jsoup.Connection.request
import org.jsoup.Connection.Base.method
import org.jsoup.Connection.Base.url
import org.jsoup.Connection.Request.data
import org.jsoup.Connection.KeyVal.value
import org.jsoup.Connection.KeyVal.key
import org.jsoup.Connection.data
import org.jsoup.nodes.Document.connection
import org.jsoup.Connection.newRequest
import org.jsoup.Connection.url
import org.jsoup.select.Elements.forms
import org.jsoup.Connection.post
import org.jsoup.Connection.response
import org.jsoup.nodes.DocumentType.publicId
import org.jsoup.nodes.DocumentType.systemId
import org.jsoup.helper.W3CDom.fromJsoup
import org.jsoup.helper.W3CDom.Companion.asString
import org.jsoup.helper.W3CDom.Companion.OutputXml
import org.jsoup.helper.W3CDom.Companion.convert
import org.jsoup.helper.W3CDom.Companion.OutputHtml
import org.jsoup.helper.W3CDom.namespaceAware
import org.jsoup.helper.W3CDom.contextNode
import org.jsoup.helper.DataUtil.getCharsetFromContentType
import org.jsoup.helper.DataUtil.parseInputStream
import org.jsoup.helper.DataUtil.mimeBoundary
import org.jsoup.helper.DataUtil.readToByteBuffer
import org.jsoup.helper.Validate.notNull
import org.jsoup.helper.Validate.notNullParam
import org.jsoup.helper.HttpConnection.request
import org.jsoup.helper.HttpConnection.Companion.connect
import org.jsoup.Connection.Response.parse
import org.jsoup.Connection.Response.bodyAsBytes
import org.jsoup.Connection.Base.header
import org.jsoup.Connection.Base.hasHeader
import org.jsoup.Connection.Base.removeHeader
import org.jsoup.Connection.headers
import org.jsoup.helper.HttpConnection.Response.processResponseHeaders
import org.jsoup.helper.HttpConnection.Base.header
import org.jsoup.Connection.Base.addHeader
import org.jsoup.Connection.Base.headers
import org.jsoup.Connection.Base.multiHeaders
import org.jsoup.Connection.Base.hasHeaderWithValue
import org.jsoup.helper.HttpConnection.Base.cookies
import org.jsoup.helper.HttpConnection.Base.cookie
import org.jsoup.Connection.Request.timeout
import org.jsoup.Connection.timeout
import org.jsoup.Connection.referrer
import org.jsoup.Connection.method
import org.jsoup.Connection.cookie
import org.jsoup.Connection.Base.cookie
import org.jsoup.helper.HttpConnection.KeyVal.Companion.create
import org.jsoup.Connection.KeyVal.hasInputStream
import org.jsoup.Connection.requestBody
import org.jsoup.Connection.Request.requestBody
import org.jsoup.helper.UrlBuilder.build
import org.jsoup.helper.HttpConnection.execute
import org.jsoup.helper.HttpConnection.Base.url
import org.jsoup.parser.Tag.isBlock
import org.jsoup.parser.Tag.formatAsBlock
import org.jsoup.parser.Tag.isInline
import org.jsoup.parser.Tag.isSelfClosing
import org.jsoup.parser.Tag.Companion.isKnownTag
import org.jsoup.parser.Parser.setTrackErrors
import org.jsoup.parser.Parser.Companion.parseBodyFragment
import org.jsoup.parser.Parser.Companion.unescapeEntities
import org.jsoup.parser.Parser.errors
import org.jsoup.parser.ParseError.errorMessage
import org.jsoup.select.Elements.value
import org.jsoup.nodes.Element.value
import org.jsoup.Jsoup.parseBodyFragment
import org.jsoup.internal.StringUtil.normaliseWhitespace
import org.jsoup.parser.ParseError.toString
import org.jsoup.parser.Parser.Companion.parseFragment
import org.jsoup.Jsoup.isValid
import org.jsoup.safety.Safelist.Companion.basic
import org.jsoup.Jsoup.clean
import org.jsoup.safety.Safelist.Companion.relaxed
import org.jsoup.nodes.Document.parser
import org.jsoup.parser.Parser.treeBuilder
import org.jsoup.parser.TreeBuilder.reader
import org.jsoup.parser.Parser.parseFragmentInput
import org.jsoup.select.Elements.select
import org.jsoup.select.Elements.unwrap
import org.jsoup.parser.TokenQueue.consumeTo
import org.jsoup.parser.TokenQueue.chompBalanced
import org.jsoup.parser.TokenQueue.remainder
import org.jsoup.parser.TokenQueue.Companion.unescape
import org.jsoup.parser.TokenQueue.Companion.escapeCssIdentifier
import org.jsoup.parser.TokenQueue.chompToIgnoreCase
import org.jsoup.parser.TokenQueue.consumeWord
import org.jsoup.parser.TokenQueue.addFirst
import org.jsoup.parser.TokenQueue.consumeElementSelector
import org.jsoup.parser.TokenQueue.consumeWhitespace
import org.jsoup.parser.TokenQueue.isEmpty
import org.jsoup.parser.TokenQueue.consumeCssIdentifier
import org.jsoup.parser.ParseSettings.normalizeTag
import org.jsoup.parser.ParseSettings.normalizeAttribute
import org.jsoup.parser.ParseSettings.normalizeAttributes
import org.jsoup.parser.ParseErrorList.Companion.tracking
import org.jsoup.parser.ParseError.position
import org.jsoup.parser.XmlTreeBuilder.parse
import org.jsoup.parser.Parser.Companion.parseXmlFragment
import org.jsoup.nodes.XmlDeclaration.wholeDeclaration
import org.jsoup.nodes.XmlDeclaration.name
import org.jsoup.select.Elements.append
import org.jsoup.nodes.Element.tag
import org.jsoup.parser.Tag.name
import org.jsoup.parser.CharacterReader.pos
import org.jsoup.parser.CharacterReader.current
import org.jsoup.parser.CharacterReader.consume
import org.jsoup.parser.CharacterReader.isEmpty
import org.jsoup.parser.CharacterReader.unconsume
import org.jsoup.parser.CharacterReader.mark
import org.jsoup.parser.CharacterReader.rewindToMark
import org.jsoup.parser.CharacterReader.consumeToEnd
import org.jsoup.parser.CharacterReader.nextIndexOf
import org.jsoup.parser.CharacterReader.consumeTo
import org.jsoup.parser.CharacterReader.advance
import org.jsoup.parser.CharacterReader.consumeToAny
import org.jsoup.parser.CharacterReader.matches
import org.jsoup.parser.CharacterReader.consumeLetterSequence
import org.jsoup.parser.CharacterReader.consumeLetterThenDigitSequence
import org.jsoup.parser.CharacterReader.matchesIgnoreCase
import org.jsoup.parser.CharacterReader.containsIgnoreCase
import org.jsoup.parser.CharacterReader.matchesAny
import org.jsoup.parser.CharacterReader.matchesDigit
import org.jsoup.parser.CharacterReader.rangeEquals
import org.jsoup.parser.CharacterReader.matchConsume
import org.jsoup.parser.CharacterReader.isTrackNewlines
import org.jsoup.parser.CharacterReader.trackNewlines
import org.jsoup.parser.CharacterReader.lineNumber
import org.jsoup.parser.CharacterReader.columnNumber
import org.jsoup.parser.CharacterReader.cursorPos
import org.jsoup.parser.TreeBuilder.parse
import org.jsoup.safety.Safelist.Companion.simpleText
import org.jsoup.safety.Safelist.Companion.basicWithImages
import org.jsoup.safety.Safelist.removeTags
import org.jsoup.safety.Safelist.removeAttributes
import org.jsoup.safety.Safelist.addAttributes
import org.jsoup.safety.Safelist.removeProtocols
import org.jsoup.safety.Safelist.removeEnforcedAttribute
import org.jsoup.safety.Safelist.Companion.none
import org.jsoup.safety.Safelist.addTags
import org.jsoup.safety.Safelist.addProtocols
import org.jsoup.safety.Cleaner.isValid
import org.jsoup.safety.Safelist.preserveRelativeLinks
import org.jsoup.safety.Cleaner.clean
import org.jsoup.safety.Safelist.isSafeTag
import org.jsoup.safety.Safelist.isSafeAttribute
import org.jsoup.safety.Safelist.addEnforcedAttribute
import org.jsoup.safety.Safelist.getEnforcedAttributes
import org.jsoup.nodes.Element.selectXpath
import org.jsoup.select.Elements.removeAttr
import org.jsoup.select.Elements.eachAttr
import org.jsoup.select.Elements.hasAttr
import org.jsoup.select.Elements.hasClass
import org.jsoup.select.Elements.addClass
import org.jsoup.select.Elements.removeClass
import org.jsoup.select.Elements.toggleClass
import org.jsoup.select.Elements.hasText
import org.jsoup.select.Elements.html
import org.jsoup.select.Elements.prepend
import org.jsoup.select.Elements.before
import org.jsoup.select.Elements.after
import org.jsoup.select.Elements.wrap
import org.jsoup.select.Elements.empty
import org.jsoup.select.Elements.remove
import org.jsoup.select.Elements.eq
import org.jsoup.select.Elements.`is`
import org.jsoup.select.Elements.parents
import org.jsoup.select.Elements.not
import org.jsoup.select.Elements.tagName
import org.jsoup.select.Elements.traverse
import org.jsoup.select.Elements.comments
import org.jsoup.select.Elements.textNodes
import org.jsoup.select.Elements.dataNodes
import org.jsoup.nodes.DataNode.setWholeData
import org.jsoup.select.Elements.next
import org.jsoup.select.Elements.nextAll
import org.jsoup.select.Elements.prev
import org.jsoup.select.Elements.prevAll
import org.jsoup.select.Elements.eachText
import org.jsoup.select.NodeTraversor.filter
import org.jsoup.select.CombiningEvaluator.evaluators
import org.jsoup.select.CombiningEvaluator.And.toString
import org.jsoup.internal.StringUtil.join
import org.jsoup.internal.StringUtil.padding
import org.jsoup.internal.StringUtil.isNumeric
import org.jsoup.internal.StringUtil.isWhitespace
import org.jsoup.internal.StringUtil.resolve
import org.jsoup.internal.StringUtil.isAscii
import org.jsoup.Connection.maxBodySize
import org.jsoup.Connection.Response.bodyStream
import org.jsoup.internal.ConstrainableInputStream.readToByteBuffer
import org.jsoup.internal.ConstrainableInputStream.reset
import org.jsoup.Jsoup.newSession
import org.jsoup.Connection.header
import org.jsoup.Connection.ignoreHttpErrors
import org.jsoup.Connection.Response.statusCode
import org.jsoup.Connection.Response.bufferUp
import org.jsoup.Connection.Base.cookies
import org.jsoup.Connection.cookies
import org.jsoup.Connection.Response.charset
import org.jsoup.Connection.Response.contentType
import org.jsoup.Connection.KeyVal.inputStream
import org.jsoup.Connection.Request.parser
import org.jsoup.Connection.Base.hasCookie
import org.jsoup.UnsupportedMimeTypeException.toString
import org.jsoup.Connection.Response.statusMessage
import org.jsoup.Connection.followRedirects
import org.jsoup.Connection.proxy
import org.jsoup.Connection.Request.proxy
import java.util.concurrent.atomic.AtomicInteger
import org.jsoup.select.GenericNodeVisitor
import org.jsoup.select.GenericNodeFilter
import org.junit.jupiter.params.ParameterizedTest
import java.io.IOException
import org.jsoup.integration.ParseTest
import java.nio.charset.Charset
import java.io.ByteArrayInputStream
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.integration.UrlConnectTest
import org.jsoup.nodes.BuildEntities.CharacterRef
import java.io.FileWriter
import org.jsoup.nodes.BuildEntities.ByName
import org.jsoup.nodes.BuildEntities.ByCode
import org.jsoup.integration.servlets.EchoServlet
import org.jsoup.integration.servlets.CookieServlet
import org.junit.jupiter.api.BeforeAll
import org.jsoup.integration.TestServer
import org.jsoup.helper.W3CDomTest
import java.io.SequenceInputStream
import org.jsoup.helper.DataUtilTest.VaryingReadInputStream
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import java.net.MalformedURLException
import org.jsoup.helper.UrlBuilder
import org.jsoup.parser.TokenQueueTest
import java.net.URISyntaxException
import java.io.FileInputStream
import org.jsoup.parser.CharacterReaderTest
import java.io.BufferedReader
import org.jsoup.parser.HtmlTreeBuilderStateTest
import org.jsoup.safety.SafelistTest
import org.junit.jupiter.api.BeforeEach
import org.jsoup.select.CssTest
import org.jsoup.select.XpathTest.AlternateXpathFactory
import org.jsoup.integration.servlets.BaseServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.jsoup.integration.servlets.SlowRider
import java.io.PrintWriter
import javax.servlet.http.HttpServlet
import javax.servlet.ServletException
import javax.servlet.MultipartConfigElement
import javax.servlet.ServletOutputStream
import org.jsoup.integration.servlets.HelloServlet
import org.jsoup.integration.servlets.Deflateservlet
import org.jsoup.integration.servlets.RedirectServlet
import org.jsoup.integration.servlets.InterruptedServlet
import org.jsoup.integration.Benchmark
import java.net.SocketTimeoutException
import org.jsoup.integration.SessionIT.ThreadCatcher
import java.net.InetSocketAddress
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.server.ServerConnector
import org.jsoup.integration.ConnectTest
import java.net.URLDecoder
import org.jsoup.integration.FuzzFixesIT
import org.jsoup.integration.SessionTest
import org.jsoup.integration.SafelistExtensionTest.OpenSafelist
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.ArgumentsSource
import org.jsoup.MultiLocaleExtension
import org.junit.jupiter.api.extension.ExtendWith

class TokeniserStateTest {
    val whiteSpace = charArrayOf('\t', '\n', '\r', '\u000C', ' ')
    val quote = charArrayOf('\'', '"')
    @Test
    fun ensureSearchArraysAreSorted() {
        val arrays = arrayOf<CharArray>()
        for (array in arrays) {
            val copy = Arrays.copyOf(array, array.size)
            Arrays.sort(array)
            Assertions.assertArrayEquals(array, copy)
        }
    }

    @Test
    fun testCharacterReferenceInRcdata() {
        val body = "<textarea>You&I</textarea>"
        val doc = Jsoup.parse(body)
        val els = doc.select("textarea")
        Assertions.assertEquals("You&I", els.text())
    }

    @Test
    fun testBeforeTagName() {
        for (c in whiteSpace) {
            val body = String.format("<div%c>test</div>", c)
            val doc = Jsoup.parse(body)
            val els = doc.select("div")
            Assertions.assertEquals("test", els.text())
        }
    }

    @Test
    fun testEndTagOpen() {
        var body: String
        var doc: Document
        var els: Elements
        body = "<div>hello world</"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("hello world</", els.text())
        body = "<div>hello world</div>"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("hello world", els.text())
        body = "<div>fake</></div>"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("fake", els.text())
        body = "<div>fake</?</div>"
        doc = Jsoup.parse(body)
        els = doc.select("div")
        Assertions.assertEquals("fake", els.text())
    }

    @Test
    fun testRcdataLessthanSign() {
        var body: String
        var doc: Document
        var els: Elements
        body = "<textarea><fake></textarea>"
        doc = Jsoup.parse(body)
        els = doc.select("textarea")
        Assertions.assertEquals("<fake>", els.text())
        body = "<textarea><open"
        doc = Jsoup.parse(body)
        els = doc.select("textarea")
        Assertions.assertEquals("", els.text())
        body = "<textarea>hello world</?fake</textarea>"
        doc = Jsoup.parse(body)
        els = doc.select("textarea")
        Assertions.assertEquals("hello world</?fake", els.text())
    }

    @Test
    fun testRCDATAEndTagName() {
        for (c in whiteSpace) {
            val body = String.format("<textarea>data</textarea%c>", c)
            val doc = Jsoup.parse(body)
            val els = doc.select("textarea")
            Assertions.assertEquals("data", els.text())
        }
    }

    @Test
    fun testCommentEndCoverage() {
        val html =
            "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --! --- --><p>Hello</p></body></html>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val comment = body.childNode(1) as Comment
        Assertions.assertEquals(" <table><tr><td></table> --! --- ", comment.data)
        val p = body.child(1)
        val text = p.childNode(0) as TextNode
        Assertions.assertEquals("Hello", text.wholeText)
    }

    @Test
    fun testCommentEndBangCoverage() {
        val html =
            "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --!---!>--><p>Hello</p></body></html>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val comment = body.childNode(1) as Comment
        Assertions.assertEquals(" <table><tr><td></table> --!-", comment.data)
        val p = body.child(1)
        val text = p.childNode(0) as TextNode
        Assertions.assertEquals("Hello", text.wholeText)
    }

    @Test
    fun testPublicIdentifiersWithWhitespace() {
        val expectedOutput = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\">"
        for (q in quote) {
            for (ws in whiteSpace) {
                val htmls = arrayOf(
                    String.format("<!DOCTYPE html%cPUBLIC %c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                    String.format("<!DOCTYPE html %cPUBLIC %c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                    String.format("<!DOCTYPE html PUBLIC%c%c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                    String.format("<!DOCTYPE html PUBLIC %c%c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                    String.format("<!DOCTYPE html PUBLIC %c-//W3C//DTD HTML 4.0//EN%c%c>", q, q, ws),
                    String.format("<!DOCTYPE html PUBLIC%c-//W3C//DTD HTML 4.0//EN%c%c>", q, q, ws)
                )
                for (html in htmls) {
                    val doc = Jsoup.parse(html)
                    Assertions.assertEquals(expectedOutput, doc.childNode(0).outerHtml())
                }
            }
        }
    }

    @Test
    fun testSystemIdentifiersWithWhitespace() {
        val expectedOutput = "<!DOCTYPE html SYSTEM \"http://www.w3.org/TR/REC-html40/strict.dtd\">"
        for (q in quote) {
            for (ws in whiteSpace) {
                val htmls = arrayOf(
                    String.format(
                        "<!DOCTYPE html%cSYSTEM %chttp://www.w3.org/TR/REC-html40/strict.dtd%c>",
                        ws,
                        q,
                        q
                    ),
                    String.format("<!DOCTYPE html %cSYSTEM %chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                    String.format("<!DOCTYPE html SYSTEM%c%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                    String.format("<!DOCTYPE html SYSTEM %c%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                    String.format("<!DOCTYPE html SYSTEM %chttp://www.w3.org/TR/REC-html40/strict.dtd%c%c>", q, q, ws),
                    String.format("<!DOCTYPE html SYSTEM%chttp://www.w3.org/TR/REC-html40/strict.dtd%c%c>", q, q, ws)
                )
                for (html in htmls) {
                    val doc = Jsoup.parse(html)
                    Assertions.assertEquals(expectedOutput, doc.childNode(0).outerHtml())
                }
            }
        }
    }

    @Test
    fun testPublicAndSystemIdentifiersWithWhitespace() {
        val expectedOutput = ("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\""
                + " \"http://www.w3.org/TR/REC-html40/strict.dtd\">")
        for (q in quote) {
            for (ws in whiteSpace) {
                val htmls = arrayOf(
                    String.format(
                        "<!DOCTYPE html PUBLIC %c-//W3C//DTD HTML 4.0//EN%c"
                                + "%c%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", q, q, ws, q, q
                    ), String.format(
                        "<!DOCTYPE html PUBLIC %c-//W3C//DTD HTML 4.0//EN%c"
                                + "%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", q, q, q, q
                    )
                )
                for (html in htmls) {
                    val doc = Jsoup.parse(html)
                    Assertions.assertEquals(expectedOutput, doc.childNode(0).outerHtml())
                }
            }
        }
    }

    @Test
    fun handlesLessInTagThanAsNewTag() {
        // out of spec, but clear author intent
        val html = "<p\n<p<div id=one <span>Two"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals(
            "<p></p><p></p><div id=\"one\"><span>Two</span></div>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun testUnconsumeAtBufferBoundary() {
        val triggeringSnippet = "<a href=\"\"foo"
        val padding =
            CharArray(CharacterReader.readAheadLimit - triggeringSnippet.length + 2) // The "foo" part must be just at the limit.
        Arrays.fill(padding, ' ')
        val paddedSnippet = String(padding) + triggeringSnippet
        val errorList = tracking(1)
        parseFragment(paddedSnippet, null, "", errorList)
        Assertions.assertEquals(CharacterReader.readAheadLimit - 1, errorList[0].position)
    }

    @Test
    fun testUnconsumeAfterBufferUp() {
        // test for after consume() a bufferUp occurs (look-forward) but then attempts to unconsume. Would throw a "No buffer left to unconsume"
        val triggeringSnippet = "<title>One <span>Two"
        val padding =
            CharArray(CharacterReader.readAheadLimit - triggeringSnippet.length + 8) // The "<span" part must be just at the limit. The "containsIgnoreCase" scan does a bufferUp, losing the unconsume
        Arrays.fill(padding, ' ')
        val paddedSnippet = String(padding) + triggeringSnippet
        val errorList = tracking(1)
        parseFragment(paddedSnippet, null, "", errorList)
        // just asserting we don't get a WTF on unconsume
    }

    @Test
    fun testOpeningAngleBracketInsteadOfAttribute() {
        val triggeringSnippet = "<html <"
        val errorList = tracking(1)
        parseFragment(triggeringSnippet, null, "", errorList)
        Assertions.assertEquals(6, errorList[0].position)
    }

    @Test
    fun testMalformedSelfClosingTag() {
        val triggeringSnippet = "<html /ouch"
        val errorList = tracking(1)
        parseFragment(triggeringSnippet, null, "", errorList)
        Assertions.assertEquals(7, errorList[0].position)
    }

    @Test
    fun testOpeningAngleBracketInTagName() {
        val triggeringSnippet = "<html<"
        val errorList = tracking(1)
        parseFragment(triggeringSnippet, null, "", errorList)
        Assertions.assertEquals(5, errorList[0].position)
    }

    @Test
    fun rcData() {
        val doc = Jsoup.parse("<title>One \u0000Two</title>")
        Assertions.assertEquals("One �Two", doc.title())
    }

    @Test
    fun plaintext() {
        val doc = Jsoup.parse("<div>One<plaintext><div>Two</plaintext>\u0000no < Return")
        Assertions.assertEquals(
            "<html><head></head><body><div>One<plaintext>&lt;div&gt;Two&lt;/plaintext&gt;�no &lt; Return</plaintext></div></body></html>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun nullInTag() {
        val doc = Jsoup.parse("<di\u0000v>One</di\u0000v>Two")
        Assertions.assertEquals("<di�v>\n One\n</di�v>Two", doc.body().html())
    }

    @Test
    fun attributeValUnquoted() {
        var doc = Jsoup.parse("<p name=foo&lt;bar>")
        val p = doc.selectFirst("p")
        Assertions.assertEquals("foo<bar", p!!.attr("name"))
        doc = Jsoup.parse("<p foo=")
        Assertions.assertEquals("<p foo></p>", doc.body().html())
    }
}
