package com.genscape.reportrunner.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class JFreeChartRenderer extends AbstractRenderer {

    private JFreeChartCreator creator = null;

    public void setCreator(String name) {
        try {
            Class c = Class.forName(name);
            creator = (JFreeChartCreator) c.newInstance();
        } catch(ClassNotFoundException cnfe) {
        } catch(InstantiationException ie) {
        } catch(IllegalAccessException iae) {
        }
    }

	public void display(Result result, Report report, Writer out) throws IOException {
		throw new RuntimeException("This should not be used with a Writer. ");
	}

	public void display(Result result, Report report, OutputStream out)	throws IOException {
		JFreeChart chart = this.creator.createChart(result, report);
        SerializationUtils.serialize(chart, out);
	}

}
