package com.generationjava.web;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class XmlWTest extends TestCase {

    public XmlWTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //     XmlW.removeXml
    //     XmlW.getContent

    public void testRemove() {
        assertEquals( "FOO", XmlW.removeXml("<foo>FOO</foo>") );
        assertEquals( "FOO", XmlW.removeXml("<foo test='blah'>FOO</foo>") );
        assertEquals( "FOO", XmlW.removeXml("<foo><blah/>FOO</foo>") );
        assertEquals( "FOO", XmlW.removeXml("FOO") );
        assertEquals( "", XmlW.removeXml("") );
    }

}
