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
package org.osjava.atom4j.servlet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.Atom4J;
import org.osjava.atom4j.pojo.Entry;
import org.osjava.atom4j.pojo.Feed;
import org.osjava.atom4j.pojo.Template;
import org.osjava.atom4j.reader.EntryReader;
import org.osjava.atom4j.reader.FeedReader;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http://bitworking.org/news/AtomAPI_Quick_Reference
 * http://atomenabled.org/developers/api/atom-api-spec.php
 *  
 * All requests should have a URL format
 * /atom/username/ACTION[/args]
 * 
 * GET    Feed  PathInfo: /USER/feed[/id] - include id for multiple feeds per user
 * GET    Entry PathInfo: /USER/entry/id
 * DELETE Entry PathInfo: /USER/delete/id
 * CREATE Entry PathInfo: /USER/entry
 * UPDATE Entry PathInfo: /USER/entry/id
 * 
 * -- No Longer Specified --
 * CREATE Comment PathInfo: /USER/comment/entry-id
 * 
 * -- No Longer Specified --
 * GET    Template PathInfo: /USER/template/id
 * UPDATE Template PathInfo: /USER/template/id
 * DELETE Template PathInfo: /USER/template/id
 * GET    Templates PathInfo: /USER/findTemplates
 * 
 * -- No Longer Specified --
 * SEARCH PathInfo: /USER/search/??
 *        Queries: ?atom-last=20
 *                 ?atom-all=y
 *                 ?atom-start-range=0&atom-end-range=10
 *  
 * Created on Aug 21, 2003
 * @author llavandowska
 * 
 * @ web.servlet name="AtomServlet"
 * @ web.servlet-mapping url-pattern="/atom"
 */
public abstract class AtomServlet extends HttpServlet
{
    private static Log logger = 
        LogFactory.getFactory().getInstance(AtomServlet.class);
                
    protected static String baseURL = null;
    
    private static final byte[] XML_HEADER = 
        "<?xml version=\"1.0\" encoding='utf-8'?>".getBytes();
    
