package com.generationjava.logview.log;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogViewException;

public class LogletLogIterator extends AbstractLogIterator {

    private Loglet loglet;
    private LogIterator iterator;

    public LogletLogIterator(Loglet loglet, LogIterator iterator) {
        this.loglet = loglet;
        this.iterator = iterator;
    }

    public LogEvent nextLogEvent() throws LogViewException {
        LogEvent logevent = this.loglet.parseEvent(this.iterator);
        return currentLogEvent(logevent);
    }

    public boolean hasNext() {
//        System.err.println(""+iterator+":"+iterator.hasNext());
        return this.loglet.hasMoreEvents(this.iterator);
    }

}
