package com.generationjava.web;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class HtmlWTest extends TestCase {

    private String testHtml = "<bar thing=\"foo\" other='single'>test</bar>";

    public HtmlWTest(String name) {
        super(name);
    }

    public void testEscape() {
        String toEscape = "4 is < 7 & 7 is > 4 but \"7\" and '4' make error. ";
        String expected = "4 is &lt; 7 &amp; 7 is &gt; 4 but &quot;7&quot; and &apos;4&apos; make error. ";
        assertEquals( expected, HtmlW.escapeHtml( toEscape ) );
    }

    /*
    public void testUnescape() {
        String toUnescape = "4 is &lt; 7 &amp; 7 is &gt; 4 but &quot;7&quot; and &apos;4&apos; make error. ";
        String expected = "4 is < 7 & 7 is > 4 but \"7\" and '4' make error. ";
        assertEquals( expected, HtmlW.unescapeHtml( toUnescape ) );
    }

    public void testSymmetry() {
        String expected = "4 is < 7 & 7 is > 4 but \"7\" and '4' make error. ";
        assertEquals( expected, HtmlW.unescapeHtml( HtmlW.escapeHtml( expected ) ) );
    }
    */

    public void testGetAttribute() {
        assertEquals( "foo", HtmlW.getAttribute( this.testHtml, "thing" ) );
        assertEquals( "foo", HtmlW.getAttribute( this.testHtml, "Thing" ) );
    }

    public void testGetIndexOpeningTag() {
        assertEquals( 0, HtmlW.getIndexOpeningTag(this.testHtml, "bar") );
        assertEquals( 0, HtmlW.getIndexOpeningTag(this.testHtml, "BAR") );
    }

    public void testGetIndexClosingTag() {
        assertEquals( 36, HtmlW.getIndexClosingTag(this.testHtml, "bar") );
        assertEquals( 36, HtmlW.getIndexClosingTag(this.testHtml, "bAR") );
    }

    public void testGetContent() {
        assertEquals( "test", HtmlW.getContent(this.testHtml, "bar") );
        assertEquals( "test", HtmlW.getContent(this.testHtml, "bAr") );
    }

    public void testSingleQuoteAttribute() {
        assertEquals( "single", HtmlW.getAttribute(this.testHtml, "other" ) );
        assertEquals( "single", HtmlW.getAttribute(this.testHtml, "oTHer" ) );
    }

}
