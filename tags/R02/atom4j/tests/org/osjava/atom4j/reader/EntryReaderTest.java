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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.osjava.atom4j.Atom4J;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created on Aug 23, 2003
 * @author llavandowska
 */
public class EntryReaderTest extends TestCase
{
    String entryXML = "<?xml version=\"1.0\" encoding='iso-8859-1'?>" +
        "<entry xmlns=\"" + Atom4J.xmlns + "\" > " + 
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
        "<content type=\"application/xhtml+xml\" xml:lang=\"en-us\">" + 
        "Start here<p xmlns=\"...\">Hello, <em>weblog</em> world! 2 &lt; 4!</p>  " + 
        "</content>   " + 
        "</entry>";
        
    String expected = 
        "<entry xmlns=\"" + Atom4J.xmlns + "\" >\n" + 
        "    <title>My First Entry</title>\n" + 
        "    <summary>A very boring entry...</summary>\n" + 
        //"    <subtitle>In which a newbie learns to blog...</subtitle>\n" + 
        "    <author>\n" + 
        "      <name>Bob B. Bobbington</name>\n" + 
        "    </author>\n" + 
        "    <issued>2003-02-05T12:29:29Z</issued>\n" + 
        "    <created>2003-02-05T14:10:58Z</created>\n" + 
        "    <modified>2003-02-05T14:10:58Z</modified>\n" + 
        "    <link>http://example.org/reilly/2003/02/05#My_First_Entry</link>\n" +
        "    <comment></comment>\n" +
        "    <id>urn:example.org:reilly:1</id>\n" + 
        "    <content type=\"application/xhtml+xml\" xml:lang=\"en-us\">" + 
        "Start here<p xmlns=\"...\">Hello, <em>weblog</em> world! 2 &lt; 4!</p>  " + 
        "</content>\n" + 
        "</entry>\n";
    
    public static Test suite()
    {
        return new TestSuite(EntryReaderTest.class);
    }
    
    public void testEntryReaderXML()
    {
        EntryReader reader = new EntryReader(entryXML);
        assertEquals(expected, reader.toString()); 
    }
    
    public void testEntryReaderInputStream()
    {
        InputStream input = new ByteArrayInputStream(entryXML.getBytes());  
        EntryReader reader = new EntryReader(input);
        assertEquals(expected, reader.toString()); 
    }

}
