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
import javax.naming.ContextNotEmptyException;
import javax.naming.NamingException;
import javax.naming.NameParser;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NotContextException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.NameClassPair;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.osjava.naming.ContextBindings;
import org.osjava.naming.ContextNames;
import org.osjava.naming.SimpleNameParser;

import org.osjava.jndi.util.Parser;
import org.osjava.jndi.util.PropertiesParser;
import org.osjava.jndi.util.IniParser;
import org.osjava.jndi.util.XmlParser;
import org.osjava.jndi.util.HierarchicalMap;

/**
 * The heart of the system, the Context for simple-jndi. 
 */
public class PropertiesContext implements Context  {

    /**
     * Constant indicating whether or not the PropertiesContext is in debug
     * mode or not.  This is determined by the System Property SJ.DEBUG when 
     * the PropertiesContext is instantiated.
     */
    public static final boolean DEBUG = (System.getProperty("SJ.DEBUG")!=null);

    private static final Object FILE = new String("FILE");
    private static final Object CLASSPATH = new String("CLASSPATH");
    private static final Object HTTP = new String("HTTP");

    // table is used as a read-write cache which sits 
    // above the file-store
    private Hashtable table = new Hashtable();
    private Hashtable subContexts = new Hashtable();
    private Hashtable env = new Hashtable();
    private String root;
    private Object protocol;
    private String separator;
    private String delimiter;
    private NameParser nameParser;
    /* 
     * The name full name of this context. 
     */
    private Name nameInNamespace = null;

    // original values
    private String originalRoot;
    private String originalDelimiter;

    private boolean closing;

    /**
     * Creates a PropertiesContext.
     */
    public PropertiesContext() {
        this((Hashtable)null);
    }
    
    /**
     * Creates a PropertiesContext.
     * 
     * @param env a Hashtable containing the Context's environemnt.
     */
    public PropertiesContext(Hashtable env) {
        /* By default allow system properties to override. */
        this(env, true, null);
    }
    
    /**
     * Creates a PropertiesContext.
     * 
     * @param env a Hashtable containing the Context's environment.
     * @param systemOverride allow System Parameters to override the
     *        environment that is passed in.
     */
    public PropertiesContext(Hashtable env, boolean systemOverride) {
        this(env, systemOverride, null);
    }

    /**
     * Creates a PropertiesContext.
     * 
     * @param env a Hashtable containing the Context's environment.
     * @param parser the NameParser being used by the Context.
     */
    public PropertiesContext(Hashtable env, NameParser parser) {
        this(env, true, parser);
    }

    /**
     * Creates a PropertiesContext.
     * 
     * @param systemOverride allow System Parameters to override the
     *        environment that is passed in.
     */
    public PropertiesContext(boolean systemOverride) {
        this(null, systemOverride, null);
    }

    /**
     * Creates a PropertiesContext.
     * 
     * @param systemOverride allow System Parameters to override the
     *        environment that is passed in.
     * @param parser the NameParser being used by the Context.
     */
    public PropertiesContext(boolean systemOverride, NameParser parser) {
        this(null, systemOverride, parser);
    }

    /**
     * Creates a PropertiesContext.
     * 
     * @param parser the NameParser being used by the Context.
     */
    public PropertiesContext(NameParser parser) {
        this(null, true, parser);
    }

