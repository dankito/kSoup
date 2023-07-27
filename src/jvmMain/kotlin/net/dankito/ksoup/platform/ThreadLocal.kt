package net.dankito.ksoup.platform

import java.lang.ThreadLocal

actual class ThreadLocal<T : Any> actual constructor(initializer: (() -> T)?) {

    private val impl: ThreadLocal<T>

    init {
        impl = if (initializer != null) {
            ThreadLocal.withInitial { initializer() }
        } else {
            ThreadLocal()
        }
    }

    actual fun get(): T? = impl.get()

    actual fun set(newValue: T?) = impl.set(newValue)

}