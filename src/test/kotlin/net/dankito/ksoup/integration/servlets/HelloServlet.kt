package net.dankito.ksoup.integration.servlets

import net.dankito.ksoup.integration.TestServer
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HelloServlet : BaseServlet() {
    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        res.contentType = BaseServlet.TextHtml
        res.status = HttpServletResponse.SC_OK
        val doc = "<p>Hello, World!"
        res.writer.write(doc)
    }

    companion object {
        val Url = TestServer.map(HelloServlet::class.java)
    }
}
