package com.generationjava.logview.taglib;

import com.generationjava.taglib.AbstractBodyTag;

/**
 * <logreport:param name="order" value="reverse"/>
 *
 * @author bayard@generationjava.com
 * @version 0.1, 20011030
 */
public class ParamTag extends AbstractBodyTag {

    private String name;
    private String value;

    /**
     * Empty constructor. 
     */
    public ParamTag() {
        super();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Initialise any properties to default values.
     */
    public void initAttributes() {
    }

}
