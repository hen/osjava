package org.osjava.jndi;

import javax.naming.*;
import javax.sql.*;
import java.util.*;
import org.osjava.jndi.util.HierarchicalMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class DataSourceLookupTest extends TestCase {

    private InitialContext ctxt;
    private String delimiter;

    public DataSourceLookupTest(String name) {
        super(name);
    }

    public void setUp() {
        try {
            ctxt = new InitialContext();
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
            props.put("TestDS/url", "foofoo");
            props.put("TestDS/driver", "bing");
            props.put("TestDS/user", "Boo");
            props.put("TestDS/password", "bong");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
            fake.setName("TestDS");
            assertEquals( fake, lookup("java:/TestDS") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testFailedDSLookup() {
        try {
            Object obj = lookup("java:/FailedTestDS");
            fail("Should have failed, instead found: "+obj);
        } catch(NamingException ne) {
        }
    }

    public void testDS2Lookup() {
        try {
            DataSource aTestDS = (DataSource) lookup("thing.db.ATestDS");
            DataSource fake = TestUtils.createFakeDS(
            "jdbc:mysql://192.168.133.2/bikehell", 
            "org.gjt.mm.mysql.Driver", 
            "nico", 
            "bear","ATestDS", this.delimiter);
            assertEquals( fake, aTestDS );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }
            
    public void testDS3Lookup() {
        try {
            DataSource genscapeDS = (DataSource) lookup("TestDS");
            HierarchicalMap props = new HierarchicalMap("/");
            props.put("url", "jdbc:mysql://192.168.133.2/bikehell");
            props.put("driver", "org.gjt.mm.mysql.Driver");
            props.put("user", "nico");
            props.put("password", "bear");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
            fake.setName("");
            assertEquals( fake, genscapeDS );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testXmlDSLookup() {
        try {
            DataSource aTestDS = (DataSource) lookup("thing.db2.ATestDS");
            DataSource fake = TestUtils.createFakeDS(
            "jdbc:mysql://192.168.133.2/bikehell", 
            "org.gjt.mm.mysql.Driver", 
            "nico", 
            "bear","ATestDS", this.delimiter);
            assertEquals( fake, aTestDS );

        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testPoolDSLookup() {
        try {
            DataSource oneDS = (DataSource) lookup("pooltest.OneDS");
            DataSource twoDS = (DataSource) lookup("pooltest.TwoDS");
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    private Object lookup(String key) throws NamingException {
        return this.ctxt.lookup( key.replaceAll("\\.", this.delimiter) );
    }
}
