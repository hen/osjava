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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.NumberUtils;

/// TODO: Have it do ConvertUtils stuff. "1:,2%,3$" etc.
public class FixedWidthReader {
 
    private BufferedReader rdr;
    private int[] widths;
    private String trim;
    private int minWidth;
    private boolean hasGlob;
 
    public FixedWidthReader(Reader rdr) {
        if( !(rdr instanceof BufferedReader) ) {
            rdr = new BufferedReader(rdr);
        }
        this.rdr = (BufferedReader)rdr;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
        calcSizeCondition();
    }

 

    public void setWidths(String widths) {
        String[] nums = StringUtils.split(widths, ",");
        int sz = nums.length;
        this.widths = new int[sz];

        for(int i=0; i<sz; i++) {
            if("*".equals(nums[i])) {
                this.widths[i] = 0;
            } else {
                this.widths[i] = NumberUtils.stringToInt(nums[i]);
            }
        }
        calcSizeCondition();
    }

    private void calcSizeCondition() {
        if(this.widths != null) {
            this.hasGlob = false;
            for(int i=0; i<this.widths.length; i++) {
                 if(this.widths[i] == 0) {
                     this.hasGlob = true;
                 } else {
                     this.minWidth += this.widths[i];
                 }
            }
        }
    }

    public void setTrim(String trim) {
        this.trim = trim;
    }

    public Object[] readLine() throws IOException {

        String line = this.rdr.readLine();
        if(line == null) {
            return null;
        }
        if(line.length() < this.minWidth) {
            return null;
        }
        if(!this.hasGlob) {
            if(line.length() > this.minWidth) {
                return null;
            }
        }

 
        int sz = widths.length;
        String[] fields = new String[widths.length];
 
        boolean reverse = false;
        int idx = 0;
        int i = 0;      // we hang on to this in case it's needed in the j loop

        for(i=0; i<sz; i++) {
            if(this.widths[i] == 0) {
                // it's stretchy. we break and do a reverse loop
                reverse = true;
                break;
            }

            fields[i] = line.substring(idx, idx + this.widths[i]);
            idx += this.widths[i];
        }
        if(reverse) {
            int rev = line.length();
            for(int j=sz-1; j>i; j--) {
                rev -= this.widths[j];
                fields[j] = line.substring(rev, rev+this.widths[j]);
            }
            fields[i] = line.substring(idx, rev);
        }

        if(this.trim != null) {
            for(int index=0; index<sz; index++) {
                fields[index] = StringUtils.strip(fields[index], this.trim);
            }
        }

        // do conversion here? or integrate into loops above..
        // refactor: extract method

        return fields;
    }

}

 


