package com.generationjava.logview.reportlet;

import java.util.Iterator;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;
import com.generationjava.logview.Reportlet;
import com.generationjava.logview.Report;

import com.generationjava.logview.report.TableReport;

public class TableReportlet implements Reportlet {

    public Report report(Log log) throws LogViewException {
        TableReport table = new TableReport(log.iterateFieldNames());

        LogIterator logIt = log.iterator();
//        System.err.println(""+logIt);
        while(logIt.hasNext()) {
            LogEvent event = logIt.nextLogEvent();
            if(event == null) {
                continue;
            }
            Iterator iterator = log.iterateFieldNames();
            while(iterator.hasNext()) {
                String fieldName = (String)iterator.next();
                LogField field = event.get(fieldName);
                if(field == null) {
                    table.addField(null);
                } else {
                    table.addField(field.getValue());
                }
            }
            table.endRow();
        }
        return table;
    }

}
