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
 * + Neither the name of Simple-JNDI nor the names of its contributors 
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

package org.osjava.jndi;

import java.util.Hashtable;

class PropertiesStaticStore extends Hashtable {

    static private Hashtable SELF = new Hashtable();

    public synchronized int size() {
        return SELF.size();
    }

    public synchronized boolean isEmpty() {
        return SELF.isEmpty();
    }

    public synchronized java.util.Enumeration keys() {
        return SELF.keys();
    }

    public synchronized java.util.Enumeration elements() {
        return SELF.elements();
    }

    public synchronized boolean contains(java.lang.Object obj) {
        return SELF.contains(obj);
    }

    public boolean containsValue(java.lang.Object obj) {
        return SELF.containsValue(obj);
    }

    public synchronized boolean containsKey(java.lang.Object obj) {
        return SELF.containsKey(obj);
    }

    public synchronized java.lang.Object get(java.lang.Object obj) {
        return SELF.get(obj);
    }

    public synchronized java.lang.Object put(java.lang.Object key, java.lang.Object value) {
        return SELF.put(key, value);
    }

    public synchronized java.lang.Object remove(java.lang.Object obj) {
        return SELF.remove(obj);
    }

    public synchronized void putAll(java.util.Map map) {
        SELF.putAll(map);
    }

    public synchronized void clear() {
        SELF.clear();
    }

//    public synchronized java.lang.Object clone()

    public synchronized java.lang.String toString() {
        return SELF.toString();
    }

    public java.util.Set keySet() {
        return SELF.keySet();
    }

    public java.util.Set entrySet() {
        return SELF.entrySet();
    }

    public java.util.Collection values() {
        return SELF.values();
    }

    public synchronized boolean equals(java.lang.Object obj) {
        return SELF.equals(obj);
    }

    public synchronized int hashCode() {
        return SELF.hashCode();
    }


}
