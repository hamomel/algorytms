package storage

import java.lang.IndexOutOfBoundsException

class SingleArray<T> : IArray<T> {
    private var arr = arrayOf<Any?>()

    override fun add(item: T) {
        resize()
        arr[arr.size - 1] = item
    }

    override fun size(): Int = arr.size

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): T? {
        if (index >= arr.size) throw IndexOutOfBoundsException("index = $index")
        return arr[index] as? T
    }

    override fun add(item: T, index: Int) {
        if (index > arr.size) throw IndexOutOfBoundsException("index = $index")
        resize()

        if (size() == 1) arr[0] = item

        for (i in (size() - 2) downTo index) {
            arr[i + 1] = arr[i]
        }

        arr[index] = item
    }

    private fun resize() {
        val newArr: Array<Any?> = Array(arr.size + 1) { Unit }

        arr.forEachIndexed { i, element ->
            newArr[i] =  element
        }

        arr = newArr
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