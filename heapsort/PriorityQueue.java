import java.util.Arrays;

public class PriorityQueue<T> {

    private class Node {
        private int priority;
        private T value;

        Node(T value, int priority) {
            this.priority = priority;
            this.value = value;
        }
    }

    private Object[] arr = new Object[10];
    private int size;

    void offer(T element, int priority) {
        if (size == arr.length)
            resize();
        arr[size] = new Node(element, priority);
        size++;
        bubble(size - 1);
    }

    T poll() {
        if (size == 0)
            throw new IllegalStateException("Heap is empty");
        Node result = (Node) arr[0];
        arr[0] = arr[size - 1];
        size--;
        drain(0);
        return result.value;
    }

    int size() {
        return size;
    }

    boolean remove(T element) {
        int position = -1;
        for (int i = 0; i < size; i++) {
            if (((Node) arr[i]).value.equals(element)) {
                position = i;
                break;
            }
        }

        if (position < 0)
            return false;

        swap(size - 1, position);
        size--;
        drain(position);
        return true;
    }

    private void resize() {
        arr = Arrays.copyOf(arr, arr.length + 10);
    }

    private int left(int position) {
        return position * 2 + 1;
    }

    private int right(int position) {
        return left(position) + 1;
    }

    private int parent(int position) {
        return (position - 1) / 2;
    }

    private void bubble(int position) {
        int current = position;

        while (current > 0) {
            int parent = parent(current);
            if (((Node) arr[parent]).priority < ((Node) arr[current]).priority) {
                swap(parent, current);
                current = parent;
            } else {
                break;
            }
        }
    }

    private void drain(int position) {
        int current = position;
        int right = right(current);
        int left = left(current);

        while (left < size && ((Node) arr[current]).priority < ((Node) arr[left]).priority
                || right < size && ((Node) arr[current]).priority < ((Node) arr[right]).priority) {

            if (right >= size || ((Node) arr[left]).priority > ((Node) arr[right]).priority) {
                swap(current, left);
                current = left;
            } else {
                swap(current, right);
                current = right;
            }
            right = right(current);
            left = left(current);
        }
    }

    private void swap(int first, int second) {
        Object temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(String.valueOf(arr[i]) + " ");
        }

        return builder.toString();
    }

    private int getHeight() {
        return 31 - Integer.numberOfLeadingZeros(size);
    }
}