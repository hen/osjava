
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
package org.osjava.atom4j.digester;

import org.xml.sax.Attributes;
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
 * Created on Aug 28, 2003
 * @author lance
 */
public class ContentParser extends DefaultHandler
{
    ContentHandler oldHandler;
    XMLReader xmlReader;
    String closingTag = "</content>";
    String value = "";
    String contentType;
        
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
    
	/** 
     * Must account for multipart/alternative Content,
     * that is, multiple Content nodes nested inside
     * another Content node.   
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
        String full = String.valueOf(ch);

        // if it is multipart it should contain more Content,
        // and thus we have to look for the last <content> in
        // this entry.
        if (contentType != null && contentType.equals("multipart/alternative"))
        {
            // find the first nested Content
            int nextContent = full.indexOf("<content", start);
            int lastIndex = full.indexOf(closingTag, start);
            
            // nextContent cannot begin after next closingTag
            if (nextContent > 0 && nextContent < lastIndex)
            {
                // we found the next tag, get content up to that point
                lastIndex = nextContent;
            }
            
            try
            {
                value = full.substring(start, lastIndex);
            }
            catch (IndexOutOfBoundsException e)
            {
                // there was no lastIndex found - bad
            }
        }
        // it isn't multipart, so find next occurence of closingTag.
        else
        {    
            int endContent = full.indexOf(closingTag, start);
            if (endContent > 0)
                value += full.substring(start, endContent);
        }
        
        xmlReader.setContentHandler(oldHandler);
	}
    
    /*
     * We need to capture the value of the attribute 'type'.
     */
    public void getContentType(Attributes attributes)
        throws SAXException
    {   
        contentType = attributes.getValue("type");
    }

}
