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
package org.osjava.atom4j.pojo;

import org.osjava.atom4j.Atom4J;

/**
 * @deprecated
 * 
 * Created on Aug 23, 2003
 * @author llavandowska
 */
public class UserPreferences
{
    private String name;
    private String id;
    private String email;
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer("<userprefs xmlns=\"" + Atom4J.xmlns + "\">\n");
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
