package org.osjava.scraping;

public interface Parser {

    public Result parse(Page page, Config cfg, Session session) throws ParsingException;
    public void startUp(Config cfg) throws Exception;
    public void bringDown(Config cfg) throws Exception;
}
