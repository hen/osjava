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

import java.util.Map;
import org.apache.commons.collections.ProxyMap;

/**
 * A map which stores objects by a key Class.
 * When obtaining the object, it will check inheritence and 
 * interface trees to see if the Class matches.
 */
public class ClassMap extends ProxyMap {

    public ClassMap(Map m) {
        super(m);
    }

    /**
     * Get the object from the map. If the key is not 
     * a Class object, then it uses the Class of the object.
     */
    public Object get(Object key) {
        if(key == null) {
            return null;
        }
        Class clss = null;

        if(key instanceof Class) {
            clss = (Class)key;
        } else {
            clss = key.getClass();
        }

        Object obj = super.get(clss);

        if(obj == null) {
            
            // if this is null, let's go up the inheritence tree
            obj = getInterfaces(clss);

            if(obj == null) {
                obj = getSuperclass(clss);
            }
        }

        return obj;
    }

    private Object getInterfaces(Class clss) {
        if(clss == null) {
            return null;
        }
        Object obj = null;
        Class[] interfaces = clss.getInterfaces();
        for(int i=0; i<interfaces.length; i++) {
            obj = (Object)super.get(interfaces[i]);
            if(obj != null) {
                return obj; 
            }
            obj = getInterfaces(interfaces[i]);
            if(obj != null) {
                return obj; 
            }
            obj = getSuperclass(interfaces[i]);
            if(obj != null) {
                return obj; 
            }
        }
        return null;
    }

    private Object getSuperclass(Class clss) {
        if(clss == null) {
            return null;
        }
        Object obj = null;
        Class superclass = clss.getSuperclass();
        obj = (Object)super.get(superclass);
        if(obj != null) {
            return obj; 
        }
        obj = getInterfaces(superclass);
        if(obj != null) {
            return obj; 
        }
        obj = getSuperclass(superclass);
        if(obj != null) {
            return obj; 
        }
        return null;
    }

}
