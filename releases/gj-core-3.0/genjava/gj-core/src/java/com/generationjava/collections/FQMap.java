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
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

// @date    2000-05-13

// There is a potential bug, but it is rare, and a fix is 
// scheduled. Also there are many opportunities for optimisation.
/**
 * A map which nests maps depending on a separator in the key.
 * The default case is to use the '.' character. 
 */
public class FQMap implements Map {

    private Map  myMap = null;
    private char myChar;

    // unused atm. Use this to optimise for situations when 
    // FQ's are not being used. Basically count FQ's out and in,
    // if possible.
    private int numberFQMapValues = 0;

    // create a FQ map built on top of this type of map.
    public FQMap(Map m, char separator) {
        m.clear();
        myMap  = m;
        myChar = separator;
    }

    // create a FQ map built on top of a HashMap, and 
    // separated by '.'
    public FQMap() {
        this(new HashMap(),'.');
    }

    public FQMap(Map m) {
        this(m,'.');
    }
    public FQMap(char c) {
        this(new HashMap(),c);
    }

    public String toString() {
        return "{"+myMap+"}";
    }

    public Map createEmptyMap() {
        // TODO: make a new version of myMap

        return new FQMap(myChar);
    }

    public char getSeperationChar() {
        return myChar;
    }

    /**
     * returns an array of size 2. First element is a 
     * Collection of the values that are FQMaps, second 
     * element is the values that aren`t FQMaps.
     */
    public Collection[] getSeparatedValues() {
        Collection col = myMap.values();
        if(col == null) {
            return null;
        }
        ArrayList fqmaps    = null;
        ArrayList nonfqmaps = null;

        Iterator it = col.iterator();
        while(it.hasNext()) {
            Object ob = it.next();
            if(ob instanceof FQMap) {
                if(fqmaps == null) {
                    fqmaps = new ArrayList();
                }
                fqmaps.add(ob);
            } else {
                if(nonfqmaps == null) {
                    nonfqmaps = new ArrayList();
                }
                nonfqmaps.add(ob);
            }
        }
        Collection[] retVal = new Collection[2];
        retVal[0] = fqmaps;
        retVal[1] = nonfqmaps;
        return retVal;
    }

    /* map interface */

    //  Removes all mappings from this map (optional operation). 
    public void clear() {
        myMap.clear();
    }

    //  Returns true if this map contains a mapping for the specified key. 
    public boolean containsKey(Object key) {
        return (get(key) == null);
    }

    //  Returns true if this map maps one or more keys to the specified value. 
    public boolean containsValue(Object value) {
        return (values().contains(value));
    }

    //  Returns a set view of the mappings contained in this map. 
    public Set entrySet() {
        Set keys = keySet();
        if(keys == null) {
            return null;
        }

        HashSet retSet = new HashSet();

        Iterator it = keys.iterator();
        while(it.hasNext()) {
            Object key = it.next();
            retSet.add(new FQEntry(this,key));
        }

        return retSet;
    }

    //  Compares the specified object with this map for equality. 
    public boolean equals(Object o) {
        return myMap.equals(o);    // FIX:?
    }

    //  Returns the value to which this map maps the specified key. 
    public Object get(Object key) {
        if(key instanceof String) { 
            String keyStr = (String)key;
            int idx = keyStr.indexOf(myChar);
            if(idx == -1 || idx == keyStr.length() - 1) {
                // it's top level.
                return myMap.get(key);
            } else {
                String first = keyStr.substring(0,idx);
                Object ob = myMap.get(first);
                Map subMap = null;
                if(ob != null) {
                    if(ob instanceof Map) {
                            subMap = (Map)ob;
                        return subMap.get(keyStr.substring(idx+1));
                    } else {
                        return null;
                    }                
                } else {
                    return null;
                }
            }
        } else {  // handle non-string keys
            return myMap.get(key);
        }
    }

    //  Returns the hash code value for this map. 
    public int hashCode() {
        return myMap.hashCode();    // FIX:?
    }

    //  Returns true if this map contains no key-value mappings. 
    public boolean isEmpty() {
        return myMap.isEmpty();
    }

