package net.dankito.ksoup.jvm

import kotlin.math.max
import kotlin.math.min

class StringReader(private val input: String) : Reader {

    private val length = input.length

    private var position = 0
    private var mark = 0
    private var isOpen = true
    
    override fun skip(n: Long): Long {
        ensureOpen()

        return if (this.position >= this.length) {
            0L
        } else {
            var count = min(this.length.toLong() - this.position, n)
            count = max((-1) * this.position.toLong(), count)
            this.position = this.position + count.toInt()
            count
        }
    }

    override fun markSupported() = true

    override fun mark(readAheadLimit: Int) {
        if (readAheadLimit < 0) {
            throw IllegalArgumentException("Read-ahead limit < 0")
        }
        
        ensureOpen()
        this.mark = position
    }

    override fun read(buffer: CharArray, offset: Int, length: Int): Int {
        ensureOpen()
        return if (offset >= 0 && offset <= buffer.size && length >= 0 && offset + length <= buffer.size && offset + length >= 0) {
            if (length == 0) {
                0
            } else if (this.position >= this.length) {
                -1
            } else {
                val n: Int = min(this.length - this.position, length)
                input.toCharArray(this.position, this.position + n).forEachIndexed { index, char ->
                    buffer[offset + index] = char
                }

                this.position += n
                n
            }
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    override fun reset() {
        ensureOpen()
        this.position = mark
    }

    override fun close() {
        this.isOpen = false
    }

    private fun ensureOpen() {
         if (!isOpen) {
           throw IOException("Stream closed");
         }
    }
}