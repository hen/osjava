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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.io.InputStream;

import java.io.Writer;
import java.io.FileWriter;

import java.net.URL;

import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
//import com.generationjava.util.Wildcard;

/**
 * Provides useful, standard behaviour for dealing with java.io.Files.
 */
final public class FileW {

    /**
     * Load the contents of a given filename. Return null if the 
     * file is not loadable.
     */
    static public String loadFile(String filename) {
        File file = new File( filename );
        return loadFile(file);
    }

    /**
     * Load the contents of a given stream. Return null if the 
     * file is not loadable.
     */
    static public String loadFile(InputStream in) {
        try {
            int ptr = 0;
            StringBuffer buffer = new StringBuffer();
            while( (ptr = in.read()) != -1 ) {
                buffer.append((char)ptr);
            }
            return buffer.toString();
        } catch(IOException ioe) {
//            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Load the contents of a given url. Return null if the 
     * file is not loadable.
     */
    static public String loadFile(URL url) {
        try {
            InputStream in = url.openStream();
            String str = loadFile(in);
            in.close();
            return str;
        } catch(IOException ioe) {
//            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Load the contents of a given java.io.File. Return null if the 
     * file is not loadable.
     */
    static public String loadFile(File file) {
        try {
            Reader rdr = new FileReader( file );
            long sz = file.length();
            char[] ch = new char[(int)sz];   // can only read in things of MAXINT length
            rdr.read(ch);
            rdr.close();
            return new String(ch);
        } catch(IOException ioe) {
//            ioe.printStackTrace();
            return null;
        }
    }

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
        return loadFile(url);
    }

    /**
     * Save a String to a file with the given filename.
     * 
     * @return boolean did the save work.
     */
    static public boolean saveFile(String filename, String contents) {
        try {
            File file = new File( filename );
            if(file.getParent() != null) {
	        new File(file.getParent()).mkdirs();
            }
            Writer wtr = new FileWriter( file );
            long sz = file.length();
            char[] ch = contents.toCharArray();
            wtr.write(ch);
            wtr.close();
            return true;
        } catch(IOException ioe) {
//            ioe.printStackTrace();
            return false;
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
        return dottify( removeExtension(path) );
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

    // change  bob.blah to bob
    // change bob/jim/fred.blah to bob/jim/fred
    // change bob.jim/fred to bob.jim/fred
    // change //// to empty string
    // change null to null
    /**
     * If a filename has an extension, then it removes this.
     *
     * @param in String pathname
     *
     * @return String pathname without extension.
     */
    static public String removeExtension(String in) {
        if(in == null) {
            return null;
        }

        // use File.SEP etc...
        int idx = in.lastIndexOf( java.io.File.separator );
        int dot = in.indexOf(".");
        if(dot != -1) {
            if( dot > idx ) {   // handles idx being -1
                in = in.substring(0,dot);
            }
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

    // deals with wildcards. That is:
    //    * any number of chars
    //    ? one char
    static public File[] findFiles(String str) {
        return findFiles(str, new File(System.getProperty("user.home")));
    }
    static public File[] findFiles(String str, File directory) {
        LinkedList files = new LinkedList();
        /*
        LinkedList dirs = new LinkedList();
        dirs.add(directory);
        String[] elements = StringUtils.split(str, File.separator);
        */
        files.add( new File(directory, str) );
        return (File[])files.toArray(new File[0]);
    }

}

/*
class WildcardFilter implements FilenameFilter {

    private Wildcard wildcard = new Wildcard();

    public boolean accept(File file, String str) {
        String name = file.getName();
        return wildcard.match(str, name);
    }

}
*/
