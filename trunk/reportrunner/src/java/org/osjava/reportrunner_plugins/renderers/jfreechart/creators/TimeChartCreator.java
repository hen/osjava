package org.osjava.reportrunner_plugins.renderers.jfreechart.creators;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.osjava.reportrunner.ArrayResult;
import org.osjava.reportrunner.Column;
import org.osjava.reportrunner.Renderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;
import org.osjava.reportrunner_plugins.renderers.jfreechart.JFreeChartCreator;

public class TimeChartCreator implements JFreeChartCreator {
    
    private int xAxisColumn = 0;                   // x axis defaults to using first column from the Result
    private List[] yAxisColumns = null;            // y1 & y2 axis column numbers from the Result
    private String y1AxisTitle = null; 
    private String y2AxisTitle = null;  
    private String[][] yLabels = new String[2][];  // labels for the series for each axis
    
    private Column[] columns;
    
    private void initSettings(Report report, Renderer renderer) {
        this.columns = report.getColumns();
        
        Map variableMap = renderer.getVariables();
        String xAxis = (String) variableMap.get("timeValueColumn");
        String y1AxisTitleStr = (String) variableMap.get("y1AxisTitle");
        String y2AxisTitleStr = (String) variableMap.get("y2AxisTitle");
        String y1Axis = (String) variableMap.get("y1AxisColumns");
        String y2Axis = (String) variableMap.get("y2AxisColumns");
        
        if (xAxis != null) {
            try {
                this.xAxisColumn = Integer.parseInt(xAxis);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid xAxis value: '" + xAxis + "'");
            }
        }
        
        if (y1AxisTitleStr != null) {
            this.y1AxisTitle = y1AxisTitleStr;
        }
        
        if (y2AxisTitleStr != null) {
            this.y2AxisTitle = y2AxisTitleStr;
        }
        
        this.yAxisColumns = new List[2];        
        
        this.yAxisColumns[0] = new ArrayList();
        if (y1Axis != null) {               
            String[] y1AxisStrings = y1Axis.split(",");            
            
            for (int j = 0; j < y1AxisStrings.length; j++) {                    
                try {
                    this.yAxisColumns[0].add(new Integer(y1AxisStrings[j]));
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Invalid y1Axis value: '" + y1AxisStrings[j] + "'");
                }
            }               
        } else {                        
            // By default, everything in the Result after position 0 goes in the y1Axis columns           
            int length = columns.length - 1;
            for (int i = 0; i < length; i++) {
                this.yAxisColumns[0].add(new Integer(i + 1));                
            }
        }
        
        if (y2Axis != null) {                             
            String[] y2AxisStrings = y2Axis.split(",");
            this.yAxisColumns[1] = new ArrayList();
            
            for (int j = 0; j < y2AxisStrings.length; j++) {                    
                try {
                    this.yAxisColumns[1].add(new Integer(y2AxisStrings[j]));                        
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Invalid y1Axis value: '" + y2AxisStrings[j] + "'");
                }
            }                               
        }   
                
        this.yLabels[0] = new String[this.yAxisColumns[0].size()];        
        
        int i = 0;
        for (Iterator iter = this.yAxisColumns[0].iterator(); iter.hasNext(); i++) {        
            int columnIdx = ((Integer) iter.next()).intValue();            
            this.yLabels[0][i] = columns[columnIdx].getLabel();
        }
        
        if (this.yAxisColumns[1] != null) {
            this.yLabels[1] = new String[this.yAxisColumns[1].size()];
            i = 0;
            for (Iterator iter = this.yAxisColumns[1].iterator(); iter.hasNext(); i++) {        
                int columnIdx = ((Integer) iter.next()).intValue();            
                this.yLabels[1][i] = columns[columnIdx].getLabel();
            }           
        }
        
    }
    
    public JFreeChart createChart(Result result, Report report, Renderer renderer) {
        this.initSettings(report, renderer);       
        return this.createChart(result, report.getLabel());        
    }
    
