package org.jsoup.integration

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletHandler
import org.jsoup.integration.servlets.BaseServlet
import java.net.InetSocketAddress

object TestServer {
    private val jetty = Server(InetSocketAddress("localhost", 0))
    private val handler = ServletHandler()

    init {
        TestServer.jetty.handler = TestServer.handler
    }

    fun start() {
        synchronized(TestServer.jetty) {
            try {
                TestServer.jetty.start() // jetty will safely no-op a start on an already running instance
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }
    }

    fun map(servletClass: Class<out BaseServlet?>): String {
        synchronized(TestServer.jetty) {
            if (!TestServer.jetty.isStarted) TestServer.start() // if running out of the test cases
            val path = "/" + servletClass.simpleName
            TestServer.handler.addServletWithMapping(servletClass, "$path/*")
            val port = (TestServer.jetty.connectors[0] as ServerConnector).localPort
            return "http://localhost:$port$path"
        }
    }
}
