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
import org.osjava.atom4j.pojo.Content;
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
public class MultipartFeedTest extends TestCase
{
    String fileName = "Atom03_Multipart.xml";
    private InputStream stream;
    private Feed feed;
    
    public void setUp() throws IOException
    {
        stream = MultipartFeedTest.class.getClassLoader().getResourceAsStream(fileName);
        assertNotNull("Unable to load " + fileName + ", make sure it is on Classpath.", stream);
        FeedReader reader = new FeedReader(stream);
        reader.parse();
        stream.close();
        feed = reader.getFeed();
    }
    
    public MultipartFeedTest()
    {
        super();
    }

    public MultipartFeedTest(String arg0)
    {
        super(arg0);
    }
    
    public void testFeed()
    {
        assertNotNull(feed);
        assertEquals("dive into mark", feed.getTitle().getText());

        assertNotNull(feed.getLinks());
        Link link = (Link)((ArrayList)feed.getLinks()).get(0);
        assertEquals("text/html", link.getType());
        assertEquals("http://diveintomark.org/", link.getHref());
        assertEquals(Link.ALTERNATE, link.getRel());
        
        assertEquals(Content.XML, feed.getTagline().getMode());
        assertEquals("A lot of effort went into making this effortless", 
                     feed.getTagline().getText());

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
        
        assertEquals(2, entry.getContributors().size());
        
        assertEquals("The Atom 0.3 snapshot is out.  Here are some sample feeds.", 
                     entry.getSummary().getText());

        // should be multipart
        assertNotNull(entry.getContent());
        assertEquals(Content.MULTIPART, entry.getContent().getMimeType());
        assertEquals(2, entry.getContent().getMultipart().size());
        assertEquals(firstMultipart, 
            ((Content)entry.getContent().getMultipart().iterator().next()).getText().trim());
        
        //TODO Add more asserts. Should these be broken up into
        // multiple test methods?
        
        //System.out.println(feed.toString());
        //System.out.println("|"+entry.getContent().getText()+"|");
    }
    private static String firstMultipart = "The Atom 0.3 snapshot is out.  Changes from the 0.2 snapshot [http://diveintomark.org/archives/2003/08/05/atom02]: (1) MAY contain feed/info.  Free-form info statement that describes the feed format.  More explanation of info [http://www.shellen.com/sandbox/atom-info-proposal.html].  (2) Expanded link syntax.  All links now include rel, type, and href attributes.  (3) Expanded content model.  feed/title, feed/tagline, feed/info, feed/copyright, entry/title, and entry/summary can now contain arbitrary data (such as HTML).  You must specify the data type in the type attribute, and (for non-XML formats) the escaping mode in the mode attribute.  (4) feed/generator now has url and version attributes.  The child text is now the human-readable toolkit name.  (5) several other changes not listed here.";

}
