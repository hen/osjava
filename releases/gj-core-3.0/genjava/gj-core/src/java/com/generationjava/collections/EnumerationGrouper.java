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

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.collections.IteratorUtils;

/**
 * Take an enumeration of different types of objects.
 * Pass them into this object and then get them out by classname.
 */
public class EnumerationGrouper implements Grouper {

    static private Enumeration nullEnum = IteratorUtils.asEnumeration(IteratorUtils.EMPTY_ITERATOR);

    private Hashtable table = new Hashtable();

    public EnumerationGrouper(Enumeration enum) {
        addEnumeration(enum);
    }
    
    /**
     * Add this enumeration to the grouper.
     */
    public void addEnumeration(Enumeration enum) {
        while(enum.hasMoreElements()) {
            Object obj = enum.nextElement();
            String classname = obj.getClass().getName();
            Object vec = table.get(classname);
            if(vec == null) {
                vec = new Vector();
                table.put(classname, vec);
            }
            ((Vector)vec).addElement(obj);
        }
    }
    
    /**
     * Get an enumeration of all the Classes of type classname.
     */
    public Enumeration enumerateGroup(String classname) {
        Object vec = table.get(classname);
        if(vec instanceof Vector) {
            return ((Vector)vec).elements();
        } else {
           return nullEnum;
        }
    }
    
    /**
     * Enumerate over the types this group contains.
     */
    public Enumeration enumerateTypes() {
        return table.keys();
    }

}
