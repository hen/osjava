/*
 * Created on Aug 21, 2003
 */
package org.osjava.atom4j.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.Atom4J;
import org.osjava.atom4j.pojo.Entry;
import org.osjava.atom4j.pojo.Introspection;
import org.osjava.atom4j.pojo.Template;
import org.osjava.atom4j.pojo.UserPreferences;
import org.osjava.atom4j.reader.EntryReader;
import org.osjava.atom4j.reader.PrefsReader;

/**
 * http://bitworking.org/news/AtomAPI_URIs
 * http://bitworking.org/rfc/draft-gregorio-07.html
 *  
 * All requests should have a URL format
 * /atom/username/ACTION[/args]
 * 
 * Valid ACTIONs are: introspection, create, prefs, 
 * entry, search, comment, findTemplates, template
 * 
 * NOTE: Update getIntrospectionXML() as ACTIONs are implemented.
 * 
 * @author llavandowska
 * 
 * @web.servlet name="AtomServlet"
 * @web.servlet-mapping url-pattern="/atom/*"
 */
public abstract class AtomServlet extends HttpServlet
{
    private static Log logger = 
        LogFactory.getFactory().getInstance(AtomServlet.class);
                
    protected static String baseURL = null;
    
    private static final byte[] XML_HEADER = 
        "<?xml version=\"1.0\" encoding='iso-8859-1'?>".getBytes();
    
