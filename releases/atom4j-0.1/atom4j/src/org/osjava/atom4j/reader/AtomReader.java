/* 
 * Created on Aug 23, 2003
 */
package org.osjava.atom4j.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
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
    
    protected abstract void initDigester();
        
    /**
     * Parse the InputStream according the rules set up in init().
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
            // huh? IOException on a String?
            logger.error(e);
        }
        catch (SAXException e)
        {
            logger.error(e);
        }
    }
}
