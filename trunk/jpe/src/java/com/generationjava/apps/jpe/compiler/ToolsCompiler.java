/*
 * Created on Aug 23, 2003
 */
package com.generationjava.apps.jpe.compiler;

import java.io.PrintStream;

import com.generationjava.apps.jpe.Compiler;
import com.generationjava.apps.jpe.CompilerException;

/**
 * @author hen
 * This is a compiler using the javac jar. Most PDAs won't have tools.jar 
 * though, so to use this you have to take that jar from an existing platform.
 */
public class ToolsCompiler implements Compiler {

    public void compile(String filename) throws CompilerException {
    	String[] argv = new String[1];
    	argv[0] = filename;
	    sun.tools.javac.Main.main(argv);
    }
    
    public void setErrorStream(PrintStream errorStream) {
    
    }

}
