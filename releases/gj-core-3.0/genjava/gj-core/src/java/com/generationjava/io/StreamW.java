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
// StreamW.java
package com.generationjava.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamW {

    static public void pushStream(InputStream in, OutputStream out) 
        throws IOException 
    {
        byte[] buffer = new byte[1023];
        while(true) {
            int size = in.read(buffer);
            if(size == -1) {
                break;
            }
            out.write(buffer,0,size);
        }
    }


    static public InputStream loadFromClasspath(String file) {
        return loadFromClasspath(file, ClassLoader.getSystemClassLoader());
    }
    static public InputStream loadFromClasspath(String file, Object instance) {
        return loadFromClasspath(file, instance.getClass().getClassLoader());
    }
    static public InputStream loadFromClasspath(String file, ClassLoader loader) {
        if(loader == null) {
            return null;
        }

        InputStream in = loader.getResourceAsStream(file);
        if(in == null) {
            in = loader.getResourceAsStream("/"+file);
        }
        return in;
    }

    static public void closeQuietly(InputStream in) {
        try { if(in != null) in.close(); } catch(IOException ioe) { ; }
    }

    static public void closeQuietly(OutputStream out) {
        try { if(out != null) out.close(); } catch(IOException ioe) { ; }
    }

}
