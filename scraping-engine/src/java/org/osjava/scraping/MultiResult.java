package org.osjava.scraping;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiResult implements Result {

    private List list = new LinkedList();

    public MultiResult() {
    }

    public void addResult(Result result) {
        /// TODO: Might want to rethink this in case 
        ///       results hide a lot of resource or something
        this.list.add(result.iterateRows());
    }

    public Iterator iterateRows() {
        return new MultiIterator(this.list);
    }

}

class MultiIterator implements Iterator {

    private List list;
    private int current;
    private int max;

    public MultiIterator(List list) {
        this.list = list;
        this.max = list.size() - 1;
    }

    public boolean hasNext() {
        boolean b = ((Iterator)list.get(this.current)).hasNext();
        if(!b) {
            if(this.current == max) {
                return false;
            }
        }
        return true;
    }

    public Object next() {
        return ((Iterator)list.get(this.current)).next();
    }

    public void remove() {
        ((Iterator)list.get(this.current)).remove();
    }

}
