package org.osjava.scraping;

public abstract class AbstractParser implements Parser {

    abstract public Result parse(Page page, Config cfg, Session session) throws ParsingException;

    public void startUp(Config cfg) throws Exception { }
    public void bringDown(Config cfg) throws Exception { }

    // helper methods
    // ??

}
