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
package com.generationjava.compare;

import java.util.Comparator;

/**
 * Sorts java package names.
 * Packages are grouped into java, javax, and other.
 * Inside each one they are alphabetical.
 */
public class PackageNameComparator implements Comparator {

    static private int JAVA = 1;
    static private int JAVAX = 2;
    static private int OTHER = 3;

    public int compare(Object obj1, Object obj2) {
        if( (obj1 instanceof String) && (obj2 instanceof String) ) {
            String str1 = (String)obj1;
            String str2 = (String)obj2;
            int type1 = getType(str1);
            int type2 = getType(str2);
            
            if(type1 == JAVA) {
                if(type2 == JAVA) {
                    str1 = str1.substring(4);
                    str2 = str2.substring(4);
                } else {
                    return -1;
                }
            } else
            if(type2 == JAVA) {
                return 1;
            } else
            if(type1 == JAVAX) {
                if(type2 == JAVAX) {
                    str1 = str1.substring(5);
                    str2 = str2.substring(5);
                } else {
                    return -1;
                }
            } else
            if(type2 == JAVAX) {
                return 1;
            }

            return str1.compareTo(str2);
        } else {
            return 0;
        }
    }

    private static int getType(String str) {
        if(str.startsWith("java")) {
            if(str.charAt(4) == '.') {
                return JAVA;
            } else 
            if( (str.charAt(4) == 'x') && (str.charAt(5) == '.') ) {
                return JAVAX;
            }            
        }
        return OTHER;
    }

}
