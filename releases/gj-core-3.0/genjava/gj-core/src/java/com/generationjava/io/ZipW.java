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

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Makes creating Zip files easier.
 */
final public class ZipW {

    private ZipOutputStream zipout = null;

    /**
     * Provide the OutputStream that this Zip will be written to.
     */
    public ZipW(OutputStream out) {
            this.zipout = new ZipOutputStream( out );
    }

    /**
     * Add the passed in String data to the Zip under the specified 
     * filename.
     */
    public boolean addFile(String filename, String contents) {
        // test if this dir has been written to yet?
        try {
            ZipEntry entry = new ZipEntry(filename);
            entry.setTime(System.currentTimeMillis());
            entry.setSize(contents.length());   // *2?
            zipout.putNextEntry(entry);

            zipout.write(contents.getBytes());

            zipout.closeEntry();
            return true;
        } catch(ZipException ze) {
            ze.printStackTrace();
            return false;
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

    }

    /**
     * Close the zip file.
     */
    public void close() {
        try {
            zipout.close();
        } catch(ZipException ze) {
            ze.printStackTrace();
            return;
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return;
        }
    }

}
