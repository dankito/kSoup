package org.jsoup.integrationimport

import org.jsoup.Connection
import org.jsoup.Connection.KeyVal.value
import org.jsoup.Jsoup
import org.jsoup.Jsoup.parse
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.integration.UrlConnectTest
import org.jsoup.nodes.*
import org.jsoup.nodes.Attribute.value
import org.jsoup.nodes.Element.value
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.select.Elements.value
import org.junit.jupiter.api.*
import java.io.IOException
import java.net.*

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

/**
 * Tests the URL connection. Not enabled by default, so tests don't require network connection.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
@Disabled // ignored by default so tests don't require network access. comment out to enable.
// todo: rebuild these into a local Jetty test server, so not reliant on the vagaries of the internet.

class UrlConnectTest {
    @Test
    @Throws(IOException::class)
    fun fetchBaidu() {
        val res = Jsoup.connect("http://www.baidu.com/").timeout(10 * 1000).execute()
        val doc = res.parse()
        Assertions.assertEquals("GBK", doc.outputSettings().charset()!!.displayName())
        Assertions.assertEquals("GBK", res.charset())
        assert(res.hasCookie("BAIDUID"))
        Assertions.assertEquals("text/html;charset=gbk", res.contentType())
    }

    @Test
    fun exceptOnUnknownContentType() {
        val url = "http://direct.jsoup.org/rez/osi_logo.png" // not text/* but image/png, should throw
        var threw = false
        try {
            val doc = parse(URL(url), 3000)
        } catch (e: UnsupportedMimeTypeException) {
            threw = true
            Assertions.assertEquals(
                "org.jsoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml. Mimetype=image/png, URL=http://direct.jsoup.org/rez/osi_logo.png",
                e.toString()
            )
            Assertions.assertEquals(url, e.url)
            Assertions.assertEquals("image/png", e.mimeType)
        } catch (e: IOException) {
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun ignoresContentTypeIfSoConfigured() {
        val doc = Jsoup.connect("https://jsoup.org/rez/osi_logo.png").ignoreContentType(true).get()
        Assertions.assertEquals("", doc.title()) // this will cause an ugly parse tree
    }

    @Test
    @Throws(IOException::class)
    fun followsTempRedirect() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302.pl") // http://jsoup.org
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
    }

    @Test
    @Throws(IOException::class)
    fun followsNewTempRedirect() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/307.pl") // http://jsoup.org
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
        Assertions.assertEquals("https://jsoup.org/", con.response().url().toString())
    }

    @Test
    @Throws(IOException::class)
    fun postRedirectsFetchWithGet() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302.pl")
            .data("Argument", "Riposte")
            .method(Connection.Method.POST)
        val res = con.execute()
        Assertions.assertEquals("https://jsoup.org/", res.url().toExternalForm())
        Assertions.assertEquals(Connection.Method.GET, res.method())
    }

    @Test
    @Throws(IOException::class)
    fun followsRedirectToHttps() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-secure.pl") // https://www.google.com
        con.data("id", "5")
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("Google"))
    }

    @Test
    @Throws(IOException::class)
    fun followsRelativeRedirect() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-rel.pl") // to /tidy/
        val doc = con.post()
        Assertions.assertTrue(doc.title().contains("HTML Tidy Online"))
    }

    @Test
    @Throws(IOException::class)
    fun followsRelativeDotRedirect() {
        // redirects to "./ok.html", should resolve to http://direct.infohound.net/tools/ok.html
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-rel-dot.pl") // to ./ok.html
        val doc = con.post()
        Assertions.assertTrue(doc.title().contains("OK"))
        Assertions.assertEquals(doc.location(), "http://direct.infohound.net/tools/ok.html")
    }

    @Test
    @Throws(IOException::class)
    fun followsRelativeDotRedirect2() {
        //redirects to "esportspenedes.cat/./ep/index.php", should resolve to "esportspenedes.cat/ep/index.php"
        val con =
            Jsoup.connect("http://esportspenedes.cat") // note lack of trailing / - server should redir to / first, then to ./ep/...; but doesn't'
                .timeout(10000)
        val doc = con.post()
        Assertions.assertEquals(doc.location(), "http://esportspenedes.cat/ep/index.php")
    }

    @Test
    @Throws(IOException::class)
    fun followsRedirectsWithWithespaces() {
        val con = Jsoup.connect("http://tinyurl.com/kgofxl8") // to http://www.google.com/?q=white spaces
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("Google"))
    }

    @Test
    @Throws(IOException::class)
    fun gracefullyHandleBrokenLocationRedirect() {
        val con = Jsoup.connect("http://aag-ye.com") // has Location: http:/temp/AAG_New/en/index.php
        con.get() // would throw exception on error
        Assertions.assertTrue(true)
    }

    @Test
    @Throws(IOException::class)
    fun ignores500tExceptionIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/500.pl").ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(500, res.statusCode())
        Assertions.assertEquals("Application Error", res.statusMessage())
        Assertions.assertEquals("Woops", doc.select("h1").first()!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun ignores500WithNoContentExceptionIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/500-no-content.pl").ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(500, res.statusCode())
        Assertions.assertEquals("Application Error", res.statusMessage())
    }

    @Test
    @Throws(IOException::class)
    fun ignores200WithNoContentExceptionIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/200-no-content.pl").ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
        Assertions.assertEquals("All Good", res.statusMessage())
    }

    @Test
    @Throws(IOException::class)
    fun handles200WithNoContent() {
        var con = Jsoup
            .connect("http://direct.infohound.net/tools/200-no-content.pl")
            .userAgent(UrlConnectTest.Companion.browserUa)
        var res = con.execute()
        var doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
        con = Jsoup
            .connect("http://direct.infohound.net/tools/200-no-content.pl")
            .parser(xmlParser())
            .userAgent(UrlConnectTest.Companion.browserUa)
        res = con.execute()
        doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
    }

    @Test
    @Throws(IOException::class)
    fun doesntRedirectIfSoConfigured() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302.pl").followRedirects(false)
        val res = con.execute()
        Assertions.assertEquals(302, res.statusCode())
        Assertions.assertEquals("http://jsoup.org", res.header("Location"))
    }

    @Test
    @Throws(IOException::class)
    fun redirectsResponseCookieToNextResponse() {
        val con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl")
        val res = con.execute()
        Assertions.assertEquals(
            "asdfg123",
            res.cookie("token")
        ) // confirms that cookies set on 1st hit are presented in final result
        val doc = res.parse()
        Assertions.assertEquals(
            "token=asdfg123; uid=jhy",
            UrlConnectTest.Companion.ihVal("HTTP_COOKIE", doc)
        ) // confirms that redirected hit saw cookie
    }

    @Test
    fun maximumRedirects() {
        var threw = false
        try {
            val doc = Jsoup.connect("http://direct.infohound.net/tools/loop.pl").get()
        } catch (e: IOException) {
            Assertions.assertTrue(e.message!!.contains("Too many redirects"))
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun handlesDodgyCharset() {
        // tests that when we get back "UFT8", that it is recognised as unsupported, and falls back to default instead
        val url = "http://direct.infohound.net/tools/bad-charset.pl"
        val res = Jsoup.connect(url).execute()
        Assertions.assertEquals("text/html; charset=UFT8", res.header("Content-Type")) // from the header
        Assertions.assertNull(res.charset()) // tried to get from header, not supported, so returns null
        val doc = res.parse() // would throw an error if charset unsupported
        Assertions.assertTrue(doc.text().contains("Hello!"))
        Assertions.assertEquals("UTF-8", res.charset()) // set from default on parse
    }

    /**
     * Verify that security disabling feature works properly.
     *
     *
     * 1. try to hit url with invalid certificate and evaluate that exception is thrown
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testUnsafeFail() {
        val url: String = UrlConnectTest.Companion.WEBSITE_WITH_INVALID_CERTIFICATE
        Assertions.assertThrows(IOException::class.java) { Jsoup.connect(url).execute() }
    }

    /**
     * Verify that requests to websites with SNI fail on jdk 1.6
     *
     *
     * read for more details:
     * http://en.wikipedia.org/wiki/Server_Name_Indication
     *
     * Test is ignored independent from others as it requires JDK 1.6
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testSNIFail() {
        Assertions.assertThrows<IOException>(IOException::class.java) {
            Jsoup.connect(UrlConnectTest.Companion.WEBSITE_WITH_SNI).execute()
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldWorkForCharsetInExtraAttribute() {
        val res = Jsoup.connect("https://www.creditmutuel.com/groupe/fr/").execute()
        val doc = res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("ISO-8859-1", res.charset())
    }

    // The following tests were added to test specific domains if they work. All code paths
    // which make the following test green are tested in other unit or integration tests, so the following lines
    // could be deleted
    @Test
    @Throws(IOException::class)
    fun shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        val res = Jsoup.connect("http://aamo.info/").execute()
        res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("ISO-8859-1", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun shouldParseBrokenHtml5MetaCharsetTagCorrectly() {
        val res = Jsoup.connect("http://9kuhkep.net").execute()
        res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("UTF-8", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun shouldEmptyMetaCharsetCorrectly() {
        val res = Jsoup.connect("http://aastmultimedia.com").execute()
        res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("UTF-8", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun shouldWorkForDuplicateCharsetInTag() {
        val res = Jsoup.connect("http://aaptsdassn.org").execute()
        val doc = res.parse() // would throw an error if charset unsupported
        Assertions.assertEquals("ISO-8859-1", res.charset())
    }

    @Test
    @Throws(IOException::class)
    fun handles201Created() {
        val doc = Jsoup.connect("http://direct.infohound.net/tools/201.pl").get() // 201, location=jsoup
        Assertions.assertEquals("https://jsoup.org/", doc.location())
    }

    /*
     Proxy tests. Assumes local proxy running on 8888, without system propery set (so that specifying it is required).
     */
    @Test
    @Throws(IOException::class)
    fun fetchViaHttpProxy() {
        val url = "https://jsoup.org"
        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("localhost", 8888))
        val doc = Jsoup.connect(url).proxy(proxy).get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
    }

    @Test
    @Throws(IOException::class)
    fun fetchViaHttpProxySetByArgument() {
        val url = "https://jsoup.org"
        val doc = Jsoup.connect(url).proxy("localhost", 8888).get()
        Assertions.assertTrue(doc.title().contains("jsoup"))
    }

    @Test
    fun invalidProxyFails() {
        var caught = false
        val url = "https://jsoup.org"
        try {
            val doc = Jsoup.connect(url).proxy("localhost", 8889).get()
        } catch (e: IOException) {
            caught = e is ConnectException
        }
        Assertions.assertTrue(caught)
    }

    @Test
    @Throws(IOException::class)
    fun proxyGetAndSet() {
        val url = "https://jsoup.org"
        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("localhost", 8889)) // invalid
        val con = Jsoup.connect(url).proxy(proxy)
        assert(con.request().proxy() === proxy)
        con.request().proxy(null) // disable
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("jsoup")) // would fail if actually went via proxy
    }

    @Test
    @Throws(IOException::class)
    fun throwsIfRequestBodyForGet() {
        var caught = false
        val url = "https://jsoup.org"
        try {
            val doc = Jsoup.connect(url).requestBody("fail").get()
        } catch (e: IllegalArgumentException) {
            caught = true
        }
        Assertions.assertTrue(caught)
    }

    @Test
    @Throws(IOException::class)
    fun canSpecifyResponseCharset() {
        // both these docs have <80> in there as euro/control char depending on charset
        val noCharsetUrl = "http://direct.infohound.net/tools/Windows-1252-nocharset.html"
        val charsetUrl = "http://direct.infohound.net/tools/Windows-1252-charset.html"

        // included in meta
        val res1 = Jsoup.connect(charsetUrl).execute()
        Assertions.assertNull(res1.charset()) // not set in headers
        val doc1 = res1.parse()
        Assertions.assertEquals("windows-1252", doc1.charset()!!.displayName()) // but determined at parse time
        Assertions.assertEquals("Cost is €100", doc1.select("p").text())
        Assertions.assertTrue(doc1.text().contains("€"))

        // no meta, no override
        val res2 = Jsoup.connect(noCharsetUrl).execute()
        Assertions.assertNull(res2.charset()) // not set in headers
        val doc2 = res2.parse()
        Assertions.assertEquals("UTF-8", doc2.charset()!!.displayName()) // so defaults to utf-8
        Assertions.assertEquals("Cost is �100", doc2.select("p").text())
        Assertions.assertTrue(doc2.text().contains("�"))

        // no meta, let's override
        val res3 = Jsoup.connect(noCharsetUrl).execute()
        Assertions.assertNull(res3.charset()) // not set in headers
        res3.charset("windows-1252")
        Assertions.assertEquals("windows-1252", res3.charset()) // read back
        val doc3 = res3.parse()
        Assertions.assertEquals("windows-1252", doc3.charset()!!.displayName()) // from override
        Assertions.assertEquals("Cost is €100", doc3.select("p").text())
        Assertions.assertTrue(doc3.text().contains("€"))
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnescapedRedirects() {
        // URL locations should be url safe (ascii) but are often not, so we should try to guess
        // in this case the location header is utf-8, but defined in spec as iso8859, so detect, convert, encode
        val url = "http://direct.infohound.net/tools/302-utf.pl"
        val urlEscaped = "http://direct.infohound.net/tools/test%F0%9F%92%A9.html"
        val res = Jsoup.connect(url).execute()
        val doc = res.parse()
        Assertions.assertEquals(doc.body().text(), "\uD83D\uDCA9!")
        Assertions.assertEquals(doc.location(), urlEscaped)
        val res2 = Jsoup.connect(url).followRedirects(false).execute()
        Assertions.assertEquals("/tools/test\uD83D\uDCA9.html", res2.header("Location"))
        // if we didn't notice it was utf8, would look like: Location: /tools/testð©.html
    }

    @Test
    @Throws(IOException::class)
    fun handlesEscapesInRedirecct() {
        var doc = Jsoup.connect("http://infohound.net/tools/302-escaped.pl").get()
        Assertions.assertEquals("http://infohound.net/tools/q.pl?q=one%20two", doc.location())
        doc = Jsoup.connect("http://infohound.net/tools/302-white.pl").get()
        Assertions.assertEquals("http://infohound.net/tools/q.pl?q=one%20two", doc.location())
    }

    @Test
    @Throws(IOException::class)
    fun handlesUt8fInUrl() {
        val url = "http://direct.infohound.net/tools/test\uD83D\uDCA9.html"
        val urlEscaped = "http://direct.infohound.net/tools/test%F0%9F%92%A9.html"
        val res = Jsoup.connect(url).execute()
        val doc = res.parse()
        Assertions.assertEquals("\uD83D\uDCA9!", doc.body().text())
        Assertions.assertEquals(urlEscaped, doc.location())
    }

    @Test
    @Throws(IOException::class)
    fun inWildUtfRedirect() {
        val res = Jsoup.connect("http://brabantn.ws/Q4F").execute()
        val doc = res.parse()
        Assertions.assertEquals(
            "http://www.omroepbrabant.nl/?news/2474781303/Gestrande+ree+in+Oss+niet+verdoofd,+maar+doodgeschoten+%E2%80%98Dit+kan+gewoon+niet,+bizar%E2%80%99+[VIDEO].aspx",
            doc.location()
        )
    }

    @Test
    @Throws(IOException::class)
    fun inWildUtfRedirect2() {
        val res = Jsoup.connect("https://ssl.souq.com/sa-en/2724288604627/s").execute()
        val doc = res.parse()
        Assertions.assertEquals(
            "https://saudi.souq.com/sa-en/%D8%AE%D8%B2%D9%86%D8%A9-%D8%A2%D9%85%D9%86%D8%A9-3-%D8%B7%D8%A8%D9%82%D8%A7%D8%AA-%D8%A8%D9%86%D8%B8%D8%A7%D9%85-%D9%82%D9%81%D9%84-%D8%A5%D9%84%D9%83%D8%AA%D8%B1%D9%88%D9%86%D9%8A-bsd11523-6831477/i/?ctype=dsrch",
            doc.location()
        )
    }

    @Test
    @Throws(IOException::class)
    fun handlesEscapedRedirectUrls() {
        val url =
            "http://www.altalex.com/documents/news/2016/12/06/questioni-civilistiche-conseguenti-alla-depenalizzazione"
        // sends: Location:http://shop.wki.it/shared/sso/sso.aspx?sso=&url=http%3a%2f%2fwww.altalex.com%2fsession%2fset%2f%3freturnurl%3dhttp%253a%252f%252fwww.altalex.com%253a80%252fdocuments%252fnews%252f2016%252f12%252f06%252fquestioni-civilistiche-conseguenti-alla-depenalizzazione
        // then to: http://www.altalex.com/session/set/?returnurl=http%3a%2f%2fwww.altalex.com%3a80%2fdocuments%2fnews%2f2016%2f12%2f06%2fquestioni-civilistiche-conseguenti-alla-depenalizzazione&sso=RDRG6T684G4AK2E7U591UGR923
        // then : http://www.altalex.com:80/documents/news/2016/12/06/questioni-civilistiche-conseguenti-alla-depenalizzazione

        // bug is that jsoup goes to
        // 	GET /shared/sso/sso.aspx?sso=&url=http%253a%252f%252fwww.altalex.com%252fsession%252fset%252f%253freturnurl%253dhttp%25253a%25252f%25252fwww.altalex.com%25253a80%25252fdocuments%25252fnews%25252f2016%25252f12%25252f06%25252fquestioni-civilistiche-conseguenti-alla-depenalizzazione HTTP/1.1
        // i.e. double escaped
        val res = Jsoup.connect(url)
            .proxy("localhost", 8888)
            .execute()
        val doc = res.parse()
        Assertions.assertEquals(200, res.statusCode())
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnicodeInQuery() {
        var doc = Jsoup.connect("https://www.google.pl/search?q=gąska").get()
        Assertions.assertEquals("gąska - Szukaj w Google", doc.title())
        doc = Jsoup.connect("http://mov-world.net/archiv/TV/A/%23No.Title/").get()
        Assertions.assertEquals("Index of /archiv/TV/A/%23No.Title", doc.title())
    }

    @Test
    @Throws(IOException::class)
    fun handlesSuperDeepPage() {
        // https://github.com/jhy/jsoup/issues/955
        val start = System.currentTimeMillis()
        val url = "http://sv.stargate.wikia.com/wiki/M2J"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals("M2J | Sv.stargate Wiki | FANDOM powered by Wikia", doc.title())
        Assertions.assertEquals(110160, doc.select("dd").size)
        // those are all <dl><dd> stacked in each other. wonder how that got generated?
        Assertions.assertTrue(System.currentTimeMillis() - start < 1000)
    }

    @Test
    @Throws(IOException::class)
    fun handles966() {
        // http://szshb.nxszs.gov.cn/
        // https://github.com/jhy/jsoup/issues/966
        val doc = Jsoup.connect("http://szshb.nxszs.gov.cn/").get()
        Assertions.assertEquals("石嘴山市环境保护局", doc.title())
    }

    @Test
    @Throws(IOException::class)
    fun canRequestIdn() {
        val url = "https://räksmörgås.josefsson.org/"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals("https://xn--rksmrgs-5wao1o.josefsson.org/", doc.location())
        Assertions.assertTrue(doc.title().contains("Räksmörgås.josefßon.org"))
    }

    companion object {
        private const val WEBSITE_WITH_INVALID_CERTIFICATE = "https://certs.cac.washington.edu/CAtest/"
        private const val WEBSITE_WITH_SNI = "https://jsoup.org/"
        var browserUa =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36"

        private fun ihVal(key: String, doc: Document): String {
            return doc.select("th:contains($key) + td").first()!!.text()
        }
    }
}
