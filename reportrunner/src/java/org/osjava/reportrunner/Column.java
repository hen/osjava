package org.osjava.reportrunner;

public class Column implements Nameable {

    private String name;
    private Formatter formatter;
    private String label;

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

    public Formatter getFormatter() {
        return this.formatter;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    // debug
    public String toString() { 
        return "Column { name="+name+", label="+label+" }";
    }


}
