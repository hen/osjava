/*
 * Copyright (c) 2003, Henri Yandell
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
 * + Neither the name of Simple-JNDI nor the names of its contributors 
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

package org.osjava.jndi;

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
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.osjava.convert.Convert;
import org.osjava.jndi.util.CustomProperties;
import org.osjava.jndi.util.XmlProperties;

public class PropertiesContext implements Context  {

    private static Object FILE = new String("FILE");
    private static Object CLASSPATH = new String("CLASSPATH");
    private static Object HTTP = new String("HTTP");

    // table is used as a read-write cache which sits 
    // above the file-store
    private Hashtable table = new Hashtable();

    private Hashtable env;
    private String root;
    private Object protocol;
    private String separator;
    private String delimiter;

    public PropertiesContext(Hashtable env) {
        if(env != null) {
            this.env = (Hashtable)env.clone();
            this.root = (String)this.env.get("org.osjava.jndi.root");
            this.delimiter = (String)this.env.get("org.osjava.jndi.delimiter");
            if(this.delimiter == null) {
                this.delimiter = ".";
            }

            // Work out the protocol of the root
            // No root means we're using a classpath protocol,
            // no protocol means we're using file protocol [legacy]
            if(root != null) {
                if(root.indexOf("://") != -1) {
                    String proto = StringUtils.chomp(root, "://");
                    this.root = StringUtils.prechomp(root, "://");
                    if("file".equals(proto)) {
                        this.protocol = FILE;
                        this.separator = ""+File.separatorChar;
                    } else
                    if("http".equals(proto)) {
                        this.protocol = HTTP;
                        this.separator = ""+File.separatorChar;
                    } else 
                    if("classpath".equals(proto)) {
                        this.protocol = CLASSPATH;
                        this.separator = "/";
                    }
                } else {
                    this.protocol = FILE;
                    this.separator = ""+File.separatorChar;
                }
            } else {
                this.protocol = CLASSPATH;
                this.separator = "/";
                this.root = "";
            }

//            System.err.println("proto: "+this.protocol);
//            System.err.println("root: "+this.root);
//            System.err.println("sepChar: "+this.separator);
        }
    }

    private PropertiesContext(PropertiesContext that) {
        this(that.env);
    }

    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    private Object getElement(String key) throws NamingException {
//        System.err.println("Asked for: "+key+" via "+this.protocol);
        if(this.protocol == FILE) {
            File file = new File(key);
            if(!file.exists()) {
                file = null;
            }
            return file;
        } else
        if(this.protocol == CLASSPATH) {
            if(key.startsWith(this.separator)) {
                key = key.substring(1);
            }
//            System.err.println("KEY: "+key);
            URL url = this.getClass().getClassLoader().getResource(key);
            return url;
        } else
        if(this.protocol == HTTP) {
            try {
                URL url = new URL(key);
                return url;
            } catch(MalformedURLException murle) {
                throw new NamingException("Unable to open http url: "+key);
            }
        } else {
            throw new NamingException("Unsupported protocol: "+this.protocol);
        }
    }

    private Properties loadProperties(Object file) throws NamingException {
        Properties properties = null;
        if(file instanceof File) {
            if( ((File)file).getName().endsWith(".xml") ) {
                properties = new XmlProperties();
                ((XmlProperties)properties).setDelimiter(this.delimiter);
            } else {
                properties = new CustomProperties();
            }
        } else
        if(file instanceof URL) {
            if( ((URL)file).getFile().endsWith(".xml") ) {
                properties = new XmlProperties();
                ((XmlProperties)properties).setDelimiter(this.separator);
            } else {
                properties = new CustomProperties();
            }
        } else {
            properties = new CustomProperties();
        }

        if(this.protocol == FILE) {
            try {
                FileInputStream fis = new FileInputStream((File)file);
                properties.load(fis);
                fis.close();
                return properties;
            } catch(IOException ioe) {
                throw new NamingException("Failure to open: "+file);
            }
        } else
        if(this.protocol == CLASSPATH) {
            try {
                InputStream fis = ((URL)file).openStream();
                properties.load(fis);
                fis.close();
                return properties;
            } catch(IOException ioe) {
                throw new NamingException("Failure to open: "+file);
            }
        } else
        if(this.protocol == HTTP) {
            try {
                InputStream fis = ((URL)file).openStream();
                properties.load(fis);
                fis.close();
                return properties;
            } catch(IOException ioe) {
                throw new NamingException("Failure to open: "+file);
            }
        } else {
            throw new NamingException("Unsupported protocol: "+this.protocol);
        }
    }

    private boolean isDirectory(Object file) throws NamingException {
//        System.err.println("Is dir: "+file);
        if(this.protocol == FILE) {
            return ((File)file).isDirectory();
        } else
        if(this.protocol == CLASSPATH) {
            // how to figure out if this is a directory?
            // could use reflection, currently we'll copy the http solution
            try { 
                Properties props = loadProperties(file);
//                System.err.println("P:"+props);
                if(props == null) {
                    return true;
                } else {
//                    System.err.println("P#:"+props.size());
                    // This is shit. Somehow I am getting an index back
                    // and I assume it is a directory as every key 
                    // starts with <, ie html markup.
                    // replace with reflection??
                    Iterator iterator = props.keySet().iterator();
                    while(iterator.hasNext()) {
                        String key = (String)iterator.next();
                        if(!key.startsWith("<")) {
//                            System.err.println("bing: "+key);
                            return false;
                        }
                    }
                    return true;
                }
            } catch(Exception e) {
                // we assume this just means a failure to load,
                // therefore it must be a directory
//                System.err.println("Unknown e: "+e);
                return true;
            }
        } else
        if(this.protocol == HTTP) {
            // how the hell do we know a directory online???
            try { 
                Properties props = loadProperties(file);
                return (props == null);
            } catch(Exception e) {
                // we assume this just means a failure to load,
                // therefore it must be a directory
                return true;
            }
        } else {
            throw new NamingException("Unsupported protocol: "+this.protocol);
        }
    }

    // to add:
    // need to add file://, although this is default
    // need to add classpath://. 
    // need to add http://.
    // Use VFS???
    public Object lookup(String name) throws NamingException {
        if("".equals(name)) {
            return new PropertiesContext(this);
        }

        if(this.table.containsKey(name)) {
            return this.table.get(name);
        }

        if(System.getProperty(name) != null) {
            return System.getProperty(name);
        }

        // name is a delimited notation, each element is either a 
        // directory, file or part of a key.
        String[] elements = StringUtils.split(name, this.delimiter);
        String path = root;
        Properties properties = null;
        int sz = elements.length;
        String remaining = null;

        for(int i=0; i<sz; i++) {
            String element = elements[i];

            Object file = getElement(path+this.separator+element);
//            System.err.println("Into directory? "+file);
            if( (file != null) && isDirectory(file) ) { 
//                System.err.println("Was directory. ");
                path = path+this.separator+element;
                continue;
            }

            file = getElement(path+this.separator+element+".properties");
            if(file == null) {
                file = getElement(path+this.separator+element+".xml");
            }
//            System.err.println("Into file? "+file);
            if(file != null) {
                properties = loadProperties(file);

                // build the rest of the list
                java.util.ArrayList list = new java.util.ArrayList();
                for(int j=i+1; j<sz; j++) {
                    list.add(elements[j]);
                }
                if(list.size() > 0) {
                    remaining = StringUtils.join(list.iterator(), this.delimiter);
                }
                break;
            } else {
                java.util.ArrayList list = new java.util.ArrayList();
                for(int j=i; j<sz; j++) {
                    list.add(elements[j]);
                }
                if(list.size() > 0) {
                    remaining = StringUtils.join(list.iterator(), this.delimiter);
                }
//                System.err.println("Rem: "+remaining);
                break;  // TODO: Is this right?
            }
        }

        if(properties == null) {
            //  if properties is null, then we look for default.properties
            Object file = getElement(path+this.separator+"default.properties");
            if(file == null) {
                file = getElement(path+this.separator+"default.xml");
            }
            if(file != null) {
                properties = loadProperties(file);
            }
        }

       // We have a Properties object by now, or should.

       // TODO: If not, should we attempt to search up the tree?
       // For example, in classpath, com.genjava, com is a directory. 
       // if genjava doesn't exist, it should look for com.genjava as a 
       // key in the parent directory, ad infinitum

        if(properties == null) {
            // unable to find default
            throw new InvalidNameException("Properties for "+name+" not found. ");
        }

        if("true".equals(properties.get("org.osjava.jndi.datasource"))) {
//            System.err.println("Datasource!");
            PropertiesDataSource pds = new PropertiesDataSource(properties, env);
            pds.setName(StringUtils.prechomp(StringUtils.getChomp(name,this.delimiter),this.delimiter));
            return pds;
        }

//        System.err.println("remaining: "+remaining);
        if(remaining == null) {
            return properties;
        }

        Object answer = properties.get(remaining);

        if(answer == null) {
            throw new InvalidNameException(""+name+" not found. ");
        } else {
            if(properties.containsKey(remaining+".type")) {
                String type = properties.getProperty(remaining+".type");
                if(answer instanceof List) {
                    List list = (List)answer;
                    for(int i=0; i<list.size(); i++) {
                        list.set(i, Convert.convert((String)list.get(i), type) );
                    }
                    return list;
                } else {
                    return Convert.convert((String)answer, type);
                }
            } else {
                return answer;
            }
        }
    }

    /* Start of Write-functionality */
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
    /* End of Write-functionality */

    /* Start of List functionality */
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
    /* End of List functionality */

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


