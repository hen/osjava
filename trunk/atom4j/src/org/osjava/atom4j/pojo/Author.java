/*
 * Created on Aug 22, 2003
 */
package org.osjava.atom4j.pojo;

/**
 * @author llavandowska
 */
public class Author
{
    private String name;
    private String homepage;
    private String weblog;
    /**
     * @return
     */
    public String getHomepage()
    {
        return homepage;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public String getWeblog()
    {
        return weblog;
    }

    /**
     * @param string
     */
    public void setHomepage(String string)
    {
        homepage= string;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name= string;
    }

    /**
     * @param string
     */
    public void setWeblog(String string)
    {
        weblog= string;
    }

}
