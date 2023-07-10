package org.jsoup.integrationimport

import org.jsoup.Connection
import org.jsoup.Connection.KeyVal.value
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.Jsoup.newSession
import org.jsoup.Jsoup.parse
import org.jsoup.helper.*
import org.jsoup.helper.W3CDom.Companion.asString
import org.jsoup.integration.ConnectTest
import org.jsoup.integration.ParseTest
import org.jsoup.integration.TestServer
import org.jsoup.integration.UrlConnectTest
import org.jsoup.integration.servlets.*
import org.jsoup.internal.StringUtil
import org.jsoup.nodes.*
import org.jsoup.nodes.Attribute.value
import org.jsoup.nodes.Element.value
import org.jsoup.parser.*
import org.jsoup.parser.Parser.Companion.htmlParser
import org.jsoup.parser.Parser.Companion.xmlParser
import org.jsoup.select.Elements.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.*
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
import org.jsoup.integration.ParseTest
import java.nio.charset.Charset
import org.jsoup.integration.servlets.FileServlet
import org.jsoup.integration.UrlConnectTest
import org.jsoup.nodes.BuildEntities.CharacterRef
import org.jsoup.nodes.BuildEntities.ByName
import org.jsoup.nodes.BuildEntities.ByCode
import org.jsoup.integration.servlets.EchoServlet
import org.jsoup.integration.servlets.CookieServlet
import org.junit.jupiter.api.BeforeAll
import org.jsoup.integration.TestServer
import org.jsoup.helper.DataUtilTest.VaryingReadInputStream
import org.jsoup.MultiLocaleExtension.MultiLocaleTest
import java.util.Locale
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
 * Tests Jsoup.connect against a local server.
 */
