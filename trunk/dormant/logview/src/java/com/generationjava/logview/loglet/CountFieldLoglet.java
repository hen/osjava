package com.generationjava.logview.loglet;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.SequencedHashMap;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogTypes;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.MemoryLog;
import com.generationjava.logview.log.SimpleLogEvent;
import com.generationjava.logview.log.SimpleLogField;

public class CountFieldLoglet extends SinkLoglet {

    private String field;

    // hold result in so it can be dolled out one at a time
    private LogIterator myIterator;

    // iterator we're currently working on
    private LogIterator argIterator;

    public CountFieldLoglet(String field) {
        this("Count-"+field, field);
    }
    public CountFieldLoglet(String name, String field) {
        super(name);
        this.field = field;
    }

    public String[] getFieldNames() {
        return new String[] { this.field, "count", "total" };
    }

    public boolean hasMoreEvents(LogIterator logIt) {
        if(myIterator == null) {
            return logIt.hasNext();
        } else {
            return myIterator.hasNext();
        }
    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
//        System.err.println("bing");
        if( (myIterator != null) && (logIt == argIterator) ) {
            if(myIterator.hasNext()) {
//                System.err.println("bing1");
                return myIterator.nextLogEvent();
            } else {
//                System.err.println("bing2");
                return null;
            }
        }

        this.argIterator = logIt;

        LogEvent event = null;
        LogField logfield = null;
        MultiMap map = new MultiHashMap(new SequencedHashMap());
        int sz = 0;

        while(logIt.hasNext()) {
            event = logIt.nextLogEvent();
            logfield = event.get(this.field);
            if(logfield != null) {
                map.put( logfield.getValue(), logfield );
                sz++;
            } else {
                System.err.println("Had null logfield.");
            }
        }

        MemoryLog ml = new MemoryLog(getName(), getFieldNames());

        SimpleLogField total = new SimpleLogField( "total", LogTypes.INTEGER,
                                            new Integer(sz) ); 

        Iterator iterator = map.values().iterator();
        while(iterator.hasNext()) {
            SimpleLogEvent newevent = new SimpleLogEvent();
            newevent.setLog( ml );
            Object obj = iterator.next();

            if(obj instanceof List) {
                logfield = new SimpleLogField(
                          "count",
                          LogTypes.INTEGER,
                          new Integer( ((List)obj).size() ) );
                newevent.set(logfield);
                newevent.set( (LogField)((List)obj).get(0) );
            } else {
                logfield = new SimpleLogField(
                          "count",
                          LogTypes.INTEGER,
                          new Integer(1) );
                newevent.set(logfield);
                newevent.set( (LogField)obj );
            }
            newevent.set( total );
            ml.append( newevent );
//sz++;
        }
//System.err.println("SZ:"+sz);
        this.myIterator = ml.iterator();

        return parseEvent(argIterator);
    }

}
