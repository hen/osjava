package com.generationjava.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class WildcardUtilsTest extends TestCase {

    public WildcardUtilsTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   WildcardUtils.match(String,String)

    public void testMatch() {
        assertTrue( WildcardUtils.match("Foo", "Foo") );
        assertTrue( WildcardUtils.match("", "") );
        assertTrue( WildcardUtils.match("Foo", "Fo*") );
        assertTrue( WildcardUtils.match("Foo", "Fo?") );
        assertTrue( WildcardUtils.match("Foo Bar and Catflap", "Fo*") );
        assertTrue( WildcardUtils.match("New Bookmarks", "N?w ?o?k??r?s") );
        assertFalse( WildcardUtils.match("Foo", "Bar") );
        assertTrue( WildcardUtils.match("Foo Bar Foo", "F*o Bar*") );
        assertTrue( WildcardUtils.match("Adobe Acrobat Installer", "Ad*er") );
    }

    public void testSplitOnTokens() {
        assertArrayEquals( new String[] { "Ad", "*", "er" }, WildcardUtils.splitOnTokens("Ad*er") );
        assertArrayEquals( new String[] { "" }, WildcardUtils.splitOnTokens("") );
    }

    private void assertArrayEquals(Object[] a1, Object[] a2) {
        assertEquals(a1.length, a2.length);
        for(int i=0; i<a1.length; i++) {
            assertEquals(a1[i], a2[i]);
        }
    }

}
