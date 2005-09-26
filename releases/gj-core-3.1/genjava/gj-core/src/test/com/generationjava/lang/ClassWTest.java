package com.generationjava.lang;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

// used in tests
import java.util.HashMap;

public class ClassWTest extends TestCase {

    public ClassWTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //    Object createObject(String) {
    //    Object createObject(Class) {
    //    boolean classExists(String) {
    //    Class getClass(String) {
    //    void callMain(String[]) {
    //    void callMain(String, String[]) {
    //    Object callStatic(String, String, Class[], Object[])
    //    Object callStatic(Class, String, Class[], Object[])

    public void testCreateObject() {
        assertEquals("Unable to make a new HashMap", new HashMap(), ClassW.createObject("java.util.HashMap") );
        assertEquals("Unable to make a new HashMap",  new HashMap(), ClassW.createObject(HashMap.class) );
    }

    public void testClassExists() {
        assertEquals("java.util.HashMap does not exist",  true, ClassW.classExists("java.util.HashMap") );
        assertEquals("java.boo.Ghost exists",  false, ClassW.classExists("java.boo.Ghost") );
    }

    public void testGetClass() {
        assertEquals("Unable to get String.class",  String.class, ClassW.getClass("java.lang.String") );
    }

}
