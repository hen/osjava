package com.generationjava.logview.loglet;

import java.util.HashMap;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.MemoryLog;
import com.generationjava.logview.log.SimpleLogEvent;

public class UniqueLoglet extends AbstractLoglet {

    private String field;
    private HashMap map;

    public UniqueLoglet(String field) {
        this("Unique", field);
    }
    public UniqueLoglet(String name, String field) {
        super(name);
        this.field = field;
        map = new HashMap();
    }

    public String[] getFieldNames() {
        return new String[] { this.field };
    }

    public Log parse() throws LogViewException {
        MemoryLog ml = new MemoryLog(getName(), getFieldNames());
        LogEvent event = null;

        LogIterator logIt = getLoglet().parse().iterator();
        while(hasMoreEvents(logIt)) {
            event = parseEvent(logIt);
            if(event != null) {
                ml.append(event);
                event.setLog( ml );
            }
        }

        return ml;
    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
        LogEvent event = logIt.nextLogEvent();
        LogField logfield = event.get(this.field);

        if(map.containsKey( logfield.getValue() )) {
            return null;
        } else {
            SimpleLogEvent newevent = new SimpleLogEvent();
            newevent.set(this.field, logfield);
            map.put( logfield.getValue(), logfield );

            return newevent;
        }
    }

}
