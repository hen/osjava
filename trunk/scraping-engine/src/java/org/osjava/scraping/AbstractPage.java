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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.io.BufferedReader;

import org.apache.log4j.Logger;

public abstract class AbstractPage implements Page {

    private static Logger logger = Logger.getLogger(AbstractPage.class);

    private String documentBase;

    public AbstractPage() {
    }

    public abstract Reader read() throws IOException;

    public Page fetch(String uri, Config cfg, Session session) throws FetchingException {

        // TODO: This knows about HTTP PROTOCOL urls. Fix.
        int idx = uri.indexOf("://");
        if(idx == -1) {
            // TODO: also check it is less than 15 or something??
            if(uri.startsWith("/")) {
                // Here we need to work backwards to the 'root' of the 
                // protocol. How do we define that??
                int idx2 = this.documentBase.indexOf("://");
                idx2 = this.documentBase.indexOf("/", idx2 + 3);
                uri = this.documentBase.substring(0, idx2) + "/" + uri;
            } else {
                uri = this.documentBase + "/" + uri;
            }
        }

        
        logger.debug("Fetching: "+uri);
        Fetcher fetcher = FetchingFactory.getFetcher(cfg, session);
        Page page = fetcher.fetch(uri, cfg, session);
        return page;
    }

    public void setDocumentBase(String documentBase) {
        logger.debug("Document base: "+documentBase);
        this.documentBase = documentBase;
    }

    public String getDocumentBase() {
        return this.documentBase;
    }

    public String readAsString() throws IOException {
        Reader rdr = null;
        try {       
            rdr = this.read();
            BufferedReader bfr = new BufferedReader(rdr);
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while( (line = bfr.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            return buffer.toString();
        } finally {
            if(rdr != null) {
                try {
                    rdr.close();
                } catch(IOException ioe) {
                    // ignore
                }
            }
        }
    }

}
