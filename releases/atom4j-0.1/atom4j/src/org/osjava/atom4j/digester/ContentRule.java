/*
 * Created on Aug 28, 2003
 *
 */
package org.osjava.atom4j.digester;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import org.osjava.atom4j.pojo.Content;

/**
 * @author lance
 */
public class ContentRule extends Rule
{
    ContentParser saxValueParser;
    private static String value = "";
    
	public void begin(String namespace, String name, Attributes attributes)
		throws java.lang.Exception
	{
		// Set up a new content handler to work in these situations..
		// The content handler will be responsible for setting back the current
		// content handler so we can resume the normally scheduled program 
        ContentHandler oldHandler =
			getDigester().getXMLReader().getContentHandler();
		saxValueParser =
			new ContentParser(oldHandler, getDigester().getXMLReader(), null);
		this.getDigester().getXMLReader().setContentHandler(saxValueParser);
        
		super.begin(namespace, name, attributes);
	}

	public void end(String namespace, String name) throws java.lang.Exception
	{
		Content content = (Content) digester.peek();
		
		content.setText( saxValueParser.getValue());
	}
    
    
}
