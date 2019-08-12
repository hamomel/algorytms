package storage

class NativeArray<T> : IArray<T> {
    private val arr = ArrayList<T>()

    override fun add(item: T) {
        arr.add(item)
    }

    override fun add(item: T, index: Int) = arr.add(index, item)

    override fun size(): Int = arr.size

    override fun set(item: T, index: Int): T = arr.set(index, item)

    override fun get(index: Int): T? = arr[index]

    override fun remove(index: Int): T = arr.removeAt(index)
}