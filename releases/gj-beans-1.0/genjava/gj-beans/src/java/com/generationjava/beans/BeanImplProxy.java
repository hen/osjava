package com.generationjava.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.generationjava.namespace.Namespace;
import com.generationjava.namespace.SimpleNamespace;

import org.apache.commons.lang.StringUtils;

public class BeanImplProxy implements InvocationHandler {

    public static Object newInstance(Class intfce) {
        BeanImplProxy handler = new BeanImplProxy(intfce);
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

    private Class type;
    private Namespace namespace;

    private BeanImplProxy(Class type) {
        this.type = type;
        this.namespace = new SimpleNamespace();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class returnType = method.getReturnType();
        String name = method.getName();
        if(returnType == Void.TYPE) {
            // test this
            if(name.startsWith("set")) {
                if(args.length == 1) {
                    String property = StringUtils.uncapitalise(name.substring(3));
                    this.namespace.putValue(property, args[0]);
                }
            }
            return null;
        }
        if(returnType == Class.class) {
            if("getClass".equals(name)) {
                // pretend to be this
                return this.type;
            }
        }
        if("toString".equals(name)) {
            // try to output nicely
            return BeansW.beanToString(proxy);
        }
        if(name.startsWith("get")) {
            String property = StringUtils.uncapitalise(name.substring(3));
            return this.namespace.getValue(property);
        }
        return null; // ??
    }

}
