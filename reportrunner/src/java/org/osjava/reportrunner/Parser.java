package org.osjava.reportrunner;

public interface Parser {

    Object parse(String input);
    void setPattern(String pattern);

}
