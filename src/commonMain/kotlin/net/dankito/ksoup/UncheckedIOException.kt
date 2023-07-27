package net.dankito.ksoup

import net.dankito.ksoup.jvm.IOException

class UncheckedIOException : RuntimeException {
    constructor(cause: IOException?) : super(cause)
    constructor(message: String) : super(IOException(message))

    fun ioException(): IOException {
        return cause as IOException
    }
}
