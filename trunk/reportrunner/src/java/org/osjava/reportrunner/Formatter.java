package org.osjava.reportrunner;

public interface Formatter {

    String format(Object input);
    void setPattern(String pattern);

}
