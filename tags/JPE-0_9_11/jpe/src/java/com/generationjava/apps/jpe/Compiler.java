/*
 * Created on Aug 23, 2003
 */
package com.generationjava.apps.jpe;

import java.io.PrintStream;

/**
 * Plug-in compiler so that different platforms may have 
 * different compiling.
 * 
 * @author hen
 */
public interface Compiler {

    void compile(String filename) throws CompilerException;
    void setErrorStream(PrintStream errorStream);

}
