/*
 * Created on Aug 23, 2003
 */
package com.generationjava.apps.jpe.compiler;

import java.io.PrintStream;

import com.generationjava.apps.jpe.Compiler;
import com.generationjava.apps.jpe.CompilerException;

/**
 * @author hen
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
