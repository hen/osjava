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
 * Created on Feb 14, 2004
 * @author Lance
 */
public class Generator
{
    private String content;
    private String url;
    private String version;

    public String getContent()
    {
        return content;
    }

    public String getUrl()
    {
        return url;
    }

    public String getVersion()
    {
        return version;
    }

    public void setContent(String string)
    {
        content = string;
    }

    public void setUrl(String string)
    {
        url = string;
    }

    public void setVersion(String string)
    {
        version = string;
    }
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer("<generator");
        if (version != null) buf.append(" version=\"").append(version).append("\"");
        if (url != null) buf.append(" url=\"").append(url).append("\"");
        buf.append(">");
        if (content != null) buf.append(content);
        buf.append("</generator>\n");
        return buf.toString();
    }
}
