package com.generationjava.swing;

// used to hold sorting data when there's a listener.
public class GJTableSortIndex {

    private int index;
    private Object data;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public String toString() {
        return "D["+index+"]='"+data+"'";
    }

}
