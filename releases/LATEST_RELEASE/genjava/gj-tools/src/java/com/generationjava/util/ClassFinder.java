/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Genjava-Core nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.generationjava.util;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import com.generationjava.tools.AbstractTool;
import com.generationjava.tools.CommandLineToolRunner;

import org.apache.commons.cli.CommandLine;

/**
 * Find a Class by name. That is, find the directory or jar it is in.
 */
public class ClassFinder extends AbstractTool {

    // add an argument to return all duplicate fqnames in jars
    static public void main(String[] strs) {
        CommandLineToolRunner cltr = new CommandLineToolRunner();
        cltr.runTool(ClassFinder.class, strs);
    }

    public void runTool(String[] strs) {
        CommandLine cargs = getCArgs("fc:", strs);
        ClassFinder cf = new ClassFinder();

        if(cargs.hasOption("c")) {
            cf.setClasspath((String)cargs.getOptionValue("c"));
        }

        boolean fail = cargs.hasOption("f");

        for(int i=0;i<strs.length;i++) {
            if(fail) {
                if(cf.findClass(strs[i]) == null) {
                    System.out.println(strs[i]);
                }
            } else {
                System.out.println(cf.findClass(strs[i]));
            }
        }
    }

    private String classpath;

    public ClassFinder() {
        this(System.getProperty("java.class.path"));
    }

    public ClassFinder(String classpath) {
        setClasspath(classpath);
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * Find the filename that the passed in classname exists in.
     * Returns null if it couldn't find the location.
     */
    public String findClass(String name) {
        // check the class exists in classpath
        // here we assume the JVM is far more efficient
        // than us.
        try {
            Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            return null;
        }

        // get System classpath property
//        String classpath = System.getProperty("java.class.path");

        // convert name from blah.blah to blah/blah
        String filename = name.replace('.','\\') + ".class";
        String reversename = name.replace('.','/') + ".class";

        // explode String
        String[] paths = StringUtils.split(this.classpath, File.pathSeparator);

        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            File file = new File(path);
            if (file.isDirectory()) {
                File file2 = new File(path + filename);
                if (file2.exists()) {
                    return path + filename;
                }
            } else {
                // jar. need to open and look.
                try {
                    ZipFile zip = new ZipFile(file);
                    Iterator iterator =
                            IteratorUtils.asIterator(zip.entries());
                    while (iterator.hasNext()) {
                        ZipEntry entry = (ZipEntry) iterator.next();
                        String zipname = entry.getName();
                        if(zipname.equals(filename)) {
                            return path+":"+filename;
                        }
                        if(zipname.equals(reversename)) {
                            return path+":"+reversename;
                        }
                    }
                    zip.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    System.err.println("BAD: "+file);
                    continue;
                }
            }
        }

        // damn. can't find it. and yet it exists.
        return null;
    }


}
