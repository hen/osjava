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
// CsvFieldReader.java
package com.generationjava.io;

import java.io.IOException;
import java.io.Reader;

public class CsvFieldReader extends CsvReader {

    private String[] headers;
    private String[] currentLine;

    public CsvFieldReader(Reader reader) {
        super(reader);
    }

    public void loadHeaders() throws IOException {
        if(this.headers == null) {
            useHeaders(super.readLine());
        }
    }

    public void useHeaders(String[] headers) {
        this.headers = headers;
    }

    public String readField(String name) throws IOException {
        if(this.headers == null) {
            loadHeaders();
        }

        if(this.currentLine == null) {
            nextBlock();
            if(this.currentLine == null) {
                return null;
            }
        }

        int idx = -1;
        for(int i=0;i<this.headers.length;i++) {
            if(name.equals(this.headers[i])) {
                idx = i;
            }
        }

        String field = null;

        if(idx != -1) {
            field = currentLine[idx];
        }

        return field;
    }

    public boolean nextBlock() throws IOException {
        this.currentLine = super.readLine();
        return (this.currentLine != null);
    }

}
