package org.osjava.jndi;

import javax.naming.*;
import javax.sql.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SharedMemoryTest extends TestCase {

    private Context ctxt1;
    private Context ctxt2;

    public SharedMemoryTest(String name) {
        super(name);
    }

    public void setUp() {
        Hashtable ht = new Hashtable();
        ht.put("org.osjava.jndi.shared", "true");
        ctxt1 = new PropertiesContext(ht);
        ctxt2 = new PropertiesContext(ht);
    }

    public void tearDown() {
        this.ctxt1 = null;
        this.ctxt2 = null;
    }

    public void testShared() {
        try {
            String test1 = "TEST1";
            ctxt1.bind(test1, test1);
            assertEquals( test1, ctxt2.lookup(test1) );
        } catch(NamingException ne) {
            fail("Some kind of JNDI problem: "+ne);
        }
    }

}
