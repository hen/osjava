package org.osjava.scraping;

public class StoringException extends Exception {

    public StoringException(String msg) {
        super(msg);
    }

    public StoringException(String msg, Throwable t) {
        super(msg, t);
    }
}
