package org.osjava.sj.loader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class ReplaceTest extends TestCase {

    public ReplaceTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    public void testReplace() {
        assertEquals("one:two:three", JndiLoader.replace("one--two--three", "--", ":" ) );
        assertEquals("one:two:", JndiLoader.replace("one--two--", "--", ":" ) );
        assertEquals(":two:three", JndiLoader.replace("--two--three", "--", ":" ) );
    }

}
