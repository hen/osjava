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
// ByteArray.java

package com.generationjava.lang;


/**
 * Provides useful methods for manipulating ByteArrays.
 *
 * Todo: Integrate faster byte handling supplied by sclayman.
 *
 * @author bayard@generationjava.com
 * @date   2000-09-25
 */
public class ByteArray {

    private byte[] myBits;

    /**
     * Construct an object to contain an array of bytes.
     *
     * @param b byte[] of values to contain
     */
    public ByteArray(byte[] b) {
        myBits = b;
    }

    /**
     * converts a byte array into a String in hexidecimal form.
     *
     * @param b byte[] to turn to a String
     *
     * @return String of bytes in hexadecimal format
     */
    static public String byteArrayToString(byte[] b) {
        if(b == null) {
            return null;
        }

        StringBuffer ret = new StringBuffer();
        int sz = b.length;

        for(int i=0; i<sz; i++) {
            ret.append( byteToString(b[i]) );
        }

        return ret.toString();
    }
    
    /**
     * given a byte, return a String like:  "09" or "AA" etc..
     *
     * @param b byte to turn to a String
     *
     * @return String of byte in hexadecimal format
     */
    static public String byteToString(byte b) {
        int i = (int)b;
        if(i < 0) {
            i += 256;
        }
        String tmp = Integer.toHexString( i );
        if(tmp.length() == 1) {
            tmp = "0"+tmp;
        }
        return tmp;
    }

    /**
     * given a String like: "09" or "AA" etc, return the relevant byte.
     *
     * @param s String of byte in hexadecimal format
     *
     * @return byte value of hexadecimal-string
     */
    static public byte stringToByte(String s) {
        try {
            return (byte)Integer.parseInt(s,16);
        } catch(NumberFormatException nfe) {
                return 0;
        }
    }

    /**
     * converts a hexidecimal form of String to a byte array.
     *
     * @param s String of bytes in hexadecimal format
     *
     * @return byte[] values of hexadecimal-string
     */
    static public byte[] stringToByteArray(String bstr) {
        if(bstr == null) {
            return null;
        }

        int sz = bstr.length();
        byte[] bytes = new byte[sz/2];

        for(int i=0; i<sz/2; i++) {
            bytes[i] = stringToByte(bstr.substring(2*i,2*i+2));
        }

        return bytes;
    }
    
    /**
     * Flattern an array of longs into an array of byte.
     * The primitive type, 'long' is 8 times the size of 
     * a byte.
     *
     * @param s String of byte in hexadecimal format
     *
     * @return byte value of hexadecimal-string
     */
    static public byte[] longArrayToByteArray(long[] lns) {
        byte[] ret = new byte[lns.length*8];

        for(int i=0; i<lns.length; i++) {
            ret[i*8 + 0] = (byte)((lns[i] >>  0) & 0xFF);
            ret[i*8 + 1] = (byte)((lns[i] >>  8) & 0xFF);
            ret[i*8 + 2] = (byte)((lns[i] >> 16) & 0xFF);
            ret[i*8 + 3] = (byte)((lns[i] >> 24) & 0xFF);
            ret[i*8 + 4] = (byte)((lns[i] >> 32) & 0xFF);
            ret[i*8 + 5] = (byte)((lns[i] >> 40) & 0xFF);
            ret[i*8 + 6] = (byte)((lns[i] >> 48) & 0xFF);
            ret[i*8 + 7] = (byte)((lns[i] >> 56) & 0xFF);
        }
        
        return ret;
    }

}
