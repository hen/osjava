package com.generationjava.logview.log;

import java.util.Iterator;

import com.generationjava.logview.LogEvent;

public class SimpleLogIterator extends AbstractLogIterator {

    private Iterator iterator;

    public SimpleLogIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public LogEvent nextLogEvent() {
        LogEvent logevent = (LogEvent)this.iterator.next();
        return currentLogEvent( logevent );
    }

    public boolean hasNext() {
//        System.err.println("zip");
        return this.iterator.hasNext();
    }

}
