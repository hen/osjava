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
// @file   FileFinder.java
// @author bayard@generationjava.com
// @create 2000-11-18
// @modify 2000-11-18
// @modify 2001-01-27 FindListener 

package com.generationjava.io.find;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
//import java.util.Date;

/**
 * Finds Files in a file system.
 *
 * Informs FindListeners whenever a Find is made, and returns the 
 * finds to the user.
 */
public class FileFinder implements Finder {

    private List findListeners;
    private boolean includeDirs = false;

    /**
     * Find directories, as well as files.
     */
    public void setIncludeDirectories(boolean bool) {
        this.includeDirs = bool;
    }
    /**
     * Will it find directories, as well as files.
     */
    public boolean getIncludeDirectories() {
        return this.includeDirs;
    }

    /**
     * Find all files in the specified directory.
     */
    public String[] find(File directory) {
        return find(directory, null);
    }

    /// TODO: Add int maxcount, int mincount, Date lastmodified
    /**
     * Find all files in the specified directory with the specified extension.
     */
    public String[] find(File directory, String extension) {
//    public String[] find(File directory, String extension, int maxcount, int mincount, Date lastmodified) {
        ExtensionFileFilter filter = null;

        if (extension != null) {
            // use a FileFilter
            filter = new ExtensionFileFilter();
            filter.addExtension(extension);
        }

        String[] list = null;
        if (filter == null) {
            list = directory.list();
        } else {
            list = directory.list(filter);
        }
        List retlist = new LinkedList();
        List filelist = null;
        if(!includeDirs) {
            filelist = new LinkedList();
        }
        int sz = list.length;

        for (int i = 0; i < sz; i++) {
            File tmp = new File(directory, list[i]);
            if (tmp.isDirectory()) {
                notifyDirectoryStarted(tmp);
                String[] sublist = find(tmp, extension);
                int subsz = sublist.length;
                for (int j = 0; j < subsz; j++) {
                    retlist.add(list[i] + File.separator + sublist[j]);
                }
                if(includeDirs) {
                    retlist.add(tmp.getName());
                }
            } else {
                retlist.add(list[i]);
                notifyFileFound(directory,list[i]);
                if(!includeDirs) {
                    filelist.add(list[i]);
                }
            }
        }
        if(includeDirs) {
//            retlist.add(directory.getName());
            notifyDirectoryFinished(directory,list);
        } else {
            notifyDirectoryFinished(directory,
                             (String[]) filelist.toArray(new String[0]) );
        }
        return (String[]) retlist.toArray(new String[0]);
    }
    
    /**
     * Add a FindListener.
     */
    public void addFindListener(FindListener fl) {
        if(findListeners == null) {
            findListeners = new LinkedList();
        }
        findListeners.add(fl);
    }

    /**
     * Notify all FindListeners that a directory is being started.
     */
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

    /**
     * Notify all FindListeners that a directory has been finished.
     * Supplying the filenames that have been found.
     */
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

    /**
     * Notify FindListeners that a file has been found.
     */
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
