package net.dankito.ksoup.jvm

class IdentityHashMap<K, V> {

    private val entries = mutableListOf<Pair<K, V>>()

    val size: Int
        get() = entries.size

    fun get(key: K): V? =
        findEntry(key)?.second

    fun put(key: K, value: V) {
        findEntry(key)?.let {
            entries.remove(it)
        }

        entries.add(Pair(key, value))
    }

    fun clear() = entries.clear()

    private fun findEntry(key: K): Pair<K, V>? =
        entries.firstOrNull { it.first === key }

}