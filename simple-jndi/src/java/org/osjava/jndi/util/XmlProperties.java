// XmlProperties.java
package org.osjava.jndi.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Properties;
import java.util.Enumeration;

import com.generationjava.io.xml.XMLParser;
import com.generationjava.io.xml.XMLNode;

/**
 */
public class XmlProperties extends Properties {

    private String delimiter = ".";

    public XmlProperties() {
        super();
    }

    public XmlProperties(Properties props) {
        super(props);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void load(InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        this.load( reader );
        reader.close();
    }

    public void load(Reader reader) throws IOException {
        XMLParser parser = new XMLParser();
        XMLNode root = parser.parseXML(reader);
        Enumeration enum = root.enumerateNode();
        while(enum.hasMoreElements()) {
            XMLNode node = (XMLNode)enum.nextElement();
//            add("", node);
            add(root.getName()+getDelimiter(), node);
        }
    }
    
    public void add(String level, XMLNode node) {
        if( node.getValue() != null ) {
            setProperty( level+node.getName(), node.getValue());
        }
        Enumeration attrs = node.enumerateAttr();
        if(attrs != null) {
            while(attrs.hasMoreElements()) {
                String attr = (String)attrs.nextElement();
                setProperty( level+node.getName()+getDelimiter()+attr, node.getAttr(attr));
//                System.err.println("Prop: "+(level+node.getName()+getDelimiter()+attr) +":"+node.getAttr(attr));
            }
        }
        Enumeration nodes = node.enumerateNode();
        if(nodes != null) {
            while(nodes.hasMoreElements()) {
                XMLNode subnode = (XMLNode)nodes.nextElement();
                add(level+subnode.getName()+getDelimiter(), subnode);
            }
        }
    }
    
    public Object setProperty(String key, String value) {
        return put( key, value );
    }
 
    // has to make sure not to write out any defaults
    public void save(OutputStream outstrm, String header) {
        super.save(outstrm,header);
    }
    // has to make sure not to write out any defaults
    public void store(OutputStream outstrm, String header) throws IOException {
        super.store(outstrm,header);
    }
}
