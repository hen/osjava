package org.osjava.scraping;

public abstract class LoopParser extends AbstractParser {

    private boolean finished = false;

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        MultiResult result = new MultiResult();
        int index = 0;
        while(!finished) {
            // fetch new page.... how??
            result.addResult(this.parse(page, cfg, session, index));
            index++;
        }
        return result;
    }

    abstract Result parse(Page page, Config cfg, Session session, int index) throws ParsingException;

    void finished() {
        this.finished = true;
    }

}
