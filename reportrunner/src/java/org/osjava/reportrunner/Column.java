package org.osjava.reportrunner;

public class Column implements Nameable {

    private String name;
    private Formatter formatter;
    private String label;

    /**
     * Constructs a Column with a name and label of text. 
     */
    public Column(String text) {
        this(text, text);
    }

    public Column(String name, String label) {
        this.name = name;
        this.label = label;
    }

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

    // helper
    public static Column[] createColumns(String[] names) {
        Column[] columns = new Column[names.length];
        int sz = columns.length;
        for(int i=0; i < sz; i++) {
            columns[i] = new Column(names[i]);
        }
        return columns;
    }


}
