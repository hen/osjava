package org.osjava.jndi;

import javax.naming.*;
import javax.sql.*;
import java.util.*;
import org.osjava.jndi.util.HierarchicalMap;

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
            ArrayList list = new ArrayList();
            list.add( "Henri" );
            list.add( "Fred" );
            assertEquals( list, lookup("name") );
            assertEquals( "yandell.org", lookup("url") );
            assertEquals( "Foo", this.ctxt.lookup("com.genjava") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    public void testHierarchicalLookup() {
        try {
            ArrayList list1 = new ArrayList();
            list1.add("1");
            list1.add("3");
            ArrayList list2 = new ArrayList();
            list2.add("2");
            list2.add("4");
            assertEquals( list1, lookup("hierarchy.test.path.one") );
            assertEquals( list2, lookup("hierarchy.test.path.two") );

// depends on Xml or Properties/Ini
//            HierarchicalMap hmap = (HierarchicalMap) lookup("hierarchy.test.path");
//            assertEquals( HierarchicalMap.class, hmap.get("one").getClass() );
//            assertEquals( HierarchicalMap.class, hmap.get("two").getClass() );
// this one should have two hierarchical maps, one with one=1, two=2 and one with one=3, two=4
//            System.out.println( lookup("hierarchy.test.path") );

// ?? what should be here?
//            System.out.println( lookup("hierarchy.test") );
//            System.out.println( lookup("hierarchy") );
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
            List list = new ArrayList();
            list.add("one");
            list.add("two");
            assertEquals( list, lookup("xmltest.config.multi.item") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }


    public void testIniLookup() {
        try {
            assertEquals( "blockless", lookup("testini/first") );
            assertEquals( "13", lookup("testini/block1/value") );
            assertEquals( "pears", lookup("testini/block2/apple") );
            assertEquals( "stairs", lookup("testini/block2/orange") );
        } catch(NamingException ne) {
            fail("NamingException: "+ne.getMessage());
        }
    }

    private Object lookup(String key) throws NamingException {
        return this.ctxt.lookup( key.replaceAll("\\.", this.delimiter) );
    }
}
