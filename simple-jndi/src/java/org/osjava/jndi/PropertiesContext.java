package org.apache.commons.dbutils.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NameParser;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NotContextException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.OperationNotSupportedException;
import javax.naming.Name;
import javax.naming.CompositeName;
import javax.naming.Binding;
import javax.naming.NameClassPair;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class PropertiesContext implements Context  {

    private Hashtable env;
    private Hashtable table = new Hashtable();
    private String root;

    public PropertiesContext(Hashtable env) {
        if(env != null) {
            this.env = (Hashtable)env.clone();
            root = (String)this.env.get("org.apache.commons.dbutils.jndi.root");
        }
    }

    private PropertiesContext(PropertiesContext that) {
        this(that.env);
    }

    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    public Object lookup(String name) throws NamingException {
        if("".equals(name)) {
            return new PropertiesContext(this);
        }
//        Object answer = table.get(name);
        // name is a dotted convention. Each element is 
        // either a directory, file, or element in a file.
        String[] elements = StringUtils.split(name, ".");
        String path = root;
        Properties properties = null;
        int sz = elements.length;
        String remaining = null;
        for(int i=0; i<sz; i++) {
            String element = elements[i];

            File file = new File(path+File.separatorChar+element);
//            System.err.println("Into directory? "+file);
            if(file.exists() && file.isDirectory()) {
                path = path+File.separatorChar+element;
                continue;
            }

            file = new File(path+File.separatorChar+element+".properties");
//            System.err.println("Into file? "+file);
            if(file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    properties = new Properties();
                    properties.load(fis);
                    fis.close();
                } catch(IOException ioe) {
                    throw new NamingException("Failure to open: "+file);
                }
                java.util.ArrayList list = new java.util.ArrayList();
                for(int j=i+1; j<sz; j++) {
                    list.add(elements[j]);
                }
                if(list.size() > 0) {
                    remaining = StringUtils.join(list.iterator(), ".");
                }
                break;
            }
        }

        if(properties == null) {
            //  if properties is null, then we look for default.properties
            File file = new File(path+File.separatorChar+"default.properties");
//            System.err.println("Looking for: "+file);
            if(file.exists()) {
                try {
//                    System.err.println("Loading default..");
                    FileInputStream fis = new FileInputStream(file);
                    properties = new Properties();
                    properties.load(fis);
                    fis.close();
                } catch(IOException ioe) {
                    throw new NamingException("Failure to open: "+file);
                }
            }
        }

        if(properties == null) {
            // unable to find default
            throw new InvalidNameException("Properties for "+name+" not found. ");
        }

        if("true".equals(properties.get("org.apache.commons.dbutils.jndi.datasource"))) {
//            System.err.println("Datasource!");
            PropertiesDataSource pds = new PropertiesDataSource(properties, env);
            pds.setName(StringUtils.prechomp(StringUtils.getChomp(name,"."),"."));
            return pds;
        }

//        System.err.println("remaining: "+remaining);
        if(remaining == null) {
            // check here to see if org.apache.commons.dbutils.jndi.datasource=true is set.
            return properties;
        }

        String answer = properties.getProperty(remaining);

        if(answer == null) {
            throw new InvalidNameException(""+name+" not found. ");
        } else {
            if(properties.containsKey(remaining+".type")) {
                String type = properties.getProperty(remaining+".type");
                System.err.println("Should attempt to convert "+answer+" to "+type);
            }
            return answer;
        }
    }

    public void bind(Name name, Object object) throws NamingException {
        bind(name.toString(), object);
    }

    public void bind(String name, Object object) throws NamingException {
        if("".equals(name)) {
            throw new InvalidNameException("Cannot bind to empty name");
        } 
        if(this.table.get(name) != null) {
            throw new NameAlreadyBoundException("Use rebind to override");
        }
        this.table.put(name, object);
    }

    public void rebind(Name name, Object object) throws NamingException {
        rebind(name.toString(), object);
    }

    public void rebind(String name, Object object) throws NamingException {
        if("".equals(name)) {
            throw new InvalidNameException("Cannot bind to empty name");
        } 
        this.table.put(name, object);
    }

    public void unbind(Name name) throws NamingException {
        unbind(name.toString());
    }

    public void unbind(String name) throws NamingException {
        if("".equals(name)) {
            throw new InvalidNameException("Cannot bind to empty name");
        } 
        this.table.remove(name);
    }

    public void rename(Name name, Name newname) throws NamingException {
        rename(name.toString(), newname.toString());
    }

    public void rename(String oldname, String newname) throws NamingException {
        if("".equals(oldname) || "".equals(newname)) {
            throw new InvalidNameException("Cannot bind to empty name");
        } 
        if(this.table.get(newname) != null) {
            throw new NameAlreadyBoundException(""+newname+" is already bound");
        }
        Object old = this.table.remove(oldname);
        if(old == null) {
            throw new NameNotFoundException(""+oldname+" not bound");
        }
    }

    public NamingEnumeration list(Name name) throws NamingException {
        return list(name.toString());
    }

    public NamingEnumeration list(String name) throws NamingException {
        if("".equals(name)) {
            // here we should return a list of the directories and prop files 
            // minus the .properties that are in the root directory
            return new PropertiesNames(this.table.keys());
        }

// if name is a directory, we should do the same as we do above
// if name is a properties file, we should return the keys (?)
// issues: default.properties ?

        Object target = lookup(name);
        if(target instanceof Context) {
            return ((Context)target).list("");
        }
        throw new NotContextException(name+" cannot be listed");
    }

    public NamingEnumeration listBindings(Name name) throws NamingException {
        return listBindings(name.toString());
    }

    public NamingEnumeration listBindings(String name) throws NamingException {
        if("".equals(name)) {
            return new PropertiesBindings(this.table.keys());
        }

        Object target = lookup(name);
        if(target instanceof Context) {
            return ((Context)target).listBindings("");
        }
        throw new NotContextException(name+" cannot be listed");
    }

    public void destroySubcontext(Name name) throws NamingException {
        destroySubcontext(name.toString());
    }

    public void destroySubcontext(String name) throws NamingException {
        throw new OperationNotSupportedException("Unsupported");
    }

    public Context createSubcontext(Name name) throws NamingException {
        return createSubcontext(name.toString());
    }

    public Context createSubcontext(String name) throws NamingException {
        throw new OperationNotSupportedException("Unsupported");
    }

    public Object lookupLink(Name name) throws NamingException {
        return lookupLink(name.toString());
    }

    public Object lookupLink(String name) throws NamingException {
        return lookup(name);
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return getNameParser(name.toString());
    }

    public NameParser getNameParser(String name) throws NamingException {
        // WTF DO I GET ONE OF THESE!
        return null; // implement
    }

    public Name composeName(Name name, Name name2) throws NamingException {
        // NO IDEA IF THIS IS RIGHT
        return getNameParser(name.toString()).parse(name2.toString());
    }

    public String composeName(String name, String prefix) throws NamingException {
        Name result = composeName(new CompositeName(name), new CompositeName(prefix));
        return result.toString();
    }

    public Object addToEnvironment(String name, Object object) throws NamingException {
        if(this.env == null) {
            return null;
        } else {
            return this.env.put(name, object);
        }
    }

    public Object removeFromEnvironment(String name) throws NamingException {
        if(this.env == null) {
            return null;
        } else {
            return this.env.remove(name);
        }
    }

    public Hashtable getEnvironment() throws NamingException {
        if(this.env == null) {
            return new Hashtable();
        } else {
            return (Hashtable)this.env.clone();
        }
    }

    public void close() throws NamingException {
        this.env = null;
        this.table = null;
    }

    public String getNameInNamespace() throws NamingException {
        return "";
    }

    class PropertiesNames implements NamingEnumeration {

        private Enumeration names;

        public PropertiesNames(Enumeration names) {
            this.names = names;
        }

        public boolean hasMoreElements() {
            return names.hasMoreElements();
        }

        public boolean hasMore() throws NamingException {
            return hasMoreElements();
        }

        public Object nextElement() {
            String name = (String)names.nextElement();
            String className = PropertiesContext.this.table.get(name).getClass().getName();
            return new NameClassPair(name, className);
        }

        public Object next() throws NamingException {
            return nextElement();
        }

        public void close() {
        }

    }

    class PropertiesBindings implements NamingEnumeration {

        private Enumeration names;

        public PropertiesBindings(Enumeration names) {
            this.names = names;
        }

        public boolean hasMoreElements() {
            return names.hasMoreElements();
        }

        public boolean hasMore() throws NamingException {
            return hasMoreElements();
        }

        public Object nextElement() {
            String name = (String)names.nextElement();
            return new Binding(name, PropertiesContext.this.table.get(name));
        }

        public Object next() throws NamingException {
            return nextElement();
        }

        public void close() {
        }

    }
}


