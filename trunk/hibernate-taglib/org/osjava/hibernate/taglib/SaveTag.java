package org.osjava.hibernate.taglib;

import java.io.IOException;
import java.io.StringWriter;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import javax.servlet.ServletRequest;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.type.Type;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

// Make this need a DataSource
public class SaveTag extends TagSupport {

    private Object value;

    public SaveTag() {
    }

    public Object getValue() { return this.value; }
    public void setValue(Object value) { this.value = value; }

    public int doEndTag() throws JspException {
        try {
            Session hSession = JspUtils.getHibernateSession(pageContext);

            if(value instanceof String) {
                throw new JspTagException("Bad to not have an Object value. ");
            }
            Object bean = this.value;

            System.err.println("BEAN: "+bean);

            /* TO DECIDE IF THIS IS DESIRED. SHOULD save MODIFY THEN SAVE?
            try {
                // copy values from request onto the bean
                BeanUtils.populate(bean, pageContext.getRequest().getParameterMap());
                // copy values from request scope onto the bean
                BeanUtils.populate(bean, attributesToMap(pageContext.getRequest()));
            } catch(IllegalAccessException iae) {
                // hmmmh...
                iae.printStackTrace();
            } catch(InvocationTargetException ite) {
                // hmmmh...
                ite.printStackTrace();
            }
            */

            hSession.save(bean);
        } catch(HibernateException he) {
            he.printStackTrace();
        } catch(Throwable t) {
            t.printStackTrace();
        }

        return EVAL_PAGE;
    }

    private Map attributesToMap(ServletRequest request) {
        HashMap map = new HashMap();
        Enumeration enum = request.getAttributeNames();
        while(enum.hasMoreElements()) {
            String key = (String) enum.nextElement();
            map.put( key, request.getAttribute( key ) );
        }
        return map;
    }

}
