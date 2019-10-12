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
            String input = scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                System.out.println(tree.find(value));
            } catch (NumberFormatException e) {
                break;
            }
        }
    }
}
