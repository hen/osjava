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

import java.io.File;

import java.util.LinkedList;
import java.util.Iterator;

public class FilePoller implements Runnable {

    static public void main(String[] strs) {
        pollFile(strs[0]);
    }

    static public void pollFile(String filename) {
        File file = new File(filename);
        FilePoller fp = new FilePoller();
        fp.addListener( new PollListener() {
            public void fileChanged(FileEvent fe) {
                System.err.println("File changed. ");
            }
        } );
        fp.setFile(file);
        fp.setDelay(1L);
        Thread t = new Thread(fp);
        t.start();
    }

    private File file;
    private long delay;
    private StringBuffer buffer;

    private LinkedList listeners;

    public void setFile(File file) {
        this.file = file;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void addListener(PollListener listener) {
        if(listeners == null) {
            listeners = new LinkedList();
        }
        listeners.add(listener);
    }

    private void notifyListeners() {
        if(listeners == null) {
            return;
        }
        FileEvent fe = new FileEvent(this);
        Iterator iterator = listeners.iterator();
        while(iterator.hasNext()) {
            PollListener pl = (PollListener)iterator.next();
            pl.fileChanged(fe);
        }
    }

    public void run() {
        long length = file.length();
        long lastMod = file.lastModified();
//        try {
            while(true) {
                if( (length != file.length()) ||
                    (lastMod != file.lastModified()) 
                  ) 
                {
                    notifyListeners();
                }
                length = file.length();
                lastMod = file.lastModified();
                try {
                    Thread.sleep(this.delay);
                } catch(InterruptedException ie) {
                }
            }
//        } catch(IOException ioe) {
//            ioe.printStackTrace();
//        }
    }

    private void append(char ch) {
        if(buffer == null) {
            buffer = new StringBuffer();
        }
        buffer.append(ch);
    }

    public String getData() {
        return buffer.toString();
    }

    public void clearData() {
        buffer = null;
    }

}
