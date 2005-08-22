package org.osjava.sj.loader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SplitTest extends TestCase {

    public SplitTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    public void testSplit() {
        assertArrayEquals("", new String[] { "config", "one", "two"}, JndiLoader.split("config.one.two", ".") );
        assertArrayEquals("", new String[] { "one", "two", "three"}, JndiLoader.split("one.two.three", ".") );
        assertArrayEquals("", new String[] { "one"}, JndiLoader.split("one", ".") );
        assertArrayEquals("", new String[] { ""}, JndiLoader.split("", ".") );
    }

    private void assertArrayEquals(String message, String[] array1, String[] array2) {
        assertEquals(message+" - Length not equal ", array1.length, array2.length);
        for(int i=0; i<array1.length; i++) {
            assertEquals(message+" - array[" + i + "] element not equal ", array1[i], array2[i]);
        }
    }

}
