package org.osjava.jndi;

import org.osjava.naming.DelegatingContext;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import org.osjava.sj.loader.JndiLoader;

// job is to hide the JndiLoader, apart from a jndi.properties entry
// can also handle switching . to / so that the delimiter may be settable
public class SimpleContext extends DelegatingContext {

    public SimpleContext(Hashtable env) throws NamingException {
        super(new InitialContext(env));
        JndiLoader loader = new JndiLoader();
        // apply env to loader
        // run load on loader
    }
    
}
