package org.osjava.jndi;

import javax.naming.*;
import javax.sql.*;
import java.util.*;
import org.osjava.jndi.util.HierarchicalMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class ClasspathLookupTest extends TestCase {

    private InitialContext ctxt;
    private String delimiter;

    public ClasspathLookupTest(String name) {
        super(name);
    }

    public void setUp() {
        try {
            Hashtable parms = new Hashtable(1);
            parms.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.jndi.PropertiesFactory");
            parms.put("org.osjava.jndi.root", "classpath://config/thing");
            parms.put("org.osjava.jndi.delimiter", "/");
            ctxt = new InitialContext(parms);
            delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void tearDown() {
        this.ctxt = null;
        this.delimiter = null;
    }

    public void testDSLookup() {
        try {
            HierarchicalMap props = new HierarchicalMap("/");
            props.put("ATestDS/url", "jdbc:mysql://192.168.133.2/bikehell");
            props.put("ATestDS/driver", "org.gjt.mm.mysql.Driver");
            props.put("ATestDS/user", "nico");
            props.put("ATestDS/password", "bear");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
            fake.setName("ATestDS");
            assertEquals( fake, lookup("db/ATestDS") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testDSLookup2() {
        try {
            HierarchicalMap props = new HierarchicalMap("/");
            props.put("url", "jdbc:mysql://192.168.133.2/bikehell");
            props.put("driver", "org.gjt.mm.mysql.Driver");
            props.put("user", "nico");
            props.put("password", "bear");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
            fake.setName("");
            assertEquals( fake, lookup("jdbc/BTestDS") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    private Object lookup(String key) throws NamingException {
        return this.ctxt.lookup( key.replaceAll("\\.", this.delimiter) );
    }
}
