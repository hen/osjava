package com.generationjava.naming;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Jndi {

    private Context context;

    public Jndi() throws NamingException {
        context = new InitialContext();
    }

    public DataSource lookupDataSource(String key) throws NamingException {
        return (DataSource)this.context.lookup(key);
    }

    public String lookupString(String key) throws NamingException {
        return (String)this.context.lookup(key);
    }

}
