/*
 * Created on Jun 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.osjava.atom4j.servlet;

import java.util.List;

import org.osjava.atom4j.pojo.Entry;
import org.osjava.atom4j.pojo.Feed;

/**
 * @author Dave Johnson
 */
public class MockAtomServlet extends AtomServlet
{

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#getFeed(java.lang.String[])
     */
    protected Feed getFeed(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#getEntry(java.lang.String[])
     */
    protected Entry getEntry(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#deleteEntry(java.lang.String[])
     */
    protected void deleteEntry(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#saveNewEntry(org.osjava.atom4j.pojo.Entry)
     */
    protected void saveNewEntry(Entry entry) throws Exception
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#updateEntry(org.osjava.atom4j.pojo.Entry,
     *      java.lang.String[])
     */
    protected void updateEntry(Entry entry, String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#postComment(org.osjava.atom4j.pojo.Entry,
     *      org.osjava.atom4j.pojo.Entry, java.lang.String[])
     */
    protected void postComment(Entry entry, Entry comment, String[] pathInfo)
            throws Exception
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#allEntries(java.lang.String[])
     */
    protected List allEntries(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#getEntryRange(int, int,
     *      java.lang.String[])
     */
    protected List getEntryRange(int startRange, int endRange, String[] pathInfo)
            throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#entryKeywordSearch(java.lang.String[])
     */
    protected List entryKeywordSearch(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#getLatestEntries(java.lang.String,
     *      int)
     */
    protected List getLatestEntries(String username, int maxEntries)
            throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#findTemplates(java.lang.String[])
     */
    protected List findTemplates(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#getTemplate(java.lang.String[])
     */
    protected byte[] getTemplate(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#updateTemplate(java.lang.String,
     *      java.lang.String[])
     */
    protected void updateTemplate(String template, String[] pathInfo)
            throws Exception
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osjava.atom4j.servlet.AtomServlet#deleteTemplate(java.lang.String[])
     */
    protected byte[] deleteTemplate(String[] pathInfo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** returns the same string that it is passed, password = userName */
    public String getPassword(String userName) throws Exception
    {
        return userName;
    }
}