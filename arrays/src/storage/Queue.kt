package storage

class Queue<T> {

    data class Node<T>(val value: T, var next: Node<T>?)

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size: Int = 0

    fun isEmpty() = head == null

    fun getHead(): Node<T>? = head

    fun size() = size

    fun enqueue(item: T) {
        if (isEmpty()) {
            head  = Node(item, null)
            tail = head
        } else {
            val newNode = Node(item, null)
            tail?.next = newNode
            tail = newNode
        }

        size++
    }

    fun dequeue(): T? {
        val res = head?.value
        head = head?.next
        size--
        return res
    }
}