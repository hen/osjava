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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created on Aug 22, 2003
 * @author llavandowska
 */
public class Content
{
    public static final String XML = "xml";
    public static final String BASE64 = "base64";
    public static final String ESCAPED = "escaped";
    public static final String MULTIPART = "multipart/alternative";
    
    private String mimeType = "text/plain";
    private String language = "en-us";
    private String text;
    private String mode = "xml";
    private Collection multipart;
    
    /**
     * @return
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * @return
     */
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * @return
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param string
     */
    public void setLanguage(String string)
    {
        language= string;
    }

    /**
     * @param string
     */
    public void setMimeType(String string)
    {
        mimeType= string;
    }

    /**
     * @param string
     */
    public void setText(String string)
    {
        text= string;
    }

    /**
     * @return Returns the mode.
     */
    public String getMode()
    {
        return this.mode;
    }
    
    /**
     * @param mode The mode to set.
     */
    public void setMode(String mode)
    {
        this.mode = mode;
    }
    
    public void addContent(Content content) 
    {
        if (multipart == null) multipart = new ArrayList();
        multipart.add(content);
    }
    
    /**
     * @return
     */
    public Collection getMultipart()
    {
        return multipart;
    }

    /**
     * @param collection
     */
    public void setMultipart(Collection collection)
    {
        multipart = collection;
    }

    /**
     * Since Content can represent several elements of an Atom
     * feed, it is necessary to abstract the generation of the
     * tag.  The actual tag name is taken as an argument.
     * 
     * @param string tag - the label of the tag to generate
     */
    public String toString(String tag)
    {
        StringBuffer buf = new StringBuffer("<").append(tag);
        if (mimeType != null) buf.append(" type=\"").append(mimeType).append("\"");
        if (language != null) buf.append(" xml:lang=\"").append(language).append("\"");
        if (mode != null) buf.append(" mode=\"").append(mode).append("\"");
        buf.append(">");
        if (multipart != null)
        {
            if (text != null) buf.append(text);
            java.util.Iterator it = multipart.iterator();
            while (it.hasNext())
            {
                Content _content = (Content)it.next();
                buf.append(_content.toString("content"));
            }       
        }
        else if (text != null)
        {
            buf.append(text);
        }
        buf.append("</").append(tag).append(">\n");
        return buf.toString();        
    }

}
