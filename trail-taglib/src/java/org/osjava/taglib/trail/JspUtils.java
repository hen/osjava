package org.osjava.taglib.trail;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.BodyContent;

import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;

class JspUtils {

    public static String getBody(BodyContent bodyContent) {
        String text = null;
        if(bodyContent != null) {
            StringWriter body = new StringWriter();
            try {
                bodyContent.writeOut(body);
                text = body.toString().trim();
            } catch(IOException ioe) {
                // ignore, return null is the error
            }
        }
        return text;
    }

    public static BreadCrumbs getBreadCrumbs(PageContext pageContext) {
        HttpSession session = pageContext.getSession();
        BreadCrumbs breadcrumbs = (BreadCrumbs) session.getAttribute("org.osjava.taglib.breadcrumbs");
        if(breadcrumbs == null) {
            breadcrumbs = new BreadCrumbs();
            session.setAttribute("org.osjava.taglib.breadcrumbs", breadcrumbs);
        }
        return breadcrumbs;
    }
}
