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

import java.math.BigInteger;

// NOTE: A BigInteger runs counter-intuitive possible. For an integer made 
// of the bits:  11010101   the first two bits are '01' and not 11. 
// That is, the indexing starts from the right and goes left.
public class BigIntegerUtils {

    public static float extractFloat(BigInteger bi, int start, int end, int decimal) {
        int intpart = extractInt(bi, start+decimal, end);
        int decpart = extractInt(bi, start, start+decimal);
        float decf = decpart;
        while(decf >= 1.0) {
            decf = decf / 10;
        }
        float ret = intpart + decf;
        return ret;
    }
    public static int extractInt(BigInteger bi, int start, int end) {
        bi = bi.shiftRight(start);
        int foo = (int)Math.pow(2, end - start) - 1;
        BigInteger mask = BigInteger.valueOf(foo);
        bi = bi.and(mask);
        return bi.intValue();
    }
    public static boolean extractBoolean(BigInteger bi, int index) {
        return bi.testBit(index);
    }

}
