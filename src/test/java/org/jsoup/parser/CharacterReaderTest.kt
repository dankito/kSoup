package org.jsoup.parserimport

import org.jsoup.Connection.KeyVal.value
import org.jsoup.UncheckedIOException
import org.jsoup.integration.ParseTest
import org.jsoup.nodes.Attribute.value
import org.jsoup.nodes.Element.value
import org.jsoup.parser.CharacterReader
import org.jsoup.parser.CharacterReaderTest
import org.jsoup.select.Elements.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*

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
import org.jsoup.nodes.ElementTest
import org.junit.jupiter.params.ParameterizedTest
import org.jsoup.integration.ParseTest
import java.nio.charset.Charset
import org.jsoup.nodes.DocumentTest
import org.jsoup.nodes.PositionTest
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.integration.UrlConnectTest
import org.jsoup.nodes.BuildEntities.CharacterRef
import org.jsoup.nodes.BuildEntities
import org.jsoup.nodes.BuildEntities.ByName
import org.jsoup.nodes.BuildEntities.ByCode
import org.jsoup.integration.servlets.EchoServlet
import org.jsoup.integration.servlets.CookieServlet
import org.junit.jupiter.api.BeforeAll
import org.jsoup.integration.TestServer
import org.jsoup.helper.W3CDomTest
import org.jsoup.helper.DataUtilTest.VaryingReadInputStream
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import java.util.Locale
import java.net.MalformedURLException
import org.jsoup.helper.UrlBuilder
import org.jsoup.parser.TokenQueueTest
import java.net.URISyntaxException
import org.jsoup.parser.CharacterReaderTest
import org.jsoup.parser.HtmlTreeBuilderStateTest
import org.jsoup.safety.SafelistTest
import org.junit.jupiter.api.BeforeEach
import org.jsoup.select.CssTest
import org.jsoup.select.XpathTest.AlternateXpathFactory
import org.jsoup.integration.servlets.BaseServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.jsoup.integration.servlets.SlowRider
import javax.servlet.http.HttpServlet
import javax.servlet.ServletException
import java.util.Enumeration
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

