/*
 * Created on Aug 28, 2003
 *
 */
package org.osjava.atom4j.digester;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Grabs all the contents from the beginning of the Rule to the
 * position of the closingTag.  This class can be reused by
 * specifying the closingTag in the constructor (default 
 * is "&lt;/content>".
 * 
 * @author lance
 */
public class ContentParser extends DefaultHandler
{
    ContentHandler oldHandler;
    XMLReader xmlReader;
    String closingTag = "</content>";
    String value = "";
        
    /**
	 * @param oldHandler
	 * @param reader
	 */
	public ContentParser(ContentHandler handler, XMLReader reader, String endTag)
	{
        oldHandler = handler;
        xmlReader = reader;
        if (endTag != null) closingTag = endTag;
	}
    
    public String getValue()
    {
        return value;
    }
    
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException
	{
        String full = String.valueOf(arg0);
        
        int endContent = full.lastIndexOf(closingTag);
        value = full.substring(arg1, endContent);
        // System.out.println("characters():" + value);
        
        xmlReader.setContentHandler(oldHandler);
	}

}
