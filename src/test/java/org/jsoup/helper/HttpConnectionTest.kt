package org.jsoup.helperimport

import org.jsoup.Connection
import org.jsoup.Connection.KeyVal.value
import org.jsoup.Jsoup
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import org.jsoup.helper.HttpConnection
import org.jsoup.helper.HttpConnection.Companion.connect
import org.jsoup.helper.HttpConnection.KeyVal.Companion.create
import org.jsoup.helper.UrlBuilder
import org.jsoup.integration.ParseTest
import org.jsoup.nodes.Attribute.value
import org.jsoup.nodes.Element.value
import org.jsoup.select.Elements.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.*
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
import org.jsoup.nodes.ElementTest
import org.junit.jupiter.params.ParameterizedTest
import java.io.IOException
import org.jsoup.integration.ParseTest
import java.nio.charset.Charset
import org.jsoup.nodes.DocumentTest
import java.io.ByteArrayInputStream
import org.jsoup.nodes.PositionTest
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.integration.UrlConnectTest
import org.jsoup.nodes.BuildEntities.CharacterRef
import org.jsoup.nodes.BuildEntities
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
import java.util.Locale
import org.jsoup.helper.UrlBuilder
import org.jsoup.parser.TokenQueueTest
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
import java.util.Enumeration
import javax.servlet.MultipartConfigElement
import javax.servlet.ServletOutputStream
import org.jsoup.integration.servlets.HelloServlet
import org.jsoup.integration.servlets.Deflateservlet
import org.jsoup.integration.servlets.RedirectServlet
import org.jsoup.integration.servlets.InterruptedServlet
import org.jsoup.integration.Benchmark
import org.jsoup.integration.SessionIT.ThreadCatcher
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.server.ServerConnector
import org.jsoup.integration.ConnectTest
import org.jsoup.integration.FuzzFixesIT
import org.jsoup.integration.SessionTest
import org.jsoup.integration.SafelistExtensionTest.OpenSafelist
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.ArgumentsSource
import org.jsoup.MultiLocaleExtension
import org.junit.jupiter.api.extension.ExtendWith

class HttpConnectionTest {
    /* most actual network http connection tests are in integration */
    @Test
    fun canCreateEmptyConnection() {
        val con = HttpConnection()
        Assertions.assertEquals(Connection.Method.GET, con.request().method())
        Assertions.assertThrows(IllegalArgumentException::class.java) { val url = con.request().url() }
    }

