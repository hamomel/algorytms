package hashtable;

import java.util.Random;
import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        HashTable<String, String> table = new HashTable<String, String>();

        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            table.insert("" + i, "" + i);
        }

        System.out.println(table);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("e"))
                break;
            System.out.println(table.find(input));
            System.out.println(table.remove(input));
            System.out.println(table);
            System.out.println(table.realSize());

        }
        scanner.close();
    }
}
