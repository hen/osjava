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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.generationjava.beans.AbstractBeanViewer;
import com.generationjava.beans.BeanViewRuntime;

/**
 * A Map which wraps a JavaBean.
 *
 * @date 2001-05-11
 */
public class BeanMap extends AbstractBeanViewer implements Map {

    private Object myBean = null;
    private Map pdCache = new HashMap();

    /**
     * Construct a BeanMap around the supplied Java Bean.
     */
    public BeanMap(Object bean) {
        myBean = bean;
        try {
            BeanInfo info = Introspector.getBeanInfo( myBean.getClass() );
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for(int i=0; i<pds.length; i++) {
                pdCache.put( pds[i].getName(), pds );
            }
        } catch(IntrospectionException ie) {
        }
    }

    /**
     * Get the PropertyDescriptor for a particular property name.
     */
    public PropertyDescriptor getPropertyDescriptor(Object key) {
        return (PropertyDescriptor)pdCache.get( key.toString() );
    }

    /* map interface */

    // Removes all mappings from this map (optional operation) {. 
    public void clear() {
    }

    // Returns true if this map contains a mapping for the specified key. 
    public boolean containsKey(Object key) { 
        return pdCache.containsKey(key);
    }

    // Returns true if this map maps one or more keys to the specified value. 
    // TODO: Implement this
    public boolean containsValue(Object value) { 
        return false;
    }

    // Returns a set view of the mappings contained in this map. 
    // TODO: Implement this    
    public Set entrySet() { 
        return null;
    }

    // Compares the specified object with this map for equality. 
    public boolean equals(Object o) {
        return myBean.equals(o);
    }

    // Returns the value to which this map maps the specified key. 
/*
    public Object get(Object key) { 
        PropertyDescriptor desc = getPropertyDescriptor(key);
        Method get = desc.getReadMethod();
        if(get == null) {
            return null;
        }
        Object[] objs = new Object[0];
        try {
            return get.invoke( myBean, objs );
        
        } catch(IllegalAccessException iae) {
            return null;
        } catch(IllegalArgumentException iae) {
            return null;
        } catch(InvocationTargetException ite) {
            return null;
        }
    }
*/

    // Returns the hash code value for this map. 
    public int hashCode() { 
        return myBean.hashCode();
    }

    // Returns true if this map contains no key-value mappings. 
    // QUERY: Is a map empty if it contains blah:null pairings?
    public boolean isEmpty() {
        return pdCache.isEmpty();
    }

    // Returns a set view of the keys contained in this map. 
    public Set keySet() { 
        return pdCache.keySet();
    }

    // Associates the specified value with the specified key in this map (optional operation) {. 
    // TODO: Implement this
    /**
     * Unimplemented.
     */
    public Object put(Object key, Object value) { 
        Object oldValue = get(key);
        PropertyDescriptor desc = getPropertyDescriptor(key);
        Method set = desc.getWriteMethod();
        if(set == null) {
            return oldValue;  // ??
        }
        Object[] objs = new Object[1];
        objs[0] = value;
        try {
            return set.invoke( myBean, objs );        
        } catch(IllegalAccessException iae) {
            return oldValue;
        } catch(IllegalArgumentException iae) {
            return oldValue;
        } catch(InvocationTargetException ite) {
            return oldValue;
        }
    }

    // Copies all of the mappings from the specified map to this map (optional operation) {. 
    public void putAll(Map t) { 
        Iterator keys = t.keySet().iterator();
        while(keys.hasNext()) {
            Object key = keys.next();
            put(key, t.get(key));
        }
    }

    // Removes the mapping for this key from this map if present (optional operation) {. 
    // TODO: Implement this
    /**
     * Unimplemented.
     */
    public Object remove(Object key) { 
        return null;
    }

    // Returns the number of key-value mappings in this map. 
    public int size() { 
        return pdCache.size();
    }

    // Returns a collection view of the values contained in this map. 
    public Collection values() { 
        Set set = keySet();
        LinkedList values = new LinkedList();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            values.add( get( iterator.next() ) );
        }
        return values;
    }

    public Object get(Object key) {
        return get(key, this.myBean);
/*        if( key == null ) {
            return null;
        }

        if(myBean == null) {
            return null;
        }

        String name = key.toString();

        int idx = 0;
        Object obj = myBean;
        while( (idx = name.indexOf('.', idx)) != -1) {
            String pre = name.substring(0, idx);
            name = name.substring(idx+1);
            if(obj != null) {
                obj = handleGet(obj, pre);
            } else {
                return null;
            }
        }

        return handleGet(obj, name);
        */
    }
