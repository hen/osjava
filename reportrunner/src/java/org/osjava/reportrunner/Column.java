package org.osjava.reportrunner;

public class Column {

    private String name;
    private Class format;
    private String label;
    private String pattern;

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

    public Class getFormat() {
        return this.format;
    }

    public void setFormat(Class format) {
        if(format != null) {
            this.format = format;
        }
    }

    public void setFormatAsString(String formatName) {
        if(formatName == null) {
            return;
        }
        try {
            this.format = Thread.currentThread().getContextClassLoader().loadClass(formatName);
        } catch(ClassNotFoundException cnfe) {
        }
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    // debug
    public String toString() { 
        return "Column { name="+name+", format="+format+", label="+label+", pattern="+pattern+" }";
    }


}
