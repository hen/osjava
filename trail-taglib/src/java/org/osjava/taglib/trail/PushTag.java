package org.osjava.taglib.trail;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

// <trail:push url="listMenu.jsp" label="Menu"/>
// <trail:push label="Menu"/>
public class PushTag extends TagSupport {

    private String url;
    private String label;

    public PushTag() {
    }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

    public int doEndTag() throws JspException {
        BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);

        String uri = this.url;
        if(this.url == null) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            // add query string
            uri = request.getRequestURI();
            uri += "?";
            uri += request.getQueryString();
        }

        breadcrumbs.addToTrail( new BreadCrumb(uri, this.label) );

        return EVAL_PAGE;
    }

}
