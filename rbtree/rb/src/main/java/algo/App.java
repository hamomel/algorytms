package algo;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        RBTree tree = new RBTree(42);
        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            tree.insert(random.nextInt());
        }

        System.out.println(tree);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            try {
                boolean exist = tree.find(Integer.parseInt(input));
                System.out.println(exist);
            } catch (Throwable e) {
                break;
            }
        }

        scanner.close();
    }
}
