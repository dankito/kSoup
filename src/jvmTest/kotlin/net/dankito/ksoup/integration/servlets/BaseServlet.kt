//package net.dankito.ksoup.integration.servlets
//
//import javax.servlet.http.HttpServlet
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//abstract class BaseServlet : HttpServlet() {
//    // these are overridden just to get the response name to be 'res' not 'resp'
//    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
//        super.doGet(req, res)
//    }
//
//    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
//        super.doPost(req, res)
//    }
//
//    override fun doPut(req: HttpServletRequest, res: HttpServletResponse) {
//        super.doPut(req, res)
//    }
//
//    companion object {
//        const val TextHtml = "text/html; charset=UTF-8"
//    }
//}
