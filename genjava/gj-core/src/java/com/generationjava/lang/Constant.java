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

import java.io.Serializable;

/**
 * Used to signify a constant value. Most commonly expected to be used 
 * in an interface, passed to a method that takes type: Constant and 
 * tests by equality and not the .equals() method.
 *
 * The constant wraps an Object value, thus allowing the Constant to 
 * contain actual information and not just a new Object.
 * It also adds typing information, so users of your code are forced 
 * to use your constant and not push their own magic number in.
 * 
 * It is extendable, so the cosntant can contain more than just the one 
 * value. It's not an interface, allowing people to provide their 
 * own classes which are 'Constantable' as this does not fit the 
 * use of ==.
 */
public class Constant implements Serializable {

    /**
     * Value being wrapped.
     */
    private final Object value;

    /**
     * Empty constructor. Uses itself as its value.
     */
    public Constant() {
        this.value = new Object();
    }

    /**
     * Pass the value to be wrapped by this Constant.
     */
    public Constant(Object obj) {
        this.value = obj;
    }

    public Constant(boolean b) {
        this.value = new Boolean(b);
    }

    public Constant(byte b) {
        this.value = new Byte(b);
    }

    public Constant(short s) {
        this.value = new Short(s);
    }

    public Constant(int i) {
        this.value = new Integer(i);
    }

    public Constant(char c) {
        this.value = new Character(c);
    }

    public Constant(long l) {
        this.value = new Long(l);
    }

    public Constant(float f) {
        this.value = new Float(f);
    }

    public Constant(double d) {
        this.value = new Double(d);
    }

    /**
     * Get the value that this Constant contains.
     *
     * @return Object value being wrapped.
     */
    public Object getValue() {
        if(this.value instanceof Constant) {
            return ((Constant)this.value).getValue();
        } else {
            return this.value;
        }
    }

    public char charValue() {
        return ((Character)this.value).charValue();
    }

    public int intValue() {
        return ((Integer)this.value).intValue();
    }

    public float floatValue() {
        return ((Float)this.value).floatValue();
    }

    public long longValue() {
        return ((Long)this.value).longValue();
    }

    public byte byteValue() {
        return ((Byte)this.value).byteValue();
    }

    public short shortValue() {
        return ((Short)this.value).shortValue();
    }

    public boolean booleanValue() {
        return ((Boolean)this.value).booleanValue();
    }

    /**
     * The toString of the wrapped object.
     *
     * @return String describing this Constant.
     */
    public String toString() {
        return getValue().toString();
    }

    /**
     * This runs equals on the underlying Object. The idea of 
     * Constant was that you could use == to show equality, however, 
     * RMI completely blows this away. So use .equals() when RMI is 
     * involved.
     */
    public boolean equals(Object obj) {
        if(obj instanceof Constant) {
            return getValue().equals(((Constant)obj).getValue());
        } else {
            return false;
        }
    }

    /** 
     * Runs hashCode on the underlying Object. 
     */
    public int hashCode() {
        return getValue().hashCode();
    }

}
