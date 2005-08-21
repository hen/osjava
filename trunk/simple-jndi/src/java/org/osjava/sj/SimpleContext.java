package org.osjava.sj;

import org.osjava.sj.naming.DelegatingContext;

import java.io.File;
import java.io.IOException;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import org.osjava.sj.loader.JndiLoader;

// job is to hide the JndiLoader, apart from a jndi.properties entry
// can also handle switching . to / so that the delimiter may be settable
public class SimpleContext extends DelegatingContext {

    // root
    public static final String SIMPLE_ROOT = "org.osjava.sj.root";

    public static final String SIMPLE_DELEGATE = "org.osjava.sj.factory";

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

        try {
            loader.loadDirectory( new File(root), this );
        } catch(IOException ioe) {
            throw new NamingException("Unable to load data from directory: "+root+" due to error: "+ioe.getMessage());
        }
    }
    
    private static InitialContext createContext(Hashtable env) throws NamingException {
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

}
