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
package org.osjava.atom4j.pojo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.Atom4J;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created on Feb 14, 2004
 * @author Lance
 */
public class Feed
{
    private String id;
    private Collection entries;
    private String version;
    private String lang;
    private Content title;
    private Person author;
    private Collection contributors;
    private Content tagline;
    private Generator generator;
    private Content copyright;
    private Content info;
    private Date modified;
    private Collection links;
        // Link for POSTing new Entry - service.post
        // Link for GETting list of services & feed link - service.feed
        // Link to HTML page - alternate
        // Link for the XML feed, can contain next/prev/start links - feed
    
    private static Log logger = 
       LogFactory.getFactory().getInstance(Feed.class);  

    public void addEntry(Entry entry)
    {
        if (entries == null) entries = new ArrayList();
        entries.add(entry);
    }

    public Collection getEntries()
    {
        return entries;
    }

    public void setEntries(Collection collection)
    {
        entries = collection;
    }

    public void addContributor(Person person)
    {
        if (contributors == null) contributors = new ArrayList();
        contributors.add(person);
    }

    public Collection getContributors()
    {
        return contributors;
    }

    public void setContributors(Collection persons)
    {
        contributors = persons;
    }

    public void addLink(Link link)
    {
        if (links == null) links = new ArrayList();
        links.add(link);
    }

    public Collection getLinks()
    {
        return links;
    }

    public void setLinks(Collection _links)
    {
        links = _links;
    }

    public Person getAuthor()
    {
        return author;
    }

    public Content getCopyright()
    {
        return copyright;
    }

    public Generator getGenerator()
    {
        return generator;
    }

    public String getId()
    {
        return id;
    }

    public Content getInfo()
    {
        return info;
    }

    public String getLang()
    {
        return lang;
    }

    public Date getModified()
    {
        return modified;
    }

    public Content getTagline()
    {
        return tagline;
    }

    public Content getTitle()
    {
        return title;
    }

    public String getVersion()
    {
        return version;
    }

    public void setAuthor(Person person)
    {
        author = person;
    }

    public void setCopyright(Content content)
    {
        copyright = content;
    }

    public void setGenerator(Generator generator)
    {
        this.generator = generator;
    }

    public void setId(String string)
    {
        id = string;
    }

    public void setInfo(Content content)
    {
        info = content;
    }

    public void setLang(String string)
    {
        lang = string;
    }

    public void setModified(Date date)
    {
        modified = date;
    }

    public void setTagline(Content content)
    {
        tagline = content;
    }

    public void setTitle(Content content)
    {
        title = content;
    }

    public void setVersion(String string)
    {
        version = string;
    }
    
    /*
     * Set modified with a String.
     */
    public void setModified(String str)
    {
        try
        {
            modified = Atom4J.READ_ISO8601_FORMAT.parse(str);
        }
        catch (ParseException e)
        {
            logger.warn( e );
        }
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer("<feed xmlns=\"" + Atom4J.xmlns + "\"");
        if (version != null) buf.append(" version=\"").append(version).append("\"");
        if (lang != null) buf.append(" xml:lang=\"").append(lang).append("\"");
        buf.append(">\n");

        if (id != null) buf.append("    ").append(Atom4J.simpleTag(id, "id"));
        if (title != null) buf.append("    ").append(title.toString("title"));
        if (tagline != null) buf.append("    ").append(tagline.toString("tagline"));
        if (info != null) buf.append("    ").append(info.toString("info"));
        if (copyright != null) buf.append("    ").append(copyright.toString("copyright"));
        if (generator != null) buf.append("    ").append(generator.toString());
        if (modified != null)
            buf.append("    ").append(Atom4J.dateTag(modified, "modified"));
        
        // generate Author and Contributor tags
        if (author != null) 
        {
            buf.append("    ").append(author.toString("author"));
        }
        if (contributors != null)
        {
            java.util.Iterator it = contributors.iterator();
            while (it.hasNext())
            {
                Person _person = (Person)it.next();
                buf.append("    ").append(_person.toString("contributor"));
            }
        }

        if (links != null)
        {
            java.util.Iterator it = links.iterator();
            while (it.hasNext())
            {
                Link _link = (Link)it.next();
                buf.append("    ").append(_link.toString());
            }
        }
        
        // generate Entries
        if (entries != null)
        {
            java.util.Iterator it = entries.iterator();
            while (it.hasNext())
            {
                Entry _entry = (Entry)it.next();
                buf.append("    ").append(_entry.toString());
            }
        }
        
        buf.append("</feed>");
        return buf.toString();
    }
}
