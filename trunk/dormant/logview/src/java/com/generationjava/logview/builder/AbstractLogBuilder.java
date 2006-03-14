package com.generationjava.logview.builder;

import com.generationjava.logview.LogBuilder;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogSource;
import com.generationjava.logview.LogViewException;
import com.generationjava.logview.LogViewRuntimeException;

import com.generationjava.logview.log.MemoryLog;

abstract public class AbstractLogBuilder implements LogBuilder {

    private LogSource source;

    public void setSource(LogSource src) {
        this.source = src;
    }

    public LogSource getSource() {
        return this.source;
    }

    public void fillLog(MemoryLog log) throws LogViewException {
        while(getSource().hasNext()) {
            LogEvent event = parseLogEvent();
            event.setLog(log);
            log.append(event);
        }
    }

    // this assumes one event per line....
    // need to do some work probably on this.
    /// TODO: Figure this out.
    public boolean hasMoreEvents() {
        if(getSource() == null) {
            throw new LogViewRuntimeException("No LogSource available");
        }
        return getSource().hasNext();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return cloneObject();
    }

}
