/*
 * Created on Jul 16, 2004
 */
package org.osjava.atom4j.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * Utilties to support WSSE authentication.
 * @author Dave Johnson
 */
public class WSSEUtilities
{
    public static synchronized String generateDigest(
            byte[] nonce, byte[] created, byte[] password) throws Exception
    {
        String result = null;
        try
        {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            digester.reset();
            digester.update(nonce);
            digester.update(created);
            digester.update(password);
            byte[] digest = digester.digest();
            result = new String(base64Encode(digest));
        }
        catch (NoSuchAlgorithmException e)
        {
            result = null;
        }
        return result;
    }

    public static byte[] base64Decode(String value) throws IOException
    {
        Base64 base64 = new Base64();
        return Base64.decodeBase64(value.getBytes("UTF-8"));
    }

    public static String base64Encode(byte[] value)
    {
        Base64 base64 = new Base64();
        return new String(Base64.encodeBase64(value));
    }
}
