/*
 * Created on Aug 22, 2003
 */
package org.osjava.atom4j.pojo;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.Atom4J;
import org.osjava.atom4j.reader.EntryReader;

/**
 * @author llavandowska
 */
public class Entry
{
    private String title;
    private String summary;
    private Author author = new Author();  // don't want to risk this being null
    
    private Date issued;
    private Date created;
    private Date modified;
    
    private String link;
    private String id;
    private String comment; // url for posting comments
    private Content content = new Content(); // don't want to risk this being null   
    
    private static Log logger = 
       LogFactory.getFactory().getInstance(Entry.class);    
    
    public void setIssued(String str)
    {
        try
        {
            issued = EntryReader.READ_ISO8601_FORMAT.parse(str);
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
            created = EntryReader.READ_ISO8601_FORMAT.parse(str);
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
            modified = EntryReader.READ_ISO8601_FORMAT.parse(str);
        }
        catch (ParseException e)
        {
            logger.warn( e );
        }
    }
    
    /**
     * @return
     */
    public Author getAuthor()
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
    public String getLink()
    {
        return link;
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
    public String getSummary()
    {
        return summary;
    }

    /**
     * @return
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param author
     */
    public void setAuthor(Author author)
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
     * @param string
     */
    public void setLink(String string)
    {
        link= string;
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
    public void setSummary(String string)
    {
        summary= string;
    }

    /**
     * @param string
     */
    public void setTitle(String string)
    {
        title= string;
    }

    /**
     * @return
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param string
     */
    public void setComment(String string)
    {
        comment= string;
    }
    
    public static String noNull(String str)
    {
        if (str == null) return "";
        return str;
    }

    /**
     * Pretty test output.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        buf.append("<entry xmlns=\"" + Atom4J.xmlns + "\" >\n");
        buf.append("    <title>" + this.getTitle() + "</title>\n");
        buf.append("    <summary>A very boring entry...</summary>\n");
        //buf.append("    <subtitle>" + this.getSummary()+ "</subtitle>\n");
     
        buf.append("    <author>\n");
        if (StringUtils.isNotEmpty(getAuthor().getName()))  
            buf.append("      <name>" + getAuthor().getName() + "</name>\n");
        if (StringUtils.isNotEmpty(getAuthor().getUrl()))   
            buf.append("      <homepage>" + getAuthor().getUrl() + "</homepage>\n");
        if (StringUtils.isNotEmpty(getAuthor().getEmail())) 
            buf.append("      <email>" + getAuthor().getEmail() + "</weblog>\n");
        buf.append("    </author>\n");
    
        buf.append("    <issued>" + EntryReader.formatIso8601(this.getIssued()) + "</issued>\n");
        buf.append("    <created>" + EntryReader.formatIso8601(this.getCreated()) + "</created>\n");
        buf.append("    <modified>" + EntryReader.formatIso8601(this.getModified()) + "</modified>\n");
    
        buf.append("    <link>" + noNull( getLink() ) + "</link>\n");
        buf.append("    <comment>" + noNull( getComment() ) + "</comment>\n");
        buf.append("    <id>" + noNull( getId() ) + "</id>\n");
     
        buf.append("    <content type=\"" + noNull( getContent().getMimeType() ) + "\"");
        buf.append(" xml:lang=\"" + noNull( getContent().getLanguage() ) + "\">");
        buf.append(  noNull( this.getContent().getText() ) );
        buf.append("</content>\n");
        buf.append("</entry>\n");
        
        return buf.toString();
    }
}
