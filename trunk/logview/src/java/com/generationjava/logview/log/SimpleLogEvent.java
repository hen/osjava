package com.generationjava.logview.log;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import com.generationjava.logview.LogField;

public class SimpleLogEvent extends AbstractLogEvent {

    private Map fields = new HashMap();

    public SimpleLogEvent() {
    }

    public LogField get(String fieldName) {
        return (LogField)fields.get(fieldName);
    }

    public Iterator iterateFieldNames() {
        return fields.keySet().iterator();
    }

    public void set(LogField field) {
        this.set(field.getName(), field);
    }

    public void set(String name, LogField field) {
        fields.put(name, field);
    }

    public String toString() {
        return ""+fields;
    }

}
