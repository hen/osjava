package com.generationjava.lang;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class BinaryTest extends TestCase {

    public BinaryTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------

    public void testNull() {
        assertReflectivity(null);
    }

    public void testEmpty() {
        assertReflectivity("");
    }

    public void testReflectivity() {
        assertReflectivity("10101011");
        assertReflectivity("00000000");
        assertReflectivity("10000000");
        assertReflectivity("111111111111");
        assertReflectivity("000000000001");
        assertReflectivity("1111");
        assertReflectivity("0000");
        assertReflectivity("0001");
        assertReflectivity("1010");
    }

    public void assertReflectivity(String str) {
        byte[] ba = Binary.binaryStringToBytes( str );
        assertEquals( str, Binary.bytesToBinaryString( ba ) );
    }

    public void testBadString() {
        try {
            byte[] ba = Binary.binaryStringToBytes( "101010" );
        } catch(IllegalArgumentException iae) {
            assertEquals("String must be a factor of 4", iae.getMessage() );
        }
    }

}
