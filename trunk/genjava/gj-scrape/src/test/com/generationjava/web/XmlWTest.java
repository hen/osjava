package com.generationjava.web;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class XmlWTest extends TestCase {

    private String testXml = "<bar thing=\"foo\" other='single'>test</bar>";

    public XmlWTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //     XmlW.escapeXml
    //     XmlW.unescapeXml
    //     XmlW.getContent

    public void testEscape() {
        String toEscape = "4 is < 7 & 7 is > 4 but \"7\" and '4' make error. ";
        String expected = "4 is &lt; 7 &amp; 7 is &gt; 4 but &quot;7&quot; and &apos;4&apos; make error. ";
        assertEquals( expected, XmlW.escapeXml( toEscape ) );
    }

    public void testUnescape() {
        String toUnescape = "4 is &lt; 7 &amp; 7 is &gt; 4 but &quot;7&quot; and &apos;4&apos; make error. ";
        String expected = "4 is < 7 & 7 is > 4 but \"7\" and '4' make error. ";
        assertEquals( expected, XmlW.unescapeXml( toUnescape ) );
    }

    public void testSymmetry() {
        String expected = "4 is < 7 & 7 is > 4 but \"7\" and '4' make error. ";
        assertEquals( expected, XmlW.unescapeXml( XmlW.escapeXml( expected ) ) );
    }

    public void testRemove() {
        assertEquals( "FOO", XmlW.removeXml("<foo>FOO</foo>") );
        assertEquals( "FOO", XmlW.removeXml("<foo test='blah'>FOO</foo>") );
        assertEquals( "FOO", XmlW.removeXml("<foo><blah/>FOO</foo>") );
        assertEquals( "FOO", XmlW.removeXml("FOO") );
        assertEquals( "some comment", XmlW.removeXml("<h2>some comment</h2>") );
        assertEquals( "", XmlW.removeXml("") );
    }

    public void testGetAttr() {
        assertEquals( "foo", XmlW.getAttr( this.testXml, "thing") );
    }
    /**
     * @deprecated
     */
    public void testGetAttribute() {
        assertEquals( "foo", XmlW.getAttribute("thing", this.testXml ) );
    }

    public void testGetIndexOpeningTag() {
        assertEquals( 0, XmlW.getIndexOpeningTag("bar", this.testXml) );
    }

    public void testGetIndexClosingTag() {
        assertEquals( 36, XmlW.getIndexClosingTag("bar", this.testXml) );
    }

    public void testGetContent() {
        assertEquals( "test", XmlW.getContent("bar", this.testXml) );
    }

    public void testSingleQuoteAttribute() {
        assertEquals( "single", XmlW.getAttribute("other", this.testXml ) );
    }

}
