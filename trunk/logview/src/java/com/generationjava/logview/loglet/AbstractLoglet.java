package com.generationjava.logview.loglet;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.LazyLog;

abstract public class AbstractLoglet implements Loglet {

    private Loglet loglet;
    private String name;

    public AbstractLoglet(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Log parse(Log log) throws LogViewException {
        setLoglet(new SourceLoglet(log));
        return parse();
    }

    public Log parse(Loglet loglet) throws LogViewException {
        setLoglet(loglet);
        return parse();
    }

    // really for a StreamLoglet, but works fine for a SinkLoglet 
    // too. Possibly have SinkLoglet do some kind of MemoryLog 
    // returning nonsense.
    // TODO abvoe.
    public Log parse() throws LogViewException {
        return new LazyLog(getLoglet(), this, getFieldNames()); 
    }

    public void setLoglet(Loglet loglet) {
        this.loglet = loglet;
    }

    public Loglet getLoglet() {
        return this.loglet;
    }

    public String[] getFieldNames() {
        return loglet.getFieldNames();
    }

    public boolean hasMoreEvents(LogIterator logIt) {
        return logIt.hasNext();
    }

}
