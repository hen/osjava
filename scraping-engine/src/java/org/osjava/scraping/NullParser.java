package org.osjava.scraping;

public class NullParser implements Parser {

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        return new NullResult();
    }
    public void startUp(Config cfg) throws Exception {
    }
    public void bringDown(Config cfg) throws Exception {
    }
}
