package com.generationjava.logview.type;

import com.generationjava.logview.Type;

abstract public class AbstractType implements Type {

    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return value.toString();
    }

}
