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
// OrderedProperties.java
package com.generationjava.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;
import java.util.Enumeration;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;

import com.generationjava.collections.OrderedSet;

/**
 * java.util.Properties stores keys and values, but it does not store them in 
 * an ordered way. Annoyingly, there is no easy way to splice this feature in, 
 * rather it leads to a rewrite of most of the methods and the creation.
 * This is that rewrite.
 */
public class OrderedProperties extends Properties {

    static public OrderedProperties load(File file) {
        OrderedProperties props = new OrderedProperties();
        try {
            BufferedReader reader = new BufferedReader( new FileReader(file) );
            String line = "";
            while( (line = reader.readLine()) != null) {
                int idx = line.indexOf('#');
                // remove comment
                if(idx != -1) {
                    line = line.substring(0,idx);
                }
                // split equals sign
                idx = line.indexOf('=');
                if(idx != -1) {
                    props.setProperty(line.substring(0,idx), line.substring(idx+1));
                }
            }
            reader.close();
            return props;
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return props;
        }
    }

    // our index for the ordering
    protected ArrayList index = new ArrayList();

    public OrderedProperties() {
        super();
    }

    // the props attribute is for defaults. These will need to be 
    // remembered for the save/store method.
    public OrderedProperties(Properties props) {
        super(props);
    }

    public synchronized Object put(Object key, Object value) {
        if(!index.contains(key)) {
            index.add(key);
        }
        return super.put(key,value);
    }
    
    public synchronized Object setProperty(String key, String value) {
        return put(key,value);
    }
    
    public synchronized Object remove(Object key) {
        index.remove(key);
        return super.remove(key);
    }
    
    // simple implementation that depends on keySet.
    public synchronized Enumeration propertyNames() {
        return IteratorUtils.asEnumeration(keySet().iterator());
    }
    public synchronized Enumeration keys() {
        return propertyNames();
    }
    
    public synchronized Set keySet() {
        return new OrderedSet(index);
    }
 
    /**
     * Currently will write out defaults as well, which is not 
     * in the specification.
     */
    public void save(OutputStream outstrm, String header) {
        super.save(outstrm,header);
    }
    /**
     * Currently will write out defaults as well, which is not 
     * in the specification.
     */
    public void store(OutputStream outstrm, String header) throws IOException {
        super.store(outstrm,header);
    }

}
