package com.generationjava.logview;

import java.util.Iterator;

public interface LogEventContainer {

    public LogEvent getLogEvent(String logEventName);
    public void putLogEvent(LogEvent logEvent);

    public Iterator iterateLogEventNames();

}
