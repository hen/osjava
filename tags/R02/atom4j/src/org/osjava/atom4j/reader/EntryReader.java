/*
 *   Copyright 2003-2004 Lance Lavandowska
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.osjava.atom4j.reader;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.pojo.Entry;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The intent of this class is for it to be used to read in the  
 * XML posted to the Atom API for inserting & updating Atom entries.
 * 
 * Created on Aug 22, 2003
 * @author llavandowska
 */
public class EntryReader extends AtomReader
{    
    private Collection entries = new LinkedList();
        
    private static Log logger = 
       LogFactory.getFactory().getInstance(EntryReader.class);
    
    protected void initDigester()
    {
        digester = new Digester();
        digester.push(this);
        //digester.setDebug(0);
        digester.setValidating(false);
        configureEntryDigester(digester, "");
    }

    /**
     * Instantiate EntryReader from a String containing XML.
     * 
     * @param xml
     */
    public EntryReader(String xml)
    {       
        super(xml);
        parse();
    }
    
    /**
     * Instantiate EntryReader from an InputStream.
     * 
     * @param xml
     */   
    public EntryReader(InputStream in)
    {
        super(in);
        parse();
    }
    
    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }
    
    /**
     * @return
     */
    public Collection getEntries()
    {
        return entries;
    }

    /**
     * @param collection
     */
    public void setEntries(Collection collection)
    {
        entries= collection;
    }
    
    /**
     * Pretty test output.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        Iterator iter = entries.iterator();
        while (iter.hasNext())
        {
            Entry entry = (Entry)iter.next();
            buf.append(entry.toString());
        }
        return buf.toString();
    }
    
    public static void main(String[] args)
    {
        String entryXML = "<?xml version=\"1.0\" encoding='iso-8859-1'?>" +
            "<entry xmlns=\"http://example.com/newformat#\" > " + 
            "<title>My First Entry</title>  " + 
            "<subtitle>In which a newbie learns to blog...</subtitle>  " + 
            "<summary>A very boring entry...</summary>  " + 
            "" + 
            "<author>  " + 
            "  <name>Bob B. Bobbington</name>  " + 
            "</author>  " + 
            "" + 
            "<issued>2003-02-05T12:29:29</issued>  " + 
            "<created>2003-02-05T14:10:58Z</created>  " + 
            "<modified>2003-02-05T14:10:58Z</modified>  " + 
            ""+
            "<link>http://example.org/reilly/2003/02/05#My_First_Entry</link> " + 
            "<id>urn:example.org:reilly:1</id> " + 
            "" +
            "<content type=\"application/xhtml+xml\" xml:lang=\"en-us\">   " + 
            "Testing123<p xmlns=\"...\">Hello, <em>weblog</em> world! 2 &lt; 4!</p>" + 
            "</content>   " + 
            "</entry> ";
        System.out.println(new EntryReader(entryXML));
    }

}
