package btree;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BTreeTest {
    private BTree tree;

    @Before
    public void setUp() throws Exception {
        tree = new BTree(3);
    }

    @Test
    public void testFind() {
        int count = 500;
        feelTree(tree, count);

        for (int i = 0; i < count; i++) {
            assertTrue(tree.find(i));
        }

        for (int i = 500; i < 550; i++) {
            assertFalse(tree.find(i));
        }
    }

    @Test
    public void testRemove() {
        feelTree(tree, 30);

        for (int i = 0; i < 30; i++) {
            assertTrue(tree.delete(i));
            assertFalse(tree.find(i));
        }
    }

    private void feelTree(BTree tree, int count) {
        for (int i = 0; i < count; i++) {
            tree.add(i);
        }
    }
}