package com.generationjava.logview;

import java.util.Date;
import java.util.Iterator;

public interface LogEvent {

    public Log getLog();
    public void setLog(Log log);

    public Date getTime();

    public String getID();

    // of Object?
    public LogField get(String fieldName);

    public Iterator iterateFieldNames();

    // public LogType getType(String fieldName);

}
