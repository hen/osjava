package com.generationjava.logview.log;

import com.generationjava.logview.LogField;
import com.generationjava.logview.LogType;
import com.generationjava.logview.LogTypes;

public class SimpleLogField implements LogField {

    private Object value;
    private LogType type;
    private String name;

    public SimpleLogField(String name, Object value) {
        this(name, LogTypes.STRING, value);
    }

    public SimpleLogField(String name, LogType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public LogType getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

}
