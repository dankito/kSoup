package org.jsoup.integration.servlets

import org.jsoup.integration.TestServer
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CookieServlet : BaseServlet() {
    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        doIt(req, res)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
        doIt(req, res)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPut(req: HttpServletRequest, res: HttpServletResponse) {
        doIt(req, res)
    }

    @Throws(IOException::class)
    private fun doIt(req: HttpServletRequest, res: HttpServletResponse) {
        // Do we want to set cookies?
        if (req.getParameter(CookieServlet.Companion.SetCookiesParam) != null) setCookies(res)

        // Do we want to redirect elsewhere?
        val loc = req.getParameter(CookieServlet.Companion.LocationParam)
        if (loc != null) {
            res.sendRedirect(loc)
            return
        }

        // print out the cookies that were received
        res.contentType = BaseServlet.TextHtml
        res.status = 200
        val w = res.writer
        w.println("<table>")
        val cookies = req.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                EchoServlet.Companion.write(w, cookie.name, cookie.value)
            }
        }
        w.println("</table>")
    }

    private fun setCookies(res: HttpServletResponse) {
        val one = Cookie("One", "Root")
        one.path = "/"
        res.addCookie(one)
        val two = Cookie("One", "CookieServlet")
        two.path = "/CookieServlet"
        two.isHttpOnly = true
        two.comment = "Quite nice"
        res.addCookie(two)
        val three = Cookie("One", "EchoServlet")
        three.path = "/EchoServlet"
        res.addCookie(three)
    }

    companion object {
        val Url = TestServer.map(CookieServlet::class.java)
        const val SetCookiesParam = "setCookies"
        const val LocationParam = "loc"
    }
}
