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
package com.generationjava.random;

public class RandomNumber implements RandomMaker {

    public RandomNumber() {
    }

    static public int randomInt() {
        return randomInt(Integer.MAX_VALUE);
    }

    static public double randomDouble() {
        return randomDouble(Double.MAX_VALUE);
    }

    static public short randomShort() {
        return randomShort(Short.MAX_VALUE);
    }

    static public byte randomByte() {
        return randomByte(Byte.MAX_VALUE);
    }

    static public long randomLong() {
        return randomLong(Long.MAX_VALUE);
    }

    static public float randomFloat() {
        return randomFloat(Float.MAX_VALUE);
    }

    static public int randomInt(int range) {
        return (int)(range*Math.random());
    }

    static public double randomDouble(double range) {
        return range*Math.random();
    }

    static public short randomShort(short range) {
        return (short)(range*Math.random());
    }

    static public byte randomByte(byte range) {
        return (byte)(range*Math.random());
    }

    static public long randomLong(long range) {
        return (long)(range*Math.random());
    }

    static public float randomFloat(float range) {
        return (float)(range*Math.random());
    }

    public Object makeInstance(Class clss) {
        if(clss == Integer.class) {
            return new Integer(randomInt());
        }
        if(clss == Long.class) {
            return new Long(randomLong());
        }
        if(clss == Float.class) {
            return new Float(randomFloat());
        }
        if(clss == Double.class) {
            return new Double(randomDouble());
        }
        if(clss == Short.class) {
            return new Short(randomShort());
        }
        if(clss == Byte.class) {
            return new Byte(randomByte());
        }
        if(clss == Number.class) {
            return new Integer(randomInt());
        }
        if(clss == Integer.TYPE) {
            return new Integer(randomInt());
        }
        if(clss == Long.TYPE) {
            return new Long(randomLong());
        }
        if(clss == Float.TYPE) {
            return new Float(randomFloat());
        }
        if(clss == Double.TYPE) {
            return new Double(randomDouble());
        }
        if(clss == Short.TYPE) {
            return new Short(randomShort());
        }
        if(clss == Byte.TYPE) {
            return new Byte(randomByte());
        }
        return null;
    }

}
