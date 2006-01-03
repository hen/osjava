package com.generationjava.io.xml;

import junit.framework.TestCase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;

public class XMLParserTest extends TestCase {

    private XMLNode node;

    public XMLParserTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    private void use(String filename) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        Reader reader = new InputStreamReader(in);
        XMLParser parser = new XMLParser();
        node = parser.parseXML(reader);
        reader.close();
    }

    public void tearDown() throws IOException {
    }

    public void testSimple() throws IOException {
        use("test.xml");
        assertEquals("one", node.getName());
        Enumeration enum = node.enumerateNode();
        XMLNode child = (XMLNode) enum.nextElement();
        assertEquals("two", child.getName());
        XMLNode child2 = node.getNode("two");
        assertEquals(child, child2);
    }

    public void testSingleEnumeration() throws IOException {
        use("test.xml");
        Enumeration nodeEnum = node.enumerateNode("two");
        int count = 0;
        while(nodeEnum.hasMoreElements()) {
            XMLNode subnode = (XMLNode) nodeEnum.nextElement();
            assertEquals("two", subnode.getName());
            count++;
        }
        assertEquals(1, count);
    }

    public void testUnescapeXml() throws IOException {
        use("unescape.xml");
        XMLNode child = node.getNode("two");
        assertEquals("Foo < Bar", child.getValue());
        assertEquals("1>2", child.getAttr("test"));
    }

}
