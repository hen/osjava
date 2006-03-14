package com.generationjava.logview.log;

import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogViewRuntimeException;
import com.generationjava.logview.LogViewException;

abstract public class AbstractLogIterator implements LogIterator {

    private LogEvent logevent;

    protected void setCurrentLogEvent(LogEvent le) {
        this.logevent = le;
    }

    public LogEvent currentLogEvent() {
        return this.logevent;
    }

    protected LogEvent currentLogEvent(LogEvent le) {
//        System.err.println("LE:"+le);
        this.logevent = le;
        return this.logevent;
    }

    public Object next() {
        try {
            return nextLogEvent();
        } catch(LogViewException lve) {
            throw new LogViewRuntimeException("Exception in next()", lve);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from this iterator.");
    }

}
