package org.osjava.scraping;

public class ParsingException extends Exception {

    public ParsingException(String msg) {
        super(msg);
    }

    public ParsingException(String msg, Throwable t) {
        super(msg, t);
    }
}
