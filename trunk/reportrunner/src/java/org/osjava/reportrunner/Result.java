package org.osjava.reportrunner;

public interface Result {

    Object[] nextRow();
    boolean hasNextRow();
    void reset();

}
