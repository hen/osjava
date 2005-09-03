package org.osjava.sj;


import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

public class SystemPropertyTest extends TestCase {

    private InitialContext ctxt;

    public SystemPropertyTest(String name) {
        super(name);
    }

    public void setUp() {
        System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory");
        System.setProperty("org.osjava.sj.root", "file://src/test/config/system-test");
        System.setProperty("org.osjava.sj.delimiter", "::::");
        try {
            ctxt = new InitialContext();
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void tearDown() {
        this.ctxt = null;
    }

    public void testSystemPropertyContext() throws NamingException {
        assertEquals( "1234", this.ctxt.lookup("one::::two::::three::::four") );
    }
}
