package com.generationjava.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

/**
 * Abstract class for the Taglibs.
 * It handles the JSP taglib side of things and calls abstract 
 * protected methods to delegate the actual body functionality.
 * 
 * @author bayard@generationjava.com
 * @version 0.1, 20011028
 */
abstract public class AbstractStringBodyTag extends AbstractBodyTag {

    /**
     * Empty constructor.
     */
    public AbstractStringBodyTag() {
        super();
    }

    /**
     * Handles the manipulation of the String tag,
     * evaluating the body of the tag. The evaluation 
     * is delegated to the actOnBody(String) method 
     * and then the initAttributes() method is called.
     */
    public int doEndTag() throws JspException {

        if( (bodyContent == null) ) {
            return SKIP_BODY;
        }
        String text = "";
        if(bodyContent != null) {
            StringWriter body = new StringWriter();
            try {
                bodyContent.writeOut(body);
                text = body.toString();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
        JspWriter writer = pageContext.getOut();
        try {
            actOnBody(text);
            initAttributes();
        } catch (IOException e) {
            throw new JspException(e.toString());
        }

        return (SKIP_BODY);
    }

    /** 
     * Perform an operation on the passed in String.
     *
     * @param str String to be manipulated
     */
    abstract public void actOnBody(String str) throws IOException;

}
