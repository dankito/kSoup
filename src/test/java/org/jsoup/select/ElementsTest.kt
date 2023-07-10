package org.jsoup.selectimport

import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.TextUtil
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
 * Tests for ElementList.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
class ElementsTest {
    @Test
    fun filter() {
        val h =
            "<p>Excl</p><div class=headline><p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>"
        val doc = Jsoup.parse(h)
        val els = doc.select(".headline").select("p")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("Hello", els[0].text())
        Assertions.assertEquals("There", els[1].text())
    }

    @Test
    fun attributes() {
        val h = "<p title=foo><p title=bar><p class=foo><p class=bar>"
        val doc = Jsoup.parse(h)
        val withTitle = doc.select("p[title]")
        Assertions.assertEquals(2, withTitle.size)
        Assertions.assertTrue(withTitle.hasAttr("title"))
        Assertions.assertFalse(withTitle.hasAttr("class"))
        Assertions.assertEquals("foo", withTitle.attr("title"))
        withTitle.removeAttr("title")
        Assertions.assertEquals(2, withTitle.size) // existing Elements are not reevaluated
        Assertions.assertEquals(0, doc.select("p[title]").size)
        val ps = doc.select("p").attr("style", "classy")
        Assertions.assertEquals(4, ps.size)
        Assertions.assertEquals("classy", ps.last()!!.attr("style"))
        Assertions.assertEquals("bar", ps.last()!!.attr("class"))
    }

    @Test
    fun hasAttr() {
        val doc = Jsoup.parse("<p title=foo><p title=bar><p class=foo><p class=bar>")
        val ps = doc.select("p")
        Assertions.assertTrue(ps.hasAttr("class"))
        Assertions.assertFalse(ps.hasAttr("style"))
    }

    @Test
    fun hasAbsAttr() {
        val doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org'>Two</a>")
        val one = doc.select("#1")
        val two = doc.select("#2")
        val both = doc.select("a")
        Assertions.assertFalse(one.hasAttr("abs:href"))
        Assertions.assertTrue(two.hasAttr("abs:href"))
        Assertions.assertTrue(both.hasAttr("abs:href")) // hits on #2
    }

    @Test
    fun attr() {
        val doc = Jsoup.parse("<p title=foo><p title=bar><p class=foo><p class=bar>")
        val classVal = doc.select("p").attr("class")
        Assertions.assertEquals("foo", classVal)
    }

    @Test
    fun absAttr() {
        val doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org'>Two</a>")
        val one = doc.select("#1")
        val two = doc.select("#2")
        val both = doc.select("a")
        Assertions.assertEquals("", one.attr("abs:href"))
        Assertions.assertEquals("https://jsoup.org", two.attr("abs:href"))
        Assertions.assertEquals("https://jsoup.org", both.attr("abs:href"))
    }

    @Test
    fun classes() {
        val doc = Jsoup.parse("<div><p class='mellow yellow'></p><p class='red green'></p>")
        val els = doc.select("p")
        Assertions.assertTrue(els.hasClass("red"))
        Assertions.assertFalse(els.hasClass("blue"))
        els.addClass("blue")
        els.removeClass("yellow")
        els.toggleClass("mellow")
        Assertions.assertEquals("blue", els[0].className())
        Assertions.assertEquals("red green blue mellow", els[1].className())
    }

    @Test
    fun hasClassCaseInsensitive() {
        val els = Jsoup.parse("<p Class=One>One <p class=Two>Two <p CLASS=THREE>THREE").select("p")
        val one = els[0]
        val two = els[1]
        val thr = els[2]
        Assertions.assertTrue(one.hasClass("One"))
        Assertions.assertTrue(one.hasClass("ONE"))
        Assertions.assertTrue(two.hasClass("TWO"))
        Assertions.assertTrue(two.hasClass("Two"))
        Assertions.assertTrue(thr.hasClass("ThreE"))
        Assertions.assertTrue(thr.hasClass("three"))
    }

    @Test
    fun text() {
        val h = "<div><p>Hello<p>there<p>world</div>"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("Hello there world", doc.select("div > *").text())
    }