/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class CharacterReaderTest {
    @Test
    fun consume() {
        val r = CharacterReader("one")
        Assertions.assertEquals(0, r.pos())
        Assertions.assertEquals('o', r.current())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.current())
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
    }

    @Test
    fun unconsume() {
        val r = CharacterReader("one")
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('n', r.current())
        r.unconsume()
        Assertions.assertEquals('o', r.current())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        r.unconsume()
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals('e', r.current())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
        r.unconsume() // read past, so have to eat again
        Assertions.assertTrue(r.isEmpty)
        r.unconsume()
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertEquals(CharacterReader.EOF, r.consume())
        Assertions.assertTrue(r.isEmpty)

        // unconsume all remaining characters
        for (i in 0..3) {
            r.unconsume()
        }
        Assertions.assertThrows(UncheckedIOException::class.java) { r.unconsume() }
    }

    @Test
    fun mark() {
        val r = CharacterReader("one")
        r.consume()
        r.mark()
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('e', r.consume())
        Assertions.assertTrue(r.isEmpty)
        r.rewindToMark()
        Assertions.assertEquals(1, r.pos())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals(2, r.pos())
    }

    @Test
    fun rewindToMark() {
        val r = CharacterReader("nothing")
        // marking should be invalid
        Assertions.assertThrows(UncheckedIOException::class.java) { r.rewindToMark() }
    }

    @Test
    fun consumeToEnd() {
        val `in` = "one two three"
        val r = CharacterReader(`in`)
        val toEnd = r.consumeToEnd()
        Assertions.assertEquals(`in`, toEnd)
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun nextIndexOfChar() {
        val `in` = "blah blah"
        val r = CharacterReader(`in`)
        Assertions.assertEquals(-1, r.nextIndexOf('x'))
        Assertions.assertEquals(3, r.nextIndexOf('h'))
        val pull = r.consumeTo('h')
        Assertions.assertEquals("bla", pull)
        r.consume()
        Assertions.assertEquals(2, r.nextIndexOf('l'))
        Assertions.assertEquals(" blah", r.consumeToEnd())
        Assertions.assertEquals(-1, r.nextIndexOf('x'))
    }

    @Test
    fun nextIndexOfString() {
        val `in` = "One Two something Two Three Four"
        val r = CharacterReader(`in`)
        Assertions.assertEquals(-1, r.nextIndexOf("Foo"))
        Assertions.assertEquals(4, r.nextIndexOf("Two"))
        Assertions.assertEquals("One Two ", r.consumeTo("something"))
        Assertions.assertEquals(10, r.nextIndexOf("Two"))
        Assertions.assertEquals("something Two Three Four", r.consumeToEnd())
        Assertions.assertEquals(-1, r.nextIndexOf("Two"))
    }

    @Test
    fun nextIndexOfUnmatched() {
        val r = CharacterReader("<[[one]]")
        Assertions.assertEquals(-1, r.nextIndexOf("]]>"))
    }

    @Test
    fun consumeToChar() {
        val r = CharacterReader("One Two Three")
        Assertions.assertEquals("One ", r.consumeTo('T'))
        Assertions.assertEquals("", r.consumeTo('T')) // on Two
        Assertions.assertEquals('T', r.consume())
        Assertions.assertEquals("wo ", r.consumeTo('T'))
        Assertions.assertEquals('T', r.consume())
        Assertions.assertEquals("hree", r.consumeTo('T')) // consume to end
    }

    @Test
    fun consumeToString() {
        val r = CharacterReader("One Two Two Four")
        Assertions.assertEquals("One ", r.consumeTo("Two"))
        Assertions.assertEquals('T', r.consume())
        Assertions.assertEquals("wo ", r.consumeTo("Two"))
        Assertions.assertEquals('T', r.consume())
        // To handle strings straddling across buffers, consumeTo() may return the
        // data in multiple pieces near EOF.
        val builder = StringBuilder()
        var part: String
        do {
            part = r.consumeTo("Qux")
            builder.append(part)
        } while (!part.isEmpty())
        Assertions.assertEquals("wo Four", builder.toString())
    }

    @Test
    fun advance() {
        val r = CharacterReader("One Two Three")
        Assertions.assertEquals('O', r.consume())
        r.advance()
        Assertions.assertEquals('e', r.consume())
    }

    @Test
    fun consumeToAny() {
        val r = CharacterReader("One &bar; qux")
        Assertions.assertEquals("One ", r.consumeToAny('&', ';'))
        Assertions.assertTrue(r.matches('&'))
        Assertions.assertTrue(r.matches("&bar;"))
        Assertions.assertEquals('&', r.consume())
        Assertions.assertEquals("bar", r.consumeToAny('&', ';'))
        Assertions.assertEquals(';', r.consume())
        Assertions.assertEquals(" qux", r.consumeToAny('&', ';'))
    }

    @Test
    fun consumeLetterSequence() {
        val r = CharacterReader("One &bar; qux")
        Assertions.assertEquals("One", r.consumeLetterSequence())
        Assertions.assertEquals(" &", r.consumeTo("bar;"))
        Assertions.assertEquals("bar", r.consumeLetterSequence())
        Assertions.assertEquals("; qux", r.consumeToEnd())
    }

    @Test
    fun consumeLetterThenDigitSequence() {
        val r = CharacterReader("One12 Two &bar; qux")
        Assertions.assertEquals("One12", r.consumeLetterThenDigitSequence())
        Assertions.assertEquals(' ', r.consume())
        Assertions.assertEquals("Two", r.consumeLetterThenDigitSequence())
        Assertions.assertEquals(" &bar; qux", r.consumeToEnd())
    }

    @Test
    fun matches() {
        val r = CharacterReader("One Two Three")
        Assertions.assertTrue(r.matches('O'))
        Assertions.assertTrue(r.matches("One Two Three"))
        Assertions.assertTrue(r.matches("One"))
        Assertions.assertFalse(r.matches("one"))
        Assertions.assertEquals('O', r.consume())
        Assertions.assertFalse(r.matches("One"))
        Assertions.assertTrue(r.matches("ne Two Three"))
        Assertions.assertFalse(r.matches("ne Two Three Four"))
        Assertions.assertEquals("ne Two Three", r.consumeToEnd())
        Assertions.assertFalse(r.matches("ne"))
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun matchesIgnoreCase() {
        val r = CharacterReader("One Two Three")
        Assertions.assertTrue(r.matchesIgnoreCase("O"))
        Assertions.assertTrue(r.matchesIgnoreCase("o"))
        Assertions.assertTrue(r.matches('O'))
        Assertions.assertFalse(r.matches('o'))
        Assertions.assertTrue(r.matchesIgnoreCase("One Two Three"))
        Assertions.assertTrue(r.matchesIgnoreCase("ONE two THREE"))
        Assertions.assertTrue(r.matchesIgnoreCase("One"))
        Assertions.assertTrue(r.matchesIgnoreCase("one"))
        Assertions.assertEquals('O', r.consume())
        Assertions.assertFalse(r.matchesIgnoreCase("One"))
        Assertions.assertTrue(r.matchesIgnoreCase("NE Two Three"))
        Assertions.assertFalse(r.matchesIgnoreCase("ne Two Three Four"))
        Assertions.assertEquals("ne Two Three", r.consumeToEnd())
        Assertions.assertFalse(r.matchesIgnoreCase("ne"))
    }

    @Test
    fun containsIgnoreCase() {
        val r = CharacterReader("One TWO three")
        Assertions.assertTrue(r.containsIgnoreCase("two"))
        Assertions.assertTrue(r.containsIgnoreCase("three"))
        // weird one: does not find one, because it scans for consistent case only
        Assertions.assertFalse(r.containsIgnoreCase("one"))
    }

    @Test
    fun containsIgnoreCaseBuffer() {
        val html =
            "<p><p><p></title><p></TITLE><p>" + CharacterReaderTest.Companion.BufferBuster("Foo Bar Qux ") + "<foo><bar></title>"
        val r = CharacterReader(html)
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>")) // cached, but we only test functionally here
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        r.consumeTo("</title>")
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        r.consumeTo("<p>")
        Assertions.assertTrue(r.matches("<p>"))
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>"))
        Assertions.assertFalse(r.containsIgnoreCase("</not>"))
        r.consumeTo("</TITLE>")
        r.consumeTo("<p>")
        Assertions.assertTrue(r.matches("<p>"))
        Assertions.assertFalse(r.containsIgnoreCase("</title>")) // because we haven't buffered up yet, we don't know
        r.consumeTo("<foo>")
        Assertions.assertFalse(r.matches("<foo>")) // buffer underrun
        r.consumeTo("<foo>")
        Assertions.assertTrue(r.matches("<foo>")) // cross the buffer
        Assertions.assertTrue(r.containsIgnoreCase("</TITLE>"))
        Assertions.assertTrue(r.containsIgnoreCase("</title>"))
    }

    @Test
    fun matchesAny() {
        val scan = charArrayOf(' ', '\n', '\t')
        val r = CharacterReader("One\nTwo\tThree")
        Assertions.assertFalse(r.matchesAny(*scan))
        Assertions.assertEquals("One", r.consumeToAny(*scan))
        Assertions.assertTrue(r.matchesAny(*scan))
        Assertions.assertEquals('\n', r.consume())
        Assertions.assertFalse(r.matchesAny(*scan))
        // nothing to match
        r.consumeToEnd()
        Assertions.assertTrue(r.isEmpty)
        Assertions.assertFalse(r.matchesAny(*scan))
    }

    @Test
    fun matchesDigit() {
        val r = CharacterReader("42")
        r.consumeToEnd()
        Assertions.assertTrue(r.isEmpty)
        // nothing to match
        Assertions.assertFalse(r.matchesDigit())
        r.unconsume()
        Assertions.assertTrue(r.matchesDigit())
    }

    @Test
    fun cachesStrings() {
        val r = CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars")
        val one = r.consumeTo('\t')
        r.consume()
        val two = r.consumeTo('\t')
        r.consume()
        val three = r.consumeTo('\t')
        r.consume()
        val four = r.consumeTo('\t')
        r.consume()
        val five = r.consumeTo('\t')
        Assertions.assertEquals("Check", one)
        Assertions.assertEquals("Check", two)
        Assertions.assertEquals("Check", three)
        Assertions.assertEquals("CHOKE", four)
        Assertions.assertSame(one, two)
        Assertions.assertSame(two, three)
        Assertions.assertNotSame(three, four)
        Assertions.assertNotSame(four, five)
        Assertions.assertEquals(five, "A string that is longer than 16 chars")
    }

    @Test
    fun rangeEquals() {
        val r = CharacterReader("Check\tCheck\tCheck\tCHOKE")
        Assertions.assertTrue(r.rangeEquals(0, 5, "Check"))
        Assertions.assertFalse(r.rangeEquals(0, 5, "CHOKE"))
        Assertions.assertFalse(r.rangeEquals(0, 5, "Chec"))
        Assertions.assertTrue(r.rangeEquals(6, 5, "Check"))
        Assertions.assertFalse(r.rangeEquals(6, 5, "Chuck"))
        Assertions.assertTrue(r.rangeEquals(12, 5, "Check"))
        Assertions.assertFalse(r.rangeEquals(12, 5, "Cheeky"))
        Assertions.assertTrue(r.rangeEquals(18, 5, "CHOKE"))
        Assertions.assertFalse(r.rangeEquals(18, 5, "CHIKE"))
    }

    @Test
    fun empty() {
        var r = CharacterReader("One")
        Assertions.assertTrue(r.matchConsume("One"))
        Assertions.assertTrue(r.isEmpty)
        r = CharacterReader("Two")
        val two = r.consumeToEnd()
        Assertions.assertEquals("Two", two)
    }

    @Test
    fun consumeToNonexistentEndWhenAtAnd() {
        val r = CharacterReader("<!")
        Assertions.assertTrue(r.matchConsume("<!"))
        Assertions.assertTrue(r.isEmpty)
        val after = r.consumeTo('>')
        Assertions.assertEquals("", after)
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun notEmptyAtBufferSplitPoint() {
        val r = CharacterReader(StringReader("How about now"), 3)
        Assertions.assertEquals("How", r.consumeTo(' '))
        Assertions.assertFalse(r.isEmpty, "Should not be empty")
        Assertions.assertEquals(' ', r.consume())
        Assertions.assertFalse(r.isEmpty)
        Assertions.assertEquals(4, r.pos())
        Assertions.assertEquals('a', r.consume())
        Assertions.assertEquals(5, r.pos())
        Assertions.assertEquals('b', r.consume())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('u', r.consume())
        Assertions.assertEquals('t', r.consume())
        Assertions.assertEquals(' ', r.consume())
        Assertions.assertEquals('n', r.consume())
        Assertions.assertEquals('o', r.consume())
        Assertions.assertEquals('w', r.consume())
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun bufferUp() {
        val note = "HelloThere" // + ! = 11 chars
        val loopCount = 64
        val sb = StringBuilder()
        for (i in 0 until loopCount) {
            sb.append(note)
            sb.append("!")
        }
        val s = sb.toString()
        val br = BufferedReader(StringReader(s))
        val r = CharacterReader(br)
        for (i in 0 until loopCount) {
            val pull = r.consumeTo('!')
            Assertions.assertEquals(note, pull)
            Assertions.assertEquals('!', r.current())
            r.advance()
        }
        Assertions.assertTrue(r.isEmpty)
    }

    @Test
    fun canEnableAndDisableLineNumberTracking() {
        val reader = CharacterReader("Hello!")
        Assertions.assertFalse(reader.isTrackNewlines)
        reader.trackNewlines(true)
        Assertions.assertTrue(reader.isTrackNewlines)
        reader.trackNewlines(false)
        Assertions.assertFalse(reader.isTrackNewlines)
    }

    @Test
    fun canTrackNewlines() {
        val builder = StringBuilder()
        builder.append("<foo>\n<bar>\n<qux>\n")
        while (builder.length < CharacterReaderTest.Companion.maxBufferLen) builder.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        builder.append("[foo]\n[bar]")
        val content = builder.toString()
        val noTrack = CharacterReader(content)
        Assertions.assertFalse(noTrack.isTrackNewlines)
        val track = CharacterReader(content)
        track.trackNewlines(true)
        Assertions.assertTrue(track.isTrackNewlines)

        // check that no tracking works as expected (pos is 0 indexed, line number stays at 1, col is pos+1)
        Assertions.assertEquals(0, noTrack.pos())
        Assertions.assertEquals(1, noTrack.lineNumber())
        Assertions.assertEquals(1, noTrack.columnNumber())
        noTrack.consumeTo("<qux>")
        Assertions.assertEquals(12, noTrack.pos())
        Assertions.assertEquals(1, noTrack.lineNumber())
        Assertions.assertEquals(13, noTrack.columnNumber())
        Assertions.assertEquals("1:13", noTrack.cursorPos())
        // get over the buffer
        while (!noTrack.matches("[foo]")) noTrack.consumeTo("[foo]")
        Assertions.assertEquals(32778, noTrack.pos())
        Assertions.assertEquals(1, noTrack.lineNumber())
        Assertions.assertEquals(noTrack.pos() + 1, noTrack.columnNumber())
        Assertions.assertEquals("1:32779", noTrack.cursorPos())

        // and the line numbers: "<foo>\n<bar>\n<qux>\n"
        Assertions.assertEquals(0, track.pos())
        Assertions.assertEquals(1, track.lineNumber())
        Assertions.assertEquals(1, track.columnNumber())
        track.consumeTo('\n')
        Assertions.assertEquals(1, track.lineNumber())
        Assertions.assertEquals(6, track.columnNumber())
        track.consume()
        Assertions.assertEquals(2, track.lineNumber())
        Assertions.assertEquals(1, track.columnNumber())
        Assertions.assertEquals("<bar>", track.consumeTo('\n'))
        Assertions.assertEquals(2, track.lineNumber())
        Assertions.assertEquals(6, track.columnNumber())
        Assertions.assertEquals("\n", track.consumeTo("<qux>"))
        Assertions.assertEquals(12, track.pos())
        Assertions.assertEquals(3, track.lineNumber())
        Assertions.assertEquals(1, track.columnNumber())
        Assertions.assertEquals("3:1", track.cursorPos())
        Assertions.assertEquals("<qux>", track.consumeTo('\n'))
        Assertions.assertEquals("3:6", track.cursorPos())
        // get over the buffer
        while (!track.matches("[foo]")) track.consumeTo("[foo]")
        Assertions.assertEquals(32778, track.pos())
        Assertions.assertEquals(4, track.lineNumber())
        Assertions.assertEquals(32761, track.columnNumber())
        Assertions.assertEquals("4:32761", track.cursorPos())
        track.consumeTo('\n')
        Assertions.assertEquals("4:32766", track.cursorPos())
        track.consumeTo("[bar]")
        Assertions.assertEquals(5, track.lineNumber())
        Assertions.assertEquals("5:1", track.cursorPos())
        track.consumeToEnd()
        Assertions.assertEquals("5:6", track.cursorPos())
    }

    @Test
    fun countsColumnsOverBufferWhenNoNewlines() {
        val builder = StringBuilder()
        while (builder.length < CharacterReaderTest.Companion.maxBufferLen * 4) builder.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        val content = builder.toString()
        val reader = CharacterReader(content)
        reader.trackNewlines(true)
        Assertions.assertEquals("1:1", reader.cursorPos())
        while (!reader.isEmpty) reader.consume()
        Assertions.assertEquals(131096, reader.pos())
        Assertions.assertEquals(reader.pos() + 1, reader.columnNumber())
        Assertions.assertEquals(1, reader.lineNumber())
    }

    @Test
    @Throws(IOException::class)
    fun linenumbersAgreeWithEditor() {
        val content: String = ParseTest.Companion.getFileAsString(ParseTest.Companion.getFile("/htmltests/large.html"))
        val reader = CharacterReader(content)
        reader.trackNewlines(true)
        val scan = "<p>VESTIBULUM" // near the end of the file
        while (!reader.matches(scan)) reader.consumeTo(scan)
        Assertions.assertEquals(280218, reader.pos())
        Assertions.assertEquals(1002, reader.lineNumber())
        Assertions.assertEquals(1, reader.columnNumber())
        reader.consumeTo(' ')
        Assertions.assertEquals(1002, reader.lineNumber())
        Assertions.assertEquals(14, reader.columnNumber())
    }

    companion object {
        const val maxBufferLen = CharacterReader.maxBufferLen
        fun BufferBuster(content: String?): String {
            val builder = StringBuilder()
            while (builder.length < CharacterReaderTest.Companion.maxBufferLen) builder.append(content)
            return builder.toString()
        }
    }
}
