/*
 * Created on Aug 23, 2003
 */
package org.osjava.atom4j.pojo;

import org.osjava.atom4j.reader.AtomReader;

/**
 * @author llavandowska
 */
public class UserPreferences
{
    private String name;
    private String id;
    private String email;
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer("<userprefs xmlns=\"" + AtomReader.xmlns + "\">\n");
        buf.append("    <name>Reilly</name>\n");
        buf.append("    <id>1234</id>\n");
        buf.append("    <email>reilly@example.org</email>\n");
        buf.append("</userprefs>\n");
        return buf.toString();
    }
    
    /**
     * @return
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param string
     */
    public void setEmail(String string)
    {
        email= string;
    }

    /**
     * @param string
     */
    public void setId(String string)
    {
        id= string;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name= string;
    }

}
