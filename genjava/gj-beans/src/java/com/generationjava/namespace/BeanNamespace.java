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
package com.generationjava.namespace;

import java.util.Iterator;

import com.generationjava.beans.ReflectionBeanViewer;

/**
 * A Bean namespace. It gets its value from a java-bean.
 */
public class BeanNamespace extends AbstractNamespace {

    private ReflectionBeanViewer myViewer;
    private Object myBean;

    public BeanNamespace(Object bean) {
        this.myViewer = new ReflectionBeanViewer();
        this.myBean = bean;
    }

    public Object getBean() {
        return this.myBean;
    }

    /**
     * Get a property from a bean. Dotted notation, array notation 
     * and associative array notation all apply.
     */
    public Object getValue(String name) {
        return myViewer.get(name,myBean);
    }

    /**
     * Unimplemented.
     */
    public void putValue(String name, Object info) {
        // bean-namespace's are read only for the moment.
    }

    public Iterator iterateNames() {
        // return the names
        return null;
    }

}
