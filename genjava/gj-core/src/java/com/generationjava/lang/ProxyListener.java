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
package com.generationjava.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

///  Usage:
/// this.addActionListener( ProxyListener.newInstance(
///                           ActionListener.class, this, "flipColor"
///                       ) );
/// where flipColor is the name of a method

public class ProxyListener implements InvocationHandler {

    public static Object newInstance(Class intfce, Object target, String listenerMethod) {
        ProxyListener handler = new ProxyListener();
        handler.listenerMethod = listenerMethod;
        handler.target = target;
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

    private String listenerMethod;
    private Object target;

    private ProxyListener() {
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // can't do this line as proxy can't handle toString yet
        /// TODO: Add a toString handler bit.
        //System.err.println(""+proxy);
        Class clss = target.getClass();

        // it really should be a single array with an EventObject extension, 
        // but who knows. 
        Class[] sig = new Class[args.length];
        for(int i=0; i<args.length; i++) {
            if(args[i] == null) {
                sig[i] = Object.class; // wtf do we do?
            } else {
                sig[i] = args[i].getClass();
            }
        }

        // try the signature that matches the one called on this
        try {
            Method meth = clss.getMethod( this.listenerMethod, sig );
            return meth.invoke( target, args );
        } catch(NoSuchMethodException nsme) {
            // ignore. try another.
        }

        // try for an empty method
        try {
            Method meth = clss.getMethod( this.listenerMethod, new Class[0] );
            return meth.invoke( target, new Object[0] );
        } catch(NoSuchMethodException nsme) {
            // ignore. try another.
        }

        // try for a method that takes an object array
        try {
            Method meth = clss.getMethod( this.listenerMethod, new Class[] { Object[].class });
            return meth.invoke( target, new Object[] { args } );
        } catch(NoSuchMethodException nsme) {
            // ignore. try another.
        }

        // try for a method that takes an object
        try {
            Method meth = clss.getMethod( this.listenerMethod, new Class[] { Object.class });
            return meth.invoke( target, new Object[] { args[0] } );
        } catch(NoSuchMethodException nsme) {
            // ignore. complain.
        }

        throw new NoSuchMethodException("No delegating method available. ");
    }

}
