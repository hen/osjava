package org.osjava.reportrunner_plugins.renderers.jfreechart;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class JFreeChartPNGRenderer extends JFreeChartAbstractRenderer {

    private int width;
    private int height;

    public void setHeight(String height) {
        this.height = Integer.parseInt(height);
    }

    public void setWidth(String width) {
        this.width = Integer.parseInt(width);
    }

	public void display(Result result, Report report, OutputStream out)	throws IOException {
		ChartUtilities.writeChartAsPNG(out, createChart(result, report), this.width, this.height);		
	}

}
