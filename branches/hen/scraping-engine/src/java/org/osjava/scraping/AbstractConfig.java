package org.osjava.scraping;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.NumberUtils;
import java.util.Date;

public abstract class AbstractConfig implements Config {

    private String context = "";

    protected abstract Object getValue(String key);

    public Object get(String key) {
        return getValue( getContext()+key );
    }

    public boolean has(String key) {
        return (get(key) != null);
    }

    public Object getAbsolute(String key) {
        return getValue(key);
    }

    public String getString(String key) {
        return (String)get(key);
    }

    public Date getDate(String key) {
        try {
            return java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT).parse(key);
        } catch(java.text.ParseException pe) {
            return null;
        }
    }

    // rely on simple-jndi's type
    public int getInt(String key) {
        return NumberUtils.stringToInt(getString(key));
    }

    public List getList(String key) {
        Object obj = get(key);
        if(!(obj instanceof List)) {
            List list = new ArrayList(1);
            list.add(obj);
            obj = list;
        }
        return (List)obj;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return this.context;
    }

    public Config cloneConfig() {
        try {
            return (Config)this.clone();
        } catch(CloneNotSupportedException cnse) {
            // ignore
            throw new RuntimeException("Cloning of a Config failed. This should be impossible. ");
        }
    }

}