    /**
     * Creates a PropertiesContext.
     * 
     * @param env a Hashtable containing the Context's environment.
     * @param systemOverride allow System Parameters to override the
     *        environment that is passed in.
     * @param parser the NameParser being used by the Context.
     */
    public PropertiesContext(Hashtable env, boolean systemOverride, NameParser parser) {
        String shared = null;
        
        if(env != null) {
            this.env = (Hashtable)env.clone();
            this.root = (String)this.env.get("org.osjava.jndi.root");
            this.delimiter = (String)this.env.get("org.osjava.jndi.delimiter");
            shared = (String)this.env.get("org.osjava.jndi.shared");
        }

        /* let System properties override the jndi.properties file, if
         * systemOverride is true */
        if(systemOverride) {
            if(System.getProperty("org.osjava.jndi.root") != null) {
                this.root = System.getProperty("org.osjava.jndi.root");
            }
            if(System.getProperty("org.osjava.jndi.delimiter") != null) {
                this.delimiter = System.getProperty("org.osjava.jndi.delimiter");
            }
            if(System.getProperty("org.osjava.jndi.shared") != null) {
                shared = System.getProperty("org.osjava.jndi.shared");
            }
        }
        
        if("true".equals(shared)) {
            this.table = new StaticHashtable();
        }

        if(this.delimiter == null) {
            this.delimiter = ".";
        }
        this.originalRoot = this.root;
        this.originalDelimiter = this.delimiter;

        // Work out the protocol of the root
        // No root means we're using a classpath protocol,
        // no protocol means we're using file protocol [legacy]
        if(root != null) {
            int idx = root.indexOf("://");
            if(idx != -1) {
                String proto = root.substring(0,idx);
                this.root = root.substring(idx + "://".length());
                if("file".equals(proto)) {
                    this.protocol = FILE;
                    this.separator = ""+File.separatorChar;
                } else
                if("http".equals(proto)) {
                    this.protocol = HTTP;
                    this.separator = ""+File.separatorChar;
                    this.root = proto+"://"+this.root;
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
        
        if(parser == null) {
            try {
                nameParser = new SimpleNameParser(this);
            } catch (NamingException e) {
                /* 
                 * XXX: This should never really occur.  If it does, there is 
                 * a severe problem.  I also don't want to throw the exception
                 * right now because that would break compatability, even 
                 * though it is probably the right thing to do.  This might
                 * get upgraded to a fixme.
                 */
                e.printStackTrace();
            }
        }

if(DEBUG)            System.err.println("[CTXT]Protocol  is: "+this.protocol);
if(DEBUG)            System.err.println("[CTXT]Root      is: "+this.root);
if(DEBUG)            System.err.println("[CTXT]separator is: "+this.separator);

    }

    private PropertiesContext(PropertiesContext that) {
        this(that.env);
    }

    private boolean isEmpty() {
        return (table.size() > 0 || subContexts.size() > 0);
    }

    // to add:
    // need to add file://, although this is default
    // need to add classpath://. 
    // need to add http://.
    // Use VFS???
    /** 
     * @see javax.naming.Context#lookup(javax.naming.Name)
     */
    public Object lookup(Name name) throws NamingException {
        /* 
         * The string form of the name will be used in several places below 
         * if not matched in the hashtable.
         */
        String stringName = name.toString();
        /*
         * If name is empty then this context is to be cloned.  This is 
         * required based upon the javadoc of Context.  UGH!
         */
        if(name.size() == 0) {
            Object ret = null;
            try {
                ret = (PropertiesContext)this.clone();
            } catch(CloneNotSupportedException e) {
                /* 
                 * TODO: Improve error handling.  I'm not quite sure yet what 
                 *       should be done, but this almost certainly isn't it.
                 */
                e.printStackTrace();
            }
            if(ret != null) {
                return ret;
            }
        }

        /* Lookup system properties */
        /* NOTE: I'm not entirely sure that I like this approach.  
         * 1.) reconverting back to strings if a string was originally 
         *     passed.
         * 2.) I'm not sure I like system properties interfering.
         * -RMZ */
        if(System.getProperty(stringName) != null) {
            return System.getProperty(stringName);
        }

        /* Lookup the few special keys next. */
        if(isSpecialKey(stringName)) {
            return getSpecial(stringName);
        }

        Name objName = name.getPrefix(1);
        if(name.size() > 1) {
            /* Look in a subcontext. */
            if(subContexts.containsKey(objName)) {
                return ((Context)subContexts.get(objName)).lookup(name.getSuffix(1));
            } 
            /* TODO: Might need to do a little more work here and supply a 
             * reasonable message. */
            throw new NamingException();
        }
        
        /* Lookup the object in this context */
        if(table.containsKey(name)) {
            return table.get(objName);
        }
        
        stringName = handleJavaStandard(stringName);
        
        // name is a delimited notation, each element is either a 
        // directory, file or part of a key.
        String[] elements = PropertiesContext.split(stringName, this.delimiter);
        String path = root;
        HierarchicalMap hmap = null;
        int sz = elements.length;
        String remaining = null;
        
        for(int i=0; i<sz; i++) {
            String element = elements[i];
            
            Object file = getElement(path+this.separator+element);
            if( (file != null) && isDirectory(file) ) { 
    if(DEBUG)                System.err.println("[CTXT]Found directory. ");
                path = path+this.separator+element;
                continue;
            }
            
            file = getElement(path+this.separator+element+".properties");
            if(file == null) {
                file = getElement(path+this.separator+element+".xml");
            }
            if(file == null) {
                file = getElement(path+this.separator+element+".ini");
            }
            if(file != null) {
                path = path+this.separator+element;
                hmap = loadHierarchicalMap(file);
                
                // build the rest of the list
                java.util.ArrayList list = new java.util.ArrayList();
                for(int j=i+1; j<sz; j++) {
                    list.add(elements[j]);
                }
                if(list.size() > 0) {
                    remaining = PropertiesContext.join(list.iterator(), this.delimiter);
                }
    if(DEBUG)                System.err.println("[CTXT]FILE FOUND: "+file);
    if(DEBUG)                System.err.println("[CTXT]Remaining: "+remaining);
    if(DEBUG)                System.err.println("[CTXT]element: "+element);
    if(DEBUG)                System.err.println("[CTXT]path: "+path);
                break;
            } else {
                java.util.ArrayList list = new java.util.ArrayList();
                for(int j=i; j<sz; j++) {
                    list.add(elements[j]);
                }
                if(list.size() > 0) {
                    remaining = PropertiesContext.join(list.iterator(), this.delimiter);
                }
    if(DEBUG)                System.err.println("[CTXT]Remaining2: "+remaining);
                break;  // TODO: Is this right?
            }
        }
        
        if(hmap == null) {
            //  if hmap is null, then we look for default.properties
            Object file = getElement(path+this.separator+"default.properties");
            if(file == null) {
                file = getElement(path+this.separator+"default.xml");
            }
            if(file == null) {
                file = getElement(path+this.separator+"default.ini");
            }
            if(file != null) {
                hmap = loadHierarchicalMap(file);
            }
        }
        
        // We have a HierarchicalMap object by now, or should.
        
        // TODO: If not, should we attempt to search up the tree?
        // For example, in classpath, com.genjava, com is a directory. 
        // if genjava doesn't exist, it should look for com.genjava as a 
        // key in the parent directory, ad infinitum
        
        if(hmap == null) {
            // unable to find default
            throw new InvalidNameException("HierarchicalMap for "+name+" not found. ");
        }
        
        // TODO: Rewrite this block. Not enough grokk. Very badly grokked.
        String typeLookup = "type";
        if( remaining != null && !remaining.equals("")) {
            typeLookup = remaining + this.delimiter + typeLookup;
        }
    if(DEBUG)        System.err.println("[CTXT]Type-lookup: " + typeLookup);
    if(DEBUG)        System.err.println("[CTXT]DS-type? : " + hmap.get(typeLookup));
    if(DEBUG)        System.err.println("[CTXT]DS-hmap : " + hmap);
        if( "javax.sql.DataSource".equals(hmap.get(typeLookup)) ) 
        {
    if(DEBUG)            System.err.println("[CTXT]Found Datasource!");
            PropertiesDataSource pds = new PropertiesDataSource(hmap, env, this.delimiter);
            String dsName = null;   // never remaining???;
            if(dsName == null) {
                // wants to be the path without the root
    if(DEBUG)                System.err.println("[CTXT]root: "+root);
    if(DEBUG)                System.err.println("[CTXT]path: "+path);
                int ln = root.length() + this.separator.length();
    if(DEBUG)                System.err.println("[CTXT]length of root+separator: "+ln);
                if(path.equals(root)) {
                    dsName = remaining;
                } else {
                    dsName = path.substring(ln);
                }
            }
            
    if(DEBUG)            System.err.println("[CTXT]Remaining: "+remaining);
    if(DEBUG)            System.err.println("[CTXT]DsName: '"+dsName+"'");
    if(DEBUG)            System.err.println("[CTXT]Name: '"+name+"'");
            
            // get the last element in 'name'
            String dsn = stringName;
            if(!dsn.equals(dsName)) {
                int edx = stringName.lastIndexOf(this.separator);
                if(edx != -1) {
                    // TODO: Needs a little safety
                    dsn = stringName.substring(edx+1);
                }
            }
    if(DEBUG)            System.err.println("[CTXT]DataSource name: "+dsn);
    if(dsn.equals(dsName)) {
    if(DEBUG)            System.err.println("[CTXT]Blanking datasource name. ");
                dsn = "";
            }
            
            // TODO: This only handles situations with one word in the Properties, ie) BlahDS.url. What if it's foo.com.BlahDS.url?? 
            pds.setName(dsn);
            return pds;
        }
        
    if(DEBUG)        System.err.println("[CTXT]remaining: "+remaining);
        if(remaining == null) {
            return hmap;
        }
        
        Object answer = hmap.get(remaining);
        
        if(answer == null) {
            throw new InvalidNameException(""+name+" not found. ");
        } else {
            return answer;
        }
    }

    /**
     * @see javax.naming.Context#lookup(java.lang.String)
     */
    public Object lookup(String name) throws NamingException {
        return lookup(nameParser.parse(name));
    }

    private boolean isSpecialKey(String key) {
        if("org.osjava.jndi.root".equals(key)) {
            return true;
        }
        if("org.osjava.jndi.delimiter".equals(key)) {
            return true;
        }
        return false;
    }

    private void setSpecial(String key, Object value) {
        if("org.osjava.jndi.root".equals(key)) {
            this.root = (String) value;
        }
        if("org.osjava.jndi.delimiter".equals(key)) {
            this.delimiter = (String) value;
        }
    }

    private void resetSpecial(String key) throws NamingException {
        if("org.osjava.jndi.root".equals(key)) {
            this.root = this.originalRoot;
        }
        if("org.osjava.jndi.delimiter".equals(key)) {
            this.delimiter = this.originalDelimiter;
        }
    }

    private Object getSpecial(String key) throws NamingException {
        if("org.osjava.jndi.root".equals(key)) {
            return this.root;
        }
        if("org.osjava.jndi.delimiter".equals(key)) {
            return this.delimiter;
        }
        throw new NamingException("Simple-JNDI incorrectly believes "+key+" is special. ");
    }

    /*
     * java:/ is impossible to deal with on a Windows box.
     * Complete utter pain. Solution is to remove the :
     */
    private String handleJavaStandard(String key) {
        if(key != null) {
            if(key.equals("java:")) {
                return "java";
            }
            if(key.equals("java:/")) {
                return "java/";
            }
            if(key.startsWith("java:/")) {
                return "java/"+key.substring("java:/".length());
            }
        }
        return key;
    }

    private Object getElement(String key) throws NamingException {
        
if(DEBUG)        System.err.println("[CTXT]Getting element "+key+" via "+this.protocol);
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
            URL url = this.getClass().getClassLoader().getResource(key);
            return url;
        } else
        if(this.protocol == HTTP) {
            try {
                URL url = new URL(key);
                if( urlExists(url) ) {
                    return url;
                } else {
                    return null;
                }
            } catch(MalformedURLException murle) {
                throw new NamingException("Unable to treat this as an http url: "+key);
            }
        } else {
            throw new NamingException("Unsupported protocol: "+this.protocol);
        }
    }

    private HierarchicalMap loadHierarchicalMap(Object file) throws NamingException {
if(DEBUG)        System.err.println("[CTXT]Loading values from: "+file);
        Parser parser = null;
        if(file instanceof File) {
            if( ((File)file).getName().endsWith(".xml") ) {
                parser = new XmlParser();
                ((XmlParser)parser).setDelimiter(this.delimiter);
            } else 
            if( ((File)file).getName().endsWith(".ini") ) {
                parser = new IniParser();
                ((IniParser)parser).setDelimiter(this.delimiter);
            } else {
                parser = new PropertiesParser();
            }
        } else
        if(file instanceof URL) {
            if( ((URL)file).getFile().endsWith(".xml") ) {
                parser = new XmlParser();
                ((XmlParser)parser).setDelimiter(this.delimiter);
            } else
            if( ((URL)file).getFile().endsWith(".ini") ) {
                parser = new IniParser();
                ((IniParser)parser).setDelimiter(this.delimiter);
            } else
            if( ((URL)file).getFile().endsWith(".properties") ) {
                parser = new PropertiesParser();
            } else {
                return null;
            }
        } else {
            System.err.println("[CTXT]Warning: Located file was not a File or a URL. ");
            parser = new PropertiesParser();
        }

        HierarchicalMap hmap = new HierarchicalMap(this.delimiter);

        if(this.protocol == FILE) {
            try {
if(DEBUG)                System.err.println("[CTXT]Loading FILE: "+file);
                FileInputStream fis = new FileInputStream((File)file);
                parser.parse(fis, hmap);
                fis.close();
                return hmap;
            } catch(IOException ioe) {
                throw new NamingException("Failure to open: "+file);
            }
        } else
        if(this.protocol == CLASSPATH) {
            try {
if(DEBUG)                System.err.println("[CTXT]Loading CLASSPATH: "+file);
                InputStream fis = ((URL)file).openStream();
                parser.parse(fis, hmap);
                fis.close();
                return hmap;
            } catch(IOException ioe) {
                throw new NamingException("Failure to open: "+file);
            }
        } else
        if(this.protocol == HTTP) {
            try {
if(DEBUG)                System.err.println("[CTXT]Loading HTTP: "+file);
                InputStream fis = ((URL)file).openStream();
                parser.parse(fis, hmap);
                fis.close();
                return hmap;
            } catch(IOException ioe) {
                throw new NamingException("Failure to open: "+file);
            }
        } else {
            // TODO: should this be thrown in the constructor too??
            throw new NamingException("Unsupported protocol: "+this.protocol);
        }
    }

    private boolean isDirectory(Object file) throws NamingException {
if(DEBUG)        System.err.println("[CTXT]Deciding if this is a directory-> "+file);
        if(this.protocol == FILE) {
            return ((File)file).isDirectory();
        } else
        if(this.protocol == CLASSPATH) {
            // how to figure out if this is a directory?
            // could use reflection, currently we'll copy the http solution
            try { 
                HierarchicalMap props = loadHierarchicalMap(file);
                if(props == null) {
                    return true;
                } else {
                    // This is shit. Somehow I am getting an index back
                    // and I assume it is a directory as every key 
                    // starts with <, ie html markup.
                    // replace with reflection??
                    Iterator iterator = props.keySet().iterator();
                    while(iterator.hasNext()) {
                        String key = (String)iterator.next();
                        if(!key.startsWith("<")) {
                            return false;
                        }
                    }
                    return true;
                }
            } catch(Exception e) {
                // we assume this just means a failure to load,
                // therefore it must be a directory
if(DEBUG)                System.err.println("[CTXT]Unknown exception: "+e);
                return true;
            }
        } else
        if(this.protocol == HTTP) {
            // how the hell do we know a directory online???
            try { 
                HierarchicalMap props = loadHierarchicalMap(file);
                if(props == null) {
                    // TODO: Test against a server that disallows directory viewing
                    // file ought to be URL here anyway
                    if(file instanceof URL) {
                        return urlExists( (URL)file );
                    } else {
                        return false;
                    }
                } else {
                    // This is shit. Somehow I am getting an index back
                    // and I assume it is a directory if any key 
                    // starts with <, ie html markup.
                    Iterator iterator = props.keySet().iterator();
                    while(iterator.hasNext()) {
                        String key = (String)iterator.next();
                        if(key.startsWith("<")) {
                            return true;
                        }
                    }
                    return false;
                }
            } catch(Exception e) {
                // we assume this just means a failure to load,
                // therefore it must be a directory
if(DEBUG)       System.err.println("[CTXT]HTTPException? :"+e);
                // TODO: Look at the error code and decide how to handle that
                return true;
            }
        } else {
            throw new NamingException("Unsupported protocol: "+this.protocol);
        }
    }

    /* Start of Write-functionality */
    /**
     * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
     */
    public void bind(Name name, Object object) throws NamingException {
        /* 
         * If the name of obj doesn't start with the name of this context, 
         * it is an error, throw a NamingException
         */
        if(name.size() > 1) {
            Name prefix = name.getPrefix(1);
            if(subContexts.containsKey(prefix)) {
                ((Context)subContexts.get(prefix)).bind(name.getSuffix(1), object);
                return;
            }
        }
        if(name.size() == 0) {
            throw new InvalidNameException("Cannot bind to an empty name");
        }
        /* Determine if the name is already bound */
        if(env.containsKey(name)) {
            throw new NameAlreadyBoundException("Name " + name.toString()
                + " already bound.  Use rebind() to override");
        }
        put(name, object);
    }

    /**
     * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
     */
    public void bind(String name, Object object) throws NamingException {
        bind(nameParser.parse(name), object);
    }

    private void put(Name name, Object object) {
        if(isSpecialKey(name.toString())) {
            setSpecial(name.toString(), object);
        } else if(object instanceof Context) {
            this.subContexts.put(name, object);
        } else {
            this.table.put(name, object);
        }
    }

    /**
     * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
     */
    public void rebind(Name name, Object object) throws NamingException {
        if(name.isEmpty()) {
            throw new InvalidNameException("Cannot bind to empty name");
        }
        /* Look up the target context first. */
        Object targetContext = lookup(name.getPrefix(name.size() - 1));
        if(targetContext == null || !(targetContext instanceof Context)) {
            throw new NamingException("Cannot bind object.  Target context does not exist.");
        }
        unbind(name);
        bind(name, object);
    }

    /**
     * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
     */
    public void rebind(String name, Object object) throws NamingException {
        rebind(nameParser.parse(name), object);
    }

    /**
     * @see javax.naming.Context#unbind(javax.naming.Name)
     */
    public void unbind(Name name) throws NamingException {
        String stringName = name.toString();
        if(name.isEmpty()) {
            throw new InvalidNameException("Cannot unbind to empty name");
        }

        if(isSpecialKey(stringName)) {
            resetSpecial(stringName);
        }

        Object obj = null;
        if(name.size() == 1) {
            if(table.containsKey(name)) {
                table.remove(name);
            }
            return;
        }
        
        /* Look up the target context first. */
        Object targetContext = lookup(name.getPrefix(name.size() - 1));
        if(targetContext == null || !(targetContext instanceof Context)) {
            throw new NamingException("Cannot unbind object.  Target context does not exist.");
        }
        ((Context)targetContext).unbind(name.getSuffix(name.size() - 1));
    }

    /**
     * @see javax.naming.Context#unbind(java.lang.String)
     */
    public void unbind(String name) throws NamingException {
        unbind(nameParser.parse(name));
    }

    /**
     * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
     */
    public void rename(Name oldName, Name newName) throws NamingException {
        /* Confirm that this works.  We might have to catch the exception */
        Object old = lookup(oldName);
        if(newName.isEmpty()) {
            throw new InvalidNameException("Cannot bind to empty name");
        }
        
        if(old == null) {
            throw new NamingException("Name '" + oldName + "' not found.");
        }

        /* If the new name is bound throw a NameAlreadyBoundException */
        if(lookup(newName) != null) {
            throw new NameAlreadyBoundException("Name '" + newName + "' already bound");
        }

        unbind(oldName);
        unbind(newName);
        bind(newName, old);
        /* 
         * If the object is a Thread, or a ThreadContext, give it the new 
         * name.
         */
        if(old instanceof Thread) {
            ((Thread)old).setName(newName.toString());
        }
    }

    /**
     * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
     */
    public void rename(String oldName, String newName) throws NamingException {
        rename(nameParser.parse(oldName), nameParser.parse(newName));
    }
    /* End of Write-functionality */

    /* Start of List functionality */
    /**
     * @see javax.naming.Context#list(javax.naming.Name)
     */
    public NamingEnumeration list(Name name) throws NamingException {
//      if name is a directory, we should do the same as we do above
//      if name is a properties file, we should return the keys (?)
//      issues: default.properties ?
        if(name.isEmpty()) {
            /* 
             * Because there are two mappings that need to be used here, 
             * create a new mapping and add the two maps to it.  This also 
             * adds the safety of cloning the two maps so the original is
             * unharmed.
             */
            Map enumStore = new HashMap();
            enumStore.putAll(table);
            enumStore.putAll(subContexts);
            NamingEnumeration enumerator = new ContextNames(enumStore);
            return enumerator;
        }
        /* Look for a subcontext */
        Name subName = name.getPrefix(1);
        if(table.containsKey(subName)) {
            /* Nope, actual object */
            throw new NotContextException(name + " cannot be listed");
        }
        if(subContexts.containsKey(subName)) {
            return ((Context)subContexts.get(subName)).list(name.getSuffix(1));
        }
        /* 
         * Couldn't find the subcontext and it wasn't pointing at us, throw
         * an exception.
         */
        /* TODO: Give this a better message */
        throw new NamingException();
    }


    /**
     * @see javax.naming.Context#list(java.lang.String)
     */
    public NamingEnumeration list(String name) throws NamingException {
        return list(nameParser.parse(name));
    }

    /**
     * @see javax.naming.Context#listBindings(javax.naming.Name)
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        if("".equals(name)) {
            /* 
             * Because there are two mappings that need to be used here, 
             * create a new mapping and add the two maps to it.  This also 
             * adds the safety of cloning the two maps so the original is
             * unharmed.
             */
            Map enumStore = new HashMap();
            enumStore.putAll(table);
            enumStore.putAll(subContexts);
            return new ContextBindings(enumStore);
        }
        /* Look for a subcontext */
        Name subName = name.getPrefix(1);
        if(table.containsKey(subName)) {
            /* Nope, actual object */
            throw new NotContextException(name + " cannot be listed");
        }
        if(subContexts.containsKey(subName)) {
            return ((Context)subContexts.get(subName)).listBindings(name.getSuffix(1));
        }
        /* 
         * Couldn't find the subcontext and it wasn't pointing at us, throw
         * an exception.
         */
        throw new NamingException();
    }

