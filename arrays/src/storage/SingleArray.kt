package storage

import java.lang.IndexOutOfBoundsException
import java.util.Arrays

class SingleArray<T> : IArray<T> {
    private var arr = arrayOf<Any?>()

    override fun add(item: T) {
        resize()
        arr[arr.size - 1] = item
    }

    override fun size(): Int = arr.size

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): T? {
        if (index >= arr.size || index < 0) throw IndexOutOfBoundsException("index = $index")
        return arr[index] as? T
    }

    override fun add(item: T, index: Int) {
        if (index > arr.size || index < 0) throw IndexOutOfBoundsException("index = $index")
        resize()

        for (i in (size() - 2) downTo index) {
            arr[i + 1] = arr[i]
        }

        arr[index] = item
    }

    fun singleCopyAdd(item: T, index: Int) {
        if (index > arr.size || index < 0) throw IndexOutOfBoundsException("index = $index")

        val newArr = arrayOfNulls<Any>(arr.size + 1)
        System.arraycopy(arr, 0, newArr, 0, index)
        System.arraycopy(arr, index, newArr, index + 1, arr.size - index)
        newArr[index] = item

        arr = newArr
    }

    private fun resize() {
        arr = Arrays.copyOf(arr, arr.size + 1)
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(item: T, index: Int): T {
        val previous = arr[index]
        arr[index] = item
        return previous as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(index: Int): T {
        if (index >= arr.size || index < 0) throw IndexOutOfBoundsException("index = $index")

        val item = arr[index]

        for (i in index until arr.size - 2) {
            arr[i] = arr[i + 1]
        }

        arr = Arrays.copyOf(arr, arr.size - 1)
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