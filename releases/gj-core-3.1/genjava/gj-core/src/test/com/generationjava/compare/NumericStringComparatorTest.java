package com.generationjava.compare;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class NumericStringComparatorTest extends TestCase {

    public NumericStringComparatorTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(NumericStringComparatorTest.class);
    	suite.setName("NumericStringComparatorTest Tests");
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //-----------------------------------------------------------------------

    public void test1() {
        compare(".0004306 - Orlando Inventory", ".4306", 1);
    }

    public void test2() {
        compare("2a", "2", 1);
        compare("2", "2a", -1);
    }

    public void test3() {
        compare("2a", "aa2b3", -1);
    }

    public void test4() {
        compare("Brak1A", "Brak1B", -1);
        compare("Brak1", "Brak1B", -1);
    }

    private void compare(String str1, String str2, int expected) {
        NumericStringComparator nsc = new NumericStringComparator();
        int cmp = nsc.compare(str1, str2);
        if(cmp != 0) {
            cmp = cmp / Math.abs(cmp);
        }
        assertTrue( str1+" and "+str2+" should compare as "+expected+" and not "+cmp, cmp == expected);
    }

}