    /**
     *  Returns a set view of the keys contained in this map. 
     * BUG: If a FQMap is intentionally added to an FQMap, rather 
     * than created through a fully qualified key, such that 
     * put(someObj,FQMap) is effectively done, and then this top
     * level FQMap is added to another, either intentionally or 
     * under the fully qualified scheme, then the keySet will
     * contain the stringied version of someObj, which means 
     * that a request with this key will _not_ work, unless
     * someObj's hashCode() and equals() are such that someObj
     * equals the stringified version of someObj.
     *
     * To this end, a fix is to wrap all keys coming back out in 
     * a 'FQKey' object which contains an ArrayList of all subkeys.
     * Code would need changing so a value may be obtained using 
     * this FQKey.
     *
     * Also to note, the stringified form of an object is fully
     * qualified, using '.' as the package/class seperator.
     * ie) the default behaviour of this class.
     */
    public Set keySet() {
        Set keys = myMap.keySet();
        if(keys == null) {
            return null;
        }

        HashSet retSet = new HashSet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
            Object key = it.next();
            Object value = myMap.get(key);
            if(value instanceof FQMap) {
                Set subkeys = ((FQMap)value).keySet();
                if(subkeys != null) {
                    Iterator subIt = subkeys.iterator();
                    while(subIt.hasNext()) {
                        retSet.add(""+key+myChar+subIt.next());
                    }
                }
            } else {
                retSet.add(key);
            }
        }
        return retSet;
    }

    //  Associates the specified value with the specified key in this map (optional operation). 
    public Object put(Object key, Object value) {
        if(key instanceof String) { 
            String keyStr = (String)key;
            int idx = keyStr.indexOf(myChar);
            if(idx == -1 || idx == keyStr.length() - 1) {
                // it's top level.
                return myMap.put(key,value);
            } else {
                String first = keyStr.substring(0,idx);
                Object ob = myMap.get(first);
                Map subMap = null;
                if(ob != null) {
                    if(ob instanceof Map) {
                            subMap = (Map)ob;
                        return subMap.put(keyStr.substring(idx+1),value);
                    } else {
                        subMap = createEmptyMap();
                        subMap.put(keyStr.substring(idx+1),value);
                        myMap.put(first,subMap);
                        return ob;
                    }                
                } else {
                    subMap = createEmptyMap();
                    subMap.put(keyStr.substring(idx+1),value);
                    myMap.put(first,subMap);
                    return ob;
                }
            }
        } else {  // handle non-string keys
            return myMap.put(key,value);
        }
    }

    //  Copies all of the mappings from the specified map to this map (optional operation). 
    public void putAll(Map t) {
        if(t == null) {
            return;
        }

        Set keys = t.keySet();

        if(keys == null) {
            return;
        }

        Iterator it = keys.iterator();
        while(it.hasNext()) {
            Object ob = it.next();
            put(ob,t.get(ob));
        }
    }

    //  Removes the mapping for this key from this map if present (optional operation). 
    /**
     * Unimplemented.
     */
    public Object remove(Object key) {
        return null;    // FIX:
    }

    //  Returns the number of key-value mappings in this map. 
    public int size() {
        if(myMap.size() == 0) {
            return 0;
        }
        
        Collection[] values = getSeparatedValues();
        
        int ret_int = 0;
        if(values[0] != null) {
            Iterator it = values[0].iterator();
            while(it.hasNext()) {
                FQMap map = (FQMap)it.next();
                ret_int += map.size();
            }
        }
        if(values[1] != null) {
            ret_int += values[1].size();
        }
        return ret_int;
    }

    //  Returns a collection view of the values contained in this map. 
    public Collection values() {
        Collection[] values = getSeparatedValues();
        if(values == null) {
            return null;
        }
        ArrayList retList = null;
        if(values[0] != null) {
            if(retList == null) {
                retList = new ArrayList();
            }
            Iterator it = values[0].iterator();
            while(it.hasNext()) {
                FQMap map = (FQMap)it.next();
                retList.addAll(map.values());
            }
        }
        if(values[1] != null) {
            if(retList == null) {
                retList = new ArrayList();
            }
            retList.addAll(values[1]);
        }
        return retList;
    }

}
// assumes u can`t have null keys.
class FQEntry implements Map.Entry {

    private Map    myMap = null;
    private Object myKey = null;

    public FQEntry(Map map, Object key) {
        myMap = map;
        myKey = key;
    }
 
    //  Compares the specified object with this entry for equality. 
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if( o instanceof FQEntry ) {
            FQEntry fqe    = (FQEntry)o;
            Object  key    = getKey();
            Object  okey   = fqe.getKey();
            Object  value  = getValue();
            Object  ovalue = fqe.getValue();

            if(okey == null && key == null) {
                // null key assumption. doesn`t check value.
                return true;
            }

            if(getKey().equals(fqe.getKey())) {
                if(ovalue == null && value == null) {
                    return true;
                }
                if(getValue().equals(fqe.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    //  Returns the key corresponding to this entry. 
    public Object getKey() {
        return myKey;
    }

    //  Returns the value corresponding to this entry. 
    public Object getValue() {
        if(myMap != null) {
            return myMap.get(myKey);
        } else {
            return null;
        }
    }

    //  Returns the hash code value for this map entry. 
    public int hashCode() {
        int n = 0;
        
        if(myKey != null) {
            n += myKey.hashCode();
        }
        Object ob = getValue();
        if(ob != null) {
            n &= ob.hashCode();
        }
        return n;
    }

    //  Replaces the value corresponding to this entry with the specified value (optional operation). 
    public Object setValue(Object value) {
        if(myMap != null) {
            return myMap.put(getKey(),value);
        } else {
            return null;  // WHAT TO DO??
        }
    }

    public String toString() {
        return ""+getKey()+":"+getValue();
    }

}
