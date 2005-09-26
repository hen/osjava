package com.generationjava.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.generationjava.lang.ClassW;
import java.util.Map;

public class MultiKeyedMapTest extends TestCase {

    public MultiKeyedMapTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 

    public void testMultiKeyes() {
        Map map = new MultiKeyedMap();
        String[] strs = new String[] { "com", "generationjava" };
        map.put(strs, "GJ!");
        strs = new String[] { "com", "smallcogs" };
        map.put(strs, "STEVE!");
        strs = new String[] { "com", "flamefew", "www" };
        map.put(strs, "Ff-www");
        strs = new String[] { "com", "flamefew", "log" };
        map.put(strs, "Ff-log");
        map.put("fred", "foo");

        assertEquals("STEVE!", map.get(new String[] { "com", "smallcogs" } ) );
        assertEquals("Ff-www", ((Map)map.get(new String[] { "com", "flamefew" } )).get("www") );
        assertEquals("foo", map.get("fred"));
        assertEquals("foo", map.get(new String[] { "fred" } ));
        assertEquals(true, map.containsValue("Ff-log"));
        assertEquals(false, map.containsValue("Ff-dns"));
    }
}
