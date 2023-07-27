//package net.dankito.ksoup.integration.servlets
//
//import org.eclipse.jetty.server.Request
//import net.dankito.ksoup.helper.DataUtil.readToByteBuffer
//import net.dankito.ksoup.integration.TestServer
//import net.dankito.ksoup.internal.StringUtil.isBlank
//import net.dankito.ksoup.internal.StringUtil.join
//import net.dankito.ksoup.platform.Charsets
//import net.dankito.ksoup.platform.String
//import net.dankito.ksoup.nodes.Entities.escape
//import java.io.PrintWriter
//import javax.servlet.MultipartConfigElement
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//class EchoServlet : BaseServlet() {
//
//    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
//        doIt(req, res)
//    }
//
//    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
//        doIt(req, res)
//    }
//
//    override fun doPut(req: HttpServletRequest, res: HttpServletResponse) {
//        doIt(req, res)
//    }
//
//    private fun doIt(req: HttpServletRequest, res: HttpServletResponse) {
//        var intCode: Int = DefaultCode
//        val code = req.getHeader(CodeParam)
//        if (code != null) intCode = code.toInt()
//        val isMulti: Boolean = maybeEnableMultipart(req)
//        res.contentType = BaseServlet.TextHtml
//        res.status = intCode
//        // no-cache headers for test
//        res.addHeader("Cache-Control", "no-cache")
//        res.addHeader("Cache-Control", "no-store")
//        val w = res.writer
//        w.write(
//            "<title>Webserver Environment Variables</title>\n" +
//                    "    <style type=\"text/css\">\n" +
//                    "      body, td, th {font: 10pt Verdana, Arial, sans-serif; text-align: left}\n" +
//                    "      th {font-weight: bold}        \n" +
//                    "    </style>\n" +
//                    "    <body>\n" +
//                    "    <table border=\"0\">"
//        )
//
//        // some get items
//        EchoServlet.write(w, "Method", req.method)
//        EchoServlet.write(w, "Request URI", req.requestURI)
//        EchoServlet.write(w, "Path Info", req.pathInfo)
//        EchoServlet.write(w, "Query String", req.queryString)
//
//        // request headers (why is it an enumeration?)
//        val headerNames = req.headerNames
//        while (headerNames.hasMoreElements()) {
//            val header = headerNames.nextElement()
//            val headers = req.getHeaders(header)
//            while (headers.hasMoreElements()) {
//                EchoServlet.write(w, header, headers.nextElement())
//            }
//        }
//
//        // cookies
//        val cookies = req.cookies
//        if (cookies != null) {
//            for (cookie in cookies) {
//                EchoServlet.write(w, "Cookie: " + cookie.name, cookie.value)
//            }
//        }
//
//        // the request params
//        val parameterNames = req.parameterNames
//        while (parameterNames.hasMoreElements()) {
//            val name = parameterNames.nextElement()
//            val values = req.getParameterValues(name)
//            EchoServlet.write(w, name, join(values, ", "))
//        }
//
//        // post body
//        val byteBuffer = readToByteBuffer(req.inputStream, 0)
//        val postData = String(byteBuffer!!.array(), Charsets.UTF_8)
//        if (!isBlank(postData)) {
//            EchoServlet.write(w, "Post Data", postData)
//        }
//
//        // file uploads
//        if (isMulti) {
//            val parts = req.parts
//            EchoServlet.write(w, "Parts", parts.size.toString())
//            for (part in parts) {
//                val name = part.name
//                EchoServlet.write(w, "Part $name ContentType", part.contentType)
//                EchoServlet.write(w, "Part $name Name", name)
//                EchoServlet.write(w, "Part $name Filename", part.submittedFileName)
//                EchoServlet.write(w, "Part $name Size", part.size.toString())
//                part.delete()
//            }
//        }
//        w.println("</table>")
//    }
//
//    companion object {
//        const val CodeParam = "code"
//        val Url = TestServer.map(EchoServlet::class.java)
//        private const val DefaultCode = HttpServletResponse.SC_OK
//        fun write(w: PrintWriter, key: String?, `val`: String?) {
//            w.println("<tr><th>" + escape(key) + "</th><td>" + escape(`val`) + "</td></tr>")
//        }
//
//        // allow the servlet to run as a main program, for local test
//        @JvmStatic
//        fun main(args: Array<String>) {
//            TestServer.start()
//            println(EchoServlet.Companion.Url)
//        }
//
//        private fun maybeEnableMultipart(req: HttpServletRequest): Boolean {
//            val isMulti = (req.contentType != null
//                    && req.contentType.startsWith("multipart/form-data"))
//            if (isMulti) {
//                req.setAttribute(
//                    Request.MULTIPART_CONFIG_ELEMENT, MultipartConfigElement(
//                        System.getProperty("java.io.tmpdir")
//                    )
//                )
//            }
//            return isMulti
//        }
//    }
//}
