package net.dankito.ksoup.jvm

interface Reader {

    /**
     * Skips characters. This method will block until some characters are available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param n The number of characters to skip
     * @return The number of characters actually skipped
     */
    fun skip(n: Long): Long

    /**
     * Tells whether this stream supports the mark() operation. The default implementation always returns false. Subclasses should override this method.
     *
     * @return true if and only if this stream supports the mark operation.
     */
    fun markSupported(): Boolean

    /**
     * Marks the present position in the stream. Subsequent calls to reset() will attempt to reposition the stream to this point.
     * Not all character-input streams support the mark() operation.
     *
     * @param readAheadLimit Limit on the number of characters that may be read while still preserving the mark.
     * After reading this many characters, attempting to reset the stream may fail.
     */
    fun mark(readAheadLimit: Int)

    /**
     * Reads characters into a portion of an array. This method will block until some input is available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param buffer Destination buffer
     * @param offset - Offset at which to start storing characters
     * @param length - Maximum number of characters to read
     * @return The number of characters read, or -1 if the end of the stream has been reached
     */
    fun read(buffer: CharArray, offset: Int, length: Int): Int

    /**
     * Resets the stream. If the stream has been marked, then attempt to reposition it at the mark. If the stream has not been marked,
     * then attempt to reset it in some way appropriate to the particular stream, for example by repositioning it to its starting point.
     * Not all character-input streams support the reset() operation, and some support reset() without supporting mark().
     */
    fun reset()

    fun close()

}