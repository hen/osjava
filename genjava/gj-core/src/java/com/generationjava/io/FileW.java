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
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.io.InputStream;

import java.io.Writer;
import java.io.FileWriter;

import java.net.URL;

import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Provides useful, standard behaviour for dealing with java.io.Files.
 */
final public class FileW {

    static public String loadFromClasspath(String file) {
        return loadFromClasspath(file, ClassLoader.getSystemClassLoader());
    }
    static public String loadFromClasspath(String file, Object instance) {
        return loadFromClasspath(file, instance.getClass().getClassLoader());
    }
    static public String loadFromClasspath(String file, ClassLoader loader) {
        if(loader == null) {
            return null;
        }

        URL url = loader.getResource(file);
        if(url == null) {
            url = loader.getResource("/"+file);
        }
        if(url == null) {
            return null;
        }
        try {
            return FileUtils.readFileToString(FileUtils.toFile(url));
        } catch(IOException ioe) {
            throw new RuntimeException("Unexpected IOException", ioe);
        }
    }

    /**
     * What is the full classname of this file.
     *
     * @param file File to find classname of
     *
     * @return String class name
     */
    static public String getClassName(File file) {
        String path = file.getPath();
        path = removePwd( path );
        return dottify( FilenameUtils.removeExtension(path) );
    }

    /**
     * Removes the present working directory of a filename
     *
     * @param in String pathname
     *
     * @return String relative pathname
     */
    static public String removePwd(String in) {
        if(in == null) {
            return null;
        }

        String pwd = System.getProperty("user.dir");

        if(pwd.length() == in.length()) {
            return in;
        }

        int idx = in.indexOf(pwd);
        if(idx == 0) {
            in = in.substring(pwd.length()+1);
        }

        return in;
    }

    // change bob/jim/fred to bob.jim.fred
    // change /bob/jim/fred to bob.jim.fred
    // change bob/jim/fred/ to bob.jim.fred
    // change //// to empty string
    // change null to null
    /**
     * Changes File.separator to dots in a pathname.
     *
     * @param in String pathname
     *
     * @return String pathname with dots instead of file separators.
     */
    static public String dottify(String in) {
        if(in == null) {
            return null;
        }

        int idx = in.indexOf( java.io.File.separator );
        while(idx != -1) {
            if(in.length() == 1) {
                return "";  // or null?
            } else
            if(idx == 0) {
                in = in.substring(1);
            } else
            if(idx == in.length()) {
                in = in.substring(0,in.length()-1);
            } else {
                in = in.substring(0,idx)+"."+in.substring(idx+1);
            }
            idx = in.indexOf("/");
        }

        return in;
    }

}
