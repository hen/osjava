package org.osjava.sj;

import javax.naming.*;
import javax.sql.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SimpleContextTest extends TestCase {

    private InitialContext ctxt;

    public SimpleContextTest(String name) {
        super(name);
    }

    public void setUp() {
        try {
            ctxt = new InitialContext();
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
    }

    public void tearDown() {
        this.ctxt = null;
    }

    public void testValueLookup() throws NamingException {
        assertEquals( "13", this.ctxt.lookup("test.value") );
    }

    public void testListLookup() throws NamingException {
        ArrayList list = new ArrayList();
        list.add( "24" );
        list.add( "25" );
        list.add( "99" );
        assertEquals( list, this.ctxt.lookup("thing.type.bob.age") );
    }

    public void testList2Lookup() throws NamingException {
        ArrayList list2 = new ArrayList();
        list2.add( "Henri" );
        list2.add( "Fred" );
        assertEquals( list2, this.ctxt.lookup("name") );
        assertEquals( "yandell.org", this.ctxt.lookup("url") );
        assertEquals( "Foo", this.ctxt.lookup("com.genjava") );
    }

    public void testXmlLookup() throws NamingException {
//            System.err.println("XML: "+this.ctxt.lookup("xmltest") );
// TODO: Should this return something? XML?
//            System.err.println("XML: "+this.ctxt.lookup("xmltest.config") );
            assertEquals( "13", this.ctxt.lookup("xmltest.config.value") );
            assertEquals( "Bang", this.ctxt.lookup("xmltest.config.four.five") );
            assertEquals( "three", this.ctxt.lookup("xmltest.config.one.two") );
            List list = new ArrayList();
            list.add("one");
            list.add("two");
            assertEquals( list, this.ctxt.lookup("xmltest.config.multi.item") );
    }

    public void testIniLookup() throws NamingException {
        assertEquals( "blockless", this.ctxt.lookup("testini.first") );
        assertEquals( "13", this.ctxt.lookup("testini.block1.value") );
        assertEquals( "pears", this.ctxt.lookup("testini.block2.apple") );
        assertEquals( "stairs", this.ctxt.lookup("testini.block2.orange") );
    }

    public void testColonReplaceLookup() throws NamingException {
        assertEquals( "42", this.ctxt.lookup("java:.magic") );

        Context subCtxt = (Context) this.ctxt.lookup("java:");
        assertEquals( "42", subCtxt.lookup("magic") );
    }

}
