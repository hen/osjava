package org.osjava.scraping;

import com.generationjava.lang.ClassW;

public class ParserFactory {

    static public Parser getParser(Config cfg, Session session) {
        String parserClass = cfg.getString("parser");
        if(parserClass == null) {
            return new NullParser();
        }
        Parser parser = (Parser)ClassW.createObject(parserClass);
        return parser;
    }

}
