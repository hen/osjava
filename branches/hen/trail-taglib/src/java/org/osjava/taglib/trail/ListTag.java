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
// <trail:list normalized="false"/>
public class ListTag extends TagSupport {

    private String var;
    private String delimiter = " -> ";
    private String normalized;

    public ListTag() {
    }

    public String getVar() { return this.var; }
    public void setVar(String var) { this.var = var; }

    public String getNormalized() { return this.normalized; }
    public void setNormalized(String normalized) { this.normalized = normalized; }

    public String getDelimiter() { return this.delimiter; }
    public void setDelimiter(String delimiter) { this.delimiter = delimiter; }

    public int doEndTag() throws JspException {
        BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);

        StringBuffer buffer = new StringBuffer();
        Iterator itr = null;
        // fix
        if(this.normalized == null) {
            itr = breadcrumbs.iterateNormalizedTrail();
        } else {
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
