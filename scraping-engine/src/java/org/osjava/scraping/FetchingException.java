package org.osjava.scraping;

public class FetchingException extends Exception {

    public FetchingException(String msg) {
        super(msg);
    }

    public FetchingException(String msg, Throwable t) {
        super(msg, t);
    }
}
