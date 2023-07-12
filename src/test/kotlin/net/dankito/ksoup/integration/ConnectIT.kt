package net.dankito.ksoup.integration

import net.dankito.ksoup.Jsoup
import net.dankito.ksoup.integration.servlets.FileServlet
import net.dankito.ksoup.integration.servlets.SlowRider
import net.dankito.ksoup.internal.ConstrainableInputStream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets

/**
 * Failsafe integration tests for Connect methods. These take a bit longer to run, so included as Integ, not Unit, tests.
 */
class ConnectIT {
    // Slow Rider tests.
    @Test
    @Throws(InterruptedException::class)
    fun canInterruptBodyStringRead() {
        // todo - implement in interruptable channels, so it's immediate
        val body = arrayOfNulls<String>(1)
        val runner = Thread {
            try {
                val res = Jsoup.connect(SlowRider.Url)
                    .timeout(15 * 1000)
                    .execute()
                body[0] = res.body()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        runner.start()
        Thread.sleep((1000 * 3).toLong())
        runner.interrupt()
        Assertions.assertTrue(runner.isInterrupted)
        runner.join()
        Assertions.assertTrue(body[0]!!.length > 0)
        Assertions.assertTrue(body[0]!!.contains("<p>Are you still there?"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun canInterruptDocumentRead() {
        // todo - implement in interruptable channels, so it's immediate
        val body = arrayOfNulls<String>(1)
        val runner = Thread {
            try {
                val res = Jsoup.connect(SlowRider.Url)
                    .timeout(15 * 1000)
                    .execute()
                body[0] = res.parse().text()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        runner.start()
        Thread.sleep((1000 * 3).toLong())
        runner.interrupt()
        Assertions.assertTrue(runner.isInterrupted)
        runner.join()
        Assertions.assertEquals(0, body[0]!!.length) // doesn't ready a failed doc
    }

    @Test
    @Throws(IOException::class)
    fun totalTimeout() {
        val timeout = 3 * 1000
        val start = System.currentTimeMillis()
        var threw = false
        try {
            Jsoup.connect(SlowRider.Url).timeout(timeout).get()
        } catch (e: SocketTimeoutException) {
            val end = System.currentTimeMillis()
            val took = end - start
            Assertions.assertTrue(took > timeout, "Time taken was $took")
            Assertions.assertTrue(took < timeout * 1.8, "Time taken was $took")
            threw = true
        }
        Assertions.assertTrue(threw)
    }

    @Test
    @Throws(IOException::class)
    fun slowReadOk() {
        // make sure that a slow read that is under the request timeout is still OK
        val doc = Jsoup.connect(SlowRider.Url)
            .data(SlowRider.MaxTimeParam, "2000") // the request completes in 2 seconds
            .get()
        val h1 = doc.selectFirst("h1")
        Assertions.assertEquals("outatime", h1!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun infiniteReadSupported() {
        val doc = Jsoup.connect(SlowRider.Companion.Url)
            .timeout(0)
            .data(SlowRider.Companion.MaxTimeParam, "2000")
            .get()
        val h1 = doc.selectFirst("h1")
        Assertions.assertEquals("outatime", h1!!.text())
    }

    @Test
    @Throws(IOException::class)
    fun remainingAfterFirstRead() {
        val bufferSize = 5 * 1024
        val capSize = 100 * 1024
        val url: String = FileServlet.urlTo("/htmltests/large.html") // 280 K
        var stream: ConstrainableInputStream
        Jsoup.connect(url).maxBodySize(capSize)
            .execute().bodyStream().use { inputStream ->
                Assertions.assertTrue(inputStream is ConstrainableInputStream)
                stream = inputStream as ConstrainableInputStream

                // simulates parse which does a limited read first
                stream.mark(bufferSize)
                val firstBytes = stream.readToByteBuffer(bufferSize)
                val array = firstBytes.array()
                val firstText = String(array, StandardCharsets.UTF_8)
                Assertions.assertTrue(firstText.startsWith("<html><head><title>Large"))
                Assertions.assertEquals(bufferSize, array.size)
                val fullyRead = stream.read() == -1
                Assertions.assertFalse(fullyRead)

                // reset and read again
                stream.reset()
                val fullRead = stream.readToByteBuffer(0)
                val fullArray = fullRead.array()
                Assertions.assertEquals(capSize, fullArray.size)
                val fullText = String(fullArray, StandardCharsets.UTF_8)
                Assertions.assertTrue(fullText.startsWith(firstText))
            }
    }

    @Test
    @Throws(IOException::class)
    fun noLimitAfterFirstRead() {
        val bufferSize = 5 * 1024
        val url: String = FileServlet.urlTo("/htmltests/large.html") // 280 K
        var stream: ConstrainableInputStream
        Jsoup.connect(url).execute().bodyStream().use { inputStream ->
            Assertions.assertTrue(inputStream is ConstrainableInputStream)
            stream = inputStream as ConstrainableInputStream

            // simulates parse which does a limited read first
            stream.mark(bufferSize)
            val firstBytes = stream.readToByteBuffer(bufferSize)
            val array = firstBytes.array()
            val firstText = String(array, StandardCharsets.UTF_8)
            Assertions.assertTrue(firstText.startsWith("<html><head><title>Large"))
            Assertions.assertEquals(bufferSize, array.size)

            // reset and read fully
            stream.reset()
            val fullRead = stream.readToByteBuffer(0)
            val fullArray = fullRead.array()
            Assertions.assertEquals(280735, fullArray.size)
            val fullText = String(fullArray, StandardCharsets.UTF_8)
            Assertions.assertTrue(fullText.startsWith(firstText))
        }
    }
}
