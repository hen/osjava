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
 * + Neither the name of Scabies nor the names of its contributors 
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
package org.osjava.scraping;

import java.util.Iterator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;

import com.generationjava.io.StreamW;

import org.apache.log4j.Logger;

public class FileStore implements Store {

    private static Logger logger = Logger.getLogger(FileStore.class);

// FileStore assumes that the result contains a URL, InputStream, other?
// Assumes only row(?)
// TODO: If no saveAs, then it can handle multiple rows
// TODO: Optional argument 'maintainExtension=true'. In which case the 
//       saveAs does not need to mention the extension.
    public void store(Result result, Config cfg, Session session) throws StoringException {
        OutputStream out = null;
        InputStream in = null;
try {
        String path = cfg.getString("file.path");
        String saveAs = cfg.getString("file.saveAs");

        File file = new File(path, saveAs);
        FileOutputStream fos = new FileOutputStream(file);

        Iterator iterator = result.iterateRows();
        if(iterator.hasNext()) {
            Object[] row = (Object[])iterator.next();
            if(row.length == 0) {
                logger.error("Empty row found. Skipping. ");
                return;
            }
            URL url = (URL) row[0];
            in = url.openStream();
            StreamW.pushStream(in, fos);
        }

} catch(IOException ioe) {
    throw new StoringException("File I/O Storing Error: "+ioe.getMessage(), ioe);
} finally {
        try { if(in != null) in.close(); } catch(IOException ioe) { } 
        try { if(out != null) out.close(); } catch(IOException ioe) { } 
}
    }

    public boolean exists(Header header, Config cfg, Session session) throws StoringException {
        // need to write some kind of checking in here
        return false;
    }
}
