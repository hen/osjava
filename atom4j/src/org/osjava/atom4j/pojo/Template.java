/*
 * Created on Aug 29, 2003
 */
package org.osjava.atom4j.pojo;

/**
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
     * This will be used by /atom/USER/template/id in order
     * to return the contents of the template.
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
