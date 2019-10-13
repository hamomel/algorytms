package btree;

import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        BTree tree = new BTree(4);
        for (int i = 2; i < 29; i++) {
            tree.add(i);
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("enter value to find");
            String input = scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                System.out.println("find " + tree.find(value));
            } catch (NumberFormatException e) {
                break;
            }

            System.out.println("enter value to find");
            input = scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                System.out.println("delete " + tree.delete(value));
                System.out.println("find " + tree.find(value));
            } catch (NumberFormatException e) {
                break;
            }
        }
    }
}
