package org.osjava.scraping;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

import org.apache.log4j.Logger;

public class JndiConfig extends AbstractConfig {

    private static Logger logger = Logger.getLogger(JndiConfig.class);

    private Context ctxt;

    public JndiConfig() {
        try {
            this.ctxt = new InitialContext();
        } catch(NamingException ne) {
            // TODO: Log
            logger.debug("Error creating context. ", ne);
            throw new RuntimeException("Unable to make InitialContext. No JNDI. ");
        }
    }

    protected Object getValue(String key) {
        try {
            return ctxt.lookup(key);
        } catch(NamingException ne) {
            logger.debug("JNDI failed to find: "+key);
            return null;
        }
    }

}
