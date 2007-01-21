package org.osjava.reportrunner_plugins.renderers;

import java.io.*;
import java.util.*;

import org.osjava.reportrunner.*;

import org.jfree.chart.*;
import org.jfree.data.time.*;

public class TimeGraphRenderer extends AbstractRenderer {

    public void display(Report report, Writer out) throws IOException {
        throw new RuntimeException("This should not be used with a Writer. ");
    }

    public void display(Report report, OutputStream out) throws IOException {
        Object[] data = report.execute();

        List list = new ArrayList();

        for(int i=0; i<data.length; i++) {
            Object[] row = (Object[]) data[i];
            Date date = (Date) row[0];

            for(int j=1; j<row.length; j++) {
                Number num = (Number) row[j];
                TimeSeries ts = null;
                if(list.size() < j) {
                    ts = new TimeSeries("HACK", Minute.class );
                    list.add(ts);
                } else {
                    ts = (TimeSeries) list.get(j-1);
                }
                ts.addOrUpdate( new Minute(date), num );
            }
        }

        TimeSeriesCollection tsc = new TimeSeriesCollection();
        for(int i=0; i<list.size(); i++) {
            tsc.addSeries( (TimeSeries) list.get(i) );
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Title", 
            "Timestamp",
            "Value",
            tsc,
            true,
            true,
            false
        );

        ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
    }

}
