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
package com.generationjava.random;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RandomBeanProxy implements InvocationHandler {

    public static Object newInstance(Class intfce) {
        RandomBeanProxy handler = new RandomBeanProxy();
        Class proxyClass = Proxy.getProxyClass(
            intfce.getClassLoader(), 
            new Class[] { intfce }
            );
        try { 
            return proxyClass.getConstructor( new Class[] { InvocationHandler.class }
            ).newInstance(new Object[] { handler });
        } catch(NoSuchMethodException nsme) {
            nsme.printStackTrace();
        } catch(SecurityException se) {
            se.printStackTrace();
        } catch(InstantiationException ie) {
            ie.printStackTrace();
        } catch(IllegalAccessException iae) {
            iae.printStackTrace();
        } catch(InvocationTargetException ite) {
            ite.printStackTrace();
        }
        return null;
    }

    private RandomBeanProxy() {
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // we have to come up with a random reply to any method.
        // we can try to be cute and handle set and get properly....
        // but it's probably not worth it. maybe.

        // proxy should be this...
        Class returnType = method.getReturnType();
        if(returnType == Void.TYPE) {
            return null;
        }
        String name = method.getName();
        if("toString".equals(name)) {
            return ToString.beanToString(proxy);
        }
        RandomObject rndObj = new RandomObject();
        return rndObj.makeInstance(returnType);
    }

}
