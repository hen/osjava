package org.osjava.scraping;

public abstract class CheckingParser extends AbstractParser {

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        Header header = parseHeader(page, cfg, session);
        Store store = StoreFactory.getStore(cfg, session);
        try {
            boolean found = store.exists(header, cfg, session);
            if(found) {
                return new NullResult();
            } else {
                return parseBody(page, header, cfg, session);
            }
        } catch(StoringException se) {
            return new NullResult();
        }
    }

    public abstract Header parseHeader(Page page, Config cfg, Session session) throws ParsingException;
    public abstract Result parseBody(Page page, Header header, Config cfg, Session session) throws ParsingException;

}
