package org.osjava.reportrunner_plugins.renderers.jfreechart.creators;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.osjava.reportrunner.Choice;
import org.osjava.reportrunner.Column;
import org.osjava.reportrunner.Param;
import org.osjava.reportrunner.Renderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;
import org.osjava.reportrunner_plugins.renderers.jfreechart.JFreeChartCreator;

public class TimeChartCreator implements JFreeChartCreator {
        
    private int timeUnit = DateTickUnit.DAY;       // default chart units to day
    private int xAxisColumn = 0;                   // x axis defaults to using first column from the Result
    private String xLabel = null;
    private List[] yAxisColumns = null;            // y1 & y2 axis column numbers from the Result
    private String y1AxisTitle = null; 
    private String y2AxisTitle = null;  
    private String[][] yLabels = new String[2][];  // labels for the series for each axis
    private boolean y1AxisIncludesZero = false;
    private boolean y2AxisIncludesZero = false;
    
    private Color[] seriesColors = null;           // Colors to use for drawing the series
    private NumberTickUnit[] yAxisTickUnit = new NumberTickUnit[2];
    
    private DateFormat customDateFormatter = null; // Class set by user
    
    // Date Axis labelling
    private String dateAxisLabelFormat = "M/d/yy"; // default format for the date axis labels
    private int dateAxisTicksSkip = 1;             // default to labelling for every date
      
    // Chart defaults
    private String title = null;   
    private boolean showLegend = true;
    
    private double lowerMargin = 0.02;
    private double upperMargin = 0.02;
    
    // Default labelling fonts
    private Font timeAxisFont = null;
    private Font valueAxisFont = null;
    private Font titleFont = null;
    
    private Column[] columns;
    
    private Map constantsMap;
    
    public TimeChartCreator() {       
        this.constantsMap = new HashMap();
        constantsMap.put("MILLISECOND", new Integer(DateTickUnit.MILLISECOND));
        constantsMap.put("SECOND", new Integer(DateTickUnit.SECOND));
        constantsMap.put("MINUTE", new Integer(DateTickUnit.MINUTE));
        constantsMap.put("HOUR", new Integer(DateTickUnit.HOUR));
        constantsMap.put("DAY", new Integer(DateTickUnit.DAY));
        constantsMap.put("MONTH", new Integer(DateTickUnit.MONTH));
        constantsMap.put("YEAR", new Integer(DateTickUnit.YEAR));          
    }
       
