package storage

class MatrixArray<T>(private val vector: Int = 10) : IArray<T> {

    private var arr = VectorArray<VectorArray<T>>(vector)

    private var size = 0

    init {
        resize()
    }

    override fun add(item: T) {
        val row = size / vector
        val position = size % vector
        if (row == arr.size()) resize()

        arr.get(row)!!.set(item, position)
        size++
    }

    private fun resize() {
        for (i in 0 until vector) {
            arr.add(VectorArray(vector))
        }
    }

    override fun size(): Int = size

    override fun get(index: Int): T? {
        if (index >= size || index < 0) throw ArrayIndexOutOfBoundsException("index = $index")
        val row = index / vector
        val position = index % vector
        return arr.get(row)!!.get(position)
    }

    override fun add(item: T, index: Int) {
        if (index > size || index < 0) throw ArrayIndexOutOfBoundsException("index = $index")
        if (index == size) {
            add(item)
            return
        }

        if (size / vector == arr.size()) resize()

        for (i in size - 1 downTo index) {
            val row = i / vector
            val position = i % vector

            val previous = arr.get(row)!!.get(position)
            if (position == vector - 1) {
                arr.get(row + 1)!!.set(previous!!, 0)
            } else {
                arr.get(row)!!.set(previous!!, position + 1)
            }
        }

        arr.get(index / vector)!!.set(item, index % vector)
        size++
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(item: T, index: Int): T {
        val row = index / vector
        val position = index % vector
        val previous = arr.get(row)!!.get(position)
        arr.get(row)!!.set(item, position)
        return previous as T
    }

    override fun remove(index: Int): T {
        if (index >= size || index < 0) throw ArrayIndexOutOfBoundsException("index = $index")
        val item = arr.get(index / vector)!!.get(index % vector)

        for (i in index until size) {
            if (i == size - 1) continue
            val row = i / vector
            val position = i % vector
            val previous = if (position == vector - 1) {
                arr.get(row + 1)!!.get(0)
            } else {
                arr.get(row)!!.get(position + 1)
            }
            arr.get(row)!!.set(previous!!, position)
        }

        size--
        return item!!
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