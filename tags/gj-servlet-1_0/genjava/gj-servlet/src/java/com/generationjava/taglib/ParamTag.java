package com.generationjava.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <xxxx:param name="order" value="reverse"/>
 *
 * @author bayard@generationjava.com
 * @version 0.1, 20011030
 */
public class ParamTag extends TagSupport {

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

    /// TODO: Implement its doing method. This would look at its 
    ///       parent, and if it is of type AbstractBodyTag, then it 
    ///       puts itself into the tag. Maybe use an Interface instead.

}
