package org.osjava.scraping;

public class NotificationException extends Exception {

    public NotificationException(String msg) {
        super(msg);
    }

    public NotificationException(String msg, Throwable t) {
        super(msg, t);
    }
}