    private void initSettings(Report report, Renderer renderer) {
        this.columns = report.getColumns();
        
        // These are the variables that can be configured
        Map variableMap = renderer.getVariables();
        String timeUnit = (String) variableMap.get("timeUnit");
        String xAxis = (String) variableMap.get("timeValueColumn");
        String xLabel = (String) variableMap.get("timeAxisTitle");
        String y1AxisTitleStr = (String) variableMap.get("y1AxisTitle");
        String y2AxisTitleStr = (String) variableMap.get("y2AxisTitle");     
        String y1Axis = (String) variableMap.get("y1AxisColumns");
        String y2Axis = (String) variableMap.get("y2AxisColumns");
        String y1AxisZero = (String) variableMap.get("y1AxisIncludesZero");
        String y2AxisZero = (String) variableMap.get("y2AxisIncludesZero");        
        String timeFormat = (String) variableMap.get("timeFormat");
        String timeLabelsSkip = (String) variableMap.get("timeUnitLabelSkip");
        String title = (String) variableMap.get("title");        
        String legendStr = (String) variableMap.get("legend");
        String seriesColorsStr = (String) variableMap.get("seriesColors");
        String customDateFormatterStr = (String) variableMap.get("dateFormat");
        String lowerMarginStr = (String) variableMap.get("timeAxisLowerMargin");
        String upperMarginStr = (String) variableMap.get("timeAxisUpperMargin");
        String titleFontStr = (String) variableMap.get("titleFont");
        String timeAxisFontStr = (String) variableMap.get("timeAxisFont");        
        String valueAxisFontStr = (String) variableMap.get("valueAxisFont");
        
        String[] yAxisTickUnitStr = new String[2];        
        yAxisTickUnitStr[0] = (String) variableMap.get("y1AxisTickUnit");
        yAxisTickUnitStr[1] = (String) variableMap.get("y2AxisTickUnit");
        
        if (timeUnit != null) {
            Integer unit = (Integer) this.constantsMap.get(timeUnit);
            if (unit != null) {
                this.timeUnit = unit.intValue();
            }
        }
        
        if (xAxis != null) {
            try {
                this.xAxisColumn = Integer.parseInt(xAxis);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid xAxis value: '" + xAxis + "'");
            }
        }
        
        if (xLabel != null) {
            this.xLabel = xLabel;
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
        
        if (y1AxisZero != null) {
            this.y1AxisIncludesZero = true;
        }
        
        if (y2AxisZero != null) {
            this.y2AxisIncludesZero = true;
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
        
        if (timeFormat != null) {
            this.dateAxisLabelFormat = timeFormat;
        }
        
        if (timeLabelsSkip != null) {
            this.dateAxisTicksSkip = Integer.parseInt(timeLabelsSkip);
        }    
        
        this.title = this.processTokens(title, report);                
              
        if (legendStr != null) {            
            this.showLegend = Boolean.valueOf(legendStr).booleanValue();            
        }
        
        if (seriesColorsStr != null) {
            String[] seriesColorsHexStrings = seriesColorsStr.split(",");            
            this.seriesColors = new Color[seriesColorsHexStrings.length];            
            
            for (int j = 0; j < this.seriesColors.length; j++) {
                this.seriesColors[j] = Color.decode(seriesColorsHexStrings[j]);               
            }
        }
        
        for (int j = 0; j < yAxisTickUnitStr.length; j++) {
            if (yAxisTickUnitStr[j] != null) {
                String[] values = yAxisTickUnitStr[j].split(",");
                double size = Double.parseDouble(values[0]);            
                
                if (values[1].equalsIgnoreCase("Integer")) {
                    this.yAxisTickUnit[j] = new NumberTickUnit(size, NumberFormat.getIntegerInstance());
                } else if (values[1].equalsIgnoreCase("Float")) {
                    this.yAxisTickUnit[j] = new NumberTickUnit(size, NumberFormat.getNumberInstance());
                }
            }
        }
        
        if (customDateFormatterStr != null) {
            try {
                Class clazz = TimeChartCreator.class.getClassLoader().loadClass(customDateFormatterStr);
                this.customDateFormatter = (DateFormat) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();                
            }
        }
        
        if (lowerMarginStr != null) {
            this.lowerMargin = Double.parseDouble(lowerMarginStr);
        }
        
        if (upperMarginStr != null) {
            this.upperMargin = Double.parseDouble(upperMarginStr);
        }
        
        if (titleFontStr != null) {
            this.titleFont = Font.decode(titleFontStr);
        }
        
        if (timeAxisFontStr != null) {
            this.timeAxisFont = Font.decode(timeAxisFontStr);
        }
        
        if (valueAxisFontStr != null) {
            this.valueAxisFont = Font.decode(valueAxisFontStr);
        }                  
    }
   
    public JFreeChart createChart(Result result, Report report, Renderer renderer) {
        this.initSettings(report, renderer);       
        return this.createChart(result);        
    }
    
    public JFreeChart createChart(Result result) {                               
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
                    
                    if (num != null) {
                        ts.addOrUpdate(new Minute(date), num);
                    }
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
        
        
        JFreeChart chart = this.createTimeSeriesChart(y1Tsc, y2Tsc, true, false);          
        return chart;
    }
    
    private JFreeChart createTimeSeriesChart(XYDataset data, XYDataset y2data, boolean tooltips, boolean urls) {                        
        DateAxis timeAxis = new DateAxis(this.xLabel);
        
        DateFormat df = null;
        if (this.customDateFormatter != null) {
            df = this.customDateFormatter;
        } else {
            df = new SimpleDateFormat(this.dateAxisLabelFormat);
        }
        
        // only show midnight ticks        
        timeAxis.setTickUnit(new DateTickUnit(this.timeUnit, 1, 
                new DateTickUnitFormatter(df, this.dateAxisTicksSkip)));
        if (this.timeAxisFont != null) {
            timeAxis.setLabelFont(this.timeAxisFont);
            timeAxis.setTickLabelFont(this.timeAxisFont);
        }
       
        // reduce the default margins on the time axis
        timeAxis.setLowerMargin(this.lowerMargin);        
        timeAxis.setUpperMargin(this.upperMargin);        
                        
        NumberAxis valueAxis = new NumberAxis(this.y1AxisTitle);        
        valueAxis.setAutoRangeIncludesZero(this.y1AxisIncludesZero);               
        valueAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());
        if (this.valueAxisFont != null) {
            valueAxis.setLabelFont(this.valueAxisFont);
            valueAxis.setTickLabelFont(this.valueAxisFont);
        }
        
        if (this.yAxisTickUnit[0] != null) {
            valueAxis.setTickUnit(this.yAxisTickUnit[0]);
        }
        
        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {            
            tooltipGenerator = new StandardXYToolTipGenerator("{1}, {2}", new SimpleDateFormat("MM/dd/yyyy hh:mm"), 
                    new DecimalFormat("0.00"));
        }
        
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title,this.xLabel,this.y1AxisTitle,data,this.showLegend,tooltips,urls);     
        XYPlot plot = chart.getXYPlot();
        StandardXYItemRenderer renderer1 = new StandardXYItemRenderer(
                StandardXYItemRenderer.LINES,
                tooltipGenerator,
                urlGenerator);

        // Colors
        boolean customColors = false;
        int y1SeriesCount = data.getSeriesCount();
        int y2SeriesCount = 0;
        
        if (y2data != null) {
            y2SeriesCount = y2data.getSeriesCount();
        }
        
        if (this.seriesColors != null && this.seriesColors.length >= y1SeriesCount + y2SeriesCount) {
            customColors = true;
            for (int i = 0; i < y1SeriesCount; i++) {
                renderer1.setSeriesPaint(i, this.seriesColors[i]);               
            }
        }
        
        plot.setRenderer(renderer1);
        plot.setRangeAxis(valueAxis);
        plot.setDomainAxis(timeAxis);
                     
        // Turn off the vertical grid lines
        plot.setDomainGridlinesVisible(false);          
        chart.setBackgroundPaint(Color.WHITE);   
        chart.setAntiAlias(true);
        
        TextTitle chartTextTitle = null;
        if (this.titleFont != null) {
            chartTextTitle = new TextTitle(this.title, this.titleFont);
        } else {
            chartTextTitle = new TextTitle(this.title);
        }
        chart.setTitle(chartTextTitle);
                       
        // AXIS 2
        if (y2data != null) {
            NumberAxis axis2 = new NumberAxis(this.y2AxisTitle);
            axis2.setAutoRangeIncludesZero(this.y2AxisIncludesZero);
            axis2.setNumberFormatOverride(NumberFormat.getNumberInstance());
            if (this.valueAxisFont != null) {
                axis2.setLabelFont(this.valueAxisFont);
                axis2.setTickLabelFont(this.valueAxisFont);
            }
            
            if (this.yAxisTickUnit[1] != null) {
                axis2.setTickUnit(this.yAxisTickUnit[1]);
            }
            
            plot.setRangeAxis(1, axis2);
            plot.setRangeAxisLocation(1, AxisLocation.TOP_OR_RIGHT);
            plot.setDataset(1, y2data);
            plot.mapDatasetToRangeAxis(1, 1);
            StandardXYItemRenderer renderer2 = new StandardXYItemRenderer(
                    StandardXYItemRenderer.LINES,
                    tooltipGenerator,
                    urlGenerator);
            
            if (customColors) {
                for (int i = 0; i < y2SeriesCount; i++) {
                    renderer2.setSeriesPaint(i, this.seriesColors[i + y1SeriesCount]);                    
                }
            }
            
            plot.setRenderer(1, renderer2);
        }
        
        return chart;
    }
    
