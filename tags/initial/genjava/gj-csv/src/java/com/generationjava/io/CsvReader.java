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

import java.io.Reader;
import java.io.IOException;

import java.util.ArrayList;

// TODO: Add consuming so that log files may be read.
//       these are flies in which the delimiter is any number of 
//       a certain character. ie) space, tab etc.

// alternative: Allow delimiter to be larger than one char.
// still doesnt really help though. Could use Regexps?

// maybe a second class. RegexpBlockReader....

public class CsvReader {

    // new String to stop internning
    static public String END_OF_LINE = new String("END_OF_LINE");

    private char field_delim = Csv.FIELD_DELIMITER;
    private char block_delim = Csv.BLOCK_DELIMITER;

    private Reader reader;
    private boolean newline;

    // should   bbb,,,ccc be considered to be two elements?
    // useful for log parsing.
    private boolean consume;

    public CsvReader(Reader rdr) {
        this.reader = rdr;
    }

    public void setFieldDelimiter(char ch) {
        field_delim = ch;
    }

    public void setBlockDelimiter(char ch) {
        block_delim = ch;
    }

    public void setConsuming(boolean b) {
        this.consume = b;
    }

    public boolean isConsuming() {
        return this.consume;
    }

    public String[] readLine() throws IOException {
        ArrayList list = new ArrayList();
        String str;

        while( true ) {
            str = readField();
            if(str == null) {
                break;
            }
            if(str == END_OF_LINE) {
                break;
            }
            list.add(str);
        }

        if(list.isEmpty()) {
            return null;
        }

        return (String[])list.toArray(new String[0]);
    }

    public String readField() throws IOException {
        if(this.newline) {
            this.newline = false;
            return END_OF_LINE;
        }

        StringBuffer buffer = new StringBuffer();
        boolean quoted = false;
        int last = -1;
        int ch = this.reader.read();

        if(ch == -1) {
            return null;
        }

        if(ch == '"') {
            quoted = true;
        } else 
        if(ch == block_delim) {
            return END_OF_LINE;
        } else
        if(ch == field_delim) {
            return "";
        } else {
            buffer.append((char)ch);
        }

        while( (ch = this.reader.read()) != -1) {
            if(ch == block_delim) {
                this.newline = true;
                break;
            }
            if(quoted) {
                if(ch == '"') {
                    if(last == '"') {
                        // forget about this quote and move on
                        last = -1;  
                        buffer.append('"');
                        continue;
                    }
                    last = '"';
                    continue;
                }
            }
            if(ch == field_delim) {
                if(quoted) {
                    if(last == '"') {
                        break;
                    }
                } else {
                    break;
                }
            }
            buffer.append((char)ch);
        }

        return buffer.toString();
    }

    public void close() throws IOException {
        this.reader.close();
    }

}
