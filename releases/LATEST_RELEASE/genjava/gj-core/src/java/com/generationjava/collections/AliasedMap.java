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

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A Map in which some key's values may point to another key.
 * A process coined as 'aliasing'.
 *
 * @date 2000-05-14
 */
public class AliasedMap implements Map {

    // this is not lazy instantiation. One MUST be 
    // constructed in a constructor.
    private Map myMap = null;

    public AliasedMap(Map m) {
        myMap = m;
    }

    public AliasedMap() {
        this(new HashMap());
    }

    /**
     * Alias an existing key with an aliased key. The existing key may 
     * be an alias.
     * 
     * @param aliaskey Object new alias
     * @param existingkey Object existing key
     */
    public void alias(Object aliaskey,Object existingkey) {
        // enforce existing key?
        Alias alias = new SimpleAlias(existingkey);
        myMap.put(aliaskey,alias);
    }

    /**
     * Get all aliases. 
     */
    public Collection aliases() {
        Collection values = myMap.values();
        if(values == null) {
            return null;
        }

        LinkedList ret = new LinkedList();

        Iterator it = values.iterator();
        while(it.hasNext()) {
            Object obj = it.next();
            if(obj instanceof Alias) {
                ret.add(obj);
            }
        }

        return ret;
    }

/*
    /**
     * Creates a new AliasedMap with the existing aliases linked to their 
     * true values.
     *  /
    public AliasedMap newMapWithAliases() {
        return (AliasedMap)fillMapWithAliases(new AliasedMap());
    }
    /**
     * Fills a Map with the existing aliases linked to their 
     * true values.
     *  /
    public Map fillMapWithAliases(Map ret) {
        Collection aliases = aliases();
        if(aliases != null) {
            Iterator it = aliases.iterator();
            while(it.hasNext()) {
                Alias alias = (Alias)it.next();
                ret.put(alias,alias.getAlias());
            }
        }
        return ret;
    }
*/
    

    /* map interface */

    /**
     * Removes all mappings from this map.
     */
    public void clear() { 
        myMap.clear();
    }

    /**
     * Returns true if this map contains a mapping for the specified key. 
     */
    public boolean containsKey(Object key) { 
        return myMap.containsKey(key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value. 
     */
    public boolean containsValue(Object value) { 
        // QUERY: What if the value is an alias? Do we deny aliasing?
        return myMap.containsValue(value);
    }

    /**
     * Returns a set view of the mappings contained in this map. 
     */
    public Set entrySet() { 
        // FIX  : Make this actually work.
        return myMap.entrySet();
    }

    /**
     * Compares the specified object with this map for equality. 
     */
    public boolean equals(Object o) {
        // QUERY:  Is this right?
        return myMap.equals(o);
    }

    /**
     * Returns the value to which this map maps the specified key. 
     */
    public Object get(Object key) { 
        Object ob = myMap.get(key);
        if(ob instanceof Alias) {
            return get( ((Alias)ob).getAlias() );
        } else {
            return ob;
        }
    }

    /**
     * Returns the hash code value for this map. 
     */
    public int hashCode() { 
        // QUERY:  Is this right?
        return myMap.hashCode();
    }

    /**
     * Returns true if this map contains no key-value mappings. 
     */
    public boolean isEmpty() {
        // TODO : Make it ignore aliases.
        return myMap.isEmpty();
    }

    /**
     * Returns a set view of the keys contained in this map. 
     */
    public Set keySet() { 
        return myMap.keySet();
    }

    /**
     * Associates the specified value with the specified key in this map . 
     */
    public Object put(Object key, Object value) { 
        // TODO : Make it return the value aliased, rather than the alias.
        return myMap.put(key,value);
    }

    /**
     * Copies all of the mappings from the specified map to this map. 
     */
    public void putAll(Map t) { 
        myMap.putAll(t);
    }

    /**
     * Removes the mapping for this key from this map if present. 
     */
    public Object remove(Object key) { 
        // QUERY: Should it return value aliased, as it is doing now?
        Object ob = get(key);
        myMap.remove(key);
        return ob;
    }

    /**
     * Returns the number of key-value mappings in this map. 
     */
    public int size() { 
        // QUERY: This is okay I think.
        return myMap.size();
    }

    /**
     * Returns a collection view of the values contained in this map. 
     */
    public Collection values() { 
        // TODO : Remove aliases.
        return myMap.values();
    }

}
