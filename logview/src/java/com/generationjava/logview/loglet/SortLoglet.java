package com.generationjava.logview.loglet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.generationjava.collections.CollectionsW;
import com.generationjava.collections.SortedLimitedList;

import com.generationjava.logview.Log;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogType;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.LogEventComparator;
import com.generationjava.logview.log.MemoryLog;

public class SortLoglet extends SinkLoglet {

    private String field;
    private int capacity;
    private Comparator comparator;

    public SortLoglet(String field, int capacity, Comparator comparator) {
        this("Sorted-by-"+field, field, capacity, comparator);
    }
    public SortLoglet(String name, String field, int capacity, Comparator comparator) {
        super(name);
        this.field = field;
        this.capacity = capacity;
        this.comparator = comparator;
    }

    public Log parse(Log log) throws LogViewException {
        LogEvent event;

        List sllist = new SortedLimitedList(this.capacity, new LogEventComparator(this.field, this.comparator));


        LogIterator logIt = log.iterator();
        // sorting happens here
        while(logIt.hasNext()) {
            event = logIt.nextLogEvent();
            sllist.add(event);
        }

        MemoryLog ml = new MemoryLog(getName(), (String[])CollectionsW.iteratorToArray(log.iterateFieldNames(), new String[0]), sllist);

        return ml;
    }

}
