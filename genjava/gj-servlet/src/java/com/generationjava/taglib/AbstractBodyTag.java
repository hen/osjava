package com.generationjava.taglib;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Abstract class for the Taglibs.
 * It has a param ability which binds to a generic ParamTag.
 * 
 * @author bayard@generationjava.com
 * @version 0.1, 20011028
 */
abstract public class AbstractBodyTag extends BodyTagSupport {

    private Properties params;

    /**
     * Empty constructor. Initialises the attributes.
     */
    public AbstractBodyTag() {
        initAttributes();
    }

    // code to handle adding params..
    /**
     * Set a param.
     */
    public void putParam(String name, String value) {
        if(params == null) {
            params = new Properties();
        }
        params.put(name, value);
    }

    /**
     * Get a param out.
     */
    public String getParam(String name) {
        if(params == null) {
            return null;
        }
        return params.getProperty(name);
    }

    /**
     * Initialise any properties to default values.
     * This method is called upon construction.
     * This is a default empty implementation.
     */
    public void initAttributes() {
        params.clear();
    }

}
