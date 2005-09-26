package com.generationjava.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.beans.Beans;

import java.util.Map;

public class FQMapTest extends TestCase {

    public FQMapTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 

    public void testFQ() {
        Map map = new FQMap();
        map.put("hey","you");
        map.put("well.you","hmmm");
        map.put(this,this);

        assertEquals("you", map.get("hey"));
        assertEquals(this, map.get(this));
        assertEquals("hmmm", map.get("well.you"));
        assertEquals(true, Beans.isInstanceOf(map.get("well"), java.util.Map.class));
        assertEquals(3, map.size());
    }
}
