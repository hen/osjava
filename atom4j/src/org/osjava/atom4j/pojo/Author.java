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
    private String url = null;
    private String email = null;
    /**
     * @return
     */
    public String getUrl()
    {
        return url;
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
    public String getEmail()
    {
        return email;
    }

    /**
     * @param string
     */
    public void setUrl(String string)
    {
        url= string;
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
    public void setEmail(String string)
    {
        email= string;
    }

}
