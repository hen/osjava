package org.osjava.reportrunner;

public interface Parser {

    Object parse(String input, Class type);
    void setPattern(String pattern);

}
