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
        int idx = uri.indexOf("://");
        if(idx == -1) {
            // TODO: also check it is less than 15 or something??
            uri = this.documentBase + "/" + uri;
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
