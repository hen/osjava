package org.osjava.reportrunner;

public class NullResult implements Result {

    public boolean hasNextRow() {
        return false;
    }

    public Object[] nextRow() {
        throw new RuntimeException("No rows available from a NullResult. ");
    }

    public void reset() {
    }

    public Column[] getHeader() {
        return null;
    }

}
