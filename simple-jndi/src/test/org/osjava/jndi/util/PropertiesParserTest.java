package org.osjava.jndi.util;

import javax.naming.*;
import javax.sql.*;
import java.util.*;
import org.osjava.jndi.util.HierarchicalMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class PropertiesParserTest extends TestCase {

    private HierarchicalMap hmap;
    private PropertiesParser parser;

    public PropertiesParserTest(String name) {
        super(name);
    }

    public void setUp() {
        this.hmap = new HierarchicalMap(".");
        this.parser = new PropertiesParser();
    }

    public void tearDown() {
        this.hmap = null;
        this.parser = null;
    }

    public void testHierarchy() throws java.io.IOException {
        String input = "path.one=1\n" +
                       "path.two=2\n" +
                       "path.one=3\n" +
                       "path.two=4\n";

        parser.parse( new java.io.ByteArrayInputStream( input.getBytes() ), this.hmap );

        assertTrue( this.hmap.containsKey("path") );
        assertTrue( this.hmap.containsKey("path.one") );
        assertTrue( this.hmap.containsKey("path.two") );
        assertEqualList( "Cross-hierarchy not correct for path.one. ", new Object[] { "1", "3" }, this.hmap.get("path.one") );
        assertEqualList( "Cross-hierarchy not correct for path.two. ", new Object[] { "2", "4" }, this.hmap.get("path.two") );
        assertEqualList( "Getting 'path' should return a HierarchicalMap, because .properties are not fully hierarchical. ", 
                         new Object[] {"1", "3"}, ((Map) this.hmap.get("path")).get("one") 
                       );
    }

    private void assertEqualList(String msg, Object[] array, Object object) {
        assertTrue( "Value must be a java.util.List. ", object instanceof List);
        List list = (List) object;
        assertEquals( msg+" Length not equal. ", list.size(), array.length);
        for(int i=0; i<list.size(); i++) {
            assertEquals( msg+" Index not equal ["+i+"]", list.get(i), array[i] );
        }
    }

}
