package com.generationjava.logview.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;

// Hold a Log in memory and dole out when wanted..
public class MemoryLog implements Log {

    private String name;
    private List logs;
    private String[] headers;

    public MemoryLog(String name, String[] headers) {
        this(name, headers, new ArrayList());
    }

    public MemoryLog(String name, String[] headers, List logs) {
        this.name = name;
        this.headers = headers;
        this.logs = logs;
    }

    /// Log interface
    public void clear() {
        logs.clear();
    }

    public void append(LogEvent log) {
        logs.add(log);
    }

    public LogIterator iterator() {
//        System.err.println("ITR:"+count());
        return new SimpleLogIterator(this.logs.iterator());
    }

    public String getName() {
        return this.name;
    }

    public Iterator iterateFieldNames() {
        return IteratorUtils.arrayIterator(this.headers);
    }
    /// end of Log interface
    
    public int count() {
        return this.logs.size();
    }

    public Object cloneObject() throws CloneNotSupportedException {
        return clone();
    }

    protected Object clone() throws CloneNotSupportedException {
        MemoryLog log = new MemoryLog(name, headers);
        log.logs = new ArrayList( logs );
        return log;
    }

    public String toString() {
        return ""+this.logs;
    }

}
