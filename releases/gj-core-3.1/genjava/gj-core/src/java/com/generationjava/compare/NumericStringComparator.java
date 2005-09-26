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
 * A Comparator which deals with alphabet characters 'naturally', but 
 * deals with numerics numerically. Leading 0's are ignored numerically,
 * but do come into play if the number is equal. Thus aaa119yyyy comes before 
 * aaa0119xxxx regardless of x or y.
 *
 * The comparison should be very performant as it only ever deals with 
 * issues at a character level and never tries to consider the 
 * numerics as numbers.
 *
 * @author bayard@generationjava.com
 */
public class NumericStringComparator implements Comparator {

    public NumericStringComparator() {
    }

    public int compare(Object o1, Object o2) {
// System.out.println("Comparing: "+o1+" and "+o2);
        if(o1 == null) {
            return 1;
        } else
        if(o2 == null) {
            return -1;
        }

        String s1 = o1.toString();
        String s2 = o2.toString();

        // find the first digit.
        int idx1 = getFirstDigitIndex(s1);
        int idx2 = getFirstDigitIndex(s2);

        if( ( idx1 == -1 )   || 
            ( idx2 == -1 ) ||
            ( !s1.substring(0,idx1).equals(s2.substring(0,idx2)) )
          )
        {
// System.out.println("Shortcutted. ");
            return s1.compareTo(s2);
        }

        // find the last digit
        int edx1 = getLastDigitIndex(s1, idx1);
        int edx2 = getLastDigitIndex(s2, idx2);

        String sub1 = null;
        String sub2 = null;

        if(edx1 == -1) {
            sub1 = s1.substring(idx1);
        } else {
            sub1 = s1.substring(idx1, edx1);
        }

        if(edx2 == -1) {
            sub2 = s2.substring(idx2);
        } else {
            sub2 = s2.substring(idx2, edx2);
        }

        // deal with zeros at start of each number
        int zero1 = countZeroes(sub1);
        int zero2 = countZeroes(sub2);

        sub1 = sub1.substring(zero1);
        sub2 = sub2.substring(zero2);

        // if equal, then recurse with the rest of the string
        // need to deal with zeroes so that 00119 appears after 119
        if(sub1.equals(sub2)) {
            int ret = 0;
            if(zero1 > zero2) {
                ret = 1;
            } else
            if(zero1 < zero2) {
                ret = -1;
            }
// System.out.println("EDXs: "+edx1+" & "+edx2);
            if(edx1 == -1) {
                s1 = "";
            } else {
                s1 = s1.substring(edx1);
            }
            if(edx2 == -1) {
                s2 = "";
            } else {
                s2 = s2.substring(edx2);
            }

            int comp = compare(s1, s2);
            if(comp != 0) {
                ret = comp;
            }
// System.out.println("Dealt with rest of string: "+ret);
            return ret;
        } else {
            // if a numerical string is smaller in length than another
            // then it must be less. 
            if(sub1.length() != sub2.length()) {
// System.out.println("Ahah, different length. ");
                return ( sub1.length() < sub2.length() ) ? -1 : 1;
            }
        }


        // now we get to do the string based numerical thing :)
        // going to assume that the individual character for the 
        // number has the right order. ie) '9' > '0'
        // possibly bad in i18n.
        char[] chr1 = sub1.toCharArray();
        char[] chr2 = sub2.toCharArray();

        int sz = chr1.length;
        for(int i=0; i<sz; i++) {
            // this should give better speed
            if(chr1[i] != chr2[i]) {
// System.out.println("Length is different. ");
                return (chr1[i] < chr2[i]) ? -1 : 1;
            }
        }

// System.out.println("Default. Boo. ");
        return 0;
    }

    private int getFirstDigitIndex(String str) {
        return getFirstDigitIndex(str, 0);
    }
    private int getFirstDigitIndex(String str, int start) {
        return getFirstDigitIndex(str.toCharArray(), start);
    }
    private int getFirstDigitIndex(char[] chrs, int start) {
        int sz = chrs.length;

        for(int i=start; i<sz; i++) {
            if(Character.isDigit(chrs[i])) {
                return i;
            }
        }

        return -1;
    }

    private int getLastDigitIndex(String str, int start) {
        return getLastDigitIndex(str.toCharArray(), start);
    }
    private int getLastDigitIndex(char[] chrs, int start) {
        int sz = chrs.length;

        for(int i=start; i<sz; i++) {
            if(!Character.isDigit(chrs[i])) {
                return i;
            }
        }

        return -1;
    }

    public int countZeroes(String str) {
        int count = 0;

        // assuming str is small...
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '0') {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    // UNUSED
    private boolean containsOnly(String str, char ch) {
        return containsOnly( str.toCharArray(), ch );
    }
    private boolean containsOnly(char[] chrs, char ch) {
        int sz = chrs.length;

        for(int i=0; i<sz; i++) {
            if(chrs[i] != ch) {
                return false;
            }
        }

        return true;
    }
        
}
