package org.jsoup.integration.servlets

import org.jsoup.integration.TestServer
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Deflateservlet : BaseServlet() {
    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        res.contentType = BaseServlet.Companion.TextHtml
        res.status = HttpServletResponse.SC_OK
        res.setHeader("Content-Encoding", "deflate")
        val doc = "<p>Hello, World!<p>That should be enough, right?<p>Hello, World!<p>That should be enough, right?"
        val stream = DeflaterOutputStream(
            res.outputStream,
            Deflater(Deflater.BEST_COMPRESSION, true)
        ) // true = nowrap zlib headers
        stream.write(doc.toByteArray(StandardCharsets.UTF_8))
        stream.close()
    }

    companion object {
        val Url = TestServer.map(Deflateservlet::class.java)

        // allow the servlet to run as a main program, for local test
        @JvmStatic
        fun main(args: Array<String>) {
            TestServer.start()
            println(Deflateservlet.Companion.Url)
        }
    }
}
