package main;

public class Cache<K, V> {
    private int timeout;
    private int maxSize;
    private int size = 0;
    private Item head;

    public Cache(int size, int timeout) {
        this.maxSize = size;
        this.timeout = timeout;
    }

    public V get(K key) {
        Item current = head;
        while (current.next != null) {
            if (current.key.equals(key)) {
                current.lastAccess = System.currentTimeMillis();
                return current.value;
            }

            current = current.next;
        }

        return null;
    }

    public void set(K key, V value) {
        if (size == maxSize && !checkTimeout()) {
            dropLast();
        }
        Item newItem = new Item(key, value);
        newItem.next = head;
        head = newItem;
        size++;
        checkTimeout();
    }

    private boolean checkTimeout() {
        boolean isFreed = false;

        if (System.currentTimeMillis() - head.lastAccess > timeout) {
            Item next = head.next;
            head.next = null;
            head = next;
            isFreed = true;
        }
        Item previous = head;
        Item current = head.next;
        while (current != null) {
            if (System.currentTimeMillis() - current.lastAccess > timeout) {
                Item next = current.next;
                current.next = null;
                previous.next = next;
                current = next;
                isFreed = true;
            } else {
                previous = current;
                current = current.next;
            }
        }
        return isFreed;
    }

    private void dropLast() {
        Item previous = head;
        Item current = head.next;
        while (current.next != null) {
            previous = current;
            current = current.next;
        }

        previous.next = null;
    }

    private class Item {
        long lastAccess;
        Item next;
        private K key;
        private V value;

        Item(K key, V value) {
            this.key = key;
            this.value = value;
            lastAccess = System.currentTimeMillis();
        }

        public V getValue() {
            return value;
        }

        public K getKey() {
            return key;
        }
    }
}
