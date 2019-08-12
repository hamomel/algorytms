package storage

import java.lang.IndexOutOfBoundsException
import java.util.Arrays

class FactorArray<T>(private val factor: Int = 50) : IArray<T> {

    private var arr = Array<Any?>(10) { Unit }
    private var size = 0

    override fun add(item: T) {
        if (size == arr.size) resize()
        arr[size] = item
        size++
    }

    private fun resize() {
        val newSize = arr.size * (1 + factor.toFloat() / 100)
        arr = Arrays.copyOf(arr, newSize.toInt())
    }

    override fun size(): Int = size

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): T? {
        if (index >= arr.size || index < 0) throw IndexOutOfBoundsException("index = $index")
        return arr[index] as? T
    }

    override fun add(item: T, index: Int) {
        if (index > size || index < 0) throw IndexOutOfBoundsException("index = $index")
        if (size == arr.size) resize()

        for (i in (size - 1) downTo index) {
            arr[i + 1] = arr[i]
        }

        arr[index] = item

        size++
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(item: T, index: Int): T {
        val previous = arr[index]
        arr[index] = item
        return previous as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(index: Int): T {
        if (index >= size || index < 0) throw IndexOutOfBoundsException("index = $index")

        val item = arr[index]
        for (i in index until size - 2) {
            arr[i] = arr[i + 1]
        }

        size--

        return item as T
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder("[")
        for (i in 0 until size()) {
            stringBuilder.append(" ${get(i)}")
            if (i != 0 && i % 100 == 0) stringBuilder.append("\n")
        }
        stringBuilder.append(" ]")

        return stringBuilder.toString()
    }
}