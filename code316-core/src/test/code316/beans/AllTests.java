package code316.beans;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for code316.beans");
        //$JUnit-BEGIN$
        suite.addTestSuite(BeanTableTest.class);
        //$JUnit-END$
        return suite;
    }
}
