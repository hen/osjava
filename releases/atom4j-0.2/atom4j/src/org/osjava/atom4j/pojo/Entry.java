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
 * Created on Aug 22, 2003
 * @author llavandowska
 */
public class Entry
{
    private String id;
    private Content title;
    private Content summary;
    private Content content;
    private Person author = new Person();
    private Collection contributors;
    private Collection links;
        // Link for editing GET/PUT/DELETE - service.edit
        // Link for POSTing a Comment - service.post  
        // Links for GETting next/prev/start entries
    
    private Date issued;
    private Date created;
    private Date modified;
        
    private static Log logger = 
       LogFactory.getFactory().getInstance(Entry.class);    
    
    public void setIssued(String str)
    {
        try
        {
            issued = Atom4J.READ_ISO8601_FORMAT.parse(str);
        }
        catch (ParseException e)
        {
            logger.warn( e );
        }
    }
    
    public void setCreated(String str)
    {
        try
        {
            created = Atom4J.READ_ISO8601_FORMAT.parse(str);
        }
        catch (ParseException e)
        {
            logger.warn( e );
        }
    }
    
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
    
    /**
     * @return
     */
    public Person getAuthor()
    {
        return author;
    }

    /**
     * @return
     */
    public Content getContent()
    {
        return content;
    }

    /**
     * @return
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return
     */
    public Date getIssued()
    {
        return issued;
    }

    /**
     * @return
     */
    public Date getModified()
    {
        return modified;
    }

    /**
     * @return
     */
    public Content getSummary()
    {
        return summary;
    }

    /**
     * @return
     */
    public Content getTitle()
    {
        return title;
    }

    /**
     * @param author
     */
    public void setAuthor(Person author)
    {
        this.author= author;
    }

    /**
     * @param content
     */
    public void setContent(Content content)
    {
        this.content= content;
    }

    /**
     * @param date
     */
    public void setCreated(Date date)
    {
        created= date;
    }

    /**
     * @param string
     */
    public void setId(String string)
    {
        id= string;
    }

    /**
     * @param date
     */
    public void setIssued(Date date)
    {
        issued= date;
    }

    /**
     * @param date
     */
    public void setModified(Date date)
    {
        modified= date;
    }

    /**
     * @param string
     */
    public void setSummary(Content content)
    {
        summary= content;
    }

    /**
     * @param string
     */
    public void setTitle(Content content)
    {
        title= content;
    }
        
    public static String noNull(String str)
    {
        if (str == null) return "";
        return str;
    }

    public Collection getContributors()
    {
        return contributors;
    }

    public void setContributors(Collection persons)
    {
        contributors = persons;
    }
    
    public void addContributor(Person person)
    {
        if (contributors == null) contributors = new ArrayList();
        contributors.add(person);
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

    /**
     * Pretty  output.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("<entry xmlns=\"" + Atom4J.xmlns + "\" >\n");

        if (id != null) buf.append("    ").append(Atom4J.simpleTag(id, "id"));
        if (title != null) buf.append("    ").append(title.toString("title"));
        if (summary != null) buf.append("    ").append(summary.toString("summary"));
        
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
        
        // generate date tags
        if (issued != null)
            buf.append("    ").append(Atom4J.dateTag(issued, "issued"));
        if (created != null)
            buf.append("    ").append(Atom4J.dateTag(created, "created"));
        if (modified != null)
            buf.append("    ").append(Atom4J.dateTag(modified, "modified"));
        
        // generate Content
        buf.append("    ").append(content.toString("content"));
        
        // close entry
        buf.append("</entry>\n");
        
        return buf.toString();
    }
}
