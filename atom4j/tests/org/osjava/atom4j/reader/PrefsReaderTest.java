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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.osjava.atom4j.Atom4J;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created on Aug 23, 2003
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
