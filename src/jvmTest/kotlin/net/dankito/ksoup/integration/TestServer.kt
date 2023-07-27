//package net.dankito.ksoup.integration
//
//import net.dankito.ksoup.integration.servlets.BaseServlet
//import org.eclipse.jetty.server.Server
//import org.eclipse.jetty.server.ServerConnector
//import org.eclipse.jetty.servlet.ServletHandler
//import java.net.InetSocketAddress
//
//object TestServer {
//    private val jetty = Server(InetSocketAddress("localhost", 0))
//    private val handler = ServletHandler()
//
//    init {
//        jetty.handler = handler
//    }
//
//    fun start() {
//        synchronized(jetty) {
//            try {
//                jetty.start() // jetty will safely no-op a start on an already running instance
//            } catch (e: Exception) {
//                throw IllegalStateException(e)
//            }
//        }
//    }
//
//    fun map(servletClass: Class<out BaseServlet>): String {
//        synchronized(jetty) {
//            if (!jetty.isStarted) start() // if running out of the test cases
//            val path = "/" + servletClass.simpleName
//            handler.addServletWithMapping(servletClass, "$path/*")
//            val port = (jetty.connectors[0] as ServerConnector).localPort
//            return "http://localhost:$port$path"
//        }
//    }
//}
