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
