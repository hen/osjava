package org.osjava.norbert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.net.URL;
import java.net.MalformedURLException;

public class NoRobotClientTest extends TestCase {

    public NoRobotClientTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    // create -> parse -> isUrlAllowed?

    public void testAllowed() {
        try {
            String base = "file:///Users/hen/gj/norbert/data/basic/";
            NoRobotClient nrc = new NoRobotClient("Scabies-1.0");
            nrc.parse( new URL(base) );
            assertTrue( nrc.isUrlAllowed( new URL(base+"index.html") ) );
            assertFalse( nrc.isUrlAllowed( new URL(base+"view-cvs/") ) );
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        }
    }

    // Tests the example given in the RFC
    public void testRfcExampleUnhipbot() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = "file:///Users/hen/gj/norbert/data/rfc/";

            NoRobotClient nrc = new NoRobotClient("unhipbot");
            nrc.parse( new URL(base) );

            // Start of rfc test
            assertEquals( false,  nrc.isUrlAllowed( new URL(base) ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"index.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"robots.txt") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"server.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"services/fast.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"services/slow.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"orgo.gif") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"org/about.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"org/plans.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"%7Ejim/jim.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"%7Emak/mak.html") ) );
            // End of rfc test
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        }
    }

    public void testRfcExampleWebcrawler() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = "file:///Users/hen/gj/norbert/data/rfc/";

            NoRobotClient nrc = new NoRobotClient("webcrawler");
            nrc.parse( new URL(base) );
            // Start of rfc test
            assertEquals( true,  nrc.isUrlAllowed( new URL(base) ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"index.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"robots.txt") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"server.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"services/fast.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"services/slow.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"orgo.gif") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"org/about.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"org/plans.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Ejim/jim.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Emak/mak.html") ) );
            // End of rfc test
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        }
    }

    public void testRfcExampleExcite() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = "file:///Users/hen/gj/norbert/data/rfc/";

            NoRobotClient nrc = new NoRobotClient("excite");
            nrc.parse( new URL(base) );
            // Start of rfc test
            assertEquals( true,  nrc.isUrlAllowed( new URL(base) ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"index.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"robots.txt") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"server.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"services/fast.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"services/slow.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"orgo.gif") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"org/about.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"org/plans.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Ejim/jim.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Emak/mak.html") ) );
            // End of rfc test
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        }
    }

    public void testRfcExampleOther() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = "file:///Users/hen/gj/norbert/data/rfc/";

            NoRobotClient nrc = new NoRobotClient("other");
            nrc.parse( new URL(base) );
            // Start of rfc test
            assertEquals( false,  nrc.isUrlAllowed( new URL(base) ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"index.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"robots.txt") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"server.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"services/fast.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"services/slow.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"orgo.gif") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"org/about.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"org/plans.html") ) );
            assertEquals( false,  nrc.isUrlAllowed( new URL(base+"%7Ejim/jim.html") ) );
            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Emak/mak.html") ) );
            // End of rfc test
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        }
    }

    public void testRfcBadWebDesigner() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = "file:///Users/hen/gj/norbert/data/bad/";

            NoRobotClient nrc = new NoRobotClient("other");
            nrc.parse( new URL(base) );

            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Etest/%7Efoo.html") ) );
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        }
    }

}
