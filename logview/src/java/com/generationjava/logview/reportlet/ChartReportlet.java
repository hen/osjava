package com.generationjava.logview.reportlet;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;
import com.generationjava.logview.Reportlet;
import com.generationjava.logview.Report;

import com.generationjava.logview.report.ChartReport;

public class ChartReportlet implements Reportlet {

    private String xField;
    private String yField;

    public ChartReportlet(String xField, String yField) {
        this.xField = xField;
        this.yField = yField;
    }

    public Report report(Log log) throws LogViewException {
        ChartReport chart = new ChartReport(xField, yField);

        LogIterator logIt = log.iterator();

        while(logIt.hasNext()) {
            LogEvent event = logIt.nextLogEvent();
            LogField xLog = event.get(xField);
            LogField yLog = event.get(yField);
            if(xLog == null) {
                throw new LogViewException("Unable to view column: "+xField);
            }
            if(yLog == null) {
                throw new LogViewException("Unable to view column: "+yField);
            }
//            System.err.println("xLog["+xLog+"] yLog["+yLog+"]");
            chart.addData( xLog.getValue(), yLog.getValue() );
        }
        return chart;
    }

}
