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
import org.osjava.atom4j.digester.ContentRuleSet;
import org.osjava.atom4j.digester.LinkRuleSet;
import org.osjava.atom4j.digester.PersonRuleSet;
import org.osjava.atom4j.pojo.Entry;
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
            logger.info(e);
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

        digester.addRuleSet(new ContentRuleSet(entryPath + "/title", "setTitle"));
        
	    digester.addCallMethod(entryPath + "/issued",   "setIssued", 0);
	    digester.addCallMethod(entryPath + "/created",  "setCreated", 0);
	    digester.addCallMethod(entryPath + "/modified", "setModified", 0);
	    
        digester.addBeanPropertySetter(entryPath + "/id");

        digester.addRuleSet(new LinkRuleSet(entryPath + "/link"));

        digester.addRuleSet(new PersonRuleSet(entryPath + "/author", "setAuthor"));

        digester.addRuleSet(new PersonRuleSet(entryPath + "/contributor", "addContributor"));

        digester.addRuleSet(new ContentRuleSet(entryPath + "/content", "setContent"));
        digester.addRuleSet(new ContentRuleSet(entryPath + "/content/content", "addContent"));

        digester.addRuleSet(new ContentRuleSet(entryPath + "/summary", "setSummary"));
	
	    digester.addSetNext(entryPath, "addEntry",  Entry.class.getName());
	}

}
