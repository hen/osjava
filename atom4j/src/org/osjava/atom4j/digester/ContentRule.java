
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

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import org.osjava.atom4j.pojo.Content;

/**
 * Created on Aug 28, 2003
 * @author lance
 */
public class ContentRule extends Rule
{
    ContentParser saxValueParser;
    private String closingTag = null;
    
    public ContentRule() {}

	public void begin(String namespace, String name, Attributes attributes)
		throws java.lang.Exception
	{
        closingTag = "</" + name + ">";
        
		// Set up a new content handler to work in these situations..
		// The content handler will be responsible for setting back the current
		// content handler so we can resume the normally scheduled program
        ContentHandler oldHandler =
			getDigester().getXMLReader().getContentHandler();
		saxValueParser =
			new ContentParser(oldHandler, getDigester().getXMLReader(), closingTag);
		this.getDigester().getXMLReader().setContentHandler(saxValueParser);
        
        // get the value of 'type' if present
        saxValueParser.getContentType(attributes);

		super.begin(namespace, name, attributes);
	}

	public void end(String namespace, String name) throws java.lang.Exception
	{
		Content content = (Content) digester.peek();

		content.setText( saxValueParser.getValue());
	}


}
