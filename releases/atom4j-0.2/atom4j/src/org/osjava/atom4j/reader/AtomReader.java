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

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.digester.ContentRule;
import org.osjava.atom4j.pojo.Content;
import org.osjava.atom4j.pojo.Entry;
import org.osjava.atom4j.pojo.Link;
import org.osjava.atom4j.pojo.Person;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on Aug 23, 2003
 * @author llavandowska
 */
public abstract class AtomReader
{
    protected Digester digester = null;
    protected InputStream input = null;
    
    protected static Log logger = 
       LogFactory.getFactory().getInstance(AtomReader.class);

    /**
     * Instantiate AtomReader from a String containing XML.
     * 
     * @param xml
     */
    public AtomReader(String xml)
    {        
        input = new ByteArrayInputStream(xml.getBytes());
        initDigester();
    }
    
    /**
     * Instantiate AtomReader from an InputStream.
     * 
     * @param xml
     */   
    public AtomReader(InputStream in)
    {
        input = in;
        initDigester();
    }
    
    /*
     * Set XML to be parsed.
     */
    public void setXML(String xml)
    {
        input = new ByteArrayInputStream(xml.getBytes());
        if (digester == null) initDigester();
    }
    
    /*
     * Set InputStream to be parsed.
     */
    public void setInputStream(InputStream in)
    {
        input = in;
        if (digester == null) initDigester();
    }
    
    protected abstract void initDigester();
        
    /**
     * Parse the InputStream according the rules set up in initDigester().
     * 
     * @param input
     */
    protected void parse()
    { 
        try
        {
            digester.parse(input);
        }
        catch (NullPointerException e)
        {
            if (digester == null)
                logger.error("Digester was null", e);
            if (input == null)
                logger.error("input was null", e);
        }
        catch (IOException e)
        {
            // huh? Why is this happening?
            logger.error(e);
        }
        catch (SAXException e)
        {
            logger.error(e);
        }
    }

    /**
     * Configure digester to parse Entries, based on the parent path.
     * 
     * @param digester
     * @param parent
     */
	protected void configureEntryDigester(Digester digester, String parent )
	{
	    String entryPath = parent + "entry";
	    digester.addObjectCreate(entryPath, Entry.class.getName());
	    
	    configureContentDigester(digester, entryPath + "/title", "setTitle", "</title>");
        
	    digester.addCallMethod(entryPath + "/issued",   "setIssued", 0);
	    digester.addCallMethod(entryPath + "/created",  "setCreated", 0);
	    digester.addCallMethod(entryPath + "/modified", "setModified", 0);
	    digester.addCallMethod(entryPath + "/id",       "setId", 0);
	
	    configureLinkDigester(digester, entryPath + "/link");
	
	    configurePersonDigester(digester, entryPath + "/author", "setAuthor");

        configurePersonDigester(digester, entryPath + "/contributor", "addContributor");
	
	    configureContentDigester(digester, entryPath + "/content", "setContent", "</content>");
        configureContentDigester(digester, entryPath + "/content/content", "addContent", "</content>");
	
	    configureContentDigester(digester, entryPath + "/summary", "setSummary", "</summary>");
	
	    digester.addSetNext(entryPath, "addEntry",  Entry.class.getName());
	}

	/**
     * Configure digester to add a Link as defined by linkPath.
     * 
	 * @param digester
	 * @param linkPath
	 */
	protected void configureLinkDigester(Digester digester, String linkPath)
	{
		digester.addObjectCreate( linkPath, Link.class.getName());
	    digester.addSetProperties(linkPath);
	    digester.addSetNext(linkPath, "addLink", Link.class.getName());
	}

	/**
     * Configure digester to add a Person as defined by personPath, 
     * calling the method defined by setMethod.
     * 
	 * @param digester
	 * @param personPath
     * @param setMethod
	 */
	protected void configurePersonDigester(
            Digester digester, String personPath, String setMethod)
	{
		digester.addObjectCreate(personPath,    Person.class.getName());
	    digester.addCallMethod(personPath + "/name", "setName", 0);
	    digester.addCallMethod(personPath + "/url",  "setUrl", 0);
	    digester.addCallMethod(personPath + "/email","setEmail", 0);
	    digester.addSetNext(personPath, setMethod, Person.class.getName());
	}

	/**
     * Configure digester to add a Content object as defined by contentPath,
     * calling the defined setMethod.  Note: the ContentRule class
     * requires that the closing tag for the Content be defined.
     * 
	 * @param digester
	 * @param contentPath
     * @param setMethod
     * @param closeContent The closign tag for the Content identified
     * by the path.
	 */
	protected void configureContentDigester(
			Digester digester, String contentPath, 
            String setMethod, String closeContent)
	{
		digester.addObjectCreate( contentPath, Content.class.getName());
	    digester.addSetProperties(contentPath, "type",     "mimeType");
	    digester.addSetProperties(contentPath, "xml:lang", "language");
	    digester.addRule(contentPath, new ContentRule(closeContent));
	    digester.addSetNext(contentPath, setMethod, Content.class.getName());
	}

    protected void configureNestedContentDigester(
            Digester digester, String contentPath, 
            String setMethod, String closeContent)
    {
        digester.addObjectCreate( contentPath, Content.class.getName());
        digester.addSetProperties(contentPath, "type",     "mimeType");
        digester.addSetProperties(contentPath, "xml:lang", "language");
        digester.addRule(contentPath, new ContentRule(closeContent));
        digester.addSetTop(contentPath, setMethod, Content.class.getName());
    }
}
