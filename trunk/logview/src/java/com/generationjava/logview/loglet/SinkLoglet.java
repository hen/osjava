package com.generationjava.logview.loglet;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogViewException;

// A Loglet that Sinks all the LogEvents.
abstract public class SinkLoglet extends AbstractLoglet {

    private LogIterator cache;

    public SinkLoglet(String name) {
        super(name);
    }

    public LogEvent parseEvent(LogIterator iterator) throws LogViewException {
        if(cache == null) {
            cache = parse().iterator();
        }

        if(cache.hasNext()) {
            return cache.nextLogEvent();
        } else {
            return null;
        }
    }

}
