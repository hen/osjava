package com.generationjava.logview.log;

import com.generationjava.logview.LogBuilder;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogViewException;

public class StreamingLogIterator extends AbstractLogIterator {

    private LogBuilder builder;

    public StreamingLogIterator(LogBuilder builder) {
        this.builder = builder;
    }

    public LogEvent nextLogEvent() throws LogViewException {
        return currentLogEvent( builder.parseLogEvent() );
    }

    public boolean hasNext() {
        return this.builder.hasMoreEvents();
    }

}
