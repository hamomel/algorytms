package hashtable;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private String[] testStrings = new String[]{ "first", "second", "third" };
    private HashTable<String, String> table;

    @Before
    public void setup() {
        table = new HashTable<>();
    }

    @Test
    public void testAdd() {
        table.insert(testStrings[0], testStrings[1]);
        assertEquals(testStrings[1], table.find(testStrings[0]));
    }

    @Test
    public void testRemove() {
        table.insert(testStrings[0], testStrings[1]);
        table.remove(testStrings[0]);
        assertNull(table.find(testStrings[0]));
    }
}
