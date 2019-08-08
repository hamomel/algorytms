package storage

interface IArray<T> {
    fun add(item: T)
    fun size(): Int
    fun get(index: Int): T?
    fun add(item: T, index: Int)
    fun remove(index: Int): T
}