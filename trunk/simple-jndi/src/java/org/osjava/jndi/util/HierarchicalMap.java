package org.osjava.jndi.util;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

//import org.osjava.convert.Convert;

// @date    2000-05-13

// There is a potential bug, but it is rare, and a fix is 
// scheduled. Also there are many opportunities for optimisation.
// Was once known as FQMap in GenJava util library
/**
 * A map which nests maps depending on a separator in the key.
 * The default case is to use the '.' character. 
 * It is based on the FQMap in the gj-core library, but has 
 * been adapted for simple-jndi's hierarchy requirements.
 */
public class HierarchicalMap implements Map {

    private Map  nodeMap = null;
    private String delimiter;

    public HierarchicalMap(String separator) {
        this.nodeMap  = new LinkedHashMap();
        this.delimiter = separator;
    }

    public String toString() {
        return "{"+nodeMap+"}";
    }

    public Map createEmptyMap() {
        return new HierarchicalMap(delimiter);
    }

    public String getSeparationDelimiter() {
        return delimiter;
    }

    /**
     * returns an array of size 2. First element is a 
     * Collection of the values that are HierarchicalMaps, second 
     * element is the values that aren`t HierarchicalMaps.
     */
    public Collection[] getSeparatedValues() {
        Collection col = nodeMap.values();
        if(col == null) {
            return null;
        }
        ArrayList fqmaps    = null;
        ArrayList nonfqmaps = null;

        Iterator it = col.iterator();
        while(it.hasNext()) {
            Object ob = it.next();
            if(ob instanceof HierarchicalMap) {
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
        nodeMap.clear();
    }

    //  Returns true if this map contains a mapping for the specified key. 
    public boolean containsKey(Object key) {
        return (get(key) != null);
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
        if(o instanceof HierarchicalMap) {
            return nodeMap.equals( ((HierarchicalMap)o).nodeMap );
        } else {
            return false;
        }
    }

    //  Returns the value to which this map maps the specified key. 
    public Object get(Object key) {
        Object obj = null;

        if(key instanceof String) { 
            String keyStr = (String)key;
            int idx = keyStr.indexOf(delimiter);
            if(idx == -1 || idx == keyStr.length() - 1) {
                // it's top level.
                obj = nodeMap.get(key);
            } else {
                String first = keyStr.substring(0,idx);
                Object ob = nodeMap.get(first);
                Map subMap = null;
                if(ob != null) {
                    if(ob instanceof Map) {
                            subMap = (Map)ob;
                        obj = subMap.get(keyStr.substring(idx+1));
                    } else {
                        return null;
                    }                
                } else {
                    return null;
                }
            }
        } else {  // handle non-string keys
            obj = nodeMap.get(key);
        }

        if(obj instanceof List) {
            List list = (List) obj;
            int sz = list.size();
            String type = null;

            // find type
            for(int i=0; i<sz; i++) {
                Object value = list.get(i);
                if(value instanceof HierarchicalMap) {
                    // handle converting
                    HierarchicalMap hmap = (HierarchicalMap) value;
                    if(hmap.containsKey("type")) {
                        type = (String) hmap.get("type");
                        break;
                    }
                }
            }
            if(type == null) {
                return list;
            }

            List cloneList = new LinkedList();
            for(int i=0; i<sz; i++) {
                Object value = list.get(i);
                if( !(value instanceof HierarchicalMap) ) {
//                    cloneList.add(Convert.convert((String)value, type));
                     cloneList.add(value);
                }
            }
            return cloneList;
        } else {
            return obj;
        }
    }

    //  Returns the hash code value for this map. 
    public int hashCode() {
        return nodeMap.hashCode();    // FIX:?
    }

    //  Returns true if this map contains no key-value mappings. 
    public boolean isEmpty() {
        return nodeMap.isEmpty();
    }

    /**
     *  Returns a set view of the keys contained in this map. 
     * BUG: If a HierarchicalMap is intentionally added to an HierarchicalMap, rather 
     * than created through a fully qualified key, such that 
     * put(someObj,HierarchicalMap) is effectively done, and then this top
     * level HierarchicalMap is added to another, either intentionally or 
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
        Set keys = nodeMap.keySet();
        if(keys == null) {
            return null;
        }

        HashSet retSet = new HashSet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
            Object key = it.next();
            Object value = nodeMap.get(key);
            if(value instanceof HierarchicalMap) {
                Set subkeys = ((HierarchicalMap)value).keySet();
                if(subkeys != null) {
                    Iterator subIt = subkeys.iterator();
                    while(subIt.hasNext()) {
                        retSet.add(""+key+delimiter+subIt.next());
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

        // values should be Lists if many are added, rather 
        // than overriding
        if(nodeMap.containsKey(key)) {
            Object obj = nodeMap.get(key);
            if( !(obj instanceof List)) {
                List list = new LinkedList();
                list.add(obj);
                obj = list;
            } 
            ((List)obj).add(value);
            value = obj;
        }
if(org.osjava.jndi.PropertiesContext.DEBUG)        System.err.println("[HMAP]Setting: "+key+"="+value);

        if(key instanceof String) { 
            String keyStr = (String)key;
            int idx = keyStr.indexOf(delimiter);
            if(idx == -1 || idx == keyStr.length() - 1) {
                // it's top level.
                return nodeMap.put(key,value);
            } else {
                String first = keyStr.substring(0,idx);
                Object ob = nodeMap.get(first);
                if(ob != null && ob instanceof Map) {
                    return ((Map)ob).put(keyStr.substring(idx+1),value);
                } else {
                    Map subMap = createEmptyMap();
                    subMap.put(keyStr.substring(idx+1),value);
                    nodeMap.put(first,subMap);
                    return ob;
                }                
            }
        } else {  // handle non-string keys
            return nodeMap.put(key,value);
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
        if(nodeMap.size() == 0) {
            return 0;
        }
        
        Collection[] values = getSeparatedValues();
        
        int ret_int = 0;
        if(values[0] != null) {
            Iterator it = values[0].iterator();
            while(it.hasNext()) {
                HierarchicalMap map = (HierarchicalMap)it.next();
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
                HierarchicalMap map = (HierarchicalMap)it.next();
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

    private Map    nodeMap = null;
    private Object myKey = null;

    public FQEntry(Map map, Object key) {
        nodeMap = map;
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
        if(nodeMap != null) {
            return nodeMap.get(myKey);
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
        if(nodeMap != null) {
            return nodeMap.put(getKey(),value);
        } else {
            return null;  // WHAT TO DO??
        }
    }

    public String toString() {
        return ""+getKey()+":"+getValue();
    }

}
