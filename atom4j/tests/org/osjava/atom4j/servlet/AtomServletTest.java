/*
 * Created on Jun 27, 2004
 */
package org.osjava.atom4j.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Test the Atom4J servlet.
 */
public class AtomServletTest extends TestCase
{

    public void testWsseAuthentication() throws Exception
    {
        MockAtomServlet servlet = new MockAtomServlet();

        String goodHeader = generateWSSEHeader("fred", "fred");
        assertTrue(servlet.wsseAuthentication(goodHeader));

        String badHeader = generateWSSEHeader("fred", "fred1");
        assertFalse(servlet.wsseAuthentication(badHeader));
    }

    public void testBlogger()
    {
        try
        {
            URL url = new URL("http://www.blogger.com/atom");
            String username = "test";
            String password = "test";
            /*URL url = new URL("http://localhost:8080/roller/atom/admin/feed");
            String username = "admin";
            String password = "admin";*/

            URLConnection connection = url.openConnection();
            connection.addRequestProperty("X-WSSE", generateWSSEHeader(username, password));

            InputStream in = null;
            try
            {
                in = connection.getInputStream();
            } 
            catch (Exception e)
            {
                e.printStackTrace();
                in = ((HttpURLConnection) connection).getErrorStream();
            }
            BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String inputLine;
            while ((inputLine = res.readLine()) != null)
                System.out.println(inputLine);
            res.close();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String generateWSSEHeader(String username, String password)
            throws Exception
    {

        byte[] nonceBytes = Long.toString(new Date().getTime()).getBytes();
        String nonce = new String(WSSEUtilities.base64Encode(nonceBytes));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = sdf.format(new Date());

        String digest = WSSEUtilities.generateDigest(
                nonceBytes, created.getBytes("UTF-8"), password.getBytes("UTF-8"));

        StringBuffer header = new StringBuffer("UsernameToken Username=\"");
        header.append(username);
        header.append("\", ");
        header.append("PasswordDigest=\"");
        header.append(digest);
        header.append("\", ");
        header.append("Nonce=\"");
        header.append(nonce);
        header.append("\", ");
        header.append("Created=\"");
        header.append(created);
        header.append("\"");
        return header.toString();
    }
}


