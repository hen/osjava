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
package com.generationjava.compare;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import com.generationjava.beans.ReflectionBeanViewer;

/**
 * A Comparator for JavaBeans. Provide the name of the property to 
 * be compared with, and BeanComparator will deal with finding the 
 * property and comparing on it using ObjectComparator.
 * 
 * Some examples are: 
 * "person" -> getPerson()
 * "person.name" -> getPerson().getName()
 * "person.addressMap.fred" -> getPerson().getAddressMap().get("fred")
 * "person.phone[3]" -> getPerson().getPhone()[3] and 
 *                      getPerson().getPhone().get(3) (array and List)
 */
public class BeanComparator implements Comparator {

    static private Map beanComparatorRegistry = new HashMap();

    /**
     * Get a BeanComparator for the specified property.
     */
    static public BeanComparator getInstance(String attrib) {
        Object obj = beanComparatorRegistry.get(attrib);
        if(obj == null) {
            obj = new BeanComparator(attrib);
            beanComparatorRegistry.put(attrib, obj);
        }
        return (BeanComparator)obj;
    }

    private String attribute;
    private HashMap cache;
    private ReflectionBeanViewer bv = new ReflectionBeanViewer();
    private Comparator oc = new ObjectComparator();

    public BeanComparator(String attrib) {
        this.attribute = attrib;
    }

    public void setComparator(Comparator comp) {
        this.oc = comp;
    }

    /**
     * Do a Schwartzian transform precompile of all the values.
     * Pattern from Perl.
     */
    public void precompile(Collection col) {
        caching(true);
        Iterator iterator = col.iterator();
        while(iterator.hasNext()) {
            Object bean = iterator.next();
            Object ret = bv.get(this.attribute, bean);
            cache.put(bean, ret);
        }
    }

    /**
     * Turn on caching. Akin to Perl's Orcish maneuver.
     * Recalling this method will reset caching.
     */
    public boolean caching(boolean b) {
        boolean b2 = caching();
        this.cache = b?new HashMap():null;
        return b2;
    }

    /**
     * Is caching on?
     */
    public boolean caching() {
        return (this.cache != null);
    }

    public int compare(Object o1, Object o2) {
        if(o1 == null) {
            return 1;
        } else
        if(o2 == null) {
            return -1;
        }
        
        Object ret1 = null;
        Object ret2 = null;
        if(caching()) {
            ret1 = cache.get(o1);
            ret2 = cache.get(o2);
        }
        if(ret1 == null) {
            ret1 = bv.get(this.attribute, o1);
        }
        if(ret2 == null) {
            ret2 = bv.get(this.attribute, o2);
        }

        return oc.compare(ret1, ret2);
    }
}

