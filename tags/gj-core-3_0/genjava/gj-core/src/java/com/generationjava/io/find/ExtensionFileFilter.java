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
// @file   ExtensionFileFilter.java
// @author bayard@generationjava.com
// @create 2000-11-18
// @modify NOW

package com.generationjava.io.find;

import java.util.HashSet;
import java.util.Collection;
import java.io.File;
import java.io.FilenameFilter;

/**
 * A file-filter which may be preconfigured to accept a 
 * set of extensions.
 */
public class ExtensionFileFilter implements FilenameFilter {

    private HashSet myExtensions = new HashSet();

    public ExtensionFileFilter() {
    }

    public ExtensionFileFilter(String ext) {
        myExtensions.add(ext);
    }

    public ExtensionFileFilter(String[] exts) {
        for(int i=0; i<exts.length; i++) {
            myExtensions.add(exts[i]);
        }
    }

    /**
     * Accept any of the given collection of Strings.
     */
    public ExtensionFileFilter(Collection col) {
        myExtensions.addAll(col);
    }

    public boolean accept(File dir, String name) {
        File f = new File(dir,name);
        String filename = f.getName().toLowerCase();

        if(f.isDirectory()) { 
            return true; 
        }

        int index = filename.lastIndexOf(".");

        if(index == -1) { 
            return false; 
        } else 
        if(index == filename.length()-1) { 
            return false; 
        } else 
        if(myExtensions.contains(filename.substring(index+1))) { 
            return true; 
        } else {
            return false;
        }
    }

    public void addExtension(String str) {
        myExtensions.add(str);
    }

}
