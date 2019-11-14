package test;

import main.Cache;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testSetGet() throws InterruptedException {
        Cache<Integer, Integer> cache = new Cache<>(10, 1000);

        for (int i = 0; i < 100; i++) {
            cache.set(i, i);
            assertEquals(i, (int) cache.get(i));
        }
    }

    @Test
    public void testTimeout() throws InterruptedException {
        Cache<Integer, Integer> cache = new Cache<>(10, 100);
        Integer testValue = 101;
        cache.set(testValue, testValue);
        assertEquals(testValue, cache.get(testValue));
        Thread.sleep(101);
        cache.set(1, 1);
        assertNull(cache.get(testValue));
    }

    @Test
    public void testDropLatest() {
        Cache<Integer, Integer> cache = new Cache<>(10, 1000);
        for (int i = 0; i < 11; i++) {
            cache.set(i, i);
        }

        assertNull(cache.get(0));

        for (int i = 1; i < 11; i++) {
            assertEquals(i, (int) cache.get(i));
        }
    }

    @Test
    public void testLRU() {
        Cache<Integer, Integer> cache = new Cache<>(10000, 1000000);

        for (int i = 0; i < 10000; i++) {
            cache.set(i, i);
        }

        long start = System.currentTimeMillis();
        cache.get(0);
        long getFromEnd = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        cache.get(0);
        long getFromFront = System.currentTimeMillis() - start;

        assertTrue(getFromEnd > getFromFront);
    }
}
