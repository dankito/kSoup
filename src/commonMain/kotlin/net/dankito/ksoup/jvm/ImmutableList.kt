package net.dankito.ksoup.jvm

class ImmutableList<T>(private val wrapped: List<T>) : List<T> {

    override val size = wrapped.size

    override fun isEmpty() = wrapped.isEmpty()

    override fun get(index: Int) = wrapped.get(index)

    override fun indexOf(element: T) = wrapped.indexOf(element)

    override fun lastIndexOf(element: T) = wrapped.lastIndexOf(element)

    override fun contains(element: T) = wrapped.contains(element)

    override fun containsAll(elements: Collection<T>) = wrapped.containsAll(elements)

    override fun iterator() = wrapped.iterator()

    override fun listIterator() = wrapped.listIterator()

    override fun listIterator(index: Int) = wrapped.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = wrapped.subList(fromIndex, toIndex)

}