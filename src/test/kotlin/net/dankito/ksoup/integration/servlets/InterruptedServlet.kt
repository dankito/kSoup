package net.dankito.ksoup.integration.servlets

import net.dankito.ksoup.integration.TestServer
import net.dankito.ksoup.parser.CharacterReaderTest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class InterruptedServlet : BaseServlet() {

    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        var magnitude = req.getParameter(Magnitude)
        magnitude = magnitude ?: ""
        res.contentType = BaseServlet.TextHtml
        res.status = HttpServletResponse.SC_OK
        val sb = StringBuilder()
        sb.append("<title>Something</title>")
        while (sb.length <= CharacterReaderTest.maxBufferLen) {
            sb.append("A suitable amount of data. \n")
        }
        sb.append("<p>Finale.</p>")
        val data = sb.toString()
        val contentLength = if (magnitude == Larger) data.length * 2 else data.length / 2
        res.setContentLength(contentLength)
        res.writer.write(data)
    }

    companion object {
        val Url = TestServer.map(InterruptedServlet::class.java)
        const val Magnitude = "magnitude"
        const val Larger = "larger"
    }
}
