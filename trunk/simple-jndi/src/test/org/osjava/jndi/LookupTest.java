package org.osjava.jndi;

import org.apache.commons.lang.StringUtils;
import javax.naming.*;
import javax.sql.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class LookupTest extends TestCase {

    private InitialContext ctxt;
    private String delimiter;

    public LookupTest(String name) {
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
            props.setProperty("org.osjava.jndi.datasource", "true");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
            fake.setName("TestDS");
            assertEquals( fake, lookup("java:/TestDS") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testValueLookup() {
        try {
            assertEquals( "13", lookup("test/value") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testValuesLookup() {
        try {
            HashMap map = new HashMap();
            map.put("five", "5");
            map.put("one", "two");
            map.put("three", "four");
            assertEquals( map, lookup("thing/type") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testDS2Lookup() {
        try {
            DataSource aTestDS = (DataSource) lookup("thing.db.ATestDS");
            DataSource fake = createFakeDS(
            "jdbc:mysql://192.168.133.2/bikehell", 
            "org.gjt.mm.mysql.Driver", 
            "nico", 
            "bear","ATestDS");
            assertEquals( fake, aTestDS );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }
            
    public void testListLookup() {
        try {
            ArrayList list = new ArrayList();
            list.add( new Integer(24) );
            list.add( new Integer(25) );
            list.add( new Integer(99) );
            assertEquals( list, lookup("thing.type.bob.age") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testList2Lookup() {
        try {
            ArrayList list2 = new ArrayList();
            list2.add( "Henri" );
            list2.add( "Fred" );
            assertEquals( list2, lookup("name") );
            assertEquals( "yandell.org", lookup("url") );
            assertEquals( "Foo", this.ctxt.lookup("com.genjava") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testDS3Lookup() {
        try {
            DataSource genscapeDS = (DataSource) lookup("GenscapeDS");
            Properties props = new Properties();
            props.setProperty("url", "jdbc:mysql://192.168.133.2/bikehell");
            props.setProperty("driver", "org.gjt.mm.mysql.Driver");
            props.setProperty("user", "nico");
            props.setProperty("password", "bear");
            props.setProperty("org.osjava.jndi.datasource", "true");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
            fake.setName("");
            assertEquals( fake, genscapeDS );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testXmlLookup() {
        try {
//            System.err.println("XML: "+lookup("xmltest") );
// TODO: Should this return something? XML?
//            System.err.println("XML: "+lookup("xmltest.config") );
            assertEquals( "13", lookup("xmltest.config.value") );
            assertEquals( "Bang", lookup("xmltest.config.four.five") );
            assertEquals( "three", lookup("xmltest.config.one.two") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }


    public void testXmlDSLookup() {
        try {
            DataSource aTestDS = (DataSource) lookup("thing.db2.ATestDS");
            DataSource fake = createFakeDS(
            "jdbc:mysql://192.168.133.2/bikehell", 
            "org.gjt.mm.mysql.Driver", 
            "nico", 
            "bear","ATestDS");
            assertEquals( fake, aTestDS );

        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testIniLookup() {
        try {
            assertEquals( "13", lookup("testini/block1/value") );
            assertEquals( "pears", lookup("testini/block2/apple") );
            assertEquals( "stairs", lookup("testini/block2/orange") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    private DataSource createFakeDS(String url, String driver, String user, String passwd, String name) {
        Properties props = new Properties();
        // even though delimiter is a /, we use . here for the 
        // test. This is because the dot needs to be in the file
        props.setProperty(name+"/url", url);
        props.setProperty(name+"/driver", driver);
        props.setProperty(name+"/user", user);
        props.setProperty(name+"/password", passwd);
        PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), this.delimiter);
        fake.setName(name);
        return fake;
    }

    private Object lookup(String key) throws NamingException {
        return this.ctxt.lookup( StringUtils.replace(key, ".", this.delimiter) );
    }
}
