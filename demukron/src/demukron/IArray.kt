package demukron

interface IArray<T> {
    fun add(item: T)
    fun add(item: T, index: Int)
    fun size(): Int
    fun set(item: T, index: Int): T
    fun get(index: Int): T?
    fun remove(index: Int): T
}