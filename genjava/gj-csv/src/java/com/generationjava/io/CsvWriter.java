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

import java.io.IOException;
import java.io.Writer;

//import org.apache.commons.lang.StringUtils;

public class CsvWriter {

    private char field_delim = Csv.FIELD_DELIMITER;
    private char block_delim = Csv.BLOCK_DELIMITER;

    private Writer writer;
    private boolean written;

    public CsvWriter(Writer wtr) {
        this.writer = wtr;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void setFieldDelimiter(char ch) {
        field_delim = ch;
    }

    public void setBlockDelimiter(char ch) {
        block_delim = ch;
    }

    public void writeField(String field) throws IOException {
        if(written) {
            writer.write(field_delim);
            written = false;
        }

        if(field == null) {
            field = "";
        }

        int idx = field.indexOf(field_delim);
        if(idx != -1) {
            field = "\""+replace(field,"\"","\"\"")+"\"";
        }

        writer.write(field);
        written = true;
    }

    public void endBlock() throws IOException {
        writer.write(block_delim);
        written = false;
    }

    public void writeLine(String[] strs) throws IOException {
        int sz = strs.length;
        for(int i=0;i<sz;i++) {
            writeField(strs[i]);
        }
        endBlock();
    }

    public void writeField(Object obj) throws IOException {
        if(obj == null) {
            writeField("");
        } else {
            writeField(obj.toString());
        }
    }

    public void writeLine(Object[] objs) throws IOException {
        int size = objs.length;
        String[] strs = new String[size];
        for(int i=0; i<size; i++) {
            if(objs[i] == null) {
                strs[i] = "";
            } else {
                strs[i] = objs[i].toString();
            }
        }
        writeLine(strs);
    }

    public void close() throws IOException {
        this.writer.close();
    }

    // from Commons.Lang.StringUtils
    private static String replace(String text, String repl, String with) {   
        int max = -1;
        if (text == null || repl == null || with == null || repl.length() == 0 || max == 0) {
            return text;
        }
        
        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
            
            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

}
