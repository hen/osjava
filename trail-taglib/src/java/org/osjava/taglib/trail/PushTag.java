/*
 * Copyright (c) 2004, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of OSJava nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

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
    private boolean query = false;

    public PushTag() {
    }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

    public String getQuery() { return ""+this.query; }
    public void setQuery(String queryStr) { 
        this.query = "true".equalsIgnoreCase(queryStr);
    }

    public int doEndTag() throws JspException {
        BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        String uri = this.url;
        if(this.url == null) {
            Object requestUri = request.getAttribute("javax.servlet.forward.request_uri");
            if(requestUri != null) {
                uri = ""+requestUri;
                Object queryString = request.getAttribute("javax.servlet.forward.query_string");
                if(this.query && queryString != null) {
                    uri += "?" + queryString;
                }
            } else {
                uri = request.getRequestURI();
                if(this.query && request.getQueryString() != null) {
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
