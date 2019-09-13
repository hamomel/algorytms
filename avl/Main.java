import java.util.Random;

class Main {
    public static void main(String[] args) {
        XYtree tree = new XYtree(34, 22, null, null);
        Random random = new Random();
        for (int i = 4; i < 40; i += 1) {
            tree.add(random.nextInt(1000));
        } 

        System.out.println(tree.getTreeString());
    }
}