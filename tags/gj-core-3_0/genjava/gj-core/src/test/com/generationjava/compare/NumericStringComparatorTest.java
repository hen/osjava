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
        compare(".0004306 - Orlando Inventory", ".4306");
    }

    public void test2() {
        compare("2a", "2");
    }

    public void test3() {
        compare("2a", "aa2b3");
    }

    private void compare(String str1, String str2) {
        NumericStringComparator nsc = new NumericStringComparator();
        nsc.compare(str1, str2);
    }

}
