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
// Csv.java
package com.generationjava.io;

import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

public class Csv {

    static public char FIELD_DELIMITER = ',';
    static public char BLOCK_DELIMITER = '\n';

    private LinkedList list = new LinkedList();
    private String[] headers;

    public Csv(CsvReader reader) throws IOException {
        this.headers = reader.readLine();

        String[] line = null;

        // suck in the file.
        // TODO: What's the optimal structure for this
        while( (line = reader.readLine()) != null) {
            list.add(line);
        }
    }

    // get all of the unique fields for this header
    public String[] getAll(String header) {
        HashSet values = new HashSet();
        int idx = getHeaderIndex(header);
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) {
            values.add( ((String[])iterator.next())[idx]);
        }
        return (String[])values.toArray(new String[0]);
    }

    private int getHeaderIndex(String header) {
        for(int i=0;i<this.headers.length;i++) {
            if(header.equals(this.headers[i])) {
                return i;
            }
        }
        return -1;
    }

    public String[] get(String header, String subheader, String value) {
        HashSet values = new HashSet();
        int idx = getHeaderIndex(header);
        int subidx = getHeaderIndex(subheader);
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) {
            String[] strs = (String[])iterator.next();
            if(value.equals(strs[idx])) {
                values.add( strs[subidx] );
            }
        }
        return (String[])values.toArray(new String[0]);
    }

    public String[] get(String header, Properties context) {
        // optimisation
        if(context.contains(header)) {
            String[] ret = new String[1];
            ret[1] = context.getProperty(header);
            return ret;
        }

        HashSet values = new HashSet();
        int idx = getHeaderIndex(header);
        Iterator iterator = list.iterator();
        Set keys = context.keySet();
LABEL:  while(iterator.hasNext()) {
            // get next csv row
            String[] strs = (String[])iterator.next();

            // check that this row is in context
            Iterator keysIterator = keys.iterator();
            while(keysIterator.hasNext()) {
                String key = (String)keysIterator.next();
                String value = context.getProperty(key);
                int hdrIndex = getHeaderIndex(key);
                if(!value.equals(strs[hdrIndex])) {
                    continue LABEL;
                }
            }

            values.add(strs[idx]);
        }

        return (String[])values.toArray(new String[0]);
    }

}
