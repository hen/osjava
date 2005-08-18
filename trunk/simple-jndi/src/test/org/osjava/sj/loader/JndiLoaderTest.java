package org.osjava.sj.loader;

import javax.naming.*;
import java.io.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.sql.DataSource;

public class JndiLoaderTest extends TestCase {

    private Context ctxt;

    public JndiLoaderTest(String name) {
        super(name);
    }

    public void setUp() {

        /* The default is 'flat', which isn't hierarchial and not what I want. */
        /* Separator is required for non-flat */

        Hashtable contextEnv = new Hashtable();

        /* For GenericContext */
        contextEnv.put(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.memory.MemoryContextFactory");
        contextEnv.put("jndi.syntax.direction", "left_to_right");
        contextEnv.put("jndi.syntax.separator", "/");
        /**/

        /* For Directory-Naming
        contextEnv.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        contextEnv.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
        contextEnv.put("jndi.syntax.direction", "left_to_right");
        contextEnv.put("jndi.syntax.separator", "/");
        */
        
        try {
            ctxt = new InitialContext(contextEnv);
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void tearDown() {
        this.ctxt = null;
    }

    public void testProperties() {
        try {
            Properties props = new Properties();
            props.put("foo", "13");
            props.put("bar/foo", "42");
            JndiLoader loader = new JndiLoader();
            loader.load( props, ctxt );
            assertEquals( "13", ctxt.lookup("foo") );
            assertEquals( "42", ctxt.lookup("bar/foo") );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testDirectory() {
        try {
            File file = new File("src/test/config/");
            JndiLoader loader = new JndiLoader();
            loader.loadDirectory( file, ctxt );
            assertEquals( "13", ctxt.lookup("test/value") );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testDefaultFile() {
        try {
            File file = new File("src/test/config/");
            JndiLoader loader = new JndiLoader();
            loader.loadDirectory( file, ctxt );
            assertEquals( "Fred", ctxt.lookup("name") );
            assertEquals( "Foo", ctxt.lookup("com.genjava") );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testSubContext() {
        String dsString = "bing::::foofoo::::Boo";
        try {
            File file = new File("src/test/config/");
            JndiLoader loader = new JndiLoader();
            loader.loadDirectory( file, ctxt );
            Context subctxt = (Context) ctxt.lookup("java");
            assertEquals( dsString, subctxt.lookup("TestDS").toString() );
            DataSource ds = (DataSource) ctxt.lookup("java/TestDS");
            assertEquals( dsString, ds.toString() );
        } catch(IOException ioe) {
            ioe.printStackTrace();
            fail("IOException: "+ioe.getMessage());
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testBoolean() {
        try {
            Properties props = new Properties();
            props.put("foo", "true");
            props.put("foo/type", "java.lang.Boolean");
            JndiLoader loader = new JndiLoader();
            loader.load( props, ctxt );
            assertEquals( new Boolean(true), ctxt.lookup("foo") );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testDate() {
        try {
            Properties props = new Properties();
            props.put("birthday", "2004-10-22");
            props.put("birthday/type", "java.util.Date");
            props.put("birthday/format", "yyyy-MM-dd");

            JndiLoader loader = new JndiLoader();
            loader.load( props, ctxt );

            Date d = (Date) ctxt.lookup("birthday");
            Calendar c = Calendar.getInstance();
            c.setTime(d);

            assertEquals( 2004, c.get(Calendar.YEAR) );
            assertEquals( 10 - 1, c.get(Calendar.MONTH) );
            assertEquals( 22, c.get(Calendar.DATE) );
        } catch(NamingException ne) {
            ne.printStackTrace();
            fail("NamingException: "+ne.getMessage());
        }
    }

}
