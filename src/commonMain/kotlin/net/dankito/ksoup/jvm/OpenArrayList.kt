package net.dankito.ksoup.jvm

open class OpenArrayList<E> : MutableList<E> {

    private val impl: ArrayList<E>

    constructor() {
        impl = ArrayList()
    }

    constructor(initialCapacity: Int) {
        impl = ArrayList(initialCapacity)
    }

    constructor(elements: Collection<E>) {
        impl = ArrayList(elements)
    }


    override val size: Int
        get() = impl.size

    override fun isEmpty() = impl.isEmpty()

    override fun iterator() = impl.iterator()

    override fun listIterator() = impl.listIterator()

    override fun listIterator(index: Int) = impl.listIterator(index)

    override fun indexOf(element: E) = impl.indexOf(element)

    override fun lastIndexOf(element: E) = impl.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int) = impl.subList(fromIndex, toIndex)

    override fun contains(element: E) = impl.contains(element)

    override fun containsAll(elements: Collection<E>) = impl.containsAll(elements)

    override fun get(index: Int) = impl.get(index)

    override fun add(e: E) = impl.add(e)

    override fun add(index: Int, element: E) = impl.add(index, element)

    override fun set(index: Int, element: E) = impl.set(index, element)

    override fun remove(elements: E) = impl.remove(elements)

    override fun removeAt(index: Int) = impl.removeAt(index)

    override fun addAll(elements: Collection<E>) = impl.addAll(elements)

    override fun addAll(index: Int, elements: Collection<E>) = impl.addAll(index, elements)

    override fun removeAll(elements: Collection<E>) = impl.removeAll(elements)

    override fun retainAll(elements: Collection<E>) = impl.retainAll(elements)

    override fun clear() = impl.clear()

}