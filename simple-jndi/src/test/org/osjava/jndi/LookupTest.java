package org.osjava.jndi;

import org.apache.commons.lang.*;
import javax.naming.*;
import java.sql.*;
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

    public void testLookup() {
        try {
            InitialContext ctxt = new InitialContext();
            this.delimiter = (String) ctxt.lookup("org.osjava.jndi.delimiter");
            Properties props = new Properties();
            props.setProperty("TestDS/url", "foofoo");
            props.setProperty("TestDS/driver", "bing");
            props.setProperty("TestDS/user", "Boo");
            props.setProperty("TestDS/password", "bong");
            props.setProperty("org.osjava.jndi.datasource", "true");
            PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), "/");
            fake.setName("TestDS");
            assertEquals( lookup("java:/TestDS/url", ctxt), fake );

            assertEquals( lookup("test/value", ctxt), "13" );

            HashMap map = new HashMap();
            map.put("five", "5");
            map.put("one", "two");
            map.put("three", "four");
            assertEquals( lookup("thing/type", ctxt), map );

            /*
            System.err.println("thing.db.ATestDS="+lookup("thing.db.ATestDS", ctxt));
            System.err.println("thing.db.ATestDS.class="+lookup("thing.db.ATestDS", ctxt).getClass());
            DataSource ds = (DataSource)lookup("thing.db.ATestDS", ctxt);
            print(ds);
            System.err.println("thing.type.bob.age="+lookup("thing.type.bob.age", ctxt));
            System.err.println("thing.type.bob.age.class="+lookup("thing.type.bob.age", ctxt).getClass());
            System.err.println("thing.type.bob.age[0].class="+((List)lookup("thing.type.bob.age", ctxt)).get(0).getClass());
            ds = (DataSource)lookup("GenscapeDS", ctxt);
            print(ds);
            System.err.println("name="+lookup("name", ctxt));
            System.err.println("url="+lookup("url", ctxt));
            System.err.println("com.genjava="+lookup("com.genjava", ctxt));
            */
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public static void print(DataSource ds) throws Exception {
        Connection conn = ds.getConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        System.err.println(dbmd.getDatabaseProductVersion());
        System.err.println(dbmd.getDriverName());
        System.err.println(dbmd.getDriverVersion());
        conn.close();
    }

    private Object lookup(String key, Context ctxt) throws NamingException {
        return ctxt.lookup( StringUtils.replace(key, ".", delimiter) );
    }
}
