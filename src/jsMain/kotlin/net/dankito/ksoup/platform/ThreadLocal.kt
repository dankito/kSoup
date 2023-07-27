package net.dankito.ksoup.platform

actual class ThreadLocal<T : Any> actual constructor(private val initializer: (() -> T)?) {

    private var value: T? = null

    private var isInitialized = false

    actual fun get(): T? {
        if (this.isInitialized == false) {
            this.value = initializer?.invoke()
        }

        return this.value
    }

    actual fun set(newValue: T?) {
        this.value = newValue
    }

}