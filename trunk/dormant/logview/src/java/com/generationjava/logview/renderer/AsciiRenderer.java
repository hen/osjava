package com.generationjava.logview.renderer;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.generationjava.logview.report.ChartReport;
import com.generationjava.logview.report.TableReport;

public class AsciiRenderer extends AbstractRenderer {

    public String renderTable(TableReport table) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(":");
        buffer.append(table.getHeader());
        buffer.append(":\n");
        buffer.append( StringUtils.repeat("=", table.getHeader().length()+2 ) );
        buffer.append("\n");
        Iterator iterator = table.iterator();
        while(iterator.hasNext()) {
            Iterator sub = ((ArrayList)iterator.next()).iterator();
            buffer.append(":");
            if(sub.hasNext()) {
                while(sub.hasNext()) {
                    buffer.append(sub.next());
                    buffer.append(":");
                }
            } else {
                buffer.append(":");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public String renderChart(ChartReport chart) {
        // assume yField is a number
        Iterator iterator = chart.iterateKeys();
        StringBuffer buffer = new StringBuffer();
        float ratio = 40F / chart.getYMax();

        String xField = chart.getXField();

        buffer.append(xField);
        if(chart.getLink(xField) != null) {
            buffer.append("[cf.");
            buffer.append(chart.getLink(xField));
            buffer.append("]");
        }
        buffer.append("\t");
        buffer.append(chart.getYField());
        buffer.append("\n");
        while(iterator.hasNext()) {
            Object key = iterator.next();
            
            Number value = (Number)chart.getData(key);
            int count = value.intValue();
            buffer.append(key);
            buffer.append("\t");
            count *= ratio; 
            for(int i=count; i>0; i--) {
                buffer.append("*");
            }
            for(int i=count; i<40; i++) {
                buffer.append(" ");
            }
            buffer.append("\t");
            buffer.append(value.intValue());
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
