package com.generationjava.logview.loglet;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

public class NullLoglet extends AbstractLoglet {

    public NullLoglet() {
        this("Null");
    }

    public NullLoglet(String name) {
        super(name);
    }

    public Log parse() throws LogViewException {
        return getLoglet().parse();
    }

    public LogEvent parseEvent(LogIterator iterator) throws LogViewException {
        return getLoglet().parseEvent(iterator);
    }

}