    @Test
    fun throwsExceptionOnResponseWithoutExecute() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val con = HttpConnection.connect("http://example.com")
            con.response()
        }
    }

    @Test
    fun throwsExceptionOnParseWithoutExecute() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val con = HttpConnection.connect("http://example.com")
            con.response().parse()
        }
    }

    @Test
    fun throwsExceptionOnBodyWithoutExecute() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val con = HttpConnection.connect("http://example.com")
            con.response().body()
        }
    }

    @Test
    fun throwsExceptionOnBodyAsBytesWithoutExecute() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val con = HttpConnection.connect("http://example.com")
            con.response().bodyAsBytes()
        }
    }

    @MultiLocaleTest
    fun caseInsensitiveHeaders(locale: Locale?) {
        Locale.setDefault(locale)
        val res: Connection.Response = HttpConnection.Response()
        res.header("Accept-Encoding", "gzip")
        res.header("content-type", "text/html")
        res.header("refErrer", "http://example.com")
        Assertions.assertTrue(res.hasHeader("Accept-Encoding"))
        Assertions.assertTrue(res.hasHeader("accept-encoding"))
        Assertions.assertTrue(res.hasHeader("accept-Encoding"))
        Assertions.assertTrue(res.hasHeader("ACCEPT-ENCODING"))
        Assertions.assertEquals("gzip", res.header("accept-Encoding"))
        Assertions.assertEquals("gzip", res.header("ACCEPT-ENCODING"))
        Assertions.assertEquals("text/html", res.header("Content-Type"))
        Assertions.assertEquals("http://example.com", res.header("Referrer"))
        res.removeHeader("Content-Type")
        Assertions.assertFalse(res.hasHeader("content-type"))
        res.removeHeader("ACCEPT-ENCODING")
        Assertions.assertFalse(res.hasHeader("Accept-Encoding"))
        res.header("ACCEPT-ENCODING", "deflate")
        Assertions.assertEquals("deflate", res.header("Accept-Encoding"))
        Assertions.assertEquals("deflate", res.header("accept-Encoding"))
    }

    @Test
    fun headers() {
        val con = HttpConnection.connect("http://example.com")
        val headers: MutableMap<String, String> = HashMap()
        headers["content-type"] = "text/html"
        headers["Connection"] = "keep-alive"
        headers["Host"] = "http://example.com"
        con.headers(headers)
        Assertions.assertEquals("text/html", con.request().header("content-type"))
        Assertions.assertEquals("keep-alive", con.request().header("Connection"))
        Assertions.assertEquals("http://example.com", con.request().header("Host"))
    }

    @Test
    fun sameHeadersCombineWithComma() {
        val headers: MutableMap<String, List<String?>> = HashMap()
        val values: MutableList<String?> = ArrayList()
        values.add("no-cache")
        values.add("no-store")
        headers["Cache-Control"] = values
        val res = HttpConnection.Response()
        res.processResponseHeaders(headers)
        Assertions.assertEquals("no-cache, no-store", res.header("Cache-Control"))
    }

    @Test
    fun multipleHeaders() {
        val req: Connection.Request = HttpConnection.Request()
        req.addHeader("Accept", "Something")
        req.addHeader("Accept", "Everything")
        req.addHeader("Foo", "Bar")
        Assertions.assertTrue(req.hasHeader("Accept"))
        Assertions.assertTrue(req.hasHeader("ACCEpt"))
        Assertions.assertEquals("Something, Everything", req.header("accept"))
        Assertions.assertTrue(req.hasHeader("fOO"))
        Assertions.assertEquals("Bar", req.header("foo"))
        val accept = req.headers("accept")
        Assertions.assertEquals(2, accept.size)
        Assertions.assertEquals("Something", accept[0])
        Assertions.assertEquals("Everything", accept[1])
        var headers: Map<String?, List<String?>> = req.multiHeaders()
        Assertions.assertEquals(accept, headers["Accept"])
        Assertions.assertEquals("Bar", headers["Foo"]!![0])
        Assertions.assertTrue(req.hasHeader("Accept"))
        Assertions.assertTrue(req.hasHeaderWithValue("accept", "Something"))
        Assertions.assertTrue(req.hasHeaderWithValue("accept", "Everything"))
        Assertions.assertFalse(req.hasHeaderWithValue("accept", "Something for nothing"))
        req.removeHeader("accept")
        headers = req.multiHeaders()
        Assertions.assertEquals("Bar", headers["Foo"]!![0])
        Assertions.assertFalse(req.hasHeader("Accept"))
        Assertions.assertNull(headers["Accept"])
    }

    @Test
    fun ignoresEmptySetCookies() {
        // prep http response header map
        val headers: MutableMap<String, List<String?>> = HashMap()
        headers["Set-Cookie"] = emptyList<String>()
        val res = HttpConnection.Response()
        res.processResponseHeaders(headers)
        Assertions.assertEquals(0, res.cookies().size)
    }

    @Test
    fun ignoresEmptyCookieNameAndVals() {
        // prep http response header map
        val headers: MutableMap<String, List<String?>> = HashMap()
        val cookieStrings: MutableList<String?> = ArrayList()
        cookieStrings.add(null)
        cookieStrings.add("")
        cookieStrings.add("one")
        cookieStrings.add("two=")
        cookieStrings.add("three=;")
        cookieStrings.add("four=data; Domain=.example.com; Path=/")
        headers["Set-Cookie"] = cookieStrings
        val res = HttpConnection.Response()
        res.processResponseHeaders(headers)
        Assertions.assertEquals(4, res.cookies().size)
        Assertions.assertEquals("", res.cookie("one"))
        Assertions.assertEquals("", res.cookie("two"))
        Assertions.assertEquals("", res.cookie("three"))
        Assertions.assertEquals("data", res.cookie("four"))
    }

    @Test
    @Throws(MalformedURLException::class)
    fun connectWithUrl() {
        val con = connect(URL("http://example.com"))
        Assertions.assertEquals("http://example.com", con.request().url().toExternalForm())
    }

    @Test
    fun throwsOnMalformedUrl() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { HttpConnection.connect("bzzt") }
    }

    @Test
    fun userAgent() {
        val con = HttpConnection.connect("http://example.com/")
        Assertions.assertEquals(HttpConnection.DEFAULT_UA, con.request().header("User-Agent"))
        con.userAgent("Mozilla")
        Assertions.assertEquals("Mozilla", con.request().header("User-Agent"))
    }

    @Test
    fun timeout() {
        val con = HttpConnection.connect("http://example.com/")
        Assertions.assertEquals(30 * 1000, con.request().timeout())
        con.timeout(1000)
        Assertions.assertEquals(1000, con.request().timeout())
    }

    @Test
    fun referrer() {
        val con = HttpConnection.connect("http://example.com/")
        con.referrer("http://foo.com")
        Assertions.assertEquals("http://foo.com", con.request().header("Referer"))
    }

    @Test
    fun method() {
        val con = HttpConnection.connect("http://example.com/")
        Assertions.assertEquals(Connection.Method.GET, con.request().method())
        con.method(Connection.Method.POST)
        Assertions.assertEquals(Connection.Method.POST, con.request().method())
    }

    @Test
    fun throwsOnOddData() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val con = HttpConnection.connect("http://example.com/")
            con.data("Name", "val", "what")
        }
    }

    @Test
    fun data() {
        val con = HttpConnection.connect("http://example.com/")
        con.data("Name", "Val", "Foo", "bar")
        val values: Collection<Connection.KeyVal> = con.request().data()
        val data: Array<Any> = values.toTypedArray()
        val one = data[0] as Connection.KeyVal
        val two = data[1] as Connection.KeyVal
        Assertions.assertEquals("Name", one.key())
        Assertions.assertEquals("Val", one.value())
        Assertions.assertEquals("Foo", two.key())
        Assertions.assertEquals("bar", two.value())
    }

    @Test
    fun cookie() {
        val con = HttpConnection.connect("http://example.com/")
        con.cookie("Name", "Val")
        Assertions.assertEquals("Val", con.request().cookie("Name"))
    }

    @Test
    fun inputStream() {
        var kv: Connection.KeyVal = create("file", "thumb.jpg", ParseTest.Companion.inputStreamFrom("Check"))
        Assertions.assertEquals("file", kv.key())
        Assertions.assertEquals("thumb.jpg", kv.value())
        Assertions.assertTrue(kv.hasInputStream())
        kv = create("one", "two")
        Assertions.assertEquals("one", kv.key())
        Assertions.assertEquals("two", kv.value())
        Assertions.assertFalse(kv.hasInputStream())
    }

    @Test
    fun requestBody() {
        val con = HttpConnection.connect("http://example.com/")
        con.requestBody("foo")
        Assertions.assertEquals("foo", con.request().requestBody())
    }

    @Test
    @Throws(MalformedURLException::class)
    fun encodeUrl() {
        val url1 = URL("https://test.com/foo bar/[One]?q=white space#frag")
        val url2 = UrlBuilder(url1).build()
        Assertions.assertEquals("https://test.com/foo%20bar/%5BOne%5D?q=white+space#frag", url2.toExternalForm())
    }

    @Test
    @Throws(MalformedURLException::class)
    fun encodedUrlDoesntDoubleEncode() {
        val url1 = URL("https://test.com/foo bar/[One]?q=white space#frag ment")
        val url2 = UrlBuilder(url1).build()
        val url3 = UrlBuilder(url2).build()
        Assertions.assertEquals("https://test.com/foo%20bar/%5BOne%5D?q=white+space#frag%20ment", url2.toExternalForm())
        Assertions.assertEquals("https://test.com/foo%20bar/%5BOne%5D?q=white+space#frag%20ment", url3.toExternalForm())
    }

    @Test
    fun connectToEncodedUrl() {
        val connect = Jsoup.connect("https://example.com/a%20b%20c?query+string")
        val url = connect.request().url()
        Assertions.assertEquals("https://example.com/a%20b%20c?query+string", url.toExternalForm())
    }

    @Test
    @Throws(IOException::class)
    fun noUrlThrowsValidationError() {
        val con = HttpConnection()
        var threw = false
        try {
            con.execute()
        } catch (e: IllegalArgumentException) {
            threw = true
            Assertions.assertEquals("URL not set. Make sure to call #url(...) before executing the request.", e.message)
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun handlesHeaderEncodingOnRequest() {
        val req: Connection.Request = HttpConnection.Request()
        req.addHeader("xxx", "é")
    }

    @Test
    @Throws(MalformedURLException::class)
    fun supportsInternationalDomainNames() {
        val idn = "https://www.测试.测试/foo.html?bar"
        val puny = "https://www.xn--0zwm56d.xn--0zwm56d/foo.html?bar"
        val con = Jsoup.connect(idn)
        Assertions.assertEquals(puny, con.request().url().toExternalForm())
        val req = HttpConnection.Request()
        req.url(URL(idn))
        Assertions.assertEquals(puny, req.url().toExternalForm())
    }

    @Test
    @Throws(MalformedURLException::class)
    fun supportsIdnWithPort() {
        val idn = "https://www.测试.测试:9001/foo.html?bar"
        val puny = "https://www.xn--0zwm56d.xn--0zwm56d:9001/foo.html?bar"
        val con = Jsoup.connect(idn)
        Assertions.assertEquals(puny, con.request().url().toExternalForm())
        val req = HttpConnection.Request()
        req.url(URL(idn))
        Assertions.assertEquals(puny, req.url().toExternalForm())
    }

    @Test
    @Throws(IOException::class)
    fun validationErrorsOnExecute() {
        val con: Connection = HttpConnection()
        var urlThrew = false
        try {
            con.execute()
        } catch (e: IllegalArgumentException) {
            urlThrew = e.message!!.contains("URL")
        }
        Assertions.assertTrue(urlThrew)
    }

    @Test
    fun testMalformedException() {
        var threw = false
        try {
            Jsoup.connect("jsoup.org/test")
        } catch (e: IllegalArgumentException) {
            threw = true
            Assertions.assertEquals(
                "The supplied URL, 'jsoup.org/test', is malformed. Make sure it is an absolute URL, and starts with 'http://' or 'https://'. See https://jsoup.org/cookbook/extracting-data/working-with-urls",
                e.message
            )
        }
        Assertions.assertTrue(threw)
    }
}
