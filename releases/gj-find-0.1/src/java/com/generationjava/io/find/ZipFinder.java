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
// @file   ZipFinder.java
// @author bayard@generationjava.com
// @create 2000-11-18
// @modify 2001-05-27

package com.generationjava.io.find;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;  
import java.util.zip.ZipException;  
import java.io.IOException;

/**
 * A Finder which finds files inside a Zip file.
 * Will also work for jar files.
 */
public class ZipFinder implements Finder {

    private List findListeners;

    public File[] find(File zip) {
        return find(zip, new java.util.HashMap());
    }

    public File[] find(File zipfile, Map options) {
        String extension = null;
        if(options.containsKey(Finder.NAME)) {
            extension = options.get(Finder.NAME).toString();
        }
        List retlist = new ArrayList();

        try {
            ZipFile zip = new ZipFile(zipfile);
            Enumeration enum = zip.entries();
            while(enum.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)enum.nextElement();
                if(extension != null) {
                    if(entry.getName().endsWith(extension)) {
                        addFile(retlist, entry.getName());
                    }
                } else {
                    addFile(retlist, entry.getName());
                }
            }
        } catch(ZipException ze) {
            throw new FinderException(ze);
        } catch(IOException ioe) {
            throw new FinderException(ioe);
        }

        return (File[]) retlist.toArray(new File[0]);
    }

    private void addFile(List list, String file) {
        if(file.endsWith("/")) {
            // directory
            file = file.substring(0, file.length() - 1);
            int idx = file.lastIndexOf("/");
            if(idx != -1) {
                file = file.substring(idx+1);
            }
            return;
        }

        int idx = file.lastIndexOf("/");
        String directory = "";
        if(idx != -1) {
            directory = file.substring(0,idx);
            file = file.substring(idx+1);
        }
        notifyFileFound(new File(directory), new File(directory, file));
        list.add(file);
    }
    
    public void addFindListener(FindListener fl) {
        if(findListeners == null) {
            findListeners = new LinkedList();
        }
        findListeners.add(fl);
    }

    public void removeFindListener(FindListener fl) {
        if(findListeners != null) {
            findListeners.remove(fl);
        }
    }

    public void notifyDirectoryStarted(File directory) {
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,"directoryStarted",directory);
            Iterator itr = findListeners.iterator();
            while(itr.hasNext()) {
                FindListener findListener = (FindListener)itr.next();
                findListener.directoryStarted( fe );
            }
        }
    }

    public void notifyDirectoryFinished(File directory, File[] files) {
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,"directoryFinished",directory,files);
            Iterator itr = findListeners.iterator();
            while(itr.hasNext()) {
                FindListener findListener = (FindListener)itr.next();
                findListener.directoryFinished( fe );
            }
        }
    }

    public void notifyFileFound(File directory, File file) {
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,"fileFound",directory,file);
            Iterator itr = findListeners.iterator();
            while(itr.hasNext()) {
                FindListener findListener = (FindListener)itr.next();
                findListener.fileFound( fe );
            }
        }
    }
    
}
