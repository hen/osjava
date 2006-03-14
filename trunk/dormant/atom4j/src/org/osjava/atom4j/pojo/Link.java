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
public class Link
{
    public static final String ALTERNATE    = "alternate"; // link to HTML format (or other)
    public static final String FEED         = "feed"; // link to Atom Syndication Format
    public static final String START        = "start";
    public static final String NEXT         = "next";
    public static final String PREV         = "prev";
    public static final String SERVICE_POST = "service.post";
    public static final String SERVICE_EDIT = "service.edit";
    public static final String SERVICE_FEED = "service.feed"; // list other service.x links
    
    private String href;
    private String rel;
    private String title;
    private String type = "application/x.atom+xml";

    public String getHref()
    {
        return href;
    }

    public String getRel()
    {
        return rel;
    }

    public String getTitle()
    {
        return title;
    }

    public String getType()
    {
        return type;
    }

    public void setHref(String string)
    {
        href = string;
    }

    public void setRel(String string)
    {
        rel = string;
    }

    public void setTitle(String string)
    {
        title = string;
    }

    public void setType(String string)
    {
        type = string;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer("<link");
        if (rel != null) buf.append(" rel=\"").append(rel).append("\"");
        if (type != null) buf.append(" type=\"").append(type).append("\"");
        if (title != null) buf.append(" title=\"").append(title).append("\"");
        if (href != null) buf.append(" href=\"").append(href).append("\"");
        buf.append("/>\n");
        return buf.toString();
    }
}
