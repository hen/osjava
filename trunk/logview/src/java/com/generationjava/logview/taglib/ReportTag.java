package com.generationjava.logview.taglib;

import com.generationjava.taglib.AbstractBodyTag;

/**
 * <logreport:report type="chart" format="html"/>
 *
 * Output a report, currently based on type and format.
 * Probably make it so you can pass param tags in 
 * for things like titles etc.
 *
 * @author bayard@generationjava.com
 * @version 0.1, 20011028
 */
public class ReportTag extends AbstractBodyTag {

    private String type;
    private String format;

    /**
     * Empty constructor. 
     */
    public ReportTag() {
        super();
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Initialise any properties to default values.
     */
    public void initAttributes() {
        this.format = "common";
    }

}
