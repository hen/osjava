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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.generationjava.collections.ClassMap;

import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * Attempts to compare any Object.
 * Some standard types of objects can be dealt with immediately, 
 * but others will need a Comparator being added with the static 
 * registerComparator method.
 * 
 * The following are already handled:
 *
 * Any extension of Comparable. This includes all the Number's and 
 * String, Character amongst others.
 * java.net.URL is handled.
 */
public class ObjectComparator implements Comparator {

    static private Map registry = new ClassMap( new HashMap() );

    static {
        Comparator cc = new ComparableComparator();
        registry.put(java.lang.Comparable.class, cc);

        // Do all these comparables to increase lookup speed
        registry.put(java.lang.String.class, cc);
        registry.put(java.lang.Number.class, cc);
        registry.put(java.io.File.class, cc);
        registry.put(java.lang.Character.class, cc);
        registry.put(java.math.BigDecimal.class, cc);
        registry.put(java.lang.Byte.class, cc);
        registry.put(java.lang.Long.class, cc);
        registry.put(java.lang.Short.class, cc);
        registry.put(java.lang.Float.class, cc);
        registry.put(java.lang.Double.class, cc);
        registry.put(java.math.BigInteger.class, cc);

        registry.put(java.net.URL.class, new UrlComparator());
    }

    /**
     * Register a Comparator to be used to compare a particular Class.
     */
    static public void registerComparator(Class clss, Comparator c) {
        registry.put(clss, c);
    }

    /**
     * Get the Comparator to be used for a particular Class.
     * If no Comparator is available, then it will search the 
     * inheritence tree for one.
     */
    static public Comparator getComparator(Class clss) {

        if(clss == null) {
            return null;
        }

        return (Comparator)registry.get(clss);
    }

    public ObjectComparator() {
    }

    public int compare(Object o1, Object o2) {
        if(o1 == null) {
            return -1;
        } else
        if(o2 == null) {
            return 1;
        }

        if(o1.getClass() != o2.getClass()) {
            return -1;
        }

        Comparator subCom = getComparator(o1.getClass());
        if(subCom == null) {
            return -1;
        } else {
            return subCom.compare(o1, o2);
        }
    }

}
