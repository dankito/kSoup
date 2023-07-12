package org.jsoup.integration.servlets

import org.jsoup.integration.ParseTest
import org.jsoup.integration.TestServer
import java.io.*
import java.nio.file.Files
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FileServlet : BaseServlet() {
    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        var contentType = req.getParameter(FileServlet.Companion.ContentTypeParam)
        if (contentType == null) contentType = FileServlet.Companion.DefaultType
        val location = req.pathInfo
        val file: File = ParseTest.Companion.getFile(location)
        if (file.exists()) {
            res.contentType = contentType
            if (file.name.endsWith("gz")) res.addHeader("Content-Encoding", "gzip")
            res.status = HttpServletResponse.SC_OK
            val out = res.outputStream
            Files.copy(file.toPath(), out)
            out.flush()
        } else {
            res.status = HttpServletResponse.SC_NOT_FOUND
        }
    }

    @Throws(IOException::class)
    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
        doGet(req, res)
    }

    companion object {
        val Url = TestServer.map(FileServlet::class.java)
        const val ContentTypeParam = "contentType"
        const val DefaultType = "text/html"
        fun urlTo(path: String): String {
            return FileServlet.Companion.Url + path
        }
    }
}
