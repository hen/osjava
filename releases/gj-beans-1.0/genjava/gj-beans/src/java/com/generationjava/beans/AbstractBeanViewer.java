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
package com.generationjava.beans;

import java.util.List;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;

/**
 * Helps us deal with such wonderful things as array notation 
 * and object notation. Array notation may use associative array notation.
 *
 * @date 2001-06-14
 */
abstract public class AbstractBeanViewer {

    /**
     * Get the property of myBean with the name of the key's toString.
     *
     * The key may use dotted notation to imply a property in a property 
     * and array notation to get the nth property in a list of propertys.
     */
    public Object get(Object key, Object myBean) {
        if( key == null ) {
            return null;
        }

        if(myBean == null) {
            return null;
        }

        String name = key.toString();

        BeanViewRuntime runtime = new BeanViewRuntime();
        runtime.pushObject(myBean);

        int idx = 0;
        Object obj = myBean;
        while( (idx = name.indexOf('.', idx)) != -1) {
            String pre = name.substring(0, idx);
            name = name.substring(idx+1);
            if(obj != null) {
                obj = handleGet(runtime, obj, pre);
                runtime.pushObject(obj);
            } else {
                return null;
            }
        }

        return handleGet(runtime, obj, name);
    }

    /**
     * Give a bean, and a String name, get the property with that name 
     * from the bean.
     * Does not handle dot notation, but does handle array notation 
     * and associative array notation.
     */
    public Object handleGet(BeanViewRuntime runtime, Object bean, String name) {
        // deal with array
        int i = name.indexOf('[');
        Object index = null;
        if(i != -1) {
            String pre = name.substring(i+1);
            if(pre.endsWith("]")) {
                pre = pre.substring(0, pre.length() -1);
            }
            try {
                index = Integer.valueOf(pre);
            } catch(NumberFormatException nfe) {
                index = pre;
            }
            name = name.substring(0,i);
        }

        // shouldn't be null
        if(name.equals("")) {
            return CollectionUtils.index(bean, index);
        } else {
            return invokeGet( runtime, bean, name, index );
        }
    }

    /**
     * Get a value from a bean with the specified name.
     * If the bean is indexed, an int index is passed. Else it is -1.
     * It is recommmended that implementors of this method use the 
     * index method to handle the array notation.
     */
    abstract public Object invokeGet(BeanViewRuntime runtime, Object bean, String name, Object idx);

    public Object set(Object key, Object bean, Object value) {
        if(key == null) {
            return null;
        }

        if(bean == null) {
            return null;
        }

        String name = key.toString();

        int idx = name.lastIndexOf(".");
        if(idx != -1) {
            String pre = name.substring(0, idx);
            String post = name.substring(idx+1);
            Object parent = get(pre, bean);
// TODO: remove [] notation from name.
// equally, need to pass idx as an Object, so implies that 
// invokeGet shuld have Object idx and so should index.
            bean = parent;
            name = post;
        }
        // call invokeGet to get the current value, then 
        // call invokeSet
        invokeSet(null, bean, name, null, value);

        // return old value
        return null;
    }

    abstract public void invokeSet(BeanViewRuntime runtime, Object bean, String name, Object idx, Object value);

}
