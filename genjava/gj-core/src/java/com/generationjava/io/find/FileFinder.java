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
package com.generationjava.io.find;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;

/**
 * Finds Files in a file system.
 *
 * Informs FindListeners whenever a Find is made, and returns the 
 * finds to the user.
 */
public class FileFinder implements Finder {

    private List findListeners;

    /**
     * Find all files in the specified directory.
     */
    public File[] find(File directory) {
        return find(directory, new java.util.HashMap());
    }

    // add maxdepth and mindepth somehow
    public File[] find(File directory, Map options) {
        notifyDirectoryStarted(directory);
        boolean depthFirst = options.containsKey(Finder.DEPTH);
        File[] list = find(directory, new FindingFilter(options), depthFirst);
        notifyDirectoryFinished(directory, list);
        return list;
    }

    private File[] find(File directory, FindingFilter filter, boolean depthFirst) {

        // we can't use listFiles(filter) here, directories don't work correctly
        File[] list = directory.listFiles();

        if (list == null) {
            return new File[0];
        }

        List retlist = new LinkedList();
        int sz = list.length;

        for (int i = 0; i < sz; i++) {
            File tmp = list[i];
            if(!depthFirst && filter.accept(tmp)) {
                retlist.add(tmp);
                notifyFileFound(directory,tmp);
            }
            if (tmp.isDirectory()) {
                notifyDirectoryStarted(tmp);
                File[] sublist = find(tmp, filter, depthFirst);
                int subsz = sublist.length;
                for (int j = 0; j < subsz; j++) {
                    retlist.add(sublist[j]);
                }
                notifyDirectoryFinished(tmp, sublist);
            }
            if(depthFirst && filter.accept(tmp)) {
                retlist.add(tmp);
                notifyFileFound(directory,tmp);
            }
        }

        return (File[]) retlist.toArray(list);
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
     * Remove a FindListener.
     */
    public void removeFindListener(FindListener fl) {
        if(findListeners != null) {
            findListeners.remove(fl);
        }
    }

    /**
     * Notify all FindListeners that a directory is being started.
     */
    public void notifyDirectoryStarted(File directory) {
        if(!directory.isDirectory()) {
            return;
        }
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,"directoryStarted",directory);
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
    public void notifyDirectoryFinished(File directory, File[] files) {
        if(!directory.isDirectory()) {
            return;
        }
        if(findListeners != null) {
            FindEvent fe = new FindEvent(this,"directoryFinished",directory,files);
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
    public void notifyFileFound(File directory, File file) {
        if(file.isDirectory()) {
            return;
        }
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
