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

/**
 * Handles encoding binary as a string. Inefficient, but easy to read.
 *
 * State: Prototype - 20010622
 */
public class Binary {

    /**
     * compress a string like "010110101010" into a byte[]
     *
     * @param str String encoding
     *
     * @return byte[] containing binary
     */
    public static byte[] binaryStringToBytes(String str) {
        if(str == null) {
            return null;
        }

        int sz = str.length();
        if(sz % 4 != 0) {
            throw new IllegalArgumentException("String must be a factor of 4");
        }
        byte[] ret = new byte[ sz/4 ];
        for( int i=0; i<sz; i+=4 ) {
            String b = str.substring(i, i+4);
            ret[i/4] = binaryToByte(b);
        }
        return ret;
    }
    
    /**
     * show the binary for a byte array
     *
     * @param b byte[] to be encoded
     *
     * @return String encoding
     */
    public static String bytesToBinaryString(byte[] b) {
        if(b == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<b.length; i++) {
            buffer.append(byteToBinary(b[i]));
        }
        return buffer.toString();
    }

    private static String byteToBinary(byte b) {
        switch(b) {
            case 0 : return  "0000";
            case 1 : return  "0001";
            case 2 : return  "0010";
            case 3 : return  "0011";
            case 4 : return  "0100";
            case 5 : return  "0101";
            case 6 : return  "0110";
            case 7 : return  "0111";
            case 8 : return  "1000";
            case 9 : return  "1001";
            case 10: return  "1010";
            case 11 : return "1011";
            case 12 : return "1100";
            case 13 : return "1101";
            case 14 : return "1110";
            case 15 : return "1111";
        }
        return "xxxx";
    }

    private static byte binaryToByte(String b) {
        if(b.equals("0000")) {
            return 0;
        } else
        if(b.equals("0001")) {
            return 1;
        } else
        if(b.equals("0010")) {
            return 2;
        } else
        if(b.equals("0011")) {
            return 3;
        } else
        if(b.equals("0100")) {
            return 4;
        } else
        if(b.equals("0101")) {
            return 5;
        } else
        if(b.equals("0110")) {
            return 6;
        } else
        if(b.equals("0111")) {
            return 7;
        } else
        if(b.equals("1000")) {
            return 8;
        } else
        if(b.equals("1001")) {
            return 9;
        } else
        if(b.equals("1010")) {
            return 10;
        } else
        if(b.equals("1011")) {
            return 11;
        } else
        if(b.equals("1100")) {
            return 12;
        } else
        if(b.equals("1101")) {
            return 13;
        } else
        if(b.equals("1110")) {
            return 14;
        } else
        if(b.equals("1111")) {
            return 15;
        } 

        // throw exception?
        return 0;
    }

}
