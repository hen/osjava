package org.osjava.reportrunner;

public class Param {

    private String name;
    private String label;
    private Class type = String.class;
    private Object value;
    private String binding;
    private Parser parser;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Class getType() {
        return this.type;
    }

    public void setType(String typeName) {
        if(typeName == null) {
            return;
        }
        try {
            this.type = Thread.currentThread().getContextClassLoader().loadClass(typeName);
        } catch(ClassNotFoundException cnfe) {
        }
    }

    public Parser getParser() {
        return this.parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getBinding() {
        return this.binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    // debug
    public String toString() { 
        return "Param { name="+name+", type="+type+", value="+value+"/"+((value!=null)?value.getClass().getName():"NULL")+", binding="+binding+", "+parser+" }";
    }


}
