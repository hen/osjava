package com.generationjava.logview.loglet;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogFilter;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

public class FilterLoglet extends StreamLoglet {

    private LogFilter filter;
    private LogEvent cache;

    public FilterLoglet(LogFilter filter) {
        this("Filter", filter);
    }
    public FilterLoglet(String name, LogFilter filter) {
        super(name);
        this.filter = filter;
    }

    public boolean hasMoreEvents(LogIterator logIt) {
        if(!logIt.hasNext()) {
            return false;
        }

        try {
            cache = parseEvent(logIt);
        } catch(LogViewException lve) {
            lve.printStackTrace();
        }

        return (cache != null);

    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
        LogEvent event;

        if(cache != null) {
            event = cache;
            cache = null;
            return event;
        } else {
            event = logIt.nextLogEvent();
        }
        
        while(true) {
            event = this.filter.filter(event);
            if(event != null) {
                return event;
            } else
            if(logIt.hasNext()) {
                event = logIt.nextLogEvent();
            } else {
                break;
            }
        }

        return null;
    }

}
