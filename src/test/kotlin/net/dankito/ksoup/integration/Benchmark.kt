package net.dankito.ksoup.integration

import java.util.Date

/**
 * Does an A/B test on two methods, and prints out how long each took.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
object Benchmark {
    fun run(a: Runnable, b: Runnable, count: Int) {
        val aMillis: Long
        val bMillis: Long
        print("Running test A (x$count)")
        aMillis = time(a, count)
        print("Running test B")
        bMillis = time(b, count)
        print("\nResults:")
        print("A: %.2fs", aMillis / 1000f)
        print("B: %.2fs", bMillis / 1000f)
        print(
            "\nB ran in %.2f %% time of A\n",
            bMillis * 1f / aMillis * 1f * 100f
        )
    }

    private fun time(test: Runnable, count: Int): Long {
        val start = Date()
        for (i in 0 until count) {
            test.run()
        }
        val end = Date()
        return end.time - start.time
    }

    private fun print(msgFormat: String, vararg msgParams: Any) {
        println(String.format(msgFormat, *msgParams))
    }
}
