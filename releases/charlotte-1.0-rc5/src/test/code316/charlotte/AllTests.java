package code316.charlotte;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for code316.bits");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(ValueExtractorTest.class));
        suite.addTest(new TestSuite(EncodingTest.class));
        //$JUnit-END$
        return suite;
    }
}