    /**
     * @see javax.naming.Context#listBindings(java.lang.String)
     */
    public NamingEnumeration listBindings(String name) throws NamingException {
        return listBindings(nameParser.parse(name));
    }
    /* End of List functionality */

    /**
     * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
     */
    public void destroySubcontext(Name name) throws NamingException {
        if(name.size() > 1) {
            if(subContexts.containsKey(name.getPrefix(1))) {
                Context subContext = (Context)subContexts.get(name.getPrefix(1));
                subContext.destroySubcontext(name.getSuffix(1));
                return;
            } 
            /* TODO: Better message might be necessary */
            throw new NameNotFoundException();
        }
        /* Look at the contextStore to see if the name is bound there */
        if(table.containsKey(name)) {
            throw new NotContextException();
        }
        /* Look for the subcontext */
        if(!subContexts.containsKey(name)) {
            throw new NameNotFoundException();
        }
        Context subContext = (Context)subContexts.get(name); 
        /* Look to see if the context is empty */
        NamingEnumeration names = subContext.list("");
        if(names.hasMore()) {
            throw new ContextNotEmptyException();
        }
        ((Context)subContexts.get(name)).close();
        subContexts.remove(name);
    }

    /**
     * @see javax.naming.Context#destroySubcontext(java.lang.String)
     */
    public void destroySubcontext(String name) throws NamingException {
        destroySubcontext(nameParser.parse(name));
    }

