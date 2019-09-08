import java.util.Arrays;
import java.util.Random;

public class Main {

    private static int threadCount = 0;

    public static void main(String[] args) {
        int[] arr = new int[10000000];

        Random random = new Random();

        for (int i = 0; i < 10000000; i++) {
            int next = random.nextInt();
            arr[i] = next;
        }

        long start = System.currentTimeMillis();
        mergeSort(arr);

        long time = System.currentTimeMillis() - start;

        System.out.println(String.valueOf(time));
        // int size = arr.length;
        // for (int i = 0; i < size; i++) {
        //     System.out.print(String.valueOf(arr[i] + " "));
        // }
    }

    public static void mergeSort(int[] arr) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        splitMerge(arr, 0, arr.length, copy);
    }

    static void splitMerge(int[] arr, int begin, int end, int[] copy) {
        if (end - begin < 2)
            return;

        int middle = begin + (end - begin) / 2;

        if (threadCount < 4) {

            Thread first = new Thread(() -> {
                splitMerge(copy, begin, middle, arr);
            });
            Thread second = new Thread(() -> {
                splitMerge(copy, middle, end, arr);
            });

            first.start();
            second.start();

            threadCount += 2;

            try {
                first.join();
                second.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
 
            merge(arr, begin, middle, end, copy);
        } else {
            splitMerge(copy, begin, middle, arr);
            splitMerge(copy, middle, end, arr);
            merge(arr, begin, middle, end, copy);
        }
    }

    static void merge(int[] arr, int begin, int middle, int end, int[] copy) {
        int firstSorted = begin;
        int secondSorted = middle;

        for (int i = begin; i < end; i++) {
            if (firstSorted == middle || secondSorted != end && copy[firstSorted] > copy[secondSorted]) {
                arr[i] = copy[secondSorted];
                secondSorted++;
            } else {
                arr[i] = copy[firstSorted];
                firstSorted++;
            }
        }
    }
}