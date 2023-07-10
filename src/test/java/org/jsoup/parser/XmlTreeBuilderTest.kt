package org.jsoup.parserimport

import org.jsoup.Connection.KeyVal.value
import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.TextUtil
import org.jsoup.nodes.*
import org.jsoup.nodes.Attribute.value
import org.jsoup.nodes.Document.Companion.createShell
import org.jsoup.nodes.Element.value
import org.jsoup.parser.*
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.Companion.parseXmlFragment
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.select.Elements.value
import org.junit.jupiter.api.*
import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

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
import org.jsoup.integration.ParseTest
import java.nio.charset.Charset
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.integration.UrlConnectTest
import org.jsoup.nodes.BuildEntities.CharacterRef
import org.jsoup.nodes.BuildEntities.ByName
import org.jsoup.nodes.BuildEntities.ByCode
import org.jsoup.integration.servlets.EchoServlet
import org.jsoup.integration.servlets.CookieServlet
import org.jsoup.integration.TestServer
import org.jsoup.helper.W3CDomTest
import org.jsoup.helper.DataUtilTest.VaryingReadInputStream
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import java.util.Locale
import java.net.MalformedURLException
import org.jsoup.helper.UrlBuilder
import java.net.URISyntaxException
import org.jsoup.safety.SafelistTest
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
 * Tests XmlTreeBuilder.
 *
 * @author Jonathan Hedley
 */
class XmlTreeBuilderTest {
    @Test
    fun testSimpleXmlParse() {
        val xml = "<doc id=2 href='/bar'>Foo <br /><link>One</link><link>Two</link></doc>"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        Assertions.assertEquals(
            "<doc id=\"2\" href=\"/bar\">Foo <br /><link>One</link><link>Two</link></doc>",
            TextUtil.stripNewlines(doc.html())
        )
        Assertions.assertEquals(doc.getElementById("2")!!.absUrl("href"), "http://foo.com/bar")
    }

