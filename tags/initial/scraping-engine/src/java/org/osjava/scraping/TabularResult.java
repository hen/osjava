package org.osjava.scraping;

import java.util.Iterator;

public class TabularResult implements Result {

    private Iterator iterator;

    public TabularResult(Iterator iterator) {
        this.iterator = iterator;
    }

    // returns Iterator of <Object[]>.
    // very simple system to start with 
    // so our JdbcStore can be simple
    public Iterator iterateRows() {
        return this.iterator;
    }

}
