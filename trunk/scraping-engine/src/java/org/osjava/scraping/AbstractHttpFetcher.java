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

/**
 * Fetches a piece of content for a url
 */
public abstract class AbstractHttpFetcher implements Fetcher {

    public abstract int getDefaultPort();

    protected abstract void startSession(URL url, int port, HttpClient client, Config cfg, Session session);

    public Page fetch(String uri, Config cfg, Session session) throws FetchingException {
        try {
            URL url = new URL(uri);
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(url.getFile());

            int port = url.getPort();
            if(port == -1) {
                port = getDefaultPort();
            }
            startSession(url, port, client, cfg, session);
            if(cfg.has("timeout")) {
                client.setTimeout(cfg.getInt("timeout"));
            }
            int result = client.executeMethod(get);
            if(result != 200) {
                throw new FetchingException("Unable to fetch from "+uri+" due to error code "+result);
            }
            // Now we look at the content type
            // If doesn't start with text or plain, we ignore
            // TODO: Implement a lazy downloader instead
            // Also, might want to check Content-Length. 
            // Some binaries might be declaring an ascii type
            org.apache.commons.httpclient.Header hdr = get.getResponseHeader("Content-Type");
            String type = "unknown";
            if(hdr != null) {
                type = hdr.toExternalForm();
                if(!type.startsWith("Content-Type: text") && !type.startsWith("Content-Type: plain")) {
                    throw new FetchingException("Not going to fetch a non-text file");
                }
            }

            String txt = get.getResponseBodyAsString();
            get.releaseConnection(); 
            Page page = new MemoryPage(txt, type);
            String base = url.getProtocol()+"://"+url.getHost();
            if(url.getPort() != -1) {
                base += ":"+url.getPort();
            }
            String path = url.getPath();
            int idx = path.lastIndexOf("/");
            if(idx != -1) {
                base += path.substring(0, idx);
            }
            page.setDocumentBase(base);
            return page;
        } catch(IOException ioe) {
            throw new FetchingException("Error. "+ioe.getMessage(), ioe);
        }
    }

}
