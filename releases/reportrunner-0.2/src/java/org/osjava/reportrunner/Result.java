package org.osjava.reportrunner;

public interface Result {

    public Object[] nextRow();
    public boolean hasNextRow();

}
