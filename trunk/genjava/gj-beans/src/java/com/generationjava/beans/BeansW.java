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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.generationjava.lang.ClassW;
import org.apache.commons.lang.NumberUtils;

public class BeansW {

    static private java.text.DateFormat format = new java.text.SimpleDateFormat();

    static public Object convert(Object value, Class toClass) {
        if(toClass.equals(value.getClass())) {
            return value;
        } else
        if(ClassW.classInstanceOf(toClass, "java.lang.String")) {
            return value;
        } else 
        if(ClassW.classInstanceOf(toClass, "java.lang.Integer") ||
           Integer.TYPE.equals(toClass)
          ) {
            return NumberUtils.createInteger(value.toString());
        } else 
        if(ClassW.classInstanceOf(toClass, "java.lang.Long") ||
           Long.TYPE.equals(toClass)
          ) {
            return NumberUtils.createLong(value.toString());
        } else 
        if(ClassW.classInstanceOf(toClass, "java.lang.Double") ||
           Double.TYPE.equals(toClass)
          ) {
            return NumberUtils.createDouble(value.toString());
        } else 
        if(ClassW.classInstanceOf(toClass, "java.lang.Number")) {
            return NumberUtils.createNumber(value.toString());
        } else 
        if(ClassW.classInstanceOf(toClass, "java.util.Date")) {
//            return DateW.parseString(value.toString());
            try {
                return format.parse( value.toString() );
            } catch(java.text.ParseException pe) {
                return value;
            }
        } else {
            return value;
        }
    }

    static public String beanToString(Object bean) {
        if(bean == null) {
            return null;
        }
        Class clss = bean.getClass();
        StringBuffer text = new StringBuffer();
        try {
            BeanInfo beaninfo = Introspector.getBeanInfo(clss);
            PropertyDescriptor[] pd = beaninfo.getPropertyDescriptors();
            for(int i=0; i<pd.length; i++) {
                if(pd[i] == null) {
                    continue;
                }
                Method method = pd[i].getReadMethod();
                Class arg = method.getReturnType();
                if("class".equals(pd[i].getName())) {
                    if(arg == Class.class) {
                        continue;
                    }
                }
                Object val = method.invoke(bean,new Object[0]);
                text.append(pd[i].getName());
                text.append(": ");
                text.append(""+val);
                text.append("\n");
            }
        } catch(IntrospectionException ie) {
            ie.printStackTrace();
        } catch(IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch(IllegalAccessException iae) {
            iae.printStackTrace();
        } catch(InvocationTargetException ite) {
            ite.printStackTrace();
        }

        return text.toString();
    }

}
