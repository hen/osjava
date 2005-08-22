package org.osjava.sj;

import org.osjava.sj.naming.DelegatingContext;

import java.io.File;
import java.io.IOException;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.naming.Context;

import org.osjava.sj.loader.JndiLoader;

// job is to hide the JndiLoader, apart from a jndi.properties entry
// can also handle switching . to / so that the delimiter may be settable
public class SimpleContext extends DelegatingContext {

    // root
    public static final String SIMPLE_ROOT = "org.osjava.sj.root";

    public static final String SIMPLE_DELEGATE = "org.osjava.sj.factory";

    // option for top level space; ie) java:comp
    public static final String SIMPLE_SPACE = "org.osjava.sj.space";

    /*
     * 
     * root
     *    org.osjava.jndi.root
     * separator, or just put them in as contexts?
     *    org.osjava.jndi.delimiter
     * option for top level space; ie) java:comp
     *    org.osjava.jndi.space
     * share the same InitialContext
     *    org.osjava.jndi.shared
     */
    public SimpleContext(Hashtable env) throws NamingException {
        super(createContext(env));

        JndiLoader loader = new JndiLoader(env);

        String root = (String) env.get(SIMPLE_ROOT);

        if(root == null) {
            throw new IllegalStateException("Property "+SIMPLE_ROOT+" is mandatory. ");
        }

        if(root.startsWith("file://")) {
            root = root.substring("file://".length());
        }

        Context ctxt = this;
        String space = (String) env.get(SIMPLE_SPACE);
        if(space != null) {
            // make contexts for space...
            String[] array = JndiLoader.split(space, (String) env.get(JndiLoader.SIMPLE_DELIMITER) );
            for(int i=0; i<array.length; i++) {
                ctxt = ctxt.createSubcontext(array[i]);
            }
        }

        try {
            loader.loadDirectory( new File(root), ctxt );
        } catch(IOException ioe) {
            throw new NamingException("Unable to load data from directory: "+root+" due to error: "+ioe.getMessage());
        }
    }
    
    private static InitialContext createContext(Hashtable env) throws NamingException {

        copyFromSystemProperties(env, JndiLoader.SIMPLE_DELIMITER);
        copyFromSystemProperties(env, SIMPLE_ROOT);
        copyFromSystemProperties(env, SIMPLE_SPACE);
        copyFromSystemProperties(env, JndiLoader.SIMPLE_SHARED);
        copyFromSystemProperties(env, SIMPLE_DELEGATE);
        
        env.put("jndi.syntax.direction", "left_to_right");
        if(!env.containsKey(JndiLoader.SIMPLE_DELIMITER)) {
            env.put(JndiLoader.SIMPLE_DELIMITER, ".");
        }
        env.put("jndi.syntax.separator", env.get(JndiLoader.SIMPLE_DELIMITER));

        if(!env.containsKey(SIMPLE_DELEGATE)) {
            env.put(SIMPLE_DELEGATE, "org.osjava.sj.memory.MemoryContextFactory");
        }

        env.put("java.naming.factory.initial", env.get(SIMPLE_DELEGATE) );

        return new InitialContext(env);
    }

    private static void copyFromSystemProperties(Hashtable env, String key) {
        if(System.getProperty(key) != null) {
            env.put(key, System.getProperty(key));
        }
    }

}
