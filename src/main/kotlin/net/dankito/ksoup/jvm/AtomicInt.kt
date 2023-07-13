package net.dankito.ksoup.jvm

import java.util.concurrent.atomic.AtomicInteger

class AtomicInt(value: Int = 0) {

    private val impl = AtomicInteger(value)

    fun get() = impl.get()

    fun set(newValue: Int) = impl.set(newValue)

    fun incrementAndGet() = impl.incrementAndGet()

}