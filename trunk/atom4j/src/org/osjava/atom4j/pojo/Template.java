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

/**
 * Created on Aug 29, 2003
 * @author llavandowska
 */
public class Template
{
    private String baseURL = "";
    
    /**
     * The title of the template is for readability
     * purposes only.
     */
    private String title;
    
    /**
     * Uniquely identifies this Template for your application.
     * This will be used to return the contents of the template.
     */
    private String id;

    public String toString()
    {
        StringBuffer buf = new StringBuffer("<resource>");
        buf.append("  <title>" + title + "</title>");
        buf.append("  <id>" + baseURL + "/atom/USER/template/" + id + "</id>");
        buf.append("</resource>");
        return buf.toString();
    }
    
    /**
     * @return
     */
    public String getBaseURL()
    {
        return baseURL;
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
    public String getTitle()
    {
        return title;
    }

    /**
     * @param string
     */
    public void setBaseURL(String string)
    {
        baseURL= string;
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
    public void setTitle(String string)
    {
        title= string;
    }

}
