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

public class CloseTag extends TagSupport {

    public CloseTag() {
    }

    public int doEndTag() throws JspException {
        try {
            Session hSession = JspUtils.getHibernateSession(pageContext);
            hSession.close();
        } catch(HibernateException he) {
            he.printStackTrace();
        }

        return EVAL_PAGE;
    }

}
