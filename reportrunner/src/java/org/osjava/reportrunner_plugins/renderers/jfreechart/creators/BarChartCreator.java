package org.osjava.reportrunner_plugins.renderers.jfreechart.creators;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.osjava.reportrunner.Renderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;
import org.osjava.reportrunner_plugins.renderers.jfreechart.JFreeChartCreator;

public class BarChartCreator implements JFreeChartCreator {

    private double itemMargin = 0.0;        // margin between series in a category
    private double lowerMargin = 0.02;      // margin on lower end of domain axis
    private double categoryMargin = 0.10;   // margin between categories
    private double upperMargin = 0.02;      // margin on upper end of domain axis
    
    private String title = "";
    private String domainAxisLabel = "";
    private String rangeAxisLabel = "";
    private PlotOrientation orientation = PlotOrientation.VERTICAL;
    private boolean legend = true;
   
    // Default labelling fonts
    private Font domainAxisFont = null;
    private Font rangeAxisFont = null;
    private Font titleFont = null;
    
    // For parsing the results
    private int categoryColumn = 0;
    private int seriesColumn = 1;
    private int valueColumn = 2;
    
    private void initSettings(Report report, Renderer renderer) {
        // These are the variables that can be configured
        Map variableMap = renderer.getVariables();
                
        this.title = (String) this.checkNull(this.title, variableMap.get("title"));
        this.domainAxisLabel = (String) this.checkNull(this.domainAxisLabel, variableMap.get("domainAxisLabel"));
        this.rangeAxisLabel = (String) this.checkNull(this.rangeAxisLabel, variableMap.get("rangeAxisLabel"));
        
        this.itemMargin = this.checkNullDouble(this.itemMargin, variableMap.get("itemMargin"));
        this.lowerMargin = this.checkNullDouble(this.lowerMargin, variableMap.get("lowerMargin"));
        this.categoryMargin = this.checkNullDouble(this.categoryMargin, variableMap.get("categoryMargin"));
        this.upperMargin = this.checkNullDouble(this.upperMargin, variableMap.get("upperMargin"));
                             
        this.categoryColumn = (int) this.checkNullDouble(this.categoryColumn, variableMap.get("categoryColumn"));
        this.seriesColumn = (int) this.checkNullDouble(this.seriesColumn, variableMap.get("seriesColumn"));
        this.valueColumn = (int) this.checkNullDouble(this.seriesColumn, variableMap.get("valueColumn"));
        
        String orientationStr = (String) variableMap.get("orientation");
        if (orientationStr != null && "horizontal".equalsIgnoreCase(orientationStr)) {
            this.orientation = PlotOrientation.HORIZONTAL;
        }
        
        String legendStr = (String) variableMap.get("legend");
        if (legendStr != null) {
            this.legend = Boolean.valueOf(legendStr).booleanValue();
        }
        
        String domainAxisFontStr = (String) variableMap.get("domainAxisFont");
        if (domainAxisFontStr != null) {
            this.domainAxisFont = Font.decode(domainAxisFontStr);
        }
        
        String rangeAxisFontStr = (String) variableMap.get("rangeAxisFont");
        if (rangeAxisFontStr != null) {
            this.rangeAxisFont = Font.decode(rangeAxisFontStr);
        }
        
        String titleFontStr = (String) variableMap.get("titleFont");
        if (titleFontStr != null) {
            this.titleFont = Font.decode(titleFontStr);
        }                   
    }
    
    public JFreeChart createChart(Result result, Report report, Renderer renderer) {
        this.initSettings(report, renderer);
        
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        
        while (result.hasNextRow()) {
            Object[] row = result.nextRow();
            Comparable series = (Comparable) row[this.seriesColumn];
            Comparable category = (Comparable) row[this.categoryColumn];            
            Number value = (Number) row[this.valueColumn];
            ds.addValue(value, series, category);
        }
                        
        return this.createChart(ds);
    }
     
    private JFreeChart createChart(CategoryDataset dataset) {                     
        JFreeChart chart = 
            ChartFactory.createBarChart(this.title, this.domainAxisLabel, this.rangeAxisLabel, dataset, 
                    this.orientation, this.legend, false, false);
        
        chart.setBackgroundPaint(Color.WHITE);              
        
        CategoryPlot plot = chart.getCategoryPlot();
        
        // Set margins for categories
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(this.lowerMargin);
        domainAxis.setCategoryMargin(this.categoryMargin);
        domainAxis.setUpperMargin(this.upperMargin);
        
        // Set margins for series        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.0);
        
        // Fonts
        if (this.domainAxisFont != null) {
            domainAxis.setLabelFont(this.domainAxisFont);
            domainAxis.setTickLabelFont(this.domainAxisFont);
        }
        
        if (this.rangeAxisFont != null) {
            ValueAxis rangeAxis = plot.getRangeAxis();
            rangeAxis.setLabelFont(this.rangeAxisFont);
            rangeAxis.setTickLabelFont(this.rangeAxisFont);
        }
                        
        if (this.titleFont != null) {
            TextTitle textTitle = chart.getTitle();
            textTitle.setFont(this.titleFont);
        }
        
        return chart;
    }
   
    private Object checkNull(Object target, Object source) {
        return (source != null ? source : target);      
    }
    
    private double checkNullDouble(double d, Object source) {
        if (source != null) {
            try {
                return Double.parseDouble((String) source);
            } catch (Exception e) {
                return d;
            }
        } else {
            return d;
        }
    }   
}
