package org.osjava.scraping;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public abstract class MultiParser extends AbstractParser {

    private List queue = new LinkedList();

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        Iterator iterator = queue.iterator();
        MultiResult result = new MultiResult();
        while(iterator.hasNext()) {
            Parser parser = (Parser)iterator.next();
            // fetch new page.... how??
            result.addResult(parser.parse(page, cfg, session));
        }
        return result;
    }

    void addParser(Parser m) {
        this.queue.add(m);
    }

}
