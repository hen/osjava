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
// Fraction.java

package com.generationjava.math;

/**
 * A Fraction, containing a denominator and a numerator.
 */
public class Fraction {
    
    /**
     * Find the greatest common divisor of two numbers.
     */
    static public int gcd(int i1, int i2) {
        if(i2 == 0) {
            return i1;
        } else {
            return gcd(i2, i1 % i2);
        }
    }
    
    /**
     * Reduce a fraction down to its lowest level.
     */
    static public Fraction reduce(Fraction f) {
        int d = gcd(f.num, f.den);
        return new Fraction(f.num/d, f.den/d);
    }

    private int num;
    private int den;

    /**
     * Create a fraction. num/den
     *
     * @param num int numerator - one on top
     * @param den int denominator - one on bottom
     */
    public Fraction(int num, int den) {
        this.num = num;
        this.den = den;
    }
    
    /**
     * Create a Fraction that represents the number specified..
     *
     * @param num int numerator
     */
    public Fraction(int num) {
        this(num,1);
    }
    
    /**
     * Add a Fraction to this Fraction.
     */
    public Fraction add(Fraction f) {
        return new Fraction( this.num*f.den+this.den*f.num, this.den*f.den );
    }

    /**
     * Subtract a Fraction from this Fraction.
     */
    public Fraction sub(Fraction f) {
        return new Fraction( this.num*f.den-this.den*f.num, this.den*f.den );
    }
    
    /**
     * Multiple this fraction by another..
     */
    public Fraction mul(Fraction f) {
         return new Fraction( this.num*f.num, this.den*f.den );
    }

    /**
     * Divide this fraction by another.
     */
    public Fraction div(Fraction f) {
         return new Fraction( this.num*f.den, this.den*f.num );
    }
    
    /**
     * Invert this fraction.
     */
    public Fraction inverse() {
        return new Fraction( this.den, this.num );
    }
    
    /**
     * Are two fractions equal?
     */
    public boolean equals(Object obj) {
        if(obj instanceof Fraction) {
            Fraction f = (Fraction)obj;
            return ( (this.num*f.den) == (this.den*f.num) );
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return (int)((Integer.MAX_VALUE/this.den)*this.num);
    }
    
    public String toString() {
        return this.num+"/"+this.den;
    }
}
