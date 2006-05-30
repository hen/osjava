package org.osjava.hibernate.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.BodyContent;

import javax.servlet.jsp.PageContext;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.HibernateException;

class JspUtils {
    
    static String HIBERNATE_SESSION = "HibernateSession";

    static Session getHibernateSession(PageContext ctxt) {
// Make this need a DataSource
        Session hSession = (Session) ctxt.getAttribute(HIBERNATE_SESSION);
        if(hSession == null) {
            try {
                SessionFactory hSf = new Configuration().configure().buildSessionFactory();
                hSession = hSf.openSession();
            } catch(HibernateException he) {
                // TODO: ??
                he.printStackTrace();
            }
        }
        return hSession;
    }

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
