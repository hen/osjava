package org.osjava.scraping;

import java.util.List;

public interface Session extends Config {

    public Object get(String key);
    public String getString(String key);
    public List getList(String key);
    public Object remove(String key);

    public void put(String key, Object value);

}
