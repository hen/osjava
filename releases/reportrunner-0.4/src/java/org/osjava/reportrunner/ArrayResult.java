package org.osjava.reportrunner;

public class ArrayResult implements Result {

    private Object[] result;
    private Column[] header;
    private int idx;

    public ArrayResult(Object[] result) {
        this.result = result;
    }

    public ArrayResult(Column[] header, Object[] result) {
        this.result = result;
        this.header = header;
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

    public Column[] getHeader() {
        return this.header;
    }
}
