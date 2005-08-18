package org.osjava.naming;

import java.util.*;
import javax.naming.*;

/**
 * Standard delegating pattern for JNDI Contexts.
 * Sub-classes of this may filter calls to the JNDI code. 
 */
public abstract class DelegatingContext implements Context {

    private Context target;

    public DelegatingContext(Context ctxt) {
        this.target = ctxt;
    }

    public Object lookup(Name name) throws NamingException {
        return this.target.lookup(name);
    }

    public Object lookup(String name) throws NamingException {
        return this.target.lookup(name);
    }

    public void bind(Name name, Object value) throws NamingException {
        this.target.bind(name, value);
    }

    public void bind(String name, Object value) throws NamingException {
        this.target.bind(name, value);
    }

    public void rebind(Name name, Object value) throws NamingException {
        this.target.rebind(name, value);
    }

    public void rebind(String name, Object value) throws NamingException {
        this.target.rebind(name, value);
    }

    public void unbind(Name name) throws NamingException {
        this.target.unbind(name);
    }

    public void unbind(String name) throws NamingException {
        this.target.unbind(name);
    }

    public void rename(Name name, Name name2) throws NamingException {
        this.target.rename(name, name2);
    }

    public void rename(String name, String name2) throws NamingException {
        this.target.rename(name, name2);
    }

    public NamingEnumeration list(Name name) throws NamingException {
        return this.target.list(name);
    }

    public NamingEnumeration list(String name) throws NamingException {
        return this.target.list(name);
    }

    public NamingEnumeration listBindings(Name name) throws NamingException {
        return this.target.listBindings(name);
    }

    public NamingEnumeration listBindings(String name) throws NamingException {
        return this.target.listBindings(name);
    }

    public void destroySubcontext(Name name) throws NamingException {
        this.target.destroySubcontext(name);
    }

    public void destroySubcontext(String name) throws NamingException {
        this.target.destroySubcontext(name);
    }

    public Context createSubcontext(Name name) throws NamingException {
        return this.target.createSubcontext(name);
    }

    public Context createSubcontext(String name) throws NamingException {
        return this.target.createSubcontext(name);
    }

    public Object lookupLink(Name name) throws NamingException {
        return this.target.lookupLink(name);
    }

    public Object lookupLink(String name) throws NamingException {
        return this.target.lookupLink(name);
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return this.target.getNameParser(name);
    }

    public NameParser getNameParser(String name) throws NamingException {
        return this.target.getNameParser(name);
    }

    public Name composeName(Name name, Name name2) throws NamingException {
        return this.target.composeName(name, name2);
    }

    public String composeName(String name, String name2) throws NamingException {
        return this.target.composeName(name, name2);
    }

    public Object addToEnvironment(String key, Object value) throws NamingException {
        return this.target.addToEnvironment(key, value);
    }

    public Object removeFromEnvironment(String key) throws NamingException {
        return this.target.removeFromEnvironment(key);
    }

    public Hashtable getEnvironment() throws NamingException {
        return this.target.getEnvironment();
    }

    public void close() throws NamingException {
        this.target.close();
    }

    public String getNameInNamespace() throws NamingException {
        return this.target.getNameInNamespace();
    }

}

