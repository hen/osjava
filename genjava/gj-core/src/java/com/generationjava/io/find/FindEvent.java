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
// @file   FindEvent.java
// @author bayard@generationjava.com
// @create 2001-01-27 

package com.generationjava.io.find;

import java.io.File;

/**
 * Event upon which notification is made to a FindListener.
 * It contains references to the Finder and the Directory in which 
 * the event occured.
 * Depending on the particular occasion, it may also contain 
 * a set of files or a file.
 */
public class FindEvent {

    private File directory;
    private Finder finder;
    private String file;
    private String[] files;

    public FindEvent(Finder finder, File directory) {
        this.finder = finder;
        this.directory = directory;
    }
    
    public FindEvent(Finder finder, File directory, String file) {
        this.finder = finder;
        this.directory = directory;
        this.file = file;
    }
    
    public FindEvent(Finder finder, File directory, String[] files) {
        this.finder = finder;
        this.directory = directory;
        this.files = files;
    }

    public File getDirectory() {
        return this.directory;
    }
    
    public Finder getFinder() {
        return this.finder;
    }
    
    /**
     * File found.
     */
    public String getFile() {
        return this.file;
    }
    
    /**
     * Files found in a directory.
     */
    public String[] getFiles() {
        return this.files;
    }

}
