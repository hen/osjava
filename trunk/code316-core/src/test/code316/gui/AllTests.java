package code316.gui;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;


public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for code316.gui");
        //$JUnit-BEGIN$
        suite.addTestSuite(BeanModelTester.class);
        suite.addTestSuite(ModelDescriptionTest.class);
        //$JUnit-END$
        return suite;
    }
    
    public static void main(String[] args) {
        suite().run(new TestResult());
    }
}
