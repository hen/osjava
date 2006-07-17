package com.generationjava.io.xml;

import junit.framework.TestCase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

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
        Iterator iter = node.iterateNode();
        XMLNode child = (XMLNode) iter.next();
        assertEquals("two", child.getName());
        XMLNode child2 = node.getNode("two");
        assertEquals(child, child2);
    }

    public void testSingleIterator() throws IOException {
        use("test.xml");
        Iterator nodeIter = node.iterateNode("two");
        int count = 0;
        while(nodeIter.hasNext()) {
            XMLNode subnode = (XMLNode) nodeIter.next();
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
