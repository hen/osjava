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

import java.io.IOException;
import java.io.StringWriter;

import java.util.Iterator;

import javax.servlet.ServletRequest;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

// <trail:list/>
// <trail:list type="full"/>
public class ListTag extends TagSupport {

    private String var;
    private String delimiter = " -> ";
    private String normalized;

    public ListTag() {
    }

    public String getVar() { return this.var; }
    public void setVar(String var) { this.var = var; }

    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }

    public String getDelimiter() { return this.delimiter; }
    public void setDelimiter(String delimiter) { this.delimiter = delimiter; }

    public int doEndTag() throws JspException {
        BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);

        StringBuffer buffer = new StringBuffer();
        Iterator itr = null;
        // fix
        if(this.type == null || "normalized".equals(this.type) ) {
            itr = breadcrumbs.iterateNormalizedTrail();
        } else {
            // "full" or anything else does this
            itr = breadcrumbs.iterateTrail();
        }
        while( itr.hasNext() ) {
            BreadCrumb breadcrumb = (BreadCrumb) itr.next();
            Object label = breadcrumb.getLabel();
            Object url = breadcrumb.getUrl();
            buffer.append("<a href=\"");
            buffer.append(url);
            buffer.append("\">");
            buffer.append(label);
            buffer.append("</a>");
            buffer.append(this.delimiter);
        }
        String txt = buffer.toString();

        if(this.var == null) {
            JspWriter writer = pageContext.getOut();
            try {
                writer.print(txt);
            } catch(IOException ioe) {
                throw new JspException(ioe.toString());
            }
        } else {
            pageContext.setAttribute(this.var, txt);
        }
        return EVAL_PAGE;
    }

}
