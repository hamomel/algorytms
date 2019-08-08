package storage

import java.lang.IndexOutOfBoundsException

class VectorArray<T>(private val vector: Int) : IArray<T> {

    private var arr = arrayOf<Any?>()
    private var size = 0

    override fun add(item: T) {
        if (size == arr.size) resize()
        arr[size] = item
        size++
    }

    private fun resize() {
        val newArr: Array<Any?> = Array(arr.size + vector) { Unit }

        arr.forEachIndexed { i, element ->
            newArr[i] =  element
        }

        arr = newArr
    }

    override fun size(): Int = size

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): T? {
        if (index >= arr.size) throw IndexOutOfBoundsException("index = $index")
        return arr[index] as? T
    }

    override fun add(item: T, index: Int) {
        if (index > size) throw IndexOutOfBoundsException("index = $index")
        if (size == arr.size) resize()

        for (i in (size - 1) downTo index) {
            arr[i + 1] = arr[i]
        }

        arr[index] = item

        size++
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(index: Int): T {
        if (index > arr.size - 1) throw IndexOutOfBoundsException("index = $index")

        val item = arr[index]
        val newArr: Array<Any?> = Array(arr.size - 1) { Unit }

        for (i in 0 until arr.size) {
            if (i == index) continue

            if (i < index) {
                newArr[i] = arr[i]
            } else {
                newArr[i - 1] = arr[i]
            }
        }

        arr = newArr

        return item as T
    }
}