package btree;

import org.junit.Test;

import static org.junit.Assert.*;

public class BTreeTest {

    private int depth = 0;

    @Test
    public void testFind() {
        int count = 500;
        BTree tree = initTree(count, 3);

        for (int i = 0; i < count; i++) {
            assertTrue(tree.find(i));
        }

        for (int i = 500; i < 550; i++) {
            assertFalse(tree.find(i));
        }
    }

    @Test
    public void testRemove() {
        BTree tree = initTree(30, 3);

        for (int i = 0; i < 30; i++) {
            assertTrue(tree.delete(i));
            assertFalse(tree.find(i));
        }
    }

    @Test
    public void testDepth() {
        BTree tree = initTree(900, 3);

        measureDepth(tree.getRoot(), depth);
        System.out.println(depth);
    }

    private void measureDepth(BTree.Node node, int nodeDepth) {
        if (node.children == null) {
            if (depth > 0) {
                assertEquals(depth, nodeDepth);
            } else {
                depth = nodeDepth;
            }
            return;
        }

        for (int i = 0; i < node.size + 1; i++) {
            measureDepth(node.children[i], nodeDepth + 1);
        }
    }

    private BTree initTree(int count, int maxDegree) {
        BTree tree = new BTree(maxDegree);
        for (int i = 0; i < count; i++) {
            tree.add(i);
        }
        return tree;
    }
}