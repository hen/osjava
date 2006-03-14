package com.generationjava.logview;

import com.generationjava.logview.log.MemoryLog;

public interface LogBuilder extends Cloneable {

    public void fillLog(MemoryLog log) throws LogViewException;
    public LogEvent parseLogEvent() throws LogViewException;

    public boolean hasMoreEvents();

    public LogSource getSource();
    public void setSource(LogSource source);

    public Object cloneObject() throws CloneNotSupportedException;

}
