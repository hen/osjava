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

import org.osjava.atom4j.Atom4J;
import org.osjava.atom4j.pojo.Entry;
import org.osjava.atom4j.pojo.Feed;
import org.osjava.atom4j.pojo.Link;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * Created on Feb 14, 2004
 * @author Lance
 */
public class MinimalFeedTest extends TestCase
{
    String fileName = "Atom03_Min.xml";
    private InputStream stream;
    private Feed feed;
    
    public void setUp() throws IOException
    {
        stream = MinimalFeedTest.class.getClassLoader().getResourceAsStream(fileName);
        assertNotNull("Unable to load " + fileName + ", make sure it is on Classpath.", stream);
        FeedReader reader = new FeedReader(stream);
        reader.parse();
        stream.close();
        feed = reader.getFeed();
    }
    
    public MinimalFeedTest()
    {
        super();
    }

    public MinimalFeedTest(String arg0)
    {
        super(arg0);
    }
    
    public void testFeed()
    {
        assertNotNull(feed);
        assertEquals("dive into mark", feed.getTitle().getText());

        //TODO Add more asserts. Should these be broken up into
        // multiple test methods?
    }
    
    public void testEntries()
    {        
        assertNotNull(feed);
        assertNotNull(feed.getEntries());
        assertEquals(1, feed.getEntries().size());
        
        Entry entry = (Entry)feed.getEntries().iterator().next();
        assertNotNull(entry);
        assertEquals("Atom 0.3 snapshot", entry.getTitle().getText());

        assertNotNull(feed.getLinks());
        Link link = (Link)((ArrayList)feed.getLinks()).get(0);
        assertEquals("text/html", link.getType());
        assertEquals(Link.ALTERNATE, link.getRel());
        
        String modified = Atom4J.formatIso8601(entry.getModified());
        assertEquals("2003-12-13T18:30:02Z", modified);

        //TODO Add more asserts. Should these be broken up into
        // multiple test methods?
    }

}
