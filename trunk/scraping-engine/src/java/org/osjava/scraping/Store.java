package org.osjava.scraping;

public interface Store {

    public void store(Result result, Config cfg, Session session) throws StoringException;

    public boolean exists(Header header, Config cfg, Session session) throws StoringException;

}
