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

import java.util.ArrayList;

/**
 * An implementation of Wildcard logic, as seen on command lines 
 * on UNIX and DOS.
 */
public class WildcardUtils {

    /**
     * See if a particular piece of text, often a filename, 
     * matches to a specified wildcard. 
     */
    public static boolean match(String text, String wildcard) {
        // split wildcard on ? and *
        // for each element of the array, find a matching block in text
        // earliest matching block counts
        String[] wcs = splitOnTokens(wildcard);
        int textIdx = 0;
        for(int i=0; i<wcs.length; i++) {
            if(textIdx == text.length()) {
                if("*".equals(wcs[i])) {
                    return true;
                }
                return wcs[i].length() == 0;
            }

            if("?".equals(wcs[i])) {
                textIdx++;
            } else
            if("*".equals(wcs[i])) {
                int nextIdx = i+1;
                if(nextIdx == wcs.length) {
                    return true;
                }
                int restartIdx = text.indexOf(wcs[nextIdx], textIdx);
                if(restartIdx == -1) {
                    return false;
                } else {
                    textIdx = restartIdx;
                }
            } else {
                if(!text.startsWith(wcs[i], textIdx)) {
                    return false;
                } else {
                    textIdx += wcs[i].length();
                }
            }
        }

        return true;
    }

    // package level so a unit test may run on this
    static String[] splitOnTokens(String text) {
        char[] array = text.toCharArray();
        if(text.indexOf("?") == -1 && text.indexOf("*") == -1) {
            return new String[] { text };
        }

        ArrayList list = new ArrayList();
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<array.length; i++) {
            if(array[i] == '?' || array[i] == '*') {
                if(buffer.length() != 0) {
                   list.add(buffer.toString());
                   buffer.setLength(0);
                }
                list.add(new String( new char[] { array[i] } ));
            } else {
                buffer.append(array[i]);
            }
        }
        if(buffer.length() != 0) {
            list.add(buffer.toString());
        }

        return (String[]) list.toArray(new String[0]);
    }
        

}
