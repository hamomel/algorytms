package storage

class SpacedArray<T>(private val vector: Int = 10) : IArray<T> {
    private val arr = VectorArray<VectorArray<T>>(vector)
    private var size = 0

    init {
        resize()
    }

    override fun add(item: T) {
        if (size == 0) {
            arr.get(0)!!.add(item)
            size++
            return
        }

        val rowNum = calculatePosition(size - 1).first
        val row = arr.get(rowNum)!!
        if (row.size() == vector) {
            if (rowNum == arr.size() - 1) resize()
            arr.get(rowNum + 1)!!.add(item)
        } else {
            row.add(item)
        }

        size++
    }

    private fun calculatePosition(index: Int): Pair<Int, Int> {
        var sizeSum = 0
        var row = 0
        while (true) {
            sizeSum += arr.get(row)!!.size()
            if (sizeSum > index) break
            row++
        }

        val pos = arr.get(row)!!.size() - (sizeSum - index)
        return row to pos
    }

    private fun resize() {
        for (i in 0 until vector) {
            arr.add(VectorArray(vector))
        }
    }

    override fun add(item: T, index: Int) {
        if (index > size) throw ArrayIndexOutOfBoundsException("index = $index")
        val (rowNumber, pos) = calculatePosition(index)
        val row = arr.get(rowNumber)!!
        if (row.size() == vector) {
            val newRow = VectorArray<T>(vector)
            for (i in pos until vector) {
                newRow.add(row.get(i)!!)
            }
            for (i in (vector - 1) downTo pos) {
                row.remove(i)
            }
            arr.add(newRow, rowNumber + 1)
        }
        row.add(item, pos)
        size++
    }

    override fun size(): Int = size

    override fun set(item: T, index: Int): T {
        val (rowNum, pos) = calculatePosition(index)
        return arr.get(rowNum)!!.set(item, pos)
    }

    override fun get(index: Int): T? {
        val (rowNum, pos) = calculatePosition(index)
        return arr.get(rowNum)!!.get(pos)
    }

    override fun remove(index: Int): T {
        val (rowNum, pos) = calculatePosition(index)
        size--
        return arr.get(rowNum)!!.remove(pos)
    }

    override fun toString(): String {
        return buildString {
            val rowNum = calculatePosition(size -1).first
            for (i in 0..rowNum) {
                append(arr.get(i))
                if (i < rowNum) append("\n")
            }
        }
    }
}