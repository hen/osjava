package org.osjava.scraping;

public class NullFetcher implements Fetcher {

    public Page fetch(String uri, Config cfg, Session session) throws FetchingException {
        return new NullPage();
    }

}
