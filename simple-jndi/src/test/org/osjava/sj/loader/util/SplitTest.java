package org.osjava.sj.loader.util;

import junit.framework.TestCase;

public class SplitTest extends TestCase {

    public SplitTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    public void testSplit() {
        assertArrayEquals("", new String[] { "config", "one", "two"}, Utils.split("config.one.two", ".") );
        assertArrayEquals("", new String[] { "one", "two", "three"}, Utils.split("one.two.three", ".") );
        assertArrayEquals("", new String[] { "one"}, Utils.split("one", ".") );
        assertArrayEquals("", new String[] { ""}, Utils.split("", ".") );
    }

    private void assertArrayEquals(String message, String[] array1, String[] array2) {
        assertEquals(message+" - Length not equal ", array1.length, array2.length);
        for(int i=0; i<array1.length; i++) {
            assertEquals(message+" - array[" + i + "] element not equal ", array1[i], array2[i]);
        }
    }

}
