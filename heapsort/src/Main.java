import java.util.Random;

class Main {
    public static void main(String[] args) {
    //    Heap heap = new Heap();
       int[] arr = new int[20];
       Random random = new Random();

       for(int i = 0; i < 20; i++) {
           arr[i] = random.nextInt(100);
       }

       Heap heap = new Heap(arr);
       System.out.println(heap);

       int[] sorted = heap.sorted();

       for(int i = 0; i < sorted.length; i++) {
           System.out.print(String.valueOf(sorted[i]) + " ");
       }
    }
}