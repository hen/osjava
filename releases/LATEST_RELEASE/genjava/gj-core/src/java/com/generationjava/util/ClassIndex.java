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
package com.generationjava.util;

import java.io.File;
import java.io.IOException;

import java.util.Collection;

import com.generationjava.collections.FQMap;

import com.generationjava.io.find.Finder;
import com.generationjava.io.find.FileFinder;
import com.generationjava.io.find.ZipFinder;
import com.generationjava.io.find.FindListener;
import com.generationjava.io.find.FindEvent;

/**
 * Indexes a class path.
 */
public class ClassIndex implements FindListener {

    private FQMap map = null;

    /**
     * Create an index of the runtime classpath.
     */
    public ClassIndex() {
        this(System.getProperty("java.class.path"));
    }
    
    /**
     * Creates an index of the given classpath.
     */
    public ClassIndex(String classpath) {
        map = new FQMap();
        int idx = -1;
        int current = 0;
        while( (idx = classpath.indexOf(File.pathSeparatorChar, idx)) != -1 ) {
            String path = classpath.substring(current, idx);

            File file = new File(path);
            Finder finder = null;
            if(file.isDirectory()) {
                // find all .class files.
                finder = new FileFinder();
            } else {
                // assume it's a zip/jar
                // open and find all .class files
                finder = new ZipFinder();
            }
            finder.addFindListener(this);
            finder.find(file, "class");

            idx++;
            current = idx;
        }

    }

    /**
     * Get the package that a given classname is in.
     */
    public String getPackage(String classname) {
        return (String)map.get(classname);
    }

    public Collection getRootClasses() {
        return getClassesIn("");
    }
    public Collection getClassesIn(String packagename) {
        FQMap tmp = map;
        if(!"".equals(packagename)) {
            tmp = (FQMap)map.get(packagename);
        }
        Collection[] colls = tmp.getSeparatedValues();
        return colls[1];
    }

    public Collection getRootPackages() {
        return getPackagesIn("");
    }
    public Collection getPackagesIn(String packagename) {
        FQMap tmp = map;
        if(!"".equals(packagename)) {
            tmp = (FQMap)map.get(packagename);
        }
        Collection[] colls = tmp.getSeparatedValues();
        return colls[0];
    }

    public void directoryStarted(FindEvent findEvent) {
        // ignore
    }

    public void directoryFinished(FindEvent findEvent) {
        // ignore
    }

    public void fileFound(FindEvent findEvent) {
        String file = findEvent.getFile();

        // ignore inner classes
        if(file.indexOf("$") != -1) {
            return;
        }

        file = file.substring(0,file.length()-6);

        if(map.get(file) != null) {
            // simulate the import method of getting the first one
            return;
        }

        String pck = findEvent.getDirectory().getPath();
        pck = pck.replace('/','.');
        while(pck.startsWith(".")) {
            pck = pck.substring(1);
        }

        map.put(file, pck);
    }

}

