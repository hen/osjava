package org.osjava.sj.loader;

import java.util.*;
import java.io.*;
import javax.naming.*;

/**
 * Loads a .properties file into a JNDI server.
 */
public class JndiLoader {

    private Hashtable table = new Hashtable();
//        table.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
//        table.put(Context.URL_PKG_PREFIXES, "org.apache.naming");

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

        // problems: classpath doesn't work well here
        // if unpacked, it will do the find fine
        // if packed, it won't. So will need to iterate 
        // through the classpath I guess
        //Context init = new InitialContext(this.table);

    /**
     * Loads all .properties files in a directory into a context
     */
    public void loadDirectory(File directory, Context ctxt) throws NamingException, IOException {

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("java.io.File parameter must be a directory. ");
        }

        File[] files = directory.listFiles();
        if(files == null) {
            return;
        }

        for(int i=0; i<files.length; i++) {
            File file = files[i];
            if( file.isDirectory() ) {
                loadDirectory(file, ctxt);
            } else
            if( file.getName().endsWith(".properties") ) {
                load( loadFile(file), ctxt );
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
            return p;
        } finally {
            if(fin != null) fin.close();
        }
    }


    /**
     * Loads a properties object into a context.
     */
    public void load(Properties properties, Context ctxt) throws NamingException {

        Iterator iterator = properties.keySet().iterator();
        while(iterator.hasNext()) {
            String key = (String) iterator.next();
            ctxt.bind( key, properties.get(key) );
        }

    }

        /*
        Context cmp = init.createSubcontext("java:comp");

        // each directory equals a subcontext
        Context env = cmp.createSubcontext("env");
        env.bind("test", "42");
        */
}
