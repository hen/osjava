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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * used to handle interpolation concepts, ie:
 *   org.osjava.payload=true
 *   org.osjava.payload.interpolate.endsWith=txt
 *   org.osjava.payload.interpolate.endsWith=xml
 *   org.osjava.payload.interpolate.matches=regexp
 */
class Interpolation {

    private static final String ENDS_WITH = "org.osjava.payload.interpolate.endsWith";
    private static final String MATCHES = "org.osjava.payload.interpolate.matches";

    public static final Interpolation DEFAULT = new Interpolation(
        "org.osjava.payload=true\n" +
        "org.osjava.payload.interpolate.endsWith=xml\n" +
        "org.osjava.payload.interpolate.endsWith=jcml\n" +
        "org.osjava.payload.interpolate.endsWith=properties\n" +
        "org.osjava.payload.interpolate.endsWith=txt\n" +
        "org.osjava.payload.interpolate.endsWith=conf\n");

    private List fileMatches;
    private List fileEndsWith;

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
            str = str.replaceAll( "\\$\\{"+key+"\\}", props.getProperty(key) );
        }
        return str;
    }
}
