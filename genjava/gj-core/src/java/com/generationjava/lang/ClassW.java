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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.generationjava.collections.CollectionsW;

/**
 * A set of static utilities for use with Classes.
 *
 * @author bayard@generationjava.com
 * @date   2001-05-19
 */
final public class ClassW {

    /**
     * Create an object from the classname. Must have an empty constructor.
     *
     * @param classname String name of the class
     *
     * @return Object instance of the class or null
     */
    static public Object createObject(String classname) {
        Class tmpClass = null;

        tmpClass = getClass(classname);

        if(tmpClass == null) {
            System.err.println("No class for: "+classname);
            return null;
        }


        return createObject(tmpClass);
    }

    /**
     * Create an object from a class. 
     *
     * @param clss Class object to instantiate
     *
     * @return Object instance of the class or null
     */
    static public Object createObject(Class clss) {
        if(clss == null) {
            System.err.println("Null class passed in. ");
            return null;
        }

        try {
            return clss.newInstance();
        } catch (IllegalAccessException  iae) {
            System.err.println("Cant instantiate " + clss.getName() + " because " +
                   iae.getMessage());
        } catch (InstantiationException  ie) {
            System.err.println("Cant instantiate " + clss.getName() + " because " +
                   ie.getMessage());
        }

        return null;
    }

    /**
     * Is this Class in the CLASSPATH
     *
     * @param classname String of the class
     *
     * @return boolean exists or not.
     */
    static public boolean classExists(String classname) {
        Class tmpClass = null;

        /* try and load class */
        try {
//            tmpClass = Class.forName(classname);
            tmpClass = Thread.currentThread().getContextClassLoader().loadClass(classname);
        } catch (ClassNotFoundException cnfe) {
            return false;
        } catch (IllegalArgumentException iae) {
            return false;
        }
     
        return true;   
    }

    /**
     * Get the Class object for a classname.
     *
     * @param classname String of the class
     *
     * @return Class instance for the class.
     */
    static public Class getClass(String classname) {
        Class tmpClass = null;

        /* try an load class */
        try {
//            tmpClass = Class.forName(classname);
            tmpClass = Thread.currentThread().getContextClassLoader().loadClass(classname);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Can not resolve classname " + classname);
        } catch (IllegalArgumentException iae) {
            System.err.println("Can nott resolve " + tmpClass.getName() + " because " + iae.getMessage());
        }
     
        return tmpClass;   
    }

    /**
     * Is this Class object an instance of the class with this name.
     *
     * @param clss Class instance
     * @param inst String name of potential supertype
     *
     * @return boolean was it an instanceof
     */
    static public boolean classInstanceOf(Class clss, String inst) {
        if(classImplements(clss,inst)) {
            return true;
        } else
        if(classExtends(clss,inst)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Does this Class implement an interface with this name.
     *
     * @param clss Class instance
     * @param exts String name of potential interface
     *
     * @return boolean was it an implementor
     */
    static public boolean classImplements(Class clss, String exts) {

      Class sprcls = clss;
      Class excls  = getClass(exts);

      while(sprcls != null) {
        Class[] interfaces = sprcls.getInterfaces();

        for(int i=0;i<interfaces.length;i++) {
            if(interfaces[i].equals(excls)) {
                return true;
            }
        }

        sprcls = sprcls.getSuperclass();
      }

      return false;
    }

    /**
     * Does this Class extend a superclass with this name.
     *
     * @param clss Class instance
     * @param exts String name of potential superclass
     *
     * @return boolean was it a superclass
     */
    static public boolean classExtends(Class clss, String exts) {
        if(clss == null) {
            return false;
        }
        if(clss.getName().equals(exts)) {
            return true;
        }
        Class sprcls = clss.getSuperclass();
        Class excls = getClass(exts);

//        while(! sprcls.equals(sprcls.getSuperclass()) ) {
        while( sprcls != null ) {
            if(sprcls.equals(excls)) {
                return true;
            }
            sprcls = sprcls.getSuperclass();
        }
        return false;
    }

    static public void callMain(String[] args) {
        callMain(args[0], CollectionsW.getSubArray(args, 1) );
    }
    static public void callMain(String classname, String[] args) {
        callStatic(classname, "main", new Class[] { String[].class }, new Object[] { args } );
    }
    static public void callMain(Class clss, String[] args) {
        callStatic(clss, "main", new Class[] { String[].class }, new Object[] { args } );
    }
// figure out types from object args
   // static public void callStatic(String classname, String method, Object[] args) {
   // }
    /**
     * Runs a static method on a class.
     *
     * @param classname String name of class to invoke on.
     * @param method    String name of method to call.
     * @param args      Object[] arguments to method.
     */
    static public Object callStatic(String classname, String methodName, 
                                  Class[] types, Object[] args) 
    {
        return callStatic(getClass(classname), methodName, types, args);
    }

    static public Object callStatic(Class clss, String methodName, 
                                  Class[] types, Object[] args) 
    {
        try {
            Method method = clss.getMethod(methodName, types );
            return method.invoke( null, args );
        } catch(NoSuchMethodException nsme) {
        } catch(IllegalAccessException iae) {
        } catch(InvocationTargetException ite) {
        }
        return null;
    }

}
