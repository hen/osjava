package com.genscape.reportrunner.util;

import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

import org.jfree.chart.JFreeChart;

public interface JFreeChartCreator {

	JFreeChart createChart(Result result, Report report);

}
