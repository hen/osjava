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
package org.osjava.payload;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.util.Enumeration;
import java.util.Properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;

import java.util.zip.*;

/**
 * Extracts itself from the jar it is in, assuming it is run with java -jar.
 */
public class PayloadExtractor {

    public static final boolean DEBUG = (System.getProperty("PAYLOAD.DEBUG")!=null);

    public static void main(String[] args) {
if(DEBUG) System.out.println("DEBUG turned on. ");
        System.out.print("Payload extraction setup");

        // when run with -jar, the class path is the jar file
        String jarFile = System.getProperty("java.class.path");
        if(jarFile.indexOf(":") != -1) {
            jarFile = null;
            // get the jarFile as a -j argument
        }
        String jarName = jarFile.substring( 0, jarFile.length() - ".jar".length() );
        System.out.print(".");

        Properties props = null;
        if(args.length == 0) {
            System.err.println("\nNo properties file specified, will output without interpolation. ");
        } else {
            for(int i=0; i<args.length; i++) {
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(new File(args[i]));
                    if(props == null) {
                        props = new Properties();
                    }
                    props.load(fin);
if(DEBUG) System.out.println("\n"+args[i]+" being used as interpolation values. ");
if(DEBUG) System.out.println(props.toString());
                } catch(IOException ioe) {
                    System.err.println("\nUnable to find properties file, will skip it. ");
                } finally {
                    if(fin != null) {
                        try {
                            fin.close();
                        } catch(IOException ioe) {
                            ; // ignore
                        }
                    }
                }
            }
        }
        System.out.println(".");

        // loop....
        try {
            JarFile jar = new JarFile(new File(jarFile));

            Interpolation interpolation = null;

            // need to find a way to ensure the interpolation is read 
            // first. possibly scan through the zip first?
            Enumeration enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if(entry.getName().equals("payload.properties")) {
if(DEBUG) System.out.println("Custom interpolation being used. ");
                    InputStream in = jar.getInputStream( entry );
                    String text = IOUtils.readToString(in);
                    interpolation = new Interpolation(text);
                    break;
                }
            }

            if(interpolation == null) {
if(DEBUG) System.out.println("Default interpolation being used. ");
                interpolation = Interpolation.DEFAULT;
            }

            System.out.print("Payload extracting");

            enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if(!entry.getName().startsWith("payload")) {
                    continue;
                }
                if(entry.getName().equals("payload.properties")) {
                    continue;
                }
                // remove payload/
                String inName = entry.getName().substring("payload/".length());
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
                boolean interpolated = false;
                if( props != null && interpolation.interpolatable(outName)) {
                    // interpolate push
if(DEBUG) System.out.println("Interpolating "+outName);
                    String text = IOUtils.readToString(in);
                    text = interpolation.interpolate(text, props);
                    in.close();
                    in = new ByteArrayInputStream(text.getBytes());
                    interpolated = true;
                }

                boolean interpolateArchive = false;

                // if an archive, then interpolate in the archive
                // TODO: Make this configurable
                if(props != null && interpolation.interpolatableArchive(outName)) {
                    // first pass. See if any interpolatables
                    // if so, then flag this for a zip-handler
                    byte[] bytes = IOUtils.readToBytes(in);
                    ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(bytes));
                    ZipEntry zEntry = null;
                    while( (zEntry = zin.getNextEntry()) != null) {
                        if(interpolation.interpolatable(zEntry.getName())) {
                            interpolateArchive = true;
                            break;
                        }
                    }
                    zin.close();
                    in = new ByteArrayInputStream(bytes);

                }

                if(interpolateArchive) {
                    FileOutputStream out = new FileOutputStream( outFile );
                    interpolateArchive(out, in, interpolation, props);
                    out.close();
                    System.out.print("#");
                } else {
                    OutputStream out = new FileOutputStream( outFile );
                    IOUtils.pushBytes(in, out);
                    out.close();
                    in.close();
                    if(interpolated) {
                        System.out.print("$");
                    } else {
                        System.out.print(".");
                    }
                }

            }
        } catch(IOException ioe) { ioe.printStackTrace(); }


        System.out.println("");
        System.out.println("Payload has arrived. ");
    }


    private static void interpolateArchive(OutputStream out, InputStream in, Interpolation interpolation, Properties props) throws IOException {
        ZipOutputStream zout = new ZipOutputStream(out);
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry zEntry = null;
        InputStream tmpin = null;
        while( (zEntry = zin.getNextEntry()) != null) {
            tmpin = zin;
            long size = zEntry.getSize();
            long crc = zEntry.getCrc();
            if(props != null && interpolation.interpolatable(zEntry.getName())) {
if(DEBUG) System.out.println("Interpolating in archive");
                String text = IOUtils.readToString(zin);
                text = interpolation.interpolate(text, props);
                tmpin = new ByteArrayInputStream(text.getBytes());
                size = text.getBytes().length;
                CRC32 crc32 = new CRC32();
                crc32.update(text.getBytes());
                crc = crc32.getValue();
            }
            // if interpolatable archive, then recurse.....
            if(props != null && interpolation.interpolatableArchive(zEntry.getName())) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.pushBytes(zin, baos);
                tmpin = new ByteArrayInputStream( baos.toByteArray() );
                baos = new ByteArrayOutputStream();
if(DEBUG) System.out.println("Recursing into sub-archive");
                interpolateArchive(baos, tmpin, interpolation, props);
                tmpin = new ByteArrayInputStream( baos.toByteArray() );
                size = baos.toByteArray().length;
                CRC32 crc32 = new CRC32();
                crc32.update(baos.toByteArray());
                crc = crc32.getValue();
            }

            ZipEntry newEntry = new ZipEntry(zEntry.getName());
            if(zEntry.getComment() != null) {
                newEntry.setComment(zEntry.getComment());
            }
            if(zEntry.getExtra() != null) {
                newEntry.setExtra(zEntry.getExtra());
            }
            if(zEntry.getTime() != -1) {
                newEntry.setTime(zEntry.getTime());
            }
            if(zEntry.getMethod() != -1) {
                newEntry.setMethod(zEntry.getMethod());
                zout.setMethod(zEntry.getMethod());
            }
            if(crc != -1) {
                newEntry.setCrc(crc);
            }
            if(zEntry.getSize() != -1) {
                newEntry.setSize(size);
            }
            /* Oddly fails, unsure why. 
            if(zEntry.getCompressedSize() != -1 && size == zEntry.getSize()) {
                newEntry.setCompressedSize(zEntry.getCompressedSize());
            }
            */
            zout.putNextEntry(newEntry);

            IOUtils.pushBytes(tmpin, zout);
            zin.closeEntry();
            zout.closeEntry();
        }
        zout.finish();
        zin.close();
    }

}
