/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Scabies nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
