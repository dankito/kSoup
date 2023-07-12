package net.dankito.ksoup.nodes

import net.dankito.ksoup.Connection
import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.Jsoup.parse
import net.dankito.ksoup.integration.TestServer
import net.dankito.ksoup.integration.servlets.CookieServlet
import net.dankito.ksoup.integration.servlets.EchoServlet
import net.dankito.ksoup.integration.servlets.FileServlet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Tests for FormElement
 *
 * @author Jonathan Hedley
 */
class FormElementTest {
    @Test
    fun hasAssociatedControls() {
        //"button", "fieldset", "input", "keygen", "object", "output", "select", "textarea"
        val html = "<form id=1><button id=1><fieldset id=2 /><input id=3><keygen id=4><object id=5><output id=6>" +
                "<select id=7><option></select><textarea id=8><p id=9>"
        val doc = Jsoup.parse(html)
        val form = doc.select("form").first() as FormElement?
        Assertions.assertEquals(8, form!!.elements().size)
    }

    @Test
    fun createsFormData() {
        val html = "<form><input name='one' value='two'><select name='three'><option value='not'>" +
                "<option value='four' selected><option value='five' selected><textarea name=six>seven</textarea>" +
                "<input name='seven' type='radio' value='on' checked><input name='seven' type='radio' value='off'>" +
                "<input name='eight' type='checkbox' checked><input name='nine' type='checkbox' value='unset'>" +
                "<input name='ten' value='text' disabled>" +
                "<input name='eleven' value='text' type='button'>" +
                "</form>"
        val doc = Jsoup.parse(html)
        val form = doc.select("form").first() as FormElement?
        val data = form!!.formData()
        Assertions.assertEquals(6, data.size)
        Assertions.assertEquals("one=two", data[0].toString())
        Assertions.assertEquals("three=four", data[1].toString())
        Assertions.assertEquals("three=five", data[2].toString())
        Assertions.assertEquals("six=seven", data[3].toString())
        Assertions.assertEquals("seven=on", data[4].toString()) // set
        Assertions.assertEquals("eight=on", data[5].toString()) // default
        // nine should not appear, not checked checkbox
        // ten should not appear, disabled
        // eleven should not appear, button
    }

    @Test
    fun formDataUsesFirstAttribute() {
        val html = "<form><input name=test value=foo name=test2 value=bar>"
        val doc = Jsoup.parse(html)
        val form = doc.selectFirst("form") as FormElement?
        Assertions.assertEquals("test=foo", form!!.formData()[0].toString())
    }

    @Test
    fun createsSubmitableConnection() {
        val html = "<form action='/search'><input name='q'></form>"
        val doc = parse(html, "http://example.com/")
        doc.select("[name=q]").attr("value", "jsoup")
        val form = doc.select("form").first() as FormElement?
        val con = form!!.submit()
        Assertions.assertEquals(Connection.Method.GET, con.request().method())
        Assertions.assertEquals("http://example.com/search", con.request().url().toExternalForm())
        val dataList = con.request().data() as List<Connection.KeyVal>
        Assertions.assertEquals("q=jsoup", dataList[0].toString())
        doc.select("form").attr("method", "post")
        val con2 = form.submit()
        Assertions.assertEquals(Connection.Method.POST, con2.request().method())
    }

    @Test
    fun actionWithNoValue() {
        val html = "<form><input name='q'></form>"
        val doc = parse(html, "http://example.com/")
        val form = doc.select("form").first() as FormElement?
        val con = form!!.submit()
        Assertions.assertEquals("http://example.com/", con.request().url().toExternalForm())
    }

    @Test
    fun actionWithNoBaseUri() {
        val html = "<form><input name='q'></form>"
        val doc = Jsoup.parse(html)
        val form = doc.select("form").first() as FormElement?
        var threw = false
        try {
            form!!.submit()
        } catch (e: IllegalArgumentException) {
            threw = true
            Assertions.assertEquals(
                "Could not determine a form action URL for submit. Ensure you set a base URI when parsing.",
                e.message
            )
        }
        Assertions.assertTrue(threw)
    }

    @Test
    fun formsAddedAfterParseAreFormElements() {
        val doc = Jsoup.parse("<body />")
        doc.body().html("<form action='http://example.com/search'><input name='q' value='search'>")
        val formEl = doc.select("form").first()
        Assertions.assertTrue(formEl is FormElement)
        val form = formEl as FormElement?
        Assertions.assertEquals(1, form!!.elements().size)
    }

