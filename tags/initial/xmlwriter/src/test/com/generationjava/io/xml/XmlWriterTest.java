package com.generationjava.io.xml;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.io.IOException;

public class XmlWriterTest extends TestCase {

    public XmlWriterTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSimpleXmlWriter() throws IOException {

        // test just an empty tag
        StringWriter sw = new StringWriter();
        XmlWriter xw = new SimpleXmlWriter(sw);
        xw.writeEntity("unit");
        xw.endEntity();
        xw.close();
        assertEquals("SimpleXmlWriter not outputting test-1 correctly ",
                     sw.toString(),
                     "<unit/>");


        sw = new StringWriter();
        xw = new SimpleXmlWriter(sw);
        writeTest2(xw);
        xw.close();
        assertEquals("SimpleXmlWriter not outputting test-2 correctly ",
                     sw.toString(), getTest2Output() );
    }

    public void testXmlEncXmlWriter() throws IOException {
        // test just an empty tag
        StringWriter sw = new StringWriter();
        XmlWriter xw = new XmlEncXmlWriter(sw);
        xw.writeEntity("unit");
        xw.endEntity();
        xw.close();
        assertEquals("XmlEncXmlWriter not outputting test-1 correctly ",
                     sw.toString(),
                     "<unit/>");

        StringWriter sw2 = new StringWriter();
        xw = new XmlEncXmlWriter(sw2);
        writeTest2(xw);
        xw.close();
        assertEquals("XmlEncXmlWriter not outputting test-2 correctly ",
                     sw2.toString(), getTest2Output() );
    }

    private void writeTest2(XmlWriter xw) throws IOException {
        xw.writeXmlVersion();
        xw.writeComment("Unit test");
        xw.writeEntity("unit");
        xw.writeEntity("test").writeAttribute("order","1").writeAttribute("language","english").endEntity();
        xw.writeEntity("again").writeAttribute("order","2").writeAttribute("language","english").writeEntity("andAgain").endEntity().endEntity();
        xw.endEntity();
    }

    private String getTest2Output() {
        return "<?xml version=\"1.0\"?>" +
               "<!--Unit test-->" +
               "<unit>" +
               "<test order=\"1\" language=\"english\"/>" +
               "<again order=\"2\" language=\"english\"><andAgain/></again>" +
               "</unit>";
    }

}
