package com.generationjava.logview.log;

import java.util.Date;
import java.util.Iterator;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;

abstract public class AbstractLogEvent implements LogEvent {

    private Log log;

    public AbstractLogEvent() {
    }

    public Log getLog() {
        return this.log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Date getTime() {
        return (Date)get("time").getValue();
    }

    public String getID() {
        return (String)get("id").getValue();
    }

    abstract public LogField get(String fieldName);

    abstract public Iterator iterateFieldNames();

}
