package com.generationjava.logview.taglib;

import com.generationjava.taglib.AbstractBodyTag;

/**
 * <logreport:loglet type="Sort"/>
 *
 * This tag creates the specified Loglet and gets the current Loglet 
 * from the session, wraps this, and puts the new Loglet back in.
 *
 * The param tag can be used inside this one to pass in special
 * parameters.
 *
 * @author bayard@generationjava.com
 * @version 0.1, 20011030
 */
public class LogletTag extends AbstractBodyTag {

    private String type;

    /**
     * Empty constructor. 
     */
    public LogletTag() {
        super();
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Initialise any properties to default values.
     */
    public void initAttributes() {
    }

}