    private String processTokens(String input, Report report) {        
        Param[] params = report.getParams();
        
        int idx = input.indexOf("$PARAM[");            
        
        int endIdx = input.indexOf(']', idx);
        if (endIdx <= idx) {
            return input;
        }
        
        int paramIndex = Integer.parseInt(input.substring(idx + 7, endIdx));
        
        Param param = params[paramIndex];
        
        int dotIdx = input.indexOf('.', endIdx);        
        String func = input.substring(dotIdx + 1);
        
        if (func.equalsIgnoreCase("value")) {            
            return param.getValue().toString();
        } else if (func.equalsIgnoreCase("name")) {
            Choice[] choices = report.getParamChoices(param);
            for (int i = 0; i < choices.length; i++) {
                if (choices[i].getValue().equals(param.getValue().toString())) {
                    return choices[i].getLabel();
                }
            }                        
        }
                       
        return input;
    }    
    
    private class DateTickUnitFormatter extends DateFormat {            
        private DateFormat format = null;     
        private int ticksSkip = 1; // default to showing label for every tick mark        
        private int count = 0; 
        private long lastDateMillis = 0;
        
        public DateTickUnitFormatter(DateFormat df, int ticksSkip) {                        
            this.ticksSkip = ticksSkip;           
            this.format = df;            
        }
                        
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            // We need to start labelling over for 2nd series
            if (date.getTime() < this.lastDateMillis) {            
                this.count = 0;
            }
          
            String s = "";                                           
            if (this.count == 0 || this.count % ticksSkip == 0) {                
                s = format.format(date);
            }            
            
            this.lastDateMillis = date.getTime();
            this.count++;
                         
            return new StringBuffer(s);              
        }
        
        public Date parse(String source, ParsePosition pos) {
            return null;
        }     
    }
      
}