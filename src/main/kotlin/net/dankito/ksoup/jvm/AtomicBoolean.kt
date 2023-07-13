package net.dankito.ksoup.jvm

import java.util.concurrent.atomic.AtomicBoolean

class AtomicBoolean(value: Boolean = false) {

    private val impl = AtomicBoolean(value)

    fun get() = impl.get()

    fun set(newValue: Boolean) = impl.set(newValue)

}