/*
 * Created on Aug 23, 2003
 */
package org.osjava.atom4j.reader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.osjava.atom4j.Atom4J;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author llavandowska
 */
public class PrefsReaderTest extends TestCase
{
    String inputXML = 
        "<userprefs xmlns=\"" + Atom4J.xmlns + "\">\n" +
        "    <name>Reilly</name>\n" +
        "    <id>1234</id>\n" +
        "    <email>reilly@example.org</email>\n" +
        "</userprefs>\n";

    /**
     * @return
     */
    public static Test suite()
    {
        return new TestSuite(PrefsReaderTest.class);
    }   
    
    public void testPrefsReaderXML()
    {
        PrefsReader reader = new PrefsReader(inputXML);
        assertEquals(inputXML, reader.toString()); 
    }
    
    public void testPrefsReaderInputStream()
    {
        InputStream input = new ByteArrayInputStream(inputXML.getBytes());  
        PrefsReader reader = new PrefsReader(input);
        assertEquals(inputXML, reader.toString()); 
    }

}
