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

        // TODO: check arguments. There can be a -d to specify target dir
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
    }

    private static boolean interpolatable(String name) {
        return name.endsWith(".xml") || name.endsWith(".txt");
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

    private static String interpolate(String str, Properties props) {
        Iterator itr = props.keySet().iterator();
        while(itr.hasNext()) {
            String key = (String) itr.next();
            // switch away from regexp with Lang's replace method?
            str = str.replaceAll( "\\$\\{"+key+"\\}", props.getProperty(key) );
        }
        return str;
    }
}
