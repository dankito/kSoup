package net.dankito.ksoup.platform

import net.codinux.kotlin.concurrent.ConcurrentMap
import kotlin.native.concurrent.Worker

actual class ThreadLocal<T : Any> actual constructor(private val initializer: (() -> T)?) {

    private val threadLocalValues = ConcurrentMap<String, T?>()

    actual fun get(): T? {
        var currentValue = threadLocalValues.get(Worker.current.name)

        if (currentValue == null && initializer != null) {
            currentValue = initializer.invoke()
            set(currentValue)
        }

        return currentValue
    }

    actual fun set(newValue: T?) {
        threadLocalValues.put(Worker.current.name, newValue)
    }

}