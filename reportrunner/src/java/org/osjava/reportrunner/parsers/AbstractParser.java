package org.osjava.reportrunner.parsers;

import org.osjava.reportrunner.*;

public abstract class AbstractParser implements Parser {

    private String pattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    protected String getPattern() {
        return this.pattern;
    }

    public abstract Object parse(String input, Class type);

}
