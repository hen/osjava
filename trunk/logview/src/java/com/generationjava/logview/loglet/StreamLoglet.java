package com.generationjava.logview.loglet;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.LazyLog;

abstract public class StreamLoglet extends AbstractLoglet {

    public StreamLoglet(String name) {
        super(name);
    }

}
