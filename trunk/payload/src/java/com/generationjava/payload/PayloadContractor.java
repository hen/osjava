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
        System.out.print(".");

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
        FileOutputStream fout = null;
        try {
            System.out.print("Contracting payload header");
            fin = new FileInputStream(new File(jarFile));
            fout = new FileOutputStream(new File(targetJar));

            // copy class file over
            System.out.println(".");
            // copy manifest with changed main-class
            System.out.println(".");

            // loop over every argument, handling recursion, 
            // and pushing into the payload/ directory
            System.out.print("Contracting payload body");
            System.out.println(".");

        } catch(IOException ioe) {
            System.err.println("\nIOException in building new jar. ");
            ioe.printStackTrace();
        } finally {
            closeQuietly(fin);
            closeQuietly(fout);
        }

        /*
        try {
            JarFile jar = new JarFile(new File(jarFile));
            Enumeration enum = jar.entries();
            while(enum.hasMoreElements()) {
                JarEntry entry = (JarEntry) enum.nextElement();
                if(!entry.getName().startsWith("payload")) {
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
                if( props != null && interpolatable(outName)) {
                    // interpolate push
                    String text = readToString(in);
                    text = interpolate(text, props);
                    in.close();
                    in = new ByteArrayInputStream(text.getBytes());
                }

                OutputStream out = new FileOutputStream( outFile );
                pushBytes(in, out);
                out.close();
                in.close();
                System.out.print(".");
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }


        System.out.println("");
        System.out.println("Payload has arrived. ");
        */
    }

    // util methods from here down
    private static void pushBytes(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1023];
        while(true) {
            int size = in.read(buffer);
            if(size == -1) {
                break;
            }
            out.write(buffer,0,size);
        }
    }

    private static String readToString(InputStream in) throws IOException {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while( (line = rdr.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    static void closeQuietly(InputStream in) {
        if(in != null) {
            try {
                in.close();
            } catch(IOException ioe) {
                // ignore
            }
        }
    }

    static void closeQuietly(OutputStream out) {
        if(out != null) {
            try {
                out.close();
            } catch(IOException ioe) {
                // ignore
            }
        }
    }

}
