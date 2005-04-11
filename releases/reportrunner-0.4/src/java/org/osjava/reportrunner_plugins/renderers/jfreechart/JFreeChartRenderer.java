package org.osjava.reportrunner_plugins.renderers.jfreechart;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.*;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.apache.commons.lang.SerializationUtils;

public class JFreeChartRenderer extends JFreeChartAbstractRenderer {

	public void display(Result result, Report report, OutputStream out)	throws IOException {
		JFreeChart chart = createChart(result, report);
        SerializationUtils.serialize(chart, out);
	}

}
