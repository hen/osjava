package org.osjava.reportrunner.parsers;

import org.osjava.reportrunner.*;

public abstract class AbstractParser implements Parser {

    private String pattern;
    private Class type;

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    protected String getPattern() {
        return this.pattern;
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

    public Class getType() {
        return this.type;
    }

    public abstract Object parse(String input);

}
