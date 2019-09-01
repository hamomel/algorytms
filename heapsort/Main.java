import java.util.Random;

class Main {
    public static void main(String[] args) {
    //    Heap heap = new Heap();
       int[] arr = new int[1000000];
       Random random = new Random();

       for(int i = 0; i < 1000000; i++) {
           arr[i] = random.nextInt(1000000);
       }

       long start = System.currentTimeMillis();
       heapSort(arr);

       long time = System.currentTimeMillis() - start;

       System.out.println(String.valueOf(time));
    //    for(int i = 0; i < arr.length; i++) {
    //        System.out.print(String.valueOf(arr[i]) + " ");
    //    }
    }

    static void heapSort(int[] arr) {
        for(int i = parent(arr.length - 1); i >= 0; i--) {
            drain(arr, i, arr.length);
        }    

        int sortedPos = arr.length - 1;
        while(sortedPos > 0) {
           swap(arr, sortedPos, 0);
            sortedPos--;
            drain(arr, 0, sortedPos);
        }
    }

    static int left(int position) {
        return position * 2 + 1;
    }

    static int right(int position) {
        return left(position) + 1;
    }

    static int parent(int position) {
        return (position - 1) / 2;
    }

    static void drain(int[] arr, int position, int size) {
        int current = position;
        int right = right(current);
        int left = left(current);

        while (arr[current] < arr[left] || right < size && arr[current] < arr[right]) {
            if (right >= size || arr[left] > arr[right]) {
                swap(arr, current, left);
                current = left;
            } else {
                swap(arr, current, right);
                current = right;
            }
            right = right(current);
            left = left(current);
            if (left >= size) break;
        }
    }

    static void swap(int[] arr, int first, int second) {
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }
}