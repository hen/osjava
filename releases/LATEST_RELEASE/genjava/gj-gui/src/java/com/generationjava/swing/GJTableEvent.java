package com.generationjava.swing;

import java.util.List;
import com.generationjava.lang.Constant;

public class GJTableEvent {

    static public Constant SORT = new Constant();
    static public Constant REMOVE = new Constant();

    private List list;
    private Constant type;
    private int row;

    public void setNewOrder(List list) {
        this.type = GJTableEvent.SORT;
        this.list = list;
    }

    public List getNewOrder() {
        return this.list;
    }

    public void setRemoved(int row) {
        this.type = GJTableEvent.REMOVE;
        this.row = row;
    }

    public int getRemoved() {
        return this.row;
    }

    public Constant getType() {
        return this.type;
    }

}
