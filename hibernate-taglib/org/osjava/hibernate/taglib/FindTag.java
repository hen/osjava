package org.osjava.hibernate.taglib;

import java.io.IOException;
import java.io.StringWriter;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.Type;

public class FindTag extends BodyTagSupport {

    private String var;
    private String expected;

    private List types = new ArrayList();
    private List parameters = new ArrayList();

    public FindTag() {
    }

    public String getVar() { return this.var; }
    public void setVar(String var) { this.var = var; }

    public String getExpected() { return this.expected; }
    public void setExpected(String expected) { this.expected = expected; }

// move to doStart?
    public int doEndTag() throws JspException {
        if(bodyContent == null) {
            return EVAL_PAGE;
        }

        String query = JspUtils.getBody(bodyContent);
        System.out.println("QUERY: "+query);
        try {
            Session hSession = JspUtils.getHibernateSession(pageContext);
            List categories = null;
            if(this.types.size() == 0 &&
               this.parameters.size() == 0)
            {
                categories = hSession.find(query);
            } else {
                categories = hSession.find(
                    query, 
                    convertToHibernateValueArray(this.parameters, this.types),
                    convertToHibernateArray(this.types)
                );
            }
            if("one".equals(expected)) {
                if(categories.size() == 1) {
                    pageContext.setAttribute(this.var, categories.get(1));
                } else {
                    throw new JspTagException("Only one value was expected. ");
                }
            } else {
                pageContext.setAttribute(this.var, categories);
            }
        } catch(HibernateException he) {
            he.printStackTrace();
        } finally {
            this.types.clear();
            this.parameters.clear();
        }

        return EVAL_PAGE;
    }

    // convert a list of hibernate strings to the parameters...
    // bit lame, but it works...
    // I think this will become a String-based API to Hibernate
    private Type[] convertToHibernateArray(List types) {
        ArrayList list2 = new ArrayList();
        Iterator itr = types.iterator();
        while(itr.hasNext()) {
            String parameter = itr.next().toString();
            if("Hibernate.LONG".equals(parameter)) {
                list2.add(Hibernate.LONG);
            }
        }
        System.err.println("L2: "+list2);
        return (Type[]) list2.toArray(new Type[0]);
    }

    private Object[] convertToHibernateValueArray(List values, List types) {
        ArrayList list2 = new ArrayList();
        for(int i=0; i<types.size(); i++) {
            String parameter = types.get(i).toString();
            String value = values.get(i).toString();
            if("Hibernate.LONG".equals(parameter)) {
                list2.add( Long.valueOf(value) );
            }
        }
        System.err.println("L2: "+list2);
        return list2.toArray();
    }

    public void addType(String type) {
        this.types.add(type);
        System.out.println(""+this.types);
    }

    public void addParameter(String parameter) {
        this.parameters.add(parameter);
        System.out.println(""+this.parameters);
    }

}
