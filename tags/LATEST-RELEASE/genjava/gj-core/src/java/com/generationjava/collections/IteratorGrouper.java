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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.collections.IteratorUtils;

/**
 * Take an enumeration of different types of objects.
 * Pass them into this object and then get them out by classname.
 */
public class IteratorGrouper implements Grouper {

    static private Iterator nullIterator = IteratorUtils.EMPTY_ITERATOR;

    private HashMap map = new HashMap();

    public IteratorGrouper(Iterator iterator) {
        addIterator(iterator);
    }
    
    /**
     * Add this enumeration to the grouper.
     */
    public void addIterator(Iterator iterator) {
        while(iterator.hasNext()) {
            Object obj = iterator.next();
            String classname = obj.getClass().getName();
            Object list = map.get(classname);
            if(list == null) {
                list = new ArrayList();
                map.put(classname, list);
            }
            ((List)list).add(obj);
        }
    }
    
    /**
     * Get an enumeration of all the Classes of type classname.
     */
    public Iterator iterateGroup(String classname) {
        Object list = map.get(classname);
        if(list instanceof List) {
            return ((List)list).iterator();
        } else {
           return nullIterator;
        }
    }
    
    /**
     * Enumerate over the types this group contains.
     */
    public Iterator iterateTypes() {
        return map.keySet().iterator();
    }

}
