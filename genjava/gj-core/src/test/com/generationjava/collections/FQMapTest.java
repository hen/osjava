package com.generationjava.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.generationjava.lang.ClassW;
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
        assertEquals(true, ClassW.classInstanceOf(map.get("well").getClass(), "java.util.Map"));
        assertEquals(3, map.size());
    }
}
