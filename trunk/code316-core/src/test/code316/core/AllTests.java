package code316.core;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;


public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for code316.core");
        //$JUnit-BEGIN$
        suite.addTestSuite(FileUtilTest.class);
        suite.addTestSuite(FilterPropertiesTest.class);
        suite.addTestSuite(IsEmptyTest.class);
        //$JUnit-END$
        return suite;
    }
    
    public static void main(String[] args) {
        suite().run(new TestResult());
    }
}