    /**
     * @see javax.naming.Context#createSubcontext(javax.naming.Name)
     */
    public Context createSubcontext(Name name) throws NamingException {
        Context newContext;
        if(name.size() > 1) {
            if(subContexts.containsKey(name.getPrefix(1))) {
                Context subContext = (Context)subContexts.get(name.getPrefix(1));
                newContext = subContext.createSubcontext(name.getSuffix(1));
                return newContext;
            } 
            throw new NameNotFoundException("The subcontext " + name.getPrefix(1) + " was not found.");
        }
        
        if(table.containsKey(name) || subContexts.containsKey(name)) {
            throw new NameAlreadyBoundException();
        }

        Name contextName = (Name)nameInNamespace.clone();
        contextName.addAll(name);
        newContext = new PropertiesContext(env);
        ((PropertiesContext)newContext).setName(contextName);
        subContexts.put(name, newContext);
        return newContext;
    }

    /**
     * @see javax.naming.Context#createSubcontext(java.lang.String)
     */
    public Context createSubcontext(String name) throws NamingException {
        return createSubcontext(nameParser.parse(name));
    }
    
    /**
     * Set the name of the Context.  This is only used from createSubcontext. 
     * It might get replaced by adding more constructors, but there is really
     * no reason to expose it publicly anyway.
     * 
     * @param name the Name of the context.
     * @throws NamingException if the subContext already has a name.
     */
    protected void setName(Name name) throws NamingException {
        if(nameInNamespace != null) {
            throw new NamingException("Name already set.");
        }
        nameInNamespace = name;
    }

