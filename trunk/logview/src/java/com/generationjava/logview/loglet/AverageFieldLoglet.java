package com.generationjava.logview.loglet;

import java.util.Iterator;
import java.util.List;

import java.util.HashMap;

import com.generationjava.math.Average;
import org.apache.commons.lang.NumberUtils;

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

// Takes the average of a field based on another field. So like 
// CountField except that it does different maths. These
// therefore need refactoring into a 'GroupByThisMaths'Loglet
public class AverageFieldLoglet extends SinkLoglet {

    private String field;
    private String numberField;

    // hold result in so it can be dolled out one at a time
    private LogIterator myIterator;

    // iterator we're currently working on
    private LogIterator argIterator;

    public AverageFieldLoglet(String field, String numberField) {
        this("Average-"+field, field, numberField);
    }
    public AverageFieldLoglet(String name, String field, String numberField) {
        super(name);
        this.field = field;
        this.numberField = numberField;
    }

    public String[] getFieldNames() {
        return new String[] { this.field, "average" };
    }

    public boolean hasMoreEvents(LogIterator logIt) {
        if(myIterator == null) {
            return logIt.hasNext();
        } else {
            return myIterator.hasNext();
        }
    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
        if( (myIterator != null) && (logIt == argIterator) ) {
            if(myIterator.hasNext()) {
                return myIterator.nextLogEvent();
            } else {
                return null;
            }
        }

        this.argIterator = logIt;

        LogEvent event = null;
        LogField logfield = null;
        HashMap map = new HashMap();
        HashMap fieldsMap = new HashMap();
        int sz = 0;

        while(logIt.hasNext()) {
            event = logIt.nextLogEvent();
            logfield = event.get(this.field);
            if(logfield != null) {
                Object value = logfield.getValue();

                // just save this for later.
                fieldsMap.put(value, logfield);
                
                LogField numberLogField = event.get(this.numberField);
                if(numberLogField == null) {
                    System.err.println("Had null numberLogField. Treat as non-existent. ");
                    continue;
                }
                Object numberValue = numberLogField.getValue();
                if(!(numberValue instanceof Number)) {
                    numberValue = NumberUtils.createNumber(numberValue.toString());
                }
                int num = ((Number)numberValue).intValue();
                Object obj = (Object)map.get( value );
                if(obj == null) {
                    map.put(value, new Average(num));
                } else {
                    Average avg = (Average)obj;
                    avg.add(num);
                }
            } else {
                // throw exception...
                System.err.println("Had null field: " +this.field);
            }
        }

        // now have a map of value to its average
        // so we turn this into a log to pass on

        MemoryLog ml = new MemoryLog(getName(), getFieldNames());

        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
            SimpleLogEvent newevent = new SimpleLogEvent();
            newevent.setLog( ml );
            Object key = iterator.next();
            Average avg = (Average)map.get(key);
            logfield = new SimpleLogField(
                      "average",
                      LogTypes.INTEGER,
                      new Integer( avg.average() ) );
            newevent.set(logfield);
            newevent.set((LogField)fieldsMap.get(key));

            ml.append( newevent );
        }
        this.myIterator = ml.iterator();

        return parseEvent(argIterator);
    }

}
