package org.osjava.jndi.util;

import javax.naming.*;
import javax.sql.*;
import java.util.*;
import org.osjava.jndi.util.HierarchicalMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class HierarchicalMapTest extends TestCase {

    private HierarchicalMap hmap;

    public HierarchicalMapTest(String name) {
        super(name);
    }

    public void setUp() {
        this.hmap = new HierarchicalMap(".");
        this.hmap.put("one", "two");
        this.hmap.put("1.2.3", "4");
        this.hmap.put("multiple", "one");
        this.hmap.put("multiple", "two");
        this.hmap.put("delimited.multiple.pair", "one");
        this.hmap.put("delimited.multiple.pair", "two");
    }

    public void tearDown() {
        this.hmap = null;
    }

    public void testGet() {
        assertEquals( "Failed to get prepared value from Map. ", "two", this.hmap.get("one") );
        assertNull( "Should not have found a value in the Map. ", this.hmap.get("two") );
        assertEquals( "Failed to get delimited values from Map. ", "4", this.hmap.get("1.2.3") );
        assertEquals( "Failed to get delimited values from Map. ", "4", ( (Map) ( (Map) this.hmap.get("1") ).get("2")).get("3") );
        assertEqualList( "Failed to get List from Map. ", new Object[] { "one", "two" }, (List) this.hmap.get("multiple") );
        assertEqualList( "Failed to get List from Map. ", new Object[] { "one", "two" }, (List) this.hmap.get("delimited.multiple.pair") );
    }

    private void assertEqualList(String msg, Object[] array, List list) {
        assertEquals( msg+" Length not equal. ", list.size(), array.length);
        for(int i=0; i<list.size(); i++) {
            assertEquals( msg+" Index not equal ["+i+"]", list.get(i), array[i] );
        }
    }

}
