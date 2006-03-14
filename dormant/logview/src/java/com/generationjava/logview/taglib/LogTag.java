package com.generationjava.logview.taglib;

import com.generationjava.taglib.AbstractBodyTag;

/**
 * <logreport:log source="filename.log" type="file" format="common">
 * Default for type is 'file'.
 *
 * This tag loads a SourceLoglet into the session. The tag creates 
 * a LogBuilder which wraps a LogSource. Said LogBuilder is then put 
 * behind a SourceLoglet, and all further work just treats the 
 * in memory Loglet as the face of the chain.
 *
 * @author bayard@generationjava.com
 * @version 0.1, 20011028
 */
public class LogTag extends AbstractBodyTag {

    private String source;
    private String type;
    private String format;

    /**
     * Empty constructor. 
     */
    public LogTag() {
        super();
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
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
        this.type = "com.generationjava.logview.source.FileLogSource";
        this.format = "common";
    }

}
