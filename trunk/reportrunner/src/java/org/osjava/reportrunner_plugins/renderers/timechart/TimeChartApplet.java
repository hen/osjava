package org.osjava.reportrunner_plugins.renderers.timechart;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.generationjava.io.xml.XMLNode;
import com.generationjava.io.xml.XMLParser;

public class TimeChartApplet extends JApplet {

    private JFreeChart chart;
    
    public void init() {
        try {
            String timeAttr = getParameter("timestamp-field");
            String xAttr = getParameter("x-label");
            String yAttr = getParameter("y-label");
            String extendAttr = getParameter("extend");

//            String xmlUrl = getParameter("docbase");
            String xmlUrl = ""+getDocumentBase();
            HashMap map = new HashMap();
            String xml = loadFile( new URL(xmlUrl.toString().replaceFirst("timechart-applet", "xml")) );

            StringReader reader = new StringReader(xml);
            XMLParser parser = new XMLParser();
            XMLNode report = parser.parseXML(reader);
            
            String title = report.getAttr("name");
            Enumeration data = report.enumerateNode("data");
            TimeSeriesCollection tsc = new TimeSeriesCollection();
            JPanel hidePanel = new JPanel();
            long maxTime = -1;
            while(data.hasMoreElements()) {
                XMLNode dataNode = (XMLNode) data.nextElement();
                long t = Long.parseLong(dataNode.getAttr(timeAttr));
                if(t > maxTime) {
                    maxTime = t;
                }
                Enumeration attrs = dataNode.enumerateAttr();
                while(attrs.hasMoreElements()) {
                    String key = (String) attrs.nextElement();
                    if(key.equals(timeAttr)) {
                        continue;
                    }
                    TimeSeries ts = (TimeSeries) map.get(key);
                    if(ts == null) {
                        ts = new TimeSeries(key, FixedMillisecond.class);
                        map.put(key, ts);
                        tsc.addSeries(ts);
                        
                        JCheckBox box = new JCheckBox(key, true);
                        final int i = map.size();
                        box.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent ae) {
                                TimeChartApplet.this.setSeriesHiddenStatus(i - 1, ((JCheckBox)ae.getSource()).isSelected());
                            }
                        });
                        hidePanel.add(box);
                    }                                     
                    double v = Double.parseDouble(dataNode.getAttr(key));
                    ts.addOrUpdate(new FixedMillisecond(t), v);
                }              
            }
            
            if("true".equals(extendAttr)) {
                // use maxTime to push it out to the right
                // use minTime to pull it to the left
            }

            this.chart = ChartFactory.createTimeSeriesChart(title, xAttr, yAttr, tsc, true, true, false);
            
            // set colors if any set
            XYPlot plot = chart.getXYPlot();
            int sz = plot.getSeriesCount(); 
            for(int i=0; i<sz; i++) {
                if(getParameter("color-"+i) != null) {
                    plot.getRenderer().setSeriesPaint(i, Color.decode( getParameter("color-"+i)));
                }
            }
            ChartPanel panel = new ChartPanel(chart);
            panel.setMouseZoomable(true);
            panel.add(hidePanel);

            this.getContentPane().add(panel);
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

    private String loadFile(URL url) throws IOException {
        InputStream in = url.openStream();
        int ptr = 0;
        StringBuffer buffer = new StringBuffer();
        while( (ptr = in.read()) != -1 ) {
            buffer.append((char)ptr);
        }
        in.close();
        return buffer.toString();
    }


}
