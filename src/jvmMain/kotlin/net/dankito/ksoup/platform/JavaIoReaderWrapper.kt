package net.dankito.ksoup.platform

import net.dankito.ksoup.jvm.Reader

class JavaIoReaderWrapper(private val delegate: java.io.Reader) : Reader {

    override fun skip(n: Long) = delegate.skip(n)

    override fun markSupported() = delegate.markSupported()

    override fun mark(readAheadLimit: Int) = delegate.mark(readAheadLimit)

    override fun read(buffer: CharArray, offset: Int, length: Int) = delegate.read(buffer, offset, length)

    override fun reset() = delegate.reset()

    override fun close() = delegate.close()

}