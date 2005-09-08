/*
 * Created on Aug 23, 2003
 */
package com.generationjava.apps.jpe.compiler;

import java.io.PrintStream;

import java.lang.reflect.Method;

import com.generationjava.apps.jpe.Compiler;
import com.generationjava.apps.jpe.CompilerException;

/**
 * @author hen
 * This is a compiler using the javac jar. Most PDAs won't have tools.jar 
 * though, so to use this you have to take that jar from an existing platform.
 */
public class ToolsCompiler implements Compiler {

    public void compile(String filename) throws CompilerException {
	    //sun.tools.javac.Main.main(argv);
        // use reflection to avoid compile failures when 
        // Tools not present
        try {
            Class javac = Class.forName("sun.tools.javac.Main");
            Method main = javac.getMethod("main", new Class[] { String[].class } );
            main.invoke(null, new Object[] { new String[] { filename } } );
        } catch(Exception e) {
            throw new CompilerException(e);
        }
    }
    
    public void setErrorStream(PrintStream errorStream) {
    
    }

}
