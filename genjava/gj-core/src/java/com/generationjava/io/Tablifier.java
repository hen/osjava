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

/*
   TableHandler. Give it a data structure. Should have either Array of Array or 
   add an Array at a time. In the latter case, it should be told of column sizes. 
   In the former it may work them out for itself.
*/
public class Tablifier {

    private String   rowSep = "\n";
    private String   colSep = " ";
    private String[] header;
    private String[] footer;
    private int      width;
    private int      numCols;
    private int[]    columnLocations;

    public Tablifier() {
    }

    // needs to space things out.
    public String tablify(Collection coll2d) {
        if(coll2d == null) {
            return null;
        }
        Table table = new Table(coll2d);
        return tablify(table);
    }
        
    public String tablify(Table table) {    
        
        StringBuffer buffer = new StringBuffer();
        
        int[] columnwidths = table.getColumnWidths();
        columnLocations = evaluateColumnLocations(columnwidths);
        width = table.getWidth();
        numCols = table.getNumberOfColumns();

        //printTop(buffer);
        // print header
        String[] header = getHeader();
        if(header != null) {
          //  printRow(buffer,header);
          //  printLine(buffer);
        }
        
        while(table.hasNext()) {
            
            TableRow row = (TableRow)table.next();
            
            int idx = 0;
            buffer.append(getVerticalBorder());
            while(row.hasNext()) {
                String value = (String)row.next();
                if(!row.firstColumn()) {
                    buffer.append(getColumnSeparator());
                }
                buffer.append( pad(value,columnwidths[idx],getColumnSeparator()) );
                idx++;
                if(!row.lastColumn()) {
                    buffer.append(getVerticalBorder());
                }
            }
            buffer.append(getVerticalBorder());
            buffer.append(getRowSeparator());        
        }
        
        // print footer
        String[] footer = getFooter();
        if(footer != null) {
           // printLine(buffer);
           // printRow(buffer,footer);
        }
        //printBottom(buffer);
        
        return buffer.toString();
    }

    //public void printLine(StringBuffer buffer) {
    //}
    //public void printRow(StringBuffer buffer, String[] data) {
    // or should it be passed a TableRow?
    //}

    public int[] evaluateColumnLocations(int[] columnwidths) {
        String vs = getVerticalBorder();
        int vsln = vs.length();
        if(vsln == 0) {
            return null;
        }
        int sz = getPrintedWidth();
        int count = 0;
        int[] cols = new int[sz+1];
        cols[0] = 0;
        for(int i=0;i<sz;i++) {
            count += vsln;
            count += columnwidths[i];
            cols[i+1] = count;
        }
        return cols;
    }

    public String pad(String value, int width, String chr) {
        while(width > value.length()) {
            value = chr+value;
        }
        return value;
    }
    
    public int getPrintedWidth() {
        int sz = width;
        sz += getVerticalBorder().length()*(numCols+1);
        sz += getColumnSeparator().length()*(numCols-1);
        return sz;    
    }
    public void printBottom(StringBuffer buffer) {
        printTop(buffer);
    }
    public void printTop(StringBuffer buffer) {
        int count = 1;
        int sz = getPrintedWidth();
        for(int i=0;i<sz;i++) {
            if( (i==0) || (i==sz-1) ) {
                if(getColumnSeparator().length() > 0) {
                    buffer.append(getCornerBorder());
                    continue;
                }
            } else
            if(columnLocations != null) {
                if(columnLocations[count] == i) {
                    buffer.append(getIntersectionBorder());
                    count++;
                    continue;
                }
            }
            
            buffer.append(getHorizontalBorder());
        }
        buffer.append(getRowSeparator());
    }
    
    //public void setHeight           // How many rows can fit in??
    //public void setWidth            // How many columns can squeeze in.
    //public void setDrawTable        // uses chars in above
    
    public String getHorizontalBorder() {
        return "-";
    }
    public String getIntersectionBorder() {
        return "+";
    }
    public String getCornerBorder() {
        return ".";
    }
    public String getVerticalBorder() {
        return "|";
    }
    
    public void setHeader(String[] strs) {
        header = strs;
    }
    public void setFooter(String[] strs) {
        footer = strs;
    }
    public void setRowSeparator(String sep) {
        rowSep = sep;
    }
    public void setColumnSeparator(String sep) {
        colSep = sep;
    }
    
    public String getRowSeparator() {
        return rowSep;
    }
    public String getColumnSeparator() {
        return colSep;
    }
    public String[] getHeader() {
        return header;
    }
    public String[] getFooter() {
        return footer;
    }
}
