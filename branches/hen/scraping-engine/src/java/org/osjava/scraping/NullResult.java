package org.osjava.scraping;

import java.util.Iterator;
import org.apache.commons.collections.IteratorUtils;

public class NullResult implements Result {

    public Iterator iterateRows() {
        return IteratorUtils.EMPTY_ITERATOR;
    }

}
