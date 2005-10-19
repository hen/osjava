package org.osjava.oscube.service.store;

import java.util.Iterator;

import com.generationjava.lang.StringW;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;
import org.osjava.oscube.container.Header;
import org.osjava.oscube.container.Result;

public class ConsoleStore implements Store {

    public void store(Result result, Config cfg, Session session) throws StoringException {
        if(false) throw new StoringException(null,null);
        Iterator iterator = result.iterateRows();
        while(iterator.hasNext()) {
            Object row = iterator.next();
            echo(row);
        }
    }

    private void echo(Object obj) {
        if(obj == null) {
            System.out.println("NULL ROW");
        } else
        if(obj instanceof Object[]) {
            Object[] objs = (Object[])obj;
            System.out.println( StringW.join(objs, ", ", "{ ", " }") );
        } else {
            System.out.println(obj.toString());
        }
    }

}
