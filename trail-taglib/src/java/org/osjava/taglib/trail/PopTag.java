package org.osjava.taglib.trail;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletRequest;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

// <trail:pop label="??"/>
public class PopTag extends TagSupport {

    private String label;

    public PopTag() {
    }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public int doEndTag() throws JspException {
        BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);

        // find label, remove it and everything before....

        return EVAL_PAGE;
    }

}
