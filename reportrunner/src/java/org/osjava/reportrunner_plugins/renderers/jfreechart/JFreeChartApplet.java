package org.osjava.reportrunner_plugins.renderers.jfreechart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import org.apache.commons.lang.SerializationUtils;

public class JFreeChartApplet extends JApplet {

    private JFreeChart chart;
    
    public void init() {
        try {
            String url = getParameter("serUrl");

            if(url == null) {
                url = getDocumentBase().toString().replaceFirst("jfreechart-applet", "jfreechart");
            }

            this.chart = loadChart( new URL( url ) );

            final ChartPanel panel = new ChartPanel(chart);
            panel.setMouseZoomable(true);
            
            XYPlot plot = chart.getXYPlot();
            plot.getDomainAxis().setAutoRange(true);
            plot.getDomainAxis().setPlot(plot);
            plot.getDomainAxis().addChangeListener(plot);
            plot.getRangeAxis().setAutoRange(true);
            plot.getRangeAxis().setPlot(plot);
            plot.getRangeAxis().addChangeListener(plot);
            int sz = plot.getSeriesCount(); 

            // setup the hide line panel
            JPanel hidePanel = new JPanel();
            hidePanel.setLayout( new BoxLayout(hidePanel, BoxLayout.Y_AXIS) );

            for(int i=0; i<sz; i++) {
                String name = plot.getDataset().getSeriesName(i);
                JCheckBox box = new JCheckBox(name, true);
                final int n = i;
                box.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        JFreeChartApplet.this.setSeriesHiddenStatus(n, ((JCheckBox)ae.getSource()).isSelected());
                    }
                });
                hidePanel.add(box);
            }                                     
            
            // set colors if any set
            for(int i=0; i<sz; i++) {
                if(getParameter("color-"+i) != null) {
                    plot.getRenderer().setSeriesPaint(i, Color.decode( getParameter("color-"+i)));
                }
            }
            JPanel main = new JPanel();
            main.setLayout( new BorderLayout() );
            main.add( panel, BorderLayout.CENTER );
            main.add( hidePanel, BorderLayout.EAST );

            this.getContentPane().add(main);
            this.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSeriesHiddenStatus(int i, boolean show) {
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesVisible(i, new Boolean(show));
        this.invalidate();
        this.repaint();
    }

    private JFreeChart loadChart(URL url) throws IOException {
        return (JFreeChart) SerializationUtils.deserialize( url.openStream() );
    }

}
