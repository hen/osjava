package com.generationjava.logview.report;

import java.util.Iterator;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.lang.NumberUtils;

/// TODO:
///    Needs the ability to set the origin value? the maximum value?
///    ie) If percentages, need to go to 100%?
///    Also, some form of width/height number? Unit size?
///    Ability to pass a set of presentation properties in
public class ChartReport extends AbstractReport {

    private String xField;
    private String yField;
    private SequencedHashMap data = new SequencedHashMap();
    private int yMax = 0;

    public ChartReport(String xField, String yField) {
        this.xField = xField;
        this.yField = yField;
    }

    public void addData(Object xValue, Object yValue) {
        if(!(yValue instanceof Number)) {
            try {
                yValue = NumberUtils.createNumber(yValue.toString());
            } catch(NumberFormatException nfe) {
                System.err.println("Bad number: "+yValue+" for "+xValue);
            }
        }
        Number value = (Number)yValue;
        if(value.intValue() > yMax) {
            yMax = value.intValue();
        }
        data.put(xValue, value);
    }

    public Iterator iterateKeys() {
        return data.keySet().iterator();
    }

    public int getYMax() {
        return this.yMax;
    }

    public String getXField() {
        return this.xField;
    }

    public String getYField() {
        return this.yField;
    }

    public Object getData(Object key) {
        return data.get(key);
    }

    // deprecated
    public String toString() {
        // assume yField is a number
        Iterator iterator = this.data.keySet().iterator();
        StringBuffer buffer = new StringBuffer();
        float ratio = 40F / this.yMax;

        buffer.append(xField);
        if(getLink(xField) != null) {
            buffer.append("[cf.");
            buffer.append(getLink(xField));
            buffer.append("]");
        }
        buffer.append("\t");
        buffer.append(yField);
        buffer.append("\n");
        while(iterator.hasNext()) {
            Object key = iterator.next();
            
            Number value = (Number)data.get(key);
            int count = value.intValue();
            buffer.append(key);
            buffer.append("\t");
            count *= ratio; 
            for(int i=count; i>0; i--) {
                buffer.append("*");
            }
            for(int i=count; i<40; i++) {
                buffer.append(" ");
            }
            buffer.append("\t");
            buffer.append(value.intValue());
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
