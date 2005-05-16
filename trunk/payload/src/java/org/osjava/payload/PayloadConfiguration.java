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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Holds the payload.properties file. 
 * Said file relies on order and allows duplicates so is not 
 * a standard Properties file, nor wants to be accessed 
 * via that API.
 *
 *   org.osjava.payload=true
 *   org.osjava.payload.paylet=com.example.ExamplePaylet arg1 arg2
 */
public class PayloadConfiguration {

    private static final String PAYLET = "org.osjava.payload.paylet";
    private static final String ENDS_WITH = "org.osjava.payload.interpolate.endsWith";
    private static final String MATCHES = "org.osjava.payload.interpolate.matches";
    private static final String ARCHIVE_ENDS_WITH = "org.osjava.payload.interpolate.archive.endsWith";
    private static final String ARCHIVE_MATCHES = "org.osjava.payload.interpolate.archive.matches";

    public static final PayloadConfiguration DEFAULT = new PayloadConfiguration( 
        "org.osjava.payload=true\n" +
        "org.osjava.payload.interpolate.endsWith=xml\n" +
        "org.osjava.payload.interpolate.endsWith=jcml\n" +
        "org.osjava.payload.interpolate.endsWith=properties\n" +
        "org.osjava.payload.interpolate.endsWith=txt\n" +
        "org.osjava.payload.interpolate.endsWith=conf\n"
    );

    private List fileMatches = new ArrayList();
    private List fileEndsWith = new ArrayList();
    private List archiveEndsWith = new ArrayList();
    private List archiveMatches = new ArrayList();
    private List paylets = new ArrayList();

    public PayloadConfiguration(String txt) {

        try {

            BufferedReader rdr = new BufferedReader(new StringReader(txt));
            String line = "";

            while( (line = rdr.readLine()) != null) {
                if(line.startsWith(ENDS_WITH)) {
                    int idx = line.indexOf("=");
                    if(idx != -1) {
if(PayloadExtractor.DEBUG) System.out.println("Adding endsWith rule: "+line.substring(idx+1));
                        this.fileEndsWith.add(line.substring(idx+1));
                    }
                }
    
                if(line.startsWith(ARCHIVE_ENDS_WITH)) {
                    int idx = line.indexOf("=");
                    if(idx != -1) {
if(PayloadExtractor.DEBUG) System.out.println("Adding archiveEndsWith rule: "+line.substring(idx+1));
                        this.archiveEndsWith.add(line.substring(idx+1));
                    }
                }
    
                if(line.startsWith(MATCHES)) {
                    int idx = line.indexOf("=");
                    if(idx != -1) {
if(PayloadExtractor.DEBUG) System.out.println("Adding matches rule: "+line.substring(idx+1));
                        this.fileMatches.add(line.substring(idx+1));
                    }
                }
    
                if(line.startsWith(ARCHIVE_MATCHES)) {
                    int idx = line.indexOf("=");
                    if(idx != -1) {
if(PayloadExtractor.DEBUG) System.out.println("Adding archiveMatches rule: "+line.substring(idx+1));
                        this.archiveMatches.add(line.substring(idx+1));
                    }
                }
    
                try {
                    if(line.startsWith(PAYLET)) {
                        int idx = line.indexOf("=");
                        if(idx != -1) {
if(PayloadExtractor.DEBUG) System.out.println("Loading paylet: "+line.substring(idx+1));
                            String[] words = line.substring(idx+1).split(" ");
                            Paylet paylet = (Paylet) Class.forName(words[0]).newInstance();
                            String[] args = new String[words.length - 1];
                            System.arraycopy( words, 1, args, 0, args.length );
                            paylet.setArgs(args);
                            this.paylets.add( paylet );
                        }
                    }
                } catch(ClassNotFoundException cnfe) {
                    // ? throw ParsingException?
                    cnfe.printStackTrace();
                } catch(InstantiationException ie) {
                    // ? throw ParsingException?
                    ie.printStackTrace();
                } catch(IllegalAccessException iae) {
                    // ? throw ParsingException?
                    iae.printStackTrace();
                }
            }
        } catch(IOException ioe) {
            // ? throw ParsingException?
            ioe.printStackTrace();
        }
    }

    public List getArchiveEndsWith() { return this.archiveEndsWith; }
    public List getArchiveMatches() { return this.archiveMatches; }
    public List getFileEndsWith() { return this.fileEndsWith; }
    public List getFileMatches() { return this.fileMatches; }
    public List getPaylets() { return this.paylets; }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("PayloadConfiguration: ");
        buffer.append("archiveEndsWith: ");
        buffer.append(this.archiveEndsWith);
        buffer.append(", archiveMatches: ");
        buffer.append(this.archiveMatches);
        buffer.append(", fileEndsWith: ");
        buffer.append(this.fileEndsWith);
        buffer.append(", fileMatches: ");
        buffer.append(this.fileMatches);
        buffer.append(", paylets: ");
        buffer.append(this.paylets);

        return buffer.toString();
    }

}
