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
 * + Neither the name of Simple-JNDI nor the names of its contributors 
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

/// TODO: Refactor this out
package org.osjava.jndi.util;

import java.io.*;
import java.util.*;
import org.apache.commons.collections.IteratorUtils;
import com.generationjava.collections.OrderedSet;

/** 
 * Functionally like the CustomProperties class in that it has 
 * comments and an order, IniProperties reads .ini files. These
 * implicitly have a two level dotted notation, though any values 
 * not in the two level are treated as simple one levels. 
 * Comments are a semi-colon. 
 */
public class IniProperties extends Properties {

    public synchronized void load(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader(in) );
            String line = "";
            String block = "";
            while( (line = reader.readLine()) != null) {

                line = line.trim();   // important?? bad??

                // handle blocks
                if(line.startsWith("[") && line.endsWith("]")) {
                    block = line.substring(1, line.length()-1)+".";
                }

                int idx = line.indexOf(';');
                // remove comment
                if(idx != -1) {
                    line = line.substring(0,idx);
                }

                // split equals sign
                idx = line.indexOf('=');
                if(idx != -1) {
//                    System.err.println("Setting: "+line.substring(0,idx)+"="+line.substring(idx+1));
                    this.setProperty(block+line.substring(0,idx), line.substring(idx+1));
                } else {
                    // blank line, or just a bad line
                    // we ignore it
                }
            }
            reader.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // This is a copy of CustomProperties from here on in
    // in reality they should share

    public synchronized Object put(Object key, Object value) {
        if(index.contains(key)) {
            Object obj = get(key);
            if( !(obj instanceof List)) {
                List list = new LinkedList();
                list.add(obj);
                obj = list;
            } 
            ((List)obj).add(value);
            value = obj;
        }
        if(!index.contains(key)) {
//            System.err.println("Updating index for: "+key);
            index.add(key);
        }
//        System.err.println("Really setting: "+key+"="+value);
        return super.put(key, value);
    }


    // our index for the ordering
    protected ArrayList index = new ArrayList();

    public IniProperties() {
        super();
    }

    // the props attribute is for defaults. These will need to be 
    // remembered for the save/store method.
    public IniProperties(Properties props) {
        super(props);
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
