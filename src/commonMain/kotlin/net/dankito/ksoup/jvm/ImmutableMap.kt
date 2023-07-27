package net.dankito.ksoup.jvm

class ImmutableMap<K, V>(private val wrapped: Map<K, V>) : Map<K, V> {

    override val size = wrapped.size

    override val keys = wrapped.keys

    override val values = wrapped.values

    override val entries = wrapped.entries


    override fun isEmpty() = wrapped.isEmpty()

    override fun containsKey(key: K) = wrapped.containsKey(key)

    override fun containsValue(value: V) = wrapped.containsValue(value)

    override fun get(key: K) = wrapped.get(key)

}