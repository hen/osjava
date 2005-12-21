package org.osjava.reportrunner_plugins.renderers.jfreechart;

import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;
import org.osjava.reportrunner.Renderer;

import org.jfree.chart.JFreeChart;

public interface JFreeChartCreator {

	JFreeChart createChart(Result result, Report report, Renderer renderer);

}
