package org.jsoup.safetyimport

import org.jsoup.Jsoup
import org.jsoup.Jsoup.clean
import org.jsoup.Jsoup.isValid
import org.jsoup.Jsoup.parse
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import org.jsoup.TextUtil
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.safety.Cleaner
import org.jsoup.safety.Safelist
import org.jsoup.safety.Safelist.Companion.basic
import org.jsoup.safety.Safelist.Companion.basicWithImages
import org.jsoup.safety.Safelist.Companion.none
import org.jsoup.safety.Safelist.Companion.relaxed
import org.jsoup.safety.Safelist.Companion.simpleText
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
import java.util.Locale
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
 * Tests for the cleaner.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class CleanerTest {
    @Test
    fun simpleBehaviourTest() {
        val h = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a></div>"
        val cleanHtml = clean(h, simpleText())
        Assertions.assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun simpleBehaviourTest2() {
        val h = "Hello <b>there</b>!"
        val cleanHtml = clean(h, simpleText())
        Assertions.assertEquals("Hello <b>there</b>!", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun basicBehaviourTest() {
        val h =
            "<div><p><a href='javascript:sendAllMoney()'>Dodgy</a> <A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>"
        val cleanHtml = clean(h, basic())
        Assertions.assertEquals(
            "<p><a rel=\"nofollow\">Dodgy</a> <a href=\"http://nice.com\" rel=\"nofollow\">Nice</a></p><blockquote>Hello</blockquote>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun basicWithImagesTest() {
        val h = "<div><p><img src='http://example.com/' alt=Image></p><p><img src='ftp://ftp.example.com'></p></div>"
        val cleanHtml = clean(h, basicWithImages())
        Assertions.assertEquals(
            "<p><img src=\"http://example.com/\" alt=\"Image\"></p><p><img></p>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testRelaxed() {
        val h = "<h1>Head</h1><table><tr><td>One<td>Two</td></tr></table>"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals(
            "<h1>Head</h1><table><tbody><tr><td>One</td><td>Two</td></tr></tbody></table>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testRemoveTags() {
        val h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>"
        val cleanHtml = clean(h, basic().removeTags("a"))
        Assertions.assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun testRemoveAttributes() {
        val h = "<div><p>Nice</p><blockquote cite='http://example.com/quotations'>Hello</blockquote>"
        val cleanHtml = clean(h, basic().removeAttributes("blockquote", "cite"))
        Assertions.assertEquals("<p>Nice</p><blockquote>Hello</blockquote>", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun allAttributes() {
        val h = "<div class=foo data=true><p class=bar>Text</p></div><blockquote cite='https://example.com'>Foo"
        val safelist = relaxed()
        safelist.addAttributes(":all", "class")
        safelist.addAttributes("div", "data")
        val clean1 = clean(h, safelist)
        Assertions.assertEquals(
            "<div class=\"foo\" data=\"true\"><p class=\"bar\">Text</p></div><blockquote cite=\"https://example.com\">Foo</blockquote>",
            TextUtil.stripNewlines(clean1)
        )
        safelist.removeAttributes(":all", "class", "cite")
        val clean2 = clean(h, safelist)
        Assertions.assertEquals(
            "<div data=\"true\"><p>Text</p></div><blockquote>Foo</blockquote>",
            TextUtil.stripNewlines(clean2)
        )
    }

    @Test
    fun removeProtocols() {
        val h = "<a href='any://example.com'>Link</a>"
        val safelist = relaxed()
        val clean1 = clean(h, safelist)
        Assertions.assertEquals("<a>Link</a>", clean1)
        safelist.removeProtocols("a", "href", "ftp", "http", "https", "mailto")
        val clean2 = clean(h, safelist) // all removed means any will work
        Assertions.assertEquals("<a href=\"any://example.com\">Link</a>", clean2)
    }

    @Test
    fun testRemoveEnforcedAttributes() {
        val h = "<div><p><A HREF='HTTP://nice.com'>Nice</a></p><blockquote>Hello</blockquote>"
        val cleanHtml = clean(h, basic().removeEnforcedAttribute("a", "rel"))
        Assertions.assertEquals(
            "<p><a href=\"http://nice.com\">Nice</a></p><blockquote>Hello</blockquote>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @Test
    fun testRemoveProtocols() {
        val h = "<p>Contact me <a href='mailto:info@example.com'>here</a></p>"
        val cleanHtml = clean(h, basic().removeProtocols("a", "href", "ftp", "mailto"))
        Assertions.assertEquals(
            "<p>Contact me <a rel=\"nofollow\">here</a></p>",
            TextUtil.stripNewlines(cleanHtml)
        )
    }

    @MultiLocaleTest
    fun safeListedProtocolShouldBeRetained(locale: Locale?) {
        Locale.setDefault(locale)
        val safelist = none()
            .addTags("a")
            .addAttributes("a", "href")
            .addProtocols("a", "href", "something")
        val cleanHtml = clean("<a href=\"SOMETHING://x\"></a>", safelist)
        Assertions.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml))
    }

    @Test
    fun testDropComments() {
        val h = "<p>Hello<!-- no --></p>"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals("<p>Hello</p>", cleanHtml)
    }

    @Test
    fun testDropXmlProc() {
        val h = "<?import namespace=\"xss\"><p>Hello</p>"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals("<p>Hello</p>", cleanHtml)
    }

    @Test
    fun testDropScript() {
        val h = "<SCRIPT SRC=//ha.ckers.org/.j><SCRIPT>alert(/XSS/.source)</SCRIPT>"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals("", cleanHtml)
    }

    @Test
    fun testDropImageScript() {
        val h = "<IMG SRC=\"javascript:alert('XSS')\">"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals("<img>", cleanHtml)
    }

    @Test
    fun testCleanJavascriptHref() {
        val h = "<A HREF=\"javascript:document.location='http://www.google.com/'\">XSS</A>"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals("<a>XSS</a>", cleanHtml)
    }

    @Test
    fun testCleanAnchorProtocol() {
        val validAnchor = "<a href=\"#valid\">Valid anchor</a>"
        val invalidAnchor = "<a href=\"#anchor with spaces\">Invalid anchor</a>"

        // A Safelist that does not allow anchors will strip them out.
        var cleanHtml = clean(validAnchor, relaxed())
        Assertions.assertEquals("<a>Valid anchor</a>", cleanHtml)
        cleanHtml = clean(invalidAnchor, relaxed())
        Assertions.assertEquals("<a>Invalid anchor</a>", cleanHtml)

        // A Safelist that allows them will keep them.
        val relaxedWithAnchor = relaxed().addProtocols("a", "href", "#")
        cleanHtml = clean(validAnchor, relaxedWithAnchor)
        Assertions.assertEquals(validAnchor, cleanHtml)

        // An invalid anchor is never valid.
        cleanHtml = clean(invalidAnchor, relaxedWithAnchor)
        Assertions.assertEquals("<a>Invalid anchor</a>", cleanHtml)
    }

    @Test
    fun testDropsUnknownTags() {
        val h = "<p><custom foo=true>Test</custom></p>"
        val cleanHtml = clean(h, relaxed())
        Assertions.assertEquals("<p>Test</p>", cleanHtml)
    }

    @Test
    fun testHandlesEmptyAttributes() {
        val h = "<img alt=\"\" src= unknown=''>"
        val cleanHtml = clean(h, basicWithImages())
        Assertions.assertEquals("<img alt=\"\">", cleanHtml)
    }

    @Test
    fun testIsValidBodyHtml() {
        val ok = "<p>Test <b><a href='http://example.com/' rel='nofollow'>OK</a></b></p>"
        val ok1 =
            "<p>Test <b><a href='http://example.com/'>OK</a></b></p>" // missing enforced is OK because still needs run thru cleaner
        val nok1 = "<p><script></script>Not <b>OK</b></p>"
        val nok2 = "<p align=right>Test Not <b>OK</b></p>"
        val nok3 = "<!-- comment --><p>Not OK</p>" // comments and the like will be cleaned
        val nok4 = "<html><head>Foo</head><body><b>OK</b></body></html>" // not body html
        val nok5 = "<p>Test <b><a href='http://example.com/' rel='nofollowme'>OK</a></b></p>"
        val nok6 = "<p>Test <b><a href='http://example.com/'>OK</b></p>" // missing close tag
        val nok7 = "</div>What"
        Assertions.assertTrue(isValid(ok, basic()))
        Assertions.assertTrue(isValid(ok1, basic()))
        Assertions.assertFalse(isValid(nok1, basic()))
        Assertions.assertFalse(isValid(nok2, basic()))
        Assertions.assertFalse(isValid(nok3, basic()))
        Assertions.assertFalse(isValid(nok4, basic()))
        Assertions.assertFalse(isValid(nok5, basic()))
        Assertions.assertFalse(isValid(nok6, basic()))
        Assertions.assertFalse(isValid(ok, none()))
        Assertions.assertFalse(isValid(nok7, basic()))
    }

    @Test
    fun testIsValidDocument() {
        val ok = "<html><head></head><body><p>Hello</p></body><html>"
        val nok = "<html><head><script>woops</script><title>Hello</title></head><body><p>Hello</p></body><html>"
        val relaxed = relaxed()
        val cleaner = Cleaner(relaxed)
        val okDoc = Jsoup.parse(ok)
        Assertions.assertTrue(cleaner.isValid(okDoc))
        Assertions.assertFalse(cleaner.isValid(Jsoup.parse(nok)))
        Assertions.assertFalse(Cleaner(none()).isValid(okDoc))
    }

    @Test
    fun resolvesRelativeLinks() {
        val html = "<a href='/foo'>Link</a><img src='/bar'>"
        val clean = clean(html, "http://example.com/", basicWithImages())
        Assertions.assertEquals(
            "<a href=\"http://example.com/foo\" rel=\"nofollow\">Link</a><img src=\"http://example.com/bar\">",
            clean
        )
    }

    @Test
    fun preservesRelativeLinksIfConfigured() {
        val html = "<a href='/foo'>Link</a><img src='/bar'> <img src='javascript:alert()'>"
        val clean = clean(html, "http://example.com/", basicWithImages().preserveRelativeLinks(true))
        Assertions.assertEquals("<a href=\"/foo\" rel=\"nofollow\">Link</a><img src=\"/bar\"> <img>", clean)
    }

    @Test
    fun dropsUnresolvableRelativeLinks() {
        val html = "<a href='/foo'>Link</a>"
        val clean = clean(html, basic())
        Assertions.assertEquals("<a rel=\"nofollow\">Link</a>", clean)
    }

    @Test
    fun dropsConcealedJavascriptProtocolWhenRelativesLinksEnabled() {
        val safelist = basic().preserveRelativeLinks(true)
        val html = "<a href=\"&#0013;ja&Tab;va&Tab;script&#0010;:alert(1)\">Link</a>"
        val clean = clean(html, "https://", safelist)
        Assertions.assertEquals("<a rel=\"nofollow\">Link</a>", clean)
        val colon = "<a href=\"ja&Tab;va&Tab;script&colon;alert(1)\">Link</a>"
        val cleanColon = clean(colon, "https://", safelist)
        Assertions.assertEquals("<a rel=\"nofollow\">Link</a>", cleanColon)
    }

    @Test
    fun dropsConcealedJavascriptProtocolWhenRelativesLinksDisabled() {
        val safelist = basic().preserveRelativeLinks(false)
        val html = "<a href=\"ja&Tab;vas&#0013;cript:alert(1)\">Link</a>"
        val clean = clean(html, "https://", safelist)
        Assertions.assertEquals("<a rel=\"nofollow\">Link</a>", clean)
    }

    @Test
    fun handlesCustomProtocols() {
        val html = "<img src='cid:12345' /> <img src='data:gzzt' />"
        val dropped = clean(html, basicWithImages())
        Assertions.assertEquals("<img> <img>", dropped)
        val preserved = clean(html, basicWithImages().addProtocols("img", "src", "cid", "data"))
        Assertions.assertEquals("<img src=\"cid:12345\"> <img src=\"data:gzzt\">", preserved)
    }

    @Test
    fun handlesAllPseudoTag() {
        val html = "<p class='foo' src='bar'><a class='qux'>link</a></p>"
        val safelist = Safelist()
            .addAttributes(":all", "class")
            .addAttributes("p", "style")
            .addTags("p", "a")
        val clean = clean(html, safelist)
        Assertions.assertEquals("<p class=\"foo\"><a class=\"qux\">link</a></p>", clean)
    }

    @Test
    fun addsTagOnAttributesIfNotSet() {
        val html = "<p class='foo' src='bar'>One</p>"
        val safelist = Safelist()
            .addAttributes("p", "class")
        // ^^ safelist does not have explicit tag add for p, inferred from add attributes.
        val clean = clean(html, safelist)
        Assertions.assertEquals("<p class=\"foo\">One</p>", clean)
    }

    @Test
    fun supplyOutputSettings() {
        // test that one can override the default document output settings
        val os = Document.OutputSettings()
        os.prettyPrint(false)
        os.escapeMode(Entities.EscapeMode.extended)
        os.charset("ascii")
        val html = "<div><p>&bernou;</p></div>"
        val customOut = clean(html, "http://foo.com/", relaxed(), os)
        val defaultOut = clean(html, "http://foo.com/", relaxed())
        Assertions.assertNotSame(defaultOut, customOut)
        Assertions.assertEquals("<div><p>&Bscr;</p></div>", customOut) // entities now prefers shorted names if aliased
        Assertions.assertEquals(
            """<div>
 <p>ℬ</p>
</div>""", defaultOut
        )
        os.charset("ASCII")
        os.escapeMode(Entities.EscapeMode.base)
        val customOut2 = clean(html, "http://foo.com/", relaxed(), os)
        Assertions.assertEquals("<div><p>&#x212c;</p></div>", customOut2)
    }

    @Test
    fun handlesFramesets() {
        val dirty =
            "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\" /><frame src=\"foo\" /></frameset></html>"
        val clean = clean(dirty, basic())
        Assertions.assertEquals("", clean) // nothing good can come out of that
        val dirtyDoc = Jsoup.parse(dirty)
        val cleanDoc = Cleaner(basic()).clean(dirtyDoc)
        Assertions.assertNotNull(cleanDoc)
        Assertions.assertEquals(0, cleanDoc.body().childNodeSize())
    }

    @Test
    fun cleansInternationalText() {
        Assertions.assertEquals("привет", clean("привет", none()))
    }

    @Test
    fun testScriptTagInSafeList() {
        val safelist = relaxed()
        safelist.addTags("script")
        Assertions.assertTrue(isValid("Hello<script>alert('Doh')</script>World !", safelist))
    }

    @Test
    fun bailsIfRemovingProtocolThatsNotSet() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {

            // a case that came up on the email list
            val w = none()

            // note no add tag, and removing protocol without adding first
            w.addAttributes("a", "href")
            w.removeProtocols("a", "href", "javascript") // with no protocols enforced, this was a noop. Now validates.
        }
    }

    @Test
    fun handlesControlCharactersAfterTagName() {
        val html = "<a/\u0006>"
        val clean = clean(html, basic())
        Assertions.assertEquals("<a rel=\"nofollow\"></a>", clean)
    }

    @Test
    fun handlesAttributesWithNoValue() {
        // https://github.com/jhy/jsoup/issues/973
        val clean = clean("<a href>Clean</a>", basic())
        Assertions.assertEquals("<a rel=\"nofollow\">Clean</a>", clean)
    }

    @Test
    fun handlesNoHrefAttribute() {
        val dirty = "<a>One</a> <a href>Two</a>"
        val relaxedWithAnchor = relaxed().addProtocols("a", "href", "#")
        val clean = clean(dirty, relaxedWithAnchor)
        Assertions.assertEquals("<a>One</a> <a>Two</a>", clean)
    }

    @Test
    fun handlesNestedQuotesInAttribute() {
        // https://github.com/jhy/jsoup/issues/1243 - no repro
        val orig = "<div style=\"font-family: 'Calibri'\">Will (not) fail</div>"
        val allow = relaxed()
            .addAttributes("div", "style")
        val clean = clean(orig, allow)
        val isValid = isValid(orig, allow)
        Assertions.assertEquals(orig, TextUtil.stripNewlines(clean)) // only difference is pretty print wrap & indent
        Assertions.assertTrue(isValid)
    }

    @Test
    fun copiesOutputSettings() {
        val orig = Jsoup.parse("<p>test<br></p>")
        orig.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        orig.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        val safelist = none().addTags("p", "br")
        val result = Cleaner(safelist).clean(orig)
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, result.outputSettings().syntax())
        Assertions.assertEquals("<p>test<br /></p>", result.body().html())
    }

    @Test
    fun preservesSourcePositionViaUserData() {
        val orig = parse("<script>xss</script>\n <p>Hello</p>", htmlParser().setTrackPosition(true))
        val p = orig.expectFirst("p")
        val origRange = p.sourceRange()
        Assertions.assertEquals("2,2:22-2,5:25", origRange.toString())
        val clean = Cleaner(relaxed()).clean(orig)
        val cleanP = clean.expectFirst("p")
        val cleanRange = cleanP.sourceRange()
        Assertions.assertEquals(cleanRange, origRange)
        Assertions.assertEquals(clean.endSourceRange(), orig.endSourceRange())
    }
}
