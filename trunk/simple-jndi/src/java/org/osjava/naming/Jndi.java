package org.osjava.naming;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Jndi {

    private Context context;

    public Jndi() throws NamingException {
        context = new InitialContext();
    }

    public Object lookup(String key) throws NamingException {
        return this.context.lookup(key);
    }

    public DataSource lookupDataSource(String key) throws NamingException {
        return (DataSource)this.context.lookup(key);
    }

    public String lookupString(String key) throws NamingException {
        return (String)this.context.lookup(key);
    }

    public List lookupList(String key) throws NamingException {
        Object obj = this.context.lookup(key);
        if( !(obj instanceof List) ) {
            List list = new ArrayList(1);
            list.add(obj);
            obj = list;
        }
        return (List)obj;
    }

}
