/*
 * Copyright (c) 2003-2004, Henri Yandell
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
 * + Neither the name of OSJava nor the names of its contributors 
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
package org.osjava.payload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * used to handle interpolation concepts, ie:
 *   org.osjava.payload=true
 *   org.osjava.payload.interpolate.endsWith=txt
 *   org.osjava.payload.interpolate.endsWith=xml
 *   org.osjava.payload.interpolate.matches=regexp
 */
class Interpolation {

    private PayloadConfiguration config;

    public Interpolation(PayloadConfiguration configuration) {
        this.config = configuration;
    }

    public boolean interpolatableArchive(String name) {
        Iterator itr = this.config.getArchiveEndsWith().iterator();
        while(itr.hasNext()) {
            String substr = (String) itr.next();
            if(name.endsWith(substr)) {
                return true;
            }
        }
        itr = this.config.getArchiveMatches().iterator();
        while(itr.hasNext()) {
            String pattern = (String) itr.next();
            if(name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public boolean interpolatable(String name) {
        Iterator itr = this.config.getFileEndsWith().iterator();
        while(itr.hasNext()) {
            String substr = (String) itr.next();
            if(name.endsWith(substr)) {
                return true;
            }
        }
        itr = this.config.getFileMatches().iterator();
        while(itr.hasNext()) {
            String pattern = (String) itr.next();
            if(name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public String interpolate(String str, Properties props) {
        Iterator itr = props.keySet().iterator();
        while(itr.hasNext()) {
            String key = (String) itr.next();
//            str = str.replaceAll( "\\$\\{"+key+"\\}", props.getProperty(key) );
            str = replace(str, "${"+key+"}", props.getProperty(key), -1 );
        }
        return str;
    }
    
    // From Jakarta Commons Lang's StringUtils
    private static String replace(String text, String repl, String with, int max) {
        if (text == null || repl == null || repl.equals("") || with == null || max == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

}
