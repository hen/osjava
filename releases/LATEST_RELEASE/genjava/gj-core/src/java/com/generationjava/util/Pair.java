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
package com.generationjava.util;

/**
 * A Lisp like Pair object.
 */
public class Pair {

    /**
     * Create a String version of a tree of pairs.
     */
    public String dumpTree() {
        return dumpTree(this);
    }
    /**
     * Create a String version of a tree of pairs using this start pair.
     */
    public String dumpTree(Pair pair) {
        String str = "";
        boolean ret = false;
        if(! (pair.car() instanceof Pair) ) {
            str += pair.car();
        }
        if(! (pair.cdr() instanceof Pair) ) {
            str += pair.cdr();
        }
        if(pair.car() instanceof Pair) {
            str += dumpTree( (Pair)pair.car()); ret = true;
        }
        if(pair.cdr() instanceof Pair) {
            str += dumpTree( (Pair)pair.cdr()); ret = true;
        }
        if(ret) str += "\n";
        return str;
    }

    private Object one;
    private Object two;
    private int weighting1;
    private int weighting2;
    
    /**
     * Create a Pair with two Pairs in it.
     */
    public Pair(Pair one, Pair two) {
        this(one, two, one.weighting(), two.weighting() );
        if(one.cdr() == null) {
            this.one = one.car();
        }
        if(two.cdr() == null) {
            this.two = two.car();
        } 
//        System.out.println("Building Pair from: "+one+", "+two+" gives "+this);
    }
    
    /**
     * A Pair with two Objects, weighted with the weightings given.
     */
    public Pair(Object one, Object two, int weighting1, int weighting2) {
        this.one = one;
        this.two = two;
        this.weighting1 = weighting1;
        this.weighting2 = weighting2;
    }
    /**
     * A Pair with two Objects of equal weighting.
     */
    public Pair(Object one, Object two) {
        this(one, two, 1, 1);
    }
    
    /**
     * A Pair with only one object of a specific weighting.
     */
    public Pair(Object one, int weighting) {
        this(one, null, weighting, 0);
    }
    /**
     * A Pair with only one object.
     */
    public Pair(Object one) {
        this(one, null, 1, 0);
    }
    
    /**
     * Get the combined weighting of this Pair.
     */
    public int weighting() {
        return carWeighting() + cdrWeighting();
    }
        
    /**
     * Get the weighting of the first element of this Pair.
     */
    public int carWeighting() {
        if( one instanceof Pair ) {
            return ( (Pair)one ).weighting();
        } else {
            return weighting1;
        }
    }
        
    /**
     * Get the weighting of the second element of this Pair.
     */
    public int cdrWeighting() {
        if( two instanceof Pair ) {
            return ( (Pair)two ).weighting();
        } else {        
            return weighting2;
        }        
    }
                
    /**
     * Get the first element of this Pair.
     */
    public Object car() {
        return one;
    }
    
    /**
     * Get the second element of this Pair.
     */
    public Object cdr() {
        return two;
    }
    
    /**
     * Simple debug version of this Pair.
     */
    public String toString() {
        return "{"+one+"["+weighting1+"]:"+two+"["+weighting2+"]}";
    }
}
