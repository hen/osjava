/*
 * Created on Aug 23, 2003
 */
package com.generationjava.apps.jpe.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;

import com.generationjava.apps.jpe.Compiler;
import com.generationjava.apps.jpe.CompilerException;

/**
 * @author hen
 */
public class CommandLineCompiler implements Compiler {

    private String command;
    private String arguments;
    private PrintStream errorStream;

    public CommandLineCompiler() {
    	this("javac", "");
    }
    
    public CommandLineCompiler(String command, String arguments) {
    	this.command = command;
    	this.arguments = arguments;
    }
    
    public void setErrorStream(PrintStream errorStream) {
        this.errorStream = errorStream;
    }

	/* 
	 * @see com.generationjava.apps.jpe.Compiler#compile(java.lang.String)
	 */
	public void compile(String filename) throws CompilerException {
		// we need to strip the front part off of 'filename', so that 
		// it may compile in the right place. We do this by grabbing the 
		// package out of the filename

 
        String pkge = getClassAsFile(filename);
        String dir = filename.substring(0, filename.length() - pkge.length());
    
		String run = this.command + " " + this.arguments + " " + pkge;
		
        try {
            Process ps = null;
            if(dir == null || dir.trim().equals("")) {
                ps = Runtime.getRuntime().exec(run);
            } else {
                ps = Runtime.getRuntime().exec(run, null, new File(dir));
            }
System.out.println("Just ran: "+run+" in "+dir);
            InputStream err = ps.getErrorStream();
            int ch = 0;
//            int i=0;
            while( (ch = err.read()) != -1) {
                this.errorStream.print((char)ch);
//                i++;
//                if(i == 5000) {
//                    this.errorStream.println("Had to kill the loop. ");
//                    break;
//                }
            }
	    } catch(IOException ioe) {
	    	System.err.println("ERROR: "+ioe);
	    	throw new CompilerException(ioe);
	    }
    }
    
    private String getClassAsFile(String filename) {
        Reader rdr = null;        
        try {
            rdr = new FileReader( new File(filename) );
            
            BufferedReader brdr = new BufferedReader ( rdr );
                String line = null; 
                while( (line = brdr.readLine()) != null ) {
                    line = line.trim();
                    if(line.startsWith("package")) {
                        // remove package 
                        line = line.substring(7).trim();
                        // remove ;
                        line = line.substring(0, line.length() - 1).trim();
                        line = replace(line, ".", "/", -1);
                        
                        // now to add the name of the class back on
                        int idx = filename.lastIndexOf("/");
                        if(idx != -1) {
                            String clss = filename.substring(idx);
                            line = line + clss;
                        } else {
                            // ??
                        }
                        
                        return line;
                    }
                }
        } catch(IOException ioe) {
            return filename;
        } finally {
            try {
                rdr.close();
            } catch(IOException ioe) {
            }
        }
        return filename;
    }
    
    // COPIED FROM Commons.Lang StringUtils
    private static String replace(String text, String repl, String with, int max) {
        if (text == null || repl == null || with == null || repl.length() == 0 || max == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }        
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

}
