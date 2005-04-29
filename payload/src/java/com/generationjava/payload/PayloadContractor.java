/*
 * Copyright (c) 2003-2004, Henri Yandell
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
 * + Neither the name of OSJava nor the names of its contributors 
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
package com.generationjava.payload;

import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.util.Enumeration;
import java.util.Properties;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Builds a self-extracting jar. 
 */
public class PayloadContractor {

    public static final boolean DEBUG = (System.getProperty("PAYLOAD.DEBUG")!=null);

// Allow payload/ to be specified as a system property

    public static void main(String[] args) {
        System.out.print("Payload contraction setup");

        // when run with -jar, the class path is the jar file
        String jarFile = System.getProperty("java.class.path");
        if(jarFile.indexOf(":") != -1) {
            jarFile = null;
            // get the jarFile as a -j argument
        }
        System.out.println(".");
if(DEBUG) System.out.println("DEBUG turned on. ");

        if(args.length == 0) {
            System.err.println("\nUnable to contract a payload as no target jar has been specified. ");
            System.exit(1);
        }
        if(!args[0].endsWith(".jar")) {
            System.err.println("\nRefusing to target a non-jar file. " +
                               "Please use a .jar filename then rename afterwards. ");
            System.exit(1);
        }
        String targetJar = args[0];
        if(args.length == 1) {
            System.err.println("\nUnable to contract a payload as no target files have been specified. ");
            System.exit(1);
        }

        // start writing to targetJar file
        FileInputStream fin = null;
        JarOutputStream jout = null;
        try {
            System.out.print("Contracting payload header");
            fin = new FileInputStream(new File(jarFile));
            jout = new JarOutputStream(new FileOutputStream(new File(targetJar)));

            JarFile jar = new JarFile(new File(jarFile));
            Enumeration enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                InputStream in = jar.getInputStream( entry );
                JarEntry entry2 = new JarEntry(entry.getName());
                entry2.setTime(entry.getTime());
                entry2.setSize(entry.getSize());
                jout.putNextEntry(entry2);
                if(entry.getName().equals("META-INF/MANIFEST.MF")) {
                    String mf = IOUtils.readToString(in);
                    mf = mf.replaceAll("Main-Class: com\\.generationjava\\.payload\\.PayloadContractor", 
                                       "Main-Class: com.generationjava.payload.PayloadExtractor");
                    jout.write(mf.getBytes());
                } else {
                    IOUtils.pushBytes(in, jout);
                }
                jout.closeEntry();
                System.out.print(".");
            }
            System.out.println("");

            // TODO: Look for args[1] being a .properties file 
            //       with the org.osjava.payload=true property set.
            // If so, store at the top level.
            int start = 1;
            if(args[1].endsWith(".properties")) {
                FileInputStream propsFin = null;
                try {
                    File propsFile = new File(args[1]);
                    propsFin = new FileInputStream(propsFile);
                    Properties tmp = new Properties();
                    tmp.load(propsFin);
                    if("true".equals(tmp.getProperty("org.osjava.payload"))) {
                        System.out.print("Inlining "+args[1]+" as payload.properties");
                        storeFile(jout, propsFile, "payload.properties");
                        System.out.println("");
                        start = 2;
                    }
                } catch(IOException ioe) {
                    ioe.printStackTrace();   //  reasons?
                } finally {
                    IOUtils.closeQuietly(propsFin);
                }
            }

            // loop over every argument, handling recursion, 
            // and pushing into the payload/ directory
            System.out.print("Contracting payload body");
            for(int i=start; i<args.length; i++) {
                String filename = args[i];
                File file = new File(filename);
                storeFile(jout, file);
            }

            System.out.println("\nFinished. ");

        } catch(IOException ioe) {
            System.err.println("\nIOException in building new jar. ");
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fin);
            IOUtils.closeQuietly(jout);
        }
    }

    // overloaded so that payload.properties may be written to the top level
    private static void storeFile(JarOutputStream jout, File file) throws IOException {
        String fileUnix = file.toString().replaceAll("\\\\", "/");
        storeFile(jout, file, "payload/"+fileUnix);
    }
    private static void storeFile(JarOutputStream jout, File file, String targetName) throws IOException {
        if(file.isDirectory()) {
            // recurse
            File[] children = file.listFiles();
            int sz = children.length;
            for(int i=0; i<sz; i++) {
                storeFile(jout, children[i]);
            }
        } else {
            FileInputStream fis = new FileInputStream(file);
            JarEntry entry = new JarEntry(targetName);
            // read time of file
            entry.setTime(System.currentTimeMillis());
            entry.setSize( file.length() );
            jout.putNextEntry(entry);
            IOUtils.pushBytes(fis, jout);
            jout.closeEntry();
            System.out.print(".");
        }
    }
}
