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
//import java.util.Date;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;  
import java.util.zip.ZipException;  
import java.io.IOException;

import org.apache.commons.collections.IteratorUtils;

/**
 * A Finder which finds files inside a Zip file.
 * Will also work for jar files.
 */
public class ZipFinder implements Finder {

    private List findListeners;
    private boolean includeDirs = false;

    public void setIncludeDirectories(boolean bool) {
        this.includeDirs = bool;
    }
    public boolean getIncludeDirectories() {
        return this.includeDirs;
    }

    public String[] find(File zip) {
        return find(zip, null);
    }

    /// TODO: Add int maxcount, int mincount, Date lastmodified
    /**
     * Find all files in this zipfile which have the specified extension.
     * If extension is null, then all files are returned.
     * The specified extension should not contain the '.'
     */
    public String[] find(File zipfile, String extension) {
//    public String[] find(File zipfile, String extension, int maxcount, int mincount, Date lastmodified) {
        ExtensionFileFilter filter = null;

        if (extension != null) {
            // use a FileFilter
            filter = new ExtensionFileFilter();
            filter.addExtension(extension);
        }

        List retlist = new ArrayList();

        try {
            ZipFile zip = new ZipFile(zipfile);
            Iterator iterator = IteratorUtils.asIterator(zip.entries());
            while(iterator.hasNext()) {
                ZipEntry entry = (ZipEntry)iterator.next();
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

        return (String[]) retlist.toArray(new String[0]);
    }

    private void addFile(List list, String file) {
        if(file.endsWith("/")) {
            // directory
            file = file.substring(0, file.length() - 1);
            int idx = file.lastIndexOf("/");
            if(idx != -1) {
                file = file.substring(idx+1);
            }
            if(includeDirs) {
                list.add(file);
            }
            return;
        }

        int idx = file.lastIndexOf("/");
        String directory = "";
        if(idx != -1) {
            directory = file.substring(0,idx);
            file = file.substring(idx+1);
        }
        notifyFileFound(new File(directory), file);
        list.add(file);
    }
    
    public void addFindListener(FindListener fl) {
        if(findListeners == null) {
            findListeners = new LinkedList();
        }
        findListeners.add(fl);
    }

    public void notifyDirectoryStarted(File directory) {
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,directory);
            Iterator itr = findListeners.iterator();
            while(itr.hasNext()) {
                FindListener findListener = (FindListener)itr.next();
                findListener.directoryStarted( fe );
            }
        }
    }

    public void notifyDirectoryFinished(File directory, String[] files) {
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,directory,files);
            Iterator itr = findListeners.iterator();
            while(itr.hasNext()) {
                FindListener findListener = (FindListener)itr.next();
                findListener.directoryFinished( fe );
            }
        }
    }

    public void notifyFileFound(File directory, String file) {
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,directory,file);
            Iterator itr = findListeners.iterator();
            while(itr.hasNext()) {
                FindListener findListener = (FindListener)itr.next();
                findListener.fileFound( fe );
            }
        }
    }
    
}
