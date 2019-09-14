package algo;

import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        RBTree tree = RBTree.create(42);
        Random random = new Random();
        for (int i = 22; i < 43; i++) {
            tree.insert(random.nextInt(100));
        }

        System.out.println(tree.getTreeString());

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