    @Test
    fun hasText() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div><p></p></div>")
        val divs = doc.select("div")
        Assertions.assertTrue(divs.hasText())
        Assertions.assertFalse(doc.select("div + div").hasText())
    }

    @Test
    fun html() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div><p>There</p></div>")
        val divs = doc.select("div")
        Assertions.assertEquals("<p>Hello</p>\n<p>There</p>", divs.html())
    }

    @Test
    fun outerHtml() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div><p>There</p></div>")
        val divs = doc.select("div")
        Assertions.assertEquals(
            "<div><p>Hello</p></div><div><p>There</p></div>",
            TextUtil.stripNewlines(divs.outerHtml())
        )
    }

    @Test
    fun setHtml() {
        val doc = Jsoup.parse("<p>One</p><p>Two</p><p>Three</p>")
        val ps = doc.select("p")
        ps.prepend("<b>Bold</b>").append("<i>Ital</i>")
        Assertions.assertEquals(
            "<p><b>Bold</b>Two<i>Ital</i></p>", TextUtil.stripNewlines(
                ps[1].outerHtml()
            )
        )
        ps.html("<span>Gone</span>")
        Assertions.assertEquals("<p><span>Gone</span></p>", TextUtil.stripNewlines(ps[1].outerHtml()))
    }

    @Test
    fun `val`() {
        val doc = Jsoup.parse("<input value='one' /><textarea>two</textarea>")
        val els = doc.select("input, textarea")
        Assertions.assertEquals(2, els.size)
        Assertions.assertEquals("one", els.value())
        Assertions.assertEquals("two", els.last()!!.value())
        els.value("three")
        Assertions.assertEquals("three", els.first()!!.value())
        Assertions.assertEquals("three", els.last()!!.value())
        Assertions.assertEquals("<textarea>three</textarea>", els.last()!!.outerHtml())
    }

    @Test
    fun before() {
        val doc = Jsoup.parse("<p>This <a>is</a> <a>jsoup</a>.</p>")
        doc.select("a").before("<span>foo</span>")
        Assertions.assertEquals(
            "<p>This <span>foo</span><a>is</a> <span>foo</span><a>jsoup</a>.</p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun after() {
        val doc = Jsoup.parse("<p>This <a>is</a> <a>jsoup</a>.</p>")
        doc.select("a").after("<span>foo</span>")
        Assertions.assertEquals(
            "<p>This <a>is</a><span>foo</span> <a>jsoup</a><span>foo</span>.</p>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun wrap() {
        val h = "<p><b>This</b> is <b>jsoup</b></p>"
        val doc = Jsoup.parse(h)
        doc.select("b").wrap("<i></i>")
        Assertions.assertEquals("<p><i><b>This</b></i> is <i><b>jsoup</b></i></p>", doc.body().html())
    }

    @Test
    fun wrapDiv() {
        val h = "<p><b>This</b> is <b>jsoup</b>.</p> <p>How do you like it?</p>"
        val doc = Jsoup.parse(h)
        doc.select("p").wrap("<div></div>")
        Assertions.assertEquals(
            "<div>\n <p><b>This</b> is <b>jsoup</b>.</p>\n</div>\n<div>\n <p>How do you like it?</p>\n</div>",
            doc.body().html()
        )
    }

    @Test
    fun unwrap() {
        val h = "<div><font>One</font> <font><a href=\"/\">Two</a></font></div"
        val doc = Jsoup.parse(h)
        doc.select("font").unwrap()
        Assertions.assertEquals(
            """<div>
 One <a href="/">Two</a>
</div>""", doc.body().html()
        )
    }

    @Test
    fun unwrapP() {
        val h = "<p><a>One</a> Two</p> Three <i>Four</i> <p>Fix <i>Six</i></p>"
        val doc = Jsoup.parse(h)
        doc.select("p").unwrap()
        Assertions.assertEquals(
            "<a>One</a> Two Three <i>Four</i> Fix <i>Six</i>",
            TextUtil.stripNewlines(doc.body().html())
        )
    }

    @Test
    fun unwrapKeepsSpace() {
        val h = "<p>One <span>two</span> <span>three</span> four</p>"
        val doc = Jsoup.parse(h)
        doc.select("span").unwrap()
        Assertions.assertEquals("<p>One two three four</p>", doc.body().html())
    }

    @Test
    fun empty() {
        val doc = Jsoup.parse("<div><p>Hello <b>there</b></p> <p>now!</p></div>")
        doc.outputSettings().prettyPrint(false)
        doc.select("p").empty()
        Assertions.assertEquals("<div><p></p> <p></p></div>", doc.body().html())
    }

    @Test
    fun remove() {
        val doc = Jsoup.parse("<div><p>Hello <b>there</b></p> jsoup <p>now!</p></div>")
        doc.outputSettings().prettyPrint(false)
        doc.select("p").remove()
        Assertions.assertEquals("<div> jsoup </div>", doc.body().html())
    }

    @Test
    fun eq() {
        val h = "<p>Hello<p>there<p>world"
        val doc = Jsoup.parse(h)
        Assertions.assertEquals("there", doc.select("p").eq(1).text())
        Assertions.assertEquals("there", doc.select("p")[1].text())
    }

    @Test
    fun `is`() {
        val h = "<p>Hello<p title=foo>there<p>world"
        val doc = Jsoup.parse(h)
        val ps = doc.select("p")
        Assertions.assertTrue(ps.`is`("[title=foo]"))
        Assertions.assertFalse(ps.`is`("[title=bar]"))
    }

    @Test
    fun parents() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><p>There</p>")
        val parents = doc.select("p").parents()
        Assertions.assertEquals(3, parents.size)
        Assertions.assertEquals("div", parents[0].tagName())
        Assertions.assertEquals("body", parents[1].tagName())
        Assertions.assertEquals("html", parents[2].tagName())
    }

    @Test
    operator fun not() {
        val doc = Jsoup.parse("<div id=1><p>One</p></div> <div id=2><p><span>Two</span></p></div>")
        val div1 = doc.select("div").not(":has(p > span)")
        Assertions.assertEquals(1, div1.size)
        Assertions.assertEquals("1", div1.first()!!.id())
        val div2 = doc.select("div").not("#1")
        Assertions.assertEquals(1, div2.size)
        Assertions.assertEquals("2", div2.first()!!.id())
    }

    @Test
    fun tagNameSet() {
        val doc = Jsoup.parse("<p>Hello <i>there</i> <i>now</i></p>")
        doc.select("i").tagName("em")
        Assertions.assertEquals("<p>Hello <em>there</em> <em>now</em></p>", doc.body().html())
    }

    @Test
    fun traverse() {
        val doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>")
        val accum = StringBuilder()
        doc.select("div").traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                accum.append("<").append(node.nodeName()).append(">")
            }

            override fun tail(node: Node, depth: Int) {
                accum.append("</").append(node.nodeName()).append(">")
            }
        })
        Assertions.assertEquals("<div><p><#text></#text></p></div><div><#text></#text></div>", accum.toString())
    }

    @Test
    fun forms() {
        val doc = Jsoup.parse("<form id=1><input name=q></form><div /><form id=2><input name=f></form>")
        val els = doc.select("form, div")
        Assertions.assertEquals(3, els.size)
        val forms = els.forms()
        Assertions.assertEquals(2, forms.size)
        Assertions.assertNotNull(forms[0])
        Assertions.assertNotNull(forms[1])
        Assertions.assertEquals("1", forms[0].id())
        Assertions.assertEquals("2", forms[1].id())
    }

    @Test
    fun comments() {
        val doc = Jsoup.parse("<!-- comment1 --><p><!-- comment2 --><p class=two><!-- comment3 -->")
        val comments = doc.select("p").comments()
        Assertions.assertEquals(2, comments.size)
        Assertions.assertEquals(" comment2 ", comments[0].data)
        Assertions.assertEquals(" comment3 ", comments[1].data)
        val comments1 = doc.select("p.two").comments()
        Assertions.assertEquals(1, comments1.size)
        Assertions.assertEquals(" comment3 ", comments1[0].data)
    }

    @Test
    fun textNodes() {
        val doc = Jsoup.parse("One<p>Two<a>Three</a><p>Four</p>Five")
        val textNodes = doc.select("p").textNodes()
        Assertions.assertEquals(2, textNodes.size)
        Assertions.assertEquals("Two", textNodes[0].text())
        Assertions.assertEquals("Four", textNodes[1].text())
    }

    @Test
    fun dataNodes() {
        var doc = Jsoup.parse("<p>One</p><script>Two</script><style>Three</style>")
        val dataNodes = doc.select("p, script, style").dataNodes()
        Assertions.assertEquals(2, dataNodes.size)
        Assertions.assertEquals("Two", dataNodes[0].wholeData)
        Assertions.assertEquals("Three", dataNodes[1].wholeData)
        doc = Jsoup.parse("<head><script type=application/json><crux></script><script src=foo>Blah</script>")
        val script = doc.select("script[type=application/json]")
        val scriptNode = script.dataNodes()
        Assertions.assertEquals(1, scriptNode.size)
        val dataNode = scriptNode[0]
        Assertions.assertEquals("<crux>", dataNode.wholeData)

        // check if they're live
        dataNode.setWholeData("<cromulent>")
        Assertions.assertEquals("<script type=\"application/json\"><cromulent></script>", script.outerHtml())
    }

    @Test
    fun nodesEmpty() {
        val doc = Jsoup.parse("<p>")
        Assertions.assertEquals(0, doc.select("form").textNodes().size)
    }

    @Test
    fun classWithHyphen() {
        val doc = Jsoup.parse("<p class='tab-nav'>Check</p>")
        val els = doc.getElementsByClass("tab-nav")
        Assertions.assertEquals(1, els.size)
        Assertions.assertEquals("Check", els.text())
    }

    @Test
    fun siblings() {
        val doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12</div>")
        val els = doc.select("p:eq(3)") // gets p4 and p10
        Assertions.assertEquals(2, els.size)
        val next = els.next()
        Assertions.assertEquals(2, next.size)
        Assertions.assertEquals("5", next.first()!!.text())
        Assertions.assertEquals("11", next.last()!!.text())
        Assertions.assertEquals(0, els.next("p:contains(6)").size)
        val nextF = els.next("p:contains(5)")
        Assertions.assertEquals(1, nextF.size)
        Assertions.assertEquals("5", nextF.first()!!.text())
        val nextA = els.nextAll()
        Assertions.assertEquals(4, nextA.size)
        Assertions.assertEquals("5", nextA.first()!!.text())
        Assertions.assertEquals("12", nextA.last()!!.text())
        val nextAF = els.nextAll("p:contains(6)")
        Assertions.assertEquals(1, nextAF.size)
        Assertions.assertEquals("6", nextAF.first()!!.text())
        val prev = els.prev()
        Assertions.assertEquals(2, prev.size)
        Assertions.assertEquals("3", prev.first()!!.text())
        Assertions.assertEquals("9", prev.last()!!.text())
        Assertions.assertEquals(0, els.prev("p:contains(1)").size)
        val prevF = els.prev("p:contains(3)")
        Assertions.assertEquals(1, prevF.size)
        Assertions.assertEquals("3", prevF.first()!!.text())
        val prevA = els.prevAll()
        Assertions.assertEquals(6, prevA.size)
        Assertions.assertEquals("3", prevA.first()!!.text())
        Assertions.assertEquals("7", prevA.last()!!.text())
        val prevAF = els.prevAll("p:contains(1)")
        Assertions.assertEquals(1, prevAF.size)
        Assertions.assertEquals("1", prevAF.first()!!.text())
    }

    @Test
    fun eachText() {
        val doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>")
        val divText = doc.select("div").eachText()
        Assertions.assertEquals(2, divText.size)
        Assertions.assertEquals("1 2 3 4 5 6", divText[0])
        Assertions.assertEquals("7 8 9 10 11 12", divText[1])
        val pText = doc.select("p").eachText()
        val ps = doc.select("p")
        Assertions.assertEquals(13, ps.size)
        Assertions.assertEquals(12, pText.size) // not 13, as last doesn't have text
        Assertions.assertEquals("1", pText[0])
        Assertions.assertEquals("2", pText[1])
        Assertions.assertEquals("5", pText[4])
        Assertions.assertEquals("7", pText[6])
        Assertions.assertEquals("12", pText[11])
    }

    @Test
    fun eachAttr() {
        val doc = parse(
            "<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a>4</a>",
            "http://example.com"
        )
        val hrefAttrs = doc.select("a").eachAttr("href")
        Assertions.assertEquals(3, hrefAttrs.size)
        Assertions.assertEquals("/foo", hrefAttrs[0])
        Assertions.assertEquals("http://example.com/bar", hrefAttrs[1])
        Assertions.assertEquals("", hrefAttrs[2])
        Assertions.assertEquals(4, doc.select("a").size)
        val absAttrs = doc.select("a").eachAttr("abs:href")
        Assertions.assertEquals(3, absAttrs.size)
        Assertions.assertEquals(3, absAttrs.size)
        Assertions.assertEquals("http://example.com/foo", absAttrs[0])
        Assertions.assertEquals("http://example.com/bar", absAttrs[1])
        Assertions.assertEquals("http://example.com", absAttrs[2])
    }
}
