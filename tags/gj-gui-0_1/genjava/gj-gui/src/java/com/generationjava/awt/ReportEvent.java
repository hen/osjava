package com.generationjava.awt;

public class ReportEvent {

    private String name;
    private String type;
    private Object value;

    public ReportEvent(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return ""+this.name+":"+this.value;
    }

}