package org.osjava.scraping;

/**
 * Fetches a piece of content for a uri
 * Usually hidden behind a FetcherFactory.
 */
public interface Fetcher {

    // allow URL url??
    public Page fetch(String uri, Config cfg, Session session) throws FetchingException;

}
