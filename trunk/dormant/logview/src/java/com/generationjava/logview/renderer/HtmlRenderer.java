package com.generationjava.logview.renderer;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.generationjava.logview.report.ChartReport;
import com.generationjava.logview.report.TableReport;

public class HtmlRenderer extends AbstractRenderer {

    public String renderTable(TableReport table) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table border=\"0\">\n");
        String ret = StringUtils.join(table.getHeaders(), "</th><th>");
        if( (ret != null) && (ret != "")) {
            buffer.append("<tr><th>");
            buffer.append(ret);
            buffer.append("</th></tr>");
        }
        Iterator iterator = table.iterator();
        while(iterator.hasNext()) {
            Iterator sub = ((ArrayList)iterator.next()).iterator();
            buffer.append("<tr>");
            if(sub.hasNext()) {
                while(sub.hasNext()) {
                    buffer.append("<td>");
                    buffer.append(sub.next());
                    buffer.append("</td>\n");
                }
            } else {
                buffer.append("<td>&nbsp;</td>");
            }
            buffer.append("</tr>\n");
        }
        buffer.append("</table>\n");
        return buffer.toString();
    }

    public String renderChart(ChartReport chart) {
        // assume yField is a number
        Iterator iterator = chart.iterateKeys();
        StringBuffer buffer = new StringBuffer();
        float ratio = 400F / chart.getYMax();

        String xField = chart.getXField();

        buffer.append("<table border=\"0\">");
        buffer.append("<tr><th>");
        if(chart.getLink(xField) != null) {
            buffer.append("<a href=\"");
            buffer.append(chart.getLink(xField));
            buffer.append("\">");
            buffer.append(chart.getLink(xField));
            buffer.append("</a>");
        } else {
            buffer.append(xField);
        }
        buffer.append("</th><th>");
        buffer.append(chart.getYField());
        buffer.append("</th></tr>\n");
        while(iterator.hasNext()) {
            Object key = iterator.next();
            
            Number value = (Number)chart.getData(key);
            int count = value.intValue();
            buffer.append("<tr><td>");
            buffer.append(key);
            buffer.append("</td><td>");
            count *= ratio; 

            // width of count
            buffer.append("<img src=\"http://www.flamefew.net/1x1/1x1web/663399_1x1.gif\" height=\"10\" width=\"");
            buffer.append(count);
            buffer.append("\">");
            // width of 400 - count
            buffer.append("<img src=\"http://www.flamefew.net/1x1/1x1.gif\" height=\"10\" width=\"");
            buffer.append(400-count);
            buffer.append("\">");

            buffer.append("</td><td>");
            buffer.append(value.intValue());
            buffer.append("</td><tr>\n");
        }
        buffer.append("</table>\n");

        return buffer.toString();
    }

}