    private String introspectionXML = null;
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();
        baseURL = "/" + getServletContext().getServletContextName();
    }
    
    /**
     * Implementor can/should override this method to 
     * specify which Actions they support.
     * 
     * Valid ACTIONs are: introspection, create, prefs, 
     * entry, search, comment, findTemplates, template
     */
    protected String getIntrospectionXML()
    {
        if (introspectionXML != null) return introspectionXML;
        else
        {
            Introspection intro = new Introspection();
            intro.setBaseURL( baseURL );
            intro.setCreateEntry( true );
            intro.setDeleteEntry( true ); // not part of spec?
            intro.setEditEntry( true );   // not part of spec?
            intro.setSearchEntries( true );
            intro.setUserPrefs( true );
            intro.setComment( true );    // not part of spec?
            intro.setCategories( false ); // not specified fully
            
            introspectionXML = intro.toString(); 
        }
        return introspectionXML;
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
        
        if (result.length > 0)
        {
            response.setContentType("application/x.atom+xml");
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
     * Implementor should override this method.
     * 
     * @return
     */
    protected boolean authorized()
    {   
        return false;
    }

    /**
     * Invalid request, return error message.
     * TODO : What is an appropriate error message/value/format?
     * 
     * @param request
     * @param response
     */
    private void error(HttpServletRequest request, HttpServletResponse response,
        String customMessage) throws IOException
    {
        if (customMessage == null) customMessage = "Something bad happened";
        byte[] result = ("<error>"+customMessage+"</error>").getBytes();
        writeResults(response, result);
    }
    
    /**
     * All uses of doDelete must be authorized!
     *  
     * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2) 
        {
            error(request, response, "Invalid Request."); 
            return;
        } 

        if (authorized())
        {
            if ("entry".equals(pathInfo[1]))
            {
                deleteEntry(pathInfo, request, response);
            } 
            else if ("template".equals(pathInfo[1]))
            {
                deleteTemplate(pathInfo, request, response);
            }
        }
        else
        {
            error(request, response, "You are not authorized for this Action."); 
            return;
        } 
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2) 
        {
            error(request, response, "Invalid Request."); 
            return;
        } 

        // first the options that don't require authorization
        if ("entry".equals(pathInfo[1]))
        {
            getEntry(pathInfo, request, response);
        }
        else if ("introspection".equals(pathInfo[1]))
        {
            getIntrospection(pathInfo, request, response);
        }
        else if ("search".equals(pathInfo[1]))
        {
            search(pathInfo, request, response);
        }
        // now check for those that do require authorization
        else if (authorized())
        {
            if ("delete".equals(pathInfo[1]))
            {
                deleteEntry(pathInfo, request, response);
            }
            else if ("findTemplates".equals(pathInfo[1]))
            {
                findTemplates(pathInfo, request, response);
            }
            else if ("prefs".equals(pathInfo[1]))
            {
                getPrefs(pathInfo, request, response);
            }
            else if ("template".equals(pathInfo[1]))
            {
                getTemplate(pathInfo, request, response);
            }
        }
        else
        {
            error(request, response, "No Action was requested."); 
            return;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2)
        {
            error(request, response, "Invalid Request."); 
            return;
        } 
        
        if (authorized() && "create".equals(pathInfo[1]))
        {
            postEntry(pathInfo, request, response);
        }
        else if ("comment".equals(pathInfo[1]))
        {
            postComment(pathInfo, request, response);
        }
        else
        {
            error(request, response, "No Action was requested.");
            return;
        }
    }


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String[] pathInfo = getPathInfo(request);
        if (pathInfo.length < 2 || !authorized())
        {
            error(request, response, "Invalid Request."); 
            return;
        } 
        
        if (authorized())
        {
            if ("prefs".equals(pathInfo[1]))
            {
                updatePrefs(pathInfo, request, response);
            }
            else if ("entry".equals(pathInfo[1]))
            {
                updateEntry(pathInfo, request, response);
            }
            else if ("template".equals(pathInfo[1]))
            {
                updateTemplate(pathInfo, request, response);
            }
            else
            {
                error(request, response, "No Action was requested.");
                return;
            }
        }
        else
        {
            error(request, response, "You are not authorized for the requested Action."); 
            return;
        }
    }

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
            error(request, response, "Invalid Request.");
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
            error(request, response, "Invalid Request.");
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
     * Location header containing the Atom URL to that Entry.
     * 
     * PathInfo: /USER/entry
     * 
     * @param request
     */
    protected void postEntry(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        EntryReader reader = new EntryReader( request.getInputStream() );
        Collection entries = reader.getEntries();
        if (entries == null || entries.size() < 1)
        { 
            error(request, response, "Invalid Request"); 
            return; 
        }

        Iterator iter = entries.iterator();
        while (iter.hasNext())
        {
            Entry entry = (Entry)iter.next();
            try
            {
                saveNewEntry(entry);
                
                String entryLoc = new StringBuffer(baseURL).append("/atom/")
                    .append(pathInfo[0])
                    .append("/entry/")
                    .append(entry.getId()).toString();
                response.setHeader("Location", entryLoc);
                response.setStatus( HttpServletResponse.SC_CREATED ); 
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
    }

    /**
     * @param entry
     */
    protected abstract void saveNewEntry(Entry entry) throws Exception;

    /**
     * Update the Entry (save changes)
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
            error(request, response, "No Entry Found For Update."); 
            return; 
        }
        
        if (pathInfo.length > 2)
        {
            // find the WeblogEntryData to be edited
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
            error(request, response, "Invalid request.");
        }
    }

    /**
     * @param entry
     */
    protected abstract void updateEntry(Entry entry, String[] pathInfo) throws Exception;

    /**
     * Return the Search Results
     * 
     * PathInfo: /USER/search/??
     * Queries: ?atom-last=20
     *          ?atom-all=y
     *          ?atom-start-range=0&atom-end-range=10
     *  
     * @param request
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
            // TODO : Improve generation of "search results"
            buf.append("<entry><title>").append(entry.getTitle())
                .append("</title><id>").append(baseURL).append("/atom/")
                .append(pathInfo[0]).append("/entry/")
                .append(entry.getId()).append("</id></entry>\n");
        }
        buf.append("</search-results>");
        
        writeResults(response, buf.toString().getBytes());

    }

    /**
     * @param pathInfo
     * @return
     */
    protected abstract List allEntries(String[] pathInfo) throws Exception;

    /**
     * @param startRange
     * @param endRange
     * @param pathInfo
     * @return
     */
    protected abstract List getEntryRange(int startRange, int endRange, String[] pathInfo) throws Exception;

    /**
     * @param pathInfo
     * @return
     */
    protected abstract List entryKeywordSearch(String[] pathInfo) throws Exception;

    /**
     * @param maxEntries
     * @return
     */
    protected abstract List getLatestEntries(String username, int maxEntries) 
        throws Exception;

    /**
     * Return the Introspection response XML.
     * 
     * PathInfo: /USER/introspection
     * 
     * @param request
     */
    protected void getIntrospection(String[] pathInfo, HttpServletRequest request, HttpServletResponse response) 
        throws IOException
    {
        String introspectionXML = getIntrospectionXML();
        if (introspectionXML == null)
        {
            error(request, response, "Error getting Introspection data."); 
            return;
        } 
        
        byte[] result = introspectionXML.replaceAll("USER", pathInfo[0]).getBytes();

        writeResults(response, result);
    }

    /**
     * Return the User "Preferences"
     * 
     * PathInfo: /USER/prefs
     * 
     * @param request
     */
    protected void getPrefs(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        UserPreferences userPrefs;
        try
        {
            userPrefs= getUserPreferences(pathInfo);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
        
        writeResults(response, userPrefs.toString().getBytes());       
    }


    /**
     * @param pathInfo
     * @return
     */
    protected abstract UserPreferences getUserPreferences(String[] pathInfo) throws Exception;

    /**
     * Return ??
     * 
     * PathInfo: /USER/findTemplates
     * 
     * @param request
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
     * 
     * @param pathInfo
     * @return
     */
    protected abstract List findTemplates(String[] pathInfo) throws Exception;

    /**
     * Return the page template ??
     * 
     * PathInfo: /USER/template/id
     * 
     * @param request
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
            error(request, response, "Invalid Request.");
        }
    }

    /**
     * Return the contents of the template (HTML or what-have-you)
     * as a byte array.
     * 
     * @param pathInfo
     * @return
     */
    protected abstract byte[] getTemplate(String[] pathInfo) throws Exception;

    /**
     * Update the template (save changes)
     * 
     * PathInfo: /USER/template
     * 
     * @param request
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
     * @param stream
     * @param pathInfo
     */
    protected abstract void updateTemplate(String template, String[] pathInfo) throws Exception;

    /**
     * Delete the template.
     * 
     * PathInfo: /USER/template/id
     * 
     * @param pathInfo
     * @param request
     * @param response
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
     * @param pathInfo
     * @return
     */
    protected abstract byte[] deleteTemplate(String[] pathInfo) throws Exception;

    /**
     * Create a new comment
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
     * @param entry
     * @param comment
     * @param pathInfo
     */
    protected abstract void postComment(Entry entry, Entry comment, String[] pathInfo) throws Exception;

    /**
     * Save the User "Preferences"
     * 
     * PathInfo: /USER/prefs
     * 
     * @param request
     */
    protected void updatePrefs(String[] pathInfo, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        try
        {
            UserPreferences userPrefs = new PrefsReader( 
                request.getInputStream() ).getUserPrefs();
            updatePrefs(userPrefs, pathInfo);  
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }      
    }


    /**
     * @param userPrefs
     * @param pathInfo
     */
    protected abstract void updatePrefs(UserPreferences userPrefs, String[] pathInfo) throws Exception;
}
