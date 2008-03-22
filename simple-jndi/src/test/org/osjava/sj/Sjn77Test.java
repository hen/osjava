package org.osjava.sj;

import junit.framework.TestCase;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Sjn77Test extends TestCase {

    public void testPut() throws NamingException {
        System.setProperty("java.naming.factory.initial", "org.osjava.sj.memory.MemoryContextFactory");
        InitialContext ic = new InitialContext();
        String name = "falez:/hoba";
        String value = "hoba1";
        ic.bind(name, value);
        String result = (String) ic.lookup(name);
        ic.close();
    }
}
