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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Uses Reflection to get a named property from an object.
 */
public class ReflectionBeanViewer extends AbstractBeanViewer {

    public ReflectionBeanViewer() {
    }

    /**
     * Get an indexed property from a java bean. If the property is 
     * not indexed then a -1 is passed in.
     *
     * If it's not possible to get the object, then null is returned.
     */
    public Object invokeGet(BeanViewRuntime runtime, Object bean, String methodName, Object index) {
        int idx = -1;
        boolean arrayLike = (index instanceof Integer);
        if(arrayLike) {
            idx = ((Integer)index).intValue();
        }
        methodName = "get"+StringUtils.capitalise(methodName);

        Method getMethod = null;
        Class[] clss0 = new Class[0];
        Class[] clss = null;
        try {
            if(arrayLike) {
                clss = new Class[1];
                clss[0] = Integer.TYPE;
            } else {
                clss = clss0;
            }
            getMethod = bean.getClass().getMethod(methodName, clss);
        } catch(SecurityException se) {
        } catch(NoSuchMethodException nsme) {
            try {
                if(clss != clss0) {
                    getMethod = bean.getClass().getMethod(methodName, clss0);
                }
                arrayLike = false;
            } catch(SecurityException se2) {
            } catch(NoSuchMethodException nsme2) {
            }
        }

        if(getMethod == null) {
            return null;
        }
        try {
            Object[] param = null;
            if(arrayLike) {
                param = new Object[1];
                param[0] = (Integer)index;
            } else {
                param = new Object[0];
            }
            Object obj = getMethod.invoke( bean, param );
            obj = CollectionUtils.index(obj, index);

            return obj;
        } catch(IllegalAccessException iae) {
            iae.printStackTrace();
            return null;
        } catch(IllegalArgumentException iae) {
            iae.printStackTrace();
            return null;
        } catch(InvocationTargetException ite) {
            ite.printStackTrace();
            return null;
        }

    }

    public void invokeSet(BeanViewRuntime runtime, Object bean, String methodName, Object idx, Object value) {
//        int idx = -1;
//        boolean arrayLike = (index instanceof Integer);
//        if(arrayLike) {
//            idx = ((Integer)index).intValue();
//        }
        methodName = "set"+StringUtils.capitalise(methodName);

        Method setMethod = null;
        Method[] methods = bean.getClass().getMethods();
        int size = methods.length;
        for(int i=0; i<size; i++) {
            if(methods[i].getName().equals(methodName)) {
                setMethod = methods[i];
                break;
            }
        }

        /* OLD GET
        Class[] clss0 = new Class[0];
        Class[] clss = null;
        try {
            if(arrayLike) {
                clss = new Class[1];
                clss[0] = Integer.TYPE;
            } else {
                clss = clss0;
            }
            getMethod = bean.getClass().getMethod(methodName, clss);
        } catch(SecurityException se) {
        } catch(NoSuchMethodException nsme) {
            try {
                if(clss != clss0) {
                    getMethod = bean.getClass().getMethod(methodName, clss0);
                }
                arrayLike = false;
            } catch(SecurityException se2) {
            } catch(NoSuchMethodException nsme2) {
            }
        }
        */

        if(setMethod == null) {
            return;
        }

        System.err.println("CONVERT-TO"+setMethod.getParameterTypes()[0]);
        System.err.println("CONVERT:"+value);
        value = BeansW.convert(value, setMethod.getParameterTypes()[0]);
        
        try {
            Object[] param = null;
//            if(arrayLike) {
//                param = new Object[1];
//                param[0] = (Integer)index;
//            } else {
                param = new Object[1];
                param[0] = value;
//            }
            setMethod.invoke( bean, param );
//            obj = CollectionUtils.index(obj, index);

            return;
        } catch(IllegalAccessException iae) {
            iae.printStackTrace();
            return;
        } catch(IllegalArgumentException iae) {
            iae.printStackTrace();
            return;
        } catch(InvocationTargetException ite) {
            ite.printStackTrace();
            return;
        }

    }

}
