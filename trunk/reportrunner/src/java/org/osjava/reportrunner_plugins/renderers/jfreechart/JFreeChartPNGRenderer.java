package org.osjava.reportrunner_plugins.renderers.jfreechart;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class JFreeChartPNGRenderer extends AbstractRenderer {

    private int width;
    private int height;
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

    public void setHeight(String height) {
        this.height = Integer.parseInt(height);
    }

    public void setWidth(String width) {
        this.width = Integer.parseInt(width);
    }

	public void display(Result result, Report report, Writer out) throws IOException {
		throw new RuntimeException("This should not be used with a Writer. ");
	}

	public void display(Result result, Report report, OutputStream out)	throws IOException {
		ChartUtilities.writeChartAsPNG(out, this.creator.createChart(result, report), this.width, this.height);		
	}

}
