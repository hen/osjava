package com.generationjava.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SimpleAliasTest extends TestCase {

    public SimpleAliasTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   SimpleAlias(Object) -> getAlias()

    public void testNull() {
        SimpleAlias alias = new SimpleAlias(null);
        assertEquals(null, alias.getAlias());
    }

    public void test() {
        SimpleAlias alias = new SimpleAlias("value");
        assertEquals("value", alias.getAlias());
    }

}
