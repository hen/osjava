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
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a table of information.
 * A Table consists of a Collection of TableRows.
 *
 * Some dubious design with regards to the iteratorness.
 */
public class Table implements Iterator {
    
    private List     table;
    private int      width;
    private int[]    colwidths;
    private int      totalwidth;
    private int      ptr;        // current iteration index
    private Iterator myIterator;

    /**
     * An empty table.
     */
    public Table() {
        table = new ArrayList();
        width = 0;
        totalwidth = -1;
        ptr = 0;        
        colwidths = new int[0];
    }

    /**
     * Build a table from a Collection of Objects.
     * The Objects may be Collections themselves.
     */
    public Table(Collection coll2d) {
        this();
        colwidths = new int[coll2d.size()];
        for(int i=0;i<colwidths.length;i++) {
            colwidths[i]=0;
        }
        
        Iterator iterator = coll2d.iterator();
        while(iterator.hasNext()) {
            addRow(iterator.next());
        }
    }
    
    /**
     * Add a Row.
     */
    public void addRow(Object obj) {    
        TableRow row = new TableRow(obj);
        table.add(row);
        int wd = row.getWidth();
        if(wd > width) {
            width = wd;
        }
        int[] wds = row.getColumnSizes();
        
        if(wds.length > colwidths.length) {
            // expand the array.
            int[] old = colwidths;
            colwidths = new int[wds.length];
            System.arraycopy(old,0,colwidths,0,old.length);
            for(int i=colwidths.length;i<wds.length;i++) {
                colwidths[i]=0;
            }
        }
        
        for(int i=0;i<wds.length;i++) {
            if(wds[i] > colwidths[i]) {
                colwidths[i]=wds[i];
            }
        }
    }
    
    /**
     * Used to iterate over the table.
     */
    public boolean hasNext() {
        if(myIterator == null) {
            myIterator = table.iterator();
        }
        return myIterator.hasNext();
    }
    
    /**
     * Get the next value from the table.
     */
    public Object next() {
        if(myIterator == null) {
            myIterator = table.iterator();
        }
        ptr++;
        return myIterator.next();
    }

    /**
     * Reset the iterator.
     */
    public void reset() {
        myIterator = null;
    }
    
    /**
     * Iterator interface.
     * Unimplemented.
     */
    public void remove() {}

    /**
     * Are we currently on the first row?
     */
    public boolean firstRow() {
        return (ptr<2);
    }
    
    /**
     * Are we on the last row?
     */
    public boolean lastRow() {
        return (ptr==getNumberOfRows());
    }
    
    /**
     * Number of rows in this table.
     */
    public int getNumberOfRows() {
        return table.size();
    }
    
    /**
     * Number of columns in this table.
     */
    public int getNumberOfColumns() {
        return width;
    }
    
    /**
     * Get the width of all the columns added together.
     */
    public int getWidth() {
        if(totalwidth == -1) {
            totalwidth = 0;
            for(int i=0;i<colwidths.length;i++) {
                totalwidth += colwidths[i];
            }
        }
        return totalwidth;
    }
    
    /**
     * Get the widths of each column in an array.
     */
    public int[] getColumnWidths() {
        return colwidths;
    }

}
