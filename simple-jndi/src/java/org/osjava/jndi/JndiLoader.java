package org.osjava.jndi;

import java.util.*;
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

    public void load(Properties properties) throws NamingException {

        // find all .xxx files from a given root; 
        // add their contents into the jndi system

        // problems: classpath doesn't work well here
        // if unpacked, it will do the find fine
        // if packed, it won't. So will need to iterate 
        // through the classpath I guess

        Context init = new InitialContext(table);

        Context cmp = init.createSubcontext("java:comp");

        // each directory equals a subcontext
        Context env = cmp.createSubcontext("env");
        env.bind("test", "42");
    }

}
