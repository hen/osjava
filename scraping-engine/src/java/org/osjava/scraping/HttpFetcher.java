package org.osjava.scraping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.generationjava.net.UrlW;

import org.apache.commons.lang.StringUtils;

/**
 * Fetches a piece of content for a url
 */
public class HttpFetcher implements Fetcher {

    public Page fetch(String uri, Config cfg, Session session) throws FetchingException {
        try {
            URL url = new URL(uri);  
            String txt = ""+UrlW.getContent(uri);
            Page page = new MemoryPage(txt);
            String base = url.getProtocol()+"://"+url.getHost();
            if(url.getPort() != 80 && url.getPort() != -1) {
                base += ":"+url.getPort();
            }
            base += StringUtils.chomp(url.getPath(), "/");
            page.setDocumentBase(base);
            return page;
        } catch(MalformedURLException murle) {
            throw new FetchingException("Bad uri. "+murle.getMessage(), murle);
        } catch(IOException ioe) {
            throw new FetchingException("Error. "+ioe.getMessage(), ioe);
        }
    }

}
