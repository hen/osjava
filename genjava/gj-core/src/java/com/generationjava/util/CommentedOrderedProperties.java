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
// CommentedOrderedProperties.java
package com.generationjava.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;
import java.util.Enumeration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;
import com.generationjava.collections.OrderedSet;
import com.generationjava.lang.StringW;

import org.apache.commons.lang.StringUtils;


/**
 * java.util.Properties stores keys and values, but it does not store them in 
 * an ordered way. Annoyingly, there is no easy way to splice this feature in, 
 * rather it leads to a rewrite of most of the methods and the creation.
 * This is that rewrite.
 */
public class CommentedOrderedProperties extends OrderedProperties {

    static public CommentedOrderedProperties createCommentedOrderedProperties(File file) {
        CommentedOrderedProperties props = new CommentedOrderedProperties();
        try {
            BufferedReader reader = new BufferedReader( new FileReader(file) );
            String line = "";
            String comment;
            boolean found;
            while( (line = reader.readLine()) != null) {
                int idx = line.indexOf('#');
                comment = null;
                found = false;
                // remove comment
                if(idx != -1) {
                    comment = line.substring(idx + 1);
                    line = line.substring(0,idx);
                    found = true;
                }
                // split equals sign
                idx = line.indexOf('=');
                if(idx != -1) {
                    props.setProperty(line.substring(0,idx), line.substring(idx+1));
                    found = true;
                }
                if(comment != null) {
                    props.addComment(comment);
                    found = true;
                }

                if(!found) {
                    props.addSeparator();
                }
            }
            reader.close();
            return props;
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return props;
        }
    }

    // comment character to use. 99% of the time is #, though ! is also legal
    private char commentChar = '#';

    public CommentedOrderedProperties() {
        super();
    }

    // the props attribute is for defaults. These will need to be 
    // remembered for the save/store method.
    public CommentedOrderedProperties(Properties props) {
        super(props);
    }

    public synchronized Set keySet() {
        Iterator iterator = index.iterator();
        Set set = new OrderedSet();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            if(get(key) != null) {
                set.add(key);
            }
        }
        return set;
    }
 
    /**
     * Currently will write out defaults as well, which is not 
     * in the specification.
     */
    public void save(OutputStream outstrm, String header) {
        try {
            store(outstrm,header);
        } catch(IOException ioe) {
            // ignore as that's the API :(
        }
    }
    /**
     * Currently will write out defaults as well, which is not 
     * in the specification.
     */
    public void store(OutputStream outstrm, String header) throws IOException {
        Writer writer = new OutputStreamWriter(outstrm);
        writer.write("#");
        writer.write(header);
        writer.write("\n");
        writer.write("#");
        writer.write(new Date().toString());
        writer.write("\n");
        Iterator iterator = index.iterator();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            if(get(key) == null) {
                writer.write(key.toString());
            } else {
                writer.write(key.toString());
                writer.write("=");
                writer.write(get(key).toString());
            }
            writer.write("\n");
        }
        writer.close();
    }


    // set a comment character. should be an ! or a #
    public void setCommentChar(char ch) {
        this.commentChar = ch;
    }
    public char getCommentChar() {
        return this.commentChar;
    }
    public void addComment(String str) {
        addComment(this.commentChar, str);
    }
    public void addComment(char ch, String str) {
        index.add(""+ch+" "+str);
    }
    // similar wording to awt.Menu basically a blank line
    public void addSeparator() {
        index.add("");
    }
    public void wordWrapComment(char ch, String str, int width) {
        str = StringW.wordWrap(str, width - 2);
        String[] strs = StringUtils.split(str, "\n");
        int sz = strs.length;
        for(int i=0; i<sz; i++) {
            addComment(ch, strs[i]);
        }
    }
    public void insertComment(char ch, String str, Object key) {
        int idx = index.indexOf(key);
        if(idx == -1) {
            addComment(ch, str);
        } else {
            index.add(idx, ""+ch+" "+str);
        }
    }
}
