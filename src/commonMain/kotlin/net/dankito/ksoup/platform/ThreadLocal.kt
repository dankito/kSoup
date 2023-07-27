package net.dankito.ksoup.platform

expect class ThreadLocal<T : Any>(initializer: (() -> T)? = null) {

    fun get(): T?

    fun set(newValue: T?)

}