package karaskal;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class KaraskalTest {

    private int[][] testGraph = new int[][] {
            new int[] { 0, 8, 0, 0, 1, 4, 0, 0, 10 },
            new int[] { 8, 0, 0, 0, 0, 0, 0, 5, 0 },
            new int[] { 0, 0, 0, 0, 7, 0, 6, 0, 0 },
            new int[] { 0, 0, 0, 0, 0, 0, 3, 0, 0 },
            new int[] { 1, 0, 7, 0, 0, 0, 0, 9, 0 },
            new int[] { 4, 0, 0, 0, 0, 0, 0, 11, 0 },
            new int[] { 0, 0, 6, 3, 0, 0, 0, 0, 0 },
            new int[] { 0, 5, 0, 0, 9, 11, 0, 0, 0 },
            new int[] { 10, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    private List<Edge> testTree = new ArrayList<>();

    @Before
    public void setUp() {
        testTree.add(new Edge(0, 4, 1));
        testTree.add(new Edge(3, 6, 3));
        testTree.add(new Edge(0, 5, 4));
        testTree.add(new Edge(1, 7, 5));
        testTree.add(new Edge(2, 6, 6));
        testTree.add(new Edge(2, 4, 7));
        testTree.add(new Edge(0, 1, 8));
        testTree.add(new Edge(0, 8 ,10));
    }

    @Test
    public void test() {
        List<Edge> tree = App.findMinimalTree(testGraph);
        assertEquals(testTree, tree);
    }
}
