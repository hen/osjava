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
 * Created on Aug 22, 2003
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
