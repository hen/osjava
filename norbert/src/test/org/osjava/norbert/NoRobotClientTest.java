/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of OSJava nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.norbert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

public class NoRobotClientTest extends TestCase {

    private String hardCode = "file://"+new File("data/").getAbsoluteFile()+File.separator;

    public NoRobotClientTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    // create -> parse -> isUrlAllowed?

    public void testAllowed() {
        try {
            String base = this.hardCode + "basic/";
            NoRobotClient nrc = new NoRobotClient("Scabies-1.0");
            nrc.parse( new URL(base) );
            assertTrue( nrc.isUrlAllowed( new URL(base+"index.html") ) );
            assertFalse( nrc.isUrlAllowed( new URL(base+"view-cvs/") ) );
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

    // Tests the example given in the RFC
    public void testRfcExampleUnhipbot() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = this.hardCode + "rfc/";

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
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

    public void testRfcExampleWebcrawler() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = this.hardCode + "rfc/";

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
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

    public void testRfcExampleExcite() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = this.hardCode + "rfc/";

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
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

    public void testRfcExampleOther() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = this.hardCode + "rfc/";

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
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

    public void testRfcBadWebDesigner() {
        try {
            // pretend that http://www.fict.org/ is our base
            String base = this.hardCode + "bad/";

            NoRobotClient nrc = new NoRobotClient("other");
            nrc.parse( new URL(base) );

            assertEquals( true,  nrc.isUrlAllowed( new URL(base+"%7Etest/%7Efoo.html") ) );
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

    // Tests NRB-3
    // http://www.osjava.org:8080/jira/secure/ViewIssue.jspa?key=NRB-3
    public void testNrb3() {
        try {
            String base = this.hardCode + "basic/";
            NoRobotClient nrc = new NoRobotClient("Scabies-1.0");
            nrc.parse( new URL(base) );
            assertTrue( nrc.isUrlAllowed( new URL(this.hardCode + "basic" ) ) );
        } catch(MalformedURLException murle) {
            throw new RuntimeException("Test failed: "+murle.getMessage());
        } catch(NoRobotException nre) {
            throw new RuntimeException("Test failed: "+nre.getMessage());
        }
    }

}
