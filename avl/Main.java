import java.util.Random;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        XYtree tree = new XYtree(34, 22, null, null);
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            tree.add(random.nextInt(1000));
        }

        System.out.println(tree.getTreeString());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            var toRemove = scanner.next();
            try {
                tree.remove(Integer.parseInt(toRemove));
            } catch (Throwable e) {
                break;
            }

            System.out.println(tree.getTreeString());
        }

        scanner.close();

        // tree.remove(tree.left.left.value);

    }
}