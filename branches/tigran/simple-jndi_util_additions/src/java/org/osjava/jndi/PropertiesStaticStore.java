package org.osjava.jndi;

import java.util.Hashtable;

class PropertiesStaticStore extends Hashtable {

    static private Hashtable SELF = new Hashtable();

    public synchronized int size() {
        return SELF.size();
    }

    public synchronized boolean isEmpty() {
        return SELF.isEmpty();
    }

    public synchronized java.util.Enumeration keys() {
        return SELF.keys();
    }

    public synchronized java.util.Enumeration elements() {
        return SELF.elements();
    }

    public synchronized boolean contains(java.lang.Object obj) {
        return SELF.contains(obj);
    }

    public boolean containsValue(java.lang.Object obj) {
        return SELF.containsValue(obj);
    }

    public synchronized boolean containsKey(java.lang.Object obj) {
        return SELF.containsKey(obj);
    }

    public synchronized java.lang.Object get(java.lang.Object obj) {
        return SELF.get(obj);
    }

    public synchronized java.lang.Object put(java.lang.Object key, java.lang.Object value) {
        return SELF.put(key, value);
    }

    public synchronized java.lang.Object remove(java.lang.Object obj) {
        return SELF.remove(obj);
    }

    public synchronized void putAll(java.util.Map map) {
        SELF.putAll(map);
    }

    public synchronized void clear() {
        SELF.clear();
    }

//    public synchronized java.lang.Object clone()

    public synchronized java.lang.String toString() {
        return SELF.toString();
    }

    public java.util.Set keySet() {
        return SELF.keySet();
    }

    public java.util.Set entrySet() {
        return SELF.entrySet();
    }

    public java.util.Collection values() {
        return SELF.values();
    }

    public synchronized boolean equals(java.lang.Object obj) {
        return SELF.equals(obj);
    }

    public synchronized int hashCode() {
        return SELF.hashCode();
    }


}
