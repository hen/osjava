/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Genjava-Core nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.generationjava.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * A Map in which multiple keys are used, ie) an Object array 
 * of keys. 
 *
 * @author bayard@generationjava.com
 * @date 2001-12-19
 */
/// Pass in:  { "key1", "key2", "key3" } and a value.
public class MultiKeyedMap implements Map {

    // this is not lazy instantiation. One MUST be 
    // constructed in a constructor.
    private Map myMap = null;

    public MultiKeyedMap(Map m) {
        myMap = m;
    }

    public MultiKeyedMap() {
        this(new HashMap());
    }

    /* map interface */
    
    // Removes all mappings from this map (optional operation) {. 
    public void clear() { 
        myMap.clear();
    }

    // Returns true if this map contains a mapping for the specified key. 
    public boolean containsKey(Object key) { 
        return get(key) != null;
    }

    // Returns true if this map maps one or more keys to the specified value. 
    public boolean containsValue(Object value) { 
        return this.containsValue(this.myMap, value);
    }

    private boolean containsValue(Map map, Object value) {
        Iterator values = map.values().iterator();
        while(values.hasNext()) {
            Object obj = values.next();
            if(obj instanceof Map) {
                if(containsValue((Map)obj, value) ) {
                    return true;
                }
            }
            if(value == obj) {
                return true;
            }
            if(value.equals(obj)) {
                return true;
            }
        }

        return false;
    }

    // Returns a set view of the mappings contained in this map. 
    public Set entrySet() { 
        // FIX  : Make this actually work.
        return myMap.entrySet();
    }

    // Compares the specified object with this map for equality. 
    public boolean equals(Object o) {
        return myMap.equals(o);
    }

    // Returns the value to which this map maps the specified key. 
    public Object get(Object key) { 
        if(key instanceof Object[]) {
            Object[] keys = (Object[])key;
            Map map = this.myMap;
            int szLessOne = keys.length - 1;
            for(int i=0; i<szLessOne; i++) {
                Object value = map.get(keys[i]);
                if(value instanceof Map) {
                    map = (Map)value;
                } else {
                    return null;
                }
            }
            return map.get(keys[szLessOne]);
        } else {
            return this.myMap.get(key);
        }
    }

    // Returns the hash code value for this map. 
    public int hashCode() { 
        return myMap.hashCode();
    }

    // Returns true if this map contains no key-value mappings. 
    public boolean isEmpty() {
        return myMap.isEmpty();
    }

    // Returns a set view of the keys contained in this map. 
    /// NOTE: If fred:foo is added, it will return that key as 
    ///   the array [fred]. Arrays are the correct way to talk 
    ///   to this class.
    public Set keySet() { 
        Set set = new HashSet();
        keySetRecurse(this.myMap, set, new LinkedList());
        return set;
    }

    // SLOW :(
    private void keySetRecurse(Map map, Set set, LinkedList head) {
        Iterator keys = map.keySet().iterator();
        while(keys.hasNext()) {
            Object key = keys.next();
            head.add(key);
            Object value = map.get(key);
            if(value instanceof Map) {
                keySetRecurse((Map)value, set, head);
            } else {
                set.add(head.toArray());
            }
            head.removeLast();
        }
    }

    // Put a value into the specified map (optional operation)
    public Object put(Object key, Object value) {
        if(key instanceof Object[]) {
            Object[] keys = (Object[])key;
            int szLessOne = keys.length-1;
            Map map = this.myMap;
            for(int i=0; i<szLessOne; i++) {
                Object obj = map.get(keys[i]);
                if(obj instanceof Map) {
                    map = (Map)obj;
                } else {
                    Map oldmap = map;
                    map = CollectionsW.cloneNewEmptyMap(map);
                    oldmap.put(keys[i], map);
                }
            }
            return map.put(keys[szLessOne], value);
        } else {
            return this.myMap.put(key, value);
        }
    }

    // Copies all of the mappings from the specified map to this map (optional operation)
    public void putAll(Map t) { 
        if(t != null) {
            Set set = t.keySet();
            if(set != null) {
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    Object key = iterator.next();
                    put(key,t.get(key));
                }
            }
        }
    }

    // Removes the mapping for this key from this map if present (optional operation) 
    public Object remove(Object key) { 
        if(key instanceof Object[]) {
            Object[] keys = (Object[])key;
            int szLessOne = keys.length-1;
            Map map = this.myMap;
            for(int i=0; i<szLessOne; i++) {
                Object obj = map.get(keys[i]);
                if(obj instanceof Map) {
                    map = (Map)obj;
                } else {
                    return null;
                }
            }
            return map.remove(keys[szLessOne]);
        } else {
            return this.myMap.remove(key);
        }
    }

    // Returns the number of key-value mappings in this map. 
    public int size() { 
        // QUERY: This is okay I think. Should it return size of flattened???
        return values().size();
    }

    // Returns a collection view of the values contained in this map. 
    public Collection values() { 
        Collection list = new LinkedList();
        valuesRecurse( this.myMap, list );
        return list;
    }

    private void valuesRecurse(Map map, Collection list) { 
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            if(value instanceof Map) {
                valuesRecurse((Map)value, list);
            } else {
                list.add(value);
            }
        }
    }

    public String toString() {
        return myMap.toString();
    }

}
