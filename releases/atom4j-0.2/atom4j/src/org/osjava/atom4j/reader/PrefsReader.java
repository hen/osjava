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

import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.osjava.atom4j.pojo.UserPreferences;

/**
 * <userprefs xmlns="http://example.com/newformat#" > 
 * <name>Reilly</name>
 * <id>1234</id>
 * <email>reilly@example.org</email>
 * </userprefs>
 * 
 * @deprecated
 * 
 * Created on Aug 23, 2003
 * @author llavandowska
 */
public class PrefsReader extends AtomReader
{        
    private static Log logger = 
       LogFactory.getFactory().getInstance(EntryReader.class);
       
    private UserPreferences userPrefs = null;
    
    protected void initDigester()
    {
        digester = new Digester();
        digester.push(this);
        //digester.setDebug(0);
        digester.setValidating(false);
        digester.addObjectCreate("userprefs",     "org.osjava.atom4j.pojo.UserPreferences");
        digester.addCallMethod("userprefs/name",  "setName", 0);
        digester.addCallMethod("userprefs/id",    "setId", 0);
        digester.addCallMethod("userprefs/email", "setEmail", 0);
        digester.addSetNext("userprefs", "setUserPrefs",  "org.osjava.atom4j.pojo.UserPreferences");
    }
    
    /**
     * Instantiate EntryReader from a String containing XML.
     * 
     * @param xml
     */
    public PrefsReader(String xml)
    {        
        super(xml);
        parse();
    }
    
    /**
     * Instantiate EntryReader from an InputStream.
     * 
     * @param xml
     */   
    public PrefsReader(InputStream input)
    {
        super(input);
        parse();
    }
    
    /**
     * @return UserPreferences
     */
    public UserPreferences getUserPrefs()
    {
        return userPrefs;
    }
    
    public void setUserPrefs(UserPreferences prefs)
    {
        userPrefs = prefs;
    }
    
    public String toString()
    {
        if (userPrefs == null) return "null";
        return userPrefs.toString();
    }

}