/*
    public Object handleGet(Object bean, String name) {

        // deal with array
        Object index = null;
        int i = name.indexOf('[');
        String pre = null;
//        boolean arrayLike = false;
        if(i != -1) {
            pre = name.substring(i+1);
            if(pre.endsWith("]")) {
                pre = pre.substring(0, pre.length() -1);
            }
            try {
                index = Integer.valueOf(pre);
//                arrayLike = true;
            } catch(NumberFormatException nfe) {
                index = pre;
            }
            name = name.substring(0,i);
        }

        // capitalise
        name = name.substring(0,1).toUpperCase();
        if(name.length() > 1) {
            name += name.substring(1);
        }

/*
        methodName = "get"+methodName;

        Method getMethod = null;
        Class[] clss0 = new Class[0];
        Class[] clss = null;
        try {
            if(idx != -1) {
                clss = new Class[1];
                clss[0] = Integer.TYPE;
            } else {
                clss = clss0;
            }
            getMethod = bean.getClass().getMethod(methodName, clss);
        } catch(SecurityException se) {
        } catch(NoSuchMethodException nsme) {
            try {
                if(clss != clss0) {
                    getMethod = bean.getClass().getMethod(methodName, clss0);
                }
                arrayLike = false;
            } catch(SecurityException se2) {
            } catch(NoSuchMethodException nsme2) {
            }
        }

        if(getMethod == null) {
            return null;
        }
        try {
            Object[] param = null;
            if(arrayLike) {
                param = new Object[1];
                param[0] = new Integer(idx);
            } else {
                param = new Object[0];
            }
            Object obj = getMethod.invoke( bean, param );
*       /
            Object obj = invokeGet( bean, name, index );
            obj = CollectionUtils.index(obj, index);
            return obj;
            /*
            if( (obj instanceof Map) && (pre != null) ) {
                return ((Map)obj).get(pre);
            } else
            if( (obj instanceof List) && (idx != -1) ) {
                return ((List)obj).get(idx);
            } else
            if( (obj instanceof Object[]) && (idx != -1) ) {
                return ((Object[])obj)[idx];
            } else {
                return obj;
            }
            */
/*
        } catch(IllegalAccessException iae) {
            return null;
        } catch(IllegalArgumentException iae) {
            return null;
        } catch(InvocationTargetException ite) {
            return null;
        }
*        /
    }
*/
    /**
     * Returns bean.get<methodName>[idx]
     */
    public Object invokeGet(BeanViewRuntime runtime, Object bean, String methodName, Object index) {
        int idx = -1;
        boolean arrayLike = (index instanceof Integer);
        if(arrayLike) {
            idx = ((Integer)index).intValue();
        }
        methodName = "get"+methodName;

        Method getMethod = null;
        Class[] clss0 = new Class[0];
        Class[] clss = null;
        try {
            if(arrayLike) {
                clss = new Class[1];
                clss[0] = Integer.TYPE;
            } else {
                clss = clss0;
            }
            getMethod = bean.getClass().getMethod(methodName, clss);
        } catch(SecurityException se) {
        } catch(NoSuchMethodException nsme) {
            try {
                if(clss != clss0) {
                    getMethod = bean.getClass().getMethod(methodName, clss0);
                }
                arrayLike = false;
            } catch(SecurityException se2) {
            } catch(NoSuchMethodException nsme2) {
            }
        }

        if(getMethod == null) {
            return null;
        }
        try {
            Object[] param = null;
            if(arrayLike) {
                param = new Object[1];
                param[0] = new Integer(idx);
            } else {
                param = new Object[0];
            }
            Object obj = getMethod.invoke( bean, param );
            obj = CollectionUtils.index(obj, idx);

            return obj;
//            if( (obj instanceof Map) && (pre != null) ) {
//                return ((Map)obj).get(pre);
//            } else
//            if( (obj instanceof List) && (idx != -1) ) {
//                return ((List)obj).get(idx);
//            } else
//            if( (obj instanceof Object[]) && (idx != -1) ) {
//                return ((Object[])obj)[idx];
//            } else {
//                return obj;
//            }
        } catch(IllegalAccessException iae) {
            return null;
        } catch(IllegalArgumentException iae) {
            return null;
        } catch(InvocationTargetException ite) {
            return null;
        }
    }

    public void invokeSet(BeanViewRuntime runtime, Object bean, String name, Object idx, Object value) {
    }
}
