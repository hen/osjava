package org.osjava.scraping;

import java.util.List;
import com.generationjava.namespace.Namespace;
import com.generationjava.namespace.SimpleNamespace;

public class NamespaceSession extends AbstractConfig implements Session {

    private Namespace namespace;

    public NamespaceSession() {
        this.namespace = new SimpleNamespace();
    }

    protected Object getValue(String key) {
        return this.namespace.getValue(key);
    }

    public void put(String key, Object value) {
        this.namespace.putValue(key, value);
    }

    // TODO: Move to AbstractSession
    public Object remove(String key) {
        Object obj = get(key);
        this.namespace.putValue(key, null);
        return obj;
    }

}
