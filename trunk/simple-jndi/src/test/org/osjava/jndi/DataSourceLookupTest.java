package org.osjava.jndi;

import javax.naming.*;
import javax.sql.*;
import java.util.*;

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
            Properties props = new Properties();
            props.setProperty("TestDS/url", "foofoo");
            props.setProperty("TestDS/driver", "bing");
            props.setProperty("TestDS/user", "Boo");
            props.setProperty("TestDS/password", "bong");
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
            Properties props = new Properties();
            props.setProperty("url", "jdbc:mysql://192.168.133.2/bikehell");
            props.setProperty("driver", "org.gjt.mm.mysql.Driver");
            props.setProperty("user", "nico");
            props.setProperty("password", "bear");
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

    private Object lookup(String key) throws NamingException {
        return this.ctxt.lookup( key.replaceAll("\\.", this.delimiter) );
    }
}
