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

    private String delimiter = ".";

    public LookupTest(String name) {
        super(name);
    }

    public void testDSLookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            Properties props = new Properties();
            props.setProperty("TestDS/url", "foofoo");
            props.setProperty("TestDS/driver", "bing");
            props.setProperty("TestDS/user", "Boo");
            props.setProperty("TestDS/password", "bong");
            props.setProperty("org.osjava.jndi.datasource", "true");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), this.delimiter);
            fake.setName("TestDS");
            assertEquals( fake, lookup("java:/TestDS", ctxt) );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void testValueLookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            assertEquals( "13", lookup("test/value", ctxt) );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void testValuesLookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            HashMap map = new HashMap();
            map.put("five", "5");
            map.put("one", "two");
            map.put("three", "four");
            assertEquals( map, lookup("thing/type", ctxt) );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void testDS2Lookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            DataSource aTestDS = (DataSource) lookup("thing.db.ATestDS", ctxt);
            Properties props1 = new Properties();
            // even though delimiter is a /, we use . here for the 
            // test. This is because the dot needs to be in the file
            props1.setProperty("ATestDS/url", "jdbc:mysql://192.168.133.2/bikehell");
            props1.setProperty("ATestDS/driver", "org.gjt.mm.mysql.Driver");
            props1.setProperty("ATestDS/user", "nico");
            props1.setProperty("ATestDS/password", "bear");
            props1.setProperty("org.osjava.jndi.datasource", "true");
            PropertiesDataSource fake1 = new PropertiesDataSource(props1, new Hashtable(), this.delimiter);
            fake1.setName("ATestDS");
            assertEquals( fake1, aTestDS );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }
            
    public void testListLookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            ArrayList list = new ArrayList();
            list.add( new Integer(24) );
            list.add( new Integer(25) );
            list.add( new Integer(99) );
            assertEquals( list, lookup("thing.type.bob.age", ctxt) );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void testList2Lookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            ArrayList list2 = new ArrayList();
            list2.add( "Henri" );
            list2.add( "Fred" );
            assertEquals( list2, lookup("name", ctxt) );
            assertEquals( "yandell.org", lookup("url", ctxt) );
            assertEquals( "Foo", ctxt.lookup("com.genjava") );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void testDS3Lookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

            DataSource genscapeDS = (DataSource) lookup("GenscapeDS", ctxt);
            Properties props2 = new Properties();
            props2.setProperty("url", "jdbc:mysql://192.168.133.2/bikehell");
            props2.setProperty("driver", "org.gjt.mm.mysql.Driver");
            props2.setProperty("user", "nico");
            props2.setProperty("password", "bear");
            props2.setProperty("org.osjava.jndi.datasource", "true");
            PropertiesDataSource fake2 = new PropertiesDataSource(props2, new Hashtable(), this.delimiter);
            fake2.setName("");
            assertEquals( fake2, genscapeDS );
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void testXmlLookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");

//            System.err.println("XML: "+lookup("xmltest", ctxt) );
// TODO: Should this return something? XML?
//            System.err.println("XML: "+lookup("xmltest/config", ctxt) );
            assertEquals( "13", lookup("xmltest/config/value", ctxt) );
            assertEquals( "Bang", lookup("xmltest/config/four/five", ctxt) );
            assertEquals( "three", lookup("xmltest/config/one/two", ctxt) );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }


    private Object lookup(String key, Context ctxt) throws NamingException {
        return ctxt.lookup( StringUtils.replace(key, ".", delimiter) );
    }
}
