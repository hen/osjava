package com.generationjava.logview.builder.parse;

import com.generationjava.lang.Constant;

abstract public class AbstractParseToken implements ParseToken {

    private String value;
    private String type;

    public AbstractParseToken(String value, Constant type) {
        this(value, type.toString());
    }
    public AbstractParseToken(String value, String type) {
        setValue(value);
        setType(type);
    }

    public void setValue(String str) {
        this.value = str;
    }

    public String getValue() {
        return this.value;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        return "$["+this.value+":"+this.type+"]";
    }

}
