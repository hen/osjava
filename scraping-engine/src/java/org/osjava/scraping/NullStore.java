package org.osjava.scraping;

import java.util.Iterator;

public class NullStore implements Store {

    public void store(Result result, Config cfg, Session session) throws StoringException {
        if(false) throw new StoringException(null,null);
        Iterator iterator = result.iterateRows();
        int i=0;
        while(iterator.hasNext()) {
            Object[] array = (Object[])iterator.next();
            for(int j=0; j<array.length; j++) {
                System.err.println("["+i+", "+j+"] = "+array[j]);
            }
            i++;
        }
    }

    public boolean exists(Header header, Config cfg, Session session) throws StoringException {
        return false;
    }

}
