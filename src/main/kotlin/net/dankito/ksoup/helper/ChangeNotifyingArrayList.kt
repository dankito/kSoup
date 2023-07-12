package net.dankito.ksoup.helper

/**
 * Implementation of ArrayList that watches out for changes to the contents.
 */
abstract class ChangeNotifyingArrayList<E>(initialCapacity: Int) : ArrayList<E>(initialCapacity) {

    abstract fun onContentsChanged()

    override fun set(index: Int, element: E): E {
        onContentsChanged()
        return super.set(index, element)
    }

    override fun add(e: E): Boolean {
        onContentsChanged()
        return super.add(e)
    }

    override fun add(index: Int, element: E) {
        onContentsChanged()
        super.add(index, element)
    }

    override fun removeAt(index: Int): E {
        onContentsChanged()
        return super.removeAt(index)
    }

    override fun remove(elements: E): Boolean {
        onContentsChanged()
        return super.remove(elements)
    }

    override fun clear() {
        onContentsChanged()
        super.clear()
    }

    override fun addAll(elements: Collection<E>): Boolean {
        onContentsChanged()
        return super.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        onContentsChanged()
        return super.addAll(index, elements)
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        onContentsChanged()
        super.removeRange(fromIndex, toIndex)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        onContentsChanged()
        return super.removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        onContentsChanged()
        return super.retainAll(elements)
    }
}
