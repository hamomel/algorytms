package hashtable;

import java.security.KeyStore.Entry;

public class HashTable<K, V> {

    private class Entry {
        K key;
        V value;
        Entry next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "[ " + key + ", " + value + " ]";
        }
    }

    private static final float loadFactor = 0.5f;
    private static final float resizeFactor = 1.5f;

    @SuppressWarnings("unchecked")
    private Object[] arr = new Object[10];
    private int size = 0;

    public void insert(K key, V value) {
        if (size > (float) arr.length * loadFactor) {
            resize();
        }

        put(new Entry(key, value));        
        size++;
    }

    public V find(K key) {
        int index = getIndex(key);
        Entry current = (Entry) arr[index];

        while (true) {
            if (current == null) {
                return null;
            }
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
    }

    public V remove(K key) {
        int index = getIndex(key);
        Entry current = (Entry) arr[index];

        if (current == null) return null;

        if (current.key.equals(key)) {
            V found = current.value;
            arr[index] = current.next;
            size--;
            return found;
        }

        while (true) {
            if (current == null) {
                return null;
            }
            if (current.next.key.equals(key)) {
                V found = current.next.value;
                current.next = current.next.next;
                size--;
                return found;
            }
            current = current.next;
        }
    }

    private int getIndex(K key) {
        int index =  key.hashCode() % arr.length;
        return index < 0 ? -index : index;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = (int) (arr.length * resizeFactor);
        
        Object[] oldArray = arr;
        this.arr = new Object[newCapacity];

        for(int i = 0; i < oldArray.length; i++) {
            Entry entry = (Entry) oldArray[i];
            
            while (entry != null) {
                Entry next = entry.next;
                put(entry);
                entry = next;
            }
        }
    }

    private void put(Entry entry) {
        int index = getIndex(entry.key);
        entry.next = (Entry) arr[index];
        arr[index] = entry;
    }

    public int realSize() {
        int res = 0;
        for(Object entry : arr) {
            Entry e = (Entry) entry;
            
            while (e != null) {
                res++;
                e = e.next;
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Object entry :  arr) {
            Entry current = (Entry) entry;

            while(current != null) {
                builder.append(current);
                if (current.next != null) {
                    builder.append(", ");
                }
                current = current.next;
            }

            if (entry != null)
                builder.append("\n");
        }

        return builder.toString();
    }

}