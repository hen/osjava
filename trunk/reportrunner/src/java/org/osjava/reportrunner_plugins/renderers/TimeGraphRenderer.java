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
        Result data = report.execute();
        Column[] columns = report.getColumns();

        List list = new ArrayList();

        while(data.hasNextRow()) {
            Object[] row = data.nextRow();
            Date date = (Date) row[0];

            for(int j=1; j<row.length; j++) {
                Number num = (Number) row[j];
                TimeSeries ts = null;
                if(list.size() < j) {
                    String name = "Column "+j;
                    if(columns != null && columns.length != 0) {
                        name = columns[j].getLabel();
                    }
                    ts = new TimeSeries(name, Minute.class );
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

        String time = "Time";
        if(columns != null && columns.length != 0) {
            time = columns[0].getLabel();
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            report.getLabel(),
            time,
            "",
            tsc,
            true,
            true,
            false
        );

        ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
    }

}
