package org.osjava.hibernate.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.BodyContent;

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

}
