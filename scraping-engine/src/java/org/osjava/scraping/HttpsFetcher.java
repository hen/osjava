package org.osjava.scraping;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.generationjava.net.UrlW;

/**
 * Fetches a piece of content for a url
 */
public class HttpsFetcher implements Fetcher {

    public Page fetch(String uri, Config cfg, Session session) throws FetchingException {
        try {
            URL url = new URL(uri);
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(url.getFile());
            UsernamePasswordCredentials upc = new UsernamePasswordCredentials(
                cfg.getString("username"), cfg.getString("password") 
            );

            int port = url.getPort();
            if(port == -1) {
                port = 443;
            }
            client.startSession( url.getHost(), 
                                 port,
                                 upc,
                                 true
            );
            if(cfg.has("timeout")) {
                client.setTimeout(cfg.getInt("timeout"));
            }
            int result = client.executeMethod(get);
            if(result != 200) {
                throw new FetchingException("Unable to fetch from "+uri+" due to error code "+result);
            }
            String txt = get.getResponseBodyAsString();
            Page page = new MemoryPage(txt);
            String base = url.getProtocol()+"://"+url.getHost();
            if(url.getPort() != 443) {
                base += ":"+url.getPort();
            }
            page.setDocumentBase(base);
            return page;
        } catch(IOException ioe) {
            throw new FetchingException("Error. "+ioe.getMessage(), ioe);
        }
    }

}
