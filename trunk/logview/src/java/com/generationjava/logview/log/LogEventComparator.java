package com.generationjava.logview.log;

import java.util.Comparator;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;

public class LogEventComparator implements Comparator {

    private String field;
    private Comparator comparator;

    public LogEventComparator(String field, Comparator comparator) {
        this.field = field;
        this.comparator = comparator;
    }

    public int compare(Object obj1, Object obj2) {
        if(obj1 instanceof LogEvent) {
            if(obj2 instanceof LogEvent) {
                LogEvent le1 = (LogEvent)obj1;
                LogEvent le2 = (LogEvent)obj2;
                int i= this.comparator.compare(
                         le1.get(this.field).getValue(),
                         le2.get(this.field).getValue() );
//                System.err.println(""+le1.get(this.field).getValue()+" vs "+le2.get(this.field).getValue()+" is "+i);
                return i;
            }
        }
        return 0;
    }

}