    @Test
    fun testPopToClose() {
        // test: </val> closes Two, </bar> ignored
        val xml = "<doc><val>One<val>Two</val></bar>Three</doc>"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testCommentAndDocType() {
        val xml = "<!DOCTYPE HTML><!-- a comment -->One <qux />Two"
        val tb = XmlTreeBuilder()
        val doc = tb.parse(xml, "http://foo.com/")
        Assertions.assertEquals(
            "<!DOCTYPE HTML><!-- a comment -->One <qux />Two",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testSupplyParserToJsoupClass() {
        val xml = "<doc><val>One<val>Two</val></bar>Three</doc>"
        val doc = parse(xml, "http://foo.com/", xmlParser())
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Disabled
    @Test
    @Throws(IOException::class)
    fun testSupplyParserToConnection() {
        val xmlUrl = "http://direct.infohound.net/tools/jsoup-xml-test.xml"

        // parse with both xml and html parser, ensure different
        val xmlDoc = Jsoup.connect(xmlUrl).parser(xmlParser()).get()
        val htmlDoc = Jsoup.connect(xmlUrl).parser(htmlParser()).get()
        val autoXmlDoc = Jsoup.connect(xmlUrl).get() // check connection auto detects xml, uses xml parser
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(xmlDoc.html())
        )
        Assertions.assertNotEquals(htmlDoc, xmlDoc)
        Assertions.assertEquals(xmlDoc, autoXmlDoc)
        Assertions.assertEquals(1, htmlDoc.select("head").size) // html parser normalises
        Assertions.assertEquals(0, xmlDoc.select("head").size) // xml parser does not
        Assertions.assertEquals(0, autoXmlDoc.select("head").size) // xml parser does not
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun testSupplyParserToDataStream() {
        val xmlFile = File(XmlTreeBuilder::class.java.getResource("/htmltests/xml-test.xml").toURI())
        val inStream: InputStream = FileInputStream(xmlFile)
        val doc = parse(inStream, null, "http://foo.com", xmlParser())
        Assertions.assertEquals(
            "<doc><val>One<val>Two</val>Three</val></doc>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testDoesNotForceSelfClosingKnownTags() {
        // html will force "<br>one</br>" to logically "<br />One<br />". XML should be stay "<br>one</br> -- don't recognise tag.
        val htmlDoc = Jsoup.parse("<br>one</br>")
        Assertions.assertEquals("<br>\none\n<br>", htmlDoc.body().html())
        val xmlDoc = parse("<br>one</br>", "", xmlParser())
        Assertions.assertEquals("<br>one</br>", xmlDoc.html())
    }

    @Test
    fun handlesXmlDeclarationAsDeclaration() {
        val html = "<?xml encoding='UTF-8' ?><body>One</body><!-- comment -->"
        val doc = parse(html, "", xmlParser())
        Assertions.assertEquals("<?xml encoding=\"UTF-8\"?><body>One</body><!-- comment -->", doc.outerHtml())
        Assertions.assertEquals("#declaration", doc.childNode(0).nodeName())
        Assertions.assertEquals("#comment", doc.childNode(2).nodeName())
    }

    @Test
    fun xmlFragment() {
        val xml = "<one src='/foo/' />Two<three><four /></three>"
        val nodes = parseXmlFragment(xml, "http://example.com/")
        Assertions.assertEquals(3, nodes.size)
        Assertions.assertEquals("http://example.com/foo/", nodes[0].absUrl("src"))
        Assertions.assertEquals("one", nodes[0].nodeName())
        Assertions.assertEquals("Two", (nodes[1] as TextNode).text())
    }

    @Test
    fun xmlParseDefaultsToHtmlOutputSyntax() {
        val doc = parse("x", "", xmlParser())
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
    }

    @Test
    fun testDoesHandleEOFInTag() {
        val html = "<img src=asdf onerror=\"alert(1)\" x="
        val xmlDoc = parse(html, "", xmlParser())
        Assertions.assertEquals("<img src=\"asdf\" onerror=\"alert(1)\" x=\"\" />", xmlDoc.html())
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun testDetectCharsetEncodingDeclaration() {
        val xmlFile = File(XmlTreeBuilder::class.java.getResource("/htmltests/xml-charset.xml").toURI())
        val inStream: InputStream = FileInputStream(xmlFile)
        val doc = parse(inStream, null, "http://example.com/", xmlParser())
        Assertions.assertEquals("ISO-8859-1", doc.charset()!!.name())
        Assertions.assertEquals(
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><data>äöåéü</data>",
            TextUtil.stripNewlines(doc.html())
        )
    }

    @Test
    fun testParseDeclarationAttributes() {
        val xml = "<?xml version='1' encoding='UTF-8' something='else'?><val>One</val>"
        val doc = parse(xml, "", xmlParser())
        val decl = doc.childNode(0) as XmlDeclaration
        Assertions.assertEquals("1", decl.attr("version"))
        Assertions.assertEquals("UTF-8", decl.attr("encoding"))
        Assertions.assertEquals("else", decl.attr("something"))
        Assertions.assertEquals("version=\"1\" encoding=\"UTF-8\" something=\"else\"", decl.wholeDeclaration)
        Assertions.assertEquals("<?xml version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", decl.outerHtml())
    }

    @Test
    fun testParseDeclarationWithoutAttributes() {
        val xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<?myProcessingInstruction My Processing instruction.?>"
        val doc = parse(xml, "", xmlParser())
        val decl = doc.childNode(2) as XmlDeclaration
        Assertions.assertEquals("myProcessingInstruction", decl.name())
        Assertions.assertTrue(decl.hasAttr("My"))
        Assertions.assertEquals("<?myProcessingInstruction My Processing instruction.?>", decl.outerHtml())
    }

    @Test
    fun caseSensitiveDeclaration() {
        val xml = "<?XML version='1' encoding='UTF-8' something='else'?>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals("<?XML version=\"1\" encoding=\"UTF-8\" something=\"else\"?>", doc.outerHtml())
    }

    @Test
    fun testCreatesValidProlog() {
        val document = createShell("")
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        document.charset(StandardCharsets.UTF_8)
        Assertions.assertEquals(
            """<?xml version="1.0" encoding="UTF-8"?>
<html>
 <head></head>
 <body></body>
</html>""", document.outerHtml()
        )
    }

    @Test
    fun preservesCaseByDefault() {
        val xml = "<CHECK>One</CHECK><TEST ID=1>Check</TEST>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals("<CHECK>One</CHECK><TEST ID=\"1\">Check</TEST>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun appendPreservesCaseByDefault() {
        val xml = "<One>One</One>"
        val doc = parse(xml, "", xmlParser())
        val one = doc.select("One")
        one.append("<Two ID=2>Two</Two>")
        Assertions.assertEquals("<One>One<Two ID=\"2\">Two</Two></One>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun disablesPrettyPrintingByDefault() {
        val xml = "\n\n<div><one>One</one><one>\n Two</one>\n</div>\n "
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals(xml, doc.html())
    }

    @Test
    fun canNormalizeCase() {
        val xml = "<TEST ID=1>Check</TEST>"
        val doc = parse(xml, "", xmlParser().settings(ParseSettings.htmlDefault))
        Assertions.assertEquals("<test id=\"1\">Check</test>", TextUtil.stripNewlines(doc.html()))
    }

    @Test
    fun normalizesDiscordantTags() {
        val parser = xmlParser().settings(ParseSettings.htmlDefault)
        val document = parse("<div>test</DIV><p></p>", "", parser)
        Assertions.assertEquals("<div>test</div><p></p>", document.html())
        // was failing -> toString() = "<div>\n test\n <p></p>\n</div>"
    }

    @Test
    fun roundTripsCdata() {
        val xml = "<div id=1><![CDATA[\n<html>\n <foo><&amp;]]></div>"
        val doc = parse(xml, "", xmlParser())
        val div = doc.getElementById("1")
        Assertions.assertEquals("<html>\n <foo><&amp;", div!!.text())
        Assertions.assertEquals(0, div.children().size)
        Assertions.assertEquals(1, div.childNodeSize()) // no elements, one text node
        Assertions.assertEquals("<div id=\"1\"><![CDATA[\n<html>\n <foo><&amp;]]></div>", div.outerHtml())
        val cdata = div.textNodes()[0] as CDataNode
        Assertions.assertEquals("\n<html>\n <foo><&amp;", cdata.text())
    }

    @Test
    fun cdataPreservesWhiteSpace() {
        val xml = "<script type=\"text/javascript\">//<![CDATA[\n\n  foo();\n//]]></script>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals(xml, doc.outerHtml())
        Assertions.assertEquals("//\n\n  foo();\n//", doc.selectFirst("script")!!.text())
    }

    @Test
    fun handlesDodgyXmlDecl() {
        val xml = "<?xml version='1.0'><val>One</val>"
        val doc = parse(xml, "", xmlParser())
        Assertions.assertEquals("One", doc.select("val").text())
    }

    @Test
    fun handlesLTinScript() {
        // https://github.com/jhy/jsoup/issues/1139
        val html = "<script> var a=\"<?\"; var b=\"?>\"; </script>"
        val doc = parse(html, "", xmlParser())
        Assertions.assertEquals(
            "<script> var a=\"<!--?\"; var b=\"?-->\"; </script>",
            doc.html()
        ) // converted from pseudo xmldecl to comment
    }

    @Test
    fun dropsDuplicateAttributes() {
        // case sensitive, so should drop Four and Five
        val html = "<p One=One ONE=Two one=Three One=Four ONE=Five two=Six two=Seven Two=Eight>Text</p>"
        val parser = xmlParser().setTrackErrors(10)
        val doc = parser.parseInput(html, "")
        Assertions.assertEquals(
            "<p One=\"One\" ONE=\"Two\" one=\"Three\" two=\"Six\" Two=\"Eight\">Text</p>", doc.selectFirst("p")!!
                .outerHtml()
        )
    }

    @Test
    fun readerClosedAfterParse() {
        val doc = parse("Hello", "", xmlParser())
        val treeBuilder = doc.parser().treeBuilder
        Assertions.assertNull(treeBuilder.reader)
        //        assertNull(treeBuilder.tokeniser); // TODO
    }

    @Test
    fun xmlParserEnablesXmlOutputAndEscapes() {
        // Test that when using the XML parser, the output mode and escape mode default to XHTML entities
        // https://github.com/jhy/jsoup/issues/1420
        val doc = parse("<p one='&lt;two&gt;&copy'>Three</p>", "", xmlParser())
        Assertions.assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml)
        Assertions.assertEquals(doc.outputSettings().escapeMode(), Entities.EscapeMode.xhtml)
        Assertions.assertEquals("<p one=\"&lt;two>©\">Three</p>", doc.html()) // only the < should be escaped
    }

    @Test
    fun xmlSyntaxEscapesLtInAttributes() {
        // Regardless of the entity escape mode, make sure < is escaped in attributes when in XML
        val doc = parse("<p one='&lt;two&gt;&copy'>Three</p>", "", xmlParser())
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended)
        doc.outputSettings().charset("ascii") // to make sure &copy; is output
        Assertions.assertEquals(doc.outputSettings().syntax(), Document.OutputSettings.Syntax.xml)
        Assertions.assertEquals("<p one=\"&lt;two>&copy;\">Three</p>", doc.html())
    }

    @Test
    fun xmlOutputCorrectsInvalidAttributeNames() {
        val xml = "<body style=\"color: red\" \" name\"><div =\"\"></div></body>"
        val doc = parse(xml, xmlParser())
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
        val out = doc.html()
        Assertions.assertEquals("<body style=\"color: red\" name=\"\"><div></div></body>", out)
    }

    @Test
    fun customTagsAreFlyweights() {
        val xml = "<foo>Foo</foo><foo>Foo</foo><FOO>FOO</FOO><FOO>FOO</FOO>"
        val doc = parse(xml, xmlParser())
        val els = doc.children()
        val t1 = els[0].tag()
        val t2 = els[1].tag()
        val t3 = els[2].tag()
        val t4 = els[3].tag()
        Assertions.assertEquals("foo", t1.name)
        Assertions.assertEquals("FOO", t3.name)
        Assertions.assertSame(t1, t2)
        Assertions.assertSame(t3, t4)
    }
}
