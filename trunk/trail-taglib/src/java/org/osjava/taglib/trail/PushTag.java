package org.osjava.taglib.trail;

import java.net.URL;
import java.net.MalformedURLException;

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
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        String uri = this.url;
        if(this.url == null) {
            Object requestUri = request.getAttribute("javax.servlet.forward.request_uri");
            if(requestUri != null) {
                uri = ""+requestUri;
                Object queryString = request.getAttribute("javax.servlet.forward.query_string");
                if(queryString != null) {
                    uri += "?" + queryString;
                }
            } else {
                uri = request.getRequestURI();
                if(request.getQueryString() != null) {
                    uri += "?" + request.getQueryString();
                }
            }
        }

        String referer = request.getHeader("Referer");

        // check if referer is from the same machine
        // ...

        // strip out the url part
        if(referer != null) {
            try {
                URL url = new URL(referer);
                referer = url.getFile();
            } catch(MalformedURLException murle) {
                referer = null;
            }
        }

        breadcrumbs.addToTrail( referer, new BreadCrumb(uri, this.label) );

        return EVAL_PAGE;
    }

}
