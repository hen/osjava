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
package com.generationjava.util;

import java.util.Iterator;

import com.generationjava.namespace.Namespace;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.IteratorUtils;

public class Interpolator {

    /**
     * Interpolate variables into a String.
     */
    static public String interpolate(String text, Namespace namespace) {
        Iterator keys = namespace.iterateNames();
        while(keys.hasNext()) {
            String key = keys.next().toString();
            Object obj = namespace.getValue(key);
            String value = obj.toString();
            text = StringUtils.replace(text, "${"+key+"}", value);
            if(key.indexOf(" ") == -1) {
                text = StringUtils.replace(text, "$"+key, value);
            }
        }
        return text;
    }
 
    // pull out into Interpolator
    static public String interpolateLists(String text, Namespace namespace) {
        // form is:   $*{variable, 'separator'}
        // search for $*{
        int idx = text.indexOf("$*{");
        while(idx != -1) {
            // then get the variable (before , or } )
            int edx = text.indexOf("}", idx);
            if(edx == -1) {
                break;
            }
            String element = text.substring(idx+3, edx);
            int cdx = element.indexOf(",");
            String sep = ", ";
            if(cdx != -1) {
                sep = element.substring(cdx+1);
                element = element.substring(0, cdx);
            }
 
            Object obj = namespace.getValue(element);
            Iterator iterator = IteratorUtils.getIterator(obj);
            String str = "";
            if(iterator != null) {
                str = StringUtils.join(iterator, sep);
            }
            text = StringUtils.overlayString(text, str, idx, edx+1);
 
            idx = text.indexOf("$*{");
        }
 
        return text;
    }  

}
