package com.generationjava.io.xml;

import junit.framework.TestCase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;

public class XMLParserTest extends TestCase {

    private Reader reader;

    public XMLParserTest(String name) {
        super(name);
    }

    public void setupReader(String filename) {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("/"+filename);
        this.reader = new InputStreamReader(in);
    }

    public void tearDownReader() {
        try {
            this.reader.close();
        } catch(IOException ioe) {
        }
    }

    public void testSimple() {
        try {
            setupReader("test.xml");
            XMLParser parser = new XMLParser();
            XMLNode node = parser.parseXML(this.reader);
            assertEquals("one", node.getName());
            Enumeration enum = node.enumerateNode();
            XMLNode child = (XMLNode) enum.nextElement();
            assertEquals("two", child.getName());
            XMLNode child2 = node.getNode("two");
            assertEquals(child, child2);
            tearDownReader();
        } catch(IOException ioe) {
            fail("IOException: "+ioe);
        }
    }

}
