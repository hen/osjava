package com.generationjava.logview.log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import com.generationjava.logview.Log;
import com.generationjava.logview.LogBuilder;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;

// A log which can stream the log-events through the system.
public class StreamLog implements Log {

    private String name;
    private String[] headers;
    private LogBuilder builder;

    public StreamLog(String name, String[] headers, LogBuilder builder) {
        this.name = name;
        this.headers = headers;
        this.builder = builder;
    }

    /// Log interface
    public LogIterator iterator() {
        try {
            return new StreamingLogIterator( (LogBuilder)builder.cloneObject() );
        } catch(CloneNotSupportedException cnse) {
//            System.err.println("ARGH");
            return null;
        }
    }

    public String getName() {
        return this.name;
    }

    public Iterator iterateFieldNames() {
        return IteratorUtils.arrayIterator(this.headers);
    }

    protected Object clone() throws CloneNotSupportedException {
        return new StreamLog( this.name, this.headers, (LogBuilder)builder.cloneObject() );
    }

    /// end of Log interface

    public Object cloneObject() throws CloneNotSupportedException {
        return clone();
    }

}
