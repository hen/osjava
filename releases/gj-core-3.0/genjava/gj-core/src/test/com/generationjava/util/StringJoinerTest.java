package com.generationjava.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class StringJoinerTest extends TestCase {

    public StringJoinerTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   StringJoiner(String, String) -> join(String[])

    public void testJoin() {
        StringJoiner joiner = new StringJoiner("<hey><you>?</you><standing>?</standing>", "?");
        assertEquals("<hey><you>one</you><standing>two</standing>", joiner.join(new String[] { "one", "two" }));
        assertEquals("<hey><you>1</you><standing>2</standing>", joiner.join(new String[] { "1", "2" }));
        assertEquals("<hey><you>uno</you><standing>dos</standing>", joiner.join(new String[] { "uno", "dos" }));
    }

}
