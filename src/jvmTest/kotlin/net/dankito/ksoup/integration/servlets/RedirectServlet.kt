//package net.dankito.ksoup.integration.servlets
//
//import net.dankito.ksoup.integration.TestServer
//import javax.servlet.http.Cookie
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//class RedirectServlet : BaseServlet() {
//    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
//        var location = req.getParameter(LocationParam)
//        if (location == null) location = ""
//        var intCode: Int = DefaultCode
//        val code = req.getParameter(CodeParam)
//        if (code != null) intCode = code.toInt()
//        if (req.getParameter(SetCookiesParam) != null) {
//            res.addCookie(Cookie("token", "asdfg123"))
//            res.addCookie(Cookie("uid", "jhy"))
//        }
//        res.setHeader("Location", location)
//        res.status = intCode
//    }
//
//    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
//        doGet(req, res)
//    }
//
//    companion object {
//        val Url = TestServer.map(RedirectServlet::class.java)
//        const val LocationParam = "loc"
//        const val CodeParam = "code"
//        const val SetCookiesParam = "setCookies"
//        private const val DefaultCode = HttpServletResponse.SC_MOVED_TEMPORARILY
//    }
//}
