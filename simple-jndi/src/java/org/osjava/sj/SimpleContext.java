package org.osjava.sj;

import org.osjava.sj.naming.DelegatingContext;

import java.io.File;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import org.osjava.sj.loader.JndiLoader;

// job is to hide the JndiLoader, apart from a jndi.properties entry
// can also handle switching . to / so that the delimiter may be settable
public class SimpleContext extends DelegatingContext {

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
        super(new InitialContext(env));

        JndiLoader loader = new JndiLoader();

        String root = (String) env.get("org.osjava.jndi.root");

        loader.loadDirectory( new File(root), this );
    }
    
}
