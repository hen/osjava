package org.osjava.atom4j.reader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Describe the class here.
 * 
 * @author llavandowska
 */
public class TestAll extends TestCase
{
    public TestAll(String testName)
    {
        super(testName);
    }

    public static void main(String[] args)
    {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

		suite.addTest(EntryReaderTest.suite());
        suite.addTest(PrefsReaderTest.suite());

        return suite;
    }
}