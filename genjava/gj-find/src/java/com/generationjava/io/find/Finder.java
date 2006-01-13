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
// @file   Finder.java
// @author bayard@generationjava.com
// @create 2001-03-25

package com.generationjava.io.find;

import java.io.File;
import java.util.Map;

/**
 * A Finder of Files. Though the structure the files are in is unspecified.
 */
public interface Finder {

    // MODIFIER
    /** Add to other options to negate them. */
    public static final String NOT = "NOT_";

    // OPTIONS
    /** Do not include the current time in date calculations - Unimplemented*/
    public static final String DAYSTART = "DAYSTART";
    /** */
    public static final String DEPTH = "DEPTH";
    /** How far down the directory structure to start searching at - Unimplemented */
    public static final String MAXDEPTH = "MAXDEPTH";
    /** How many directories to search down - Unimplemented */
    public static final String MINDEPTH = "MINDEPTH";
    /** Don't follow hidden directories */
    public static final String IGNORE_HIDDEN_DIRS = "IGNORE_HIDDEN_DIRS";

    // Time based tests
    /** How many minutes old the file's last modified time must be at most [BUG: Bases on current time and not the time the finder started */
    public static final String MIN = "MIN";
    /** Is a file newer than the File argument to this option */
    public static final String NEWER = "NEWER";
    /** How many days old the file's last modified time must be at most */
    public static final String TIME = "TIME";

    // size based tests
    /** Whether the file is empty */
    public static final String EMPTY = "EMPTY";
    /** Whether the file equals a particular multiple of 512. TODO: Needs more work and to handle suffixes so we don't have to exist in a world of 512 blocks. */
    public static final String SIZE = "SIZE";

    // name based tests
    /** Whether the name is a case sensitive wildcard match */
    public static final String NAME = "NAME";
    /** Whether the name is a case insensitive wildcard match */
    public static final String INAME = "INAME";
    /** Whether the path is a case sensitive wildcard match */
    public static final String PATH = "PATH";
    /** Whether the path is a case insensitive wildcard match */
    public static final String IPATH = "IPATH";
    /** Whether the path is a case sensitive regex match */
    public static final String REGEX = "REGEX";
    /** Whether the path is a case sensitive regex match */
    public static final String IREGEX = "IREGEX";

    // type of file
    /** Type of file. Supports "d" and "f" */
    public static final String TYPE = "TYPE";      // supports 'd' and 'f'
    /** Hidden file */
    public static final String HIDDEN = "HIDDEN";

    // permission replacements
    /** Whether the user running the JVM can read the file */
    public static final String CAN_READ = "CAN_READ";
    /** Whether the user running the JVM can write the file */
    public static final String CAN_WRITE = "CAN_WRITE";

    /**
     * Use the Event/Notification part of the system. 
     * Useful when you want to pipeline things because 
     * there are a large number of files. 
     */
    public void addFindListener(FindListener fl);
    /**
     * Stop using the Event/Notification part of the system. 
     */
    public void removeFindListener(FindListener fl);

    /**
     * Find all files and directories under a particular root directory. 
     */
    public File[] find(File root);
    /**
     * Find all files and directories under a particular root directory and 
     * based on a particular set of filter options. 
     */
    public File[] find(File root, Map options);

}
