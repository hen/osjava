package org.osjava.scraping;

import java.io.IOException;
import java.io.Reader;

// TODO: Have a page know its URI
public interface Page {

    public Reader read() throws IOException;
    public String readAsString() throws IOException;
    public Page fetch(String uri, Config cfg, Session session) throws FetchingException;

    public void setDocumentBase(String documentBase);
    public String getDocumentBase();
}
