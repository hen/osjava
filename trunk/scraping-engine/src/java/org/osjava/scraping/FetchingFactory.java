package org.osjava.scraping;

public class FetchingFactory {

    static public Fetcher getFetcher(Config cfg, Session session) {
        String uri = cfg.getString("uri");
        if(uri == null) {
            return new NullFetcher();
        }

        if(uri.startsWith("http://")) {
            return new HttpFetcher();
        }
        if(uri.startsWith("https://")) {
            return new HttpsFetcher();
        }

        return new NullFetcher();
    }

}
