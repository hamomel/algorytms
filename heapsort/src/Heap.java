import java.util.Arrays;

public class Heap {
    private int[] arr;
    private int size;

    Heap() {
        arr = new int[10];
    }

    Heap(int[] array) {
        fromArray(array);
    }

    void add(int element) {
        if (size == arr.length)  resize();
        arr[size] = element;
        size++;
        bubble(size - 1);
    }

    int get() {
        if (size == 0)  throw new IllegalStateException("Heap is empty");
        int result = arr[0];
        arr[0] = arr[size - 1];
        size--;
        drain(0);
        return result;
    }

    int size() {
        return size;
    }

    private void fromArray(int[] array) {
        arr = array;
        size = array.length;
        for(int i = parent(size - 1); i >= 0; i--) {
            drain(i);
        }    
    }

    int[] sorted() {
        int[] res = new int[size];
        int sortedPos = size - 1;
        while(sortedPos > 0) {
            res[sortedPos] = get();
            sortedPos--;
        }
        return res;
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
            if (arr[parent] < arr[current]) {
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

        while (arr[current] < arr[left] || right < size && arr[current] < arr[right]) {
            if (right >= size || arr[left] > arr[right]) {
                swap(current, left);
                current = left;
            } else {
                swap(current, right);
                current = right;
            }
            right = right(current);
            left = left(current);
            if (left >= size) break;
        }
    }

    private void swap(int first, int second) {
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++) {
            builder.append(String.valueOf(arr[i]) + " ");
        }
        
        return builder.toString();
    }

    private int getHeight() {
        return 31 - Integer.numberOfLeadingZeros(size);
    }
}