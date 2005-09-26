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

import java.util.Comparator;
import java.util.Hashtable;

/**
 * If the Objects to be compared are known before hand, then this allows 
 * the ordering to be handled via the construction of an Object[].
 */
public class GroupedComparator implements Comparator {

    private Hashtable index = new Hashtable();

    /**
     * Provide an array of Objects that will be compared.
     * The order the Objects are in the array is the order in which 
     * they will be sorted.
     */
    public GroupedComparator(Object[] objs) {
        int sz = objs.length;
        
        for(int i=0; i<sz; i++) {
            index.put(objs[i], new Integer(i));
        }
    }

    public int compare(Object o1, Object o2) {
        Integer i1 = (Integer)index.get(o1);
        Integer i2 = (Integer)index.get(o2);
        if(i2 == null) {
            return -1;
        } else 
        if(i1 == null) {
            return 1;
        }
        
        if( i1.intValue() < i2.intValue() ) {
            return -1;
        } else
        if( i1.intValue() > i2.intValue() ) {
            return 1;
        } else {
            return 0;
        }
    }

}
