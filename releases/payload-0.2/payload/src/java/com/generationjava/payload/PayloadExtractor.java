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

import java.util.jar.*;

import java.util.*;

import java.io.*;

public class PayloadExtractor {

    public static void main(String[] args) {
        System.out.print("Payload extraction setup");

        // when run with -jar, the class path is the jar file
        String jarFile = System.getProperty("java.class.path");
        if(jarFile.indexOf(":") != -1) {
            jarFile = null;
            // get the jarFile as a -j argument
        }
        String jarName = jarFile.substring( 0, jarFile.length() - 4 );
        System.out.print(".");

        Properties props = null;
        if(args.length == 0) {
            System.err.println("\nNo properties file specified, will output without interpolation. ");
        } else {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(new File(args[0]));
                props = new Properties();
                props.load(fin);
            } catch(IOException ioe) {
                System.err.println("\nUnable to find properties file, will output without interpolation. ");
            } finally {
                if(fin != null) {
                    try {
                        fin.close();
                    } catch(IOException ioe) {
                        // ignore
                    }
                }
            }
        }
        System.out.println(".");

        System.out.print("Payload extracting");
        // loop....
        try {
            JarFile jar = new JarFile(new File(jarFile));

            Interpolation interpolation = null;

            // need to find a way to ensure the interpolation is read 
            // first. possibly scan through the zip first?
            Enumeration enum = jar.entries();
            while(enum.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum.nextElement();
                if(entry.getName().equals("payload.properties")) {
                    InputStream in = jar.getInputStream( entry );
                    String text = IOUtils.readToString(in);
                    interpolation = new Interpolation(text);
                    break;
                }
            }

            if(interpolation == null) {
                interpolation = Interpolation.DEFAULT;
            }

            enum = jar.entries();
            while(enum.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum.nextElement();
                if(!entry.getName().startsWith("payload")) {
                    continue;
                }
                if(entry.getName().equals("payload.properties")) {
                    continue;
                }
                // remove payload/
                String inName = entry.getName().substring(8);
                String outName = jarName + File.separator + inName;
                File outFile = new File(outName);
                if(entry.isDirectory()) {
                    outFile.mkdirs();
                    continue;
                } else {
                    outFile.getParentFile().mkdirs();
                }

                InputStream in = jar.getInputStream( entry );

                // TODO: configurable interpolation targets
                // trusting that we're not interpolating anything 
                // that can't fit in memory
                if( props != null && interpolation.interpolatable(outName)) {
                    // interpolate push
                    String text = IOUtils.readToString(in);
                    text = interpolation.interpolate(text, props);
                    in.close();
                    in = new ByteArrayInputStream(text.getBytes());
                }

                OutputStream out = new FileOutputStream( outFile );
                IOUtils.pushBytes(in, out);
                out.close();
                in.close();
                System.out.print(".");
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }


        System.out.println("");
        System.out.println("Payload has arrived. ");
    }

}
