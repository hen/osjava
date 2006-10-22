package org.cyberiantiger.mudclient.net;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.io.IOException;
import java.io.EOFException;

import org.cyberiantiger.mudclient.config.ClientConfiguration;

/**
 * XXX: Once we move to java 1.5 as minimum requirement it has it's own
 * built in proxy support so alot of this crap can vanish.
 */
public class SocketFactory {

    public static Socket getSocket(ClientConfiguration config) 
        throws IOException 
    {
        if (config.getUseProxy()) {

            Socket ret;
            if (config.getUseJvmProxy()) {
                ret = new Socket(config.getJvmProxyHost(), config.getJvmProxyPort());
            } else {
                ret = new Socket(config.getProxyHost(), config.getProxyPort());
            }
            try {
                OutputStreamWriter out = new OutputStreamWriter(ret.getOutputStream(), "ISO-8859-1");
                out.write("CONNECT " + config.getHost() + ":" + config.getPort() + " HTTP/1.1\r\n" +
                        "Host: " + config.getHost() + ":" + config.getPort() + "\r\n" + 
                        "\r\n");
                out.flush();
                InputStreamReader in = new InputStreamReader(ret.getInputStream(), "ISO-8859-1");
                String response = readLine(in);
                /* Read the rest of the response header */
                while (readLine(in).length() != 0);
                /* Check response code */
                int i = response.indexOf(' ');
                if (i == -1) {
                    throw new IOException("Proxy server did not send a valid response: " + response);
                }
                String version = response.substring(0,i);
                if (!version.startsWith("HTTP/1.")) {
                    throw new IOException("Proxy server did not send a valid response: " + response);
                }
                if (version.length() != 8 || !(version.charAt(7) == '0' || version.charAt(7) == '1')) {
                    throw new IOException("Proxy server did not send a valid http version: " + response);
                }
                int j  = i + 1;
                i = response.indexOf(' ', j);
                if (i == -1) { 
                    throw new IOException("Proxy server did not send a valid response: " + response);
                }
                try {
                    int rCode = Integer.parseInt(response.substring(j, i));

                    if (rCode != 200) {
                        throw new IOException("Error: " + response.substring(j));
                    }
                } catch (NumberFormatException nfe) {
                    throw new IOException("Proxy server did not send a valid response: " + response);
                }
                return ret;
            } catch (IOException e) {
                ret.close();
                throw e;
            }
        } else {
            return new Socket(config.getHost(), config.getPort());
        }
    }

    /* Assumes \r\n or just \n */
    private static String readLine(Reader reader) throws IOException {
        StringBuffer ret = new StringBuffer();
        boolean eol = false;
        while (true) {
            int i = reader.read();
            switch (i) {
                case -1:
                    throw new IOException("End of stream before end of line");
                case '\r':
                    if (eol) {
                        throw new IOException("Invalid end of line");
                    }
                    eol = true;
                    break;
                case '\n':
                    return ret.toString();
                default:
                    if (eol) {
                        throw new IOException("Invalid end of line");
                    }
                    ret.append((char)i);
            }
        }
    }
}
