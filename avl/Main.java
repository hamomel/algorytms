class Main {
    public static void main(String[] args) {
        XYtree tree = new XYtree(34, 22, null, null);
        for (int i = 4; i < 40; i += 4) {
            tree.add(i);
        } 

        System.out.println(tree.getTreeString());
    }
}