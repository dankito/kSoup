package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup.newSession
import net.dankito.ksoup.UncheckedIOException
import net.dankito.ksoup.integration.servlets.FileServlet
import net.dankito.ksoup.integration.servlets.SlowRider
import net.dankito.ksoup.jvm.AtomicInt
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import net.dankito.ksoup.jvm.IOException

/** Integration tests to test longer running Connection  */
class SessionIT {

    @Test
    fun multiThread() {
        val numThreads = 20
        val numThreadLoops = 5
        val urls = arrayOf(
            FileServlet.urlTo("/htmltests/medium.html"),
            FileServlet.urlTo("/htmltests/upload-form.html"),
            FileServlet.urlTo("/htmltests/comments.html"),
            FileServlet.urlTo("/htmltests/large.html")
        )
        val titles = arrayOf(
            "Medium HTML",
            "Upload Form Test",
            "A Certain Kind of Test",
            "Large HTML"
        )
        val catcher = ThreadCatcher()
        val session = newSession()
        val threads = arrayOfNulls<Thread>(numThreads)
        for (threadNum in 0 until numThreads) {
            val thread = Thread {
                for (loop in 0 until numThreadLoops) {
                    for (i in urls.indices) {
                        try {
                            val doc = session.newRequest().url(urls[i]).get()
                            Assertions.assertEquals(titles[i], doc.title())
                        } catch (e: IOException) {
                            throw UncheckedIOException(e)
                        }
                    }
                }
            }
            thread.name = "Runner-$threadNum"
            thread.start()
            thread.uncaughtExceptionHandler = catcher
            threads[threadNum] = thread
        }

        // now join them all
        for (thread in threads) {
            thread!!.join()
        }
        Assertions.assertEquals(0, catcher.exceptionCount.get())
    }

    // test that we throw a nice clear exception if you try to multi-thread by forget .newRequest()
    @Test
    fun multiThreadWithoutNewRequestBlowsUp() {
        val numThreads = 20
        val url: String =
            SlowRider.Companion.Url + "?" + SlowRider.Companion.MaxTimeParam + "=10000" // this makes sure that the first req is still executing whilst the others run
        val title = "Slow Rider"
        val catcher = ThreadCatcher()
        val session = newSession()
        val threads = arrayOfNulls<Thread>(numThreads)
        for (threadNum in 0 until numThreads) {
            val thread = Thread {
                try {
                    val doc = session.url(url).get()
                    Assertions.assertEquals(title, doc.title())
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }
            thread.name = "Runner-$threadNum"
            thread.start()
            thread.uncaughtExceptionHandler = catcher
            threads[threadNum] = thread
        }

        // now join them all
        for (thread in threads) {
            thread!!.join()
        }

        // only one should have passed, rest should have blown up (assuming the started whilst other was running)
        Assertions.assertEquals(numThreads - 1, catcher.multiThreadExceptions.get())
        Assertions.assertEquals(numThreads - 1, catcher.exceptionCount.get())
    }

    internal class ThreadCatcher : Thread.UncaughtExceptionHandler {
        var exceptionCount = AtomicInt()
        var multiThreadExceptions = AtomicInt()
        override fun uncaughtException(t: Thread, e: Throwable) {
            if (e is IllegalArgumentException && e.message!!.contains("Multiple threads")) multiThreadExceptions.incrementAndGet() else e.printStackTrace()
            exceptionCount.incrementAndGet()
        }
    }

    companion object {
        @BeforeAll
        fun setUp() {
            TestServer.start()
        }
    }
}
