package com.generationjava.payload;

import java.io.*;
import java.util.*;

// used to handle interpolation concepts, ie:
//  org.osjava.payload=true
//  org.osjava.payload.interpolate.endsWith=txt
//  org.osjava.payload.interpolate.endsWith=xml
//  org.osjava.payload.interpolate.matches=regexp
class Interpolation {

    static private String ENDS_WITH = "org.osjava.payload.interpolate.endsWith";
    static private String MATCHES = "org.osjava.payload.interpolate.matches";

    static public Interpolation DEFAULT = new Interpolation(
        "org.osjava.payload=true\n" +
        "org.osjava.payload.interpolate.endsWith=xml\n" +
        "org.osjava.payload.interpolate.endsWith=jcml\n" +
        "org.osjava.payload.interpolate.endsWith=properties\n" +
        "org.osjava.payload.interpolate.endsWith=txt\n" +
        "org.osjava.payload.interpolate.endsWith=conf\n");

    public List fileMatches;
    public List fileEndsWith;

    public Interpolation(String txt) {
        try {
            // parse
            BufferedReader rdr = new BufferedReader(new StringReader(txt));
            String line = "";
            this.fileMatches = new ArrayList();
            this.fileEndsWith = new ArrayList();
            while( (line = rdr.readLine()) != null) {
                if(line.startsWith(ENDS_WITH)) {
                    int idx = line.indexOf("=");
                    if(idx != -1) {
                        fileEndsWith.add(line.substring(idx+1));
                    }
                } else 
                if(line.startsWith(MATCHES)) {
                    int idx = line.indexOf("=");
                    if(idx != -1) {
                        fileMatches.add(line.substring(idx+1));
                    }
                }
            }
        } catch(IOException ioe) {
            // ? throw ParsingException?
            ioe.printStackTrace();
        }
    }

    public boolean interpolatable(String name) {
        Iterator itr = this.fileEndsWith.iterator();
        while(itr.hasNext()) {
            String substr = (String) itr.next();
            if(name.endsWith(substr)) {
                return true;
            }
        }
        itr = this.fileMatches.iterator();
        while(itr.hasNext()) {
            String pattern = (String) itr.next();
            if(name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public static String interpolate(String str, Properties props) {
        Iterator itr = props.keySet().iterator();
        while(itr.hasNext()) {
            String key = (String) itr.next();
            // switch away from regexp with Lang's replace method?
            str = str.replaceAll( "\\$\\{"+key+"\\}", props.getProperty(key) );
        }
        return str;
    }
}
