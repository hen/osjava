package com.generationjava.math;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class FractionTest extends TestCase {

    public FractionTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //     Fraction.gcd
    //     Fraction.reduce
    //     Fraction->add(Fraction)
    //     Fraction->sub(Fraction)
    //     Fraction->mul(Fraction)
    //     Fraction->div(Fraction)
    //     Fraction->inverse
    //     Fraction->toString
    //     Fraction->equals(Object)
    //     Fraction->hashCode

    public void testGcd() {
        assertEquals( 5, Fraction.gcd(5, 25) );
        assertEquals( 1, Fraction.gcd(4, 25) );
        assertEquals( 25, Fraction.gcd(0, 25) );
    }

    public void testReduce() {
        assertEquals( new Fraction(1, 2), Fraction.reduce( new Fraction(5, 10) ) );
        assertEquals( new Fraction(1, 4), Fraction.reduce( new Fraction(25, 100) ) );
        assertEquals( new Fraction(11, 2), Fraction.reduce( new Fraction(55, 10) ) );
        assertEquals( new Fraction(1), Fraction.reduce( new Fraction(5, 5) ) );
        assertEquals( new Fraction(5), Fraction.reduce( new Fraction(25, 5) ) );
    }

    public void testInverse() {
        assertEquals( new Fraction(1, 5), new Fraction(5).inverse() );
        assertEquals( new Fraction(3, 9), new Fraction(3).inverse() );
    }

}
