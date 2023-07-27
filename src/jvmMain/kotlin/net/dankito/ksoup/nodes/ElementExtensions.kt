package net.dankito.ksoup.nodes

//import net.dankito.ksoup.Connection
import net.dankito.ksoup.Jsoup
//import net.dankito.ksoup.helper.HttpConnection
import net.dankito.ksoup.helper.Validate
//import net.dankito.ksoup.newSession
import net.dankito.ksoup.select.Elements


/**
 * Find Elements that match the supplied XPath expression.
 *
 * Note that for convenience of writing the Xpath expression, namespaces are disabled, and queries can be
 * expressed using the element's local name only.
 *
 * By default, XPath 1.0 expressions are supported. If you would to use XPath 2.0 or higher, you can provide an
 * alternate XPathFactory implementation:
 *
 *  1. Add the implementation to your classpath. E.g. to use [Saxon-HE](https://www.saxonica.com/products/products.xml), add [net.sf.saxon:Saxon-HE](https://mvnrepository.com/artifact/net.sf.saxon/Saxon-HE) to your build.
 *  1. Set the system property `javax.xml.xpath.XPathFactory:jsoup` to the implementing classname. E.g.:<br></br>
 * `System.setProperty(W3CDom.XPathFactoryProperty, "net.sf.saxon.xpath.XPathFactoryImpl");`
 *
 *
 *
 * @param xpath XPath expression
 * @return matching elements, or an empty list if none match.
 * @see .selectXpath
 * @since 1.14.3
 */
//fun Element.selectXpath(xpath: String): Elements {
//    return Elements(NodeUtils.selectXpath(xpath, this, Element::class.java))
//}

/**
 * Find Nodes that match the supplied XPath expression.
 *
 * For example, to select TextNodes under `p` elements:
 * <pre>List&lt;TextNode&gt; textNodes = doc.selectXpath("//body//p//text()", TextNode.class);</pre>
 *
 * Note that in the jsoup DOM, Attribute objects are not Nodes. To directly select attribute values, do something
 * like:
 * <pre>List&lt;String&gt; hrefs = doc.selectXpath("//a").eachAttr("href");</pre>
 * @param xpath XPath expression
 * @param nodeType the jsoup node type to return
 * @see .selectXpath
 * @return a list of matching nodes
 * @since 1.14.3
 */
//fun <T : Node> Element.selectXpath(xpath: String, nodeType: Class<T>): List<T> {
//    return NodeUtils.selectXpath(xpath, this, nodeType)
//}

/**
 * Returns the Connection (Request/Response) object that was used to fetch this document, if any; otherwise, a new
 * default Connection object. This can be used to continue a session, preserving settings and cookies, etc.
 * @return the Connection (session) associated with this Document, or an empty one otherwise.
 * @see Connection.newRequest
 */
//fun Document.connection(): Connection {
//    return this.connection ?: Jsoup.newSession()
//}

/**
 * Prepare to submit this form. A Connection object is created with the request set up from the form values. This
 * Connection will inherit the settings and the cookies (etc) of the connection/session used to request this Document
 * (if any), as available in [Document.connection]
 *
 * You can then set up other options (like user-agent, timeout, cookies), then execute it.
 *
 * @return a connection prepared from the values of this form, in the same session as the one used to request it
 * @throws IllegalArgumentException if the form's absolute action URL cannot be determined. Make sure you pass the
 * document's base URI when parsing.
 */
//fun FormElement.submit(): Connection {
//    val action = if (hasAttr("action")) absUrl("action") else baseUri()
//    Validate.notEmpty(
//        action,
//        "Could not determine a form action URL for submit. Ensure you set a base URI when parsing."
//    )
//    val method =
//        if (attr("method").equals("POST", ignoreCase = true)) Connection.Method.POST else Connection.Method.GET
//    val owner = ownerDocument()
//    val connection = if (owner != null) owner.connection()!!.newRequest() else Jsoup.newSession()
//    return connection.url(action)
//        .data(formData())
//        .method(method)
//}

/**
 * Get the data that this form submits. The returned list is a copy of the data, and changes to the contents of the
 * list will not be reflected in the DOM.
 * @return a list of key vals
 */
//fun FormElement.formData(): List<Connection.KeyVal> {
//    val data = ArrayList<Connection.KeyVal>()
//
//    // iterate the form control elements and accumulate their values
//    for (el in elements()) {
//        if (!el.tag().isFormSubmittable) continue  // contents are form listable, superset of submitable
//        if (el.hasAttr("disabled")) continue  // skip disabled form inputs
//        val name = el.attr("name")
//        if (name.length == 0) continue
//        val type = el.attr("type")
//        if (type.equals("button", ignoreCase = true)) continue  // browsers don't submit these
//        if ("select" == el.normalName()) {
//            val options = el.select("option[selected]")
//            var set = false
//            for (option in options) {
//                data.add(HttpConnection.KeyVal.create(name, option.value()))
//                set = true
//            }
//            if (!set) {
//                val option = el.selectFirst("option")
//                if (option != null) data.add(HttpConnection.KeyVal.create(name, option.value()))
//            }
//        } else if ("checkbox".equals(type, ignoreCase = true) || "radio".equals(type, ignoreCase = true)) {
//            // only add checkbox or radio if they have the checked attribute
//            if (el.hasAttr("checked")) {
//                val `val` = if (el.value()!!.length > 0) el.value() else "on"
//                data.add(HttpConnection.KeyVal.create(name, `val`))
//            }
//        } else {
//            data.add(HttpConnection.KeyVal.create(name, el.value()))
//        }
//    }
//    return data
//}