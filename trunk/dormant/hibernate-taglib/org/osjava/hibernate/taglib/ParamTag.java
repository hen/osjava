package org.osjava.hibernate.taglib;

import java.io.IOException;
import java.io.StringWriter;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ParamTag extends BodyTagSupport {

    private String value;
    private String type;

    public ParamTag() {
    }

    public String getValue() { return this.value; }
    public void setValue(String value) { this.value = value; }

    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }

// move to doStart?
    public int doEndTag() throws JspException {
        FindTag parent = (FindTag) findAncestorWithClass(this, FindTag.class);

        if(parent == null) {
            throw new JspTagException("Param tag outside parent find tag. ");
        }

        String paramValue = null;
        if(value != null) {
            paramValue = value;
        } else {
            String query = JspUtils.getBody(bodyContent);
            if(query.trim().length() == 0) {
                paramValue = null;
            } else {
                paramValue = query;
            }
        }

        parent.addParameter(paramValue);
        parent.addType(this.type);

        return EVAL_PAGE;
    }

}
