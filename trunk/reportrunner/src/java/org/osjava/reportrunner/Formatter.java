package org.osjava.reportrunner;

public interface Formatter {

    Object format(Object input);
    void setPattern(String pattern);

}