    /**
     * @see javax.naming.Context#lookupLink(javax.naming.Name)
     */
    public Object lookupLink(Name name) throws NamingException {
        return lookup(name);
    }

    /**
     * @see javax.naming.Context#lookupLink(java.lang.String)
     */
    public Object lookupLink(String name) throws NamingException {
        return lookup(nameParser.parse(name));
    }

    /**
     * @see javax.naming.Context#getNameParser(javax.naming.Name)
     */
    public NameParser getNameParser(Name name) throws NamingException {
        if(name.isEmpty() ) {
            return nameParser;
        }
        Name subName = name.getPrefix(1); 
        if(subContexts.containsKey(subName)) {
            return ((Context)subContexts.get(subName)).getNameParser(name.getSuffix(1));
        }
        throw new NotContextException();    
    }

    /**
     * @see javax.naming.Context#getNameParser(java.lang.String)
     */
    public NameParser getNameParser(String name) throws NamingException {
        return getNameParser(nameParser.parse(name));
    }

    /**
     * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
     */
    public Name composeName(Name name, Name prefix) throws NamingException {
        // NO IDEA IF THIS IS RIGHT
        if(name == null || prefix == null) {
            throw new NamingException("Arguments must not be null");
        }
        Name retName = (Name)prefix.clone();
        retName.addAll(name);
        return retName;
    }

