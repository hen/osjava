package com.generationjava.lang;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.math.BigInteger;

public class BigIntegerUtilsTest extends TestCase {

    public BigIntegerUtilsTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    private BigInteger bi = BigInteger.valueOf(25L);   // 11001

    // need to test extractInt extractBoolean extractFloat
    public void testExtractInt() {
        assertEquals("11001[2,4]=2 failure", 2, BigIntegerUtils.extractInt(bi, 2, 4) );
    }

    public void testExtractFloat() {
        assertEquals("11001[0,4:2.2]= failure", 2.1F, BigIntegerUtils.extractFloat(bi, 0, 4, 2), 0 );
    }

    public void testExtractBoolean() {
        assertEquals("11001[0]=true failure", true, BigIntegerUtils.extractBoolean(bi, 0) );
        assertEquals("11001[1]=false failure", false, BigIntegerUtils.extractBoolean(bi, 1) );
        assertEquals("11001[2]=false failure", false, BigIntegerUtils.extractBoolean(bi, 2) );
        assertEquals("11001[3]=true failure", true, BigIntegerUtils.extractBoolean(bi, 3) );
        assertEquals("11001[4]=true failure", true, BigIntegerUtils.extractBoolean(bi, 4) );
    }

}
