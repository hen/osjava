/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Simple-JNDI nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

// XmlProperties.java
package org.osjava.jndi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Enumeration;

import java.util.Map;

import com.generationjava.io.xml.XMLParser;
import com.generationjava.io.xml.XMLNode;

/**
 * Wraps the Genjava gj-xml parser behind a Parser facade.
 */
public class XmlParser implements Parser {

    private String delimiter = ".";

    public XmlParser() {
        super();
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void parse(InputStream in, Map map) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        this.parse( reader, map );
        reader.close();
    }

    // TODO: Decide if parse could just throw the root at add.
    private void parse(Reader reader, Map map) throws IOException {
        XMLParser parser = new XMLParser();
        XMLNode root = parser.parseXML(reader);
        Enumeration enum = root.enumerateNode();
        while(enum.hasMoreElements()) {
            XMLNode node = (XMLNode)enum.nextElement();
            if(!node.isTag()) { continue; }
//            add("", node);
if(org.osjava.jndi.PropertiesContext.DEBUG)            System.err.println("[XML]Adding: "+node+" to "+root.getName()+getDelimiter());
            add(root.getName(), node, map);
        }
        Enumeration attrs = root.enumerateAttr();
        if(attrs != null) {
            while(attrs.hasMoreElements()) {
                String attr = (String)attrs.nextElement();
                map.put( root.getName()+getDelimiter()+attr, root.getAttr(attr));
if(org.osjava.jndi.PropertiesContext.DEBUG)                System.err.println("[XML]Attr: "+(root.getName()+getDelimiter()+attr) +":"+root.getAttr(attr));
            }
        }
    }
    
    private void add(String level, XMLNode node, Map map) {
if(org.osjava.jndi.PropertiesContext.DEBUG)        System.err.println("[XML]Adding for level: "+level);
        Enumeration attrs = node.enumerateAttr();
        if(attrs != null) {
            while(attrs.hasMoreElements()) {
                String attr = (String)attrs.nextElement();
                map.put( level+getDelimiter()+node.getName()+getDelimiter()+attr, node.getAttr(attr));
if(org.osjava.jndi.PropertiesContext.DEBUG)                System.err.println("[XML]Attr: "+(level+getDelimiter()+node.getName()+getDelimiter()+attr) +":"+node.getAttr(attr));
            }
        }
        Enumeration nodes = node.enumerateNode();
        if(nodes != null) {
            level = level+getDelimiter()+node.getName();
            while(nodes.hasMoreElements()) {
                XMLNode subnode = (XMLNode)nodes.nextElement();
                if( subnode.isTextNode() ) {
    if(org.osjava.jndi.PropertiesContext.DEBUG)        System.err.println("[XML]Adding value: "+node.getValue());
                    map.put( level+getDelimiter()+node.getName(), node.getValue());
                }
                if(subnode.isTag()) {
                    // temporary pending research into XMLNode parsing:
                    if(!"".equals(subnode.getName())) {
    if(org.osjava.jndi.PropertiesContext.DEBUG)                    System.err.println("[XML]Walking children: "+node.getName());
    if(org.osjava.jndi.PropertiesContext.DEBUG)                    System.err.println("[XML]on: "+level);
                        add(level, subnode, map);
                    }
                }
            }
        }
    }
    
}