    /**
     * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
     */
    public String composeName(String name, String prefix) throws NamingException {
        Name retName = composeName(nameParser.parse(name), nameParser.parse(prefix));
        /* toString pretty much is guaranteed to exist */
        return retName.toString();
    }

    /**
     * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
     */
    public Object addToEnvironment(String name, Object object) throws NamingException {
        if(this.env == null) {
            return null;
        } else {
            return this.env.put(name, object);
        }
    }

    /**
     * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
     */
    public Object removeFromEnvironment(String name) throws NamingException {
        if(this.env == null) {
            return null;
        } else {
            return this.env.remove(name);
        }
    }

    /**
     * @see javax.naming.Context#getEnvironment()
     */
    public Hashtable getEnvironment() throws NamingException {
        if(this.env == null) {
            return new Hashtable();
        } else {
            return (Hashtable)this.env.clone();
        }
    }

    /**
     * @see javax.naming.Context#close()
     */
    public void close() throws NamingException {
        /* Don't try anything if we're already in the process of closing */
        if(closing) {
            return;
        }
        Iterator it = subContexts.keySet().iterator();
        while(it.hasNext()) {
            destroySubcontext((Name)it.next());
        }
        
        while(table.size() > 0 || subContexts.size() > 0) {
            it = table.keySet().iterator();
            while(it.hasNext()) {
                Name name = (Name)it.next();
                if(!((Thread)table.get(name)).isAlive()) {
                    table.remove(name);
                }
            }
            it = subContexts.keySet().iterator();
            while(it.hasNext()) {
                Name name = (Name)it.next();
                PropertiesContext context = (PropertiesContext)subContexts.get(name);
                if(context.isEmpty()) {
                    subContexts.remove(name);
                }
            }
        }
        this.env = null;
        this.table = null;
    }

    /**
     * @see javax.naming.Context#getNameInNamespace()
     */
    public String getNameInNamespace() throws NamingException {
        return nameInNamespace.toString();
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

    /* START OF StringUtils copy */
    private static String[] split(String str, String separatorChars) {
        return split(str,separatorChars,-1);
    }
    private static String[] split(String str, String separatorChars, int max) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()
        
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        List list = new java.util.ArrayList();
        int sizePlus1 = 1;
        int i =0, start = 0;
        boolean match = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    private static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(256);  // Java default is 16, probably too small
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if ((separator != null) && iterator.hasNext()) {
                buf.append(separator);
            }
         }
        return buf.toString();
    }
    /* END OF StringUtils copy */

    public static boolean urlExists(URL url) {
        try {
            return url.getContent() != null;
        } catch(IOException ioe) {
            return false;
        }
    }

}


