package com.generationjava.io.xml;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.io.IOException;

public class XmlUtilsTest extends TestCase {

    public XmlUtilsTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEscapingXml() {
        String txt = "'1' is < 5 & > \"-1\"";
        String xml = "&apos;1&apos; is &lt; 5 &amp; &gt; &quot;-1&quot;";
        assertEquals( xml, XmlUtils.escapeXml(txt) );
        assertEquals( txt, XmlUtils.unescapeXml(xml) );
    }

}
