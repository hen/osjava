package com.generationjava.lang;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class StringWTest extends TestCase {

    public StringWTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: join(Object[], String, String, String)

    public void testJoin() {
        assertEquals("pre1,2,3,4post", StringW.join( new String[] { "1", "2", "3", "4" }, ",", "pre", "post") );
    }

}
