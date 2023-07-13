package net.dankito.ksoup.jvm

import java.lang.ref.WeakReference

class WeakReference<T>(value: T? = null) {

    private val impl = WeakReference<T>(value)

    fun get() = impl.get()

}