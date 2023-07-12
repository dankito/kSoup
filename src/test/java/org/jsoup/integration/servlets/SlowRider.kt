package org.jsoup.integration.servlets

import org.jsoup.integration.TestServer
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Slowly, interminably writes output. For the purposes of testing timeouts and interrupts.
 */
class SlowRider : BaseServlet() {
    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        SlowRider.Companion.pause(1000)
        res.contentType = BaseServlet.Companion.TextHtml
        res.status = HttpServletResponse.SC_OK
        val w = res.writer
        var maxTime = -1
        val maxTimeP = req.getParameter(SlowRider.Companion.MaxTimeParam)
        if (maxTimeP != null) {
            maxTime = maxTimeP.toInt()
        }
        val startTime = System.currentTimeMillis()
        w.println("<title>Slow Rider</title>")
        while (true) {
            w.println("<p>Are you still there?")
            val err = w.checkError() // flush, and check still ok
            if (err) {
                log("Remote connection lost")
                break
            }
            if (SlowRider.Companion.pause(SlowRider.Companion.SleepTime)) break
            if (maxTime > 0 && System.currentTimeMillis() > startTime + maxTime) {
                w.println("<h1>outatime</h1>")
                break
            }
        }
    }

    companion object {
        val Url = TestServer.map(SlowRider::class.java)
        private const val SleepTime = 2000
        const val MaxTimeParam = "maxTime"
        private fun pause(sleepTime: Int): Boolean {
            try {
                Thread.sleep(sleepTime.toLong())
            } catch (e: InterruptedException) {
                return true
            }
            return false
        }

        // allow the servlet to run as a main program, for local test
        @JvmStatic
        fun main(args: Array<String>) {
            TestServer.start()
            println(SlowRider.Companion.Url)
        }
    }
}
