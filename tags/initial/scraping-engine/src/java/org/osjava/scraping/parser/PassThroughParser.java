package org.osjava.scraping.parser;

import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import org.osjava.scraping.*;

public class PassThroughParser extends AbstractParser {

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        try {
            ArrayList list = new ArrayList(1);
            list.add( new Object[] { page.readAsString() } );
            return new TabularResult(list.iterator());
        } catch(IOException ioe) {
            throw new ParsingException("Unable to read page", ioe);
        }
    }
}
