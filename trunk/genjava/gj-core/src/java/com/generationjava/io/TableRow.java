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
package com.generationjava.io;

import java.util.Collection;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * A TableRow contains one or many objects.
 */
public class TableRow implements Iterator {

    private List     stringList;
    private int      width;
    private int[]    colsizes;
    private Iterator myIterator;
    private int      ptr;

    public TableRow(Object obj) {
        ptr = 0;
        if(obj != null) {
            stringList = new ArrayList();
            
            // convert all arrays to Collection
            if(obj instanceof Object[]) {
                obj = Arrays.asList( (Object[])obj );
            }
            
            if(obj instanceof Collection) {
                Collection coll = (Collection)obj;
                colsizes = new int[coll.size()];
                int i=0;
                Iterator iterator = coll.iterator();
                while(iterator.hasNext()) {
                    Object nested_obj = iterator.next();
                    String str = "";
                    if(nested_obj instanceof Object[]) {
                        str = arrayToString( (Object[])nested_obj );
                    } else {
                        str = ""+nested_obj;
                    }
                    stringList.add( str );
                    colsizes[i] = str.length();
                    i++;
                }
            } else {
                colsizes = new int[1];
                String str = obj.toString();
                colsizes[0] = str.length();
                stringList.add(str);
            }
            myIterator = stringList.iterator();
        }
    }

    public boolean hasNext() {
        if(myIterator != null) {
            return myIterator.hasNext();
        } else {
            return false;
        }
    }

    public void remove() {}
    
    public Object next() {
        if(myIterator != null) {
            ptr++;
            return myIterator.next();
        } else {
            // throw an exception???
            return null;
        }
    }

    public void reset() {
        myIterator = null;
    }
    
    public boolean firstColumn() {
        return (ptr==1);
    }
    
    public boolean lastColumn() {
        return (ptr==getWidth());
    }
    
    public int getColumnWidth() {
        if(ptr == 0) {
            return 0;
        } else {
            return colsizes[ptr-1];
        }
    }

    public int getWidth() {
        if(stringList == null) {
            return 0;
        } else {
            return stringList.size();
        }
    }
    
    public int[] getColumnSizes() {
        return colsizes;
    }

    private String arrayToString(Object[] arr) {
        if(arr == null) {
            return null;
        }
        /*
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        int sz = arr.length;
        for(int i=0;i<sz;i++) {
            buffer.append(""+arr[i]);
            if(i != sz-1) {
                buffer.append(", ");
            }
        }
        buffer.append("}");
        return buffer.toString();
        */
        return Arrays.asList(arr).toString();
    }    
}
