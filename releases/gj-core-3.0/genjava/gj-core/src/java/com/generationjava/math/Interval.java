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
// @file   Interval.java
// @author bayard@generationjava.com
// @create 2001-02-04 

package com.generationjava.math;


/**
 * A continuous range between two doubles.
 */
public class Interval {

    private double lower;
    private double upper;

    /**
     * Create an Interval between two doubles.
     */
    public Interval(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    /**
     * Make an Interval with the specified center and difference from the 
     * center.
     */
    static public Interval makeIntervalCenterWidth(double center, double difference) {
        return new Interval(center - difference, center + difference);
    }

    /**
     * Make an Interval with the specified center and the specified 
     * percentage difference from the center.
     */
    static public Interval makeIntervalCenterPercentage(double center, float percentage) {
        return new Interval(center - (center*percentage/100), center + (center*percentage/100));
    }

    /**
     * Add this Interval to another one.
     */
    public Interval add(Interval i) {
        return new Interval(i.lower + this.lower, i.upper + this.upper);
    }
     
    /** 
     * Multiple this Interval by another one.
     */
    public Interval mul(Interval i) {
        double p1 = i.lower * this.lower;
        double p2 = i.lower * this.upper;
        double p3 = i.upper * this.lower;
        double p4 = i.upper * this.upper;
        return new Interval( min(p1,p2,p3,p4), max(p1,p2,p3,p4) );
    }
    
    /**
     * Divide this interval by another.
     */
    public Interval div(Interval i) {
        return mul( new Interval( 1/i.upper, 1/i.lower ) );
    }
    
    /**
     * Subtract an Interval from this Interval.
     */
    public Interval sub(Interval i) {
        return add( new Interval( -i.upper, -i.lower ) );
    }
    
    /**
     * Find the minimum of 4 values.
     */
    private double min(double d1, double d2, double d3, double d4) {
        double tmp = d1;
        if(d2 < tmp) {
            tmp = d2;
        }
        if(d3 < tmp) {
            tmp = d3;
        }
        if(d4 < tmp) {
            tmp = d4;
        }
        return tmp;
    }
    
    /**
     * Find the maximum of 4 values.
     */
    private double max(double d1, double d2, double d3, double d4) {
        double tmp = d1;
        if(d2 > tmp) {
            tmp = d2;
        }
        if(d3 > tmp) {
            tmp = d3;
        }
        if(d4 > tmp) {
            tmp = d4;
        }
        return tmp;
    }
    
    public String toString() {
        return this.lower+" - "+this.upper;
    }
    
    public boolean equals(Object obj) {
        if(obj instanceof Interval) {
            Interval ivl = (Interval)obj;
            return ( (this.lower == ivl.lower) && (this.upper == ivl.upper) );
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return (int) ((this.lower + this.upper) % Integer.MAX_VALUE);
    }
}
