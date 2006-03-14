package com.generationjava.logview.renderer;

import com.generationjava.logview.Renderer;
import com.generationjava.logview.Report;

import com.generationjava.logview.report.TableReport;
import com.generationjava.logview.report.ChartReport;

abstract public class AbstractRenderer implements Renderer {

    public void render(Report report) {
        if(report instanceof TableReport) {
            System.out.println( renderTable((TableReport)report) );
        } else 
        if(report instanceof ChartReport) {
            System.out.println( renderChart((ChartReport)report) );
        }
    }

    abstract public String renderTable(TableReport report);
    abstract public String renderChart(ChartReport report);

}
