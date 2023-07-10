package org.jsoup.selectimport

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.select.CombiningEvaluator
import org.jsoup.select.QueryParser
import org.jsoup.select.Selector
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
 * Tests that the selector selects correctly.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class SelectorTest {
    @Test
    fun testByTag() {
        // should be case insensitive
        val els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV")
        Assertions.assertEquals(3, els.size)
        Assertions.assertEquals("1", els[0].id())
        Assertions.assertEquals("2", els[1].id())
        Assertions.assertEquals("3", els[2].id())
        val none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span")
        Assertions.assertEquals(0, none.size)
    }

    @Test
    fun byEscapedTag() {
        // tested same result as js document.querySelector
        val doc = Jsoup.parse("<p.p>One</p.p> <p\\p>Two</p\\p>")
        val one = doc.expectFirst("p\\.p")
        Assertions.assertEquals("One", one.text())
        val two = doc.expectFirst("p\\\\p")
        Assertions.assertEquals("Two", two.text())
    }

    @Test
    fun testById() {
        val els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("Hello", els[0].text())
        Assertions.assertEquals("Foo two!", els[1].text())
        val none = Jsoup.parse("<div id=1></div>").select("#foo")
        Assertions.assertEquals(0, none.size)
    }

    @Test
    fun byEscapedId() {
        val doc = Jsoup.parse("<p id='i.d'>One</p> <p id='i\\d'>Two</p> <p id='one-two/three'>Three</p>")
        val one = doc.expectFirst("#i\\.d")
        Assertions.assertEquals("One", one.text())
        val two = doc.expectFirst("#i\\\\d")
        Assertions.assertEquals("Two", two.text())
        val thr = doc.expectFirst("p#one-two\\/three")
        Assertions.assertEquals("Three", thr.text())
    }

    @Test
    fun testByClass() {
        val els = Jsoup.parse("<p id=0 class='ONE two'><p id=1 class='one'><p id=2 class='two'>").select("P.One")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("0", els[0].id())
        Assertions.assertEquals("1", els[1].id())
        val none = Jsoup.parse("<div class='one'></div>").select(".foo")
        Assertions.assertEquals(0, none.size)
        val els2 = Jsoup.parse("<div class='One-Two'></div>").select(".one-two")
        Assertions.assertEquals(1, els2.size)
    }

    @Test
    fun byEscapedClass() {
        val els: Element = Jsoup.parse("<p class='one.two#three'>One</p>")
        val one = els.expectFirst("p.one\\.two\\#three")
        Assertions.assertEquals("One", one.text())
    }

    @Test
    fun testByClassCaseInsensitive() {
        val html = "<p Class=foo>One <p Class=Foo>Two <p class=FOO>Three <p class=farp>Four"
        val elsFromClass = Jsoup.parse(html).select("P.Foo")
        val elsFromAttr = Jsoup.parse(html).select("p[class=foo]")
        Assertions.assertEquals(elsFromAttr.size, elsFromClass.size)
        Assertions.assertEquals(3, elsFromClass.size)
        Assertions.assertEquals("Two", elsFromClass[1].text())
    }

    @MultiLocaleTest
    fun testByAttribute(locale: Locale?) {
        Locale.setDefault(locale)
        val h = "<div Title=Foo /><div Title=Bar /><div Style=Qux /><div title=Balim /><div title=SLIM />" +
                "<div data-name='with spaces'/>"
        val doc = Jsoup.parse(h)
        val withTitle = doc.select("[title]")
        Assertions.assertEquals(4, withTitle.size)
        val foo = doc.select("[TITLE=foo]")
        Assertions.assertEquals(1, foo.size)
        val foo2 = doc.select("[title=\"foo\"]")
        Assertions.assertEquals(1, foo2.size)
        val foo3 = doc.select("[title=\"Foo\"]")
        Assertions.assertEquals(1, foo3.size)
        val dataName = doc.select("[data-name=\"with spaces\"]")
        Assertions.assertEquals(1, dataName.size)
        Assertions.assertEquals("with spaces", dataName.first()!!.attr("data-name"))
        val not = doc.select("div[title!=bar]")
        Assertions.assertEquals(5, not.size)
        Assertions.assertEquals("Foo", not.first()!!.attr("title"))
        val starts = doc.select("[title^=ba]")
        Assertions.assertEquals(2, starts.size)
        Assertions.assertEquals("Bar", starts.first()!!.attr("title"))
        Assertions.assertEquals("Balim", starts.last()!!.attr("title"))
        val ends = doc.select("[title$=im]")
        Assertions.assertEquals(2, ends.size)
        Assertions.assertEquals("Balim", ends.first()!!.attr("title"))
        Assertions.assertEquals("SLIM", ends.last()!!.attr("title"))
        val contains = doc.select("[title*=i]")
        Assertions.assertEquals(2, contains.size)
        Assertions.assertEquals("Balim", contains.first()!!.attr("title"))
        Assertions.assertEquals("SLIM", contains.last()!!.attr("title"))
    }

    @Test
    fun testNamespacedTag() {
        val doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>")
        val byTag = doc.select("abc|def")
        Assertions.assertEquals(2, byTag.size)
        Assertions.assertEquals("1", byTag.first()!!.id())
        Assertions.assertEquals("2", byTag.last()!!.id())
        val byAttr = doc.select(".bold")
        Assertions.assertEquals(1, byAttr.size)
        Assertions.assertEquals("2", byAttr.last()!!.id())
        val byTagAttr = doc.select("abc|def.bold")
        Assertions.assertEquals(1, byTagAttr.size)
        Assertions.assertEquals("2", byTagAttr.last()!!.id())
        val byContains = doc.select("abc|def:contains(e)")
        Assertions.assertEquals(2, byContains.size)
        Assertions.assertEquals("1", byContains.first()!!.id())
        Assertions.assertEquals("2", byContains.last()!!.id())
    }

    @Test
    fun testWildcardNamespacedTag() {
        val doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div> <abc:def class=bold id=2>There</abc:def>")
        val byTag = doc.select("*|def")
        Assertions.assertEquals(2, byTag.size)
        Assertions.assertEquals("1", byTag.first()!!.id())
        Assertions.assertEquals("2", byTag.last()!!.id())
        val byAttr = doc.select(".bold")
        Assertions.assertEquals(1, byAttr.size)
        Assertions.assertEquals("2", byAttr.last()!!.id())
        val byTagAttr = doc.select("*|def.bold")
        Assertions.assertEquals(1, byTagAttr.size)
        Assertions.assertEquals("2", byTagAttr.last()!!.id())
        val byContains = doc.select("*|def:contains(e)")
        Assertions.assertEquals(2, byContains.size)
        Assertions.assertEquals("1", byContains.first()!!.id())
        Assertions.assertEquals("2", byContains.last()!!.id())
    }

    @Test
    fun testWildcardNamespacedXmlTag() {
        val doc = parse(
            "<div><Abc:Def id=1>Hello</Abc:Def></div> <Abc:Def class=bold id=2>There</abc:def>",
            "", xmlParser()
        )
        val byTag = doc.select("*|Def")
        Assertions.assertEquals(2, byTag.size)
        Assertions.assertEquals("1", byTag.first()!!.id())
        Assertions.assertEquals("2", byTag.last()!!.id())
        val byAttr = doc.select(".bold")
        Assertions.assertEquals(1, byAttr.size)
        Assertions.assertEquals("2", byAttr.last()!!.id())
        val byTagAttr = doc.select("*|Def.bold")
        Assertions.assertEquals(1, byTagAttr.size)
        Assertions.assertEquals("2", byTagAttr.last()!!.id())
        val byContains = doc.select("*|Def:contains(e)")
        Assertions.assertEquals(2, byContains.size)
        Assertions.assertEquals("1", byContains.first()!!.id())
        Assertions.assertEquals("2", byContains.last()!!.id())
    }

    @Test
    fun testWildCardNamespacedCaseVariations() {
        val doc = parse("<One:Two>One</One:Two><three:four>Two</three:four>", "", xmlParser())
        val els1 = doc.select("One|Two")
        val els2 = doc.select("one|two")
        val els3 = doc.select("Three|Four")
        val els4 = doc.select("three|Four")
        Assertions.assertEquals(els1, els2)
        Assertions.assertEquals(els3, els4)
        Assertions.assertEquals("One", els1.text())
        Assertions.assertEquals(1, els1.size)
        Assertions.assertEquals("Two", els3.text())
        Assertions.assertEquals(1, els2.size)
    }

    @MultiLocaleTest
    fun testByAttributeStarting(locale: Locale?) {
        Locale.setDefault(locale)
        val doc =
            Jsoup.parse("<div id=1 ATTRIBUTE data-name=jsoup>Hello</div><p data-val=5 id=2>There</p><p id=3>No</p>")
        var withData = doc.select("[^data-]")
        Assertions.assertEquals(2, withData.size)
        Assertions.assertEquals("1", withData.first()!!.id())
        Assertions.assertEquals("2", withData.last()!!.id())
        withData = doc.select("p[^data-]")
        Assertions.assertEquals(1, withData.size)
        Assertions.assertEquals("2", withData.first()!!.id())
        Assertions.assertEquals(1, doc.select("[^attrib]").size)
    }

    @Test
    fun testByAttributeRegex() {
        val doc =
            Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif><img></p>")
        val imgs = doc.select("img[src~=(?i)\\.(png|jpe?g)]")
        Assertions.assertEquals(3, imgs.size)
        Assertions.assertEquals("1", imgs[0].id())
        Assertions.assertEquals("2", imgs[1].id())
        Assertions.assertEquals("3", imgs[2].id())
    }

    @Test
    fun testByAttributeRegexCharacterClass() {
        val doc =
            Jsoup.parse("<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif id=4></p>")
        val imgs = doc.select("img[src~=[o]]")
        Assertions.assertEquals(2, imgs.size)
        Assertions.assertEquals("1", imgs[0].id())
        Assertions.assertEquals("4", imgs[1].id())
    }

    @Test
    fun testByAttributeRegexCombined() {
        val doc = Jsoup.parse("<div><table class=x><td>Hello</td></table></div>")
        val els = doc.select("div table[class~=x|y]")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Hello", els.text())
    }

    @Test
    fun testCombinedWithContains() {
        val doc = Jsoup.parse("<p id=1>One</p><p>Two +</p><p>Three +</p>")
        val els = doc.select("p#1 + :contains(+)")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Two +", els.text())
        Assertions.assertEquals("p", els.first()!!.tagName())
    }

    @Test
    fun testAllElements() {
        val h = "<div><p>Hello</p><p><b>there</b></p></div>"
        val doc = Jsoup.parse(h)
        val allDoc = doc.select("*")
        val allUnderDiv = doc.select("div *")
        Assertions.assertEquals(8, allDoc.size)
        Assertions.assertEquals(3, allUnderDiv.size)
        Assertions.assertEquals("p", allUnderDiv.first()!!.tagName())
    }

    @Test
    fun testAllWithClass() {
        val h = "<p class=first>One<p class=first>Two<p>Three"
        val doc = Jsoup.parse(h)
        val ps = doc.select("*.first")
        Assertions.assertEquals(2, ps.size)
    }

    @Test
    fun testGroupOr() {
        val h = "<div title=foo /><div title=bar /><div /><p></p><img /><span title=qux>"
        val doc = Jsoup.parse(h)
        val els = doc.select("p,div,[title]")
        Assertions.assertEquals(5, els.size)
        Assertions.assertEquals("div", els[0].tagName())
        Assertions.assertEquals("foo", els[0].attr("title"))
        Assertions.assertEquals("div", els[1].tagName())
        Assertions.assertEquals("bar", els[1].attr("title"))
        Assertions.assertEquals("div", els[2].tagName())
        Assertions.assertEquals(0, els[2].attr("title").length) // missing attributes come back as empty string
        Assertions.assertFalse(els[2].hasAttr("title"))
        Assertions.assertEquals("p", els[3].tagName())
        Assertions.assertEquals("span", els[4].tagName())
    }

    @Test
    fun testGroupOrAttribute() {
        val h = "<div id=1 /><div id=2 /><div title=foo /><div title=bar />"
        val els = Jsoup.parse(h).select("[id],[title=foo]")
        Assertions.assertEquals(3, els.size)
        Assertions.assertEquals("1", els[0].id())
        Assertions.assertEquals("2", els[1].id())
        Assertions.assertEquals("foo", els[2].attr("title"))
    }

    @Test
    fun descendant() {
        val h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>"
        val doc = Jsoup.parse(h)
        val root = doc.getElementsByClass("HEAD").first()
        val els = root!!.select(".head p")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("Hello", els[0].text())
        Assertions.assertEquals("There", els[1].text())
        val p = root.select("p.first")
        Assertions.assertEquals(1, p.size)
        Assertions.assertEquals("Hello", p[0].text())
        val empty = root.select("p .first") // self, not descend, should not match
        Assertions.assertEquals(0, empty.size)
        val aboveRoot = root.select("body div.head")
        Assertions.assertEquals(0, aboveRoot.size)
    }

    @Test
    fun and() {
        val h = "<div id=1 class='foo bar' title=bar name=qux><p class=foo title=bar>Hello</p></div"
        val doc = Jsoup.parse(h)
        val div = doc.select("div.foo")
        Assertions.assertEquals(1, div.size)
        Assertions.assertEquals("div", div.first()!!.tagName())
        val p = doc.select("div .foo") // space indicates like "div *.foo"
        Assertions.assertEquals(1, p.size)
        Assertions.assertEquals("p", p.first()!!.tagName())
        val div2 = doc.select("div#1.foo.bar[title=bar][name=qux]") // very specific!
        Assertions.assertEquals(1, div2.size)
        Assertions.assertEquals("div", div2.first()!!.tagName())
        val p2 = doc.select("div *.foo") // space indicates like "div *.foo"
        Assertions.assertEquals(1, p2.size)
        Assertions.assertEquals("p", p2.first()!!.tagName())
    }

    @Test
    fun deeperDescendant() {
        val h =
            "<div class=head><p><span class=first>Hello</div><div class=head><p class=first><span>Another</span><p>Again</div>"
        val doc = Jsoup.parse(h)
        val root = doc.getElementsByClass("head").first()
        val els = root!!.select("div p .first")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Hello", els.first()!!.text())
        Assertions.assertEquals("span", els.first()!!.tagName())
        val aboveRoot = root.select("body p .first")
        Assertions.assertEquals(0, aboveRoot.size)
    }

    @Test
    fun parentChildElement() {
        val h = "<div id=1><div id=2><div id = 3></div></div></div><div id=4></div>"
        val doc = Jsoup.parse(h)
        val divs = doc.select("div > div")
        Assertions.assertEquals(2, divs.size)
        Assertions.assertEquals("2", divs[0].id()) // 2 is child of 1
        Assertions.assertEquals("3", divs[1].id()) // 3 is child of 2
        val div2 = doc.select("div#1 > div")
        Assertions.assertEquals(1, div2.size)
        Assertions.assertEquals("2", div2[0].id())
    }

    @Test
    fun parentWithClassChild() {
        val h = "<h1 class=foo><a href=1 /></h1><h1 class=foo><a href=2 class=bar /></h1><h1><a href=3 /></h1>"
        val doc = Jsoup.parse(h)
        val allAs = doc.select("h1 > a")
        Assertions.assertEquals(3, allAs.size)
        Assertions.assertEquals("a", allAs.first()!!.tagName())
        val fooAs = doc.select("h1.foo > a")
        Assertions.assertEquals(2, fooAs.size)
        Assertions.assertEquals("a", fooAs.first()!!.tagName())
        val barAs = doc.select("h1.foo > a.bar")
        Assertions.assertEquals(1, barAs.size)
    }

    @Test
    fun parentChildStar() {
        val h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><span>Hi</span></div>"
        val doc = Jsoup.parse(h)
        val divChilds = doc.select("div > *")
        Assertions.assertEquals(3, divChilds.size)
        Assertions.assertEquals("p", divChilds[0].tagName())
        Assertions.assertEquals("p", divChilds[1].tagName())
        Assertions.assertEquals("span", divChilds[2].tagName())
    }

    @Test
    fun multiChildDescent() {
        val h = "<div id=foo><h1 class=bar><a href=http://example.com/>One</a></h1></div>"
        val doc = Jsoup.parse(h)
        val els = doc.select("div#foo > h1.bar > a[href*=example]")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("a", els.first()!!.tagName())
    }

    @Test
    fun caseInsensitive() {
        val h = "<dIv tItle=bAr><div>" // mixed case so a simple toLowerCase() on value doesn't catch
        val doc = Jsoup.parse(h)
        Assertions.assertEquals(2, doc.select("DiV").size)
        Assertions.assertEquals(1, doc.select("DiV[TiTLE]").size)
        Assertions.assertEquals(1, doc.select("DiV[TiTLE=BAR]").size)
        Assertions.assertEquals(0, doc.select("DiV[TiTLE=BARBARELLA]").size)
    }

    @Test
    fun adjacentSiblings() {
        val h = "<ol><li>One<li>Two<li>Three</ol>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("li + li")
        Assertions.assertEquals(2, sibs.size)
        Assertions.assertEquals("Two", sibs[0].text())
        Assertions.assertEquals("Three", sibs[1].text())
    }

    @Test
    fun adjacentSiblingsWithId() {
        val h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("li#1 + li#2")
        Assertions.assertEquals(1, sibs.size)
        Assertions.assertEquals("Two", sibs[0].text())
    }

    @Test
    fun notAdjacent() {
        val h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("li#1 + li#3")
        Assertions.assertEquals(0, sibs.size)
    }

    @Test
    fun mixCombinator() {
        val h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>"
        val doc = Jsoup.parse(h)
        val sibs = doc.select("body > div.foo li + li")
        Assertions.assertEquals(2, sibs.size)
        Assertions.assertEquals("Two", sibs[0].text())
        Assertions.assertEquals("Three", sibs[1].text())
    }

    @Test
    fun mixCombinatorGroup() {
        val h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>"
        val doc = Jsoup.parse(h)
        val els = doc.select(".foo > ol, ol > li + li")
        Assertions.assertEquals(3, els.size)
        Assertions.assertEquals("ol", els[0].tagName())
        Assertions.assertEquals("Two", els[1].text())
        Assertions.assertEquals("Three", els[2].text())
    }

    @Test
    fun generalSiblings() {
        val h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>"
        val doc = Jsoup.parse(h)
        val els = doc.select("#1 ~ #3")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Three", els.first()!!.text())
    }

    // for http://github.com/jhy/jsoup/issues#issue/10
    @Test
    fun testCharactersInIdAndClass() {
        // using CSS spec for identifiers (id and class): a-z0-9, -, _. NOT . (which is OK in html spec, but not css)
        val h = "<div><p id='a1-foo_bar'>One</p><p class='b2-qux_bif'>Two</p></div>"
        val doc = Jsoup.parse(h)
        val el1 = doc.getElementById("a1-foo_bar")
        Assertions.assertEquals("One", el1!!.text())
        val el2 = doc.getElementsByClass("b2-qux_bif").first()
        Assertions.assertEquals("Two", el2!!.text())
        val el3 = doc.select("#a1-foo_bar").first()
        Assertions.assertEquals("One", el3!!.text())
        val el4 = doc.select(".b2-qux_bif").first()
        Assertions.assertEquals("Two", el4!!.text())
    }

    // for http://github.com/jhy/jsoup/issues#issue/13
    @Test
    fun testSupportsLeadingCombinator() {
        var h = "<div><p><span>One</span><span>Two</span></p></div>"
        var doc = Jsoup.parse(h)
        val p = doc.select("div > p").first()
        val spans = p!!.select("> span")
        Assertions.assertEquals(2, spans.size)
        Assertions.assertEquals("One", spans.first()!!.text())

        // make sure doesn't get nested
        h = "<div id=1><div id=2><div id=3></div></div></div>"
        doc = Jsoup.parse(h)
        val div = doc.select("div").select(" > div").first()
        Assertions.assertEquals("2", div!!.id())
    }

    @Test
    fun testPseudoLessThan() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>")
        val ps = doc.select("div p:lt(2)")
        Assertions.assertEquals(3, ps.size)
        Assertions.assertEquals("One", ps[0].text())
        Assertions.assertEquals("Two", ps[1].text())
        Assertions.assertEquals("Four", ps[2].text())
    }

    @Test
    fun testPseudoGreaterThan() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>")
        val ps = doc.select("div p:gt(0)")
        Assertions.assertEquals(2, ps.size)
        Assertions.assertEquals("Two", ps[0].text())
        Assertions.assertEquals("Three", ps[1].text())
    }

    @Test
    fun testPseudoEquals() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>")
        val ps = doc.select("div p:eq(0)")
        Assertions.assertEquals(2, ps.size)
        Assertions.assertEquals("One", ps[0].text())
        Assertions.assertEquals("Four", ps[1].text())
        val ps2 = doc.select("div:eq(0) p:eq(0)")
        Assertions.assertEquals(1, ps2.size)
        Assertions.assertEquals("One", ps2[0].text())
        Assertions.assertEquals("p", ps2[0].tagName())
    }

    @Test
    fun testPseudoBetween() {
        val doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>")
        val ps = doc.select("div p:gt(0):lt(2)")
        Assertions.assertEquals(1, ps.size)
        Assertions.assertEquals("Two", ps[0].text())
    }

    @Test
    fun testPseudoCombined() {
        val doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>")
        val ps = doc.select("div.foo p:gt(0)")
        Assertions.assertEquals(1, ps.size)
        Assertions.assertEquals("Two", ps[0].text())
    }

    @Test
    fun testPseudoHas() {
        val doc =
            Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>")
        val divs1 = doc.select("div:has(span)")
        Assertions.assertEquals(2, divs1.size)
        Assertions.assertEquals("0", divs1[0].id())
        Assertions.assertEquals("1", divs1[1].id())
        val divs2 = doc.select("div:has([class])")
        Assertions.assertEquals(1, divs2.size)
        Assertions.assertEquals("1", divs2[0].id())
        val divs3 = doc.select("div:has(span, p)")
        Assertions.assertEquals(3, divs3.size)
        Assertions.assertEquals("0", divs3[0].id())
        Assertions.assertEquals("1", divs3[1].id())
        Assertions.assertEquals("2", divs3[2].id())
        val els1 = doc.body().select(":has(p)")
        Assertions.assertEquals(3, els1.size) // body, div, div
        Assertions.assertEquals("body", els1.first()!!.tagName())
        Assertions.assertEquals("0", els1[1].id())
        Assertions.assertEquals("2", els1[2].id())
        val els2 = doc.body().select(":has(> span)")
        Assertions.assertEquals(2, els2.size) // p, div
        Assertions.assertEquals("p", els2.first()!!.tagName())
        Assertions.assertEquals("1", els2[1].id())
    }

    @Test
    fun testNestedHas() {
        val doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p></div>")
        var divs = doc.select("div:has(p:has(span))")
        Assertions.assertEquals(1, divs.size)
        Assertions.assertEquals("One", divs.first()!!.text())

        // test matches in has
        divs = doc.select("div:has(p:matches((?i)two))")
        Assertions.assertEquals(1, divs.size)
        Assertions.assertEquals("div", divs.first()!!.tagName())
        Assertions.assertEquals("Two", divs.first()!!.text())

        // test contains in has
        divs = doc.select("div:has(p:contains(two))")
        Assertions.assertEquals(1, divs.size)
        Assertions.assertEquals("div", divs.first()!!.tagName())
        Assertions.assertEquals("Two", divs.first()!!.text())
    }

    @MultiLocaleTest
    fun testPseudoContains(locale: Locale?) {
        Locale.setDefault(locale)
        val doc = Jsoup.parse("<div><p>The Rain.</p> <p class=light>The <i>RAIN</i>.</p> <p>Rain, the.</p></div>")
        val ps1 = doc.select("p:contains(Rain)")
        Assertions.assertEquals(3, ps1.size)
        val ps2 = doc.select("p:contains(the rain)")
        Assertions.assertEquals(2, ps2.size)
        Assertions.assertEquals("The Rain.", ps2.first()!!.html())
        Assertions.assertEquals("The <i>RAIN</i>.", ps2.last()!!.html())
        val ps3 = doc.select("p:contains(the Rain):has(i)")
        Assertions.assertEquals(1, ps3.size)
        Assertions.assertEquals("light", ps3.first()!!.className())
        val ps4 = doc.select(".light:contains(rain)")
        Assertions.assertEquals(1, ps4.size)
        Assertions.assertEquals("light", ps3.first()!!.className())
        val ps5 = doc.select(":contains(rain)")
        Assertions.assertEquals(8, ps5.size) // html, body, div,...
        val ps6 = doc.select(":contains(RAIN)")
        Assertions.assertEquals(8, ps6.size)
    }

    @Test
    fun testPsuedoContainsWithParentheses() {
        val doc = Jsoup.parse("<div><p id=1>This (is good)</p><p id=2>This is bad)</p>")
        val ps1 = doc.select("p:contains(this (is good))")
        Assertions.assertEquals(1, ps1.size)
        Assertions.assertEquals("1", ps1.first()!!.id())
        val ps2 = doc.select("p:contains(this is bad\\))")
        Assertions.assertEquals(1, ps2.size)
        Assertions.assertEquals("2", ps2.first()!!.id())
    }

    @Test
    fun containsWholeText() {
        var doc = Jsoup.parse("<div><p> jsoup\n The <i>HTML</i> Parser</p><p>jsoup The HTML Parser</div>")
        val ps = doc.select("p")
        val es1 = doc.select("p:containsWholeText( jsoup\n The HTML Parser)")
        val es2 = doc.select("p:containsWholeText(jsoup The HTML Parser)")
        Assertions.assertEquals(1, es1.size)
        Assertions.assertEquals(1, es2.size)
        Assertions.assertEquals(ps[0], es1.first())
        Assertions.assertEquals(ps[1], es2.first())
        Assertions.assertEquals(0, doc.select("div:containsWholeText(jsoup the html parser)").size)
        Assertions.assertEquals(0, doc.select("div:containsWholeText(jsoup\n the html parser)").size)
        doc = Jsoup.parse("<div><p></p><p> </p><p>.  </p>")
        val blanks = doc.select("p:containsWholeText(  )")
        Assertions.assertEquals(1, blanks.size)
        Assertions.assertEquals(".  ", blanks.first()!!.wholeText())
    }

    @Test
    fun containsWholeOwnText() {
        var doc = Jsoup.parse("<div><p> jsoup\n The <i>HTML</i> Parser</p><p>jsoup The HTML Parser<br></div>")
        val ps = doc.select("p")
        val es1 = doc.select("p:containsWholeOwnText( jsoup\n The  Parser)")
        val es2 = doc.select("p:containsWholeOwnText(jsoup The HTML Parser\n)")
        Assertions.assertEquals(1, es1.size)
        Assertions.assertEquals(1, es2.size)
        Assertions.assertEquals(ps[0], es1.first())
        Assertions.assertEquals(ps[1], es2.first())
        Assertions.assertEquals(0, doc.select("div:containsWholeOwnText(jsoup the html parser)").size)
        Assertions.assertEquals(0, doc.select("div:containsWholeOwnText(jsoup\n the  parser)").size)
        doc = Jsoup.parse("<div><p></p><p> </p><p>.  </p>")
        val blanks = doc.select("p:containsWholeOwnText(  )")
        Assertions.assertEquals(1, blanks.size)
        Assertions.assertEquals(".  ", blanks.first()!!.wholeText())
    }

    @MultiLocaleTest
    fun containsOwn(locale: Locale?) {
        Locale.setDefault(locale)
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b> igor</p>")
        val ps = doc.select("p:containsOwn(Hello IGOR)")
        Assertions.assertEquals(1, ps.size)
        Assertions.assertEquals("1", ps.first()!!.id())
        Assertions.assertEquals(0, doc.select("p:containsOwn(there)").size)
        val doc2 = Jsoup.parse("<p>Hello <b>there</b> IGOR</p>")
        Assertions.assertEquals(1, doc2.select("p:containsOwn(igor)").size)
    }

    @Test
    fun testMatches() {
        val doc =
            Jsoup.parse("<p id=1>The <i>Rain</i></p> <p id=2>There are 99 bottles.</p> <p id=3>Harder (this)</p> <p id=4>Rain</p>")
        val p1 = doc.select("p:matches(The rain)") // no match, case sensitive
        Assertions.assertEquals(0, p1.size)
        val p2 = doc.select("p:matches((?i)the rain)") // case insense. should include root, html, body
        Assertions.assertEquals(1, p2.size)
        Assertions.assertEquals("1", p2.first()!!.id())
        val p4 = doc.select("p:matches((?i)^rain$)") // bounding
        Assertions.assertEquals(1, p4.size)
        Assertions.assertEquals("4", p4.first()!!.id())
        val p5 = doc.select("p:matches(\\d+)")
        Assertions.assertEquals(1, p5.size)
        Assertions.assertEquals("2", p5.first()!!.id())
        val p6 = doc.select("p:matches(\\w+\\s+\\(\\w+\\))") // test bracket matching
        Assertions.assertEquals(1, p6.size)
        Assertions.assertEquals("3", p6.first()!!.id())
        val p7 = doc.select("p:matches((?i)the):has(i)") // multi
        Assertions.assertEquals(1, p7.size)
        Assertions.assertEquals("1", p7.first()!!.id())
    }

    @Test
    fun matchesOwn() {
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b> now</p>")
        val p1 = doc.select("p:matchesOwn((?i)hello now)")
        Assertions.assertEquals(1, p1.size)
        Assertions.assertEquals("1", p1.first()!!.id())
        Assertions.assertEquals(0, doc.select("p:matchesOwn(there)").size)
    }

    @Test
    fun matchesWholeText() {
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b>\n now</p><p id=2> </p><p id=3></p>")
        val p1 = doc.select("p:matchesWholeText((?i)hello there\n now)")
        Assertions.assertEquals(1, p1.size)
        Assertions.assertEquals("1", p1.first()!!.id())
        Assertions.assertEquals(1, doc.select("p:matchesWholeText(there\n now)").size)
        Assertions.assertEquals(0, doc.select("p:matchesWholeText(There\n now)").size)
        val p2 = doc.select("p:matchesWholeText(^\\s+$)")
        Assertions.assertEquals(1, p2.size)
        Assertions.assertEquals("2", p2.first()!!.id())
        val p3 = doc.select("p:matchesWholeText(^$)")
        Assertions.assertEquals(1, p3.size)
        Assertions.assertEquals("3", p3.first()!!.id())
    }

    @Test
    fun matchesWholeOwnText() {
        val doc = Jsoup.parse("<p id=1>Hello <b>there</b>\n now</p><p id=2> </p><p id=3><i>Text</i></p>")
        val p1 = doc.select("p:matchesWholeOwnText((?i)hello \n now)")
        Assertions.assertEquals(1, p1.size)
        Assertions.assertEquals("1", p1.first()!!.id())
        Assertions.assertEquals(0, doc.select("p:matchesWholeOwnText(there\n now)").size)
        val p2 = doc.select("p:matchesWholeOwnText(^\\s+$)")
        Assertions.assertEquals(1, p2.size)
        Assertions.assertEquals("2", p2.first()!!.id())
        val p3 = doc.select("p:matchesWholeOwnText(^$)")
        Assertions.assertEquals(1, p3.size)
        Assertions.assertEquals("3", p3.first()!!.id())
    }

    @Test
    fun testRelaxedTags() {
        val doc = Jsoup.parse("<abc_def id=1>Hello</abc_def> <abc-def id=2>There</abc-def>")
        val el1 = doc.select("abc_def")
        Assertions.assertEquals(1, el1.size)
        Assertions.assertEquals("1", el1.first()!!.id())
        val el2 = doc.select("abc-def")
        Assertions.assertEquals(1, el2.size)
        Assertions.assertEquals("2", el2.first()!!.id())
    }

    @Test
    fun notParas() {
        val doc = Jsoup.parse("<p id=1>One</p> <p>Two</p> <p><span>Three</span></p>")
        val el1 = doc.select("p:not([id=1])")
        Assertions.assertEquals(2, el1.size)
        Assertions.assertEquals("Two", el1.first()!!.text())
        Assertions.assertEquals("Three", el1.last()!!.text())
        val el2 = doc.select("p:not(:has(span))")
        Assertions.assertEquals(2, el2.size)
        Assertions.assertEquals("One", el2.first()!!.text())
        Assertions.assertEquals("Two", el2.last()!!.text())
    }

    @Test
    fun notAll() {
        val doc = Jsoup.parse("<p>Two</p> <p><span>Three</span></p>")
        val el1 = doc.body().select(":not(p)") // should just be the span
        Assertions.assertEquals(2, el1.size)
        Assertions.assertEquals("body", el1.first()!!.tagName())
        Assertions.assertEquals("span", el1.last()!!.tagName())
    }

    @Test
    fun notClass() {
        val doc = Jsoup.parse("<div class=left>One</div><div class=right id=1><p>Two</p></div>")
        val el1 = doc.select("div:not(.left)")
        Assertions.assertEquals(1, el1.size)
        Assertions.assertEquals("1", el1.first()!!.id())
    }

    @Test
    fun handlesCommasInSelector() {
        val doc = Jsoup.parse("<p name='1,2'>One</p><div>Two</div><ol><li>123</li><li>Text</li></ol>")
        val ps = doc.select("[name=1,2]")
        Assertions.assertEquals(1, ps.size)
        val containers = doc.select("div, li:matches([0-9,]+)")
        Assertions.assertEquals(2, containers.size)
        Assertions.assertEquals("div", containers[0].tagName())
        Assertions.assertEquals("li", containers[1].tagName())
        Assertions.assertEquals("123", containers[1].text())
    }

    @Test
    fun selectSupplementaryCharacter() {
        val s = String(Character.toChars(135361))
        val doc = Jsoup.parse("<div k$s='$s'>^$s$/div>")
        Assertions.assertEquals("div", doc.select("div[k$s]").first()!!.tagName())
        Assertions.assertEquals("div", doc.select("div:containsOwn($s)").first()!!.tagName())
    }

    @Test
    fun selectClassWithSpace() {
        val html = """
            <div class="value">class without space</div>
            <div class="value ">class with space</div>
            """.trimIndent()
        val doc = Jsoup.parse(html)
        var found = doc.select("div[class=value ]")
        Assertions.assertEquals(2, found.size)
        Assertions.assertEquals("class without space", found[0].text())
        Assertions.assertEquals("class with space", found[1].text())
        found = doc.select("div[class=\"value \"]")
        Assertions.assertEquals(2, found.size)
        Assertions.assertEquals("class without space", found[0].text())
        Assertions.assertEquals("class with space", found[1].text())
        found = doc.select("div[class=\"value\\ \"]")
        Assertions.assertEquals(0, found.size)
    }

    @Test
    fun selectSameElements() {
        val html = "<div>one</div><div>one</div>"
        val doc = Jsoup.parse(html)
        val els = doc.select("div")
        Assertions.assertEquals(2, els.size)
        val subSelect = els.select(":contains(one)")
        Assertions.assertEquals(2, subSelect.size)
    }

    @Test
    fun attributeWithBrackets() {
        val html = "<div data='End]'>One</div> <div data='[Another)]]'>Two</div>"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("One", doc.select("div[data='End]']").first()!!.text())
        Assertions.assertEquals("Two", doc.select("div[data='[Another)]]']").first()!!.text())
        Assertions.assertEquals("One", doc.select("div[data=\"End]\"]").first()!!.text())
        Assertions.assertEquals("Two", doc.select("div[data=\"[Another)]]\"]").first()!!.text())
    }

    @MultiLocaleTest
    fun containsData(locale: Locale?) {
        Locale.setDefault(locale)
        val html = "<p>function</p><script>FUNCTION</script><style>item</style><span><!-- comments --></span>"
        val doc = Jsoup.parse(html)
        val body = doc.body()
        val dataEls1 = body.select(":containsData(function)")
        val dataEls2 = body.select("script:containsData(function)")
        val dataEls3 = body.select("span:containsData(comments)")
        val dataEls4 = body.select(":containsData(o)")
        val dataEls5 = body.select("style:containsData(ITEM)")
        Assertions.assertEquals(2, dataEls1.size) // body and script
        Assertions.assertEquals(1, dataEls2.size)
        Assertions.assertEquals(dataEls1.last(), dataEls2.first())
        Assertions.assertEquals("<script>FUNCTION</script>", dataEls2.outerHtml())
        Assertions.assertEquals(1, dataEls3.size)
        Assertions.assertEquals("span", dataEls3.first()!!.tagName())
        Assertions.assertEquals(3, dataEls4.size)
        Assertions.assertEquals("body", dataEls4.first()!!.tagName())
        Assertions.assertEquals("script", dataEls4[1].tagName())
        Assertions.assertEquals("span", dataEls4[2].tagName())
        Assertions.assertEquals(1, dataEls5.size)
    }

    @Test
    fun containsWithQuote() {
        val html = "<p>One'One</p><p>One'Two</p>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p:contains(One\\'One)")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("One'One", els.text())
    }

    @Test
    fun selectFirst() {
        val html = "<p>One<p>Two<p>Three"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("One", doc.selectFirst("p")!!.text())
    }

    @Test
    fun selectFirstWithAnd() {
        val html = "<p>One<p class=foo>Two<p>Three"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("Two", doc.selectFirst("p.foo")!!.text())
    }

    @Test
    fun selectFirstWithOr() {
        val html = "<p>One<p>Two<p>Three<div>Four"
        val doc = Jsoup.parse(html)
        Assertions.assertEquals("One", doc.selectFirst("p, div")!!.text())
    }

    @Test
    fun matchText() {
        val html = "<p>One<br>Two</p>"
        val doc = Jsoup.parse(html)
        doc.outputSettings().prettyPrint(false)
        val origHtml = doc.html()
        val one = doc.select("p:matchText:first-child")
        Assertions.assertEquals("One", one.first()!!.text())
        val two = doc.select("p:matchText:last-child")
        Assertions.assertEquals("Two", two.first()!!.text())
        Assertions.assertEquals(origHtml, doc.html())
        Assertions.assertEquals("Two", doc.select("p:matchText + br + *").text())
    }

    @Test
    fun nthLastChildWithNoParent() {
        val el = Element("p").text("Orphan")
        val els = el.select("p:nth-last-child(1)")
        Assertions.assertEquals(0, els.size)
    }

    @Test
    fun splitOnBr() {
        val html = "<div><p>One<br>Two<br>Three</p></div>"
        val doc = Jsoup.parse(html)
        val els = doc.select("p:matchText")
        Assertions.assertEquals(3, els.size)
        Assertions.assertEquals("One", els[0].text())
        Assertions.assertEquals("Two", els[1].text())
        Assertions.assertEquals("Three", els[2].toString())
    }

    @Test
    fun matchTextAttributes() {
        val doc = Jsoup.parse("<div><p class=one>One<br>Two<p class=two>Three<br>Four")
        val els = doc.select("p.two:matchText:last-child")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Four", els.text())
    }

    @Test
    fun findBetweenSpan() {
        val doc = Jsoup.parse("<p><span>One</span> Two <span>Three</span>")
        val els = doc.select("span ~ p:matchText") // the Two becomes its own p, sibling of the span
        // todo - think this should really be 'p:matchText span ~ p'. The :matchText should behave as a modifier to expand the nodes.
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Two", els.text())
    }

    @Test
    fun startsWithBeginsWithSpace() {
        val doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net\">(abc@def.net)</a></small>")
        val els = doc.select("a[href^=' mailto']")
        Assertions.assertEquals(1, els.size)
    }

    @Test
    fun endsWithEndsWithSpaces() {
        val doc = Jsoup.parse("<small><a href=\" mailto:abc@def.net \">(abc@def.net)</a></small>")
        val els = doc.select("a[href$='.net ']")
        Assertions.assertEquals(1, els.size)
    }

    // https://github.com/jhy/jsoup/issues/1257
    private val mixedCase = "<html xmlns:n=\"urn:ns\"><n:mixedCase>text</n:mixedCase></html>"
    private val lowercase = "<html xmlns:n=\"urn:ns\"><n:lowercase>text</n:lowercase></html>"
    @Test
    fun html_mixed_case_simple_name() {
        val doc = parse(mixedCase, "", htmlParser())
        Assertions.assertEquals(0, doc.select("mixedCase").size)
    }

    @Test
    fun html_mixed_case_wildcard_name() {
        val doc = parse(mixedCase, "", htmlParser())
        Assertions.assertEquals(1, doc.select("*|mixedCase").size)
    }

    @Test
    fun html_lowercase_simple_name() {
        val doc = parse(lowercase, "", htmlParser())
        Assertions.assertEquals(0, doc.select("lowercase").size)
    }

    @Test
    fun html_lowercase_wildcard_name() {
        val doc = parse(lowercase, "", htmlParser())
        Assertions.assertEquals(1, doc.select("*|lowercase").size)
    }

    @Test
    fun xml_mixed_case_simple_name() {
        val doc = parse(mixedCase, "", xmlParser())
        Assertions.assertEquals(0, doc.select("mixedCase").size)
    }

    @Test
    fun xml_mixed_case_wildcard_name() {
        val doc = parse(mixedCase, "", xmlParser())
        Assertions.assertEquals(1, doc.select("*|mixedCase").size)
    }

    @Test
    fun xml_lowercase_simple_name() {
        val doc = parse(lowercase, "", xmlParser())
        Assertions.assertEquals(0, doc.select("lowercase").size)
    }

    @Test
    fun xml_lowercase_wildcard_name() {
        val doc = parse(lowercase, "", xmlParser())
        Assertions.assertEquals(1, doc.select("*|lowercase").size)
    }

    @Test
    fun trimSelector() {
        // https://github.com/jhy/jsoup/issues/1274
        val doc = Jsoup.parse("<p><span>Hello")
        val els = doc.select(" p span ")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Hello", els.first()!!.text())
    }

    @Test
    fun xmlWildcardNamespaceTest() {
        // https://github.com/jhy/jsoup/issues/1208
        val doc = parse("<ns1:MyXmlTag>1111</ns1:MyXmlTag><ns2:MyXmlTag>2222</ns2:MyXmlTag>", "", xmlParser())
        val select = doc.select("*|MyXmlTag")
        Assertions.assertEquals(2, select.size)
        Assertions.assertEquals("1111", select[0].text())
        Assertions.assertEquals("2222", select[1].text())
    }

    @Test
    fun childElements() {
        // https://github.com/jhy/jsoup/issues/1292
        val html = "<body><span id=1>One <span id=2>Two</span></span></body>"
        val doc = Jsoup.parse(html)
        val outer = doc.selectFirst("span")
        val span = outer!!.selectFirst("span")
        val inner = outer.selectFirst("* span")
        Assertions.assertEquals("1", outer.id())
        Assertions.assertEquals("1", span!!.id())
        Assertions.assertEquals("2", inner!!.id())
        Assertions.assertEquals(outer, span)
        Assertions.assertNotEquals(outer, inner)
    }

    @Test
    fun selectFirstLevelChildrenOnly() {
        // testcase for https://github.com/jhy/jsoup/issues/984
        val html = "<div><span>One <span>Two</span></span> <span>Three <span>Four</span></span>"
        val doc = Jsoup.parse(html)
        val div = doc.selectFirst("div")
        Assertions.assertNotNull(div)

        // want to select One and Three only - the first level children
        val spans = div!!.select(":root > span")
        Assertions.assertEquals(2, spans.size)
        Assertions.assertEquals("One Two", spans[0].text())
        Assertions.assertEquals("Three Four", spans[1].text())
    }

    @Test
    fun wildcardNamespaceMatchesNoNamespace() {
        // https://github.com/jhy/jsoup/issues/1565
        val xml = "<package><meta>One</meta><opf:meta>Two</opf:meta></package>"
        val doc = parse(xml, "", xmlParser())
        val metaEls = doc.select("meta")
        Assertions.assertEquals(1, metaEls.size)
        Assertions.assertEquals("One", metaEls[0].text())
        val nsEls = doc.select("*|meta")
        Assertions.assertEquals(2, nsEls.size)
        Assertions.assertEquals("One", nsEls[0].text())
        Assertions.assertEquals("Two", nsEls[1].text())
    }

    @Test
    fun containsTextQueryIsNormalized() {
        val doc = Jsoup.parse("<p><p id=1>Hello  there now<em>!</em>")
        val a = doc.select("p:contains(Hello   there  now!)")
        val b = doc.select(":containsOwn(hello   there  now)")
        val c = doc.select("p:contains(Hello there now)")
        val d = doc.select(":containsOwn(hello There now)")
        val e = doc.select("p:contains(HelloThereNow)")
        Assertions.assertEquals(1, a.size)
        Assertions.assertEquals(a, b)
        Assertions.assertEquals(a, c)
        Assertions.assertEquals(a, d)
        Assertions.assertEquals(0, e.size)
        Assertions.assertNotEquals(a, e)
    }

    @Test
    fun selectorExceptionNotStringFormatException() {
        val ex = Selector.SelectorParseException("%&")
        Assertions.assertEquals("%&", ex.message)
    }

    @Test
    fun evaluatorMemosAreReset() {
        val eval = QueryParser.parse("p ~ p")
        val andEval = eval as CombiningEvaluator.And
        // TODO:
//        StructuralEvaluator.PreviousSibling prevEval = (StructuralEvaluator.PreviousSibling) andEval.getEvaluators().get(0);
//        IdentityHashMap<Element, IdentityHashMap<Element, Boolean>> map = prevEval.threadMemo.get();
//        assertEquals(0, map.size()); // no memo yet
//
//        Document doc1 = Jsoup.parse("<p>One<p>Two<p>Three");
//        Document doc2 = Jsoup.parse("<p>One2<p>Two2<p>Three2");
//
//        Elements s1 = doc1.select(eval);
//        assertEquals(2, s1.size());
//        assertEquals("Two", s1.first().text());
//        Elements s2 = doc2.select(eval);
//        assertEquals(2, s2.size());
//        assertEquals("Two2", s2.first().text());
//
//        assertEquals(1, map.size()); // root of doc 2
    }
}
