package org.osjava.scraping;

import org.osjava.scraping.util.FactoryUtils;

public class StoreFactory {

    static public Store getStore(Config cfg, Session session) {
        String name = cfg.getString("store");
        Store store = (Store)FactoryUtils.getObject(name, "Store");
        if(store == null) {
            throw new RuntimeException("Unable to find Store. ");
        }
        return store;
    }

}
