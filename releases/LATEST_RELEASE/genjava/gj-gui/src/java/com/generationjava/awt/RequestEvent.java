package com.generationjava.awt;

public class RequestEvent {

    private String name;
    private String type;
    private Object value;

    public RequestEvent(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }

    public void setValue(Object obj) {
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return ""+this.name+":"+this.value;
    }

}