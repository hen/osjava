package com.generationjava.logview.loglet;

import java.util.Iterator;
import java.util.List;

import com.generationjava.logview.Log;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogTypes;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.MemoryLog;
import com.generationjava.logview.log.SimpleLogEvent;
import com.generationjava.logview.log.SimpleLogField;

public class CountLoglet extends SinkLoglet {

    public CountLoglet() {
        this("Count");
    }
    public CountLoglet(String name) {
        super(name);
    }

    public String[] getFieldNames() {
        return new String[] { "count" };
    }

    public Log parse(Log log) throws LogViewException {
        int sz = 0;
        if(log instanceof MemoryLog) {
            sz = ( (MemoryLog)log ).count();
        } else {
            LogIterator logIt = log.iterator();
            while(logIt.hasNext()) {
                logIt.next();
                sz++;
            }
        }

        // create a very simple Log object
        MemoryLog ml = new MemoryLog(getName(), getFieldNames());

        SimpleLogEvent event = new SimpleLogEvent();
        event.setLog( ml );
        SimpleLogField field = new SimpleLogField( "count", LogTypes.INTEGER, new Integer(sz)); 
        event.set("count", field);
        ml.append( event );

        return ml;
    }

}
