package com.generationjava.logview;

import java.util.Iterator;

public interface LogIterator extends Iterator {

    public LogEvent nextLogEvent() throws LogViewException;

    public LogEvent currentLogEvent();

}
