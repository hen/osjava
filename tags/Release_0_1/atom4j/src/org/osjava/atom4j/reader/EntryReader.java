/*
 * Created on Aug 22, 2003
 */
package org.osjava.atom4j.reader;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.osjava.atom4j.digester.ContentRule;
import org.osjava.atom4j.pojo.Entry;

/**
 * The intent of this class is for it to be used to read in the  
 * XML posted to the Atom API for inserting & updating Atom entries.
 * 
 * http://bitworking.org/news/AtomAPI_URIs
 * http://bitworking.org/rfc/draft-gregorio-07.html
 * 
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
        digester.addObjectCreate("entry",        "org.osjava.atom4j.pojo.Entry");
        digester.addCallMethod("entry/title",    "setTitle", 0);
        //digester.addCallMethod("entry/subTitle", "setSubtitle", 0);
        digester.addCallMethod("entry/summary",   "setSummary", 0);
        digester.addCallMethod("entry/issued",    "setIssued", 0);
        digester.addCallMethod("entry/created",   "setCreated", 0);
        digester.addCallMethod("entry/modified",  "setModified", 0);
        digester.addCallMethod("entry/link",      "setLink", 0);
        digester.addCallMethod("entry/comment",   "setComment", 0);
        digester.addCallMethod("entry/id",        "setId", 0);
                
        digester.addObjectCreate("entry/author",       "org.osjava.atom4j.pojo.Author");
        digester.addCallMethod("entry/author/name",    "setName", 0);
        digester.addCallMethod("entry/author/url",     "setUrl", 0);
        digester.addCallMethod("entry/author/email",   "setEmail", 0);
        digester.addSetNext("entry/author", "setAuthor", "org.osjava.atom4j.pojo.Author");
        
        digester.addObjectCreate("entry/content",   "org.osjava.atom4j.pojo.Content");
        digester.addSetProperties("entry/content", "type",     "mimeType");
        digester.addSetProperties("entry/content", "xml:lang", "language");
        digester.addRule("entry/content", new ContentRule());
        digester.addSetNext("entry/content", "setContent", "org.osjava.atom4j.pojo.Content");

        digester.addSetNext("entry", "addEntry",  "org.osjava.atom4j.pojo.Entry");
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
    
/*
 * These convenience values/methods should move elsewhere eventually.
 */
    
    // This is a hack, but it seems to work
    public static SimpleDateFormat READ_ISO8601_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // doesn't work with the Z on the end
    public static SimpleDateFormat WRITE_ISO8601_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static String formatIso8601(Date date)
    {
        if (date == null) return "";
        
        // Add a colon 2 chars before the end of the string
        // to make it a valid ISO-8601 date.         
        String str = WRITE_ISO8601_FORMAT.format(date);
        return str;
        /*
        StringBuffer sb = new StringBuffer();
        sb.append( str.substring(0,str.length()-2) );
        sb.append( ":" );
        sb.append( str.substring(str.length()-2) );
        return sb.toString();
        */
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

}