    @Test
    fun controlsAddedAfterParseAreLinkedWithForms() {
        val doc = Jsoup.parse("<body />")
        doc.body().html("<form />")
        val formEl = doc.select("form").first()
        formEl!!.append("<input name=foo value=bar>")
        Assertions.assertTrue(formEl is FormElement)
        val form = formEl as FormElement?
        Assertions.assertEquals(1, form!!.elements().size)
        val data = form.formData()
        Assertions.assertEquals("foo=bar", data[0].toString())
    }

    @Test
    fun usesOnForCheckboxValueIfNoValueSet() {
        val doc = Jsoup.parse("<form><input type=checkbox checked name=foo></form>")
        val form = doc.select("form").first() as FormElement?
        val data = form!!.formData()
        Assertions.assertEquals("on", data[0].value())
        Assertions.assertEquals("foo", data[0].key())
    }

    @Test
    fun adoptedFormsRetainInputs() {
        // test for https://github.com/jhy/jsoup/issues/249
        val html = "<html>\n" +
                "<body>  \n" +
                "  <table>\n" +
                "      <form action=\"/hello.php\" method=\"post\">\n" +
                "      <tr><td>User:</td><td> <input type=\"text\" name=\"user\" /></td></tr>\n" +
                "      <tr><td>Password:</td><td> <input type=\"password\" name=\"pass\" /></td></tr>\n" +
                "      <tr><td><input type=\"submit\" name=\"login\" value=\"login\" /></td></tr>\n" +
                "   </form>\n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>"
        val doc = Jsoup.parse(html)
        val form = doc.select("form").first() as FormElement?
        val data = form!!.formData()
        Assertions.assertEquals(3, data.size)
        Assertions.assertEquals("user", data[0].key())
        Assertions.assertEquals("pass", data[1].key())
        Assertions.assertEquals("login", data[2].key())
    }

    @Test
    fun removeFormElement() {
        val html = "<html>\n" +
                "  <body> \n" +
                "      <form action=\"/hello.php\" method=\"post\">\n" +
                "      User:<input type=\"text\" name=\"user\" />\n" +
                "      Password:<input type=\"password\" name=\"pass\" />\n" +
                "      <input type=\"submit\" name=\"login\" value=\"login\" />\n" +
                "   </form>\n" +
                "  </body>\n" +
                "</html>  "
        val doc = Jsoup.parse(html)
        val form = doc.selectFirst("form") as FormElement?
        val pass = form!!.selectFirst("input[name=pass]")
        pass!!.remove()
        val data = form.formData()
        Assertions.assertEquals(2, data.size)
        Assertions.assertEquals("user", data[0].key())
        Assertions.assertEquals("login", data[1].key())
        Assertions.assertNull(doc.selectFirst("input[name=pass]"))
    }

    @Test
    fun formSubmissionCarriesCookiesFromSession() {
        val echoUrl: String = EchoServlet.Companion.Url // this is a dirty hack to initialize the EchoServlet(!)
        val cookieDoc = Jsoup.connect(CookieServlet.Companion.Url)
            .data(CookieServlet.Companion.SetCookiesParam, "1")
            .get()
        val formDoc: Document = cookieDoc.connection()!!.newRequest() // carries cookies from above set
            .url(FileServlet.Companion.urlTo("/htmltests/upload-form.html"))
            .get()
        val form = formDoc.select("form").forms()[0]
        val echo = form.submit().post()
        Assertions.assertEquals(echoUrl, echo.location())
        val els = echo.select("th:contains(Cookie: One)")
        // ensure that the cookies are there and in path-specific order (two with same name)
        Assertions.assertEquals("EchoServlet", els[0].nextElementSibling()!!.text())
        Assertions.assertEquals("Root", els[1].nextElementSibling()!!.text())

        // make sure that the session following kept unique requests
        Assertions.assertTrue(cookieDoc.connection()!!.response().url().toExternalForm().contains("CookieServlet"))
        Assertions.assertTrue(formDoc.connection()!!.response().url().toExternalForm().contains("upload-form"))
        Assertions.assertTrue(echo.connection()!!.response().url().toExternalForm().contains("EchoServlet"))
    }

    companion object {
        @BeforeAll
        fun setUp() {
            TestServer.start()
        }
    }
}
