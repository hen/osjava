package org.osjava.scraping;

// can notify success and error. How to separate?
// have a Formatter for formatting the thing? Opposite of a 
// Converter really

public interface Notifier {

    public void notify(Config cfg, Session session) throws NotificationException;

}
