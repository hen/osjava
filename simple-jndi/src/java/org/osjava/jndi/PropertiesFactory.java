package com.generationjava.jndi;

import javax.naming.spi.InitialContextFactory;
import javax.naming.Context;
import java.util.Hashtable;

public class PropertiesFactory implements InitialContextFactory {

    public PropertiesFactory() {
        super();
    }

    public Context getInitialContext(Hashtable environment) {
        return new PropertiesContext(environment);
    }
}