class ConnectTest {
    @Test
    @Throws(IOException::class)
    fun canConnectToLocalServer() {
        val url: String = HelloServlet.Companion.Url
        val doc = Jsoup.connect(url).get()
        val p = doc.selectFirst("p")
        Assertions.assertEquals("Hello, World!", p!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun fetchURl() {
        val doc = parse(URL(ConnectTest.Companion.echoUrl), 10 * 1000)
        Assertions.assertTrue(doc.title().contains("Environment Variables"))
    }

    @Test
    @Throws(IOException::class)
    fun fetchURIWithWhitespace() {
        val con = Jsoup.connect(ConnectTest.Companion.echoUrl + "#with whitespaces")
        val doc = con.get()
        Assertions.assertTrue(doc.title().contains("Environment Variables"))
    }

    @Test
    fun exceptOnUnsupportedProtocol() {
        val url = "file://etc/passwd"
        var threw = false
        try {
            val doc = Jsoup.connect(url).get()
        } catch (e: MalformedURLException) {
            threw = true
            Assertions.assertEquals(
                "java.net.MalformedURLException: Only http & https protocols supported",
                e.toString()
            )
        } catch (e: IOException) {
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun throwsExceptionOn404() {
        val url: String = EchoServlet.Companion.Url
        val con = Jsoup.connect(url).header(EchoServlet.Companion.CodeParam, "404")
        var threw = false
        try {
            val doc = con.get()
        } catch (e: HttpStatusException) {
            threw = true
            Assertions.assertEquals(
                "org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=[" + e.url + "]",
                e.toString()
            )
            Assertions.assertTrue(e.url.startsWith(url))
            Assertions.assertEquals(404, e.statusCode)
        } catch (e: IOException) {
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun ignoresExceptionIfSoConfigured() {
        val url: String = EchoServlet.Companion.Url
        val con = Jsoup.connect(url)
            .header(EchoServlet.Companion.CodeParam, "404")
            .ignoreHttpErrors(true)
        val res = con.execute()
        val doc = res.parse()
        Assertions.assertEquals(404, res.statusCode())
        Assertions.assertEquals("Webserver Environment Variables", doc.title())
    }

    @Test
    @Throws(IOException::class)
    fun doesPost() {
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .cookie("auth", "token")
            .post()
        Assertions.assertEquals("POST", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals("gzip", ConnectTest.Companion.ihVal("Accept-Encoding", doc))
        Assertions.assertEquals("auth=token", ConnectTest.Companion.ihVal("Cookie", doc))
        Assertions.assertEquals("度一下", ConnectTest.Companion.ihVal("百", doc))
        Assertions.assertEquals("Jsoup, Jonathan", ConnectTest.Companion.ihVal("uname", doc))
        Assertions.assertEquals(
            "application/x-www-form-urlencoded; charset=UTF-8",
            ConnectTest.Companion.ihVal("Content-Type", doc)
        )
    }

    @Test
    @Throws(IOException::class)
    fun doesPostMultipartWithoutInputstream() {
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .header(HttpConnection.CONTENT_TYPE, HttpConnection.MULTIPART_FORM_DATA)
            .userAgent(UrlConnectTest.Companion.browserUa)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .post()
        Assertions.assertTrue(
            ConnectTest.Companion.ihVal("Content-Type", doc).contains(HttpConnection.MULTIPART_FORM_DATA)
        )
        Assertions.assertTrue(
            ConnectTest.Companion.ihVal("Content-Type", doc).contains("boundary")
        ) // should be automatically set
        Assertions.assertEquals("Jsoup, Jonathan", ConnectTest.Companion.ihVal("uname", doc))
        Assertions.assertEquals("度一下", ConnectTest.Companion.ihVal("百", doc))
    }

    @Test
    @Throws(IOException::class)
    fun canSendSecFetchHeaders() {
        // https://github.com/jhy/jsoup/issues/1461
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .header("Random-Header-name", "hello")
            .header("Sec-Fetch-Site", "cross-site")
            .header("Sec-Fetch-Mode", "cors")
            .get()
        Assertions.assertEquals("hello", ConnectTest.Companion.ihVal("Random-Header-name", doc))
        Assertions.assertEquals("cross-site", ConnectTest.Companion.ihVal("Sec-Fetch-Site", doc))
        Assertions.assertEquals("cors", ConnectTest.Companion.ihVal("Sec-Fetch-Mode", doc))
    }

    @Test
    @Throws(IOException::class)
    fun secFetchHeadersSurviveRedirect() {
        val doc = Jsoup
            .connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.LocationParam, ConnectTest.Companion.echoUrl)
            .header("Random-Header-name", "hello")
            .header("Sec-Fetch-Site", "cross-site")
            .header("Sec-Fetch-Mode", "cors")
            .get()
        Assertions.assertEquals("hello", ConnectTest.Companion.ihVal("Random-Header-name", doc))
        Assertions.assertEquals("cross-site", ConnectTest.Companion.ihVal("Sec-Fetch-Site", doc))
        Assertions.assertEquals("cors", ConnectTest.Companion.ihVal("Sec-Fetch-Mode", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBodyJsonWithData() {
        val body = "{key:value}"
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .requestBody(body)
            .header("Content-Type", "application/json")
            .userAgent(UrlConnectTest.Companion.browserUa)
            .data("foo", "true")
            .post()
        Assertions.assertEquals("POST", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals("application/json", ConnectTest.Companion.ihVal("Content-Type", doc))
        Assertions.assertEquals("foo=true", ConnectTest.Companion.ihVal("Query String", doc))
        Assertions.assertEquals(body, ConnectTest.Companion.ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBodyJsonWithoutData() {
        val body = "{key:value}"
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .requestBody(body)
            .header("Content-Type", "application/json")
            .userAgent(UrlConnectTest.Companion.browserUa)
            .post()
        Assertions.assertEquals("POST", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals("application/json", ConnectTest.Companion.ihVal("Content-Type", doc))
        Assertions.assertEquals(body, ConnectTest.Companion.ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBody() {
        val body = "{key:value}"
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .requestBody(body)
            .header("Content-Type", "text/plain")
            .userAgent(UrlConnectTest.Companion.browserUa)
            .post()
        Assertions.assertEquals("POST", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals("text/plain", ConnectTest.Companion.ihVal("Content-Type", doc))
        Assertions.assertEquals(body, ConnectTest.Companion.ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun sendsRequestBodyWithUrlParams() {
        val body = "{key:value}"
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .requestBody(body)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .header("Content-Type", "text/plain") // todo - if user sets content-type, we should append postcharset
            .userAgent(UrlConnectTest.Companion.browserUa)
            .post()
        Assertions.assertEquals("POST", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals(
            "uname=Jsoup&uname=Jonathan&%E7%99%BE=%E5%BA%A6%E4%B8%80%E4%B8%8B",
            ConnectTest.Companion.ihVal("Query String", doc)
        )
        Assertions.assertEquals(body, ConnectTest.Companion.ihVal("Post Data", doc))
    }

    @Test
    @Throws(IOException::class)
    fun doesGet() {
        val con = Jsoup.connect(ConnectTest.Companion.echoUrl + "?what=the")
            .userAgent("Mozilla")
            .referrer("http://example.com")
            .data("what", "about & me?")
        val doc = con.get()
        Assertions.assertEquals("what=the&what=about+%26+me%3F", ConnectTest.Companion.ihVal("Query String", doc))
        Assertions.assertEquals("the, about & me?", ConnectTest.Companion.ihVal("what", doc))
        Assertions.assertEquals("Mozilla", ConnectTest.Companion.ihVal("User-Agent", doc))
        Assertions.assertEquals("http://example.com", ConnectTest.Companion.ihVal("Referer", doc))
    }

    @Test
    @Throws(IOException::class)
    fun doesPut() {
        val res = Jsoup.connect(ConnectTest.Companion.echoUrl)
            .data("uname", "Jsoup", "uname", "Jonathan", "百", "度一下")
            .cookie("auth", "token")
            .method(Connection.Method.PUT)
            .execute()
        val doc = res.parse()
        Assertions.assertEquals("PUT", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals("gzip", ConnectTest.Companion.ihVal("Accept-Encoding", doc))
        Assertions.assertEquals("auth=token", ConnectTest.Companion.ihVal("Cookie", doc))
    }

    /**
     * Tests upload of content to a remote service.
     */
    @Test
    @Throws(IOException::class)
    fun postFiles() {
        val thumb: File = ParseTest.Companion.getFile("/htmltests/thumb.jpg")
        val html: File = ParseTest.Companion.getFile("/htmltests/large.html")
        val res = Jsoup
            .connect(EchoServlet.Companion.Url)
            .data("firstname", "Jay")
            .data("firstPart", thumb.name, FileInputStream(thumb), "image/jpeg")
            .data("secondPart", html.name, FileInputStream(html)) // defaults to "application-octetstream";
            .data("surname", "Soup")
            .post()
        Assertions.assertEquals("4", ConnectTest.Companion.ihVal("Parts", res))
        Assertions.assertEquals(
            "application/octet-stream",
            ConnectTest.Companion.ihVal("Part secondPart ContentType", res)
        )
        Assertions.assertEquals("secondPart", ConnectTest.Companion.ihVal("Part secondPart Name", res))
        Assertions.assertEquals("large.html", ConnectTest.Companion.ihVal("Part secondPart Filename", res))
        Assertions.assertEquals("280735", ConnectTest.Companion.ihVal("Part secondPart Size", res))
        Assertions.assertEquals("image/jpeg", ConnectTest.Companion.ihVal("Part firstPart ContentType", res))
        Assertions.assertEquals("firstPart", ConnectTest.Companion.ihVal("Part firstPart Name", res))
        Assertions.assertEquals("thumb.jpg", ConnectTest.Companion.ihVal("Part firstPart Filename", res))
        Assertions.assertEquals("1052", ConnectTest.Companion.ihVal("Part firstPart Size", res))
        Assertions.assertEquals("Jay", ConnectTest.Companion.ihVal("firstname", res))
        Assertions.assertEquals("Soup", ConnectTest.Companion.ihVal("surname", res))

        /*
        <tr><th>Part secondPart ContentType</th><td>application/octet-stream</td></tr>
        <tr><th>Part secondPart Name</th><td>secondPart</td></tr>
        <tr><th>Part secondPart Filename</th><td>google-ipod.html</td></tr>
        <tr><th>Part secondPart Size</th><td>43972</td></tr>
        <tr><th>Part firstPart ContentType</th><td>image/jpeg</td></tr>
        <tr><th>Part firstPart Name</th><td>firstPart</td></tr>
        <tr><th>Part firstPart Filename</th><td>thumb.jpg</td></tr>
        <tr><th>Part firstPart Size</th><td>1052</td></tr>
         */
    }

    @Test
    @Throws(IOException::class)
    fun multipleParsesOkAfterBufferUp() {
        val res = Jsoup.connect(ConnectTest.Companion.echoUrl).execute().bufferUp()
        val doc = res.parse()
        Assertions.assertTrue(doc.title().contains("Environment"))
        val doc2 = res.parse()
        Assertions.assertTrue(doc2.title().contains("Environment"))
    }

    @Test
    fun bodyAfterParseThrowsValidationError() {
        Assertions.assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
            val res = Jsoup.connect(ConnectTest.Companion.echoUrl).execute()
            val doc = res.parse()
            val body = res.body()
        }
    }

    @Test
    @Throws(IOException::class)
    fun bodyAndBytesAvailableBeforeParse() {
        val res = Jsoup.connect(ConnectTest.Companion.echoUrl).execute()
        val body = res.body()
        Assertions.assertTrue(body.contains("Environment"))
        val bytes = res.bodyAsBytes()
        Assertions.assertTrue(bytes.size > 100)
        val doc = res.parse()
        Assertions.assertTrue(doc.title().contains("Environment"))
    }

    @Test
    fun parseParseThrowsValidates() {
        Assertions.assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
            val res = Jsoup.connect(ConnectTest.Companion.echoUrl).execute()
            val doc = res.parse()
            Assertions.assertTrue(doc.title().contains("Environment"))
            val doc2 = res.parse() // should blow up because the response input stream has been drained
        }
    }

    @Test
    @Throws(IOException::class)
    fun multiCookieSet() {
        val con = Jsoup
            .connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.CodeParam, "302")
            .data(RedirectServlet.Companion.SetCookiesParam, "true")
            .data(RedirectServlet.Companion.LocationParam, ConnectTest.Companion.echoUrl)
        val res = con.execute()

        // test cookies set by redirect:
        val cookies = res.cookies()
        Assertions.assertEquals("asdfg123", cookies["token"])
        Assertions.assertEquals("jhy", cookies["uid"])

        // send those cookies into the echo URL by map:
        val doc = Jsoup.connect(ConnectTest.Companion.echoUrl).cookies(cookies).get()
        Assertions.assertEquals("token=asdfg123; uid=jhy", ConnectTest.Companion.ihVal("Cookie", doc))
    }

    @Test
    @Throws(IOException::class)
    fun requestCookiesSurviveRedirect() {
        // this test makes sure that Request keyval cookies (not in the cookie store) are sent on subsequent redirections,
        // when not using the session method
        val con = Jsoup.connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.LocationParam, ConnectTest.Companion.echoUrl)
            .cookie("LetMeIn", "True")
            .cookie("DoesItWork", "Yes")
        val res = con.execute()
        Assertions.assertEquals(0, res.cookies().size) // were not set by Redir or Echo servlet
        val doc = res.parse()
        Assertions.assertEquals(ConnectTest.Companion.echoUrl, doc.location())
        Assertions.assertEquals("True", ConnectTest.Companion.ihVal("Cookie: LetMeIn", doc))
        Assertions.assertEquals("Yes", ConnectTest.Companion.ihVal("Cookie: DoesItWork", doc))
    }

    @Test
    @Throws(IOException::class)
    fun supportsDeflate() {
        val res = Jsoup.connect(Deflateservlet.Companion.Url).execute()
        Assertions.assertEquals("deflate", res.header("Content-Encoding"))
        val doc = res.parse()
        Assertions.assertEquals("Hello, World!", doc.selectFirst("p")!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun handlesLargerContentLengthParseRead() {
        // this handles situations where the remote server sets a content length greater than it actually writes
        val res = Jsoup.connect(InterruptedServlet.Companion.Url)
            .data(InterruptedServlet.Companion.Magnitude, InterruptedServlet.Companion.Larger)
            .timeout(400)
            .execute()
        val document = res.parse()
        Assertions.assertEquals("Something", document.title())
        Assertions.assertEquals(0, document.select("p").size)
        // current impl, jetty won't write past content length
        // todo - find way to trick jetty into writing larger than set header. Take over the stream?
    }

    @Test
    @Throws(IOException::class)
    fun handlesWrongContentLengthDuringBufferedRead() {
        val res = Jsoup.connect(InterruptedServlet.Companion.Url)
            .timeout(400)
            .execute()
        // this servlet writes max_buffer data, but sets content length to max_buffer/2. So will read up to that.
        // previous versions of jetty would allow to write less, and would throw except here
        res.bufferUp()
        val doc = res.parse()
        Assertions.assertEquals(0, doc.select("p").size)
    }

    @Test
    @Throws(IOException::class)
    fun handlesRedirect() {
        val doc = Jsoup.connect(RedirectServlet.Companion.Url)
            .data(RedirectServlet.Companion.LocationParam, HelloServlet.Companion.Url)
            .get()
        val p = doc.selectFirst("p")
        Assertions.assertEquals("Hello, World!", p!!.text())
        Assertions.assertEquals(HelloServlet.Companion.Url, doc.location())
    }

    @Test
    fun handlesEmptyRedirect() {
        var threw = false
        try {
            val res = Jsoup.connect(RedirectServlet.Companion.Url)
                .execute()
        } catch (e: IOException) {
            Assertions.assertTrue(e.message!!.contains("Too many redirects"))
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun doesNotPostFor302() {
        val doc = Jsoup.connect(RedirectServlet.Companion.Url)
            .data("Hello", "there")
            .data(RedirectServlet.Companion.LocationParam, EchoServlet.Companion.Url)
            .post()
        Assertions.assertEquals(EchoServlet.Companion.Url, doc.location())
        Assertions.assertEquals("GET", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertNull(ConnectTest.Companion.ihVal("Hello", doc)) // data not sent
    }

    @Test
    @Throws(IOException::class)
    fun doesPostFor307() {
        val doc = Jsoup.connect(RedirectServlet.Companion.Url)
            .data("Hello", "there")
            .data(RedirectServlet.Companion.LocationParam, EchoServlet.Companion.Url)
            .data(RedirectServlet.Companion.CodeParam, "307")
            .post()
        Assertions.assertEquals(EchoServlet.Companion.Url, doc.location())
        Assertions.assertEquals("POST", ConnectTest.Companion.ihVal("Method", doc))
        Assertions.assertEquals("there", ConnectTest.Companion.ihVal("Hello", doc))
    }

    @get:Throws(IOException::class)
    @get:Test
    val utf8Bom: Unit
        get() {
            val con = Jsoup.connect(FileServlet.Companion.urlTo("/bomtests/bom_utf8.html"))
            val doc = con.get()
            Assertions.assertEquals("UTF-8", con.response().charset())
            Assertions.assertEquals("OK", doc.title())
        }

    @Test
    fun testBinaryContentTypeThrowsException() {
        val con = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/thumb.jpg"))
        con.data(FileServlet.Companion.ContentTypeParam, "image/jpeg")
        var threw = false
        try {
            con.execute()
            val doc = con.response().parse()
        } catch (e: IOException) {
            threw = true
            Assertions.assertEquals(
                "Unhandled content type. Must be text/*, application/xml, or application/*+xml",
                e.message
            )
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun testParseRss() {
        // test that we switch automatically to xml, and we support application/rss+xml
        val con = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/test-rss.xml"))
        con.data(FileServlet.Companion.ContentTypeParam, "application/rss+xml")
        val doc = con.get()
        val title = doc.selectFirst("title")
        Assertions.assertNotNull(title)
        Assertions.assertEquals("jsoup RSS news", title!!.text())
        Assertions.assertEquals("channel", title.parent()!!.nodeName())
        Assertions.assertEquals(
            "",
            doc.title()
        ) // the document title is unset, this tag is channel>title, not html>head>title
        Assertions.assertEquals(3, doc.select("link").size)
        Assertions.assertEquals("application/rss+xml", con.response().contentType())
        Assertions.assertTrue(doc.parser().treeBuilder is XmlTreeBuilder)
        Assertions.assertEquals(Document.OutputSettings.Syntax.xml, doc.outputSettings().syntax())
    }

    @Test
    @Throws(IOException::class)
    fun canFetchBinaryAsBytes() {
        val res = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/thumb.jpg"))
            .data(FileServlet.Companion.ContentTypeParam, "image/jpeg")
            .ignoreContentType(true)
            .execute()
        val bytes = res.bodyAsBytes()
        Assertions.assertEquals(1052, bytes.size)
    }

    @Test
    @Throws(IOException::class)
    fun handlesUnknownEscapesAcrossBuffer() {
        val localPath = "/htmltests/escapes-across-buffer.html"
        val localUrl: String = FileServlet.Companion.urlTo(localPath)
        val docFromLocalServer = Jsoup.connect(localUrl).get()
        val docFromFileRead: Document = parse(ParseTest.Companion.getFile(localPath), "UTF-8")
        val text = docFromLocalServer.body().text()
        Assertions.assertEquals(14766, text.length)
        Assertions.assertEquals(text, docFromLocalServer.body().text())
        Assertions.assertEquals(text, docFromFileRead.body().text())
    }

    /**
     * Test fetching a form, and submitting it with a file attached.
     */
    @Test
    @Throws(IOException::class)
    fun postHtmlFile() {
        val index = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/upload-form.html")).get()
        val forms = index.select("[name=tidy]").forms()
        Assertions.assertEquals(1, forms.size)
        val form = forms[0]
        val post = form.submit()
        val uploadFile: File = ParseTest.Companion.getFile("/htmltests/large.html")
        val stream = FileInputStream(uploadFile)
        val fileData = post.data("_file")
        Assertions.assertNotNull(fileData)
        fileData!!.value("check.html")
        fileData.inputStream(stream)
        val res: Connection.Response
        res = try {
            post.execute()
        } finally {
            stream.close()
        }
        val doc = res.parse()
        Assertions.assertEquals(ConnectTest.Companion.ihVal("Method", doc), "POST") // from form action
        Assertions.assertEquals(ConnectTest.Companion.ihVal("Part _file Filename", doc), "check.html")
        Assertions.assertEquals(ConnectTest.Companion.ihVal("Part _file Name", doc), "_file")
        Assertions.assertEquals(ConnectTest.Companion.ihVal("_function", doc), "tidy")
    }

    @Test
    @Throws(IOException::class)
    fun fetchHandlesXml() {
        val types = arrayOf("text/xml", "application/xml", "application/rss+xml", "application/xhtml+xml")
        for (type in types) {
            fetchHandlesXml(type)
        }
    }

    @Throws(IOException::class)
    fun fetchHandlesXml(contentType: String?) {
        // should auto-detect xml and use XML parser, unless explicitly requested the html parser
        val xmlUrl: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val con = Jsoup.connect(xmlUrl)
        con.data(FileServlet.Companion.ContentTypeParam, contentType!!)
        val doc = con.get()
        val req = con.request()
        Assertions.assertTrue(req.parser().treeBuilder is XmlTreeBuilder)
        Assertions.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>\n", doc.outerHtml())
        Assertions.assertEquals(con.response().contentType(), contentType)
    }

    @Test
    @Throws(IOException::class)
    fun fetchHandlesXmlAsHtmlWhenParserSet() {
        // should auto-detect xml and use XML parser, unless explicitly requested the html parser
        val xmlUrl: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val con = Jsoup.connect(xmlUrl).parser(htmlParser())
        con.data(FileServlet.Companion.ContentTypeParam, "application/xml")
        val doc = con.get()
        val req = con.request()
        Assertions.assertTrue(req.parser().treeBuilder is HtmlTreeBuilder)
        Assertions.assertEquals(
            "<html> <head></head> <body> <doc> <val> One <val> Two </val>Three </val> </doc> </body> </html>",
            StringUtil.normaliseWhitespace(doc.outerHtml())
        )
    }

    @Test
    @Throws(IOException::class)
    fun combinesSameHeadersWithComma() {
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
        val con = Jsoup.connect(ConnectTest.Companion.echoUrl)
        con.get()
        val res = con.response()
        Assertions.assertEquals("text/html;charset=utf-8", res.header("Content-Type"))
        Assertions.assertEquals("no-cache, no-store", res.header("Cache-Control"))
        val header = res.headers("Cache-Control")
        Assertions.assertEquals(2, header.size)
        Assertions.assertEquals("no-cache", header[0])
        Assertions.assertEquals("no-store", header[1])
    }

    @Test
    @Throws(IOException::class)
    fun sendHeadRequest() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/xml-test.xml")
        val con = Jsoup.connect(url)
            .method(Connection.Method.HEAD)
            .data(FileServlet.Companion.ContentTypeParam, "text/xml")
        val response = con.execute()
        Assertions.assertEquals("text/xml", response.header("Content-Type"))
        Assertions.assertEquals("", response.body()) // head ought to have no body
        val doc = response.parse()
        Assertions.assertEquals("", doc.text())
    }

    @Test
    @Throws(IOException::class)
    fun fetchToW3c() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/upload-form.html")
        val doc = Jsoup.connect(url).get()
        val dom = W3CDom()
        val wDoc = dom.fromJsoup(doc)
        Assertions.assertEquals(url, wDoc.documentURI)
        val html = asString(wDoc)
        Assertions.assertTrue(html.contains("Upload"))
    }

    @Test
    @Throws(IOException::class)
    fun baseHrefCorrectAfterHttpEquiv() {
        // https://github.com/jhy/jsoup/issues/440
        val res = Jsoup.connect(FileServlet.Companion.urlTo("/htmltests/charset-base.html")).execute()
        val doc = res.parse()
        Assertions.assertEquals("http://example.com/foo.jpg", doc.select("img").first()!!.absUrl("src"))
    }

    @Test
    @Throws(IOException::class)
    fun maxBodySize() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val defaultRes = Jsoup.connect(url).execute()
        val smallRes = Jsoup.connect(url).maxBodySize(50 * 1024).execute() // crops
        val mediumRes = Jsoup.connect(url).maxBodySize(200 * 1024).execute() // crops
        val largeRes = Jsoup.connect(url).maxBodySize(300 * 1024).execute() // does not crop
        val unlimitedRes = Jsoup.connect(url).maxBodySize(0).execute()
        val actualDocText = 269535
        Assertions.assertEquals(actualDocText, defaultRes.parse().text().length)
        Assertions.assertEquals(49165, smallRes.parse().text().length)
        Assertions.assertEquals(196577, mediumRes.parse().text().length)
        Assertions.assertEquals(actualDocText, largeRes.parse().text().length)
        Assertions.assertEquals(actualDocText, unlimitedRes.parse().text().length)
    }

    @Test
    @Throws(IOException::class)
    fun repeatable() {
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val con = Jsoup.connect(url).parser(xmlParser())
        val doc1 = con.get()
        val doc2 = con.get()
        Assertions.assertEquals("Large HTML", doc1.title())
        Assertions.assertEquals("Large HTML", doc2.title())
    }

    @Test
    @Throws(IOException::class)
    fun maxBodySizeInReadToByteBuffer() {
        // https://github.com/jhy/jsoup/issues/1774
        // when calling readToByteBuffer, contents were not buffered up
        val url: String = FileServlet.Companion.urlTo("/htmltests/large.html") // 280 K
        val defaultRes = Jsoup.connect(url).execute()
        val smallRes = Jsoup.connect(url).maxBodySize(50 * 1024).execute() // crops
        val mediumRes = Jsoup.connect(url).maxBodySize(200 * 1024).execute() // crops
        val largeRes = Jsoup.connect(url).maxBodySize(300 * 1024).execute() // does not crop
        val unlimitedRes = Jsoup.connect(url).maxBodySize(0).execute()
        val actualDocText = 280735
        Assertions.assertEquals(actualDocText, defaultRes.body().length)
        Assertions.assertEquals(50 * 1024, smallRes.body().length)
        Assertions.assertEquals(200 * 1024, mediumRes.body().length)
        Assertions.assertEquals(actualDocText, largeRes.body().length)
        Assertions.assertEquals(actualDocText, unlimitedRes.body().length)
    }

    @Test
    @Throws(IOException::class)
    fun formLoginFlow() {
        val echoUrl: String = EchoServlet.Companion.Url
        val cookieUrl: String = CookieServlet.Companion.Url
        val startUrl: String = FileServlet.Companion.urlTo("/htmltests/form-tests.html")
        val loginDoc = Jsoup.connect(startUrl).get()
        val form = loginDoc.expectForm("#login")
        Assertions.assertNotNull(form)
        form!!.expectFirst("[name=username]").value("admin")
        form.expectFirst("[name=password]").value("Netscape engineers are weenies!")

        // post it- should go to Cookie then bounce to Echo
        val submit = form.submit()
        Assertions.assertEquals(Connection.Method.POST, submit.request().method())
        val postRes = submit.execute()
        Assertions.assertEquals(echoUrl, postRes.url().toExternalForm())
        Assertions.assertEquals(Connection.Method.GET, postRes.method())
        val resultDoc = postRes.parse()
        Assertions.assertEquals("One=EchoServlet; One=Root", ConnectTest.Companion.ihVal("Cookie", resultDoc))
        // should be no form data sent to the echo redirect
        Assertions.assertEquals("", ConnectTest.Companion.ihVal("Query String", resultDoc))

        // new request to echo, should not have form data, but should have cookies from implicit session
        val newEcho = submit.newRequest().url(echoUrl).get()
        Assertions.assertEquals("One=EchoServlet; One=Root", ConnectTest.Companion.ihVal("Cookie", newEcho))
        Assertions.assertEquals("", ConnectTest.Companion.ihVal("Query String", newEcho))
        val cookieDoc = submit.newRequest().url(cookieUrl).get()
        Assertions.assertEquals("CookieServlet", ConnectTest.Companion.ihVal("One", cookieDoc)) // different cookie path
    }

    @Test
    @Throws(IOException::class)
    fun formLoginFlow2() {
        val echoUrl: String = EchoServlet.Companion.Url
        val cookieUrl: String = CookieServlet.Companion.Url
        val startUrl: String = FileServlet.Companion.urlTo("/htmltests/form-tests.html")
        val session = newSession()
        val loginDoc = session.newRequest().url(startUrl).get()
        val form = loginDoc.expectForm("#login2")
        Assertions.assertNotNull(form)
        val username = "admin"
        form!!.expectFirst("[name=username]").value(username)
        val password = "Netscape engineers are weenies!"
        form.expectFirst("[name=password]").value(password)
        val submit = form.submit()
        Assertions.assertEquals(username, submit.data("username")!!.value())
        Assertions.assertEquals(password, submit.data("password")!!.value())
        val postRes = submit.execute()
        Assertions.assertEquals(cookieUrl, postRes.url().toExternalForm())
        Assertions.assertEquals(Connection.Method.POST, postRes.method())
        val resultDoc = postRes.parse()
        val echo2 = resultDoc.connection()!!.newRequest().url(echoUrl).get()
        Assertions.assertEquals("", ConnectTest.Companion.ihVal("Query String", echo2)) // should not re-send the data
        Assertions.assertEquals("One=EchoServlet; One=Root", ConnectTest.Companion.ihVal("Cookie", echo2))
    }

    @Test
    @Throws(IOException::class)
    fun preservesUrlFragment() {
        // confirms https://github.com/jhy/jsoup/issues/1686
        val url: String = EchoServlet.Companion.Url + "#fragment"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals(url, doc.location())
    }

    @Test
    @Throws(IOException::class)
    fun fetchUnicodeUrl() {
        val url: String = EchoServlet.Companion.Url + "/✔/?鍵=値"
        val doc = Jsoup.connect(url).get()
        Assertions.assertEquals("/✔/", ConnectTest.Companion.ihVal("Path Info", doc))
        Assertions.assertEquals("%E9%8D%B5=%E5%80%A4", ConnectTest.Companion.ihVal("Query String", doc))
        Assertions.assertEquals(
            "鍵=値",
            URLDecoder.decode(ConnectTest.Companion.ihVal("Query String", doc), DataUtil.UTF_8.name())
        )
    }

    companion object {
        private val echoUrl: String? = null
        @BeforeAll
        fun setUp() {
            TestServer.start()
            ConnectTest.Companion.echoUrl = EchoServlet.Companion.Url
        }

        private fun ihVal(key: String, doc: Document): String? {
            val first = doc.select("th:contains($key) + td").first()
            return first?.text()
        }
    }
}
