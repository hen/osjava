package com.generationjava.logview.loglet;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

public class HeadLoglet extends StreamLoglet {

    private int count;

    public HeadLoglet(int count) {
        this("Head", count);
    }
    public HeadLoglet(String name, int count) {
        super(name);
        this.count = count;
    }

    public boolean hasMoreEvents(LogIterator logIt) {
        return (this.count != 0);
    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
        if(this.count != 0) {
            this.count--;
            return logIt.nextLogEvent();
        }

        return null;
    }

}
