package org.osjava.scraping;

import java.util.List;
import java.util.Date;

public interface Config extends Cloneable {

    public Object get(String key);
    public boolean has(String key);
    public Object getAbsolute(String key);
    public String getString(String key);
    public int getInt(String key);
    public Date getDate(String key);
    public List getList(String key);
    public void setContext(String context);
    public String getContext();
    public Config cloneConfig();

}
