package storage

class LinkedArray<T> : IArray<T> {
    private val arr = Queue<T>()

    override fun add(item: T) {
        arr.enqueue(item)
    }

    override fun add(item: T, index: Int) {
        if (index > arr.size()) throw ArrayIndexOutOfBoundsException("index = $index")
        if (index == 0) throw NotImplementedError("adding to the first position impossible")
        if (index == arr.size()) arr.enqueue(item)

        var previous = arr.getHead()
        for (i in 0 until index - 1) {
            previous = previous!!.next
        }

        val newNode = Queue.Node(item, previous?.next)
        previous?.next = newNode
    }

    override fun size(): Int = arr.size()

    override fun set(item: T, index: Int): T {
        if (index >= arr.size()) throw ArrayIndexOutOfBoundsException("index = $index")
        if (index == 0) throw NotImplementedError("setting to the first position impossible")

        var previous = arr.getHead()
        for (i in 0 until index - 1) {
            previous = previous!!.next
        }

        val res = previous?.next
        val newNode = Queue.Node(item, previous?.next?.next)
        previous?.next = newNode

        return res!!.value
    }

    override fun get(index: Int): T? {
        if (index >= arr.size()) throw ArrayIndexOutOfBoundsException("index = $index")

        var previous = arr.getHead()
        for (i in 0 until index) {
            previous = previous!!.next
        }

        return previous?.value
    }

    override fun remove(index: Int): T {
        if (index >= arr.size()) throw ArrayIndexOutOfBoundsException("index = $index")

        var previous = arr.getHead()
        for (i in 0 until index - 1) {
            previous = previous!!.next
        }

        val res = previous!!.next!!.value
        previous.next = previous.next?.next
        return res
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