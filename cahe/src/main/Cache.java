package main;

public class Cache<K, V> {
    private int timeout;
    private int maxSize;
    private int size = 0;
    private Item head;
    private Item tail;

    public Cache(int size, int timeout) {
        this.maxSize = size;
        this.timeout = timeout;
    }

    public V get(K key) {
        Item current = head;
        while (current != null) {
            // if desired item is found, move it to the start of the list and update it's access time,
            // so that sort the list by access time and make most often used items accessible faster
            if (current.key.equals(key)) {
                // we only need to move the item if it is not the head
                if (!current.equals(head)) {
                    current.previous.next = current.next;
                    if (current.equals(tail)) {
                        tail = current.previous;
                    } else {
                        current.next.previous = current.previous;
                    }
                    current.next = head;
                    head.previous = current;
                    current.previous = null;
                    head = current;
                }
                current.lastAccess = System.currentTimeMillis();
                return current.value;
            }

            current = current.next;
        }

        return null;
    }

    public void set(K key, V value) {
        if (size == maxSize && !dropOutdated()) {
            dropTail();
        }

        Item newItem = new Item(key, value);
        // if cache is empty set new item as a tail either, otherwise link current head to the new item
        if (head == null) {
            tail = newItem;
        } else {
            head.previous = newItem;
        }
        newItem.next = head;
        head = newItem;
        size++;
        // drop outdated items to keep cache clean. Might be it is not needed (depends on purpose of cache)
        dropOutdated();
    }

    private boolean dropOutdated() {
        boolean isFreed = false;

        // the items are sorted by lastAccess by design, so we can check them in a sequence from the tail
        while (tail != null && System.currentTimeMillis() - tail.lastAccess > timeout) {
            dropTail();
            isFreed = true;
        }
        return isFreed;
    }

    private void dropTail() {
        tail.previous.next = null;
        tail = tail.previous;
        size--;
    }

    private class Item {
        long lastAccess;
        Item previous;
        Item next;
        private K key;
        private V value;

        Item(K key, V value) {
            this.key = key;
            this.value = value;
            lastAccess = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "[" + key + " : " + value + "]";
        }
    }
}
