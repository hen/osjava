package com.generationjava.logview.log;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import com.generationjava.logview.Log;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;
import com.generationjava.logview.LogViewRuntimeException;

// Hold a Log in memory and dole out when wanted..
public class LazyLog implements Log {

    private Loglet loglet;
    private Log log;
    private String[] headers;

    private LazyLog() {
    }

    public LazyLog(Loglet wrapped, Loglet loglet, String[] headers) {
        this.loglet = loglet;
        this.headers = headers;
        try {
            log = wrapped.parse();
//            System.err.println("LOG:"+log);
        } catch(LogViewException lve) {
            throw new LogViewRuntimeException("Unable to parse Loglet", lve);
        }
    }

    /// Log interface
    public LogIterator iterator() {
        return new LogletLogIterator(loglet, log.iterator());
    }

    public String getName() {
        return log.getName();
    }

    public Iterator iterateFieldNames() {
        return IteratorUtils.arrayIterator(this.headers);
    }
    
    public Object cloneObject() throws CloneNotSupportedException {
        return clone();
    }

    protected Object clone() throws CloneNotSupportedException {
        LazyLog log = new LazyLog();
        log.log = (Log)this.log.cloneObject();
        log.loglet = this.loglet;
        log.headers = this.headers;
        return log;
    }
    /// end of Log interface

}
