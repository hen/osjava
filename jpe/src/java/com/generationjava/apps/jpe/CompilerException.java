/*
 * Created on Aug 23, 2003
 */
package com.generationjava.apps.jpe;

/**
 * @author hen
 */
public class CompilerException extends Exception {

    public CompilerException(Throwable thble) {
    	super(thble.getMessage());
    }

}
