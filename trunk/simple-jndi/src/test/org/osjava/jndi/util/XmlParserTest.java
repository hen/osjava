package org.osjava.jndi.util;

import javax.naming.*;
import javax.sql.*;
import java.util.*;
import org.osjava.jndi.util.HierarchicalMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class XmlParserTest extends TestCase {

    private HierarchicalMap hmap;
    private XmlParser parser;

    public XmlParserTest(String name) {
        super(name);
    }

    public void setUp() {
        this.hmap = new HierarchicalMap(".");
        this.parser = new XmlParser();
    }

    public void tearDown() {
        this.hmap = null;
        this.parser = null;
    }

    public void testHierarchy() throws java.io.IOException {
        String input = "<root><path>\n" +
                       "  <one>1</one>\n" +
                       "  <two>2</two>\n" +
                       "</path>\n" +
                       "<path>\n" +
                       "  <one>3</one>\n" +
                       "  <two>4</two>\n" +
                       "</path></root>\n";

        parser.parse( new java.io.ByteArrayInputStream( input.getBytes() ), this.hmap );

        assertTrue( this.hmap.containsKey("root.path") );
        assertTrue( this.hmap.containsKey("root.path.one") );
        assertTrue( this.hmap.containsKey("root.path.two") );
        System.out.println("ZIP: "+this.hmap);
        System.out.println("ZIP: "+this.hmap.get("root.path"));
        assertEqualList( "Cross-hierarchy not correct for path.one. ", new Object[] { "1", "3" }, this.hmap.get("root.path.one") );
        assertEqualList( "Cross-hierarchy not correct for path.two. ", new Object[] { "2", "4" }, this.hmap.get("root.path.two") );
        System.out.println("ZIP: "+this.hmap.get("root.path"));
        assertEquals( "Getting 'path' should return List of HierarchicalMaps, because .xml are fully hierarchical. ", 
                         "1", ((Map) this.hmap.get("root.path")).get("one") 
                       );
    }

    private void assertEqualList(String msg, Object[] array, Object object) {
        System.out.println("BING: "+object);
        System.out.println("BING: "+object.getClass());
        assertTrue( "Value must be a java.util.List. ", object instanceof List);
        List list = (List) object;
        assertEquals( msg+" Length not equal. ", list.size(), array.length);
        for(int i=0; i<list.size(); i++) {
            assertEquals( msg+" Index not equal ["+i+"]", list.get(i), array[i] );
        }
    }

}
