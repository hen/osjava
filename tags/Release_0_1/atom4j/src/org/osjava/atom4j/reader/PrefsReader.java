/*
 * Created on Aug 23, 2003
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
