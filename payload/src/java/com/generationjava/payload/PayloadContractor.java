package com.generationjava.payload;

import java.util.jar.*;

import java.util.*;

import java.io.*;

public class PayloadContractor {

// Allow payload/ to be specified as a system property

    public static void main(String[] args) {
        System.out.print("Payload contraction setup");

        // when run with -jar, the class path is the jar file
        String jarFile = System.getProperty("java.class.path");
        if(jarFile.indexOf(":") != -1) {
            jarFile = null;
            // get the jarFile as a -j argument
        }
        String jarName = jarFile.substring( 0, jarFile.length() - 4 );
        System.out.println(".");

        if(args.length == 0) {
            System.err.println("\nUnable to contract a payload as no target jar has been specified. ");
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
            Enumeration enum = jar.entries();
            while(enum.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum.nextElement();
                InputStream in = jar.getInputStream( entry );
                JarEntry entry2 = new JarEntry(entry.getName());
                entry2.setTime(entry.getTime());
                entry2.setSize(entry.getSize());
                jout.putNextEntry(entry2);
                if(entry.getName().equals("META-INF/MANIFEST.MF")) {
                    String mf = IOUtils.readToString(in);
                    mf = mf.replaceAll("Main-Class: com\\.generationjava\\.payload\\.PayloadContractor", "Main-Class: com.generationjava.payload.PayloadExtractor");
                    jout.write(mf.getBytes());
                } else {
                    IOUtils.pushBytes(in, jout);
                }
                jout.closeEntry();
                System.out.print(".");
            }
            System.out.println("");

            // loop over every argument, handling recursion, 
            // and pushing into the payload/ directory
            System.out.print("Contracting payload body");
            for(int i=1; i<args.length; i++) {
                String filename = args[i];
                File file = new File(filename);
                if(file.isDirectory()) {
                    // recurse, ignore as we'll move to FileFinder
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    JarEntry entry = new JarEntry("payload/"+filename);
                    // read time of file
                    entry.setTime(System.currentTimeMillis());
// ???                    entry.setSize();
                    jout.putNextEntry(entry);
                    IOUtils.pushBytes(fis, jout);
                    jout.closeEntry();
                }
                System.out.print(".");
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

}
