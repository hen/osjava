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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.generationjava.lang.ClassW;

import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * A wrapper around the Collections. Provides functionality above and 
 * beyond the duty of java.util's Collection API.
 */
final public class CollectionsW {

    static public Collection slice(Collection coll, int start, int end) {
        if(coll == null) {
            return null;
        }
        Iterator iterator = coll.iterator();
        Collection sub = cloneNewEmptyCollection(coll);
        end -= start;
        while(iterator.hasNext()) {
            if(start == 0) {
                if(end == 0) {
                    break;
                } else {
                    sub.add(iterator.next());
                    end--;
                }
            } else {
                iterator.next();   // ignore
                start--;
            }
        }
        return sub;
    }

    // unimplemented 
    static public Collection cloneNewEmptyCollection(Collection coll) {
        return (Collection)ClassW.createObject(coll.getClass());
    }

    static public Map cloneNewEmptyMap(Map map) {
        return (Map)ClassW.createObject(map.getClass());
    }

    /// TODO: Improve this, so it takes start and end and is a slice()
    static public String[] getSubArray(String[] ob, int idx) {
        if (idx > ob.length) {
            return new String[0];
        }
        String[] ob2 = new String[ob.length - idx];
        for (int i = idx; i < ob.length; i++) {
            ob2[i - idx] = ob[i];
        }
        return ob2;
    }

    // A search that doesn't involve the list needing to be sorted
    static public int simpleSearch(List list, Object obj) {
        return simpleSearch(list, obj, new ComparableComparator() );
    }
    static public int simpleSearch(List list, Object obj, Comparator cmp) {
        int sz = list.size();
        for( int i=0; i<sz; i++ ) {
            Object tmp = list.get(i);
            if(cmp.compare(obj, tmp) == 0) {
                return i;
            }
        }
        return -1; // ?
    }

}
