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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.NumberUtils;

/**
 * A set of String library static methods. While extending String or 
 * StringBuffer would have been the nicest solution, that is not 
 * possible, so a simple set of static methods seems the most workable.
 *
 * Most methods have now gone to Commons Lang StringUtils.
 */
final public class StringW {

	static public String join(Object[] objs, String sep, String pre, String post) {
		String ret = StringUtils.join(objs, sep);
		if( (ret != null) && (ret != "")) {
			return pre + ret + post;
		} else {
			return ret;
		}
	}

    /**
     * Create a word-wrapped version of a String. Wrap at 80 characters and 
     * use newlines as the delimiter. If a word is over 80 characters long 
     * use a - sign to split it.
     */
    static public String wordWrap(String str) {
        return wordWrap(str, 80, "\n", "-");
    }
    /**
     * Create a word-wrapped version of a String. Wrap at a specified width and 
     * use newlines as the delimiter. If a word is over the width in lenght 
     * use a - sign to split it.
     */
    static public String wordWrap(String str, int width) {
        return wordWrap(str, width, "\n", "-");
    }
    static public String wordWrap(String str, String width, String delim, String split) {
        return wordWrap(str, NumberUtils.stringToInt(width), delim, split);
    }
    /**
     * Word-wrap a string.
     *
     * @param str   String to word-wrap
     * @param width int to wrap at
     * @param delim String to use to separate lines
     * @param split String to use to split a word greater than width long
     *
     * @return String that has been word wrapped
     */
    static public String wordWrap(String str, int width, String delim, String split) {
        int sz = str.length();

        /// shift width up one. mainly as it makes the logic easier
        width++;

        // our best guess as to an initial size
        StringBuffer buffer = new StringBuffer(sz/width*delim.length()+sz);

        // every line will include a delim on the end
        width = width - delim.length();

        int idx = -1;
        String substr = null;

        // beware: i is rolled-back inside the loop
        for(int i=0; i<sz; i+=width) {

            // on the last line
            if(i > sz - width) {
                buffer.append(str.substring(i));
                break;
            }

            // the current line
            substr = str.substring(i, i+width);

            // is the delim already on the line
            idx = substr.indexOf(delim);
            if(idx != -1) {
                buffer.append(substr.substring(0,idx));
                buffer.append(delim);
                i -= width-idx-delim.length();
                
                // Erase a space after a delim. Is this too obscure?
                if(substr.charAt(idx+1) != '\n') {
                    if(Character.isWhitespace(substr.charAt(idx+1))) {
                        i++;
                    }
                }
                continue;
            }

            idx = -1;

            // figure out where the last space is
            char[] chrs = substr.toCharArray();
            for(int j=width; j>0; j--) {
                if(Character.isWhitespace(chrs[j-1])) {
                    idx = j;
                    break;
                }
            }

            // idx is the last whitespace on the line.
            if(idx == -1) {
                for(int j=width; j>0; j--) {
                    if(chrs[j-1] == '-') {
                        idx = j;
                        break;
                    }
                }
                if(idx == -1) {
                    buffer.append(substr);
                    buffer.append(delim);
                } else {
                    if(idx != width) {
                        idx++;
                    }
                    buffer.append(substr.substring(0,idx));
                    buffer.append(delim);
                    i -= width-idx;
                }
            } else {
                /*
                if(force) {
                    if(idx == width-1) {
                        buffer.append(substr);
                        buffer.append(delim);
                    } else {
                        // stick a split in.
                        int splitsz = split.length();
                        buffer.append(substr.substring(0,width-splitsz));
                        buffer.append(split);
                        buffer.append(delim);
                        i -= splitsz;
                    }
                } else {
                */
                    // insert spaces
                    buffer.append(substr.substring(0,idx));
                    buffer.append(StringUtils.repeat(" ",width-idx));
                    buffer.append(delim);
                    i -= width-idx;
//                }
            }
        }
        return buffer.toString();
    }

}
