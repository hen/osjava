package com.generationjava.logview.loglet;

import com.generationjava.collections.CollectionsW;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

public class SourceLoglet extends AbstractLoglet {

    private Log log;

    public SourceLoglet(Log log) {
        this("Source", log);
    }

    public SourceLoglet(String name, Log log) {
        super(name);
        this.log = log;
    }

    public Log parse() {
        return this.log;
    }

    public String[] getFieldNames() {
        return (String[])CollectionsW.iteratorToArray( log.iterateFieldNames(), new String[0] );
    }

    public LogEvent parseEvent(LogIterator iterator) throws LogViewException {
        if(iterator.hasNext()) {
            return iterator.nextLogEvent();
        } else {
            throw new LogViewException("Used iterator passed in.");
        }
    }

}
