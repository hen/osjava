package org.osjava.reportrunner;

public class ArrayResult implements Result {

    private Object[] result;
    private int idx;

    public ArrayResult(Object[] result) {
        this.result = result;
    }

    public boolean hasNextRow() {
        return idx != result.length;
    }

    public Object[] nextRow() {
        return (Object[]) result[idx++];
    }

    public void reset() {
        this.idx = 0;
    }
}
