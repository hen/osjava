package com.generationjava.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AliasedMapTest extends TestCase {

    public AliasedMapTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   put(Object, Object) / alias(Object, Object) / get(Object)

    // TODO: Need a generic Map test. Check Collections.
    public void test() {
        AliasedMap map = new AliasedMap();
        map.put("Eric","Cartman");
        map.put("Stan","Marsh");
        map.put("Kyle","Browkalski");
        map.put("Kenny","McCormack");
        map.alias("fatkid","Eric");
        map.alias("jewkid","Kyle");
        map.alias("poorkid","Kenny");
        map.alias("yankkid","Stan");

        assertEquals("Cartman", map.get("Eric") );
        assertEquals("Marsh", map.get("Stan") );
        assertEquals("Browkalski", map.get("Kyle") );
        assertEquals("McCormack", map.get("Kenny") );
        assertEquals("Cartman", map.get("fatkid") );
        assertEquals("Browkalski", map.get("jewkid") );
        assertEquals("McCormack", map.get("poorkid") );
        assertEquals("Marsh", map.get("yankkid") );
    }

    public void testNull() {
        AliasedMap map = new AliasedMap();
        assertEquals(null, map.get("foo"));
        map.alias("foo", "bar");
        assertEquals(null, map.get("foo"));
        assertEquals(null, map.get("bar"));
    }

}
