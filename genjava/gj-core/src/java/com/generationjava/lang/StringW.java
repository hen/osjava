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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.generationjava.collections.CollectionsW;
import org.apache.commons.collections.IteratorUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.NumberUtils;

import com.generationjava.util.Interpolator;
import com.generationjava.namespace.SimpleNamespace;

/**
 * A set of String library static methods. While extending String or 
 * StringBuffer would have been the nicest solution, that is not 
 * possible, so a simple set of static methods seems the most workable.
 *
 * Most methods have now gone to Commons Lang StringUtils.
 *
 * Method ideas have so far been taken from the PHP4, Ruby and .NET languages.
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
     * Find the Levenshtein distance between two strings.
     * This is the number of changes needed to change one string into 
     * another. Where each change is a single character modification.
     *
     * This implemmentation of the levenshtein distance algorithm 
     * is from http://www.merriampark.com/ld.htm
     *
     * @deprecated in favour of Commons Codec
     */
    static public int getLevenshteinDistance(String s, String t) {
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = s.length ();
        m = t.length ();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n+1][m+1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt (i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt (j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                d[i][j] = NumberUtils.minimum(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }


    /**
     * Quote a string so that it may be used in a regular expression 
     * without any parts of the string being considered as a 
     * part of the regular expression's control characters.
     * @deprecated Useless as it doesn't handle character classes and I never use it. Gone in 3.0.
     */
    static public String quoteRegularExpression(String str) {
        // replace ? + * / . ^ $ as long as they're not in character 
        // class. so must be done by hand
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        StringBuffer buffer = new StringBuffer(2*sz);
        for(int i=0; i<sz; i++) {
            switch(chrs[i]) {
              case '[' :
              case ']' :
              case '?' :
              case '+' :
              case '*' :
              case '/' :
              case '.' :
              case '^' :
              case '$' :
                buffer.append("\\");
              default : 
                buffer.append(chrs[i]);
            }
        }
        return buffer.toString();
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
