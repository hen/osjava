/*
 * Created on Aug 22, 2003
 */
package org.osjava.atom4j.pojo;

/**
 * @author llavandowska
 */
public class Content
{
    private String mimeType = "application/xhtml+xml";
    private String language = "en-us";
    private String text;

    
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

}