    private String introspectionXML = null;

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();
    }   
        
    /**
     * Return the PathInfo from the request.  Convenience method.
     * 
     * @param request
     * @return
     */
    protected String[] getPathInfo(HttpServletRequest request)
    {
        String mPathInfo = request.getPathInfo();
        mPathInfo = (mPathInfo!=null) ? mPathInfo : "";
        return StringUtils.split(mPathInfo,"/");   
    }

    /**
     * Write the "result" bytes to the response. Convenience method.
     * 
     * @param response
     * @param result
     * @throws IOException
     */
    private void writeResults(HttpServletResponse response, byte[] result)
        throws IOException
    {
        response.setStatus( HttpServletResponse.SC_OK ); 
        response.setContentType("application/x.atom+xml");    
        
        if (result.length > 0)
        {
            response.setContentLength(XML_HEADER.length + result.length);
            OutputStream output = response.getOutputStream();
            output.write(XML_HEADER);
            output.write(result);
            output.flush();
        }
        else
        {
            response.setContentLength(0);
        }
    }

    /**
     * Authenticate user based on information in request.
     */
    protected boolean authorized(HttpServletRequest request)
    {  
        // for now, we do WSSE as do Typepad and Blogger.com
        return wsseAuthentication(request);
    }
    
    /** 
     * Get password for specified user name. 
     */
    public abstract String getPassword(String userName) throws Exception;
    
    /** 
     * Perform WSSE authentication based on information in request. 
     */
    protected boolean wsseAuthentication(HttpServletRequest request) 
    {
        return wsseAuthentication(request.getHeader("X-WSSE"));
    }
    
    /** 
     * Authenticate based on WSSE header string, public for unit testing purposes. 
     */
    public boolean wsseAuthentication(String wsseHeader)
    {
        boolean ret = false;
        Base64 base64 = new Base64();
        String userName = null;
        String created = null;
        String nonce = null;
        String passwordDigest = null;
        String[] tokens = wsseHeader.split(",");
        for (int i = 0; i < tokens.length; i++)
        {
            int index = tokens[i].indexOf('=');
            if (index != -1)
            {
                String key = tokens[i].substring(0, index).trim();
                String value = tokens[i].substring(index + 1).trim();
                value = value.replaceAll("\"", "");
                if (key.startsWith("UsernameToken"))
                {
                    userName = value;
                }
                else if (key.equalsIgnoreCase("nonce"))
                {
                    nonce = value;
                }
                else if (key.equalsIgnoreCase("passworddigest"))
                {
                    passwordDigest = value;
                }
                else if (key.equalsIgnoreCase("created"))
                {
                    created = value;
                }
            }
        }
        String digest = null;
        try
        {
            digest = WSSEUtilities.generateDigest(
                         WSSEUtilities.base64Decode(nonce), 
                         created.getBytes("UTF-8"), 
                         getPassword(userName).getBytes("UTF-8"));
            ret = digest.equals(passwordDigest);
        }
        catch (Exception e)
        {
            logger.warn("ERROR in wsseAuthenticataion: " + e.getMessage());
        }
        return ret;
    }

    /**
     * Invalid request, return error message.
     *
     * @param request
     * @param response
     */
    private void error(HttpServletRequest request, HttpServletResponse response,
        String errorTitle, String errorBody) throws IOException
    {
        if (errorTitle == null) errorTitle = "Something Bad Happened";
        if (errorBody == null) errorBody = "Something bad happened";
        String title = "<title>" + errorTitle + "</title>\n";
        String description = "<description>" + errorBody + "</description>\n";
        byte[] result = ("<error>\n"+title+description+"</error>").getBytes();
        response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); 
        response.setContentType("application/x.atom+xml");    
        
        response.setContentLength(XML_HEADER.length + result.length);
        OutputStream output = response.getOutputStream();
        output.write(XML_HEADER);
        output.write(result);
        output.flush();
    }
    
    /**
     * All uses of doDelete must be authorized!
     *  
     * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2) 
        {
            error(request, response, "Invalid Request", "Insufficient Information to Process Request."); 
            return;
        } 

        if (authorized(request))
        {
            if ("entry".equals(pathInfo[1]))
            {
                deleteEntry(pathInfo, request, response);
            }
            /*
            else if ("template".equals(pathInfo[1]))
            {
                deleteTemplate(pathInfo, request, response);
            }
            */
        }
        else
        {
            error(request, response, "Unauthorized", "You are not authorized for this Action."); 
            return;
        } 
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2) 
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request."); 
            return;
        } 

        // first the options that don't require authorization
        boolean authorized = authorized(request);
        if ("entry".equals(pathInfo[1]))
        {
            getEntry(pathInfo, request, response);
        }
        else if ("feed".equals(pathInfo[1]))
        {
            getFeed(pathInfo, request, response);
        }
        /*
        else if ("search".equals(pathInfo[1]))
        {
            search(pathInfo, request, response);
        }
        */
        // now check for those that do require authorization
        else if (authorized)
        {
            if ("delete".equals(pathInfo[1]))
            {
                deleteEntry(pathInfo, request, response);
            }
            /*
            else if ("findTemplates".equals(pathInfo[1]))
            {
                findTemplates(pathInfo, request, response);
            }
            else if ("template".equals(pathInfo[1]))
            {
                getTemplate(pathInfo, request, response);
            }
            */
        }
        else if (!authorized)
        {
            error(request, response, "Authorization Failure.", "You are not authorized for this Action.");   
        }
        else
        {
            error(request, response, "No Action was requested.", "Your request did not contain an Action."); 
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2)
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request."); 
            return;
        } 

        boolean authorized = authorized(request);
        if (authorized && "entry".equals(pathInfo[1]))
        {
            postEntry(pathInfo, request, response);
        }
        else if ("comment".equals(pathInfo[1]))
        {
            postComment(pathInfo, request, response);
        }
        else if (!authorized)
        {
            error(request, response, "Authorization Failure.", "You are not authorized for this Action.");   
        }
        else
        {
            error(request, response, "No Action was requested.", "Your request did not contain an Action."); 
        }
    }


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2)
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request."); 
            return;
        } 
        
        if (authorized(request))
        {
            if ("entry".equals(pathInfo[1]))
            {
                updateEntry(pathInfo, request, response);
            }
            /*
            else if ("template".equals(pathInfo[1]))
            {
                updateTemplate(pathInfo, request, response);
            }
            */
            else
            {
                error(request, response, "No Action was requested.", "Your request did not contain an Action.");
                return;
            }
        }
        else
        {
            error(request, response, "Unauthorized", "You are not authorized for the requested Action."); 
            return;
        }
    }

    /**
     * PathInfo: /USER/feed/id
     * 
     * @param pathInfo
     * @param request
     * @param response
     */
    private void getFeed(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        if (pathInfo.length > 0)
        {
            Feed feed = null;
            try
            {
                feed = getFeed(pathInfo);
            }
            catch (Exception e)
            {
                logger.error(e);
                throw new ServletException(e);
            }
            
            if (feed != null)
            {    
                writeResults(response, feed.toString().getBytes());
            }
            else
            {
                error(request, response, "No Feed Found", "No Feed Found for " + pathInfo[0]);
            }
        }
        else
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request.");
        }      
    }

    /**
     * @param pathInfo
     * @return
     */
    protected abstract Feed getFeed(String[] pathInfo) throws Exception;

    /**
     * PathInfo: /USER/entry/id
     * 
     * @param pathInfo
     * @param request
     * @param response
     */
    protected void getEntry(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        if (pathInfo.length > 2)
        {
            try
            {
                Entry entry = getEntry(pathInfo);
                writeResults(response, entry.toString().getBytes());
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request.");
        }
    }

    /**
     * @param pathInfo
     * @return
     */
    protected abstract Entry getEntry(String[] pathInfo) throws Exception;

    /**
     * PathInfo: /USER/delete/id
     * 
     * @param request
     */
    protected void deleteEntry(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException 
    {        
        if (pathInfo.length > 2)
        {
            try
            {
                deleteEntry(pathInfo);
                writeResults(response, "".getBytes());
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request.");
        }
    }

    /**
     * @param pathInfo
     */
    protected abstract void deleteEntry(String[] pathInfo) throws Exception;
    
    /**
     * Create a new Entry.  The jury is still out on whether AtomAPI will
     * support the creation of more than one Entry at a time.  The API
     * does specify, however, that creating an Entry will return a 
     * Location header containing the Atom URL to that Entry.  For now
     * Atom4J is returning the address of the <b>last</b> Entry created.
     * 
     * PathInfo: /USER/entry
     * 
     * @param request
     */
    protected void postEntry(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        Collection entries = null;
        // is this a <feed> wrapped around several <entry>'s ?
        FeedReader feedReader = new FeedReader( request.getInputStream() );
        if (feedReader.getFeed() != null)
        {
            entries = feedReader.getFeed().getEntries();
        }
        else
        {    
            // or is it really just one Entry?
            EntryReader reader = new EntryReader( request.getInputStream() );
            entries = reader.getEntries();
        }
        if (entries == null || entries.size() < 1)
        { 
            error(request, response, "No Entry Found", "No entry was found in the supplied information."); 
            return; 
        }

        Entry entry = null;
        Iterator iter = entries.iterator();
        while (iter.hasNext())
        {
            entry = (Entry)iter.next();
            try
            {
                saveNewEntry(entry);                
            }
            catch (Exception e)
            {
                ServletException se = new ServletException(e);
                se.fillInStackTrace();
                throw se;
            }
        }
        
        if (entry != null)
        {
            String entryLoc = new StringBuffer(baseURL).append("/atom/")
            .append(pathInfo[0])
            .append("/entry/")
            .append(entry.getId()).toString();
            response.setHeader("Location", entryLoc);
            //response.setStatus( HttpServletResponse.SC_CREATED ); 
            response.setStatus( HttpServletResponse.SC_SEE_OTHER );
        }
    }

    /**
     * @param entry
     */
    protected abstract void saveNewEntry(Entry entry) throws Exception;

    /**
     * Update the Entry (save changes).
     * 
     * PathInfo: /USER/entry/id
     * 
     * @param request
     */
    protected void updateEntry(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        EntryReader reader = new EntryReader( request.getInputStream() );
        List entries = (List)reader.getEntries();
        if (entries == null || entries.size() < 1)
        { 
            error(request, response, "No Entry Found For Update.", "Unable to locate entry submitted for update."); 
            return; 
        }
        
        if (pathInfo.length > 2)
        {
            // find the Entry to be edited
            Entry entry = (Entry)entries.get(0);
            
            if (entry != null)
            {
                try
                {
                    updateEntry(entry, pathInfo);
                    
                    response.setStatus( HttpServletResponse.SC_RESET_CONTENT );
                }
                catch (Exception e)
                {
                    throw new ServletException(e);
                }
            }
        }
        else
        {
            error(request, response, "Invalid request.", "Insufficient Information to Process Request.");
        }
    }

    /**
     * @param entry
     */
    protected abstract void updateEntry(Entry entry, String[] pathInfo) throws Exception;

    /**
     * Create a new comment.
     * The spec no longer defines Commenting, except to say that
     * Comments are Entries.  No relationship between an Entry and
     * its Comments is described.
     * 
     * PathInfo: /USER/comment/id
     * id = entry's id
     * 
     * @param request
     */
    protected void postComment(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        try
        {
            Entry entry = getEntry(pathInfo);
            if (entry != null)
            {
                Iterator iter = new EntryReader(request.getInputStream()).getEntries().iterator();
                Entry comment = (Entry)iter.next();
                if (comment != null)
                    postComment(entry, comment, pathInfo);
            }
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }


    /**
     * The spec no longer defines Commenting, except to say that
     * Comments are Entries.  No relationship between an Entry and
     * its Comments is described.
     * 
     * @param entry
     * @param comment
     * @param pathInfo
     */
    protected abstract void postComment(Entry entry, Entry comment, String[] pathInfo) throws Exception;
    
    /**
     * Return the Search Results.
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * PathInfo: /USER/search/??
     * Queries: ?atom-last=20
     *          ?atom-all=y
     *          ?atom-start-range=0&atom-end-range=10
     *  
     * @param request
     * @deprecated
     */
    protected void search(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        List entries = null;
        int maxEntries = 10;

        try
        {      
            // search by keyword(s)
            if (pathInfo.length > 2)
            {            
                entries = entryKeywordSearch(pathInfo);
            }
            // get entries in range
            else if (request.getParameter("atom-start-range") != null &&
                request.getParameter("atom-end-range") != null )
            {
                try
                {
                    int startRange = Integer.valueOf(
                        request.getParameter("atom-start-range")).intValue();
                    int endRange = Integer.valueOf(
                        request.getParameter("atom-end-range")).intValue();
                    if (startRange >= 0 && endRange > startRange)
                    {
                        entries = getEntryRange(startRange, endRange, pathInfo);
                    }
                }
                catch (NumberFormatException e)
                {
                    // Not A Number : ignore
                }            
                
            }
            // get the most recent X-number of entries
            else if (request.getParameter("atom-last") != null ||
                      request.getParameter("atom-recent") != null)
            {
                String maxStr = request.getParameter("atom-last");
                if (maxStr == null) maxStr = request.getParameter("atom-recent");
                try
                {
                    maxEntries = Integer.valueOf( maxStr ).intValue();
                }
                catch (NumberFormatException e)
                {
                    // Not A Number : ignore
                }
            }
            else if (request.getParameter("atom-all") != null)
            {
                entries = allEntries(pathInfo);
            }
    
            // if atom-last or no 'search' was specified, 
            // or a number-format exception default to this.
            if (entries == null)
            {
                entries= getLatestEntries(pathInfo[0], maxEntries);
            }
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
                
        StringBuffer buf = new StringBuffer("<search-results xmlns=\"").append(Atom4J.xmlns).append("\" >");
        Iterator iter = entries.iterator();
        while (iter.hasNext())
        {
            Entry entry = (Entry)iter.next();
            // T O D O : Improve generation of "search results"
            buf.append("<entry><title>").append(entry.getTitle())
                .append("</title><id>").append(baseURL).append("/atom/")
                .append(pathInfo[0]).append("/entry/")
                .append(entry.getId()).append("</id></entry>\n");
        }
        buf.append("</search-results>");
        
        writeResults(response, buf.toString().getBytes());

    }

    /**
     * Search is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * @param pathInfo
     * @deprecated
     */
    protected abstract List allEntries(String[] pathInfo) throws Exception;

    /**
     * Search is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * @param startRange
     * @param endRange
     * @param pathInfo
     * @deprecated
     */
    protected abstract List getEntryRange(int startRange, int endRange, String[] pathInfo) throws Exception;

    /**
     * Search is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * @param pathInfo
     * @deprecated
     */
    protected abstract List entryKeywordSearch(String[] pathInfo) throws Exception;

    /**
     * Helper method.
     * @param maxEntries
     * @deprecated
     */
    protected abstract List getLatestEntries(String username, int maxEntries) 
        throws Exception;

    /**
     * Return list of Templates.
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * PathInfo: /USER/findTemplates
     * 
     * @param request
     * @deprecated
     */
    protected void findTemplates(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        StringBuffer buf = new StringBuffer("<resources xmlns=\"" + Atom4J.xmlns + "\">");
        List templates = null;
        try
        {
            templates= findTemplates(pathInfo);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
        Iterator iter = templates.iterator();
        while (iter.hasNext())
        {
            Template template = (Template) iter.next();
            buf.append(template.toString());
        }
        buf.append("</resources>");
        writeResults(response, buf.toString().getBytes());
    }

    /**
     * This must return a List of Template objects.
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * @param pathInfo
     * @deprecated
     */
    protected abstract List findTemplates(String[] pathInfo) throws Exception;

    /**
     * Return the requested template.
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * PathInfo: /USER/template/id
     * 
     * @param request
     * @deprecated
     */
    protected void getTemplate(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {     
        if (pathInfo.length > 2)
        {
            try
            {
                byte[] template = getTemplate(pathInfo);
                writeResults(response, template);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else
        {
            error(request, response, "Invalid Request.", "Insufficient Information to Process Request.");
        }
    }

    /**
     * Return the contents of the template (HTML or what-have-you)
     * as a byte array.
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * @param pathInfo
     * @deprecated
     */
    protected abstract byte[] getTemplate(String[] pathInfo) throws Exception;

    /**
     * Update the template (save changes).
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * PathInfo: /USER/template/id
     * 
     * @param request
     * @deprecated
     */
    protected void updateTemplate(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        try
        {
            java.io.BufferedReader streamReader =
                new java.io.BufferedReader(
                    new java.io.InputStreamReader(
                        request.getInputStream()));

            StringBuffer buf = new StringBuffer();
            String dataLine;
            while((dataLine = streamReader.readLine()) != null){
                buf.append(dataLine);
            }//end while loop
            streamReader.close();

            updateTemplate(buf.toString() , pathInfo );
            writeResults(response, "".getBytes());
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }
    
    /**
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * @param stream
     * @param pathInfo
     * @deprecated
     */
    protected abstract void updateTemplate(String template, String[] pathInfo) throws Exception;

    /**
     * Delete the template.
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * 
     * PathInfo: /USER/template/id
     * 
     * @param pathInfo
     * @param request
     * @param response
     * @deprecated
     */
    protected void deleteTemplate(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        try
        {
            byte[] result = deleteTemplate(pathInfo);
            writeResults(response, result);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }       
    }


    /**
     * This is no longer a part of the Atom spec, but I'm leaving it
     * in anticipation of future spec changes.
     * @param pathInfo
     * @deprecated
     */
    protected abstract byte[] deleteTemplate(String[] pathInfo) throws Exception;
}
