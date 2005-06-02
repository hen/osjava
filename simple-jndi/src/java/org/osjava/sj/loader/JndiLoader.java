package org.osjava.sj.loader;

import java.util.*;
import java.io.*;
import javax.naming.*;

/**
 * Loads a .properties file into a JNDI server.
 */
public class JndiLoader {

    private Hashtable table = new Hashtable();

    public JndiLoader() {
        // tmphack
        this.table.put( SIMPLE_DELIMITER, "/" );
    }
    
// For Directory Naming:
//        table.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
//        table.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
// For GenericContext:

    // root
    public static final String SIMPLE_ROOT = "org.osjava.jndi.root";
    // separator, or just put them in as contexts?
    public static final String SIMPLE_DELIMITER = "org.osjava.jndi.delimiter";
    // option for top level space; ie) java:comp
    public static final String SIMPLE_SPACE = "org.osjava.jndi.space";
    // share the same InitialContext
    public static final String SIMPLE_SHARED = "org.osjava.jndi.shared";

    public void putParameter(String key, String value) {
        table.put(key, value);
    }

    public String getParameter(String key) {
        return (String) table.get(key);
    }

    /**
     * Loads all .properties files in a directory into a context
     */
    public void loadDirectory(File directory, Context ctxt) throws NamingException, IOException {

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("java.io.File parameter must be a directory. ");
        }

        File[] files = directory.listFiles();
        if(files == null) {
// System.err.println("Null files. ");
            return;
        }

        for(int i=0; i<files.length; i++) {
            File file = files[i];
            String name = file.getName();
// System.err.println("Consider: "+name);
            // TODO: Replace hack with a FilenameFilter

            if( file.isDirectory() ) {
                // HACK: Hack to stop it looking in .svn or CVS
                if(name.equals(".svn") || name.equals("CVS")) {
                    continue;
                }

// System.err.println("Is directory. Creating: "+name);
                Context tmpCtxt = ctxt.createSubcontext( name );
                loadDirectory(file, tmpCtxt);
            } else
            if( file.getName().endsWith(".properties") ) {
// System.err.println("Is .properties file. "+name);
                Context tmpCtxt = ctxt;
                if(!file.getName().equals("default.properties")) {
                    name = name.substring(0, name.length() - ".properties".length());
// System.err.println("Not default, so creating subcontext: "+name);
                    tmpCtxt = ctxt.createSubcontext( name );
                }
                load( loadFile(file), tmpCtxt );
            }
        }

    }

    private Properties loadFile(File file) throws IOException {
        // replace with better prop class
        Properties p = new Properties();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            p.load(fin);
// System.err.println("Loaded: "+p);
            return p;
        } finally {
            if(fin != null) fin.close();
        }
    }


    /**
     * Loads a properties object into a context.
     */
    public void load(Properties properties, Context ctxt) throws NamingException {
// System.err.println("Loading Properties");

        Iterator iterator = properties.keySet().iterator();
        while(iterator.hasNext()) {
            String key = (String) iterator.next();
// System.err.println("KEY: "+key);

            // here we need to break by the specified delimiter
            String[] path = key.split( (String) this.table.get(SIMPLE_DELIMITER) );
// System.err.println("LN: "+path.length);
            Context tmpCtxt = ctxt;
            int lastIndex = path.length - 1;
            for(int i=0; i < lastIndex; i++) {
                Object obj = tmpCtxt.lookup(path[i]);
                if(obj == null) {
// System.err.println("Creating: "+path[i]);
                    tmpCtxt = tmpCtxt.createSubcontext(path[i]);
                } else
                if(obj instanceof Context) {
// System.err.println("Using: "+obj);
                    tmpCtxt = (Context) obj;
                } else {
                    // HACK: Unsure how to handle the /type modifier currently
// System.err.println("Skipping due to non-support, most likely this is due to 'type': "+obj+" and "+path[lastIndex]);
                    lastIndex--;
                    break;
                }
            }
            Object obj = tmpCtxt.lookup(path[lastIndex]);
            if(obj == null) {
// System.err.println("Binding: "+path[lastIndex]);
                tmpCtxt.bind( path[lastIndex], properties.get(key) );
            } else {
// System.err.println("Rebinding: "+path[lastIndex]);
                tmpCtxt.rebind( path[lastIndex], properties.get(key) );
            }
        }

    }

}
