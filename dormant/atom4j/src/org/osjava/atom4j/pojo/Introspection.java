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
 * Created on Aug 29, 2003
 * @author llavandowska
 */
public class Introspection
{
    private String baseURL = "";
    private boolean createEntry;
    private boolean deleteEntry;
    private boolean editEntry;
    private boolean searchEntries;
    private boolean comment;
    private boolean userPrefs;
    private boolean editTemplate;
    private boolean categories;

    public String toString()
    {
        StringBuffer buf = new StringBuffer("<introspection xmlns=\"" + Atom4J.xmlns + "\" > ");
        if (createEntry)
            buf.append("  <create-entry>" + baseURL + "/atom/USER/entry/</create-entry>");
        if (deleteEntry)
            buf.append("  <delete-entry>" + baseURL + "/atom/USER/entry/id</delete-entry>");
        if (editEntry)
            buf.append("  <edit-entry>" + baseURL + "/atom/USER/entry/id</edit-entry>");
        if (userPrefs)
            buf.append("  <user-prefs>" + baseURL + "/atom/USER/prefs</user-prefs>");
        if (searchEntries)
            buf.append("  <search-entries>" + baseURL + "/atom/USER/search</search-entries>");
        if (editTemplate)
            buf.append("  <edit-template>" + baseURL + "/atom/USER/findTemplates</edit-template>");
        if (categories)
            buf.append("  <categories>" + baseURL + "/atom/USER/categories</categories>");
        if (comment)
            buf.append("  <comment>" + baseURL + "/atom/USER/comment/id</comment>");
        buf.append("</introspection>");
        return buf.toString();
    }
    
    /**
     * @return
     */
    public boolean allowsCategories()
    {
        return categories;
    }

    /**
     * @return
     */
    public boolean allowsCreateEntry()
    {
        return createEntry;
    }

    /**
     * @return
     */
    public boolean allowsDeleteEntry()
    {
        return deleteEntry;
    }

    /**
     * @return
     */
    public boolean allowsEditEntry()
    {
        return editEntry;
    }

    /**
     * @return
     */
    public boolean allowsEditTemplate()
    {
        return editTemplate;
    }

    /**
     * @return
     */
    public boolean allowsSearchEntries()
    {
        return searchEntries;
    }

    /**
     * @return
     */
    public boolean allowsUserPrefs()
    {
        return userPrefs;
    }

    /**
     * @param b
     */
    public void setCategories(boolean b)
    {
        categories= b;
    }

    /**
     * @param b
     */
    public void setCreateEntry(boolean b)
    {
        createEntry= b;
    }

    /**
     * @param b
     */
    public void setDeleteEntry(boolean b)
    {
        deleteEntry= b;
    }

    /**
     * @param b
     */
    public void setEditEntry(boolean b)
    {
        editEntry= b;
    }

    /**
     * @param b
     */
    public void setEditTemplate(boolean b)
    {
        editTemplate= b;
    }

    /**
     * @param b
     */
    public void setSearchEntries(boolean b)
    {
        searchEntries= b;
    }

    /**
     * @param b
     */
    public void setUserPrefs(boolean b)
    {
        userPrefs= b;
    }

    /**
     * @return
     */
    public String getBaseURL()
    {
        return baseURL;
    }

    /**
     * @param string
     */
    public void setBaseURL(String string)
    {
        baseURL= string;
    }

    /**
     * @return
     */
    public boolean allowsComment()
    {
        return comment;
    }

    /**
     * @param b
     */
    public void setComment(boolean b)
    {
        comment= b;
    }

}
