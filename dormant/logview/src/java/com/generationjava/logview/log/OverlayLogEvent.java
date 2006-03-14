package com.generationjava.logview.log;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;

public class OverlayLogEvent extends AbstractLogEvent {

    private LogEvent event;
    private Map overlays;

    public OverlayLogEvent(LogEvent event) {
        this.event = event;
        this.overlays = new HashMap();
    }

    public Log getLog() {
        return this.event.getLog();
    }

    public void setLog(Log log) {
        this.event.setLog(log);
    }

    public LogField get(String fieldName) {
        if(this.overlays.containsKey(fieldName)) {
            return (LogField)this.overlays.get(fieldName);
        } else {
            return this.event.get(fieldName);
        }
    }

    public Iterator iterateFieldNames() {
        return this.event.iterateFieldNames();
    }

    public void overlay(String fieldName, LogField value) {
        this.overlays.put(fieldName, value);
    }
}