    public JFreeChart createChart(Result result, String chartTitle) {                               
        List[] yData = new List[2];
        yData[0] = new ArrayList();
        yData[1] = new ArrayList();
        
        // Create TimeSeries objects
        for (int axis = 0; axis < 2; axis++) {
            if (this.yLabels[axis] != null) {
                for (int i = 0; i < this.yLabels[axis].length; i++) {
                    yData[axis].add(new TimeSeries(this.yLabels[axis][i], Minute.class));
                }
            }
        }
        
        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            Date date = (Date) row[this.xAxisColumn];
                                 
            for (int axis = 0; axis < 2; axis++) {   
                int i = 0;
                for (Iterator iter = yData[axis].iterator(); iter.hasNext(); i++) {
                    Number num = (Number) row[((Integer) this.yAxisColumns[axis].get(i)).intValue()];                    
                    TimeSeries ts = (TimeSeries) iter.next();
                    ts.addOrUpdate(new Minute(date), num);
                }
            }
            
        }       
        
        TimeSeriesCollection y1Tsc = new TimeSeriesCollection();
        for(int i=0; i< yData[0].size(); i++) {
            y1Tsc.addSeries((TimeSeries) yData[0].get(i));            
        }        
        
        TimeSeriesCollection y2Tsc = null;
        if (yData[1].size() > 0) {
            y2Tsc = new TimeSeriesCollection();
            for(int i=0; i< yData[1].size(); i++) {
                y2Tsc.addSeries((TimeSeries) yData[1].get(i));                
            }            
        }
        
        String time = "Time";
        if(this.columns != null && this.columns.length != 0) {
            time = columns[this.xAxisColumn].getLabel();
        }
        
        JFreeChart chart = this.createTimeSeriesChart(
                chartTitle,
                time,
                this.y1AxisTitle, this.y2AxisTitle,
                y1Tsc, y2Tsc,
                true,
                true,
                false
        );  
        
        return chart;
    }
    
    private JFreeChart createTimeSeriesChart(
            String title,
            String timeAxisLabel,
            String valueAxisLabel1,
            String valueAxisLabel2,
            XYDataset data,
            XYDataset y2data,
            boolean legend,
            boolean tooltips,
            boolean urls) {
        
        DateAxis timeAxis = new DateAxis(timeAxisLabel);
        
        // only show midnight ticks
        timeAxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 1));
        
        // set the time format
        SimpleDateFormat format = new SimpleDateFormat("EEE dd");
        timeAxis.setDateFormatOverride(format);
        
        // reduce the default margins on the time axis
        timeAxis.setLowerMargin(0.02);        
        timeAxis.setUpperMargin(0.02);
        
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel1);
        valueAxis.setAutoRangeIncludesZero(false); // override default
        
        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {            
            tooltipGenerator = new StandardXYToolTipGenerator("{1}, {2}", new SimpleDateFormat("MM/dd/yyyy hh:mm"), 
                    new DecimalFormat("0.00"));
        }
        
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title,timeAxisLabel,valueAxisLabel1,data,true,true,false);     
        XYPlot plot = chart.getXYPlot();
        StandardXYItemRenderer renderer1 = new StandardXYItemRenderer(
                StandardXYItemRenderer.LINES,
                tooltipGenerator,
                urlGenerator);
      
        plot.setRenderer(renderer1);
        plot.setDomainAxis(timeAxis);
        
        // AXIS 2
        if (y2data != null) {
            NumberAxis axis2 = new NumberAxis(valueAxisLabel2);
            axis2.setAutoRangeIncludesZero(true);
            plot.setRangeAxis(1, axis2);
            plot.setRangeAxisLocation(1, AxisLocation.TOP_OR_RIGHT);
            plot.setDataset(1, y2data);
            plot.mapDatasetToRangeAxis(1, 1);
            StandardXYItemRenderer renderer2 = new StandardXYItemRenderer(
                    StandardXYItemRenderer.LINES,
                    tooltipGenerator,
                    urlGenerator);
          
            plot.setRenderer(1, renderer2);
        }
        
        return chart;
    }
    
}
