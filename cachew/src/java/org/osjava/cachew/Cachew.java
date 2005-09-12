/*
 * Created on Jun 10, 2005
 */
package org.osjava.cachew;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Caches a selection of items that came from the same core URL.
 * 
 * @author hen
 */
public class Cachew {

    // TODO: Make this configurable
    private long interval = 60000L;

    private CachewItemCreator creator = null;

    private Map map = new HashMap();

    public CachewItem getItem(String path) {
        Iterator itr = map.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            if (path.matches(key)) {
                CachewItem item = (CachewItem) map.get(path);
                if (item.getCreationTime().getTime() + interval 
                        > System.currentTimeMillis()) 
                {
                    return createItem(path);
                }
            }
        }
        return null;
    }

    public CachewItem createItem(String path) {
        CachewItem item = creator.create(path);
        // TODO: Store regexp, not path here?
        map.put(path, item);
        return item;
    }

}